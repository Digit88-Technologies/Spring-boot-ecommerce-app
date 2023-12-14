package com.ecommerce.webapp.service;

import com.ecommerce.webapp.dto.*;
import com.ecommerce.webapp.exception.*;
import com.ecommerce.webapp.model.LocalUser;

import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;

/**
 * Service interface for handling user actions.
 */
public interface UserService {

    LocalUser registerUser(RegistrationBody registrationBody)
            throws UserAlreadyExistsException, EmailFailureException, MessagingException, UnsupportedEncodingException;

    String loginUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException, MessagingException, UnsupportedEncodingException;

    boolean verifyUser(String token) throws MessagingException, EmailFailureException, UnsupportedEncodingException;

    void forgotPassword(String email) throws EmailNotFoundException, EmailFailureException, MessagingException, UnsupportedEncodingException;

    void resetPassword(PasswordResetBody body);

    boolean userHasPermissionToUser(LocalUser user, Long id);

    boolean userHasPermissionToUserByUserName(LocalUser user, String name);

    MobileOTPResponseDto sendOTPToContactNumber(MobileOTPRequestDto mobileRegistrationRequestDto);

    String validateOTP(String userInputOtp, String userName);

    Boolean validateLoginOTP(String userInputOtp, String userName);
}
