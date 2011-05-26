package controllers;

import groovy.util.ObjectGraphBuilder.RelationNameResolver;

import java.util.ArrayList;
import java.util.List;

import net.sf.oval.constraint.Email;
import notifiers.Mail;

import play.data.validation.Required;
import play.data.validation.Validation;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Controller;
import play.mvc.With;
import controllers.CoolCRUD.ObjectType;
import models.BannedUser;
import models.CreateRelationshipRequest;
import models.EntityRelationship;
import models.Invitation;
import models.Log;
import models.MainEntity;
import models.Organization;
import models.Plan;
import models.RenameEndRelationshipRequest;
import models.RequestToJoin;
import models.Role;
import models.Tag;
import models.Topic;
import models.TopicRelationship;
import models.User;
import models.UserRoleInOrganization;

@With(Secure.class)
public class Organizations extends CoolCRUD {

	/**
	 * checks relation name for duplicate
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @param relationName
	 *            : name of the relation
	 * 
	 * @param relationNames
	 *            : list of relation names in the organization
	 * 
	 * @return boolean value if duplicate(true) or not(false)
	 */
	public static boolean isDuplicate(String relationName,
			ArrayList<String> relationNames) {
		for (int i = 0; i < relationNames.size(); i++) {
			if (relationName.equals(relationNames.get(i)))
				return true;
		}
		return false;
	}

	/**
	 * adds a new name to the previously created relation names in the
	 * organization
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S32
	 * 
	 * @param organizationId
	 *            : id of the organization to add the relation name in
	 * 
	 * @param name
	 *            : name of the relation to add
	 * 
	 * @return boolean
	 * 
	 */

	public static boolean addRelationName(long organizationId, String name) {
		Organization organization = Organization.findById(organizationId);
		boolean isDuplicate = true;
		if (!isDuplicate(name, organization.relationNames)) {
			isDuplicate = false;
			organization.relationNames.add(name);
			organization.save();
		}
		return isDuplicate;
	}

	/**
	 * 
	 * This Method returns the Privacy level of an organization given the
	 * organization Id
	 * 
	 * @author Fadwa sakr
	 * 
	 * @story C2S34
	 * 
	 * @param id
	 *            : the id of the organization for which the privacy level is
	 *            needed
	 * 
	 * @return int
	 */

	public static int getPrivacyLevel(long id) {

		Organization organization = Organization.findById(id);
		if (organization != null)
			return organization.privacyLevel;
		return -1;
	}

	/**
	 * 
	 * This Method enables the ability of creation of tags in a certain
	 * organization
	 * 
	 * @author Fadwa sakr
	 * 
	 * @story C2S4
	 * 
	 * @param id
	 *            : the id of the organization for which the preferences is
	 *            being enabled
	 * 
	 */

	public static void enableTags(long id) {
		System.out.println("enabling");
		System.out.println(id);
		Organization organization = Organization.findById(id);
		System.out.println(organization);
		notFoundIfNull(organization);
		organization.createTag = true;
		organization.save();
		System.out.println(organization.createTag);
		System.out.println(getPrivacyLevel(id));

	}

	/**
	 * 
	 * This Method disables the ability of creation of tags in a certain
	 * organization
	 * 
	 * @author Fadwa sakr
	 * 
	 * @story C2S4
	 * 
	 * @param id
	 *            : the id of the organization for which the preferences is
	 *            being disabled
	 * 
	 */

	public static void disableTags(Long id) {
		System.out.println("disabling");
		Organization organization = Organization.findById(id);
		System.out.println(organization);
		System.out.println(id);
		notFoundIfNull(organization);
		organization.createTag = false;
		organization.save();
		System.out.println(organization.createTag);
		System.out.println(getPrivacyLevel(id));

	}

	/**
	 * This method gets the list of topics of a certain organization
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S28
	 * 
	 * @param orgId
	 *            ID of an organization of type long
	 */

	public static void getTopics(long orgId) {
		Organization organization = Organization.findById(orgId);
		notFoundIfNull(organization);
		List<Topic> topics = new ArrayList<Topic>();
		int i = 0;
		while (i < organization.entitiesList.size()) {
			int j = 0;
			while (j < organization.entitiesList.get(i).topicList.size()) {
				topics.add(organization.entitiesList.get(i).topicList.get(j));
				j++;
			}
			i++;
		}
		render(topics);
	}

