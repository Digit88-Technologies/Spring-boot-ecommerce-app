package com.ecommerce.webapp.api.controller.auth;

import com.ecommerce.webapp.api.model.*;
import com.ecommerce.webapp.exception.*;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.service.EmailService;
import com.ecommerce.webapp.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    public static final String REGISTRATION_UNDER_PROCESS = "User registration is under process. Please check and verify your email. Also register and verify your mobile to complete user profile.";
    public static final String USER_LOGIN_FAILED = "User login failed due to bad credentials / unverified email or mobile";
    public static final String CONFLICT_ERROR_DURING_EMAIL_VERIFICATION = "Conflict error during email verification";
    public static final String VERIFIED_EMAIL_SUCCESSFULLY = "Verified email successfully!";
    public static final String PASSWORD_RESET_EMAIL_SENT = "Password reset email has been successfully sent to the user.";
    public static final String PASSWORD_RESET_SUCCESSFULLY = "Password has been successfully reset for the user.";
    private UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationBody registrationBody) throws MessagingException, EmailFailureException, UnsupportedEncodingException {
        userService.registerUser(registrationBody);
        return ResponseEntity.status(HttpStatus.OK)
                .body(REGISTRATION_UNDER_PROCESS);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) throws UserNotVerifiedException, MessagingException, UnsupportedEncodingException {
        String jwt = userService.loginUser(loginBody);
        if (jwt == null) {
            log.warn("User login failed: {}", loginBody.getUsername());
            LoginResponse response = new LoginResponse();
            response.setJwt(null);
            response.setSuccess(false);
            response.setFailureReason(USER_LOGIN_FAILED);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            response.setSuccess(true);
            response.setFailureReason("User login successful for user : " + loginBody.getUsername());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) throws MessagingException, UnsupportedEncodingException {
        if (userService.verifyUser(token)) {
            return ResponseEntity.ok().body(VERIFIED_EMAIL_SUCCESSFULLY);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(CONFLICT_ERROR_DURING_EMAIL_VERIFICATION);
        }
    }


    @GetMapping("/me")
    public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user) {
        return user;
    }

    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) throws EmailNotFoundException, MessagingException, UnsupportedEncodingException {

        userService.forgotPassword(email);
        return ResponseEntity.ok().body(PASSWORD_RESET_EMAIL_SENT);
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetBody body) {

        userService.resetPassword(body);
        return ResponseEntity.ok().body(PASSWORD_RESET_SUCCESSFULLY);
    }

    @PostMapping("/sendOTP")
    public ResponseEntity<?> sendOTP(@Valid @RequestBody MobileOTPRequestDto dto) {

        log.info("sendOTP Request: {}", dto.toString());
        return ResponseEntity.ok().body(userService.sendOTPToContactNumber(dto));
    }

    @PostMapping("/validateOTP")
    public ResponseEntity<?> validateOTP(@Valid @RequestBody MobileOTPRequestDto dto) {

        log.info("Validating OTP for user: {}", dto.getUserName());
        return ResponseEntity.ok().body(userService.validateOTP(dto.getOneTimePassword(), dto.getUserName()));
    }

}

