package com.ecommerce.webapp.exception;

/**
 * Exception to highlight a user does not have a verified email address.
 */
public class UserNotVerifiedException extends RuntimeException {

  public UserNotVerifiedException(String s) {
  }
}
