/**
 * @author Mai Magdy , Fadwa Sakr
 */

package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import models.Invitation;
import models.MainEntity;
import models.Organization;
import models.Role;
import models.Topic;
import models.User;
import models.UserRoleInOrganization;
import notifiers.Mail;
import play.data.validation.Required;
import play.mvc.With;
import models.Log;
import com.google.gson.JsonObject;

@With(Secure.class)
public class Invitations extends CoolCRUD {
	public static List<User> users = new ArrayList<User>();
	public static List<Integer> done = new ArrayList<Integer>();

	/**
	 * 
	 * Renders the entity/topic to the invitation home page that contains search
	 * for user to invite or invite by mail
	 * 
	 * @author ${Mai.Magdy} , ${Fadwa.Sakr}
	 * 
	 * @story C1S6,C2S22
	 * 
	 * @param id
	 *            long id of entity/topic that sends the invitation
	 * 
	 * @param type
	 *            int type either entity (0) or topic(1)
	 * 
	 * @param check
	 *            int check : 0 if viewing the page, 1 if search result, 2 if
	 *            invite by mail
	 * 
	 * 
	 */
	public static void invite(long id, int type, int check) {

		User user = Security.getConnected();
		List<User> usersMatched = new ArrayList<User>();
		for (User user1 : users) {
			usersMatched.add(user1);
		}
		List<Integer> usersInvited = new ArrayList<Integer>();
		for (Integer item : done) {
			usersInvited.add(item);
		}
		if (type == 0) {
			int permitted = 0;
			if (Users
					.isPermitted(
							user,
							"invite Organizer or Idea Developer to become Organizer or Idea Developer in an entity he/she manages",
							id, "entity"))
				permitted = 1;
			MainEntity entity = MainEntity.findById(id);
			render(type, check, entity, usersMatched, permitted, user);
		} else {
			int permitted = 0;
			if (Users
					.isPermitted(
							user,
							"invite Organizer or Idea Developer to become Organizer or Idea Developer in an entity he/she manages",
							id, "entity"))
				permitted = 1;
			Topic topic = Topic.findById(id);
			notFoundIfNull(topic);
			render(type, check, topic, usersMatched, user, permitted,
					usersInvited);
		}

	}

	/**
	 * 
	 * Searches for a user to invite. The result is a list not containing the
	 * organizers of this entity or the users that have been invited and didn't
	 * respond yet
	 * 
	 * @author ${Mai.Magdy}, ${Fadwa.Sakr}
	 * 
	 * @story C1S6,C2S22
	 * 
	 * 
	 * @param type
	 *            int type either entity (0) or topic(1)
	 * 
	 * @param id
	 *            long id of the entity/topic that sends the invitation.
	 * 
	 * @param name
	 *            String name of the user to search for
	 * 
	 */
	public static void searchUser(int type, long id, @Required String name) {

		List<User> filter = Users.searchUser(name);
		List<User> userFilter = new ArrayList<User>();
		List<User> postsInTopic = new ArrayList<User>();
		List<Integer> invited = new ArrayList<Integer>();

		if (validation.hasErrors()) {
			flash.error("Please enter name/email first!");
			invite(id, type, 0);
		}

		if (type == 0) {
			MainEntity entity = MainEntity.findById(id);
			List<User> organizers = Users.getEntityOrganizers(entity);
			organizers.add(entity.organization.creator);
			for (int i = 0; i < filter.size(); i++) {
				if (!organizers.contains(filter.get(i))
						|| filter.get(i).isAdmin)
					userFilter.add(filter.get(i));
			}

		} else {
			for (int i = 0; i < filter.size(); i++) {
				if (!postsInTopic.contains(filter.get(i))) {
					userFilter.add(filter.get(i));
				}
			}
		}

		for (int i = 0; i < userFilter.size(); i++) {
			Invitation sent = Invitation.find("byEmail",
					userFilter.get(i).email).first();
			if (sent != null)
				invited.add(1); // userFilter.remove(i);
			else
				invited.add(0);
		}
		users = userFilter;
		done = invited;
		invite(id, type, 1);

	}

