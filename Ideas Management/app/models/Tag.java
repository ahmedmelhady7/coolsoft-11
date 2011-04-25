package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import play.data.validation.Required;
import play.db.jpa.Model;
@Entity
public class Tag extends Model{
	@Required
	public String name;
//	@ManyToMany(mappedBy = "relatedTags") 	Still waiting for the Relationship Class
//	public List<Tag> relatedTags; 		  	Sprint 2
	
	//String creator;	We might still need this attribute;
	
	@ManyToMany(mappedBy = "followingTags", cascade = CascadeType.ALL)
	public List<User> followers;
	
	@ManyToMany(mappedBy = "relatedTags", cascade = CascadeType.ALL)
	public List<Organization> organizations;
	
	@ManyToMany
	public List<Item> taggedItems;
	
	public Tag(String name) {
		this.setName(name);
		this.followers = new ArrayList<User>();
		this.organizations = new ArrayList<Organization>();
		this.taggedItems = new ArrayList<Item>();
		//this.relatedTags = new ArrayList<Tag>();
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
	

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
