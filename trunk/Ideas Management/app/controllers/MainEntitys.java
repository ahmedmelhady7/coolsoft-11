package controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.chainsaw.Main;

import controllers.CoolCRUD.ObjectType;

import play.data.validation.Required;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import models.CreateRelationshipRequest;
import models.EntityRelationship;
import models.Invitation;
import models.Item;
import models.Log;
import models.MainEntity;
import models.Organization;
import models.Plan;
import models.RenameEndRelationshipRequest;
import models.RequestToJoin;
import models.Tag;
import models.Topic;
import models.User;

@With(Secure.class)
public class MainEntitys extends CoolCRUD {

	/**
	 * renders the related entity and the list of other entities in the
	 * organization to the view
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S5
	 * 
	 * @param entityId
	 *            : id of the entity to be related
	 * 
	 * @param organizationId
	 *            : id of the organization the entity belongs to
	 */
	public static void createRelation(long entityId, long organizationId) {

		MainEntity entity = MainEntity.findById(entityId);
		notFoundIfNull(entity);
		Organization organization = Organization.findById(organizationId);
		List<MainEntity> entityList = new ArrayList<MainEntity>();
		notFoundIfNull(organization);

		if (organization.entitiesList != null) {
			entityList = organization.entitiesList;
			entityList.remove(entity);
		}

		for (int i = 0; i < entityList.size(); i++) {
			if (!entityList.get(i).createRelationship)
				entityList.remove(entityList.get(i));
		}
		render(entity, entityList);
	}

	/**
	 * The method that allows a user to follow a certain entity and also checks
	 * first that the user is permitted to do so.
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S10
	 * 
	 * @param entityId
	 *            : The id of the entity that the user wants to follow
	 * 
	 */

	public static void followEntity(long entityId) {
		User user = Security.getConnected();
		MainEntity entity = MainEntity.findById(entityId);
		if (!entity.followers.contains(user)) {
			entity.followers.add(user);
			entity.save();
			user.followingEntities.add(entity);
			user.save();
			Log.addUserLog(
					"<a href=\"/Users/viewProfile?userId="
							+ user.id
							+ "\">"
							+ user.username
							+ "</a>"
							+ " has followed the entity "
							+ "<a href=\"/MainEntitys/viewEntity?mainentityId="
							+ entity.id 
							+ "\">" 
							+ entity.name 
							+ "</a>",
					entity.organization);
			redirect(request.controller + ".viewEntity", entity.id,
					"You are now a follower");
		}
	}

	/**
	 * The method renders the page for viewing the followers
	 * 
	 * @author Noha Khater
	 * 
	 * @story C2S10
	 * 
	 * @param entityId
	 *            : The id of the entity whose followers will be viewed
	 * 
	 * @param flag
	 *            : A string that used to represent a flag
	 * 
	 */
	public static void viewFollowers(long entityId, String flag) {
		MainEntity entity = MainEntity.findById(entityId);
		notFoundIfNull(entity);
		User user = Security.getConnected();
		
		Organization org = Organization.findById(entity.organization.id);
		List<MainEntity> entities = org.entitiesList;
		List<Topic> topicList = entity.topicList;
		List<MainEntity> subentities = entity.subentities;
		List<MainEntity> entitiesICanView = new ArrayList<MainEntity>();
		for (MainEntity entityTemp : entities) {
			if (Users.isPermitted(user, "view", entityTemp.id, "entity")) {
				entitiesICanView.add(entityTemp);
			}
		}
		List<User> followers = org.followers;
		List<Plan> plans = Plans.planList("organization", org.id);
		int canCreateEntity = 0;
		int canCreateSubEntity = 0;
		int canDeleteEntity = 0;
		if (user.isAdmin
				|| Users.isPermitted(user, "create entities",
						entity.organization.id, "organization")) {
			canCreateEntity = 1;
			canDeleteEntity = 1;
		}
		if (user.isAdmin
				|| Users.isPermitted(user, "create entities",
						entity.organization.id, "organization")
				|| Users.isPermitted(user,
						"Create a sub-entity for entity he/she manages",
						entity.id, "entity")) {
			canCreateSubEntity = 1;
		}
		int permission = 1;
		int invite = 0;
		int canEdit = 0;
		int canRequest = 0;
		int canRequestRelationship = 0;
		int canRestrict = 0;
		boolean entityIsLocked = entity.createRelationship;
		List<User> allowed = Users.getEntityOrganizers(entity);
		if (org.creator.equals(user) || user.isAdmin) {
			canRestrict = 1;
		}
		if (org.creator.equals(user) || allowed.contains(user) || user.isAdmin)
			canEdit = 1;
		if (!Users.isPermitted(user, "post topics", entity.id, "entity"))
			permission = 0;
		if (Users.isPermitted(user, "use", entity.id, "entity"))
			canRequest = 1;
		if (Users
				.isPermitted(
						user,
						"invite Organizer or Idea Developer to become Organizer or Idea Developer in an entity he/she manages",
						entity.id, "entity"))
			invite = 1;
		if (UserRoleInOrganizations.isOrganizer(user, entity.id, "entity")) {
			canRequestRelationship = 1;
		}
		int check = 0;
		if (Users.isPermitted(user,
				"block a user from viewing or using a certain entity",
				entity.id, "entity"))
			check = 1;
		
		boolean follower = user.followingEntities.contains(entity);
		boolean canCreateRelationship = EntityRelationships
				.isAllowedTo(entityId);
		render(user, org, entity, subentities, topicList, permission, invite,
				canEdit, canCreateEntity, canCreateSubEntity, follower,
				canCreateRelationship, canRequest, canRequestRelationship,
				canRestrict, entityIsLocked, plans, canDeleteEntity, followers,
				check);
		if (flag.equals("true"))
			followEntity(entityId);
		render(user, org, entities, plans, entitiesICanView, followers, entity,
				subentities);
	}