	/**
	 * 
	 * Adds a new invitation and renders the email and the entity then the Mail
	 * class sends the email, if sending to registered user the receiver will be
	 * notified with a notification
	 * 
	 * @author ${Mai.Magdy},${Fadwa.Sakr}
	 * 
	 * @story C1S6,C2S22
	 * 
	 * @param type
	 *            int type either entity (0) or topic(1)
	 * 
	 * @param email
	 *            String email of the destination of the invitation
	 * 
	 * @param id
	 *            long id of entity/topic that sends the invitation
	 * 
	 * @param userId
	 *            long userId that the invitation is to be sent to
	 * 
	 */

	public static void send(int type, @Required String email, long id,
			long userId) {
		MainEntity entity = null;
		Topic topic = null;
		String role = "";
		String name;
		List<User> invalidUsers = new ArrayList<User>();
		if (type == 0) {
			entity = MainEntity.findById(id);
			role = "Organizer";
			invalidUsers = Users.getEntityOrganizers(entity);
			name = entity.name;
			invalidUsers.add(entity.organization.creator);
		} else {
			topic = Topic.findById(id);
			role = "idea developer";
			invalidUsers = Topics.searchByTopic(id);
			name = topic.title;
		}

		if (!rfc2822.matcher(email).matches()) {
			flash.error("Invalid address");
			invite(id, type, 2);
		}

		boolean check = false;
		List<Invitation> invitation = Invitation.findAll();
		for (int i = 0; i < invitation.size(); i++) {
			if (entity != null) {
				if (invitation.get(i).email.equals(email)
						&& (invitation.get(i).entity.equals(entity) || invitation
								.get(i).entity.parent.equals(entity)))
					check = true;
			} else {
				if (invitation.get(i).email.equals(email)
						&& (invitation.get(i).topic.equals(topic)))
					check = true;
			}
		}
		if (check == true) {
			flash.error("Invitation has already been sent to that user before");
			invite(id, type, 2);
		}

		boolean isRegistered = false;
		User reciever = null;

		if (userId == 0) {

			boolean flag = true;
			isRegistered = true;
			reciever = User.find("byEmail", email).first();

			if (reciever != null) {

				for (int i = 0; i < invalidUsers.size(); i++) {
					if (invalidUsers.get(i).equals(reciever)
							|| reciever.isAdmin)
						flag = false;
				}

				if (!flag) {
					if (entity != null)
						flash.error("This user is already an organizer to this entity");
					else
						flash.error("This user is already an idea developer in that topic");
					invite(id, type, 2);
				}

				if (reciever.state.equals("n") || reciever.state.equals("d")) {
					flash.error("This account has been deactivated");
					invite(id, type, 2);
				}

			}
		}

		if (!isRegistered)
			reciever = User.find("byEmail", email).first();

		User user = Security.getConnected();
		invalidUsers.remove(user);

		if (type == 0) {
			Mail.invite(email, role, entity.organization.name, name, type);
			user.addInvitation(email, role, entity.organization, entity, null);
			for (int j = 0; j < invalidUsers.size(); j++)
				Notifications.sendNotification(invalidUsers.get(j).id,
						entity.id, "entity",
						"New organizer has been invited to be an organizer to entity  "
								+ name);

			if (reciever != null)
				Log.addUserLog("User " + user.firstName + " " + user.lastName
						+ " has invited user " + reciever.firstName + " "
						+ reciever.lastName + " to be organizer to "
						+ entity.name, entity, entity.organization, user);
			else

				Log.addUserLog("User " + user.firstName + " " + user.lastName
						+ " has invited unregiseterd user with email  " + email
						+ " to be organizer to " + entity.name, entity,
						entity.organization, user);

		} else {
			Organization organization = topic.entity.organization;
			Mail.invite(email, role, organization.name, name, type);
			user.addInvitation(email, role, organization, null, topic);
			for (int j = 0; j < invalidUsers.size(); j++)
				Notifications.sendNotification(invalidUsers.get(j).id,
						topic.id, "topic",
						"New user has been invited to be an Idea developer in topic  "
								+ name);

			if (reciever != null)
				Log.addUserLog("User " + user.firstName + " " + user.lastName
						+ " has invited user " + reciever.firstName + " "
						+ reciever.lastName + " to join topic " + topic.title,
						organization, topic, reciever, user);
			else

				Log.addUserLog("User " + user.firstName + " " + user.lastName
						+ " has invited unregiseterd user with email  " + email
						+ " to join topic " + topic.title, organization, topic,
						user);
		}

		if (reciever != null) {
			if (entity != null)
				Notifications.sendNotification(reciever.id, entity.id,
						"organization",
						"You have received a new invitation from " + name);
			else
				Notifications.sendNotification(reciever.id, id, "topic",
						"You have received a new invitation from " + name);
		}

		flash.success("Invitation has been sent successfuly");
		invite(id, type, 0);

	}

