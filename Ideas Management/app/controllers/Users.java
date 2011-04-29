package controllers;

// list of organizers in entity 

import java.util.ArrayList;
import java.util.List;

import java.util.List;

import play.data.validation.Validation;
import play.db.jpa.GenericModel.JPAQuery;

import models.MainEntity;
import models.Notification;
import models.NotificationProfile;
import models.Organization;
import models.Tag;
import models.Topic;
import models.User;
import models.UserRoleInOrganization;

import models.*;

public class Users extends CRUD {

	/**
	 * This Method adds a user to the list of followers in a given tag
	 * 
	 * @author m.hisham.sa
	 * 
	 * @story C2S11
	 * 
	 * @param tag
	 *            : the tag that the user is following
	 * 
	 * @param user
	 *            : the user who follows
	 * 
	 * @return void
	 */
	public static void follow(Tag tag, User user) {
		tag.follow(user);
		user.follow(tag);
	}

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

	/**
	 * this Method is responsible for reporting an idea as a spam
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @param idea
	 *            : the idea to be reported
	 * 
	 * @param reporter
	 *            : the user who wants to report the idea
	 * 
	 */
	public static void reportIdeaAsSpam(Idea idea, User reporter) {
		boolean alreadyReported = false;
		for (int i = 0; i < reporter.ideasReported.size(); i++) {
			if (idea == reporter.ideasReported) {
				alreadyReported = true;
			}
		}
		if (!alreadyReported) {
			idea.spamCounter++;
			reporter.ideasReported.add(idea);
			// idea.belongsToTopic.getOrganizer();
		}
	}
	/**
	 * 
	 * this method checks if the user is allowed to post an idea under a certain topic, 
	 * if yes then an idea is posted under the topic
	 * 
	 * @author m.hisham.sa
	 * 
	 * @story C2S14
	 * 
	 * @param user
	 *            : the user trying to post the idea
	 * 
	 * @param topic
	 *            : the topic that the idea is being posted under
	 * @param title
	 *            : the title of the idea
	 * 
	 * @param description
	 *            : the description/content of the idea
	 * 
	 * 
	 * @return void
	 */

	public void postTopic(String name, String description, short privacyLevel,
			User creator, MainEntity entity) {

		if (entity.organizers.contains(creator)) {
			Topic newTopic = new Topic(name, description, privacyLevel,
					creator, entity).save();
			creator.topicsCreated.add(newTopic);

			// List usr = getEntityOrganizers(entity);
			// UserRoleInOrganization.addEnrolledUser(creator,
			// newTopic.entity.organization,
			// Roles.getRoleByName("Organizer"), newTopic.getId(), "topic");
			List usr = getEntityOrganizers(entity);
			// UserRoleInOrganization.addEnrolledUser(creator,
			// newTopic.entity.organization,
			// Role.getRoleByName("Organizer"), newTopic.getId(), "topic");
			creator.topicsIOrganize.add(newTopic);
			for (int i = 0; i < entity.organizers.size(); i++) {
				entity.organizers.get(i).topicsIOrganize.add(newTopic);
			}
		}
	}
	
	public void postIdea(User user, Topic topic, String title, String description){
		if(isPermitted(user, "post", topic.id, "topic")){
				user.postIdea(topic, title, description);
				topic.postIdea(user, title, description);
		}else
			return;

	}
	

	/**
	 * 
	 * This method is responsible for searching for users using specific
	 * criteria
	 * 
	 * @author ${lama.ashraf}
	 * 
	 * @story C1S13
	 * 
	 * @param keyword
	 *            : the keyword the user enters for searching
	 * 
	 * @return void
	 */
	public static void searchUser(String keyword) {

		List<User> searchResultByName = new ArrayList<User>();
		List<User> searchResultByProfession = new ArrayList<User>();
		List<User> searchResultByEmail = new ArrayList<User>();

		if (keyword != null) {
			searchResultByName = User.find("username like ? ", "keyword%")
					.fetch();
			searchResultByProfession = User.find("profession like ? ",
					"keyword").fetch();
			searchResultByEmail = User.find("email like ? ", "keyword").fetch();
		}

		for (int i = 0; i < searchResultByName.size(); i++) {
			if (searchResultByName.get(i).state == 'd') {
				searchResultByName.remove(i);
			}
		}
		for (int i = 0; i < searchResultByProfession.size(); i++) {
			if (searchResultByProfession.get(i).state == 'd') {
				searchResultByProfession.remove(i);
			}
		}

		for (int i = 0; i < searchResultByEmail.size(); i++) {
			if (searchResultByEmail.get(i).state == 'd') {
				searchResultByEmail.remove(i);
			}
		}

		render(searchResultByName, searchResultByProfession,
				searchResultByEmail);
	}

