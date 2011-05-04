package controllers;

import java.util.ArrayList;
import java.util.List;

import notifiers.Mail;

import play.data.validation.Required;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Controller;
import controllers.CRUD.ObjectType;

import models.Invitation;
import models.MainEntity;
import models.Organization;
import models.Topic;
import models.User;

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
		Organization organization = Organization.findById(id);
		notFoundIfNull(organization);
		organization.createTag = true;
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
		Organization organization = Organization.findById(id);
		notFoundIfNull(organization);
		organization.createTag = false;
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

	public static void sendInvitation(long orgId, String email, long userId) {
		Organization org = Organization.findById(orgId);
		User sender = User.findById(userId);
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
	public static void createOrganization(@Required String name, User creator,
			int privacyLevel, boolean createTag) {
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			render(creator);
		}
		Organization existing_organization = Organization.find(
				"name like '" + name + "'").first();
		if (existing_organization != null) {
			flash.error("Organization already exists!" + "\n\t\t"
					+ "Please choose another organization name.");
			render(creator);
		}
		Organization org = new Organization(name, creator, privacyLevel,
				createTag).save();
		MainEntity m = new MainEntity("Default","",org);
		m.save();
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

	public void followOrganization(long organizationId) {
		User user = Security.getConnected();
		Organization org = Organization.findById(organizationId);
		if (Users.isPermitted(user, "follow", organizationId, "organization")) {
			org.followers.add(user);
			user.followingOrganizations.add(org);
		} else {
			System.out.println("Sorry! Action cannot be performed");
		}
	}

	public static void mainPage(){
		User user = Security.getConnected();
		List<Organization> organizations = Organization.findAll();
		render(user, organizations);
	}
	
	public static void viewProfile(long id) {
		User user = Security.getConnected();
		Organization org = Organization.findById(id);
		List<MainEntity> entities = org.entitiesList;
		render(user, org, entities);
	}
}