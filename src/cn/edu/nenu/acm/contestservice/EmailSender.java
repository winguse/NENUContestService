package cn.edu.nenu.acm.contestservice;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
	private String host = "";
	private String from = "";
	private String to = "";
	private String subject = "";
	private String content = "";
	private String user = "";
	private String password = "";

	private Properties props;
	private Session mailSession;
	private Transport transport;

	public void connect() throws MessagingException {
		props = new Properties();
		props.put("mail.smtp.host", host);// 指定SMTP服务器
		props.put("mail.smtp.auth", "true");// 指定是否需要SMTP验证
		mailSession = Session.getDefaultInstance(props);
		mailSession.setDebug(true);// 是否在控制台显示debug信息
		transport = mailSession.getTransport("smtp");
		transport.connect(host, user, password);
	}

	public void send() throws MessagingException {
		MimeMessage message = new MimeMessage(mailSession);
		message.setFrom(new InternetAddress(from));// 发件人
		message.setRecipients(Message.RecipientType.BCC, from);//密送给自己 
		String[] t = to.split(",");
		HashSet<String> setTo=new HashSet<String>();
		for (int i = 0; i < t.length; i++) {
			setTo.add(t[i].toLowerCase());
		}
		InternetAddress[] addressTo = new InternetAddress[setTo.size()];
		Iterator<String> it=setTo.iterator();
		for(int i=0;i<setTo.size();i++){
			addressTo[i] = new InternetAddress(it.next());
		}
		if(addressTo.length==1){
			message.setRecipient(Message.RecipientType.TO, addressTo[0]);// 收件人
		}else{
			message.setRecipients(Message.RecipientType.TO, addressTo);// 收件人
		}
		message.setSubject(subject);// 邮件主题
		message.setText(content, "UTF-8", "html");// 邮件内容
		message.saveChanges();
		transport.sendMessage(message, message.getAllRecipients());
	}

	public void close() throws MessagingException {
		transport.close();
	}

	public static void main__(String arg[]) {
		HashSet<String> wordSet = new HashSet<String>();
		String a = new String("hello").toLowerCase();
		String b = new String("hello").toLowerCase();
		wordSet.add(b);
		System.out.println(wordSet.contains(a));
	}
	public static void main(String arg[]) {
		Date d = new Date();
		String contentTemplate = "<!DOCTYPE html><html><head><title>东师赛事管理系统 - 系统通知</title><style>#main ptext-indent:2em;</style></head><body><p>COACH_NAME教练：</p><div id='main'><p>我们已经把ICPC官网上的数据全部导入到我们的系统中。我们的注册系统将于2012年9月18日7：00正式开放，请你使用USERNAME，密码为：PASSWORD，登录我们的系统：<a href='http://acm.nenu.edu.cn/ContestService/'>http://acm.nenu.edu.cn/ContestService/</a>，协助我们完成以下四件事情：</p><p><ol><li>确认你们的学校名（中英文，包括简称），教练名、队伍名、队员名，如有错误，请更正。并请同时告诉我们，这将关系到证书上的姓名。</li><li>补充教练、队员的中文信息（姓名、性别和衣服大小最为关键）。</li><li>添加打星队伍的信息。</li><li>添加随从人员的信息。</li></ol></p><p style='color:red;'>请登录后注意修改密码！如果贵校有多个教练，我们会同时向教练的邮箱发送邮件，所以如果您的密码修改请通知贵校的其他教练。</p><p>此外，我们的系统中还提供了宾馆预订功能，该功能将于2012年9月23日上午8：00正式开放。请及时登录预订房间。在该系统里可以浏览到你们对应的志愿者信息，如有什么问题也可以直接和志愿者联系。另外，请大家在买好往返车票或机票后，登录我们的系统补充上你们来长和离长的具体日期和时间，以方便我们的接待工作，谢谢！</p></div><p style='text-align:right;'>长春站赛事组委会<br/>"
				+ d.toLocaleString() + "</p></body></html>";
		EmailSender sender = new EmailSender();
		sender.setFrom("acm@nenu.edu.cn");
		sender.setUser("acm@nenu.edu.cn");
		sender.setPassword("密码也不能告诉你");//TODO 
		sender.setHost("smtp.nenu.edu.cn");
		sender.setSubject("【test】2012 ACM-ICPC Asia Regional Contest (Changchun Site) Contest Service System Notice");
		try {
			sender.connect();
			for (int i = 0; i < 1; i++) {
				sender.setTo("winguse@qq.com");
				sender.setContent(contentTemplate
						.replaceAll("USERNAME", "username")
						.replaceAll("PASSWORD", "jpassword")
						.replaceAll("COACH_NAME", "coachname"));
				sender.send();
			}
			sender.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public EmailSender() {
		super();
	}

	public EmailSender(String host, String from, String to, String subject,
			String content, String user, String password) {
		super();
		this.host = host;
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.content = content;
		this.user = user;
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
