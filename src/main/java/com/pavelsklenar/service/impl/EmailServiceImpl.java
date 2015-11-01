package com.pavelsklenar.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.pavelsklenar.domain.SearchResult;
import com.pavelsklenar.service.EmailService;

@Component
public class EmailServiceImpl implements EmailService {

	private static final Logger LOG = LoggerFactory
			.getLogger(EmailServiceImpl.class);
	
	@Autowired 
    private JavaMailSender mailSender;
    
    @Autowired 
    private TemplateEngine templateEngine;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pavelsklenar.service.impl.EmailService#sendMails(java.util.List)
	 */
	public void sendMails(List<SearchResult> listToSend) throws MessagingException {
		if (listToSend == null || listToSend.isEmpty()) {
			LOG.info("Cannot send emails, it would be empty, no results to send.");
			return;
		}
		LOG.info("Trying to send mails with {} new search results",
				listToSend.size());
		// Prepare the evaluation context
        final Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("name", listToSend.get(0).getSearchPage().getName());
        ctx.setVariable("searchUrl", listToSend.get(0).getSearchPage().getUrl());
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("results", listToSend);
        
        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        if (listToSend.size() == 1) {
        	message.setSubject(listToSend.get(0).getTitle() + ", " + listToSend.get(0).getPrice());
        } else {
        	message.setSubject("Nalezeny " + listToSend.size() + " nové výsledky na " + listToSend.get(0).getSearchPage().getName());
        }
        
        message.setFrom("sklenar.bol@seznam.cz");
        message.setTo("sklenar.bol@seznam.cz");

        // Create the HTML body using Thymeleaf
        final String htmlContent = this.templateEngine.process("email", ctx);
        message.setText(htmlContent, true /* isHtml */);
        
        // Send email
        this.mailSender.send(mimeMessage);
        
        LOG.info("Email successfully send.");
		
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
}