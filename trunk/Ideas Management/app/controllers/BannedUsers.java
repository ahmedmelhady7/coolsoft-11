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
       List<Organization> organizations = Organization.findAll();
     //  System.out.println(o.isEmpty() + "yaaaaaaaaaaaaaaaaaaaaaaaaaaaah");
       Organization organization = organizations.get(0);
		System.out.println(organizations .isEmpty());
	//	System.out.println("ya raaaaaaaaaaaab" + organization.name + "  " );
		long organizationID = organizations.get(0).getId();
	//	System.out.println(organizationID);
		restrictOrganizer(organizationID);
	}
	/**
	 *  restrict the permissions of a certain
	 * Organizer in an organization , this restriction will be in the whole
	 * entity or in a specific topic , this restriction is done by the
	 * organization lead
	 * 
	 * this method specifically renders the list of organizers in an
	 * organization
	 * 
	 * @author Nada-Ossama
	 * 
	 * @story C1S7
	 * 
	 * 
	 * @param orgID
	 *            long id of the organization
	 */
	public static void restrictOrganizer(long orgId) {
		
		Organization organization = Organization.findById(orgId);
	//	System.out.println((org == null) + "OFFFFFFFFFFFFFf");
		List<User> users = Users.searchOrganizer(organization);
//		List<User> u = User.findAll();
//		users.add(u.get(0));
	long	organizationID = orgId;
		render(users, organizationID);
	}

	/**
	 * takes the Id of the organization (the same as the previous
	 * method) and the ID of the User to be restricted,and renders the list of
	 * entities the selected user is enrolled in and it will give the
	 * organization lead to restrict him from the whole selected entity or from
	 * a specific topic
	 * 
	 * @story C1S7
	 * @author Nada Ossama
	 * 
	 * @param  userID
	 *              long id of the user to be restricted
	 * @param  organizationId 
	 *               long id of the organization he will be
	 *        restricted in
	 */
	public static void entitiesEnrolledIn(@Required long userId,
			long organizationId) {
    
		if (validation.hasErrors()|| userId == 0) {
			flash.error("Oops, please select one the Organizers");
			restrictOrganizer(organizationId);
		}
		else{
    // System.out.println(organizationId);
		Organization organization = Organization.findById(organizationId);
		User user = User.findById(userId);
		List<MainEntity> entities = Users.getEntitiesOfOrganizer(organization, user);
	//	System.out.println("++++" + entities.isEmpty());
		render(user, organizationId, entities);
		
		}
	}

	/**
	 * checks if the organization lead has chosen to restrict
	 * the organizer from the whole selected entity or from a topic in it and
	 * accordingly it will render the list of actions related or it will
	 * redirect to other page to select the topic
	 * 
	 * @author Nada ossama
	 * 
	 * @story C1S7
	 * @param entityId
	 *            : the selected entity id
	 * @param topic
	 *            : String flag if it is equal to true then restrict from a certain
	 *            topic o.w.from the whole entity
	 * @param organizationId
	 *            :long id of the organization entered as before
	 * @param userId
	 *            : long userId to be restricted
	 */

	public static void topicsEnrolledInOrRedirect(@Required long entityId, @Required String topic,
			long organizationId, long userId) {

		if (validation.hasErrors() || entityId == 0) {
			flash.error("Oops, please select atleast one choise ");
			entitiesEnrolledIn(userId,organizationId);
		}
		if (topic.equalsIgnoreCase("true")) {
			MainEntity entity = MainEntity.findById(entityId);
			List<Topic> entityTopics = entity.topicList;
			render(entityTopics, organizationId, userId);
		} else {
			entityActions(entityId, organizationId, userId);
		}

	}

	/**
	 *  block the organizer from the whole entity so the method will render the set of actions
	 * 
	 * 
	 * @author Nada Ossama
	 * @story C1S7
	 * 
	 * @param entityId
	 *            : long entityId to be restricted in
	 * @param organizationId
	 *            : long Id of the organization passed before
	 * @param userId
	 *            : long userId to be restricted
	 */

	public static void entityActions(long entityId, long organizationId,
			long userId) {
		
		
		List<String> entityActions = Roles.getRoleActions("organizer");
		MainEntity entity = MainEntity.findById(entityId);
		Organization organization = entity.organization;
		User user = User.findById(userId);
		
		

		List<String> restricted = BannedUser
				.find("select bu.action from BannedUser bu where bu.organization = ? and bu.bannedUser = ? and bu.resourceType like ? and resourceID = ? ",
						organization, user, "entity", entityId).fetch();
		for(int i = 0; i<entityActions.size() ; i++){
			if (restricted.contains(entityActions.get(i))){
				System.out.println(entityActions.get(i));
				entityActions.remove(i);
			}
		}
		render(entityActions, entityId, userId);
	}

	/**
	 * render the list of actions the organizer is
	 * allowed to do in that topic
	 * 
	 * @author Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * 
	 * @param topicId
	 *            : long Id of the topic to be restricted in
	 * @param userId
	 *            :long Id of the user  to be restricted
	 */

	public static void topicActions(@Required long topicId, long userId) {
		
		
		
		List<String> topicActions = Roles.getOrganizerTopicActions();
		Topic topic = (Topic.findById(topicId));
		MainEntity entity = topic.entity;
		Organization organization = entity.organization;
		User user = User.findById(userId);
		
		
		if (validation.hasErrors() || topicId == 0) {
			flash.error("Oops, please select atleast one choise ");
			topicsEnrolledInOrRedirect(entity.getId(), "topic",
					organization.getId(), user.getId());
		}

		List<String> restricted = BannedUser
				.find("select bu.action from BannedUser bu where bu.organization = ? and bu.bannedUser = ? and bu.resourceType like ? and resourceID = ? ",
						organization, user, "topic", topicId).fetch();
		for(int i = 0; i<topicActions.size() ; i++){
			if (restricted.contains(topicActions.get(i))){
				topicActions.remove(i);
			}
		}

		render(topicActions, topicId, userId);
	}
	/**
	 * restricts the user from a certain action according to the selection
	 * @story C1S7
	 * @author Nada Ossama
	 * @param action:
	 *               String to be restricted from
	 * @param type : 
	 *              String topic or entity
	 * @param entityTopicId : 
	 *                 long  id of the topic or entity
	 * @param userId : long Id of the user to be restricted
	 */

	public static void restrictFinal(@Required String actionToDo, String type,
			long entityTopicId, long userId) {
      System.out.println(actionToDo);
		boolean changed = true;
		if (type.equalsIgnoreCase("topic")) {

			Topic topic = Topic.findById(entityTopicId);
			MainEntity entity = topic.entity;
			Organization org = entity.organization;
			long organizationId = org.getId();
			
			if (validation.hasErrors() || actionToDo == null) {
				flash.error("Oops, please select atleast one choise ");
				topicActions(entityTopicId, userId);
			}
			
			
			
			changed = BannedUser.banFromActionInTopic(userId, organizationId,
					actionToDo, entityTopicId);
			
			Notifications.sendNotification(userId, Security.getConnected().getId(),
					"user", "you have been restricted from the following action :" + actionToDo  +" In organization  : " + org +" In Entity :" + entity + " In Topic :" + topic );

		}

		else {
			MainEntity entity = MainEntity.findById(entityTopicId);
			Organization org = entity.organization;
			long organizationId = org.getId();
			
			
			if (validation.hasErrors()|| actionToDo == null) {
				flash.error("Oops, please select atleast one choise ");
				entityActions(entityTopicId,organizationId, userId);
			}
			
			changed = BannedUser.banFromActionInEntity(userId, organizationId,
					actionToDo, entityTopicId);
			
			Notifications.sendNotification(userId, Security.getConnected().getId(),
					"user", "you have been restricted from the following action :" + actionToDo  +" In organization  : " + org +" In Entity :" + entity );

		}
		
		
		
		
	}
}
