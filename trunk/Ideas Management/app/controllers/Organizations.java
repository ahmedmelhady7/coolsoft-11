package controllers;

import models.MainEntity;
import models.Organization;
import models.User;

public class Organizations extends CRUD{
	
	
	/**
	 * 
	 * This Method returns the Privacy level of an organization given the organization Id
	 * 
	 * @author 	Fadwa sakr
	 * 
	 * @story 	C2S34
	 * 
	 * @param 	id 	: the id of the organization for which the privacy level is needed
	 *
	 * @return	short
	 */

	 public static short getPrivacyLevel(Long id){
		 Organization organization = Organization.findById(id);
		 if(organization!=null)
			 return organization.privacyLevel;
		return -1;
	 }
	 /**
		 * 
		 * This Method enables the ability of creation of tags in a certain organization
		 * 
		 * @author 	Fadwa sakr
		 * 
		 * @story 	C2S4
		 * 
		 * @param 	id 	: the id of the organization for which the preferences is being enabled
		 *
		 */

	 public static void enableTags(Long id){
		 Organization organization = Organization.findById(id);
		    notFoundIfNull(organization);
			 organization.createTag=true;
	 }
	 /**
		 * 
		 * This Method disables the ability of creation of tags in a certain organization
		 * 
		 * @author 	Fadwa sakr
		 * 
		 * @story 	C2S4
		 * 
		 * @param 	id 	: the id of the organization for which the preferences is being disabled
		 *
		 */
	 public static void disableTags(Long id){
		 Organization organization = Organization.findById(id);
		    notFoundIfNull(organization);
			 organization.createTag=false;
	 }
}