	/**
	 * This method creates an invitation to a user to join an organization and
	 * sends it to a user
	 * 
	 * @author ibrahim al-khayat
	 * 
	 * @story C2S26
	 * 
	 * @param organizationId
	 *            the id of the organization
	 * 
	 * @param email
	 *            the email of the receiver
	 * 
	 * @param byMail
	 *            invite by mail or by username
	 */

	public static void sendInvitation(long organizationId, String email,
			boolean byMail) {
		if (!byMail) {
			email = ((User) User.find("byUsername", email).first()).email;
		}
		User reciever = User.find("byEmail", email).first();
		if (reciever != null) {
			if (reciever.state.equalsIgnoreCase("d")
					|| reciever.state.equalsIgnoreCase("n")) {
				return;
			}
		}
		Organization organization = Organization.findById(organizationId);
		User sender = Security.getConnected();
		List<Invitation> invitations = Invitation.findAll();
		Invitation temp;
		for (int i = 0; i < invitations.size(); i++) {
			temp = invitations.get(i);
			if (temp.sender.id == sender.id
					&& temp.email.equalsIgnoreCase(email)
					&& temp.role.equalsIgnoreCase("idea developer")) {
				return;
			}
		}
		Invitation invitation = new Invitation(email, null, organization,
				"Idea Developer", sender, null);
		invitation._save();
		sender.invitation.add(invitation);
		sender.save();
		if (reciever != null) {
			Notifications.sendNotification(reciever.id, organizationId,
					"organization", "you have recived an ivitation to join"
							+ organization.name);
		}
		try {
			Mail.invite(email, "Idea Devoloper", organization.name, "", 0);
		} catch (Exception e) {

		}
	}

	/**
	 * This method merely redirects you to the create organization page where
	 * all the forms are
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S1
	 */
	public static void createOrganization() {
		User user = Security.getConnected();
		render(user);
		// if (validation.hasErrors()) {
		// params.flash();
		// validation.keep();
		// render();
		// }

	}

	/**
	 * This method creates a new Organization
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S1
	 * 
	 * @param name
	 *            name of the organization
	 * 
	 * @param privacyLevel
	 *            whether the organization is public, private or secret
	 * 
	 * @param createTag
	 *            whether the users in that organization are allowed to create
	 *            tags
	 */
	public static void createOrg(String name, String privacyLevel,
			String createTag, String description) {

		User creator = Security.getConnected();

		// Organization existing_organization = Organization.find(
		// "name like '" + name + "'").first();
		List<Organization> allOrganizations = Organization.findAll();
		boolean duplicate = false;
		int i = 0;
		while (i < allOrganizations.size()) {
			if (allOrganizations.get(i).name.equalsIgnoreCase(name)) {
				duplicate = true;
				break;
			}
			i++;
		}
		if (!duplicate) {

			int privacyLevell = 0;
			if (privacyLevel.equalsIgnoreCase("Public")) {
				privacyLevell = 2;
			} else {
				if (privacyLevel.equalsIgnoreCase("Private")) {
					privacyLevell = 1;
				}
			}
			boolean createTagg = false;
			if (createTag.equalsIgnoreCase("Yes")) {
				createTagg = true;
			}
			Organization org = new Organization(name, creator, privacyLevell,
					createTagg, description).save();
			Log.addUserLog("<a href=\"http://localhost:9008/users/viewprofile?userId="
					+ creator.id
					+ "\">"
					+ creator.firstName
					+ " "
					+ creator.lastName
					+ "</a>"
					+ " has created the organisation ("
					+ "<a href=\"http://localhost:9008/organizations/viewprofile?id="
					+ org.id + "\">" + org.name + "</a>" + ")", creator, org);
			Role role = Roles.getRoleByName("organizationLead");
			UserRoleInOrganizations.addEnrolledUser(creator, org, role);
			MainEntity defaultEntity = new MainEntity("Default", "", org, false);
			defaultEntity.save();
			flash.success("Your organization has been created!!");
		} else {
			flash.error("Organization name already in use..");
		}
		Login.homePage();

	}

