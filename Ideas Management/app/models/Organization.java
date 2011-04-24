package models;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
@Entity
public class Organization {
	String name;
	@OneToMany(mappedBy = "name")
	public ArrayList<MainEntity> entitiesList;
	@ManyToOne
	public User creator;
	@ManyToMany
	public ArrayList<User> followers;
	@ManyToMany
	public ArrayList<User> enrolledUsers;
	@ManyToMany
	public ArrayList<Tag> relatedTags;
	
	//privacy level??
	
	
	//Request_to_Join??
	
	//@OneToMany//(mappedBy = Pending class creation) 
	//public ArrayList<Invitation> invitations;
	//@OneToMany//(mappedBy = Pending class creation)
	//public ArrayList<Log> logsList;
	
	public Organization(String name, User creator) {
		this.name = name;
		this.creator = creator;
		//default privacy level
	}
	public Organization(String name, User creator, String privacyLevel) {
		this.name = name;
		this.creator = creator;
		//default privacy level
	}
}
