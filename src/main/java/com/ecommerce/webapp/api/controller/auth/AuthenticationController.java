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
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationBody registrationBody) throws MessagingException, EmailFailureException, UnsupportedEncodingException {
        userService.registerUser(registrationBody);
        String message = "User registration is under process. Please check and verify your email. Also register and verify your mobile to complete user profile.";
        return ResponseEntity.status(HttpStatus.OK)
                .body(message);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) throws UserNotVerifiedException, MessagingException, UnsupportedEncodingException {
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
            response.setFailureReason("User login successful for user : " + loginBody.getUsername());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) throws MessagingException, UnsupportedEncodingException {
        if (userService.verifyUser(token)) {
            return ResponseEntity.ok().body("Verified email successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Conflict error during email verification");
        }
    }


    @GetMapping("/me")
    public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user) {
        return user;
    }

    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) throws EmailNotFoundException, MessagingException, UnsupportedEncodingException {

        userService.forgotPassword(email);
        return ResponseEntity.ok().body("Password reset email has been successfully sent to the user.");
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetBody body) {

        userService.resetPassword(body);
        return ResponseEntity.ok().body("Password has been successfully reset for the user.");
    }

    @PostMapping("/sendOTP")
    public ResponseEntity<?> sendOTP(@Valid @RequestBody MobileOTPRequestDto dto) {

        logger.info("sendOTP Request: {}", dto.toString());
        return ResponseEntity.ok().body(userService.sendOTPToContactNumber(dto));
    }

    @PostMapping("/validateOTP")
    public ResponseEntity<?> validateOTP(@Valid @RequestBody MobileOTPRequestDto dto) {

        logger.info("Validating OTP for user: {}", dto.getUserName());
        return ResponseEntity.ok().body(userService.validateOTP(dto.getOneTimePassword(), dto.getUserName()));
    }

}