	/**
	 * This method allows the user to create an entity in an organization
	 * 
	 * @author Noha Khater, Omar Faruki
	 * 
	 * @Stroy C2S2
	 * 
	 * @param name
	 *            : The name of the entity to be created
	 * 
	 * @param description
	 *            : The description of the entity to be created
	 * 
	 * @param orgId
	 *            : The id of the organization in which the entity will be
	 *            created
	 * 
	 * @param createRelationship
	 *            specifies whether an entity can have relationships with others
	 */
	public static void createEntity(String name, String description,
			long orgId, boolean createRelationship) {
		boolean canCreate = true;
		User user = Security.getConnected();
		Organization organisation = Organization.findById(orgId);
		for (int i = 0; i < organisation.entitiesList.size(); i++) {
			if (organisation.entitiesList.get(i).name.equalsIgnoreCase(name))
				canCreate = false;
		}
		if (canCreate) {
			MainEntity entity = new MainEntity(name, description, organisation,
					createRelationship);
			entity.save();
			Log.addUserLog(
					"<a href=\"/Users/viewProfile?userId="
							+ user.id
							+ "\">"
							+ user.username
							+ "</a>"
							+ " has created the entity "
							+ "<a href=\"/MainEntitys/viewEntity?mainentityId="
							+ entity.id + "\">" + entity.name + "</a>",
					organisation);
		}
		redirect("Organizations.viewProfile", organisation.id, "Entity created");
	}

	/**
	 * This method allows the user to create a subentity in an entity
	 * 
	 * @author Noha Khater, Omar Faruki
	 * 
	 * @Stroy C2S20
	 * 
	 * @param name
	 *            : The name of the entity to be created
	 * 
	 * @param description
	 *            : The description of the entity to be created
	 * 
	 * @param parentId
	 *            : The id of the parent entity
	 * 
	 * @param orgId
	 *            : The id of the organization in which the entity will be
	 *            created
	 * 
	 * @param createRelationship
	 *            specifies whether an entity can have relationships with others
	 */
	public static void createSubEntity(String name, String description,
			long parentId, long orgId, boolean createRelationship) {
		System.out.println("create subentity");
		boolean canCreate = true;
		MainEntity parent = MainEntity.findById(parentId);
		System.out.println("parent " + parent.name);
		Organization organization = Organization.findById(orgId);
		User user = Security.getConnected();
		for (int i = 0; i < parent.subentities.size(); i++) {
			if (parent.subentities.get(i).name.equalsIgnoreCase(name))
				canCreate = false;
		}
		if (canCreate) {
			MainEntity entity = new MainEntity(name, description, parent,
					organization, createRelationship);
			entity.save();
			System.out.println("will create now");
			Log.addUserLog(
					"<a href=\"/Users/viewProfile?userId="
							+ user.id
							+ "\">"
							+ user.username
							+ "</a>"
							+ " has created the subentity "
							+ "<a href=\"/MainEntitys/viewEntity?mainentityId="
							+ entity.id
							+ "\">"
							+ entity.name
							+ "</a>"
							+ " for the entity "
							+ "<a href=\"/MainEntitys/viewEntity?mainentityId="
							+ parent.id + "\">" + parent.name + "</a>",
					organization);
			List<User> receivers = Users.getEntityOrganizers(parent);
			int size = receivers.size();
			for (int j = 0; j < size; j++) {
				Users.getEntityOrganizers(entity).add(receivers.get(j));
			}
			receivers.add(organization.creator);
			for (int i = 0; i < size; i++) {
				Notifications.sendNotification(receivers.get(i).id, parent.id,
						"entity", "A new subentity (" + name
								+ ") has been created for the entity ("
								+ parent.name + ")");
			}
			size = organization.followers.size();
			for (int i = 0; i < size; i++) {
				Notifications.sendNotification(organization.followers.get(i).id, organization.id,
						"organization", "A new entity (" + name
								+ ") has been created in " + organization.name);
			}
		
		}
		redirect("Organizations.viewProfile", organization.id,
				"SubEntity created");
	}