	/**
	 * The method that allows a user to follow a certain organization
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S10
	 * 
	 * @param organizationId
	 *            : The id of the organization that the user wants to follow
	 * 
	 */
	public static void followOrganization(long organizationId) {
		User user = Security.getConnected();
		Organization org = Organization.findById(organizationId);
		if (!org.followers.contains(user)) {
			org.followers.add(user);
			org.save();
			user.followingOrganizations.add(org);
			user.save();
			Log.addUserLog(
					"<a href=\"http://localhost:9008/users/viewprofile?userId="
							+ user.id
							+ "\">"
							+ user.firstName
							+ " "
							+ user.lastName
							+ "</a>"
							+ " has followed the organisation ("
							+ "<a href=\"http://localhost:9008/organizations/viewprofile?id="
							+ org.id + "\">" + org.name + "</a>" + ")", org);
			redirect(request.controller + ".viewProfile", org.id,
					"You are now a follower");
		}
	}

	/**
	 * The method that renders the page for viewing the followers of an
	 * organization
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S10
	 * 
	 * @param organizationId
	 *            : The id of the organization that the user wants to view its
	 *            followers
	 * 
	 * @param f
	 *            : The String which is used as a variable for checking
	 */
	public static void viewFollowers(long organizationId, String flag) {
		Organization org = Organization.findById(organizationId);
		User user = Security.getConnected();
		if (flag.equals("true"))
			followOrganization(organizationId);
		render(org, user);
	}

	/**
	 * This method render a view that gets all organizations on the system in
	 * which the user is enrolled with the ability to go to the create
	 * organization page
	 * 
	 * @author Omar Faruki
	 */
	public static void mainPage() {
		User user = Security.getConnected();
		List<Organization> organizations = new ArrayList<Organization>();
		List<Organization> allOrganizations = Organization.findAll();
		int i = 0;
		while (i < allOrganizations.size()) {
			if (Users.getEnrolledUsers(allOrganizations.get(i)).contains(user)) {
				organizations.add(allOrganizations.get(i));
			}
			i++;
		}
		render(user, organizations);
	}

	/**
	 * This method render the main Profile Page of a specific organization
	 * 
	 * @author Omar Faruki, Noha Khater
	 * 
	 * @param id
	 *            The id of the organization that you wish to view
	 */
	public static void viewProfile(long id) {
		User user = Security.getConnected();
		Organization org = Organization.findById(id);
		List<MainEntity> allEntities = MainEntity.findAll();
		List<Tag> tags = new ArrayList<Tag>();
		// List<Tag> allTags = Tag.findAll();
		int i = 0;
		int allowed = 0;
		int settings = 0;
		

		org.incrmentViewed();
		org.save();
		
		if (org.privacyLevel == 1
				&& Users.isPermitted(
						user,
						"accept/reject join requests from users to join a private organization",
						id, "organization"))
			allowed = 1;
		if (Users
				.isPermitted(
						user,
						"enable/disable the user to create their own tags within an organization",
						id, "organization"))
			settings = 1;
		System.out.println(settings);
		System.out.println(user);
		System.out.println(org);

		// while (i < org.createdTags.size()) {
		// tags.add(org.createdTags.get(i));
		// i++;
		// }
		// i = 0;
		while (i < org.relatedTags.size()) {
			tags.add(org.relatedTags.get(i));
			i++;
		}
		// System.out.println(org.relatedTags.get(0).name);
		// boolean loop = false;
		// if (tags.isEmpty()) {
		// while (i < allTags.size()) {
		// if (allTags.get(i).createdInOrganization.privacyLevel == 2) {
		// tags.add(allTags.get(i));
		// loop = true;
		// }
		// i++;
		// }
		// }
		// if (loop == false) {
		// while (i < allTags.size()) {
		// if (!tags.contains(allTags.get(i))
		// && (allTags.get(i).createdInOrganization.privacyLevel == 2)) {
		// tags.add(allTags.get(i));
		// }
		// i++;
		// }
		// }
		int canCreateEntity = 0;
		if (user.isAdmin
				|| Users.isPermitted(user, "create entities", id,
						"organization")) {
			canCreateEntity = 1;
		}
		List<MainEntity> entitiesICanView = new ArrayList<MainEntity>();
		for (MainEntity entity : allEntities) {
			if (Users.isPermitted(user, "view", entity.id, "entity")) {
				entitiesICanView.add(entity);
			}
		}

		List<MainEntity> entities = org.entitiesList;
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

		// Lama Ashraf view logs

		int logFlag = 0;
		if (Security.getConnected().equals(org.creator)
				|| Security.getConnected().isAdmin) {
			logFlag = 1;
		}

		long pictureId = org.profilePictureId;
		List<User> followers = org.followers;
		List<Plan> plans = Plans.planList("organization", org.id);
		render(user, org, entities, requestToJoin, canCreateEntity, tags, flag,
				canInvite, admin, allowed, isMember, settings, creator,
				alreadyRequested, plans, follower, usernames, join, logFlag,
				pictureId, topics, entitiesCanBeRelated, entitiesICanView,
				followers);
	}

