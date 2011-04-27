package controllers;


import java.util.List;

import play.mvc.Controller;
import models.Invitation;
import models.MainEntity;
import models.Organization;
import models.Topic;
import models.User;
import notifiers.Mail;


public class Invitations extends Controller {
	
	 public static void invite(){
		 
		      /*get user n list of organization&entity&topic tht he organizes*/
	    	//render(org,ent,top);
	  }
	 
	
	 public static void send(String email,String role,String organization,
			 String entity,String topic, String Comments){
		  
		 Mail.invite(email,role,organization,entity,topic);
		    
	    	Organization org= Organization.find("byName", organization).first();
	    	MainEntity ent=MainEntity.find("byName", entity).first();
	    	Topic top=Topic.find("byName", topic).first();
	    	 //**sender=the user from the session 
	        //**sender.addInvitation(email,role,org,ent,top);
	    	
		  //render(email,role,organization,entity,topic);
		    render(email);
	  }
	 
	 
	 public static void view(){
			
		   //**User user=get user from session
	       //<Invitation> inv = Invitation.find("byEmail", user.email).fetch();
	       //render(user,inv);

		}

	public static void respond(int id,int i){
		
		 /**User user=get user from session
		  List<Invitation> inv = Invitation.find("byEmail", user.email).fetch();
		  String role=inv.get(i).role;
		  Organization org=inv.get(i).organization;
		  MainEntity ent=inv.get(i).entity;
		  Topic top=inv.get(i).topic;
		  User sender=inv.get(i).sender;
		 
		 
		   top.invitation.get(i).delete;
		    org.invitation.get(i).delete;
		    ent.invitation.get(i).delete;
		    sender.invitation.get(i).delete;
		   
		// flash.success("Invitation(s) sent successfully!");
		 inv.get(i).delete();
		 
		 render(id,inv,i);**/
		
		
		
	}
        

	 
}
