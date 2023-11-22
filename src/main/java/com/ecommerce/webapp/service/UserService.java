package com.ecommerce.webapp.service;

import com.ecommerce.webapp.api.model.*;
import com.ecommerce.webapp.api.security.TwilioConfig;
import com.ecommerce.webapp.exception.*;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.model.VerificationToken;
import com.ecommerce.webapp.model.dao.LocalUserDAO;
import com.ecommerce.webapp.model.dao.VerificationTokenDAO;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Service for handling user actions.
 */
@Service
public class UserService {

  private LocalUserDAO localUserDAO;
  private VerificationTokenDAO verificationTokenDAO;
  private EncryptionService encryptionService;
  private JWTService jwtService;
  private EmailService emailService;

  @Autowired
  private TwilioConfig twilioConfig;

  Map<String, String> otpMap = new HashMap<>();


  public UserService(LocalUserDAO localUserDAO, VerificationTokenDAO verificationTokenDAO, EncryptionService encryptionService,
                     JWTService jwtService, EmailService emailService) {
    this.localUserDAO = localUserDAO;
    this.verificationTokenDAO = verificationTokenDAO;
    this.encryptionService = encryptionService;
    this.jwtService = jwtService;
    this.emailService = emailService;
  }

  /**
   * Attempts to register a user given the information provided.
   * @param registrationBody The registration information.
   * @return The local user that has been written to the database.
   * @throws UserAlreadyExistsException Thrown if there is already a user with the given information.
   */
  public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException, MessagingException, UnsupportedEncodingException {
    if (localUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
        || localUserDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
      throw new UserAlreadyExistsException();
    }
    LocalUser user = new LocalUser();
    user.setEmail(registrationBody.getEmail());
    user.setUsername(registrationBody.getUsername());
    user.setFirstName(registrationBody.getFirstName());
    user.setLastName(registrationBody.getLastName());
    user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
    VerificationToken verificationToken = createVerificationToken(user);

    //Sending the registration email to the local user for verification
    emailService.sendVerificationEmail(verificationToken);


    return localUserDAO.save(user);
  }

  /**
   * Creates a VerificationToken object for sending to the user.
   * @param user The user the token is being generated for.
   * @return The object created.
   */
  private VerificationToken createVerificationToken(LocalUser user) {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(jwtService.generateVerificationJWT(user));
    verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
    verificationToken.setUser(user);
    user.getVerificationTokens().add(verificationToken);
    return verificationToken;
  }

