package models;

import javax.persistence.*;

import play.data.validation.Required;
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
	
	@Required
	String comment;
	
	@ManyToOne 
	public Topic commentedTopic;
	
	@ManyToOne
	public Idea commentedIdea;

	@ManyToOne
	public Plan commentedPlan;
	
	@OneToOne
	public User commenter;
	
	public Comment(String comment, Topic commentedTopic, User commenter)
	{
		this.comment=comment;
		this.commentedTopic = commentedTopic;
		this.commenter = commenter;
	}
	
	public Comment(String comment, Plan commentedPlan, User commenter)
	{
		this.comment=comment;
		this.commentedPlan = commentedPlan;
		this.commenter = commenter;
	}
	
	public Comment(String comment, Idea commentedIdea, User commenter)
	{
		this.comment=comment;
		this.commentedIdea = commentedIdea;
		this.commenter = commenter;
	}
	
	/**
	 * @author ${Ibrahim safwat}
	 * 
	 * @param ideaID
	 * 			ID of the idea that the user wants to add the comment to
	 * @param comment
	 * 			Comment to be added to list of comments of the idea
	 */
	public void addCommentToIdea(int ideaID, Comment comment)
	{
		//get from HTML comment as string, Idea to be commented on, user that commented
		//ideaID.commentsList.add(comment);
	}
	
	/**
	 * @author ${Ibrahim safwat}
	 * 
	 * @param planID
	 * 			ID of the plan that the user wants to add the comment to
	 * @param comment
	 * 			Comment to be added to list of comments of the plan
	 */
	public void addCommentToPlan(int planID, Comment comment)
	{
		//get from HTML comment as string, Idea to be commented on, user that commented
		//plaID.commentsList.add(comment);
	}

}
