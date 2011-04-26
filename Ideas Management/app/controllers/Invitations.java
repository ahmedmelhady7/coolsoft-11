package controllers;


import play.mvc.Controller;
import models.MainEntity;
import models.Organization;
import models.Topic;
import models.User;
import notifiers.Mail;


public class Invitations extends Controller {
	
	 public static void invite(){
		 
		      /*get user n list of organization&entity&topic tht he organizes*/
	    	//render(user,org,ent,top);
	  }
	 
	
	 public static void send(String email,User sender,String Roles,
			  String organization,String entity,String topic, 
			  String Comments){
		  
		 Mail.invite(email,Roles,organization,entity,topic,Comments);
		    
	    	Organization org= Organization.find("byName", organization).first();
	    	MainEntity ent=MainEntity.find("byName", entity).first();
	    	Topic top=Topic.find("byName", topic).first();
	    	
	        sender.addInvitation(email,Roles,org,
	    	        ent,top,sender,Comments);
		  render(email,Roles,organization,entity,topic,Comments);
		  
	  }
	

}
