package com.ecommerce.webapp.exception;

import com.ecommerce.webapp.api.model.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception to highlight a user does not have a verified email address.
 */
public class UserNotVerifiedException extends Exception {

  private boolean newEmailSent;

  private static final Logger logger = LoggerFactory.getLogger(UnexpectedException.class);

  public UserNotVerifiedException(boolean newEmailSent) {
    this.newEmailSent = newEmailSent;
  }

  public boolean isNewEmailSent() {
    return newEmailSent;
  }

  public static void handleUserNotVerifiedException(String message, String username, UserNotVerifiedException ex) {
    logger.warn("{}: {}", message, username);
    LoginResponse response = new LoginResponse();
    response.setSuccess(false);
    String reason = "USER_NOT_VERIFIED";
    if (ex.isNewEmailSent()) {
      reason += "_EMAIL_RESENT";
    }
    response.setFailureReason(reason);
    throw new ResponseStatusException(HttpStatus.FORBIDDEN, message, ex);
  }

}
