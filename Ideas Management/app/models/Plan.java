package models;

import java.util.*;

import javax.persistence.*;


import controllers.Notifications;

import play.data.validation.Required;
import play.db.jpa.*;

@Entity
public class Plan extends Model {

	@Required public String title;
	@Required public String status;
	
	
	public float progress;

	
	@Required public Date startDate;
	@Required public Date endDate;

	
	
	@Required
	@OneToOne
	public Topic topic;
	
	@OneToMany(cascade = CascadeType.PERSIST)
	public List<Comment> commentsList;
	
	@OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
	public List<Idea> ideas;
	
	@Required
	@Lob
	public String description;
	@Lob
	@Required public String requirement;

	@OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
	public List<Item> items;
	
	@Required
	@ManyToOne
	public User madeBy;
	
	
	/**
	 * @author ${Ibrahim Safwat}
	 * Every plan can have a rating
	 */
	public int rating;
	/**
	 * @author ${Ibrahim Safwat} Must keep track of which users rated
	 */
	@OneToMany
	public List<User> usersRated;
	
	/**
	 * @author ${Ibrahim Safwat}
	 * List of comments in that plan
	 */

	public Plan(String title, User user, Date startDate,
			Date endDate, String description, Topic topic, String requirement) {

		this.title = title;
		this.madeBy = user;
		this.status = "new";
		this.progress = 0;
		this.startDate = startDate;
		this.endDate = endDate;
		this.description = description;
		this.requirement = requirement;

		this.items = new ArrayList<Item>();
		this.ideas = new ArrayList<Idea>();
		this.topic = topic;
		this.rating = 0;
		this.commentsList = new ArrayList<Comment>();

	}
	
	/**
	 * This Method returns a List of type Idea
	 * 
	 * @author 	yassmeen.hussein
	 * 
	 * @story 	C5S14  
	 * 
	 * @return	List<Idea>    : the ideas promoted to execution in the plan
	 */
	
	public List<Idea> listOfIdeas(){
		return this.ideas;
	}
	/**
	 * This Method adds an Item to the list of Items of a Plan
	 * 
	 * @story 	C5S1  
	 * 
	 * @author 	hassan.ziko1
	 * 
	 * @param startDate
	 * 			The start date of working on the item
	 * @param enDate
	 * 			The end date of working on the item
	 * @param status 
	 * 			The status of the item
	 * @param description
	 * 			The description of the task
	 * @param plan 
	 * 			The plan that contains the item
	 * @param summary
	 * 			The summary of the description of the item
	 * 
	 * @return	void
	 */

	public void addItem(Date startDate, Date endDate,
			String description, Plan plan, String summary) {
		Item x = new Item(startDate,endDate,description, plan, summary);
		this.items.add(x);
		this.save();
	}
	

	
	
	/**
	 * 
	 * This Method adds an idea to the list of ideas being executed in the plan given the idea
	 * 
	 * @author 	salma.qayed
	 * 
	 * @story 	C5S4
	 * 
	 * @param 	idea 	: the idea that will executed in the plan
	 * 
	 * @return	void
	 */
	public void addIdea(Idea idea) {
		this.ideas.add(idea);
		this.save();
	}
	
}