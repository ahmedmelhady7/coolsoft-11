package controllers;

import java.util.ArrayList;
import java.util.List;
import models.Idea;
import models.MainEntity;
import models.Organization;
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
	 * @author Mostafa Aboul Atta
	 * 
	 * @story C3S22
	 * 
	 * @param topicId
	 *            : the id of the topic that to be reopened
	 * 
	 * @return void
	 */
	public static boolean reopen(long topicId, User actor) {
		
		Topic targetTopic = Topic.findById(topicId);
		
		if(!targetTopic.organizers.contains(actor)) {
			return false;
		}
	
		targetTopic.openToEdit = true;
		/* TODO: buttons to be adjusted in view */
		
		return true;
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
	public static ArrayList<Topic> closedTopics() {
		List closedtopics = (List) new ArrayList<Topic>();
		closedtopics = (List) Topic.find("openToEdit", false).fetch();
		return (ArrayList<Topic>) closedtopics;
	}

	
	/**
	 * 
	 * This method gets a list of followers for a certain topic
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S29
	 * 
	 * @param id	: id of the topic
	 * 
	 * @return void
	 */
	public static void viewFollowers(long id) {
		Topic topic = Topic.findById(id);
		notFoundIfNull(topic);
		List<User> follow = topic.followers;
		render(follow);
	}


	/**
	 * This Method sends a request to post on a topic for a user to the
	 * organizer
	 * 
	 * @author ibrahim.al.khayat
	 * 
	 * @story C2S13
	 * 
	 * @param topicId
	 *            : the id of the topic
	 * 
	 * @param user
	 *            : the user who request to post
	 * 
	 * @return void
	 */

	public static void requestToBost(long topicId, User user) {
		Topic t = Topic.findById(topicId);
		t.requestFromUserToPost(user);
	}

	/**
	 * This renders the RequestToPost.html to show the list of all topics where
	 * the user is not allowed to post within an organization
	 * 
	 * @author ibrahim.al.khayat
	 * 
	 * @story C2S13
	 * 
	 * @param org
	 *            : The organization where the topics are
	 * 
	 * @param user
	 *            : the user who wants to request
	 * 
	 * @return void
	 */

	public static void requestTopicList(User user, Organization org) {
		List<MainEntity> e = org.entitiesList;
		List<Topic> topics = new ArrayList<Topic>();
		List<Topic> temp;
		for (int i = 0; i < e.size(); i++) {
			temp = e.get(i).topicList;
			for (int j = 0; j < temp.size(); j++) {
				if (user.topicsIOrganize.indexOf(temp.get(j)) < 0) {
					topics.add(temp.get(j));
				}
			}
		}
		render(topics, user);
	}
	
	/**
	 * This method closes a topic, return true if was 
	 * successful, returns false if the there was ideas and doesn't 
	 * close the topic
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @story C3S4
	 * 
	 * @param topicId		: the id of the topic to be closed
	 * 
	 * @return boolean
	 */
	public static boolean closeTopic(long topicId, User actor) {
		Topic targetTopic = Topic.findById(topicId);
		
		//checks if topic is empty or the user is not an organizer
		if(targetTopic.ideas.size() == 0 ||
				!targetTopic.organizers.contains(actor)) {
			return false;
		}
		
		targetTopic.openToEdit = false;
		
		//TODO: edit buttons in view
		//TODO: send notifications to followers and organizers
		
		return true;
		
	}

}
