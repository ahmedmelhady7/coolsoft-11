package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Plan extends Model {

	public String title;
	public User user;
	public int rate;
	public String status;
	public float progress;
	public Date startDate;
	public ArrayList<String> requirements;
	public Date endDate;
	public Topic topic;

	@OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
	public List<Tag> tags;
	
	// @OneToMany(mappedBy="plan", cascade=CascadeType.ALL)
	// public List<Comment> comments;
	
	@OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
	public List<Idea> ideas;
	
	@Lob
	public String description;

	@OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
	public ArrayList<Item> items;

	public Plan(String title, User user, int rate, String status, Date startDate,
			Date endDate, String description, Topic topic) {
		this.title = title;
		this.user = user;
		this.rate = rate;
		this.status = "new";
		this.progress = 0;
		this.startDate = startDate;
		this.endDate = endDate;
		this.description = description;
		this.requirements = new ArrayList<String>();
		this.items = new ArrayList<Item>();
		this.tags = new ArrayList<Tag>();
		//this.comments = new ArrayList<Comment>();
		this.ideas = new ArrayList<Idea>();
		this.topic = topic;

	}
	
	/**
	 * This Method returns a List of type Idea
	 * 
	 * @author 	yassmeen.hussein
	 * 
	 * @story 	C5S14  
	 * 
	 * @return	List<Idea>    : the ideas promoted to excecution in the plan
	 */
	 
	public List<Idea> listOfIdeas(){
		return this.ideas;
	}

}