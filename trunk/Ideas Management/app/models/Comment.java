package models;

import javax.persistence.*;

import play.db.jpa.Model;

/**
 * @author ${Ibrahim Safwat}
 * 
 */

/**
* Comments to be posted on ideas/plans/requests
*/

@Entity
public class Comment extends Model {
	
	String comment;
	
	//public int ID;
	
	@ManyToOne 
	public Topic commentedTopic;
		
	//@OneToMany
	String postedOn;
	
	//@OneToOne
	//User commenter;
	
	public Comment(String comment, int ID, String postedOn, User commenter)
	{
		this.comment=comment;
		//this.ID=ID;
		this.postedOn=postedOn;
		//this.commenter=commenter;
	}
	
	/**
	 * @author ${Ibrahim safwat}
	 * 
	 * @param ideaID
	 * 			ID of the idea that the user wants to add the comment to
	 * @param comment
	 * 			Comment to be added to list of comments of the idea
	 */
	public void addCommentToIdea(int ideaID, String comment)
	{
		
	}
	
	/**
	 * @author ${Ibrahim safwat}
	 * 
	 * @param planID
	 * 			ID of the plan that the user wants to add the comment to
	 * @param comment
	 * 			Comment to be added to list of comments of the plan
	 */
	public void addCommentToPlan(int planID, String comment)
	{
		
	}

}
