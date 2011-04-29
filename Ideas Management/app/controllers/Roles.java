package controllers;

import play.*;
import models.*;

public class Roles extends CRUD{

	/*
	 * retrieve the role by name
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param: role is the name of the role
	 * 
	 * @return Role
	 */

	public static Role getRoleByName(String role) {

		return Role.find("byRoleName", role).first();

	}
	
	/**
	 * method that creates the Organizer role
	 * @ author nada ossama
	 * 
	 */
	public static void createOrganizerRole(){
		
		String [] role =  new String []{
			    "invite Organizer or Idea Developer to become Organizer or Idea Developer in an entity he/she manages",
				"block a user from viewing or using a certain entity",
				"Request to start a relationship with other items",
				"Request to end a relationship with other items",
				"Accept/Reject a request to start/end a relationship with other items",
				"Create a sub-entity for entity he/she manages", 
				"Accept/Reject requests to post in a private topic in entities he/she manages",
				"Request to rename relationships that are related to any of the entities or topics he/she manages",
				"Accept/Reject request to rename relationships that are related to any of the entities or topics I am managing",
				"Create a certain topic and specify its type within entities that he/she manages",
				"Receive notifications about any changes or updates within the entities that he/she created or he/she currently manages",
				"Invite a user to join a private or secret organization",
				"Add images and documents to the organization profile",
				"post topics",
				"tag topics",
				"tag ideas in my organization",
				"close a topic and promote it to execution",
				"**delete other ideas",
				"merge ideas",
				"**hide and delete an idea",
				"delete and hide topics that have no ideas posted on them",
				"rate/prioritize ideas",
				"receive notifications about inactive ideas",
				"confirm or reject marking requests",
				"view the list of ideas",
				"create an action plan to execute an idea",
				"delete an action plan",
				"edit an action plan",
				"associate an idea or more to an already existing plan",
				"assign one or many users to a to-do item in a plan",
				"accept/Reject user request to volunteer to work on action item in a plan"};
		Role Organizer  = new Role("organizer" , role);
		Organizer.save();

	}

}