	/**
	 * This method that renders the page for creating an entity
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S2
	 * 
	 * @param orgId
	 *            : The id of the organization in which the entity will be
	 *            created
	 * 
	 */
	public static void goToCreateEntity(long orgid) {
		User user = Security.getConnected();
		Organization org = Organization.findById(orgid);
		List<MainEntity> entities = org.entitiesList;
		List<MainEntity> entitiesICanView = new ArrayList<MainEntity>();
		for (MainEntity entity : entities) {
			if (Users.isPermitted(user, "view", entity.id, "entity")) {
				entitiesICanView.add(entity);
			}
		}
		int allowed = 0;
		int settings = 0;
		if (org.privacyLevel == 1
				&& Users.isPermitted(
						user,
						"accept/reject join requests from users to join a private organization",
						org.id, "organization"))
			allowed = 1;
		if (Users
				.isPermitted(
						user,
						"enable/disable the user to create their own tags within an organization",
						org.id, "organization"))
			settings = 1;
		List<MainEntity> entitiesCanBeRelated = new ArrayList<MainEntity>();
		for (int x = 0; x < entities.size(); x++) {
			entitiesCanBeRelated.add(entities.get(x));
		}
		List<Topic> topics = new ArrayList<Topic>();
		for (int x = 0; x < entities.size(); x++) {
			for (int y = 0; y < entities.get(x).topicList.size(); y++) {
				topics.add(entities.get(x).topicList.get(y));
			}
		}
		for (int x = 0; x < entitiesCanBeRelated.size(); x++) {
			if (!entitiesCanBeRelated.get(x).createRelationship)
				entitiesCanBeRelated.remove(entitiesCanBeRelated.get(x));
		}
		boolean enrolled = false;
		boolean canInvite = false;
		if (Users.isPermitted(user,
				"Invite a user to join a private or secret organization",
				org.id, "organization")
				&& org.privacyLevel != 2) {
			canInvite = true;
		}

		if (Users.getEnrolledUsers(org).contains(user)) {
			enrolled = true;
		}
		boolean requestToJoin = false;
		if ((enrolled == false) && (org.privacyLevel == 1)) {
			requestToJoin = true;
		}
		int flag = 0;
		if ((Security.getConnected() == org.creator)
				|| (Security.getConnected().isAdmin)) {
			flag = 1;
		}
		boolean admin = user.isAdmin;
		boolean isMember = Users.getEnrolledUsers(org).contains(user);
		boolean creator = false;
		if (org.creator.equals(user)) {
			creator = true;
		}
		List<RequestToJoin> allRequests = RequestToJoin.findAll();
		boolean alreadyRequested = false;
		if ((!user.isAdmin) && (!org.creator.equals(user))
				&& (!Users.getEnrolledUsers(org).contains(user))
				&& (org.privacyLevel == 1)) {
			int ii = 0;
			while (ii < allRequests.size()) {
				if (allRequests.get(ii).organization.equals(org)
						&& allRequests.get(ii).source.equals(user)) {
					alreadyRequested = true;
				}
				ii++;
			}
		}
		boolean follower = user.followingOrganizations.contains(org);
		List<User> users = User.findAll();
		String usernames = "";
		List<User> enrolledUsers = Users.getEnrolledUsers(org);
		if (canInvite) {
			for (int j = 0; j < users.size(); j++) {
				if (users.get(j).state.equalsIgnoreCase("a")
						&& !enrolledUsers.contains(users.get(j))
						&& !users.get(j).isAdmin) {
					if (j < users.size() - 1) {
						usernames += users.get(j).username + "|";
					} else {
						usernames += users.get(j).username;
					}
				}
			}
		}
		boolean join = false;
		if ((!Users.getEnrolledUsers(org).contains(user)) && (!admin)
				&& (org.privacyLevel == 2)) {
			join = true;
		}
		int logFlag = 0;
		if (Security.getConnected().equals(org.creator)
				|| Security.getConnected().isAdmin) {
			logFlag = 1;
		}

		long pictureId = org.profilePictureId;
		List<User> followers = org.followers;
		List<Plan> plans = Plans.planList("organization", org.id);
		render(user, org, entities, requestToJoin, flag, canInvite, admin,
				allowed, isMember, settings, creator, alreadyRequested, plans,
				follower, usernames, join, logFlag, pictureId, topics,
				entitiesCanBeRelated, entitiesICanView, followers);
	}

