package models;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import play.data.validation.Required;
import play.db.jpa.Model;
@Entity
public class Tag extends Model{
	@Required
	private
	String name;
	@ManyToMany
	ArrayList<Tag> relatedTags;
	//String creator;	We might still need this attribute;
	@ManyToMany(mappedBy = "username")
	public ArrayList<User> followers;
	@ManyToMany(mappedBy = "name")
	public ArrayList<Organization> organizations;
	
	public Tag(String name) {
		this.setName(name);
	}
	
	public Tag(String name, ArrayList<Tag> tags) {
		this.setName(name);
		this.relatedTags = tags;
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
