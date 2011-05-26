

import notifiers.Mail;

import org.junit.*;

import controllers.Invitations;
import controllers.Notifications;
import controllers.Organizations;
import controllers.Roles;
import controllers.Security;
import controllers.Topics;
import controllers.UserRoleInOrganizations;
import controllers.Security;
import controllers.Users;

import java.util.*;

import play.mvc.Controller;
import play.test.*;
import models.*;

public class IbrahimAdel extends UnitTest {

	@Before
	public void setup() {
		Fixtures.deleteAll();
	}

	@Test
	public void inviteToJoinOrganizationAndReceive() {
		Role.createIdeaDeveloperRole();
		Role.createOrganizationLeadRole();
		Role OrganizationLead = Roles.getRoleByName("organizationLead");

		User sharaf = new User("sharaf@eg.gov", "sharaf", "1234", "Asam",
				"Sharaf", "What is our company's name?", "coolsoft", 0,
				"2/14/1995", "Egypt", "Primenister").save();

		Organization gov = new Organization("الحكومة المصرية", sharaf, 0, true,
				"").save();

		UserRoleInOrganizations.addEnrolledUser(sharaf, gov, OrganizationLead);

		User mai = new User("mai.jt4@gmail.com", "mai", "1234", "Mai", "Magdy",
				"What is our company's name?", "coolsoft", 0, "2/14/1995",
				"Egypt", "student").save();
		// had to copy the method body since it contains getConnected
		long organizationId = gov.id;
		String email = mai.email;

		User reciever = User.find("byEmail", email).first();
		if (reciever != null) {
			if (reciever.state.equalsIgnoreCase("d")
					|| reciever.state.equalsIgnoreCase("n")) {
				return;
			}
		}
		Organization organization2 = Organization.findById(organizationId);
		User sender = sharaf;
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
		Invitation invitation = new Invitation(email, null, organization2,
				"Idea Developer", sender, null);
		invitation._save();
		sender.invitation.add(invitation);
		sender.save();
		if (reciever != null) {
			Notifications.sendNotification(reciever.id, organizationId,
					"organization", "you have recived an ivitation to join"
							+ organization2.name);
		}
		try {
			Mail.invite(email, "Idea Devoloper", organization2.name, "", 0);
		} catch (Exception e) {

		}

		assertEquals(false, sharaf.invitation.isEmpty());
		Invitation invitation2 = Invitation
				.findById(sharaf.invitation.get(0).id);
		assertEquals(true, invitation2.organization.equals(gov));

		// accept the invitation
		assertEquals(false,
				Users.isPermitted(mai, "view", gov.id, "organization"));
		// received a notification
		assertEquals(1, mai.notificationsNumber);

		// Invitations.respond(1, invitation2.id); can not use since contains
		// session
		// method body
		int id = 1;
		long i = invitation2.id;

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
		User sender2 = invite.sender;
		sender2.invitation.remove(invite);
		invite.delete();

		// joined
		assertEquals(true,
				Users.isPermitted(mai, "view", gov.id, "organization"));
		// invitation is deleted
		assertEquals(true, Invitation.count() == 0);
		// another notification is received
		assertEquals(2, mai.notificationsNumber);

	}

	@Test
	public void unfollowTest() {

		User ashraf = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234",
				"Ashraf", "Mansoor", "What is our company's name?", "coolsoft",
				0, "2/14/1995", "Egypt", "student").save();
		// org
		Organization guc = new Organization("GUC", ashraf, 1, true,
				"The German University in Cairo").save();

		// tag
		Tag tagEducation = new Tag("Education", guc, ashraf).save();

		// entity
		MainEntity gucMet = new MainEntity("MET",
				"Media Engineering and technology", guc, true).save();

		Topic gucMetStudentUnion = new Topic("Student union", "Suggestions", 2,
				ashraf, gucMet, true).save();
		gucMetStudentUnion._save();

		// follow
		ashraf.followingOrganizations.add(guc);
		guc.followers.add(ashraf);

		ashraf.followingEntities.add(gucMet);
		gucMet.followers.add(ashraf);

		ashraf.followingTags.add(tagEducation);
		tagEducation.followers.add(ashraf);

		ashraf.topicsIFollow.add(gucMetStudentUnion);
		gucMetStudentUnion.followers.add(ashraf);

		ashraf.save();
		tagEducation.save();
		gucMetStudentUnion.save();
		gucMet.save();
		guc.save();

		// unfollow can not use the controller method since it uses session
		ashraf.unfollow(gucMet);
		gucMet.unfollow(ashraf);
		assertFalse(ashraf.followingEntities.contains(gucMet));

		ashraf.unfollow(gucMetStudentUnion);
		gucMetStudentUnion.unfollow(ashraf);
		assertFalse(ashraf.topicsIFollow.contains(gucMetStudentUnion));

		ashraf.unfollow(guc);
		guc.unfollow(ashraf);
		assertFalse(ashraf.followingOrganizations.contains(guc));

		ashraf.unfollow(tagEducation);
		tagEducation.unfollow(ashraf);
		assertFalse(ashraf.followingTags.contains(tagEducation));

	}

	@Test
	public void RequestToPostOnATopic() {
		Role.createIdeaDeveloperRole();
		Role.createOrganizationLeadRole();
		Role OrganizationLead = Roles.getRoleByName("organizationLead");
		
		User khayat = new User("ibrahim.al.khayat@gmail.com", "ialk",
				"1234", "Ibrahim", "EL-khayat", "What is our company's name?", "coolsoft",
				0, "2/14/1995",
				"Egypt", "student");
		khayat._save();
		
		User ashraf = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234",
				"Ashraf", "Mansoor", "What is our company's name?", "coolsoft",
				0, "2/14/1995", "Egypt", "student").save();

		Organization guc = new Organization("GUC", ashraf, 1, true,
				"The German University in Cairo").save();

		MainEntity gucMet = new MainEntity("MET",
				"Media Engineering and technology", guc, true).save();

		Topic gucMetStudentUnion = new Topic("Student union", "Suggestions", 2,
				ashraf, gucMet, true).save();

		gucMetStudentUnion._save();
		
		UserRoleInOrganizations.addEnrolledUser(ashraf, guc,
				OrganizationLead);
		
		Role ideadeveloper = Roles.getRoleByName("idea developer");
		
		
		ideadeveloper = Roles.getRoleByName("idea developer");
		UserRoleInOrganization roleInOrg = new UserRoleInOrganization(
				khayat, guc, ideadeveloper);
		roleInOrg._save();
		khayat.userRolesInOrganization.add(roleInOrg);
		khayat.save();
		
		
		assertEquals(false, Users.isPermitted(khayat, "use", gucMetStudentUnion.id, "topic"));
		assertEquals(true, Users.isPermitted(khayat, "view", gucMetStudentUnion.id, "topic"));
		
		gucMetStudentUnion.requestFromUserToPost(khayat.id);
	
		
	}

}
