package controllers;

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
import controllers.CRUD.ObjectType;

import models.Invitation;
import models.MainEntity;
import models.Organization;
import models.Role;
import models.Tag;
import models.Topic;
import models.User;

@With(Secure.class)
public class Organizations extends CRUD {

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
	 * @return short
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
		System.out.println(organization.createTag);
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
		System.out.println(organization.createTag);
	}

	/**
	 * This method gets the list of topics of a certain organization
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S28
	 * 
	 * @param orgId
	 *            : ID of an organization of type long
	 */

	public static void getTopics(long id) {
		Organization org = Organization.findById(id);
		notFoundIfNull(org);
		ArrayList<Topic> topics = new ArrayList<Topic>();
		int i = 0;
		while (i < org.entitiesList.size()) {
			int j = 0;
			while (j < org.entitiesList.get(i).topicList.size()) {
				topics.add(org.entitiesList.get(i).topicList.get(j));
				j++;
			}
			i++;
		}
		render(topics);
	}

	/**
	 * This method renders the page for inviting a user to organization
	 * 
	 * @author ibrahim.al.khayat
	 * 
	 * @story C2S26
	 * 
	 * @param orgId
	 *            the id of the organization
	 * 
	 * @param userId
	 *            the id of the user who will invite
	 * 
	 * @return void
	 */

	public static void InviteMember(long orgId) {
		long userId = Security.getConnected().id;
		render(orgId, userId);
	}

	/**
	 * This method creates an invitation to a user to join an organization and
	 * sends it to a user
	 * 
	 * @author ibrahim.al.khayat
	 * 
	 * @story C2S26
	 * 
	 * @param orgId
	 *            the id of the organization
	 * 
	 * @param email
	 *            the email of the receiver
	 * 
	 * @param userId
	 *            the id of the user
	 * 
	 * @return void
	 */

	public static void sendInvitation(long orgId,
			@play.data.validation.Email String email) {
		if (!Validation.hasError(email)) {
			Organization org = Organization.findById(orgId);
			User sender = Security.getConnected();
			Invitation inv = new Invitation(email, null, org, "Idea Developer",
					sender);
			inv._save();
			User rec = User.find("byEmail", email).first();
			if (rec != null) {
				rec.invitation.add(inv);
				rec._save();
			}
			Mail.invite(email, "Idea Devoloper", org.name, "");
		}
	}

	/**
	 * This method creates a new Organization
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S1
	 * 
	 * @param name
	 *            : name of the organization
	 * 
	 * @param creator
	 *            : creator of the organization
	 * 
	 * @param privacyLevel
	 *            : whether the organization is public, private or secret
	 * 
	 * @param createTag
	 *            : whether the users in that organization are allowed to create
	 *            tags
	 */

	public static void createOrganization() {
		render();
//		if (validation.hasErrors()) {
//			params.flash();
//			validation.keep();
//			render();
//		}
		
	}
	public static void createOrg(String name, String privacyLevel, String createTag) {

		User creator = Security.getConnected();
		
//		Organization existing_organization = Organization.find(
//				"name like '" + name + "'").first();
		List<Organization> allOrganizations = Organization.findAll();
		boolean duplicate = false;
		int i = 0;
		while(i < allOrganizations.size()){
			if(allOrganizations.get(i).name.equalsIgnoreCase(name)) {
				duplicate = true;
				break;
			}
			i++;
		}
		if (!duplicate) {
		
		int privacyLevell = 0; 
		if(privacyLevel.equalsIgnoreCase("Public")) {
			privacyLevell = 2;
		}
		else{
			if(privacyLevel.equalsIgnoreCase("Private")) {
				privacyLevell = 1;
			}
		}
		boolean createTagg = false;
		if(createTag.equalsIgnoreCase("Yes")) {
			createTagg = true;
		}
		Organization org = new Organization(name, creator, privacyLevell,
				createTagg).save();
		Role r = Roles.getRoleByName("organizationLead");
		UserRoleInOrganizations.addEnrolledUser(creator, org, r);
		MainEntity m = new MainEntity("Default","",org);
		m.save();

		
		}

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
	 * @param user
	 *            : The user who wants to follow an organization
	 */

	public static void viewFollowers(long organizationId, String f) {
		Organization org = Organization.findById(organizationId);
		if (f.equals("true")) {
			followOrganization(organizationId);
		}
		render(org);
	}

	public static void followOrganization(long organizationId) {
		User user = Security.getConnected();
		Organization org = Organization.findById(organizationId);
		if(org.followers.contains(user)) {
			System.out.println("You are already a follower");
		} else if (Users.isPermitted(user,
				"can follow organization/entities/topics", organizationId, "organization")) {
			org.followers.add(user);
			org.save();
			user.followingOrganizations.add(org);
			user.save();
		} else {
			System.out.println("Sorry! Action cannot be performed");
		}
	}

	/**
	 * This method render a view that gets all organizations on the system with
	 * the ability to go to the create organization page
	 * 
	 * @author Omar Faruki
	 */
	public static void mainPage() {
		User user = Security.getConnected();
		List<Organization> organizations = new ArrayList<Organization>();
		List<Organization> allOrganizations = Organization.findAll();
		int i = 0;
		while(i < allOrganizations.size()){
			if(Users.getEnrolledUsers(allOrganizations.get(i)).contains(user)) {
				organizations.add(allOrganizations.get(i));
			}
			i++;
		}
		render(user, organizations);
	}

	/**
	 * This method render the main Profile Page of a specific organization
	 * 
	 * @author Omar Faruki
	 * 
	 * @param id
	 */
	public static void viewProfile(long id) {
		User user = Security.getConnected();
		Organization org = Organization.findById(id);
		List<Tag> tags = org.createdTags;
		List<Tag> allTags = Tag.findAll();
		int i = 0;
		int allowed=0;
		if(Users.isPermitted(user,  "accept/reject join requests from users to join a private organization", id, "organization"))
			allowed=1;
		boolean loop = false;
		if (tags.isEmpty()) {
			while (i < allTags.size()) {
				if (allTags.get(i).createdInOrganization.privacyLevel == 2) {
					tags.add(allTags.get(i));
					loop = true;
				}
				i++;
			}
		}
		if (loop == false) {
			while (i < allTags.size()) {
				if (!tags.contains(allTags.get(i))
						&& (allTags.get(i).createdInOrganization.privacyLevel == 2)) {
					tags.add(allTags.get(i));
				}
				i++;
			}
		}
			List<MainEntity> entities = org.entitiesList;
			boolean enrolled = false;
	//plzzzzzz remove		
//			if (org.enrolledUsers.contains(user)) {
//				enrolled = true;
//			}
			int b = 0;
			if (Users.isPermitted(user,
					"Invite a user to join a private or secret organization",
					org.id, "organization")
					&& org.privacyLevel != 2) {
				b = 1;
			
		}


		if(Users.getEnrolledUsers(org).contains(user)) {
			enrolled = true;
		}
		boolean requestToJoin = false;
		if((enrolled == false)&&(org.privacyLevel == 1)) {
			requestToJoin = true;
		}					
			int flag = 0;
			if ((Security.getConnected() ==  org.creator) || (Security.getConnected().isAdmin)){
				flag = 1;		
			}
			boolean admin = user.isAdmin;
			render(user, org, entities, requestToJoin, tags ,flag , b, admin,allowed);

}
	public static void viewAllOrganizations() {
		List<Organization> allOrganizations = Organization.findAll();
		List<Organization> organizations = new ArrayList<Organization>();
		User user = Security.getConnected();
		boolean admin = user.isAdmin;
		if(admin) {
			int i = 0;
			while(i < allOrganizations.size()) {
				organizations.add(allOrganizations.get(i));
				i++;
			}
		}
		else {
			int i = 0;
			while(i < allOrganizations.size()) {
				if ((allOrganizations.get(i).privacyLevel != 0)) {
					organizations.add(allOrganizations.get(i));
				}
				else {
					if (Users.getEnrolledUsers(allOrganizations.get(i)).contains(user)) {
						organizations.add(allOrganizations.get(i));
					}
				}
				i++;
			}
		}
		render(organizations);
	}
}