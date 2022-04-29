package com.codeinvestigator.springbootsftpmessagehandler.services.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.util.StringUtils;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import java.util.Properties;

@Configuration
@ComponentScan({
	"com.ft.cloudsign.job.email"
})
public class EmailConfig {

    @Value("${email.smtp.auth}")
    private String auth;

    @Value("${email.smtp.starttls.enable}")
    private String starttlsEnable;

    @Value("${email.smtp.quitwait}")
    private String quitwait;

    @Value("${email.smtp.host}")
    private String host;

    @Value("${email.smtp.port}")
    private int port;
    @Value("${email.smtp.password}")
    private String password;
    @Value("${email.smtp.account}")
    private String account;

	@Bean
	@Primary
    public FreeMarkerConfigurationFactoryBean getFreeMarkerConfiguration() {
        FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
        bean.setTemplateLoaderPath("classpath:templates/");
        bean.setPreferFileSystemAccess(false);
        return bean;
    }

    @Bean
    public JavaMailSender javaMailSender() {
    	//https://stackoverflow.com/questions/52148167/random-error-javax-activation-unsupporteddatatypeexception-no-object-dch-for-mi
    	CommandMap mc = CommandMap.getDefaultCommandMap();
    	if(mc instanceof MailcapCommandMap) {
    		MailcapCommandMap mailcapComamndMap = (MailcapCommandMap) mc;
    		mailcapComamndMap.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
    		mailcapComamndMap.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
    		mailcapComamndMap.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
    		mailcapComamndMap.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
    		mailcapComamndMap.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");
            CommandMap.setDefaultCommandMap(mailcapComamndMap);
    	}
    	
        Properties mailProperties = new Properties();
        mailProperties.setProperty("mail.smtp.auth", auth);
        mailProperties.setProperty("mail.smtp.starttls.enable", starttlsEnable);
        mailProperties.setProperty("mail.smtp.quitwait", quitwait);

        JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
        javaMailSenderImpl.setHost(host);
        javaMailSenderImpl.setPort(port);

        if(!StringUtils.isEmpty(password)){
            javaMailSenderImpl.setPassword(password);
        }

        if(!StringUtils.isEmpty(account)){
            javaMailSenderImpl.setUsername(account);
        }
        javaMailSenderImpl.setJavaMailProperties(mailProperties);
        return javaMailSenderImpl;
    }
}
