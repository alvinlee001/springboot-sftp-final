package com.codeinvestigator.springbootsftpmessagehandler.services.email.service;


import javax.mail.internet.MimeMessage;
import com.codeinvestigator.springbootsftpmessagehandler.services.email.EmailBean;


public interface EmailService {
	void sendEmail(EmailBean emailBean) throws Exception;
	MimeMessage getMimeMessage(EmailBean emailBean) throws Exception;
	boolean sendMail(final String from, String tos, final String subject, final String message);
	EmailBean attachEform(EmailBean emailBean, String pdfPath, String contextPath, String id, String prefix);
}