	/**
	 * 
	 * This method is responsible for searching for organizers in a certain
	 * organization
	 * 
	 * @author ${lama.ashraf}
	 * 
	 * @story C1S13
	 * 
	 * @param o
	 *            : the organization
	 * 
	 * @return List<User>
	 */
	public static List<User> searchOrganizer(Organization o) {
		List<UserRoleInOrganization> organizers = null;
		if (o != null) {
			organizers = (List<UserRoleInOrganization>) UserRoleInOrganization
					.find("select uro.enrolled from UserRoleInOrganization uro,Role r where  uro.Role = r and uro.organization = ? and r.roleName like ? ",
							o, "organizer");

		}
		List<User> finalOrganizers = new ArrayList<User>();
		for (int i = 0; i < organizers.size(); i++) {
			finalOrganizers.add((organizers.get(i)).enrolled);
		}
		return finalOrganizers;
	}

	/*
	 * public List<User> searchByTopic(Topic id) {
	 * 
	 * 
	 * }
	 */

	/**
	 * 
	 * This method is responsible for telling whether a user is allowed to do a
	 * specific action in an organization/entity/topic/plan
	 * 
	 * @author ${lama.ashraf}
	 * 
	 * @story C1S15
	 * 
	 * @param user
	 *            : the user who is going to perfom the action
	 * 
	 * @param action
	 *            :the action performed
	 * 
	 * @param placeId
	 *            : the id of the organization/ entity/ topic/ plan
	 * 
	 * @param placeType
	 *            : the type whether an organization/ entity/ topic/ plan
	 * 
	 * @return boolean
	 */
	public static boolean isPermitted(User user, String action, long placeId,
			String placeType) {
		// User banned =
		// BannedUser.find("select b.bannedUser from BannedUser b where b.bannedUser = ? and b.resourceID = ? and b.resourceType = ? and b.action =  ",
		// user);
		JPAQuery banned = BannedUser.find(
				"byBannedUserAndActionAndResourceTypeAndResourceID", user,
				action, placeType, placeId).first();

		if (user.isAdmin) {
			return true;
		} else {
			if (banned != null) {
				return false;
			} else {
				if (placeType == "organization") {
					Organization org = Organization.findById(placeId);
					if (org.privacyLevel == 0 || org.privacyLevel == 1) {
						List<UserRoleInOrganization> allowed = UserRoleInOrganization
								.find("byEnrolledAndbyOrganization", user, org)
								.fetch();
						if (allowed == null) {
							return false;
						} else {
							return true;
						}
					} else {
						return true;
					}

				} else {
					if (placeType == "topic") {
						Topic topic = Topic.findById(placeId);
						if (topic.privacyLevel == 0 || topic.privacyLevel == 1) {
							List<UserRoleInOrganization> allowed = UserRoleInOrganization
									.find("byEnrolledAndbyEntityTopicIDAndType",
											user, topic, "topic").fetch();
							if (allowed == null) {
								return false;
							} else {
								return true;
							}
						} else {
							return true;
						}
					} else {
						if (placeType == "plan") {
							Plan plan = Plan.findById(placeId);
							Topic topic = plan.topic;
							if (topic.privacyLevel == 0
									|| topic.privacyLevel == 1) {
								List<UserRoleInOrganization> allowed = UserRoleInOrganization
										.find("byEnrolledAndbyEntityTopicIDAndType",
												user, topic, "topic").fetch();
								if (allowed == null) {
									return false;
								} else {
									return true;
								}
							} else {
								return true;
							}
						}
					}

				}
			}
		}
		System.out.println("you entered an invalid type");
		return false;

	}

	public static void r(long i) {
		Topic t = Topic.findById(i);
		t.title = "done";
		t._save();
	}

	/**
	 * gets the list of organizers of a certain topic
	 * 
	 * @author: Nada Ossama
	 * 
	 * @param e
	 *            : is the topic that i want to retrieve all the organizers that
	 *            are enrolled in it
	 * 
	 * @return List of Organizers in that topic
	 */
	public List<User> getTopicOrganizers(Topic t) {

		List<User> organizers = null;
		if (t != null) {
			Organization o = t.entity.organization;
			organizers = (List<User>) UserRoleInOrganization
					.find("select uro.enrolled from UserRoleInOrganization uro and Role r "
							+ "where uro.organization = ? and"
							+ " uro.Role = r and r.roleName like ? and uro.entityTopicID = ? "
							+ "and uro.type like ?", o, "organizer", t.getId(),
							"topic");

		}
		return organizers;
	}

