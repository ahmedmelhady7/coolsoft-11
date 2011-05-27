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
public class Comments extends CoolCRUD {
	
	/**
	 * This methods add a user-entered comment to the commentsList of the specified Plan
	 * 
	 * @author ${Ibrahim safwat}
	 * 
	 * @story C4S08
	 * 
	 * @param planId
	 *            ID of the plan that the user wants to add the comment to
	 *            
	 * @param comment
	 *            Comment to be added to list of comments of the plan
	 *            addes a comment to a plan
	 *            
	 *@return void            
	 */
	public static void addCommentToPlan(long planId, String comment) {
		Plan planInUse = Plan.findById(planId);
		User userLoggedIn = Security.getConnected();
		Comment commentInitialized = new Comment(comment, planInUse, userLoggedIn).save();
		planInUse.commentsList.add(commentInitialized);
		planInUse.save();
	}
	/**
	 * This methods add a user-entered comment to the commentsList of the specified Idea
	 * 
	 * @author ${Ibrahim safwat}
	 * 
	 * @story C4S08
	 * 
	 * @param ideaId
	 *            ID of the idea that the user wants to add the comment to
	 *            
	 * @param comment
	 *            Comment to be added to list of comments of the ida
	 *            addes a comment to an idea
	 *            
	 *@return void
	 */
	public static void addCommentToIdea(long ideaId, String comment) {
		Idea ideaInUse = Idea.findById(ideaId);
		User userLoggedIn = Security.getConnected();
		Comment commentInitialized = new Comment(comment, ideaInUse, userLoggedIn).save();
		ideaInUse.commentsList.add(commentInitialized);
		ideaInUse.save();
	}

}
