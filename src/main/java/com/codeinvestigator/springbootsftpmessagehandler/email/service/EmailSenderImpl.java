package com.codeinvestigator.springbootsftpmessagehandler.email.service;

import com.codeinvestigator.springbootsftpmessagehandler.email.EmailBean;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.ClassUtils;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.StandardCharsets;

@Service
public class EmailSenderImpl extends BaseEmailSenderImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtils.getShortName(EmailSenderImpl.class));

	@Override
	public void sendEmail(EmailBean emailBean) throws Exception {
		MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

        for(File file : emailBean.getAttachmentFiles())
        {
        	if(file == null)
        	{
        		continue;
        	}
        	
        	helper.addAttachment(file.getName(), file);
        }

        Template t = freemarkerConfig.getTemplate(emailBean.getTemplateFile());
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, emailBean.getDatamap());

        helper.setTo(emailBean.getTo().toArray(new String[0]));
        helper.setText(html, true);
        helper.setSubject(emailBean.getSubject());

        String from = emailBean.getFrom();

        helper.setFrom(from);

        LOGGER.info("***********************************************");
        LOGGER.info("********** Email Start to send! **********");
        LOGGER.info("** Email Sender {}", from);
        LOGGER.info("** Email Recipient {}", emailBean.getTo());
        LOGGER.info("** Email Subject {}", emailBean.getSubject());
        LOGGER.info("***********************************************");

        javaMailSender.send(message);
	}

    @Override
    public Logger getLogger() {
        return LOGGER;
    }
}