	/**
	 * gets the list of organizers of a certain entity
	 * 
	 * @author: Nada Ossama
	 * 
	 * @param e
	 *            : is the entity that i want to retrieve all the organizers
	 *            that are enrolled in it
	 * 
	 * @return List of Organizers in that entity
	 */
	public static List getEntityOrganizers(MainEntity e) {

		List<User> organizers = null;
		if (e != null) {
			Organization o = e.organization;
			organizers = (List<User>) UserRoleInOrganization
					.find("select uro.enrolled from UserRoleInOrganization uro and Role r "
							+ "where uro.organization = ? and"
							+ " uro.Role = r and r.roleName like ? and uro.entityTopicID = ? "
							+ "and uro.type like ?", o, "organizer", e.getId(),
							"entity");

		}
		return organizers;
	}

	/**
	 * gets all the users enrolled in an organization these users are: 1- the
	 * Organization lead 2- the organizers (even if blocked) 3- Idea Developers
	 * in secret or private topics
	 * 
	 * @author : Nada Ossama
	 * 
	 * @param: the organization that the users are enrolled in
	 * 
	 * @return :List of enrolled users
	 */

	public static List<User> getEnrolledUsers(Organization o) {
		List<User> enrolled = null;
		if (o != null) {

			enrolled = (List<User>) UserRoleInOrganization.find(
					"select uro.enrolled from UserRoleInOrganization uro "
							+ "where uro.organization = ? ", o);

		}
		return enrolled;
	}
	
	
	
	
	
	
	
    /**
     * 
     * This method checks whether a user is an organizer to a specifi entity or not
     * 
     * @author  {Mai Magdy}
     * 
     * @param entity
     *               entity that we want to check if the user is enrolled 
     *               in as organizer
     * 
     * @return  boolean
     */
	
	public static boolean isOrganizer(MainEntity entity,User user){
		
		List <User> organizers= Users.getEntityOrganizers(entity);
		  
		  boolean flag=false;
		  
		  for(int j=0;j<organizers.size();j++){
			  if(organizers.get(j).equals(user))
				  flag=true;
		  }
		  return flag;
	}
	
	
	   /**
  * 
  * This method returns list of entities within a specific organization
  *  that a user is enrolled in as organizer
  * 
  * @author  {Mai Magdy}
  * 
  * @param org
  *              the organization that we want to search within its entities 
  *               
  * 
  * @return  list<MainEntity>
  */
	
	
	public static List<MainEntity> getOrganizerEntities(Organization org,User user){
		    
		     List<MainEntity> list=new ArrayList <MainEntity>();
		     
		     for(int i=0;i<org.entitiesList.size();i++){
		     if(isOrganizer(org.entitiesList.get(i),user)&&
		    		 isPermitted(user,"invite",org.entitiesList.get(i).id,"entity"))
		    		 list.add(org.entitiesList.get(i));
		    	 
		     }
		
		  return list;
	}
	
	
	
	 /**
  * 
  * This method returns list of organizations that a user is enrolled in as organizer
  * 
  * @author  {Mai Magdy}
  * 
  * @param user
  *               
  * 
  * @return  list<Organization>
  */
	
	public static List<Organization> getOrganizerOrganization(User user){
	    
	     List<Organization> list= new ArrayList<Organization>();
	     List<Organization> org=Organization.findAll();
	    
	     for(int i=0;i<org.size();i++){
	    	 List<User> organizer=new ArrayList<User>();
	    	   organizer=searchOrganizer(org.get(i));
	    	   
	    	 for(int j=0;j<organizer.size();j++){
	    	   if( organizer.get(j).equals(user)&&
	           isPermitted(user,"invite",org.get(i).id,"organization"))
	    		   
		    		 list.add(org.get(i));
	    	  }
	     }
	     
	
	  return list;
}
	
	

	
	
	/**
	 * This method renders the list of notifications of the user, to the view to display
	 * the notifications.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @param userId
	 * 			the ID of the user required to get his notifications
	 * 
	 * @return void
	 */
	
	 public static void viewNotifications(long userId) {
	    	User u = User.findById(userId);	    
	    	List<Notification> nList = u.openNotifications();	    	
			render(nList);
	    }
	
	/**
	 * This method renders the list of notification profiles for the user to view
	 * and edit his preferences.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @param userId
	 * 			the ID of the user to view his/her profile
	 * 
	 * @return void
	 */
	
	public static void viewNotificationProfile(long userId) {
    	User u = User.findById(userId);    	
    	List<NotificationProfile> npList = u.openNotificationProfile();
    	render(npList);
    }
}
