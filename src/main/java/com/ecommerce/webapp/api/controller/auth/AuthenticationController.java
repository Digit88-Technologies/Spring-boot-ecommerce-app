package com.ecommerce.webapp.api.controller.auth;

import com.ecommerce.webapp.api.model.LoginBody;
import com.ecommerce.webapp.api.model.LoginResponse;
import com.ecommerce.webapp.api.model.PasswordResetBody;
import com.ecommerce.webapp.api.model.RegistrationBody;
import com.ecommerce.webapp.exception.EmailFailureException;
import com.ecommerce.webapp.exception.EmailNotFoundException;
import com.ecommerce.webapp.exception.UserAlreadyExistsException;
import com.ecommerce.webapp.exception.UserNotVerifiedException;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {

  private UserService userService;

  public AuthenticationController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Post Mapping to handle registering users.
   * @param registrationBody The registration information.
   * @return Response to front end.
   */
  @PostMapping("/register")
  public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody) {
    try {
      userService.registerUser(registrationBody);
      return ResponseEntity.ok().build();
    } catch (UserAlreadyExistsException ex) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    } catch (EmailFailureException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Post Mapping to handle user logins to provide authentication token.
   * @param loginBody The login information.
   * @return The authentication token if successful.
   */
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
    String jwt = null;
    try {
      jwt = userService.loginUser(loginBody);
    } catch (UserNotVerifiedException ex) {
      LoginResponse response = new LoginResponse();
      response.setSuccess(false);
      String reason = "USER_NOT_VERIFIED";
      if (ex.isNewEmailSent()) {
        reason += "_EMAIL_RESENT";
      }
      response.setFailureReason(reason);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    } catch (EmailFailureException ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    if (jwt == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } else {
      LoginResponse response = new LoginResponse();
      response.setJwt(jwt);
      response.setSuccess(true);
      return ResponseEntity.ok(response);
    }
  }

  /**
   * Post mapping to verify the email of an account using the emailed token.
   * @param token The token emailed for verification. This is not the same as a
   *              authentication JWT.
   * @return 200 if successful. 409 if failure.
   */
  @PostMapping("/verify")
  public ResponseEntity verifyEmail(@RequestParam String token) {
    if (userService.verifyUser(token)) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
  }

  /**
   * Gets the profile of the currently logged-in user and returns it.
   * @param user The authentication principal object.
   * @return The user profile.
   */
  @GetMapping("/me")
  public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user) {
    return user;
  }

  /**
   * Sends an email to the user with a link to reset their password.
   * @param email The email to reset.
   * @return Ok if sent, bad request if email not found.
   */
  @PostMapping("/forgot")
  public ResponseEntity forgotPassword(@RequestParam String email) {
    try {
      userService.forgotPassword(email);
      return ResponseEntity.ok().build();
    } catch (EmailNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } catch (EmailFailureException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Resets the users password with the given token and password.
   * @param body The information for the password reset.
   * @return Okay if password was set.
   */
  @PostMapping("/reset")
  public ResponseEntity resetPassword(@Valid @RequestBody PasswordResetBody body) {
    userService.resetPassword(body);
    return ResponseEntity.ok().build();
  }

}
