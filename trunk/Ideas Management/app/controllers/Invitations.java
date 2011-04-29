package controllers;


import java.util.ArrayList;
import java.util.List;

import play.mvc.Controller;
import models.Invitation;
import models.MainEntity;
import models.Organization;
import models.Role;
import models.Topic;
import models.User;
import models.UserRoleInOrganization;
import notifiers.Mail;


public class Invitations extends CRUD {
	
	
	 /**
	 * 
	 * This method is responsible for 
	 *
	 * 
	 * @author ${Mai.Magdy}
	 * 
	 * @story C1S6
	 * 
	 * @param 
	 * 
	 *                 
	 * @param 
	 *             
	 *               
	 *@param
	 *               
	 *               
	 *@param
	 *              
	 *                                
	 * @return
	 */
 
	
	 public static void invite(User user){
		      
		    List <Organization> org = new ArrayList<Organization>();
		    	org=Users.getOrganizerOrganization(user);
		     
		  
		    	List <MainEntity> ent = new ArrayList<MainEntity>();
		    // ent=Users.getOrganizerEntities(org.get(0), user);
		     
	    	//render(org,ent);
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
	 
	 public static void send(String email,String role,String organization,
			 String entity){
		  
		 Mail.invite(email,role,organization,entity);
		    
	    	Organization org= Organization.find("byName", organization).first();
	    	MainEntity ent=MainEntity.find("byName", entity).first();
	    	 
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
		 * This method is responsible for responding to the user (accept/reject)
		 * to the invitation
		 * It renders the invitation list and the id (0/1) and the number of the invitation
		 *
		 * 
		 * @author ${Mai.Magdy}
		 * 
		 * @story C1S4
		 * 
		 * @param choice  
		 *              0 if reject or 1 if accept
		 * 
		 * @param id  
		 *              the invitation id
		 *            
		 * 
		 * @return void
		 */

	public static void respond(int choice,int id,User user){
		  
		//User user=get user from session
		 // List<Invitation> inv = Invitation.find("byEmail", user.email).fetch();
		  
		  Invitation invite=Invitation.findById(id);
		  if(choice==1){
		  String rolename=invite.role;
		  Organization org=invite.organization;
		  MainEntity ent=invite.entity;
		  Role role=Role.find("byName", rolename).first();
		  
		  
		  if(rolename.equalsIgnoreCase("organzier")){
		  
		  boolean flag=UserRoleInOrganizations.isOrganizer(user,ent.id,"entity");
		  
		  if(!flag){
		  UserRoleInOrganizations.addEnrolledUser(user,org,role);
		  UserRoleInOrganizations.addEnrolledUser(user,org,role,ent.id,"entity");
		  
		  
		       }
	     }
		  
		}
		  
		  invite.delete();
		  
			// Invitation invite=Invitation.findById(inv.get(i).id);
			 
			 
			// render(choice,inv,i);
		  
			 
		  /*
		   *  List <User> enrolled =Users.getEnrolledUsers(org);
		  flag=false;
		  for(int j=0;j<enrolled.size();j++){
			  if(enrolled.get(j).equals(user))
				  flag=true;
		  }
		  
		  if(org.privacyLevel!=2&&flag){
			  Role rol=Role.find("byName", "Idea Developer").first();
			  UserRoleInOrganizations.addEnrolledUser(user,org,rol);
		  }*/
		 
		
	}
        

	 
}
