package controllers;

import java.util.ArrayList;
import java.util.List;

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


	
	
	
    //invitation first page ,it has search or button 'invite by mail'
 public static void invite(Organization org,MainEntity ent){
	   
	 render(org,ent);
	   
 }
   
    //Go to search page
 public static void SearchUsers(Organization org,MainEntity ent,String name){
	 List<User> filter=new ArrayList<User>();
	   filter=Users.searchUser(name);
	 List<User> organizers=Users.getEntityOrganizers(ent);
	 organizers.add(org.creator);
	 
	 List <User> users=new ArrayList<User>();
	  for(int i=0;i<filter.size();i++){
		  if(!organizers.contains(filter.get(i)))
			  users.add(filter.get(i));
	  }
	  
	    render(users,ent,org);
 }
 
 
    // page of invitation
   public static void Page(Organization org,MainEntity ent,long id){
	  // long num=id;
	     if(id==-1)
	          render(org,ent);
	  else
		  render(org,ent,User.findById(id));
 }
   

 
 /**
	 * 
	 * This method is responsible for sending an invitation
	 * It renders the email
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * @story C1S6
	 *
	 * 
	 * @param  email
	 *                 destination of the invitation
	 *                 
	 * @param  role
	 *               role that ll be assigned to the user in case accepted
	 *               
	 *@param  organization
	 *                organization that sends the invitation
	 *               
	 *@param  entity
	 *               entity that sends the invitation
	 *                                
	 * @return void
	 */
 
 public static void send(String email,String role,Organization org,
		 MainEntity ent){
	  
	 Mail.invite(email,role,org.name,ent.name);
	    
    	 
    	//sender=the user from the session 
        //sender.addInvitation(email,role,org,ent,top);
    	
	  //render(email,role,organization,entity,topic);
	    render(email);
  }
 
 /**
	 * 
	 * This method is responsible for viewing the received invitations
	 * It renders the invitation list
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
 
 public static void view(User user){
		
	   //**User user=get user from session
       List <Invitation> inv = Invitation.find("byEmail", user.email).fetch();
       render(inv);

	}

	/**
	 * 
	 * This method is responsible for responding to the user (accept/reject) to
	 * the invitation It renders the invitation list and the id (0/1) and the
	 * number of the invitation
	 * 
	 * 
	 * @author ${Mai.Magdy}
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

	public static void respond(int choice, long id, User user) {

		// User user=get user from session
		
		Invitation invite = Invitation.findById(id);
		if (choice == 1) {
			String rolename = invite.role;
			Organization org = invite.organization;
			MainEntity ent = invite.entity;
			Role role = Role.find("byName", rolename).first();
		
			
			//*fadwa
			List<User> organizers = Users.getEntityOrganizers(ent);
			if (!invite.sender.equals(org.creator)) {
				organizers.remove(invite.sender);
				organizers.add(org.creator);
			}
			Notifications.sendNotification(organizers, ent.id, "entity",
					"New organizer has been added as an organizer to entity  "
							+ ent.name);
			//*
			

			if (rolename.equalsIgnoreCase("organzier")) {

			   UserRoleInOrganizations.addEnrolledUser(user, org, role);
			   UserRoleInOrganizations.addEnrolledUser(user, org, role,
							ent.id, "entity");

				
					
			} else {
				// idea devoloper by ibrahim adel
				Role role2 = Role.find("byRoleName", "Idea Developer").first();
				if (role2 == null) {
					// role ???
					role2 = new Role("Idea Developer", new String[0]);
					role2._save();
				}
				UserRoleInOrganization roleInOrg = new UserRoleInOrganization(user,
						org, role2);
				roleInOrg._save();
				user.userRolesInOrganization.add(roleInOrg);
				Notification n1 = new Notification("Invitation accepted",
						invite.sender, user.username + " accepted th invitation");
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
