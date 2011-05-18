package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import notifiers.Mail;

import models.Idea;
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

	public static void index() {
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
		render(user,admin,drafts);

	}
}
