package com.ecommerce.webapp.controller.auth;

import com.ecommerce.webapp.dto.*;
import com.ecommerce.webapp.exception.*;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

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
        log.info("Received Request For Registering A user");
        userService.registerUser(registrationBody);
        return ResponseEntity.status(HttpStatus.OK)
                .body(REGISTRATION_UNDER_PROCESS);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) throws UserNotVerifiedException, MessagingException, UnsupportedEncodingException {
        log.info("Logging In Using Provided User Credentials / Mobile");
        String jwt = userService.loginUser(loginBody);
        if (jwt == null) {
            log.warn("User login failed for : {}", loginBody.getUsername());
            LoginResponse response = new LoginResponse();
            response.setJwt(null);
            response.setSuccess(false);
            response.setFailureReason(USER_LOGIN_FAILED);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            log.warn("User login was successful for : {}", loginBody.getUsername());
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            response.setSuccess(true);
            response.setFailureReason("User login successful!");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) throws MessagingException, UnsupportedEncodingException {
        log.info("Verifying User Via Email");
        if (userService.verifyUser(token)) {
            log.info("Verified User Via Email");
            return ResponseEntity.ok().body(VERIFIED_EMAIL_SUCCESSFULLY);
        } else {
            log.info("User Verification Failed via Email!");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(CONFLICT_ERROR_DURING_EMAIL_VERIFICATION);
        }
    }


    @GetMapping("/me")
    public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user) {
        log.info("Sharing User Details for the Current session");
        return user;
    }

    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) throws EmailNotFoundException, MessagingException, UnsupportedEncodingException {

        log.info("Password Reset Request Received!");
        userService.forgotPassword(email);
        return ResponseEntity.ok().body(PASSWORD_RESET_EMAIL_SENT);
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetBody body) {

        log.info("Password Reset Request Is In Process!");
        userService.resetPassword(body);
        return ResponseEntity.ok().body(PASSWORD_RESET_SUCCESSFULLY);
    }

    @PostMapping("/sendOTP")
    public ResponseEntity<?> sendOTP(@Valid @RequestBody MobileOTPRequestDto dto) {

        log.info("Sending OTP to Registered Mobile For Verification");
        return ResponseEntity.ok().body(userService.sendOTPToContactNumber(dto));
    }

    @PostMapping("/validateOTP")
    public ResponseEntity<?> validateOTP(@Valid @RequestBody MobileOTPRequestDto dto) {

        log.info("Validating OTP for user: {}", dto.getUserName());
        return ResponseEntity.ok().body(userService.validateOTP(dto.getOneTimePassword(), dto.getUserName()));
    }

}

