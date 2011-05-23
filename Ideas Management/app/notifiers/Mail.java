package notifiers;

import play.mvc.*;
import java.util.*;

import controllers.Security;

import models.*;
/**
 * This class is for sending the emails
 * 
 * @author Mai Magdy
 *
 */
public class Mail extends Mailer {

	/**
	 * Sends a mail to the registered/unregistered inviting him to be
	 * organizer/idea developer
	 * 
	 * @author ${Mai.Magdy},${Fadwa.Sakr}
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
	 *            String organization name that sends the invitation
	 * 
	 * @param entity
	 *            String entity name that sends the invitation whether its an
	 *            entity/topic
	 * 
	 * @param type
	 *            int type whether the email is sent from topic(1)or entity(0)
	 * 
	 */

	public static void invite(String email, String role, String organization,
			String entity, int type) {
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
		if (role.equals("ideadeveloper"))
			role = " idea developer";
		send(user, role, url, organization, entity, id, check, type);

	}

	/**
	 * Sends a mail to the user informing him that his account has been
	 *  deactivated
	 * 
	 * @author Mai Magdy
	 * 
	 * @story C1S5
	 * 
	 * 
	 */

	public static void deactivate() {
		User user = Security.getConnected();
		addRecipient(user.email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Deactivation");
		String url = "http://localhost:9008";
		send(user, url);

	}

	/**
	 * Sends a mail to the user informing him that his account has been
	 * reactivated
	 * 
	 * @author Mai Magdy
	 * 
	 * @story C1S5
	 * 
	 */

	public static void reactivate() {
		User user = Security.getConnected();
		addRecipient(user.email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Reactivation");
		send(user);

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

	public static void reportAsSpamMail(User topicOrganizer, User reporter,
			Idea reportedIdea, String description, String title) {
		System.out.println("maiiiiiiiiii");
		addRecipient(topicOrganizer.email);
		setFrom("coolsoft-11@gmail.com");
		setSubject("An Idea is Reported as a Spam");
		send(topicOrganizer, reporter, reportedIdea, description, title);
	}

	/*
	 * this Method is responsible for sending a mail to the topic organizers
	 * when a Topic inside the topic is reported as a spam
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S16
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

	public static void reportTopicMail(User reciever, User reporter,
			Topic reportedTopic, String description, String title) {
		long id = reportedTopic.id;
		System.out.println("maiiiiiiiiii");
		addRecipient("elhadiahmed3@gmail.com");
		setFrom("coolsoft-11@gmail.com");
		setSubject("A Topic has been Reported as a Spam");
		send(reciever, reporter, id, description, title);
	}

	/**
	 * Sends an e-mail to the user with a new generated password and gives him a
	 * hyper link to the login page
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S21
	 * 
	 * @param username
	 *            String the username of the receiver
	 * @param email
	 *            String the e-mail of the recipient
	 * @param password
	 *            String the new generated password
	 */

	public static void recoverPassword(String username, String email,
			String password) {
		System.out.println(email);
		addRecipient(email);
		setFrom("coolsoft-11@gmail.com");
		setSubject("No Relpay: Password recovery");
		String url = "http://localhost:9008/secure/login";
		send(username, password, url);
	}

	/**
	 *sends an email to the person who was added by the admin
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
	 *  sends an email to the person who was added by the admin to activate his account
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9 , C1S10
	 * 
	 * @param user
	 *            :User the user
	 * 
	 */
	public static void activation(User user) {
		addRecipient(user.email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Welcome to CoolSoft, activate your account ");
		String url = "http://localhost:9008/Users/activate?userId="
				+ user.id;
		send(user, url);

	}

	/**
	 * sends an email to the person who was deleted by the admin , notifying him he was deleted and 
	 * displaying the reason he was deleted for
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param user
	 *            :User the user
	 * 
	 * @param message
	 *            :String the reason for which the user has been deleted
	 */
	public static void deletion(User user, String message) {
		System.out.println(user.email);
		addRecipient(user.email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Your account has been deleted ! ");
		System.out.println(user);
		send(user, message);

	}
	
	/**
	 * sends an email to the person who was deleted by the admin , notifying him he was brought
	 *  back(undeleted)
	 * 
	 * @author Mostafa Ali
	 * 
	 * @story C1S9
	 * 
	 * @param user
	 *            :User the user
	 */
	public static void undeletion(User user) {
		System.out.println(user.email);
		addRecipient(user.email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Welcome back ! ");
		String url = "http://localhost:9008";
		send(user,url);

	}

}