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
	/*
	public void unfollow(User user) {
		followers.remove(user);
	}
	*/

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
