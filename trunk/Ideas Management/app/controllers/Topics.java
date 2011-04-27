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
	 * This Method returns true if the tag exists in the global list of tags and
	 * checks if it the topic is already tagged with the same tag (returns true
	 * also), false if the tag needs to be created.
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
	 * @param topicID
	 *            : the topic that is being tagged
	 * 
	 * @return boolean
	 */

	public static boolean tagTopic(int topicID, String tag, User user) {
		boolean tagAlreadyExists;
		ArrayList<Tag> listOfTags = (ArrayList) Tag.findAll();
		for (int i = 0; i < listOfTags.size(); i++) {
			if (listOfTags.get(i).getName().equalsIgnoreCase(tag)) {
				Topic topic = Topic.findById(topicID);

				if (!topic.tags.contains(listOfTags.get(i))) {
					topic.tags.add(listOfTags.get(i));
					// send notification to followers of the topic
					// send notification to topic organizers
					// send notification to organization lead
				} else {
					// error message
					tagAlreadyExists = true;
				}
				return true;

			}
		}
		return false;
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
	 */
	
	public static void postIdea(User user, Topic topic, String title,
			String description) {
		Idea idea = new Idea(title, description, user, topic);
		idea.privacyLevel = topic.privacyLevel;
	}
	
	/**
	 * 
	 * This method reopens a closed topic, used after its plan gets deleted
	 * 
	 * @author 	Mostafa Aboul Atta
	 * 
	 * @story 	C3S22
	 * 
	 * @param 	topicId 	: the id of the topic that to be reopened
	 * 
	 * @return	void
	 */
	public static void reopen(long topicId) {
		Topic targetTopic = Topic.findById(topicId);
		targetTopic.openToEdit = true;
		/* TODO: buttons to be adjusted in view */
	}
	
 
   /**
    * This Method returns a list of all closed topics
    *
    * @author aliaelbolock
    *
    * @story C3S1
    *
    * @return ArrayList<Topics>
    */
   public static ArrayList<Topic> closedTopics(){
        List closedtopics = (List) new ArrayList<Topic>();
        closedtopics = (List) Topic.find("openToEdit", false).fetch();
        return (ArrayList<Topic>) closedtopics;
   }

}