	private static final Pattern rfc2822 = Pattern
			.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");

	/**
	 * 
	 * Views the received invitations and renders the invitation list
	 * 
	 * @author ${Mai.Magdy} , ${Fadwa.Sakr}
	 * 
	 * @story C1S4,C2S17
	 * 
	 */

	public static void view() {

		User user = Security.getConnected();
		List<Invitation> invitation = Invitation.find("byEmail", user.email)
				.fetch();
		for (int i = 0; i < invitation.size(); i++) {
			if (invitation.get(i).entity != null) {
				if (!Users.isPermitted(user, "view",
						invitation.get(i).entity.id, "entity"))
					invitation.remove(i);
			} else if (invitation.get(i).topic != null) {
				if (!Users.isPermitted(user, "view",
						invitation.get(i).topic.id, "topic"))
					invitation.remove(i);
			}
		}
		render(invitation);

	}

	/**
	 * 
	 * Responds to the user (accept/reject) to the invitation.
	 * 
	 * @author ${Mai.Magdy} > if flag is false and role is organizer
	 * @author ${Fadwa.Sakr} > if flag is true
	 * @author ${Ibrahim.Khayat} > if flag is false and role is idea developer
	 * 
	 * @story C1S4,C2S17
	 * 
	 * 
	 * @param id
	 *            int id represents the clicked button, 0 if reject or 1 if
	 *            accept
	 * 
	 * @param i
	 *            long id of the invitation that will be removed
	 * 
	 */

