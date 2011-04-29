package notifiers;

import play.mvc.*;
import java.util.*;

import models.*;

public class Mail extends Mailer {

	public static void invite(String email, String role, String organization,
			String entity) {
		addRecipient(email);
		setFrom("CoolSoft011@gmail.com");
		setSubject("Invitation");
		// String Link = "" ;
		User user = User.find("byEmail", email).first();
		send(user, role, organization, entity);

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
	 */

	public static void ReportAsSpamMail(User topicOrganizer, User reporter,
			Idea reportedIdea) {
		String email = "The idea with this title " + reportedIdea.title
				+ " and with this description " + reportedIdea.description
				+ " has been reported as a spam by this user "
				+ reporter.username;
		addRecipient(email);
		setFrom("coolsoft-11@gmail.com");
		setSubject("An Idea is Reported as a Spam");
		send(topicOrganizer);
	}
}