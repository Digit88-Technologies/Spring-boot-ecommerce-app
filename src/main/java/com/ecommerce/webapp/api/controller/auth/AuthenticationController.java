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
      logger.info("User registration is under process: {}", registrationBody.getUsername());
      return ResponseEntity.ok().build();
    } catch (UserAlreadyExistsException ex) {
      logger.warn("User registration failed due to existing user: {}", registrationBody.getUsername());
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    } catch (EmailFailureException e) {
      EmailFailureException.handleException("Error sending registration email", registrationBody.getUsername(), e);
    } catch (Exception e) {
      new UnexpectedException("Unexpected error during user registration", registrationBody.getUsername(), e);
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
    try {
      String jwt = userService.loginUser(loginBody);
      if (jwt == null) {
        logger.warn("User login failed: {}", loginBody.getUsername());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      } else {
        logger.info("User login successful: {}", loginBody.getUsername());
        LoginResponse response = new LoginResponse();
        response.setJwt(jwt);
        response.setSuccess(true);
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
        return ResponseEntity.ok().build();
      } else {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
      }
    } catch (Exception e) {
      UnexpectedException.handleUnexpectedException("Error during email verification", "", e);
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  @GetMapping("/me")
  public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user) {
    return user;
  }

  @PostMapping("/forgot")
  public ResponseEntity<?> forgotPassword(@RequestParam String email) {
    try {
      userService.forgotPassword(email);
      return ResponseEntity.ok().build();
    } catch (EmailNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } catch (EmailFailureException | MessagingException | UnsupportedEncodingException e) {
      UnexpectedException.handleUnexpectedException("Error during password reset", email, e);
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  @PostMapping("/reset")
  public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetBody body) {
    try {
      userService.resetPassword(body);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      UnexpectedException.handleUnexpectedException("Error during password reset", null , e);
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  @PostMapping("/sendOTP")
  public ResponseEntity<?> sendOTP(@Valid @RequestBody MobileRegistrationRequestDto dto) {
    try {
      logger.info("sendOTP Request: {}", dto.toString());
      return ResponseEntity.ok().body(userService.sendOTPForPasswordReset(dto));
    } catch (Exception e) {
      UnexpectedException.handleUnexpectedException("Error during OTP generation", dto.getUserName(), e);
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  @PostMapping("/validateOTP")
  public ResponseEntity<?> validateOTP(@Valid @RequestBody MobileRegistrationRequestDto dto) {
    try {
      logger.info("Validating OTP for user: {}", dto.getUserName());
      return ResponseEntity.ok().body(userService.validateOTP(dto.getOneTimePassword(), dto.getUserName()));
    } catch (Exception e) {
      UnexpectedException.handleUnexpectedException("Error during OTP validation", dto.getUserName(), e);
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

}

