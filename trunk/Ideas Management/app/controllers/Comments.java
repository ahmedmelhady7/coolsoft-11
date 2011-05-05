/**
 * 
 */
package controllers;

import models.Comment;
import models.Idea;
import models.Plan;

/**
 * @author ${Ahmed El-Hadi}
 *
 */
public class Comments extends CRUD {
	
	/**
	 * @author ${Ibrahim safwat}
	 * 
	 * @param planID
	 *            ID of the plan that the user wants to add the comment to
	 * @param comment
	 *            Comment to be added to list of comments of the plan
	 */
	public void addCommentToPlan(long planID, Comment comment) {
		Plan p = Plan.findById(planID);
		p.commentsList.add(comment);
	}
	/**
	 * @author ${Ibrahim safwat}
	 * 
	 * @param ideaID
	 *            ID of the idea that the user wants to add the comment to
	 * @param comment
	 *            Comment to be added to list of comments of the idea
	 */
	public void addCommentToIdea(long ideaID, Comment comment) {
		Idea i = Idea.findById(ideaID);
		i.commentsList.add(comment);
	}

}
