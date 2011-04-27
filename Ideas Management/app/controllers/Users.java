package controllers;


import java.util.List;


import java.util.List;

import play.data.validation.Validation;

import models.MainEntity;
import models.Organization;
import models.Tag;
import models.Topic;
import models.User;


import models.*;

public class Users extends CRUD {
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
		user.unfollow(topic);
	}

	/**
	 * This Method removes a user from the list of followers in a given Tag
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param tag
	 *            : the tag that the user is following
	 * 
	 * @param user
	 *            : the user who follows
	 * 
	 * @return void
	 */
	public static void unfollow(Tag tag, User user) {
		tag.unfollow(user);
		user.unfollow(tag);
	}

	/**
	 * This Method removes a user from the list of followers in an organization
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param org
	 *            : the organization the user is following
	 * 
	 * @param user
	 *            : the user who follows
	 * 
	 * @return void
	 */

	public static void unfollow(Organization org, User user) {
		org.unfollow(user);
		user.unfollow(org);
	}

	/**
	 * This Method removes a user from the list of followers in an entity
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param entity
	 *            : the entity the user is following
	 * 
	 * @param user
	 *            : the user who follows
	 * 
	 * @return void
	 */

	public static void unfollow(MainEntity entity, User user) {
		entity.unfollow(user);
		user.unfollow(entity);
	}

	/**
	 * This Method renders a page of all objects followed by a user
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param user
	 *            : the user who follows
	 * 
	 * @return void
	 */

	public static void listFollows(User user) {

		try {
			List<Organization> organizations = user.followingOrganizations;
			List<Tag> tags = user.followingTags;
			List<MainEntity> entities = user.followingEntities;
			List<Topic> topics = user.topicsIFollow;
			render(organizations, tags, entities, topics, user);
		} catch (Exception e) {
		}
	}

	public static void reportIdeaAsSpam(Idea idea, User reporter) {
		idea.spamCounter++;
		// idea.belongsToTopic.getOrganizer();
	}

	/**
	 * 
	 * This method is responsible for posting a topic in a certain entity by a
	 * certain user
	 * 
	 * @author ${aliaelbolock}
	 * 
	 * @story C3S1
	 * 
	 * @param name
	 *            : the title of the topic to be posted/posted
	 * 
	 * @param description
	 *            : the description of the topic to be added/posted
	 * 
	 * @param privacyLevel
	 *            : the privacy level of the topic
	 * 
	 * @param creator
	 *            : the user who added/posted the topic
	 * 
	 * @param entity
	 *            : the entity in which the topic will be added/posted
	 * 
	 * @return void
	 */
	public void postTopic(String name, String description, short privacyLevel,
			User creator, MainEntity entity) {
		if (entity.organizers.contains(creator)) {
			Topic newTopic = new Topic(name, description, privacyLevel,
					creator, entity).save();
			creator.topicsCreated.add(newTopic);
			creator.topicsIOrganize.add(newTopic);
			for (int i = 0; i < entity.organizers.size(); i++) {
				entity.organizers.get(i).topicsIOrganize.add(newTopic);
			}
		}
	}

	/**
	 * 
	 * This method is responsible for searching for users using specific criteria
	 * 
	 * @author ${lama.ashraf}
	 * 
	 * @story C1S13
	 * 
	 * @param username
	 *            : the username of the user
	 * 
	 * @param profession
	 * 
	 * @param email
	 * 
	 * @param id
	 *            : the id of the topic
	 * 
	 * @return void
	 */
	public static void searchUser(String username, String profession,
			String email, Topic id) {

		List<User> searchResult = User.findAll();

		if (username != null) {
			searchResult = User.find("username like ? ", "name%").fetch();
		}

		if (profession != null) {
			for (int i = 0; i < searchResult.size(); i++) {
				if (!(searchResult.get(i).profession).equals(profession)) {
					searchResult.remove(i);
				}

			}
		}
		if (email != null) {
			for (int i = 0; i < searchResult.size(); i++) {
				if (!(searchResult.get(i).email).equals(email)) {
					searchResult.remove(i);
				}

			}

		}
		
		render(searchResult);
	}
	
	public static List<User> searchOrganizer(Organization o){
		List<User> organizers = null;
		if(o != null) {
		organizers = (List<User>) UserRoleInOrganization.find("select uro.enrolled from UserRoleInOrganization uro where uro.organization = o and uro.Role.name like ? ", "organizer");
		
		}
		return organizers;
	}
	/*public List<User> searchByTopic(Topic id) {	
		
		
	}*/


	public static void r(long i) {
		Topic t = Topic.findById(i);
		t.title = "done";
		t._save();
	}
}

