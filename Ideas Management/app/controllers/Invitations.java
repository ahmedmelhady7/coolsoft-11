package controllers;


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
 
	
	 public static void invite(){
		 
		      /*get user n list of organization&entity&topic tht he organizes*/
	    	//render(org,ent,top);
	  }
	 
	 /**
		 * 
		 * This method is responsible for sending an invitation
		 *
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
		 * @return
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
		 *
		 * 
		 * @author ${Mai.Magdy}
		 * 
		 * @story C1S4
		 * 
		 * @param  
		 *       
		 * 
		 * @return
		 */
	 
	 public static void view(){
			
		   //**User user=get user from session
	       //<Invitation> inv = Invitation.find("byEmail", user.email).fetch();
	       //render(inv);

		}
	 
	 
	 /**
		 * 
		 * This method is responsible for responding to the user (accept/reject)
		 * to the invitation
		 *
		 * 
		 * @author ${Mai.Magdy}
		 * 
		 * @story C1S4
		 * 
		 * @param id  
		 *              0 if reject or 1 if accept
		 * 
		 * @param id  
		 *              the number of the invitation in the list
		 *            
		 * 
		 * @return
		 */

	public static void respond(int id,int i,User user){
		
		  //User user=get user from session
		  List<Invitation> inv = Invitation.find("byEmail", user.email).fetch();
		  String rolename=inv.get(i).role;
		  Organization org=inv.get(i).organization;
		  MainEntity ent=inv.get(i).entity;
		  
		  Role role=Role.find("byName", rolename).first();
		  
		  List <User> organizers= Users.getEntityOrganizers(ent);
		  List <User> enrolled =Users.getEnrolledUsers(org);
		  
		  
		  boolean flag=false;
		  for(int j=0;j<organizers.size();j++){
			  if(organizers.get(j).equals(user))
				  flag=true;
		  }
		  
		  if(!flag){
		  UserRoleInOrganizations.addEnrolledUser(user,org,role);
		  UserRoleInOrganizations.addEnrolledUser(user,org,role,ent.id,"entity");
		  }
		  /*
		  flag=false;
		  for(int j=0;j<enrolled.size();j++){
			  if(enrolled.get(j).equals(user))
				  flag=true;
		  }
		  
		  if(org.privacyLevel!=2&&flag){
			  Role rol=Role.find("byName", "Idea Developer").first();
			  UserRoleInOrganizations.addEnrolledUser(user,org,rol);
		  }*/
		 
		 
		 Invitation invite=Invitation.findById(inv.get(i).id);
		 invite.delete();
		 
		 render(id,inv,i);
		 
		 
		
		
		
	}
        

	 
}
