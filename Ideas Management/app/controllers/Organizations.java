package controllers;

import java.util.ArrayList;
import java.util.List;

import play.db.Model;
import play.exceptions.TemplateNotFoundException;
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

	public static short getPrivacyLevel(long id) {

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
     * @param orgId : ID of an organization of type long
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
	 *            the id of the user
	 * 
	 * @return void
	 */

	public static void InviteMember(long orgId, long userId) {
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
	 * 			  the email of the receiver
	 * 
	 * @param userId
	 *            the id of the user
	 * 
	 * @return void
	 */

	public static void sendInvitation(long orgId, String email, long userId) {
		Organization org = Organization.findById(orgId);
		User sender = User.findById(userId);
		Invitation inv = new Invitation(email, null, org, "member", sender);
		inv._save();
		User rec = User.find("byEmail", email).first();
		if (rec != null) {
			rec.invitation.add(inv);
		}
		// send the invitation

	}

}