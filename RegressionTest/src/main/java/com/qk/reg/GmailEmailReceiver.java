package com.qk.reg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.imap.IMAPFolder;

public class GmailEmailReceiver {
	static String apklink = "";
	static String otp = "";

	public static String retrieveApkLink() throws MessagingException, IOException {
		IMAPFolder folder = null;
		Store store = null;
		String subject = null;
		Flag flag = null;

		try {
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");
			props.setProperty("mail.imaps.host", "imap.gmail.com");
			props.setProperty("mail.imaps.port", "993");
			props.setProperty("mail.imaps.connectiontimeout", "5000");
			props.setProperty("mail.imaps.timeout", "5000");

			Session session = Session.getInstance(props, null);

			store = session.getStore("imaps");
			store.connect("imap.gmail.com", "qkdevops@gmail.com", "Quark$1234");

			folder = (IMAPFolder) store.getFolder("inbox"); // This works for both email account

			if (!folder.isOpen())
				folder.open(Folder.READ_WRITE);

			Message[] messages = folder.getMessages();
			System.out.println("No of Messages : " + folder.getMessageCount());
			System.out.println("No of Unread Messages : " + folder.getUnreadMessageCount());
			System.out.println(messages.length);

			for (int i = 0; i < messages.length; i++) {
				System.out.println("*****************************************************************************");
				System.out.println("MESSAGE " + (i + 1) + ":");
				Message msg = messages[i];
				subject = msg.getSubject();
				System.out.println("UnreadTrue");

				if (subject.toString().indexOf("Android app") > 0) {
					System.out.println("AndroidAppTrue");
					ByteArrayOutputStream out = new ByteArrayOutputStream();

					try {
						msg.writeTo(out);

					} catch (IOException e) {
						e.printStackTrace();

					}

					//System.out.println(out);

					apklink = out.toString().substring(out.toString().indexOf("please <a href=\"https") + 16,
							out.toString().indexOf(".apk") + 4);
					
					//remove the message
					msg.setFlag(Flags.Flag.DELETED, true);

				}

				/*if (i < messages.length - 1) {
					msg.setFlag(Flags.Flag.DELETED, true);

				}*/

			}

		}

		finally {
			/*
			 * Message[] messages = folder.getMessages(); for (Message message :messages) {
			 * message.setFlag(Flags.Flag.DELETED, true); }
			 */
			if (folder != null && folder.isOpen()) {
				folder.close(true);
				
			}
			
			if (store != null) {
				store.close();
				
			}

		}

		if (apklink.contains("\n")) {
			apklink = apklink.replaceAll("\\r|\\n", "");
			apklink = apklink.replaceAll("=", "");

		}

		System.out.println("APK Link: " + apklink);

		return apklink;

	}

	public static void InboxDelAck() throws MessagingException, IOException {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("sinarmasotp@gmail.com", "BankS1narmas");
			
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("sinarmasotp@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,

			InternetAddress.parse("sinarmasotp@gmail.com"));
			message.setSubject("Acknowledgement");
			message.setText("Inbox Message Deleted");

			Transport.send(message);

			System.out.println("Message Sent Successfully");
			
		} catch (MessagingException e) {
			throw new RuntimeException(e);

		}

	}

	public static boolean recInboxDelAck() throws MessagingException, IOException {
		IMAPFolder folder = null;
		Store store = null;
		String subject = null;
		Flag flag = null;
		boolean sFlag = false;
		
		try {
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");
			props.setProperty("mail.imaps.host", "imap.gmail.com");
			props.setProperty("mail.imaps.port", "993");
			props.setProperty("mail.imaps.connectiontimeout", "5000");
			props.setProperty("mail.imaps.timeout", "5000");

			Session ssession = Session.getInstance(props, null);

			store = ssession.getStore("imaps");
			store.connect("imap.gmail.com", "sinarmasotp@gmail.com", "BankS1narmas");

			// folder = (IMAPFolder) store.getFolder("[Gmail]/Inbox"); // This doesn't work
			// for other email account
			folder = (IMAPFolder) store.getFolder("inbox"); // This works for both email account

			if (!folder.isOpen())
				folder.open(Folder.READ_WRITE);
			
			Message[] messages = folder.getMessages();
			System.out.println("No of Messages : " + folder.getMessageCount());
			System.out.println("No of Unread Messages : " + folder.getUnreadMessageCount());
			System.out.println(messages.length);
			
			for (int i = 0; i < messages.length; i++) {
				System.out.println("*****************************************************************************");
				System.out.println("MESSAGE " + (i + 1) + ":");
				Message msg = messages[i];
				subject = msg.getSubject();
				// System.out.println("Flag" +msg.getFlags());
				if (msg.getFlags().toString().indexOf("FLAGS@0") > 0) {
					// System.out.println("UnreadTrue");
					if (subject.toString().indexOf("Acknowledgement") > -1) {
						// System.out.println("AndroidAppTrue");
						sFlag = true;
						System.out.println("Acknowledgement received");
						msg.setFlag(Flags.Flag.DELETED, true);
					
					}
				
				}
			
			}

		} finally {
			/*
			 * Message[] messages = folder.getMessages(); for (Message message :messages) {
			 * message.setFlag(Flags.Flag.DELETED, true); }
			 */
			if (folder != null && folder.isOpen()) {
				folder.close(true);
				
			}
			
			if (store != null) {
				store.close();
				
			}
			
		}
		
		return sFlag;
		
	}

	public static void flushEmails() throws MessagingException, IOException {
		IMAPFolder folder = null;
		Store store = null;
		
		try {
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");
			props.setProperty("mail.imaps.host", "imap.gmail.com");
			props.setProperty("mail.imaps.port", "993");
			props.setProperty("mail.imaps.connectiontimeout", "5000");
			props.setProperty("mail.imaps.timeout", "5000");

			Session ssession = Session.getInstance(props, null);

			store = ssession.getStore("imaps");
			store.connect("imap.gmail.com", "sinarmasotp@gmail.com", "BankS1narmas");

			// folder = (IMAPFolder) store.getFolder("[Gmail]/Inbox"); // This doesn't work
			// for other email account
			folder = (IMAPFolder) store.getFolder("inbox"); // This works for both email account

			if (!folder.isOpen())
				folder.open(Folder.READ_WRITE);
			
			Message[] messages = folder.getMessages();
			
			for (Message message : messages) {
				message.setFlag(Flags.Flag.DELETED, true);
			
			}
			
			System.out.println("All mails deleted successfully");
			
			if (folder != null && folder.isOpen()) {
				folder.close(true);
			
			}
			
			if (store != null) {
				store.close();
			
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		
		}
	
	}

}