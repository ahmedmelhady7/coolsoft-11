package controllers;

import java.util.ArrayList;

import antlr.collections.List;
import models.Idea;
import models.MainEntity;
import models.Tag;
import models.Topic;
import models.User;

public class Topics extends CRUD {

	/**
	 * This Method removes a user from the list of followers in a given Topic
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param topic
	 *            : the topic that the user is following
	 * 
	 * @param user
	 *            : the user who follows
	 * 
	 * @return void
	 */
	public static void unfollow(Topic topic, User user) {
		topic.unfollow(user);
	}

	/**
	 * This Method returns true if the tag has been successfully added to the
	 * topic, false otherwise
	 * 
	 * @author Mostafayasser.1991
	 * 
	 * @story C3S2
	 * 
	 * @param tag
	 *            : the tag that is being added
	 * 
	 * @param user
	 *            : the user who is tagging the topic
	 * 
	 * @return boolean
	 */
	// public static boolean addTag(Tag tag, User user){
	//
	// }

	public static void tagTopic(int topicID, String tag, User user) {
		ArrayList<Tag> listOfTags = (ArrayList) Tag.findAll();
		for (int i = 0; i < listOfTags.size(); i++) {
			if (listOfTags.get(i).getName() == tag) {
				Topic topic = Topic.findById(topicID);
				topic.tags.add(listOfTags.get(i));
			}
		}
	}

	/**
	 * 
	 * This method is responsible for posting an idea to a topic by a certain
	 * user
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S10
	 * 
	 * @param user
	 *            : the user who posted the idea
	 * 
	 * @param topic
	 *            : the topic which the idea belongs/added to
	 * 
	 * @param title
	 *            : the title of the idea
	 * 
	 * @param description
	 *            : the description of the idea
	 * 
	 * @param privacyLevel
	 *            : the level of privacy of the idea
	 * 
	 * @return void
	 */
	public static void postIdea(User user, Topic topic, String title,
			String description, int privacyLevel) {
		Idea i = new Idea(title, description, user, topic);
		i.privacyLevel = privacyLevel;
	}
}
