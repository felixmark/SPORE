package tools;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import main.GlobalVariables;
import main.GlobalVariables.WARNING_TYPE;
import object_classes.Node;

public class MailTool {

	/**
	 * Send a mail containing the warnings of a node.
	 * @param warnings ArrayList of warnings
	 * @param node Node that should send the mail
	 * @param receiverName Name of the receiver
	 * @param receiverEmail Email of the receiver
	 */
	public static void sendWarningMail(ArrayList<WARNING_TYPE> warnings, Node node, String receiverName, String receiverEmail) {
		
		// Essential definitions
		String loginName = SettingsHandlerTool.getSenderMailFromSettings();
	    String loginPassword = SettingsHandlerTool.getSenderPasswordFromSettings();
		String from = SettingsHandlerTool.getSenderMailFromSettings();
		String host = GlobalVariables.SMTP_HOST; // "smtp.gmail.com";
		String port = GlobalVariables.SMTP_PORT; // "587"
		
		// Create Subject and text of the mail
		String subject = GlobalVariables.APPNAME + " Mail: " + node.getName();
		String text = getEmailText(warnings, node, receiverName);
		
		// Create Body-part and append to mail
		MimeMultipart content = createMailContent(text);
		
		// Create and set Properties
		Properties properties = getMailProperties(host,port,loginName, loginPassword);

		// Create a session and the message itself
		Session session = Session.getDefaultInstance(properties);
		MimeMessage message = new MimeMessage(session);
		
		// Create mail addresses
		InternetAddress fromAddress = null;
	    InternetAddress toAddress = null;
	    
	    // Final sending of the mail
		try{
			// Set addresses
	        fromAddress = new InternetAddress(from, node.getName());
	        toAddress = new InternetAddress(receiverEmail);

	        // Send mail
	        message.setFrom(fromAddress);
	        message.setRecipient(javax.mail.Message.RecipientType.TO,toAddress);
		    message.setSubject(subject);
		    message.setContent(content);
		    
		    Transport transport = session.getTransport("smtp");
		    transport.connect(host, loginName, loginPassword);
		    transport.sendMessage(message, message.getAllRecipients());
		    transport.close();
		    DebugTool.debug_print(MailTool.class, "Mail sent.");
		} catch (Exception e) {
		   e.printStackTrace();
		}
	}
	
	
	/**
	 * Get the properties for the SMTP server
	 * @param host SMTP server to send the mail (i.e. "smtp.gmail.com")
	 * @param port TODO
	 * @param loginName Login username for the SMTP server
	 * @param loginPassword Password for the SMTP server
	 * @return Properties object containing the SMTP settings
	 */
	private static Properties getMailProperties(String host, String port, String loginName, String loginPassword) {
		Properties properties = System.getProperties();
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.user", loginName);
		properties.put("mail.smtp.password", loginPassword);
		properties.put("mail.smtp.port", port);
		properties.put("mail.smtp.auth", "true");
		return properties;
	}


	/**
	 * Get the Mail as a MimeMultiPart by passing a String as the content text.
	 * @param text Body of the mail
	 * @return MimeMultiPart containing the mail
	 */
	private static MimeMultipart createMailContent(String text) {
		MimeMultipart content = new MimeMultipart("related");
		try {
			// Text
			MimeBodyPart htmlPart = new MimeBodyPart();
			
			String cid = ContentIdGeneratorTool.generateContentId("image");
			
			htmlPart.setContent(""
			  + "<html>"
			  + " <body>"
			  + "  <p>"
			  + text
			  + "  </p>"
			  + "  <img src=\"cid:"+cid+"\" />"
			  + "  <p>This mail was sent by<br>"+GlobalVariables.APPNAME+" v"+GlobalVariables.VERSION+"</p>"
			  + " </body>"
			  + "</html>" 
			  , "text/html; charset=utf-8");
			htmlPart.setDisposition(Part.INLINE);
			
			// Image
			MimeBodyPart imagePart = new MimeBodyPart();
			imagePart.attachFile("resources/spore_logo.jpg");
			imagePart.setContentID("<" + cid + ">");
			imagePart.setDisposition(Part.INLINE);
			
			// Add text and image to content
			content.addBodyPart(htmlPart);
			content.addBodyPart(imagePart);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}


	/**
	 * Get the whole email Text in HTML format.
	 * @param warnings ArrayList of Warnings that should be mentioned in the email
	 * @param node Node that should "write" the text
	 * @param receiverName Name of the receiver
	 * @return String in HTML format containing the whole email text
	 */
	public static String getEmailText(ArrayList<WARNING_TYPE> warnings, Node node, String receiverName) {
		String text = "Dear " + receiverName + "!<br><br>";
		
		for (WARNING_TYPE warning : warnings) {
			text = text + getEmailWarningText(warning);
		}
		
		// Sign at the end of the mail
		return text + "<br>" + "Yours truly,<br>" + node.getName()+"<br>" + "<hr>";
	}
	
	
	/**
	 * Get email warning text by passing a Warning Type.
	 * @param type Warning Type
	 * @return String containing the Warning for the user.
	 */
	public static String getEmailWarningText(GlobalVariables.WARNING_TYPE type) {
		switch (type) {
			case TOO_DRY_TOP:
				return	"My soil moisture (top) is critically low.<br>" + 
						"Please water me.<br>";
			case TOO_DRY_CEN:
				return	"My soil moisture (center) is critically low.<br>" + 
						"Please water me.<br>";
			case TOO_DRY_BOT:
				return	"My soil moisture (bottom) is critically low.<br>" + 
						"Please water me.<br>";
			case TOO_MUCH_SUN:
				return	"There is too much sun for me during daytime (6:00-20:00).<br>" + 
						"Please find a better place for me.<br>";
			case TOO_FEW_SUN:
				return	"There is too few sun for me during daytime (6:00-20:00).<br>" + 
						"Please find a better place for me.<br>";
			default: return null;
		}
	}
}
