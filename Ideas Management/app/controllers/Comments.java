/**
 * 
 */
package controllers;

import play.mvc.With;
import models.Comment;
import models.Idea;
import models.Plan;
import models.User;

/**
 * @author ${Ahmed El-Hadi}
 *
 */
@With(Secure.class)
public class Comments extends CRUD {
	
	/**
	 * @author ${Ibrahim safwat}
	 * 
	 * @param planID
	 *            ID of the plan that the user wants to add the comment to
	 * @param comment
	 *            Comment to be added to list of comments of the plan
	 */
	public static void addCommentToPlan(String comment, long planID) {
		planID++;
		Plan p = Plan.findById(planID);
		System.out.println("Plan is "+planID);
		User user = Security.getConnected();
		Comment c = new Comment(comment, p, user).save();
		
		p.commentsList.add(c);
	}
	/**
	 * @author ${Ibrahim safwat}
	 * 
	 * @param ideaID
	 *            ID of the idea that the user wants to add the comment to
	 * @param comment
	 *            Comment to be added to list of comments of the idea
	 */
	public static void addCommentToIdea(long ideaID, String comment) {
		Idea i = Idea.findById(ideaID);
		User user = Security.getConnected();
		Comment c = new Comment(comment, i, user);
		i.commentsList.add(c);
	}

}
