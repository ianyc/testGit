package com.chtn.cronJob;

import java.util.Hashtable;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import com.chtn.spaws.parse.ParseXML;
import com.chtn.util.Env;



public class MailAlert {
	private String mailConfig = Env.MAIL_CFG_PATH;
	
	private MailSender mailSender;
    private MailAlert MailAlert;
    private SimpleMailMessage msg;
    private String from;
    
    public MailAlert() {
		msg = new SimpleMailMessage();
		from = "SAWS@cht.com.tw";
    }
    
    
    public void setMailAlert(String filename) {
        Resource rs = new FileSystemResource(filename);
        BeanFactory factory = new XmlBeanFactory(rs);
        this.MailAlert = (MailAlert)factory.getBean("MailAlert");
    }
    
    
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }
	
    public void send() {
        try {
            for(int i=0; i<msg.getTo().length; i++)
                System.out.println("Send mail to " + msg.getTo()[i].toString());
            this.MailAlert.mailSender.send(msg);
        } catch(MailException ex) {
            //log it and go on
            System.err.println(ex.getMessage());
        }
    }
    
	
    public void setContent(String subject,StringBuffer content) {
        setMailAlert(mailConfig);
        msg.setSubject(subject);
        msg.setFrom(from);
        msg.setText(content.toString());
    }
	
	
	
    public void setTo(String [] to) {
        msg.setTo(to);
    }
	
	
	public static void main(String[] args) {
		MailAlert mailAlert = new MailAlert();
		
		//收件者
		String[] mailList = {"ianyc@cht.com.tw"};
    	mailAlert.setTo(mailList);
    	
    	//信件內容
    	StringBuffer sb = new StringBuffer();
        sb.append("此封郵件為自動發送，請勿直接回覆");
        
        //信件title
    	mailAlert.setContent("測試信件", sb);
    	
    	//Send
    	mailAlert.send();
	}
}
