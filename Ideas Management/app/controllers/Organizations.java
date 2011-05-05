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
	public static void createOrganization(@Required String name,
			String privacyLevel, String createTag) {
		System.out.println("HEEEEEEEE");
		User creator = Security.getConnected();
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			render();
		}
		Organization existing_organization = Organization.find(
				"name like '" + name + "'").first();
		if (existing_organization != null) {
			flash.error("Organization already exists!" + "\n\t\t"
					+ "Please choose another organization name.");
			render();
		}
		int privacyLevell = 0;
		if (privacyLevel.equals("Public")) {
			privacyLevell = 2;
		} else {
			if (privacyLevel.equals("Private")) {
				privacyLevell = 1;
			}
		}
		boolean createTagg = false;
		if (createTag.equals("Yes")) {
			createTagg = true;
		}
		Organization org = new Organization(name, creator, privacyLevell,
				createTagg).save();
		MainEntity m = new MainEntity("Default", "", org);
		m.save();
///////////plz remove
	//	org.enrolledUsers.add(creator);
		flash.success("Your organization has been created.");
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
		} else if (Users.isPermitted(user, "follow", organizationId, "organization")) {
			org.followers.add(user);
			user.followingOrganizations.add(org);
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
		List<Organization> organizations = Organization.findAll();
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
			
			int flag = 0;
			if ((Security.getConnected() ==  org.creator) || (Security.getConnected().isAdmin)){
				flag = 1;
				
			}
			
		

		
		
		
		render(user, org, entities, enrolled, tags ,flag ,b);
		


	}
}