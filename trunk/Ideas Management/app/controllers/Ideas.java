/**
 * 
 */
package controllers;

import java.util.ArrayList;

import models.Idea;
import models.Tag;
import models.Topic;
import models.User;

/**
 * @author ${Ahmed El-Hadi}
 * 
 */
public class Ideas extends CRUD {

	/*
	 * @author Abdalrahman Ali
	 * 
	 * this method saves an idea as a draft
	 * 
	 * @param title the title of the idea
	 * 
	 * @param body the body of the idea
	 * 
	 * @param topic the topic that the idea belongs to
	 * 
	 * @param user the user saving this idea
	 * 
	 * @return void
	 */

	public static void saveDraft(String title, String body, Topic topic,
			User user) {
		Idea idea = new Idea(title, body, user, topic, true).save();
	}

	/*
	 * @author Abdalrahman Ali
	 * 
	 * this method posts an idea that was saved as a draft
	 * 
	 * @param idea the saved idea
	 * 
	 * @return void
	 */

	public static void postDraft(Idea idea) {
		idea.isDraft = false;
		idea.save();
	}

	/*
	 * @author Abdalrahman Ali
	 * 
	 * this method returns all the ideas the user saved as draft in order to
	 * enable the user to choose one of them and post it.
	 * 
	 * @param user the user who saved the ideas
	 * 
	 * @return ArrayList<Idea> all the draft ideas saved by the user
	 */

	public static ArrayList<Idea> getDrafts(User user) {
		ArrayList<Idea> drafts = new ArrayList<Idea>();

		for (Idea idea : user.ideasCreated)
			if (idea.isDraft)
				drafts.add(idea);

		return drafts;
	}

}
