package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;


@Entity
public class MainEntity extends Model{
	
	public String name;
	
	@ManyToOne
	public MainEntity parent;	
	public short privacyLevel;
	public String description;	
	@OneToMany (mappedBy = "parent", cascade = CascadeType.ALL)
	public List<MainEntity> subentities;	
	@ManyToMany(mappedBy = "followingEntities", cascade = CascadeType.ALL)
	public List<User> followers;	
	@ManyToOne
	public Organization organization;	
	@OneToMany (mappedBy = "entity", cascade = CascadeType.ALL)
	public List<Topic> topicList;	
	@ManyToMany (mappedBy = "entities", cascade = CascadeType.ALL)
	public List<Tag> tagList;		
	@OneToMany (mappedBy = "entity", cascade = CascadeType.ALL)
	public List<Invitation> invitationList;
	@ManyToMany(mappedBy = "entitiesIOrganize", cascade = CascadeType.ALL)
	public List<User> organizers;
	
	//ArrayList<Relationship> relationshipList;
	//ArrayList<Request> requestList;	
	//Arraylist<RequestOfRelationship>

	public MainEntity (String n, String d, short p) {
		this.name = n;
		this.description = d;
		this.privacyLevel = p;
		this.parent = null;
		invitationList = new ArrayList<Invitation>();
		subentities = new ArrayList<MainEntity>();
		followers = new ArrayList<User>();
		topicList = new ArrayList<Topic>();
		tagList = new ArrayList<Tag>();
		organizers = new ArrayList<User>();
		
	}
	
	public MainEntity (String n, String d, short p, MainEntity parent) {
		this.name = n;
		this.description = d;
		this.privacyLevel = p;
		this.parent = parent;
		parent.subentities.add(this);
		invitationList = new ArrayList<Invitation>();
		subentities = new ArrayList<MainEntity>();
		followers = new ArrayList<User>();
		topicList = new ArrayList<Topic>();
		tagList = new ArrayList<Tag>();
		organizers = new ArrayList<User>();
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