	/**
	 * A method to view all organizations that a user can see
	 * 
	 * @author Omar Faruki
	 */
	public static void viewAllOrganizations() {
		List<Organization> allOrganizations = Organization.findAll();
		List<Organization> organizations = new ArrayList<Organization>();
		User user = Security.getConnected();
		boolean admin = user.isAdmin;
		if (admin) {
			int i = 0;
			while (i < allOrganizations.size()) {
				organizations.add(allOrganizations.get(i));
				i++;
			}
		} else {
			int i = 0;
			while (i < allOrganizations.size()) {
				if ((allOrganizations.get(i).privacyLevel != 0)) {
					organizations.add(allOrganizations.get(i));
				} else {
					if (Users.getEnrolledUsers(allOrganizations.get(i))
							.contains(user)) {
						organizations.add(allOrganizations.get(i));
					}
				}
				i++;
			}
		}
		render(organizations, user);
	}

	/**
	 * This method allows a user to join a public organization
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S29
	 * 
	 * @param organizationId
	 *            The id of the organization that the user wishes to join
	 */
	public static void join(long organizationId) {
		Organization organization = Organization.findById(organizationId);
		User user = Security.getConnected();
		Log.addUserLog("<a href=\"http://localhost:9008/users/viewprofile?userId="
				+ user.id
				+ "\">"
				+ user.firstName
				+ " "
				+ user.lastName
				+ "</a>"
				+ " has joined the organisation ("
				+ "<a href=\"http://localhost:9008/organizations/viewprofile?id="
				+ organization.id + "\">" + organization.name + "</a>" + ")",user, organization);
		Role role = Roles.getRoleByName("idea developer");
		UserRoleInOrganizations.addEnrolledUser(user, organization, role);
		Organizations.viewProfile(organizationId);
	}

	/**
	 * This method deletes an organization
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S37
	 * 
	 * @param organizationId
	 *            The id of the organization that the user wishes to join
	 */
	public static void deleteOrganization(long organizationId) {
		User user = Security.getConnected();
		Organization organization = Organization.findById(organizationId);
		List<User> followers = User.findAll();
		List<Tag> createdTags = organization.createdTags;
		List<BannedUser> bannedUsers = organization.bannedUsers;
		List<UserRoleInOrganization> users = organization.userRoleInOrg;
		List<MainEntity> entities = organization.entitiesList;
		List<Tag> relatedTags = organization.relatedTags;
		int j = 0;
		while (j < followers.size()) {
			if (followers.get(j).followingOrganizations.contains(organization)) {
				followers.get(j).followingOrganizations.remove(organization);
				followers.get(j).save();
			}
			j++;
		}
		j = 0;
		while (j < createdTags.size()) {
			relatedTags.remove(createdTags.get(j));
			organization.save();
			Tags.delete(createdTags.get(j).id);
			j++;
		}
		if (!relatedTags.isEmpty()) {
			j = 0;
			while (j < relatedTags.size()) {
				relatedTags.get(j).organizations.remove(organization);
				relatedTags.get(j).save();
				j++;
			}
		}
		j = 0;
		while (j < bannedUsers.size()) {
			BannedUser.delete(bannedUsers.get(j));
			j++;
		}
		j = 0;
		while (j < users.size()) {
			UserRoleInOrganization.delete(users.get(j));
			j++;
		}
		j = 0;
		while (j < entities.size()) {
			MainEntitys.deleteEntity(entities.get(j).id);
			j++;
		}
		organization.creator.createdOrganization.remove(organization);
		// fadwa
		for (int i = 0; i < organization.joinRequests.size(); i++)
			organization.joinRequests.get(i).delete();
		// fadwa

		// Mai Magdy
		List<Invitation> invite = Invitation.find("byOrganization",
				organization).fetch();
		for (int i = 0; i < invite.size(); i++)
			invite.get(i).delete();
		//

		organization.logs.clear();

		organization.delete();
		Log.addUserLog("<a href=\"http://localhost:9008/users/viewprofile?userId="
				+ user.id
				+ "\">"
				+ user.firstName
				+ " "
				+ user.lastName
				+ "</a>"
				+ " deleted the organisation ("
				+ organization.name + ")", user);
		Login.homePage();
	}

