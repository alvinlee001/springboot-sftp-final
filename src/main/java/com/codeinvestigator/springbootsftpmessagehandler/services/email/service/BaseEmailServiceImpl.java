package com.codeinvestigator.springbootsftpmessagehandler.services.email.service;

import com.codeinvestigator.springbootsftpmessagehandler.services.email.EmailBean;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseEmailServiceImpl implements EmailService {

	@Autowired
	protected JavaMailSender javaMailSender;

	@Autowired
	protected Configuration freemarkerConfig;

	@Override
	public MimeMessage getMimeMessage(EmailBean emailBean) throws Exception {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

		for(File file : emailBean.getAttachmentFiles()) {
			if(file == null) {
				continue;
			}

			helper.addAttachment(file.getName(), file);
		}

		Template template = freemarkerConfig.getTemplate(emailBean.getTemplateFile());
		String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, emailBean.getDatamap());

		helper.setTo(emailBean.getTo().toArray(new String[0]));
		helper.setText(html, true);
		helper.setSubject(emailBean.getSubject());

		return helper.getMimeMessage();
	}

	@Override
	public boolean sendMail(final String from, String tos, final String subject, final String message) {
		return sendMail(from, new String[] {tos}, new String[]{}, null, subject, message);
	}

	private boolean sendMail(final String from, String[] tos, String[] ccs, final File file, final String subject, final String message) {
		boolean isSuccess =false;
		try {

			EmailBean emailBean = new EmailBean("templates/email.ftl");
			emailBean.setSubject(subject);
			emailBean.setFrom(from);
			emailBean.setTo(Arrays.asList(tos));
			emailBean.put("content", message);

			if (file != null) {
				emailBean.addAttachmentFile(file);
			}

			sendEmail(emailBean);
			isSuccess =true;
		} catch (Exception e) {
			getLogger().error("sendMail", e);
		}

		return isSuccess;
	}

	@Override
	public EmailBean attachEform(EmailBean emailBean, String pdfPath, String contextPath, String id, String prefix) {
		File file = new File(pdfPath + contextPath + id);
		if(file.exists() && file.isDirectory()) {
			log.info("file exist and is directory");
			File[] files = file.listFiles();
			List<File> eforms = Arrays.stream(files)
					.filter(fileObj -> fileObj.getName().startsWith(prefix))
					.collect(Collectors.toList());
			for(File eform : eforms) {
				log.info("attached " + eform.getAbsolutePath());
			}
			emailBean.setAttachmentFiles(eforms);
		}
		return emailBean;
	}

	public abstract Logger getLogger();

}
