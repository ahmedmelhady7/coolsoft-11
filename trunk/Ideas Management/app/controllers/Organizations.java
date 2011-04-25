package controllers;

import models.MainEntity;
import models.Organization;
import models.User;

public class Organizations extends CRUD{
	
	/**
	 * This Method removes a user from the list of followers in an organization
	 * 
	 * @author 	Ibrahim.al.khayat
	 * 
	 * @story 	C2S12
	 * 
	 * @param   org     : the organization the user is following
	 * 
	 * @param  	user 	: the user who follows
	 * 
	 * @return	void
	 */
	
	public static void unfollow(Organization org, User user) {
		org.unfollow(user);
	}
	/**
	 * 
	 * This Method returns the Privacy level of an organization given the organization Id
	 * 
	 * @author 	Fadwa
	 * 
	 * @story 	C2S34
	 * 
	 * @param 	organizationId 	: the id of the organization for which the privacy level is needed
	 *
	 * @return	short
	 */

	 public short getPrivacyLevel(Long id){
		 Organization organization = Organization.findById(id);
		 if(organization!=null)
			 return organization.privacyLevel;
		return -1;
	 }
}