  /**
   * Logins in a user and provides an authentication token back.
   * @param loginBody The login request.
   * @return The authentication token. Null if the request was invalid.
   */
  public String loginUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException, MessagingException, UnsupportedEncodingException {
    Optional<LocalUser> opUser = localUserDAO.findByUsernameIgnoreCase(loginBody.getUsername());
    if (opUser.isPresent()) {
      System.out.println("Login user :  " + opUser.get());
      LocalUser user = opUser.get();
      if ((loginBody.getPassword() != null && encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())) ||
              (loginBody.getMobileOtp() != null && validateLoginOTP(loginBody.getMobileOtp(), user.getUsername())))      {
        if (user.isEmailVerified() && user.isPhoneNumberVerified()) {
          return jwtService.generateJWT(user);
        } else if (!user.isEmailVerified()){
          List<VerificationToken> verificationTokens = user.getVerificationTokens();
          boolean resend = verificationTokens.size() == 0 ||
              verificationTokens.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000)));
          if (resend) {
            VerificationToken verificationToken = createVerificationToken(user);
            verificationTokenDAO.save(verificationToken);
            emailService.sendVerificationEmail(verificationToken);
          }
          throw new UserNotVerifiedException(resend);
        } else {

          //Re-sending OTP for pending verification
          MobileOTPRequestDto mobileRegistrationRequestDto = new MobileOTPRequestDto();
          mobileRegistrationRequestDto.setUserName(user.getUsername());
          mobileRegistrationRequestDto.setPhoneNumber(user.getPhoneNumber());
          sendOTPToContactNumber(mobileRegistrationRequestDto);

        }
      }
    }
    return null;
  }

  /**
   * Verifies a user from the given token.
   * @param token The token to use to verify a user.
   * @return True if it was verified, false if already verified or token invalid.
   */
  @Transactional
  public boolean verifyUser(String token) throws MessagingException, EmailFailureException, UnsupportedEncodingException {
    Optional<VerificationToken> opToken = verificationTokenDAO.findByToken(token);
    if (opToken.isPresent()) {
      VerificationToken verificationToken = opToken.get();
      LocalUser user = verificationToken.getUser();
      if (!user.isEmailVerified()) {
        user.setEmailVerified(true);
        localUserDAO.save(user);
        verificationTokenDAO.deleteByUser(user);

        //Sending the welcome email to the local user post registration
        emailService.sendWelcomeEmail(user);
        return true;
      }
    }
    return false;
  }

  /**
   * Sends the user a forgot password reset based on the email provided.
   * @param email The email to send to.
   * @throws EmailNotFoundException Thrown if there is no user with that email.
   * @throws EmailFailureException
   */
  public void forgotPassword(String email) throws EmailNotFoundException, EmailFailureException, MessagingException, UnsupportedEncodingException {
    Optional<LocalUser> opUser = localUserDAO.findByEmailIgnoreCase(email);
    if (opUser.isPresent()) {
      LocalUser user = opUser.get();
      String token = jwtService.generatePasswordResetJWT(user);
      emailService.sendPasswordResetEmail(user, token);
    } else {
      throw new EmailNotFoundException();
    }
  }

  /**
   * Resets the users password using a given token and email.
   * @param body The password reset information.
   */
  public void resetPassword(PasswordResetBody body) {
    String email = jwtService.getResetPasswordEmail(body.getToken());
    Optional<LocalUser> opUser = localUserDAO.findByEmailIgnoreCase(email);
    if (opUser.isPresent()) {
      LocalUser user = opUser.get();
      user.setPassword(encryptionService.encryptPassword(body.getPassword()));
      localUserDAO.save(user);
    }
  }

  /**
   * Method to check if an authenticated user has permission to a user ID.
   * @param user The authenticated user.
   * @param id The user ID.
   * @return True if they have permission, false otherwise.
   */
  public boolean userHasPermissionToUser(LocalUser user, Long id) {
    return user.getId() == id;
  }

  /**
   * Method to check if an authenticated user has permission to a user Name.
   * @param user The authenticated user.
   * @param name The user Name.
   * @return True if they have permission, false otherwise.
   */
  public boolean userHasPermissionToUserByUserName(LocalUser user, String name) {
    return Objects.equals(user.getUsername(), name);
  }

  public MobileOTPResponseDto sendOTPToContactNumber(MobileOTPRequestDto mobileRegistrationRequestDto) {

    MobileOTPResponseDto mobileRegistrationResponseDto = null;
    try {
      PhoneNumber to = new PhoneNumber(mobileRegistrationRequestDto.getPhoneNumber());
      PhoneNumber from = new PhoneNumber(twilioConfig.getTrialNumber());
      String otp = generateOTP();
      String otpMessage = "Dear Customer , Your OTP is ##" + otp + "##. Use this Passcode to complete your transaction. Thank You.";
      Message message = Message
              .creator(to, from,
                      otpMessage)
              .create();
      otpMap.put(mobileRegistrationRequestDto.getUserName(), otp);
      mobileRegistrationResponseDto = new MobileOTPResponseDto(OtpStatus.DELIVERED, otpMessage);
    } catch (Exception ex) {
      mobileRegistrationResponseDto = new MobileOTPResponseDto(OtpStatus.FAILED, ex.getMessage());
    }
    return mobileRegistrationResponseDto;
  }

  /**
   * Validates the provided OTP against the stored OTP for the given user.
   *
   * @param userInputOtp The OTP entered by the user.
   * @param userName     The username for which OTP validation is performed.
   * @return A string indicating whether the OTP is valid or not.
   */
  public String validateOTP(String userInputOtp, String userName) {
    if (userInputOtp.equals(otpMap.get(userName))) {
      otpMap.remove(userName,userInputOtp);
      Optional<LocalUser> opUser = localUserDAO.findByUsernameIgnoreCase(userName);
      if (opUser.isPresent()) {
        LocalUser user = opUser.get();
        if(!user.isPhoneNumberVerified()) {
          user.setPhoneNumberVerified(true);
          localUserDAO.save(user);
        }
      }
      return "Valid OTP please proceed!";

    } else {
      return "Invalid User / OTP entered. Please check and retry !";
    }
  }

  /**
   * Validates the provided OTP for user login.
   *
   * @param userInputOtp The OTP entered by the user.
   * @param userName     The username for which OTP validation is performed.
   * @return True if the OTP is valid, false otherwise.
   */
  public Boolean validateLoginOTP(String userInputOtp, String userName) {
    if (userInputOtp.equals(otpMap.get(userName))) {
      otpMap.remove(userName,userInputOtp);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Generates a six-digit OTP for authentication purposes.
   *
   * @return The generated OTP.
   */
  private String generateOTP() {
    return new DecimalFormat("000000")
            .format(new Random().nextInt(999999));
  }

}