	/**
	 * This method that renders the page for creating a subentity
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S20
	 * 
	 * @param orgId
	 *            : The id of the organization in which the entity will be
	 *            created
	 * 
	 * @param pId
	 *            : The id of the parent entity
	 * 
	 */
	public static void goToCreateSubEntity(long orgid, long pId) {
		MainEntity parent = MainEntity.findById(pId);
		User user = Security.getConnected();
		Organization org = Organization.findById(parent.organization.id);
		List<MainEntity> entities = org.entitiesList;
		List<MainEntity> entitiesICanView = new ArrayList<MainEntity>();
		for (MainEntity entity : entities) {
			if (Users.isPermitted(user, "view", entity.id, "entity")) {
				entitiesICanView.add(entity);
			}
		}
		List<User> followers = org.followers;
		List<Plan> plans = Plans.planList("organization", org.id);
		render(user, org, entities, plans, entitiesICanView, followers, parent);
	}

	/**
	 * This method that renders the page for viewing any entity
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S2, C2S20
	 * 
	 * @param id
	 *            : The id of the entity that will be viewed
	 * 
	 */
	public static void viewEntity(long id) {
		User user = Security.getConnected();
		String baseurl = (String) routeArgs.get("baseurl");
		MainEntity entity = MainEntity.findById(id);
		notFoundIfNull(entity);
		if(Users.isPermitted(user,"view", id,"entity")){
		System.out.println(entity.name);
		Organization org = entity.organization;
		List<MainEntity> subentities = entity.subentities;
		List<Topic> topicList = entity.topicList;
		//faruki
		Organization organization = entity.organization;
	
		entity.incrmentViewed();
		entity.save();

		int canCreateEntity = 0;
		int canCreateSubEntity = 0;
		int canDeleteEntity = 0;
		if (user.isAdmin
				|| Users.isPermitted(user, "create entities",
						entity.organization.id, "organization")) {
			canCreateEntity = 1;
			canDeleteEntity = 1;
		}
		if (user.isAdmin
				|| Users.isPermitted(user, "create entities",
						entity.organization.id, "organization")
				|| Users.isPermitted(user,
						"Create a sub-entity for entity he/she manages",
						entity.id, "entity")) {
			canCreateSubEntity = 1;
		}
		int permission = 1;
		int invite = 0;
		int canEdit = 0;
		int canRequest = 0;
		int canRequestRelationship = 0;
		int canRestrict = 0;
		boolean entityIsLocked = entity.createRelationship;
		System.out.println("ENTITY "+entity);
		List<User> allowed = Users.getEntityOrganizers(entity);
		if (org.creator.equals(user) || user.isAdmin) {
			canRestrict = 1;
		}
		if (org.creator.equals(user) || allowed.contains(user) || user.isAdmin)
			canEdit = 1;
		if (!Users.isPermitted(user, "post topics", entity.id, "entity"))
			permission = 0;
		if (Users.isPermitted(user, "use", entity.id, "entity"))
			canRequest = 1;
		if (Users
				.isPermitted(
						user,
						"invite Organizer or Idea Developer to become Organizer or Idea Developer in an entity he/she manages",
						entity.id, "entity"))
			invite = 1;
		if (UserRoleInOrganizations.isOrganizer(user, entity.id, "entity")) {
			canRequestRelationship = 1;
		}
		int check = 0;
		if (Users.isPermitted(user,
				"block a user from viewing or using a certain entity",
				entity.id, "entity"))
			check = 1;
		int check1 = 0;
		if (Users.isPermitted(user, "view", entity.id, "entity"))
			check1 = 1;
		int check2 = 0;
		if (Users.isPermitted(user, "use", entity.id, "entity"))
			check2 = 1;
		boolean follower = user.followingEntities.contains(entity);
		boolean canCreateRelationship = EntityRelationships.isAllowedTo(id);
		boolean manageTopicRequests = Users.isPermitted(user, "accept/reject requests to post topics", entity.id, "entity");
		List<User> followers = entity.followers;
		List<Plan> plans = Plans.planList("entity", entity.id);
		List<MainEntity> entityList = new ArrayList<MainEntity>();

		if (entity.organization.entitiesList != null) {
			entityList = entity.organization.entitiesList;
			entityList.remove(entity);
		}

		for (int i = 0; i < entityList.size(); i++) {
			if (!entityList.get(i).createRelationship)
				entityList.remove(entityList.get(i));
		}
		List<String> relationNames = new ArrayList<String>();
		
		for(int j=0;j<org.relationNames.size();j++)
			relationNames.add(org.relationNames.get(j).name);
		
		render(user, org, entity, subentities, topicList, permission, invite,
				canEdit, canCreateEntity, canCreateSubEntity, follower,
				canCreateRelationship, canRequest, canRequestRelationship,
				canRestrict, entityIsLocked, plans, canDeleteEntity, followers,
				check, check1, check2, entityList, manageTopicRequests,relationNames);
		}
		else{
			BannedUsers.unauthorized();
		}
		}
		
	

