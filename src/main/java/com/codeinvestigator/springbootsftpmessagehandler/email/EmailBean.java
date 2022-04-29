package com.codeinvestigator.springbootsftpmessagehandler.email;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6025087307789018430L;
	
	private String templateFile;
	private String from;
	private List<String> to;
	private List<String> cc;
	private List<String> bcc;
	private List<File> attachmentFiles;
	private Map<String, Object> datamap;
	private String subject;
	
	public EmailBean(final String templateFile) {
		this.templateFile = templateFile;
		this.from = null;
		this.to = new ArrayList<>();
		this.cc = new ArrayList<>();
		this.bcc = new ArrayList<>();
		this.attachmentFiles = new ArrayList<>();
		this.datamap = new HashMap<>();
	}
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public List<String> getCc() {
		return cc;
	}

	public void setCc(List<String> cc) {
		this.cc = cc;
	}

	public List<String> getBcc() {
		return bcc;
	}

	public void setBcc(List<String> bcc) {
		this.bcc = bcc;
	}

	public List<File> getAttachmentFiles() {
		return attachmentFiles;
	}
	public void setAttachmentFiles(List<File> attachmentFiles) {
		this.attachmentFiles = attachmentFiles;
	}
	public Map<String, Object> getDatamap() {
		return datamap;
	}
	public void setDatamap(Map<String, Object> datamap) {
		this.datamap = datamap;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(String templateFile) {
		this.templateFile = templateFile;
	}
	
	public void addTo(String to) {
		this.to.add(to);
	}
	public void addCc(String cc) {
		this.cc.add(cc);
	}
	public void addBcc(String bcc) {
		this.bcc.add(bcc);
	}
	public void addAttachmentFile(File file) {
		this.attachmentFiles.add(file);
	}
	public void put(String key, Object value) {
		this.datamap.put(key, value);
	}
}
