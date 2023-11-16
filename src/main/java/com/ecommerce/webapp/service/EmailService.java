package com.ecommerce.webapp.service;

import com.ecommerce.webapp.exception.EmailFailureException;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.model.VerificationToken;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * Service for handling emails being sent.
 */
@Service
public class EmailService {

  /** The from address to use on emails. */
  @Value("${email.from}")
  private String fromAddress;
  /** The url of the front end for links. */
  @Value("${app.frontend.url}")
  private String url;
  /** The JavaMailSender instance. */
  private JavaMailSender javaMailSender;


  public EmailService(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

  /**
   * Makes a SimpleMailMessage for sending.
   * @return The SimpleMailMessage created.
   */
  private SimpleMailMessage makeMailMessage() {
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setFrom(fromAddress);
    return simpleMailMessage;
  }

  /**
   * Sends a verification email to the user.
   * @param verificationToken The verification token to be sent.
   * @throws EmailFailureException Thrown if are unable to send the email.
   */
  public void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException, MessagingException, UnsupportedEncodingException {
//    SimpleMailMessage message = makeMailMessage();
//    message.setTo(verificationToken.getUser().getEmail());
//    message.setSubject("Verify your email to active your account.");
//    message.setText("Please follow the link below to verify your email to active your account.\n" +
//        url + "/auth/verify?token=" + verificationToken.getToken());

    String toAddress = verificationToken.getUser().getEmail();
    String fromTheAddress = fromAddress;
    String senderName = "E-Commerce Shop";
    String subject = "Please verify your registration";
    String content = "Dear [[name]],<br>"
            + "Please click the link below to verify your registration:<br>"
            + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
            + "Thank you,<br>"
            + "E-Commerce Shop";

    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    helper.setFrom(fromTheAddress, senderName);
    helper.setTo(toAddress);
    helper.setSubject(subject);

    content = content.replace("[[name]]", verificationToken.getUser().getUsername());
    String verifyURL = url + "/auth/verify?token=" + verificationToken.getToken();

    content = content.replace("[[URL]]", verifyURL);

    helper.setText(content, true);

    try {
      javaMailSender.send(message);
      System.out.println("Email has been sent");
    } catch (MailException ex) {
      EmailFailureException.handleException("Error sending registration email", verificationToken.getUser().getUsername(), ex);
    }
  }


  /**
   * Sends a Welcome email to the user.
   * @param verificationToken The verification token to be sent.
   * @throws EmailFailureException Thrown if are unable to send the email.
   */
  public void sendWelcomeEmail(VerificationToken verificationToken) throws EmailFailureException, MessagingException, UnsupportedEncodingException {

    String toAddress = verificationToken.getUser().getEmail();
    String fromTheAddress = fromAddress;
    String senderName = "E-Commerce Shop";
    String subject = "Congratulations and welcome aboard!";
    String content = "Dear [[name]],<br>"
            + " Thank you for joining E-Commerce Shop. We'd like to confirm that your account was created successfully. you can continue to access our portal.<br>"
            + "Thank you,<br>"
            + "E-Commerce Shop";

    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    helper.setFrom(fromTheAddress, senderName);
    helper.setTo(toAddress);
    helper.setSubject(subject);

    content = content.replace("[[name]]", verificationToken.getUser().getUsername());

    helper.setText(content, true);

    try {
      javaMailSender.send(message);
      System.out.println("Email has been sent out for successful registration");
    } catch (MailException ex) {
      EmailFailureException.handleException("Error sending welcome email", verificationToken.getUser().getUsername(), ex);
    }
  }

  /**
   * Sends a password reset request email to the user.
   * @param user The user to send to.
   * @param token The token to send the user for reset.
   * @throws EmailFailureException
   */
  public void sendPasswordResetEmail(LocalUser user, String token) throws EmailFailureException, MessagingException, UnsupportedEncodingException {
//    SimpleMailMessage message = makeMailMessage();
//    message.setTo(user.getEmail());
//    message.setSubject("Your password reset request link.");
//    message.setText("You requested a password reset on our website. Please " +
//        "find the link below to be able to reset your password.\n" + url +
//        "/auth/reset?token=" + token);

    String toAddress = user.getEmail();
    String fromTheAddress = fromAddress;
    String senderName = "E-Commerce Shop";
    String subject = "Your password reset request link.";
    String content = "Dear [[name]],<br>"
            + "Please click the link below to reset your password:<br>"
            + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
            + "Thank you,<br>"
            + "E-Commerce Shop";

    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    helper.setFrom(fromTheAddress, senderName);
    helper.setTo(toAddress);
    helper.setSubject(subject);

    content = content.replace("[[name]]", user.getUsername());
    String verifyURL = url + "/auth/reset?token=" + token;

    content = content.replace("[[URL]]", verifyURL);

    helper.setText(content, true);
    try {
      javaMailSender.send(message);
    } catch (MailException ex) {
      EmailFailureException.handleException("Error sending password reset email", user.getUsername(), ex);
    }
  }

}
