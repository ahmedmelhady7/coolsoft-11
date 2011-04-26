
	package notifiers;

	import play.mvc.*;
	import java.util.*;

	import models.Organization;
	import models.User;

	public class Mail extends Mailer{


		public static void invite(String email,String Roles,
				String Organization,String Entity,String Topic,
				String Comments)
		{
			addRecipient(email);
			setFrom("CoolSoft011@gmail.com");
			setSubject("Invitation");
			//String Link = "" ;
			User user=User.find("byEmail", email).first();
			send(user,Roles,Organization,Entity,Topic,Comments);
	        
	        
	        
		

}
}