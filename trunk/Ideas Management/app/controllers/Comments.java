/**
 * 
 */
package controllers;

import java.lang.reflect.Constructor;
import java.util.List;

import controllers.CoolCRUD.ObjectType;

import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.With;
import sun.net.www.content.text.plain;
import models.Comment;
import models.Idea;
import models.Log;
import models.Plan;
import models.Topic;
import models.User;

/**
 * @author ${Ahmed El-Hadi}
 * 
 */
@With(Secure.class)
public class Comments extends CoolCRUD {

	/**
	 * This methods add a user-entered comment to the commentsList of the
	 * specified Plan
	 * 
	 * @author ${Ibrahim safwat}
	 * 
	 * @story C4S08
	 * 
	 * @param planId
	 *            ID of the plan that the user wants to add the comment to
	 * 
	 * @param comment
	 *            Comment to be added to list of comments of the plan addes a
	 *            comment to a plan
	 * 
	 * @return void
	 */
	public static void addCommentToPlan(long planId, String comment) {
		Plan planInUse = Plan.findById(planId);
		User userLoggedIn = Security.getConnected();
		Comment commentInitialized = new Comment(comment, planInUse,
				userLoggedIn).save();
		planInUse.commentsList.add(commentInitialized);
		planInUse.save();
		String logDescription = "<a href=\"/users/viewprofile?userId="
			+ userLoggedIn.id + "\">" 
			+ userLoggedIn.username + "</a>" 
			+ " added a comment to plan " +"<a href=\"/plans/viewaslist?planId="
			+ planId
			+ "\">"
			+ planInUse.title
			+ "</a>";
	 Log.addUserLog(logDescription, userLoggedIn, commentInitialized, planInUse, planInUse.topic,
			planInUse.topic.entity, planInUse.topic.entity.organization);
	}

	/**
	 * This methods add a user-entered comment to the commentsList of the
	 * specified Idea
	 * 
	 * @author ${Ibrahim safwat}
	 * 
	 * @story C4S08
	 * 
	 * @param ideaId
	 *            ID of the idea that the user wants to add the comment to
	 * 
	 * @param comment
	 *            Comment to be added to list of comments of the ida addes a
	 *            comment to an idea
	 * 
	 * @return void
	 */
	public static void addCommentToIdea(long ideaId, String comment) {
		Idea ideaInUse = Idea.findById(ideaId);
		User userLoggedIn = Security.getConnected();
		Comment commentInitialized = new Comment(comment, ideaInUse,
				userLoggedIn).save();
		userLoggedIn.hisComments.add(commentInitialized);
		ideaInUse.commentsList.add(commentInitialized);
		ideaInUse.save();
		
		String logDescription = "<a href=\"/users/viewprofile?userId="
			+ userLoggedIn.id + "\">" 
			+ userLoggedIn.username + "</a>" 
			+ " added a comment to idea " +"<a href=\"/ideas/show?ideaId="
			+ ideaId
			+ "\">"
			+ ideaInUse.title
			+ "</a>";
	 Log.addUserLog(logDescription, userLoggedIn, commentInitialized, ideaInUse, ideaInUse.belongsToTopic,
			 ideaInUse.belongsToTopic.entity, ideaInUse.belongsToTopic.entity.organization);
	}

	/**
	 * deletes a comment
	 * 
	 * @author Noha Khater
	 * 
	 * @param commentId
	 *            : the id of the comment to be deleted
	 * @return boolean
	 */
	public static boolean deleteComment(long commentId) {
		Comment comment = Comment.findById(commentId);
		List<Topic> allTopics = Topic.findAll();
		List<Idea> allIdeas = Idea.findAll();
		List<Plan> allPlans = Plan.findAll();
		
		for (int i = 0; i < allTopics.size(); i++) {
			if (allTopics.get(i).commentsOn.contains(comment)) {
				allTopics.get(i).commentsOn.remove(comment);
				
				String logDescription = "<a href=\"/users/viewprofile?userId="
					+ Security.getConnected().id + "\">" 
					+ Security.getConnected().username + "</a>" 
					+ " deleted a comment on the topic " +"<a href=\"/topics/show?topicId="
					+ allTopics.get(i).id
					+ "\">"
					+ allTopics.get(i).title
					+ "</a>";
			 Log.addUserLog(logDescription, Security.getConnected(), comment, allTopics.get(i).entity, allTopics.get(i),
					 allTopics.get(i).entity.organization);
				allTopics.get(i).save();
			}
		}
		for (int i = 0; i < allIdeas.size(); i++) {
			if (allIdeas.get(i).commentsList.contains(comment)) {
				allIdeas.get(i).commentsList.remove(comment);
				
				String logDescription = "<a href=\"/users/viewprofile?userId="
					+ Security.getConnected().id + "\">" 
					+ Security.getConnected().username + "</a>" 
					+ " deleted a comment on the idea " +"<a href=\"/ideas/show?ideaId="
					+ allIdeas.get(i).id
					+ "\">"
					+ allIdeas.get(i).title
					+ "</a>";
			 Log.addUserLog(logDescription, Security.getConnected(), comment, allIdeas.get(i).belongsToTopic, allIdeas.get(i),
					 allIdeas.get(i).belongsToTopic.entity.organization,  allIdeas.get(i).belongsToTopic.entity);
				allIdeas.get(i).save();
			}
		}
		for (int i = 0; i < allPlans.size(); i++) {
			if (allPlans.get(i).commentsList.contains(comment)) {
				allPlans.get(i).commentsList.remove(comment);
				
				String logDescription = "<a href=\"/users/viewprofile?userId="
					+ Security.getConnected().id + "\">" 
					+ Security.getConnected().username + "</a>" 
					+ " deleted a comment on the idea " +"<a href=\"/plans/viewaslist?planId="
					+ allPlans.get(i).id
					+ "\">"
					+ allPlans.get(i).title
					+ "</a>";
			 Log.addUserLog(logDescription, Security.getConnected(), comment, allPlans.get(i).topic, allPlans.get(i),
					 allPlans.get(i).topic.entity.organization,  allPlans.get(i).topic.entity);
				allPlans.get(i).save();
			}
		}
		
		
		return true;
	}
}
