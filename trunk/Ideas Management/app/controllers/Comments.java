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
	 * @param planID
	 *            ID of the plan that the user wants to add the comment to
	 *            
	 * @param comment
	 *            Comment to be added to list of comments of the plan
	 *            addes a comment to a plan
	 *            
	 *@return void            
	 */
	public static void addCommentToPlan(long planID, String comment) {
		//planID++;
		Plan p = Plan.findById(planID);
		User user = Security.getConnected();
		Comment c = new Comment(comment, p, user).save();
		p.commentsList.add(c);
		p.save();
		redirect("/plans/viewaslist?planId="+p.id);
	}
	/**
	 * This methods add a user-entered comment to the commentsList of the specified Idea
	 * 
	 * @author ${Ibrahim safwat}
	 * 
	 * @story C4S08
	 * 
	 * @param ideaID
	 *            ID of the idea that the user wants to add the comment to
	 *            
	 * @param comment
	 *            Comment to be added to list of comments of the ida
	 *            addes a comment to an idea
	 *            
	 *@return void
	 */
	public static void addCommentToIdea(long ideaID, String comment) {
		Idea i = Idea.findById(ideaID);
		User user = Security.getConnected();
		Comment c = new Comment(comment, i, user).save();
		i.commentsList.add(c);
		i.save();
	}

}
