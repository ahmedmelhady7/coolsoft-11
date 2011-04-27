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
	
        

	 
}