	/**
	 * This method that renders the page for editing any entity
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S2, C2S20
	 * 
	 * @param entityId
	 *            : The id of the entity that will be edited
	 * 
	 */
	public static void editEntityPage(long entityId) {
		MainEntity targetEntity = MainEntity.findById(entityId);
		User user = Security.getConnected();
		Organization org = Organization.findById(targetEntity.organization.id);
		List<MainEntity> entities = org.entitiesList;
		List<MainEntity> entitiesICanView = new ArrayList<MainEntity>();
		for (MainEntity entity : entities) {
			if (Users.isPermitted(user, "view", entity.id, "entity")) {
				entitiesICanView.add(entity);
			}
		}
		List<User> followers = org.followers;
		List<Plan> plans = Plans.planList("organization", org.id);
		render(user, org, entities, plans, entitiesICanView, followers,
				targetEntity);
	}

	/**
	 * The method that allows editing any entity
	 * 
	 * @author Noha Khater, Omar Faruki
	 * 
	 * @Stroy C2S2, C2S20
	 * 
	 * @param entityId
	 *            : The id of the entity that will be edited
	 * 
	 * @param name
	 *            : The new name of the entity
	 * 
	 * @param description
	 *            : The new description of the entity
	 * 
	 * @param createRelationship
	 *            specifies whether an entity can have relationships with others
	 * 
	 */
	public static void editEntity(long entityId, String name,
			String description, boolean createRelationship) {
		User user = Security.getConnected();
		MainEntity entity = MainEntity.findById(entityId);
		entity.name = name;
		entity.description = description;
		entity.createRelationship = createRelationship;
		entity.save();
		Log.addUserLog(
				"<a href=\"/Users/viewProfile?userId="
						+ user.id
						+ "\">"
						+ user.username
						+ "</a>"
						+ " has edited the entity "
						+ "<a href=\"/MainEntitys/viewEntity?mainentityId="
						+ entity.id + "\">" + entity.name + "</a>",
				entity.organization);
		redirect(request.controller + ".viewEntity", entity.id,
				"Entity created");
	}