	/**
	 * This method only renders the view for editing the organization
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S8
	 * 
	 * @param organizationId
	 *            The id of the organization that will be edited
	 */
	public static void editOrganization(long organizationId) {
		Organization organization = Organization.findById(organizationId);
		User user = Security.getConnected();
		render(organization, user);
	}

	/**
	 * This method edits the Organization
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S8
	 * 
	 * @param organizationId
	 *            The id of the edited organization
	 * 
	 * @param createTag
	 *            A String to determine if the organization allows its users to
	 *            create tags
	 * 
	 * @param privacyLevel
	 *            The privacy level of the organization
	 * 
	 * @param description
	 *            The description of the organization
	 */
	public static void edit(long organizationId, String createTag,
			String privacyLevel, String description) {
		User user = Security.getConnected();
		Organization organization = Organization.findById(organizationId);
		int privacyLevell = 0;
		if (privacyLevel.equalsIgnoreCase("Public")) {
			privacyLevell = 2;
		} else {
			if (privacyLevel.equalsIgnoreCase("Private")) {
				privacyLevell = 1;
			}
		}
		boolean createTagg = false;
		if (createTag.equalsIgnoreCase("Yes")) {
			createTagg = true;
		}
		organization.privacyLevel = privacyLevell;
		organization.createTag = createTagg;
		organization.description = description;
		organization.save();
		Log.addUserLog("<a href=\"http://localhost:9008/users/viewprofile?userId="
				+ user.id
				+ "\">"
				+ user.firstName
				+ " "
				+ user.lastName
				+ "</a>"
				+ " has edited the organisation ("
				+ "<a href=\"http://localhost:9008/organizations/viewprofile?id="
				+ organization.id + "\">" + organization.name + "</a>" + ")", user, organization);
		Organizations.viewProfile(organizationId);
	}

	public static boolean isDuplicateRequest(String source, String destination,
			String relationshipName, long organisationId, int type, int flag,
			int requestType, long relationId) {
		Organization organisation = Organization.findById(organisationId);
		if (flag == 1) {
			for (CreateRelationshipRequest request : organisation.createRelationshipRequest) {
				if (request.type == 0) {
					if (request.sourceEntity.name.equalsIgnoreCase(source)
							&& request.destinationEntity.name
									.equalsIgnoreCase(destination)
							&& request.name.equalsIgnoreCase(relationshipName)) {
						return true;
					}
				} else {
					if (request.sourceTopic.title.equalsIgnoreCase(source)
							&& request.destinationTopic.title
									.equalsIgnoreCase(destination)
							&& request.name.equalsIgnoreCase(relationshipName)) {
						return true;
					}
				}
			}
		} else {
			for (RenameEndRelationshipRequest request : organisation.renameEndRelationshipRequest) {
				if (request.requestType == requestType) {
					if (request.type == 0) {
						EntityRelationship entityRelation = EntityRelationship
								.findById(relationId);
						if (requestType == 1) {
							if (request.entityRelationship
									.equals(entityRelation)
									&& request.newName
											.equalsIgnoreCase(relationshipName)) {
								return true;
							}
						} else {
							if (request.entityRelationship
									.equals(entityRelation)) {
								return true;
							}
						}
					} else {
						TopicRelationship topicRelation = TopicRelationship
								.findById(relationId);
						if (requestType == 1) {
							if (request.topicRelationship.equals(topicRelation)
									&& request.newName
											.equalsIgnoreCase(relationshipName)) {
								return true;
							}
						} else {
							if (request.topicRelationship.equals(topicRelation)) {
								return true;
							}
						}
					}
				}

			}

		}
		return false;
	}

}