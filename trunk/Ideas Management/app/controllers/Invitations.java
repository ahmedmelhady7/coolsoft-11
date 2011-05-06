
/**
 * @author Mai Magdy
 */

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

	
	/**
	 * 
	 * Renders the entity to the invitation home page that contains 
	 * search for user to invite or invite by mail
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * @story C1S6
	 * 
	 * 
	 * @param entId
	 *             long id of entity that sends the invitation
	 * 
	 */
	public static void invite(long entId) {
		
        MainEntity entity= MainEntity.findById(entId);
		render(entity);

	}

	
	/**
	 * 
	 * Searchs for a user to invite. The result
	 * is a list not containing the organizers of this entity
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * @story C1S6
	 * 
	 * 
	 * @param entId
	 *             long id of entity that sends the invitation
	 * 
	 * @param name
	 *            String name that represents the text that has been entered
	 * 
	 */
	public static void searchUser(long entId,@Required String name) {

        MainEntity entity= MainEntity.findById(entId);
        User user= Security.getConnected();
        
        
		
		if (validation.hasErrors()) {
			flash.error("Please enter a name first!");
			invite(entId);
		}
         
	 
		List<User> filter = new ArrayList<User>();
		filter = Users.searchUser(name);
		List<User> organizers = Users.getEntityOrganizers(entity);
		organizers.add(entity.organization.creator);

		List<User> users = new ArrayList<User>();
		for (int i = 0; i < filter.size(); i++) {
			if (!organizers.contains(filter.get(i)) || filter.get(i).isAdmin)
				users.add(filter.get(i));
		}

		render(users, entity);
	}

	/**
	 * 
	 * Renders the organization, the entity and the user 
	 * to the invitation page , if no user selected the view will
	 * enable a text box to enter the email
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * 
	 * @story C1S6
	 * 
	 * 
	 * @param entId
	 *              long id of entity that sends the invitation
	 * 
	 * @param id
	 *            long id of the selected user , 0 if inviting by mail
	 *
	 */
	public static void page(long entId, long id) {
		
        MainEntity entity= MainEntity.findById(entId);
		User user=User.findById(id);
		render(entity,id,user);
	}

	/**
	 * 
	 * Adds a new invitation and renders the email and the entity
	 * then the Mail class sends the email, if sending to registered user
	 * the receiver will be notified with a notification
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * @story C1S6
	 * 
	 * 
	 * @param email
	 *            String email the destination of the invitation
	 * 
	 * @param role
	 *            String role that will be assigned to the user in case accepted
	 * 
	 * 
	 * @param entId
	 *              long id of entity that sends the invitation
	 * 
	 */


	 public static void send(@Required String email,long entId,long id){
		  
	        
	        MainEntity entity= MainEntity.findById(entId);
         
		    if (!rfc2822.matcher(email).matches()) {
			    flash.error("Invalid address");
			    page(entId,id);
		    }
		
			
			boolean check=false;
			List <Invitation> inv=Invitation.findAll();
			for(int i = 0;i < inv.size();i++){
				if(inv.get(i).email.equals(email)&&inv.get(i).entity.equals(entity))
					check=true;
					
			}
			if(check == true){
				flash.error("Invitation has already been sent to that user before");
		        page(entId,id);
			}
			
			
			List<User> organizers = Users.getEntityOrganizers(entity);
			organizers.add(entity.organization.creator);
			
			if(id==0){
			  	
				boolean flag=true;
				User user=User.find("byEmail", email).first();
			if(user != null){
				System.out.println("CONDITION");
	    	for (int i = 0; i < organizers.size(); i++) {
				if (organizers.contains(user) || user.isAdmin)
					flag=false;
			}
			
				if(!flag) {
			        flash.error("This user is already an organizer to this entity");
			        page(entId,id);
			    }
			}
			}
			
	         
			 String role="Organizer";
		     Mail.invite(email,role,entity.organization.name,entity.name);
		    
	    	 
	    	 User user=Security.getConnected();
	          user.addInvitation(email,role,entity.organization,entity);
	        
	         User receiver=User.find("byEmail", email).first();
	         if(receiver != null){
	       Notifications.sendNotification(receiver.id, entity.id, "organization",
	 					"You have received a new invitation from "
	 							+ entity.name);
	            }


	             
	          //**Fadwa
	            // User sender = Security.getConnected();
			     organizers.remove(user);
					
				
				for(int j = 0;j < organizers.size();j++)
				Notifications.sendNotification(organizers.get(j).id, entity.id, "entity",
						"New organizer has been invited to be an organizer to entity  "
								+ entity.name);

			//**
		       

			 render(email,entity);
		                 
	  }
	 private static final Pattern rfc2822 = Pattern.compile(
		        "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
		);
	 

	/**
	 * 
	 * Views the received invitations and renders the invitation list
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * @story C1S4
	 * 
	 */

	public static void view() {

		User user = Security.getConnected();
		List<Invitation> invitation = Invitation.find("byEmail", user.email).fetch();
		
		render(invitation);

	}

	/**
	 * 
	 * Responds to the user (accept/reject) to the invitation and
	 * renders the invitation list,the id (0/1) and the
	 * invitation id
	 * 
	 * 
	 * @author ${Mai.Magdy} > if role is organizer
	 * @author ${Fadwa Sakr} > notifications
	 * @author ${Ibrahim.Khayat} > if role is idea developer
	 * 
	 * @story C1S4
	 * 
	 * 
	 * @param id
	 *            int id represents the clicked button, 0 if reject or 1 if accept
	 * 
	 * @param i
	 *            long id of the invitation
	 * 
	 */

	public static void respond(int id,long i) {
		
		
		Invitation invite = Invitation.findById(i);
		String roleName = invite.role.toLowerCase();
		
		Organization organization = invite.organization;
		MainEntity entity = invite.entity;
		
		if (id == 1) {
			
			User user = User.find("byEmail", invite.email).first();
			Role role = Role.find("byRoleName", roleName).first();
			
                     
			if (role.roleName.equals("organizer")) {
                
				UserRoleInOrganizations.addEnrolledUser(user, organization, role,
						entity.id, "entity");
				
				List <MainEntity> sub=new ArrayList<MainEntity>();
				 sub=MainEntity.find("byParent", entity).fetch();
				 System.out.println(sub.size());
				if(sub.size() != 0){
					System.out.println("here");
				for(int j = 0;j < sub.size();j++){
					UserRoleInOrganizations.addEnrolledUser(user, organization, role,
							sub.get(j).id, "entity");
				  System.out.println(sub.get(j).name);
				}
				}

				
			//**Fadwa	
				List<User> organizers = Users.getEntityOrganizers(entity);
					organizers.add(organization.creator);
					organizers.remove(invite.sender);
				
				for(int j = 0;j < organizers.size();j++)
				Notifications.sendNotification(organizers.get(j).id, entity.id, "entity",
						"New organizer has been added as an organizer to entity  "
								+ entity.name);
			//**
				

			} 
			else {
				//* idea devoloper by ibrahim adel
				role = Roles.getRoleByName("idea developer");
				UserRoleInOrganization roleInOrg = new UserRoleInOrganization(user,
						organization, role);
				roleInOrg._save();
				user.userRolesInOrganization.add(roleInOrg);
				user.save();
				User orgLead = organization.creator;
				Notifications.sendNotification(user.id, organization.id, "Organization",
						user.username + " has accepted the invitation to join the "
								+ organization.name);
				Notifications.sendNotification(orgLead.id, organization.id, "Organization",
						user.username + " has accepted the invitation to join the "
								+ organization.name);

			}

		}  //**
				 
		              organization.invitation.remove(invite);
				     if(entity != null){
				      entity.invitationList.remove(invite);
				      }
				      User sender=invite.sender;
				      sender.invitation.remove(invite);
				      invite.delete();

 	}
	
	

}