	/**
	 * renders the page for requesting relationship creation between entities.
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
	 * 
	 * @param userId
	 *            the id of the user making the request
	 * 
	 * @param organisationId
	 *            the id of the organisation
	 * 
	 * @param entityId
	 *            the id of the entity from which the request is made
	 * 
	 */
	public static void requestRelationship(long userId, long organisationId,
			long entityId) {
		Organization organisation = Organization.findById(organisationId);
		User user = User.findById(userId);
		MainEntity entity = MainEntity.findById(entityId);
		List<MainEntity> subentities = entity.subentities;
		List<Topic> topicList = entity.topicList;
		int canCreateEntity = 0;
		int canCreateSubEntity = 0;
		int canDeleteEntity = 0;
		if (user.isAdmin
				|| Users.isPermitted(user, "create entities",
						entity.organization.id, "organization")) {
			canCreateEntity = 1;
			canDeleteEntity = 1;
		}
		if (user.isAdmin
				|| Users.isPermitted(user, "create entities",
						entity.organization.id, "organization")
				|| Users.isPermitted(user,
						"Create a sub-entity for entity he/she manages",
						entity.id, "entity")) {
			canCreateSubEntity = 1;
		}
		int permission = 1;
		int invite = 0;
		int canEdit = 0;
		int canRequest = 0;
		int canRequestRelationship = 0;
		int canRestrict = 0;
		boolean entityIsLocked = entity.createRelationship;
		List<User> allowed = Users.getEntityOrganizers(entity);
		if (organisation.creator.equals(user) || user.isAdmin) {
			canRestrict = 1;
		}
		if (organisation.creator.equals(user) || allowed.contains(user)
				|| user.isAdmin)
			canEdit = 1;
		if (!Users.isPermitted(user, "post topics", entity.id, "entity"))
			permission = 0;
		if (Users.isPermitted(user, "use", entity.id, "entity"))
			canRequest = 1;
		if (Users
				.isPermitted(
						user,
						"invite Organizer or Idea Developer to become Organizer or Idea Developer in an entity he/she manages",
						entity.id, "entity"))
			invite = 1;
		if (UserRoleInOrganizations.isOrganizer(user, entity.id, "entity")) {
			canRequestRelationship = 1;
		}
		int check = 0;
		if (Users.isPermitted(user,
				"block a user from viewing or using a certain entity",
				entity.id, "entity"))
			check = 1;
		int check1 = 0;
		if (Users.isPermitted(user, "view", entity.id, "entity"))
			check1 = 1;
		int check2 = 0;
		if (Users.isPermitted(user, "use", entity.id, "entity"))
			check2 = 1;
		boolean follower = user.followingEntities.contains(entity);
		List<Plan> plans = Plans.planList("entity", entity.id);
		List<User> followers = entity.followers;
		render(user, organisation, entity, subentities, topicList, permission,
				invite, canEdit, canCreateEntity, canCreateSubEntity, follower,
				canRequest, canRequestRelationship, canRestrict,
				entityIsLocked, plans, canDeleteEntity, followers, check,
				check1, check2);
	}

	/**
	 * renders the page for viewing the relationships for an entity.
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
	 * 
	 * @param userId
	 *            the id of the user viewing the requests.
	 * 
	 * @param organisationId
	 *            the id of the organisation.
	 * 
	 * @param entityId
	 *            the id of the topic's entity.
	 * 
	 * @param canRequestRelationship
	 *            boolean variable whether this user is allowed to make
	 *            relationship requests or not.
	 * 
	 */
	public static void viewRelationships(long userId, long organisationId,
			long entityId, boolean canRequestRelationship) {
		User user = User.findById(userId);
		Organization organisation = Organization.findById(organisationId);
		MainEntity entity = MainEntity.findById(entityId);
		render(user, organisation, entity, canRequestRelationship);
	}

