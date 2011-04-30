package controllers;

import java.util.List;

import models.MainEntity;
import models.Organization;
import models.Role;
import models.User;

public class BannedUsers extends CRUD{

	
	  public void restrictOrganizer(long organizationID , User user ){
		  Organization o =Organization.findById(organizationID); 
		  List<User> users = Users.searchOrganizer(o); 
		  render( users ); 
		  }
	  
	  

	
	
}
