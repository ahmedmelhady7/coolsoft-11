package controllers;

import java.util.List;

import play.db.jpa.Blob;
import play.mvc.Controller;
import models.Document;
import models.Organization;
import models.Picture;
import models.User;

public class Contribution extends Controller
{
	/**
	  * 
	  * @author Fady Amir
	  * 
	  * this method renders a list of users 
	  * @story c4s11 : view contribution counter
	  * 
	  *@param Id
	  * @return void
	  * 
	  */
	
	public static void contribution(long id)
	{
		//User user = Security.getConnected();
		User user = Security.getConnected();
		
		List<User> users = Users.getEnrolledUsers((Organization)Organization.findById(id));
		
		User temp;
		int n=users.size();

		for(int i=0; i<n; i++)
		{
		   for(int j = 1; j < (n-i); j++)
		   {
		        if((users.get(j-1).communityContributionCounter) > (users.get(j).communityContributionCounter))
		        {
		          temp = users.get(j-1);
		          users.set(j-1,users.get(j));
		          users.set(j,temp);
		        }
		   }
		}
		render(users,user);
	}  
	   
}