	/**
	 * helper method for deleting an entity.
	 * 
	 * @author Noha Khater
	 * 
	 * @param entityId
	 *            the id of the entity to be deleted.
	 * 
	 * @return boolean
	 */
	public static boolean deleteEntityHelper(long entityId) {
		MainEntity entity = MainEntity.findById(entityId);
		System.out.println("will now delete entity " + entity.name);
		List<Organization> allOrganizations = Organization.findAll();
		List<MainEntity> allEntities = MainEntity.findAll();
		List<Tag> tags = Tag.findAll();
		List<User> followers = User.findAll();
		List<Topic> topicList =entity.topicList;
		int size = allOrganizations.size();
		for (int i = 0; i < size; i++) {
			if (allOrganizations.get(i).entitiesList.contains(entity)) {
				System.out.println("organization "+allOrganizations.get(i).name);
				allOrganizations.get(i).entitiesList.remove(entity);
				allOrganizations.get(i).save();
			}
		}
		size = followers.size();
		for (int i = 0; i < size; i++) {
			if (followers.get(i).followingEntities.contains(entity)) {
				System.out.println("followers " +followers.get(i));
				followers.get(i).followingEntities.remove(entity);
				followers.get(i).save();
			}
		}
		size = tags.size();
		for (int i = 0; i < size; i++) {
			if (tags.get(i).entities.contains(entity)) {
				System.out.println("tags " + tags.get(i).name);
				tags.get(i).entities.remove(entity);
				tags.get(i).save();
			}
		}
		// size = entity.relationsSource.size();
		// for (int j = 0; j < size; j++) {
		// EntityRelationships.deleteER(entity.relationsSource.get(j).id);
		// }
		// size = entity.relationsDestination.size();
		// for (int j = 0; j < size; j++) {
		// EntityRelationships.deleteER(entity.relationsDestination.get(j).id);
		// }
		// Mohamed Hisham {
		for (int i = 0; i <entity.relationsSource.size(); i++) {
			EntityRelationships.delete(entity.relationsSource.get(i).id);
			i--;
		}

		for (int i = 0; i < entity.relationsDestination.size(); i++) {
			EntityRelationships.delete(entity.relationsDestination.get(i).id);
			i--;
		}
		// }
		size = entity.relationshipRequestsSource.size();
		for (int j = 0; j < size; j++) {
			CreateRelationshipRequests.delete(entity.relationshipRequestsSource
					.get(0).id);
		}
		size = entity.relationshipRequestsDestination.size();
		for (int j = 0; j < size; j++) {
			CreateRelationshipRequests
					.delete(entity.relationshipRequestsDestination.get(0).id);
		}
	//	size = topicList.size();
		for (int j = 0; j <entity.topicList.size(); j++) {
			System.out.println("topics " + entity.topicList.get(j).title);
			Topics.deleteTopicInternally("" + entity.topicList.get(j).id);
			j--;
		}
		size = allEntities.size();
		for (int i = 0; i < size; i++) {
			if (allEntities.get(i).subentities.contains(entity)) {
				System.out.println("subentities remove " + allEntities.get(i).name);
				allEntities.get(i).subentities.remove(entity);

				allEntities.get(i).save();
			}
		}
		for (int i = 0; i <entity.subentities.size(); i++) {
			System.out.println("subentities delete " + entity.subentities.get(i).name);
			MainEntitys.deleteEntityHelper(entity.subentities.get(i).id);
			i--;
		}
		
		// Mai Magdy
		List<Invitation> invite = Invitation.find("byEntity", entity).fetch();
		for (int i = 0; i < invite.size(); i++){
			invite.get(i).delete();
			i--;
		}
		//

		size = entity.topicRequests.size();
		for (int i = 0; i < size; i++) {
			TopicRequests.delete(""+entity.topicRequests.get(i).id);
			i--;
		}
		
		List<Item> allItems = Item.findAll();
		size = entity.relatedItems.size();
		for (int i = 0; i < size; i++) {
			if(allItems.get(i).relatedEntity==entity) {
				allItems.get(i).relatedEntity = null;
				allItems.get(i).save();
			}
		}
		entity.logs.clear();
		entity.delete();
		return true;
	}
	
	/**
	 * deletes the entity.
	 * 
	 * @author Noha Khater
	 *  
	 * @param entityId
	 *            the id of the entity to be deleted.
	 */
	public static void deleteEntity(long entityId) {

		MainEntity entity = MainEntity.findById(entityId);
		notFoundIfNull(entity);
		Organization organization = entity.organization;
User user = Security.getConnected();
	List<User>	organizers = Users.getEntityOrganizers(entity);
	organizers.add(organization.creator);
	for (int i = 0; i < organizers.size(); i++)
		Notifications.sendNotification(organizers.get(i).id,
				entityId, "Entity", " User " + user.username
						+ " has deleted entity " + entity.name);
	deleteEntityHelper(entityId);
		Organizations.viewProfile(entity.organization.id);
	}
	
	/**
	 * Ovverides CRUD's delete and deletes an entity
	 * 
	 * @author Alia El Bolock
	 * 
	 * @param id
	 *            : the id of the entity to be deleted
	 * 
	 */
	public static void delete(String id) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Model object = type.findById(id);
        notFoundIfNull(object);
        long entityId = Long.parseLong(id);
        try {
        	deleteEntityHelper(entityId);
        } catch (Exception e) {
            flash.error(Messages.get("crud.delete.error", type.modelName));
            redirect(request.controller + ".show", object._key());
        }
        flash.success(Messages.get("crud.deleted", type.modelName));
        redirect(request.controller + ".list");
    }

}
