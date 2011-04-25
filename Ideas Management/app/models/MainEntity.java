package models;

import java.util.ArrayList;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;


@Entity
public class MainEntity extends Model{
	String name;
	
	@ManyToOne
	MainEntity parent;	
	int privacyLevel;
	String description;	
	@OneToMany (mappedBy = "parent", cascade = CascadeType.ALL)
	ArrayList<MainEntity> subentities;	
	ArrayList<User> followers;	
	//@ManyToOne
	Organization organization;	
	@OneToMany (mappedBy = "entity", cascade = CascadeType.ALL)
	ArrayList<Topic> topicList;	
	@ManyToMany (mappedBy = "relatedTags", cascade = CascadeType.ALL)
	ArrayList<Tag> tagList;	
	@OneToMany (mappedBy = "entity", cascade = CascadeType.ALL)
	ArrayList<Invitation> invitationList;
	//ArrayList<User> organizer;
	//ArrayList<Relationship> relationshipList;
	//ArrayList<Request> requestList;	
	//Arraylist<RequestOfRelationship>
	
	
	public MainEntity (String n, String d, int p) {
		this.name = n;
		this.description = d;
		this.privacyLevel = p;
		this.parent = null;
	}
	
	public MainEntity (String n, String d, int p, MainEntity parent) {
		this.name = n;
		this.description = d;
		this.privacyLevel = p;
		this.parent = parent;
		parent.subentities.add(this);
	}
	
	/**
	 * This Method removes a user from the list of followers
	 * 
	 * @author 	Ibrahim.al.khayat
	 * 
	 * @story 	C2S12
	 * 
	 * @param  	user 	: the user who follows
	 * 
	 * @return	void
	 */
	public void unfollow(User user) {
		followers.remove(user);
	}
}
