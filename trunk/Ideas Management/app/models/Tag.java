package models;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import play.data.validation.Required;
import play.db.jpa.Model;
@Entity
public class Tag extends Model{
	@Required
	String name;
	ArrayList<Tag> relatedTags;
	//String creator;	We might still need this attribute;
	@ManyToMany
	public ArrayList<User> followers;
	@ManyToMany
	public ArrayList<Organization> organizations;
	
	public Tag(String name) {
		this.name = name;
	}
	
	public Tag(String name, ArrayList<Tag> tags) {
		this.name = name;
		this.relatedTags = tags;
	}
	/*
	public void unfollow(User user) {
		followers.remove(user);
	}
	*/
}
