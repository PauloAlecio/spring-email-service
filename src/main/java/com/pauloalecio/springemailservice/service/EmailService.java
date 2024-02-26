package com.pauloalecio.springemailservice.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.pauloalecio.springemailservice.dto.Email;
import com.pauloalecio.springemailservice.exception.ObjectNotFoundException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
  
    @Value("${default.sender}")
      private String sender;
      @Value("${default.url}")
      private String contextPath;
   @Autowired
    private TemplateEngine templateEngine;
  private final JavaMailSender mailSender;

  public EmailService(JavaMailSender mailSender){
    this.mailSender =  mailSender;
  }


  public void registerUser(Email email) {
    sendConfirmationHtmlEmail(email);
  }

  public void sendEmail(Email email){
    var message = new SimpleMailMessage();
    message.setFrom("noreplay@email.com");
    message.setTo(email.to());
    message.setSubject(email.subject());
    message.setText(email.body());
    mailSender.send(message);
  }

  public void sendConfirmationHtmlEmail(Email email){
    try {
        MimeMessage mimeMessage = prepareMimeMessageFromUser(email);
        sendHtmlEmail(mimeMessage);
    } catch (MessagingException msg) {
        throw new ObjectNotFoundException(String.format("Erro ao tentar enviar o e-mail"));
    }
}

  private void sendHtmlEmail(MimeMessage mimeMessage) {
    mailSender.send(mimeMessage);
  }

  protected MimeMessage prepareMimeMessageFromUser(Email email) throws MessagingException {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
    mimeMessageHelper.setTo(email.to());
    mimeMessageHelper.setFrom(this.sender);
    mimeMessageHelper.setSubject("Confirmação de Registro");
    mimeMessageHelper.setSentDate(new Date((System.currentTimeMillis())));
    mimeMessageHelper.setText(htmlFromTemplateUser(email), true);
    return mimeMessage;
}

protected String htmlFromTemplateUser(Email email){
        String token = UUID.randomUUID().toString();
        
        String confirmationUrl = this.contextPath + "/api/v1/public/registrationConfirm/users?token="+token;
        Context context = new Context();
        context.setVariable("email", email.to());
        context.setVariable("confirmationUrl", confirmationUrl);
        return templateEngine.process("email/registerUser", context);
    }

}
