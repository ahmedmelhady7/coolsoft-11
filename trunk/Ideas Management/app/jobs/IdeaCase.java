package jobs;
import java.util.*;
import javax.persistence.*;
import models.Comment;
import models.Idea;
import models.Organization;
import models.User;
import controllers.Notifications;
import controllers.Users;
import play.data.validation.*;
import play.db.jpa.Model;
import play.jobs.On;

@On("0 0 12 0 0 ?")
public class IdeaCase {
	/**
	 * 
	 * @author ${Fady Amir}
	 * 
	 * 
	 * @param idea
	 *            the idea that we check its date
	 * @return void
	 */
	
	public static void checkDate(Idea idea) {

		Comment lastComment = idea.commentsList.get(idea.commentsList.size() - 1);

		Date now = new Date();

		Date lastCommentDate = lastComment.commentDate;

		lastCommentDate.setDate(lastCommentDate.getDate() + 14);

		if (lastCommentDate.after(now)) {
			List<User> user = new ArrayList<User>();
			user.add(idea.author);

			String type = "idea ";
			String desc = "This idea is inactive";
			// Send notification

			for (int i = 0; i < user.size(); i++) {
				Notifications.sendNotification(user.get(i).id, idea.id, type,
						desc);
			}
		}

	}

	/**
	 * 
	 * @author ${Fady Amir}
	 * 
	 *         checking the dates of all ideas
	 * @return void
	 * 
	 */

	public static void getAllIdeas() {
		List<Organization> listOfOrganizations = Organization.findAll();
		for (int i = 0; i < listOfOrganizations.size(); i++) {
			Organization org = listOfOrganizations.get(i);
			List<User> users = Users.getEnrolledUsers(org);

			for (int j = 0; j < users.size(); j++) {
				User user = users.get(j);
				List<Idea> ideas = user.ideasCreated;

				for (int k = 0; k < ideas.size(); k++) {
					Idea idea = ideas.get(k);
					checkDate(idea);
				}
			}
		}
	}


}
