/**
 * @author Mai Magdy
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
//import com.mysql.jdbc.log.Log;

@With(Secure.class)
public class Invitations extends CRUD {

	/**
	 * 
	 * Renders the entity/topic to the invitation home page that contains search
	 * for user to invite or invite by mail
	 * 
	 * @author ${Mai.Magdy} , ${Fadwa.Sakr}
	 * 
	 * @story C1S6,C2S22
	 * 
	 * @param type
	 *            type either entity (0) or topic(1)
	 * 
	 * @param id
	 *            long id of entity/topic that sends the invitation
	 * 
	 */
	public static void invite(int type, long id) {
		System.out.println(type);
		if (type == 0) {
			MainEntity entity = MainEntity.findById(id);
			render(type, entity);
		} else {
			Topic topic = Topic.findById(id);
			notFoundIfNull(topic);
			render(type, topic);
		}

	}

	/**
	 * 
	 * Searches for a user to invite. The result is a list not containing the
	 * organizers of this entity
	 * 
	 * @author ${Mai.Magdy}, ${Fadwa.Sakr}
	 * 
	 * @story C1S6,C2S22
	 * 
	 * 
	 * @param type
	 *            type either entity (0) or topic(1)
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
		List<User> users = new ArrayList<User>();
		List<User> postsInTopic = new ArrayList<User>();

		System.out.println("type :" + type);
		System.out.println(id);
		System.out.println(name);

		if (validation.hasErrors()) {
			flash.error("Please enter a name first!");
			invite(type, id);
		}

		if (type == 0) {
			MainEntity entity = MainEntity.findById(id);
			List<User> organizers = Users.getEntityOrganizers(entity);
			organizers.add(entity.organization.creator);
			for (int i = 0; i < filter.size(); i++) {
				if (!organizers.contains(filter.get(i))
						|| filter.get(i).isAdmin)
					users.add(filter.get(i));
			}

		} else {
			System.out.println(users);
			postsInTopic = Topics.searchByTopic(id);
			System.out.println(filter);
			System.out.println(postsInTopic);
			for (int i = 0; i < filter.size(); i++) {
				if (!postsInTopic.contains(filter.get(i))) {
					System.out.println(postsInTopic.contains(filter.get(i)));
					users.add(filter.get(i));
				}
			}
			System.out.println(users);
		}
		JsonObject json = new JsonObject();
		String names = "";
		String ids = "";
		String emails = "";
		for (int i = 0; i < users.size(); i++) {
			names = names + users.get(i).username + ",";
			ids = ids + users.get(i).id + ",";
			emails = emails + users.get(i).email + ",";
		}

		json.addProperty("type", type);
		json.addProperty("names", names);
		json.addProperty("ids", ids);
		json.addProperty("emails", emails);
		if (type == 0)
			json.addProperty("entity", id);
		else
			json.addProperty("topic", id);
		renderJSON(json.toString());
	}

	/**
	 * 
	 * Renders the organization, the entity and the user to the invitation page
	 * , if no user selected the view will enable a text box to enter the email
	 * 
	 * @author ${Mai.Magdy},${Fadwa.Sakr}
	 * 
	 * 
	 * @story C1S6,C2S22
	 * 
	 * @param type
	 *            type either entity (0) or topic(1)
	 * 
	 * @param id
	 *            long id of entity/topic that sends the invitation
	 * 
	 * @param userId
	 *            long id of the selected user to send the invitation to , 0 if
	 *            inviting by mail
	 * 
	 */
	public static void page(int type, long id, long userId) {
		System.out.println("YA RAAAB");
		User user = User.findById(userId);
		System.out.println("typoo" + type);
		System.out.println(userId);
		System.out.println(user);
		if (type == 0) {
			System.out.println(id);
			MainEntity entity = MainEntity.findById(id);

			render(type, entity, userId, user);
			System.out.println("mai");
		} else {
			Topic topic = Topic.findById(id);
			render(type, topic, userId, user);
		}
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
	 *            type either entity (0) or topic(1)
	 * 
	 * @param email
	 *            String email of the destination of the invitation
	 * 
	 * 
	 * @param id
	 *            long id of entity/topic that sends the invitation
	 * 
	 * @param userId
	 *            long userId that the invitation is to be send to
	 * 
	 */

	public static void send(int type, @Required String email, long id,
			long userId) {
		System.out.println("here");
		System.out.println(email);
		System.out.println(userId);
		System.out.println();

		MainEntity entity = null;
		Topic topic = null;
		String role = "";
		String name;
		List<User> users = new ArrayList<User>();
		if (type == 0) {
			entity = MainEntity.findById(id);
			role = "Organizer";
			users = Users.getEntityOrganizers(entity);
			name = entity.name;
			users.add(entity.organization.creator);
		} else {
			topic = Topic.findById(id);
			role = "ideadeveloper";
			users = Topics.searchByTopic(id);
			name = topic.title;
		}
		System.out.println(topic);
		System.out.println(role);
		System.out.println(users);
		System.out.println(name);

		if (!rfc2822.matcher(email).matches()) {
			System.out.println("3'lat!!");
			flash.error("Invalid address");
			page(type, id, userId);
		}

		System.out.println("fadwa");
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
			page(type, id, userId);
		}

		boolean isRegistered = false;
		User reciever = null;
		if (id == 0) {

			boolean flag = true;
			isRegistered = true;
			reciever = User.find("byEmail", email).first();
			if (reciever != null) {

				System.out.println("CONDITION");
				for (int i = 0; i < users.size(); i++) {
					if (users.contains(reciever) || reciever.isAdmin)
						flag = false;
				}

				if (!flag) {
					if (!entity.equals(null))
						flash.error("This user is already an organizer to this entity");
					else
						flash.error("This user is already an idea developer in that topic");
					page(type, id, userId);
				}

				if (reciever.state.equals("n") || reciever.state.equals("d")) {
					flash.error("This user is not available");
					page(type, id, userId);
				}

			}
		}
		if (!isRegistered)
			reciever = User.find("byEmail", email).first();
		User user = Security.getConnected();
		users.remove(user);
		Mail.invite(email, role, entity.organization.name, name,type);
		if (type==0) {
			user.addInvitation(email, role, entity.organization, entity, null);
			for (int j = 0; j < users.size(); j++)
				Notifications.sendNotification(users.get(j).id, entity.id,
						"entity",
						"New organizer has been invited to be an organizer to entity  "
								+ name);
		} else {
			Organization organization = topic.entity.organization;
			user.addInvitation(email, role, entity.organization, null, topic);
			for (int j = 0; j < users.size(); j++)
				Notifications.sendNotification(users.get(j).id, topic.id,
						"topic",
						"New organizer has been invited to be an Idea developer topic  "
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
			if (!entity.equals(null))
				Notifications.sendNotification(reciever.id, entity.id,
						"organization",
						"You have received a new invitation from " + name);
			else
				Notifications.sendNotification(reciever.id, id, "topic",
						"You have received a new invitation from " + name);
		}

		flash.error("Invitation has been sent successfuly");
		invite(type, id);

	}

	private static final Pattern rfc2822 = Pattern
			.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");

	/**
	 * 
	 * Views the received invitations and renders the invitation list
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * @story C1S4
	 * 
	 */

	public static void view() {

		User user = Security.getConnected();
		List<Invitation> invitation = Invitation.find("byEmail", user.email)
				.fetch();

		render(invitation);

	}

	/**
	 * 
	 * Responds to the user (accept/reject) to the invitation.
	 * 
	 * 
	 * 
	 * @author ${Mai.Magdy} > if flag is false and role is organizer
	 * @author ${Fadwa.Sakr} > if flag is true
	 * @author ${Ibrahim.Khayat} > if flag is false and role is idea developer
	 * 
	 * @story C1S4,C2S22
	 * 
	 * 
	 * @param id
	 *            int id represents the clicked button, 0 if reject or 1 if
	 *            accept
	 * 
	 * @param i
	 *            long id of the invitation
	 * 
	 */

	public static void respond(int id, long invId) {

		Invitation invite = Invitation.findById(invId);
		String roleName = invite.role.toLowerCase();
		Organization organization = null;
		MainEntity entity = null;
		Topic topic = null;
		User user = User.find("byEmail", invite.email).first();
		Role role = Role.find("byRoleName", roleName).first();
		boolean flag = true;
		if (invite.topic != null) {
			organization = invite.organization;
			entity = invite.entity;
			flag=false;
		} else {
			topic = invite.topic;
			entity = topic.entity;
			organization = entity.organization;
		}
		List<User> organizers = Users.getEntityOrganizers(entity);
		organizers.remove(invite.sender);
		organizers.add(organization.creator);

		if (id == 1) {

			if (!flag) {
				if (role.roleName.equals("organizer")) {
					UserRoleInOrganizations.addEnrolledUser(user, organization,
							role, entity.id, "entity");

					List<MainEntity> sub = new ArrayList<MainEntity>();
					sub = MainEntity.find("byParent", entity).fetch();

					if (sub.size() != 0) {
						for (int j = 0; j < sub.size(); j++) {
							List<User> entityOrganizers = Users
									.getEntityOrganizers(sub.get(j));
							if (!entityOrganizers.contains(user))
								UserRoleInOrganizations.addEnrolledUser(user,
										organization, role, sub.get(j).id,
										"entity");

						}
					}
					// **Fadwa

					for (int j = 0; j < organizers.size(); j++)
						Notifications.sendNotification(organizers.get(j).id,
								entity.id, "entity",
								"New organizer has been added as an organizer to entity  "
										+ entity.name);
					// **

				} else {
					// * idea devoloper by ibrahim adel
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
				for (int i = 0; i < organizers.size(); i++)
					Notifications.sendNotification(organizers.get(i).id,
							topic.id, "topic", " A new User " + user.username
									+ " has joined topic " + topic.title);
				
				Log.addUserLog("User " + user.firstName + " " + user.lastName
						+ " has accepted the invitation to join topic "
						+ topic.title, organization, topic, user);
				
			}
		}

		// **
          if(!flag){
		organization.invitation.remove(invite);
			entity.invitationList.remove(invite);
		}
          else{
        	  topic.invitations.remove(invite);
        	  for (int i = 0; i < organizers.size(); i++)
					Notifications.sendNotification(organizers.get(i).id,
							topic.id, "topic", " A new User " + user.username
									+ " has rejected the invitation to join topic " + topic.title);
        	  Log.addUserLog("User " + user.firstName + " " + user.lastName
						+ " has rejected the invitation to join topic "
						+ topic.title, organization, topic, user);
          }
		User sender = invite.sender;
		sender.invitation.remove(invite);
		invite.delete();

	}

}
