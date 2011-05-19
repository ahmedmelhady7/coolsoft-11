package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import notifiers.Mail;

import models.Idea;
import models.Invitation;
import models.MainEntity;
import models.Organization;
import models.Topic;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * This is just a controller class for the login page and it still requires a
 * lot of modifications
 */

@With(Secure.class)
public class Login extends Controller {
	
	/**
	 * This is method renders the login page, first the user has to login using his user name
	 * and password and then his profile page will be rendered
	 * 
	 * @author Ahmed Maged
	 * 
	 * @return void
	 */
	
	/**Reactivates the account if it has been deactivated before
	 * Redirects the new registered users to the invitation page if 
	 * they have been invited to be idea developers/organizers
	 * 
	 * @author Mai Magdy
	 * 
	 * @Stroy C1S5, C1S11
	 * 
	 * @return void
	 */

	public static void homePage() {
		User user = Security.getConnected();
		List<Idea> ideas = user.ideasCreated;
		List<Idea> drafts = new ArrayList<Idea>();
		
		for(Idea s : ideas)
			if(s.isDraft)
				drafts.add(s);
			
		int admin=0;
		if(user.isAdmin)
		  admin=1;
		
		if(user.state.equals("n")){
		//System.out.println(user.state);
			user.state="a";
			user.save();
			Mail.reactivate();
			flash.error("Your account has been reactivated successfuly");
		}	
		
		if(user.state.equals("c")){
			 Invitation invite=Invitation.find("byEmail",user.email).first();
			    user.state="a";
			    user.save();
			    Mail.activation(user);
			 
			    if(invite !=null){
				   Invitations.view();
			   }
				
			}
		
		render(user,admin,drafts);
	}
	
	public static void about() {
		User user = Security.getConnected();
		render(user);
	}
	
	public static void contact() {
		User user = Security.getConnected();
		render(user);
	}
}
