package com.moocode.apifacturas.service;

import com.moocode.apifacturas.exception.BusinessException;
import com.moocode.apifacturas.enums.OtpPurpose;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailNotificationService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String fromAddress;

    public EmailNotificationService(JavaMailSender mailSender, TemplateEngine templateEngine,
                                    @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.fromAddress = fromAddress;
    }

    public void sendCode(String recipient, String code, long expirationMinutes, OtpPurpose purpose) {
        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("expirationMinutes", expirationMinutes);
        context.setVariable("purpose", purpose);
        String html = templateEngine.process("email/otp", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(recipient);
            helper.setSubject("Codigo para " + purpose.getDescription() + " en MooCode");
            helper.setText(html, true);
            helper.addInline("banner", new ClassPathResource("static/images/BannerMooCode.jpeg"), "image/jpeg");
            mailSender.send(message);
        }
        catch (MessagingException | MailException mailException) {
            throw new BusinessException("No se pudo enviar el correo de verificación");
        }
    }

    public void sendCode(String recipient, String firstName){
        Context context = new Context();
        context.setVariable("firstName", firstName);
        String html = templateEngine.process("email/account-activated", context);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(recipient);
            helper.setSubject("Tu cuenta ya no esta activa");
            helper.setText(html, true);
            helper.addInline("banner", new ClassPathResource("static/images/BannerMooCode.jpeg"), "image/jpeg");
            mailSender.send(message);
        }
        catch (MessagingException | MailException mailException) {
            throw new BusinessException("No se pudo enviar el correo de activación de la cuenta");
        }
    }

    public void sendAccountActivated(String recipient,String firstName){
        Context context = new Context();
        context.setVariable("firstName", firstName);
        String html = templateEngine.process("email/account-activated", context);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
            helper.setFrom(fromAddress);
            helper.setSubject(recipient);
            helper.setText(html,true);
            helper.addInline("banner",new ClassPathResource("static/images/BannerMooCide.jpeg"),"image/jpeg");
            mailSender.send(message);
        }
        catch (MessagingException | MailException mailException) {
            throw new BusinessException("No se pudo enviar el correo de activacion de la cuenta");
        }
    }

    public void sendPaymentStatus(String recipient, String paymentCode, String invoiceCode,
                                  String status, String amount, String comment){
        Context context = new Context();
        context.setVariable("paymentCode", paymentCode);
        context.setVariable("invoiceCode", invoiceCode);
        context.setVariable("status", status);
        context.setVariable("amount", amount);
        context.setVariable("comment", comment);
        String html = templateEngine.process("email/payment-status", context);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(recipient);
            helper.setSubject("Actualización de tu pago " + paymentCode +" en MooCode");
            helper.setText(html, true);
            helper.addInline("banner", new ClassPathResource("static/images/BannerMooCode.jpeg"), "image/jpeg");
            mailSender.send(message);
        }
        catch (MessagingException | MailException mailException) {
            throw new BusinessException("No se pudo enviar el correo de actualización de pago");
        }
    }

    public void sendInvoiceStatus(String recipient, String invoiceCode, String status,
                                  String stampingFolio, String messageText){
        Context context = new Context();
        context.setVariable("invoiceCode", invoiceCode);
        context.setVariable("status", status);
        context.setVariable("stampingFolio", stampingFolio);
        context.setVariable("messageText", messageText);
        String html = templateEngine.process("email/invoice-status", context);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(recipient);
            helper.setSubject("Actualización de tu factura " + invoiceCode +" en MooCode");
            helper.setText(html, true);
            helper.addInline("banner", new ClassPathResource("static/images/BannerMooCode.jpeg"), "image/jpeg");
            mailSender.send(message);
        }
        catch (MessagingException | MailException mailException) {
            throw new BusinessException("No se pudo enviar el correo de actualización del correo");
        }
    }
}
