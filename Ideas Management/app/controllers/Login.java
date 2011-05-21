package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import notifiers.Mail;

import models.Document;
import models.Idea;
import models.Invitation;
import models.MainEntity;
import models.Organization;
import models.Tag;
import models.Topic;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Manages the home page after logging in
 * 
 * @author Ahmed Maged
 */

@With(Secure.class)
public class Login extends Controller {

	/**
	 * Reactivates the account if it has been deactivated before Redirects the
	 * new registered users to the invitation page if they have been invited to
	 * be idea developers/organizers
	 * 
	 * Renders the login page, first the user has to login using his user name
	 * and password and then his profile page will be rendered
	 * 
	 * @author Mai Magdy
	 * @author Ahmed Maged
	 * 
	 * @Stroy C1S5, C1S11, C1S18
	 * 
	 */

	public static void homePage() {
		User user = Security.getConnected();
		List<Idea> ideas = user.ideasCreated;
		List<Idea> drafts = new ArrayList<Idea>();

		for (Idea s : ideas)
			if (s.isDraft)
				drafts.add(s);

		int admin = 0;
		if (user.isAdmin)
			admin = 1;

		if (user.state.equals("n")) {
			user.state = "a";
			user.save();
			Mail.reactivate();
			flash.error("Your account has been reactivated successfuly");
		}

		
		List<Organization> organizations = user.followingOrganizations;
		List<Tag> tags = user.followingTags;
		List<MainEntity> entities = user.followingEntities;
		List<Topic> topics = user.topicsIFollow;
		
		List<Document> documents;
		documents = Document.find("byUserOrganizationId", user.id).fetch();
		for (int i = 0; i < documents.size(); i++) {
			if (documents.get(i).isOrganization)
				documents.remove(i);
			}

		render(user, admin, drafts, organizations, tags, entities, topics, documents);
	}

	/**
	 * Renders the about view
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S18
	 */

	public static void about() {
		User user = Security.getConnected();
		render(user);
	}

	/**
	 * Renders the contact view
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S18
	 */

	public static void contact() {
		User user = Security.getConnected();
		render(user);
	}
}
