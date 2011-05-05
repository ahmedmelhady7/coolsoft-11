package controllers;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

import play.data.validation.Required;
import play.mvc.With;

import models.BannedUser;
import models.MainEntity;
import models.Organization;
import models.Role;
import models.Topic;
import models.User;
@With(Secure.class)
public class BannedUsers extends CRUD {
	@Before
	public static void restrictOrganizerrr(){
//		User u = (User) User.findAll().get(0);
//		Organization org = new Organization("coooolSoft", u);
//		MainEntity e = (MainEntity) MainEntity.findAll().get(0);
//		long id = e.getId();
//		System.out.println(id);
       List<Organization> o = Organization.findAll();
       Organization org = o.get(0);
		System.out.println(o .isEmpty());
		System.out.println("ya raaaaaaaaaaaab" + org.name + "  " );
		long organizationID = o.get(0).getId();
		System.out.println(organizationID);
		restrictOrganizer(organizationID);
	}
	/**
	 * this action method and it's helpers restrict the permissions of a certain
	 * Organizer in an organization , this restriction will be in the whole
	 * entity or in a specific topic , this restriction is done by the
	 * organization lead
	 * 
	 * this method specifically renders the list of organizers in an
	 * organization
	 * 
	 * @story C1S7
	 * @author Nada-Ossama
	 * 
	 * @param organizationID
	 *            the id of the organization
	 */
	public static void restrictOrganizer(long organizationID) {
		
		Organization org = Organization.findById(organizationID);
		List<User> users = Users.searchOrganizer(org);
//		List<User> u = User.findAll();
//		users.add(u.get(0));
		render(users, organizationID);
	}

	/**
	 * this method takes the Id of the organization (the same as the previous
	 * method) and the ID of the User to be restricted,and renders the list of
	 * entities the selected user is enrolled in and it will give the
	 * organization lead to restrict him from thr whole selected entity or from
	 * a specific topic
	 * 
	 * @story C1S7
	 * @author Nada Ossama
	 * 
	 * @param long userID, the id of the user to be restricted
	 * @param long organizationId the id of the organization he will be
	 *        restricted in
	 */
	public static void restrictOrganizerHelper1(@Required long userId,
			long organizationId) {
     
		if (validation.hasErrors()) {
			flash.error("Oops, please select one the Organizers");
			restrictOrganizer(organizationId);
		}
     System.out.println(organizationId);
		Organization org = Organization.findById(organizationId);
		User user = User.findById(userId);
		List<MainEntity> entities = Users.getEntitiesOfOrganizer(org, user);
		render(user, organizationId, entities);
	}

	/**
	 * this method will check if the organization lead has chosen to restrict
	 * the organizer from the whole selected entity or from a topic in it and
	 * accordingly it will render the list of actions related or it will
	 * redirect to other page to select the topic
	 * 
	 * @author Nada ossama
	 * @story C1S7
	 * @param entityId
	 *            : the selected entity id
	 * @param topic
	 *            : flag if it is equal to true then restrict from a certain
	 *            topic o.w.from the whole entity
	 * @param organizationId
	 *            :the id of the organization entered as before
	 * @param userId
	 *            : to be restricted
	 */

	public static void restrictOrganizerHelper2(@Required long entityId, @Required String topic,
			long organizationId, long userId) {

		if (validation.hasErrors()) {
			flash.error("Oops, please select one of the Entities ");
			restrictOrganizer(organizationId);
		}
		if (topic.equalsIgnoreCase("true")) {
			MainEntity e = MainEntity.findById(entityId);
			List<Topic> entityTopics = e.topicList;
			render(entityTopics, organizationId, userId);
		} else {
			restrictOrganizerHelper3(entityId, organizationId, userId);
		}

	}

	/**
	 * this method will be called if the organizer will be blocked from the
	 * whole entity so it will render the set of actions
	 * 
	 * @story C1S7
	 * @author Nada Ossama
	 * @param entityId
	 *            : to be restricted in
	 * @param organizationId
	 *            : as passed before
	 * @param userId
	 *            : to be restricted
	 */

	public static void restrictOrganizerHelper3(long entityId, long organizationId,
			long userId) {
		List<String> entityActions = Roles.getRoleActions("organizer");
		MainEntity e = MainEntity.findById(entityId);
		Organization o = e.organization;
		User u = User.findById(userId);

		List<String> restricted = BannedUser
				.find("select bu.action from BannedUser bu where bu.organization = ? and bu.bannedUser = ? and bu.resourceType like ? and resourceID = ? ",
						o, u, "entity", entityId).fetch();
		for(int i = 0; i<entityActions.size() ; i++){
			if (restricted.contains(entityActions.get(i))){
				entityActions.remove(i);
			}
		}
		render(entityActions, entityId, userId);
	}

	/**
	 * this method is used to render the list of actions the organizer is
	 * allowed to do in that topic
	 * @story :C1S7
	 * @author Nada Ossama
	 * 
	 * @param topicId
	 *            : to be restricted in
	 * @param userId
	 *            : to be restricted
	 */

	public static void restrictOrganizerHelper4(long topicId, long userId) {
		List<String> topicActions = Roles.getOrganizerTopicActions();
		Topic t = (Topic.findById(topicId));
		MainEntity e = t.entity;
		Organization o = e.organization;
		User u = User.findById(userId);

		List<String> restricted = BannedUser
				.find("select bu.action from BannedUser bu where bu.organization = ? and bu.bannedUser = ? and bu.resourceType like ? and resourceID = ? ",
						o, u, "topic", topicId).fetch();
		for(int i = 0; i<topicActions.size() ; i++){
			if (restricted.contains(topicActions.get(i))){
				topicActions.remove(i);
			}
		}

		render(topicActions, topicId, userId);
	}
	/**
	 * This method restricts the user from a certain action according to the slection
	 * @story C1S7
	 * @author Nada Ossama
	 * @param action: to be restricted from
	 * @param type : topic or entity
	 * @param entityTopicId : the id of the topic or entity
	 * @param userId : to be restricted
	 */

	public static void restrictOrganizerHelper5(String actionToDo, String type,
			long entityTopicId, long userId) {
      System.out.println(actionToDo);
		boolean changed = true;
		if (type.equalsIgnoreCase("topic")) {

			Topic topic = Topic.findById(entityTopicId);
			MainEntity entity = topic.entity;
			Organization org = entity.organization;
			long organizationId = org.getId();
			changed = BannedUser.banFromActionInTopic(userId, organizationId,
					actionToDo, entityTopicId);
		}

		else {
			MainEntity entity = MainEntity.findById(entityTopicId);
			Organization org = entity.organization;
			long organizationId = org.getId();
			changed = BannedUser.banFromActionInEntity(userId, organizationId,
					actionToDo, entityTopicId);
		}
		List restricted = new ArrayList<User>();
		restricted.add(User.findById(userId));
		
		
//		Notifications.sendNotification(restricted, Security.getConnected().getId(),
//				"user", "you have been restricted from the following action :" + action  +" " );

	}
}
