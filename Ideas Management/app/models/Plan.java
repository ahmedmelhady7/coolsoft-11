package models;

import java.util.*;

import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Plan extends Model {

	public String title;
	public int rate;
	public String status;
	public float progress;
	public Date startDate;
	public ArrayList<String> requirements;
	public Date endDate;
	
	@OneToOne
	public Topic topic;
	
	// @OneToMany(mappedBy="plan", cascade=CascadeType.ALL)
	// public List<Comment> comments;
	
	@OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
	public List<Idea> ideas;
	
	@Lob
	public String description;

	@OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
	public List<Item> items;
	
	@ManyToOne
	public User madeBy;
	/*
	 * list of users that can access the plan
	 */
	
	/**
	 * @author ${Ibrahim Safwat}
	 * Every plan can have a rating
	 */
	public int rating;
	
	/**
	 * @author ${Ibrahim Safwat}
	 * List of comments in that plan
	 */
//	@OneToOne (mappedBy = "idea", cascade = CascadeType.ALL)
//	public List<String> commentList;
	

	public Plan(String title, User user, String status, Date startDate,
			Date endDate, String description, Topic topic) {
		this.title = title;
		this.madeBy = user;
		this.rate = 0;
		this.status = "new";
		this.progress = 0;
		this.startDate = startDate;
		this.endDate = endDate;
		this.description = description;
		this.requirements = new ArrayList<String>();
		this.items = new ArrayList<Item>();
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
	 * @return	List<Idea>    : the ideas promoted to execution in the plan
	 */
	
	public List<Idea> listOfIdeas(){
		return this.ideas;
	}
	/**
	 * This Method adds an Item to the list of Items of a Plan
	 * 
	 * @author 	hassan.ziko1
	 * 
	 * @story 	C5S1  
	 * 
	 * @return	void
	 */

	public void addItem(Date startDate, Date endDate, short status,
			String description, Plan plan, String summary) {
		Item x = new Item(startDate,endDate, status, description, plan, summary);
		this.items.add(x);
	}
	
	/**
	 * @author ${Ibrahim safwat}
	 * 
	 * @param UserToShare
	 * 				User that wants to share the plan
	 * @param UserToShareWith
	 * 				User the will be sent the notification with the planID
	 * @param planID
	 * 				ID of the plan to be shared
	 */
	public void sharePlan(User UserToShare, User UserToShareWith, int planID)
	{
		// send a notification to UserToShareWith with the planID from UserToShare
	}
	
	
	/**
	 * @author ${Ibrahim Safwat}
	 * 
	 * @param rate
	 * 			the user given rating for the specified plan
	 * @param planID
	 * 			ID of the plan wished to rate
	 */
	
	public void rate(int rate, int planID)
	{
		//organization must be public or user must be enrolled in organization to rate
		//if user already rated -> error "already rated"
		
//		if(rate>=0 && rate<=5)
//		{
//			float oldRating;
//			float tempRating;
//			tempRating = (oldRating + rate)/2;
//			planID.rating = tempRating;
//		}
//		else
//		{
//			error"Number must be between 0 and 5"
//		}
		
		
	}
	
}