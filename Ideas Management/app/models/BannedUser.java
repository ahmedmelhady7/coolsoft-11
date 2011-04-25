package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class BannedUser extends Model {

	@ManyToOne
	 User bannedUser;
	

	// @OneToMany
	// List<Organization> organizations;

	@ManyToOne
	Action action;
  
	@ManyToOne
	Organization organization;
	
	String resourceType;
	int resourceID;

	public BannedUser() {
		
	}

}
