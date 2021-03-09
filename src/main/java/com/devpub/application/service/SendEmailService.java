package com.devpub.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class SendEmailService {

	@Value("${rootPage}")
	private String rootPage;
	@Value("${loginChangeSuffix}")
	private String loginChangeSuffix;

	@Autowired
	private JavaMailSender javaMailSender;

	public void sendPasswordRecoveryEmail(String to, String userName, String hash)
			throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		String body =
				"<h3>Password recovery</h3>" +
				"<p><br>Hi, " + userName + ".  We heard that you lost your DevPub password. Sorry about that! " +
				"But don’t worry! You can click <a href=\"http://" + rootPage + loginChangeSuffix + hash +
				"\">here</a> to reset your password<br></p>";
		message.setContent(body, "text/html; charset=utf-8");
		helper.setFrom("noreply@devpub.com");
		helper.setTo(to);
		helper.setSubject("Password recovery for DevPub blog");

		javaMailSender.send(message);
	}
}