	public static void respond(int id, long i) {

		Invitation invite = Invitation.findById(i);
		String roleName = invite.role.toLowerCase();
		Organization organization = null;
		MainEntity entity = null;
		Topic topic = null;
		User user = User.find("byEmail", invite.email).first();
		Role role = Role.find("byRoleName", roleName).first();
		boolean flag = true;
		if (invite.topic == null) {
			organization = invite.organization;
			entity = invite.entity;
			flag = false;
		} else {
			topic = invite.topic;
			entity = topic.entity;
			organization = entity.organization;
		}
		List<User> organizers = Users.getEntityOrganizers(entity);
		organizers.add(organization.creator);

		if (id == 1) {

			if (!flag) {
				if (role.roleName.equals("organizer")) {
					UserRoleInOrganizations.addEnrolledUser(user, organization,
							role, entity.id, "entity");

					for (int c = 0; c < entity.topicList.size(); c++) {
						UserRoleInOrganizations.addEnrolledUser(user,
								organization, role, entity.topicList.get(c).id,
								"topic");
					}

					List<MainEntity> subEntity = new ArrayList<MainEntity>();
					subEntity = MainEntity.find("byParent", entity).fetch();

					if (subEntity.size() != 0) {
						for (int j = 0; j < subEntity.size(); j++) {
							List<User> entityOrganizers = Users
									.getEntityOrganizers(subEntity.get(j));
							if (!entityOrganizers.contains(user)) {
								UserRoleInOrganizations.addEnrolledUser(user,
										organization, role,
										subEntity.get(j).id, "entity");
								for (int s = 0; s < subEntity.get(j).topicList
										.size(); s++) {
									UserRoleInOrganizations
											.addEnrolledUser(user,
													organization, role,
													subEntity.get(j).topicList
															.get(s).id, "topic");
								}
							}

						}
					}

					for (int j = 0; j < organizers.size(); j++)
						Notifications
								.sendNotification(
										organizers.get(j).id,
										entity.id,
										"entity",
										user.firstName
												+ " "
												+ user.lastName
												+ " has been added as an organizer to entity  "
												+ entity.name);

					Log.addUserLog(
							"User "
									+ user.firstName
									+ " "
									+ user.lastName
									+ " has accepted the invitation to be organizer to Entity "
									+ entity.name, entity.organization, user);

				} else {
					/**
					 * * idea devoloper by ibrahim adel
					 */
					role = Roles.getRoleByName("idea developer");
					UserRoleInOrganization roleInOrg = new UserRoleInOrganization(
							user, organization, role);
					roleInOrg._save();
					user.userRolesInOrganization.add(roleInOrg);
					user.save();
					User orgLead = organization.creator;
					Notifications
							.sendNotification(
									user.id,
									organization.id,
									"Organization",
									user.username
											+ " has accepted the invitation to join the "
											+ organization.name);
					Notifications
							.sendNotification(
									orgLead.id,
									organization.id,
									"Organization",
									user.username
											+ " has accepted the invitation to join the "
											+ organization.name);
					List<User> followers = organization.followers;
					for (int j = 0; j < followers.size(); j++) {
						if (followers.get(j).state.equalsIgnoreCase("a")) {
							Notifications.sendNotification(followers.get(j).id,
									organization.id, "Organization",
									user.username + " has join "
											+ organization.name);
						}
					}

				}
			} else {
				UserRoleInOrganizations.addEnrolledUser(user, organization,
						role, topic.id, "topic");
				for (int k = 0; k < organizers.size(); k++)
					Notifications.sendNotification(organizers.get(k).id,
							topic.id, "topic", " A new User " + user.username
									+ " has joined topic " + topic.title);

				Log.addUserLog("User " + user.firstName + " " + user.lastName
						+ " has accepted the invitation to join topic "
						+ topic.title, organization, topic, user);
			}
		}

		if (id == 0) {
			if (!flag) {

				if (entity != null) {

					List<User> organizer = Users.getEntityOrganizers(entity);
					for (int j = 0; j < organizer.size(); j++)
						Notifications
								.sendNotification(
										organizers.get(j).id,
										entity.id,
										"entity",
										"User "
												+ user.firstName
												+ " "
												+ user.lastName
												+ "has rejected the invitation to be an organizer to Entity  "
												+ entity.name);

					Log.addUserLog(
							"User "
									+ user.firstName
									+ " "
									+ user.lastName
									+ " has rejected the invitation to be organizer to Entity "
									+ entity.name, organization, user);
				}

			} else {

				for (int s = 0; s < organizers.size(); s++)
					Notifications
							.sendNotification(
									organizers.get(s).id,
									topic.id,
									"topic",
									" A new User "
											+ user.username
											+ " has rejected the invitation to join topic "
											+ topic.title);
				Log.addUserLog("User " + user.firstName + " " + user.lastName
						+ " has rejected the invitation to join topic "
						+ topic.title, organization, topic, user);
			}
		}

		organization.invitation.remove(invite);
		if (entity != null)
			entity.invitationList.remove(invite);
		if (flag)
			topic.invitations.remove(invite);
		User sender = invite.sender;
		sender.invitation.remove(invite);
		invite.delete();

	}

}
