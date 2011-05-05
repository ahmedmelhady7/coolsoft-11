package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;
import models.Invitation;
import models.MainEntity;
import models.Notification;
import models.Organization;
import models.Role;
import models.Topic;
import models.User;
import models.UserRoleInOrganization;
import notifiers.Mail;


@With(Secure.class)
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
	 * @param entt
	 *             the entity id that sends the invitation
	 * 
	 * 
	 * 
	 * @return void
	 */
	public static void invite(long entId) {
         
		//Organization org=Organization.findById(orgId);
        MainEntity ent= MainEntity.findById(entId);      User u=Security.getConnected();
        System.out.println("id"+entId);
		render(ent);

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
	 * @param entt
	 *            the entity that sends the invitation
	 * 
	 * @param name
	 *            the entered user name
	 * 
	 * 
	 * 
	 * @return void
	 */
	public static void SearchUsers(long entId,@Required String name) {

        
       // Organization org=Organization.findById(((long)1));
       // MainEntity ent= MainEntity.findById(((long)1));
     //   Organization org=Organization.findById(entId);
        MainEntity ent= MainEntity.findById(entId);
        User u= Security.getConnected();
        
        
		
		if (validation.hasErrors()) {
			flash.error("Please enter a name first!");
			invite(entId);
		}
         
	 
		List<User> filter = new ArrayList<User>();
		filter = Users.searchUser(name);
		List<User> organizers = Users.getEntityOrganizers(ent);
		organizers.add(ent.organization.creator);

		List<User> users = new ArrayList<User>();
		for (int i = 0; i < filter.size(); i++) {
			if (!organizers.contains(filter.get(i)))
				users.add(filter.get(i));
		}

		render(users, ent);
	}

	/**
	 * 
	 * This method is responsible for rendering the organization, the entity and
	 * the user to the invitation page , if no user selected the view will
	 * enable a text box to enter the email
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * 
	 * @story C1S6
	 * 
	 * 
	 * @param entt
	 *            the entity id that sends the invitation
	 * 
	 * @param id
	 *            the id of the selected user , 0 if inviting by mail
	 * 
	 * 
	 * 
	 * @return void
	 */
	public static void Page(long entId, long id) {
		
	//	Organization org=Organization.findById(orgId);
        MainEntity ent= MainEntity.findById(entId);
		System.out.println(id);
		User user=User.findById(id);
		render(ent,id,user);
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
	 * 
	 * @param entt
	 *              entity id that sends the invitation
	 * 
	 * @return void
	 */


	 public static void send(@Required String email,
			 long entId,long id){
		  
	        
	        //Organization org=Organization.findById(((long)1));
	       // MainEntity ent= MainEntity.findById(((long)1));
	        MainEntity ent= MainEntity.findById(entId);
         
		    if (!rfc2822.matcher(email).matches()) {
			    flash.error("Invalid address");
			    Page(entId,id);
		    }
		
			
			boolean check=false;
			List <Invitation> inv=Invitation.findAll();
			for(int i=0;i<inv.size();i++){
				if(inv.get(i).email.equals(email)&&inv.get(i).entity.equals(ent))
					check=true;
					
			}
			if(check==true){
				flash.error("Invitation has already been sent to that user before");
		        Page(entId,id);
			}
			
			
			List<User> organizers = Users.getEntityOrganizers(ent);
			organizers.add(ent.organization.creator);
			
			if(id==0){
			  	
				boolean flag=true;
				User u=User.find("byEmail", email).first();
			
	    	for (int i = 0; i <organizers.size(); i++) {
				if (organizers.contains(u))
					flag=false;
			}
			
				if(!flag) {
			        flash.error("This user is already an organizer to this entity");
			        Page(entId,id);
			    }
			}
	         
			 String role="Organizer";
		     Mail.invite(email,role,ent.organization.name,ent.name);
		    
	    	 
	    	 User user=Security.getConnected();
	          user.addInvitation(email,role,ent.organization,ent);
	        
	         User receiver=User.find("byEmail", email).first();
	         if(receiver==null){
	       Notifications.sendNotification(receiver.id, ent.id, "organization",
	 					"You have received a new invitation from "
	 							+ ent.name);
	            }


	       User sender = Security.getConnected();
			
					organizers.remove(sender);
					
				
				for(int j=0;j<organizers.size();j++)
				Notifications.sendNotification(organizers.get(j).id, ent.id, "entity",
						"New organizer has been invited to be an organizer of entity  "
								+ ent.name);

			
		       

			 render(email,ent);
		                 
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

	public static void respond(int id,long i) {
		
		System.out.println("HERE");
		
		Invitation invite = Invitation.findById(i);
		String rolename = invite.role.toLowerCase();
		System.out.println(rolename);
		Organization org = invite.organization;
		MainEntity ent = invite.entity;
		System.out.println(ent.name);
		
		if (id == 1) {
			
			User user = User.find("byEmail", invite.email).first();
			Role role = Role.find("byRoleName", rolename).first();
			System.out.println("here"+role.roleName);

	
			List<User> organizers = Users.getEntityOrganizers(ent);
				organizers.add(org.creator);
			
			for(int j=0;j<organizers.size();j++)
			Notifications.sendNotification(organizers.get(j).id, ent.id, "entity",
					"New organizer has been added as an organizer to entity  "
							+ ent.name);
		
                     
			if (rolename.equalsIgnoreCase("organzier")) {

				UserRoleInOrganizations.addEnrolledUser(user, org, role,
						ent.id, "entity");

			} 
			else {
				// idea devoloper by ibrahim adel
				role = Roles.getRoleByName("idea developer");
				UserRoleInOrganization roleInOrg = new UserRoleInOrganization(user,
						org, role);
				roleInOrg._save();
				user.userRolesInOrganization.add(roleInOrg);
				user.save();
				User orgLead = org.creator;
				Notifications.sendNotification(user.id, org.id, "Organization",
						user.username + " has accepted the invitation to join the "
								+ org.name);
				Notifications.sendNotification(orgLead.id, org.id, "Organization",
						user.username + " has accepted the invitation to join the "
								+ org.name);

			}

		}
				     org.invitation.remove(invite);
				     if(ent!=null){
				      ent.invitationList.remove(invite);
				      }
				      User u=invite.sender;
				      u.invitation.remove(invite);
				      invite.delete();

 	}
	
	

}
