package com.ecommerce.webapp.api.controller.auth;

import com.ecommerce.webapp.api.model.*;
import com.ecommerce.webapp.exception.*;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.service.EmailService;
import com.ecommerce.webapp.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {

  private UserService userService;

  private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

  public AuthenticationController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationBody registrationBody) {
    try {
      userService.registerUser(registrationBody);
      String message ="User registration is under process. Please check and verify your email and mobile to register yourself.";
      logger.info(message);
      return ResponseEntity.status(HttpStatus.OK)
              .body(message);
    } catch (UserAlreadyExistsException ex) {

      String message ="User registration failed due to existing user: "+ registrationBody.getUsername();
      logger.info(message);
      return ResponseEntity.status(HttpStatus.CONFLICT)
              .body(message);

    } catch (EmailFailureException e) {
      EmailFailureException.handleException("Error sending registration email", registrationBody.getUsername(), e);
    } catch (Exception e) {
      new UnexpectedException("Unexpected error during user registration", registrationBody.getUsername(), e);
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something Went Wrong!!");
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
    try {
      String jwt = userService.loginUser(loginBody);
      if (jwt == null) {
        logger.warn("User login failed: {}", loginBody.getUsername());
        LoginResponse response = new LoginResponse();
        response.setJwt(null);
        response.setSuccess(false);
        response.setFailureReason("User login failed due to bad credentials / unverified email or mobile");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      } else {
        LoginResponse response = new LoginResponse();
        response.setJwt(jwt);
        response.setSuccess(true);
        response.setFailureReason("User login successful for user : "+ loginBody.getUsername());
        return ResponseEntity.ok(response);
      }
    } catch (UserNotVerifiedException ex) {
      UserNotVerifiedException.handleUserNotVerifiedException("User login failed due to unverified account", loginBody.getUsername(), ex);
    } catch (EmailFailureException ex) {
      EmailFailureException.handleException("Error sending login email", loginBody.getUsername(), ex);
    } catch (Exception e) {
      UnexpectedException.handleUnexpectedException("Unexpected error during user login", loginBody.getUsername(), e);
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  @PostMapping("/verify")
  public ResponseEntity<?> verifyEmail(@RequestParam String token) {
    try {
      if (userService.verifyUser(token)) {
        return ResponseEntity.ok().body("Verified email successfully!");
      } else {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Conflict error during email verification");
      }
    } catch (Exception e) {
      UnexpectedException.handleUnexpectedException("Error during email verification", "", e);
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Something went wrong!!");
  }

  @GetMapping("/me")
  public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user) {
    return user;
  }

  @PostMapping("/forgot")
  public ResponseEntity<?> forgotPassword(@RequestParam String email) {
    try {
      userService.forgotPassword(email);
      return ResponseEntity.ok().body("Password reset email has been successfully sent to the user.");
    } catch (EmailNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad credentials. Please check the data provided!");
    } catch (EmailFailureException | MessagingException | UnsupportedEncodingException e) {
      UnexpectedException.handleUnexpectedException("Error during password reset", email, e);
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something Went Wrong!");
  }

  @PostMapping("/reset")
  public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetBody body) {
    try {
      userService.resetPassword(body);
      return ResponseEntity.ok().body("Password has been successfully reset for the user.");
    } catch (Exception e) {
      UnexpectedException.handleUnexpectedException("Error during password reset", null , e);
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something Went Wrong!");
  }

  @PostMapping("/sendOTP")
  public ResponseEntity<?> sendOTP(@Valid @RequestBody MobileOTPRequestDto dto) {
    try {
      logger.info("sendOTP Request: {}", dto.toString());
      return ResponseEntity.ok().body(userService.sendOTPToContactNumber(dto));
    } catch (Exception e) {
      UnexpectedException.handleUnexpectedException("Error during OTP generation", dto.getUserName(), e);
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something Went Wrong!");
  }

  @PostMapping("/validateOTP")
  public ResponseEntity<?> validateOTP(@Valid @RequestBody MobileOTPRequestDto dto) {
    try {
      logger.info("Validating OTP for user: {}", dto.getUserName());
      return ResponseEntity.ok().body(userService.validateOTP(dto.getOneTimePassword(), dto.getUserName()));
    } catch (Exception e) {
      UnexpectedException.handleUnexpectedException("Error during OTP validation", dto.getUserName(), e);
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something Went Wrong!");
  }

}

