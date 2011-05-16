package notifiers;

import play.mvc.*;
import java.util.*;

import controllers.Security;

import models.*;

public class Mail extends Mailer {

	/**
	 * Send a mail to the registered/unregistered inviting him to be
	 * organizer/idea developer
	 * 
	 * @author ${Mai Magdy}
	 * 
	 * @story C1S6
	 * 
	 * @param role
	 *            String role whether idea developer/organizer
	 * 
	 * @param email
	 *            String email the invitation will be sent to
	 * 
	 * @param organization
	 *            Organization organization name that sends the invitation
	 * 
	 * @param entity
	 *            MainEntity entity name that sends the invitation
	 * 
	 */

	public static void invite(String email, String role, String organization,
			String entity) {
		addRecipient(email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Invitation");
		String url = "http://localhost:9008/invitations/view";
		User user = User.find("byEmail", email).first();
		System.out.println(entity);
		int id = 1;
		int check = 1;
		if (user == null)
			id = 0;
		if (entity == "")
			check = 0;
		send(user, role, url, organization, entity, id, check);

	}

	/*
	 * this Method is responsible for sending a mail to the topic organizers
	 * when an idea inside the topic is reported as a spam
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S12
	 * 
	 * @param topicOrganizer : the organizer of be topic
	 * 
	 * @param reporter : the user who reported the idea
	 * 
	 * @param reportedIdea : the reported idea
	 * 
	 * @param description : description of the idea
	 * 
	 * @param title : title of idea
	 */

	public static void ReportAsSpamMail(User topicOrganizer, User reporter,
			Idea reportedIdea, String description, String title) {
		addRecipient(topicOrganizer.email);
		setFrom("coolsoft-11@gmail.com");
		setSubject("An Idea is Reported as a Spam");
		send(topicOrganizer, reporter, reportedIdea);
	}
	
	public static void recoverPassword(String username, String email, String password) {
		System.out.println(email);
		addRecipient(email);
		setFrom("coolsoft-11@gmail.com");
		setSubject("No Relpay: Password recovery");
		String url = "http://localhost:9008/secure/login";
		send(username, password, url);
	}

	/**
	 * @description used to send a mail to the person who was added by the admin
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param user
	 *            :User the user
	 * 
	 */
	public static void welcome(User user) {
		addRecipient(user.email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Welcome");
		String url = "http://localhost:9008";
		send(user, url);

	}
	

	/**
	 * @Sends a mail to the user informing him that his account has been deactivated
	 * 
	 * @author Mai Magdy
	 * 
	 * @story C1S5
	 * 
	 * 
	 */
	
	public static void deactivate() {
		User user=Security.getConnected();
		addRecipient(user.email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Deactivation");
		String url = "http://localhost:9008";
		send(user, url);

	}
	
	
	

	/**
	 * @Sends a mail to the user informing him that his account has been reactivated
	 * 
	 * @author Mai Magdy
	 * 
	 * @story C1S5
	 * 
	 * 
	 */
	
	public static void reactivate() {
		User user=Security.getConnected();
		addRecipient(user.email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Reactivation");
		send(user);

	}
	
	
}