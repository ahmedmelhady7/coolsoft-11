package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import play.data.validation.Required;
import play.mvc.Controller;
import models.Invitation;
import models.MainEntity;
import models.Notification;
import models.Organization;
import models.Role;
import models.Topic;
import models.User;
import models.UserRoleInOrganization;
import notifiers.Mail;

public class Invitations extends CRUD {

	// invitation first page ,it has search or button 'invite by mail'
	/**
	 * 
	 * This method is responsible for rendering the organization, the entity to
	 * the invitation home page, that contains search for user to invite or
	 * invite by mail
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * @story C1S6
	 * 
	 * 
	 * @param org
	 *            the organization that sends the invitation
	 * 
	 * @param role
	 *            the entity that sends the invitation
	 * 
	 * 
	 * 
	 * @return void
	 */
	public static void invite(long org,long ent) {
         
         Organization organization=Organization.findById(org);
         MainEntity entity= MainEntity.findById(ent);
		render(organization, entity);

	}

	// Go to search page
	/**
	 * 
	 * This method is responsible for searching for a user to invite the result
	 * is a list not containing the organizers of this entity
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * @story C1S6
	 * 
	 * 
	 * @param org
	 *            the organization that sends the invitation
	 * 
	 * @param role
	 *            the entity that sends the invitation
	 * 
	 * @param name
	 *            the entered user name
	 * 
	 * 
	 * 
	 * @return void
	 */
	public static void SearchUsers(long org,long ent,
			@Required String name) {

		if (validation.hasErrors()) {
			flash.error("Please enter a name first!");
			invite(org, ent);
		}
         
		Organization organization=Organization.findById(org);
        MainEntity entity= MainEntity.findById(ent);
		List<User> filter = new ArrayList<User>();
		filter = Users.searchUser(name);
		List<User> organizers = Users.getEntityOrganizers(entity);
		organizers.add(organization.creator);

		List<User> users = new ArrayList<User>();
		for (int i = 0; i < filter.size(); i++) {
			if (!organizers.contains(filter.get(i)))
				users.add(filter.get(i));
		}

		render(users, entity, organization);
	}

	/**
	 * 
	 * This method is responsible for rendering the organization, the entity and
	 * the user to the invitation page , if no user selected the view will
	 * enable a text box to enter the email
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * @story C1S6
	 * 
	 * 
	 * @param org
	 *            the organization that sends the invitation
	 * 
	 * @param role
	 *            the entity that sends the invitation
	 * 
	 * @param id
	 *            the id of the selected user , 0 if inviting by mail
	 * 
	 * 
	 * 
	 * @return void
	 */
	public static void Page(long org, long ent, long id) {
		// long num=id;
		// if(id==0)
		// render(org,ent);
		// else
		Organization organization=Organization.findById(org);
        MainEntity entity= MainEntity.findById(ent);
		render(organization, entity, User.findById(id));
	}

	/**
	 * 
	 * This method is responsible for adding a new invitation and rendering the
	 * email then the Mail class sends the email, if sending to registered user
	 * he ll be notified with a notification
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * @story C1S6
	 * 
	 * 
	 * @param email
	 *            destination of the invitation
	 * 
	 * @param role
	 *            role that ll be assigned to the user in case accepted
	 * 
	 * @param organization
	 *            organization that sends the invitation
	 * 
	 * @param entity
	 *            entity that sends the invitation
	 * 
	 * @return void
	 */


	 public static void send(@Required String email,String role,long org,
			 long ent,long id){
		 
		    if (!rfc2822.matcher(email).matches()) {
			    flash.error("Invalid address");
			    Page(org,ent,id);
		    }
			if(role.equalsIgnoreCase("select")) {
			        flash.error("Please choose a Role");
			        Page(org,ent,id);
			    }
			Organization organization=Organization.findById(org);
	         MainEntity entity= MainEntity.findById(ent);
	         
		     Mail.invite(email,role,organization.name,entity.name);
		    
	    	 
	    	 User user=Security.getConnected();
	          user.addInvitation(email,role,organization,entity);
	        
	         User receiver=User.find("byEmail", email).first();
	         if(!receiver.equals(null)){
	        	 List<User> u=new ArrayList<User>();
	        	  u.add(receiver);
	        	//if(role.equalsIgnoreCase("organizer"))
	        	 Notifications.sendNotification(u, organization.id, "organization",
	 					"You have received a new invitation from "
	 							+ organization.name);
	            }

				//**fadwa
				List<User> organizers = Users.getEntityOrganizers(entity);
				if (!user.equals(organization.creator)) {
					organizers.remove(user);
					organizers.add(organization.creator);
				}
				Notifications.sendNotification(organizers, entity.id, "entity",
						"Invitation has been sent from entity "+ entity.name);
		                
		        //**
				
			 render(email);
		                 
	  }
	 private static final Pattern rfc2822 = Pattern.compile(
		        "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
		);
	 

	/**
	 * 
	 * This method is responsible for viewing the received invitations It
	 * renders the invitation list
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * @story C1S4
	 * 
	 * @param
	 * 
	 * 
	 * @return void
	 */

	public static void view() {

		User user = Security.getConnected();
		List<Invitation> inv = Invitation.find("byEmail", user.email).fetch();
		render(inv);

	}

	/**
	 * 
	 * This method is responsible for responding to the user (accept/reject) to
	 * the invitation It renders the invitation list and the id (0/1) and the
	 * invitation id
	 * 
	 * 
	 * @author ${Mai.Magdy} > if role is organizer
	 * 
	 * @story C1S4
	 * 
	 * @param choice
	 *            0 if reject or 1 if accept
	 * 
	 * @param id
	 *            the invitation id
	 * 
	 * 
	 * @return void
	 */

	public static void respond(int choice, long id) {

		Invitation invite = Invitation.findById(id);
		if (choice == 1) {
			String rolename = invite.role;
			Organization org = invite.organization;
			MainEntity ent = invite.entity;
			User user = User.find("byEmail", invite.destination).first();
			Role role = Role.find("byName", rolename).first();

			// *fadwa
			List<User> organizers = Users.getEntityOrganizers(ent);
			if (!invite.sender.equals(org.creator)) {
				organizers.remove(invite.sender);
				organizers.add(org.creator);
			}
			Notifications.sendNotification(organizers, ent.id, "entity",
					"New organizer has been added as an organizer to entity  "
							+ ent.name);
			// *

			if (rolename.equalsIgnoreCase("organzier")) {

				UserRoleInOrganizations.addEnrolledUser(user, org, role);
				UserRoleInOrganizations.addEnrolledUser(user, org, role,
						ent.id, "entity");

			} else {
				// idea devoloper by ibrahim adel
				Role role2 = Role.find("byRoleName", "Idea Developer").first();
				if (role2 == null) {
					// role ???
					role2 = new Role("Idea Developer", "");
					role2._save();
				}
				UserRoleInOrganization roleInOrg = new UserRoleInOrganization(
						user, org, role2);
				roleInOrg._save();
				user.userRolesInOrganization.add(roleInOrg);
				Notification n1 = new Notification("Invitation accepted",
						invite.sender, user.username
								+ " accepted th invitation");
				n1._save();
				User orgLead = org.creator;
				if (orgLead.id != invite.sender.id) {
					Notification n2 = new Notification("Invitation accepted",
							orgLead, user.username + " accepted th invitation");
					n2._save();
				}

			}

		}

		invite.delete();

	}

}
