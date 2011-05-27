package jobs;

import java.text.DateFormat;
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
import play.jobs.Job;
import play.jobs.On;

@On("0 0 12 * * ?")
public class IdeaCase extends Job {

	/**
	 * @author Mohamed Ghanem
	 * 
	 * @see play.jobs.Job#doJob()
	 */
	public void doJob() {
		List<Idea> ideas = Idea.findAll();
		System.out.println("ideas size =" + ideas.size());
		for (int i = 0; i < ideas.size(); i++) {
			List<Comment> m = ideas.get(i).commentsList;
			System.out.println("comments size =" + m.size());
			for (int j = 0; j < m.size(); j++) {
				int d = m.get(j).commentDate.getDay() - 14;
				int mo = m.get(j).commentDate.getMonth();
				int y = m.get(j).commentDate.getYear();
				Date x = new Date(y, mo, (d - 14));
				if (ideas.get(i).active && (new Date()).after(x)) {
					Notifications.sendNotification(ideas.get(i).author.id,
							ideas.get(i).id, "Idea",
							"This Idea now is inactive!!");
					ideas.get(i).active = false;
				} else {
					if (!ideas.get(i).active) {
						if (x.after(ideas.get(i).commentsList.get(j).commentDate)) {
							ideas.get(i).active = true;
							Notifications.sendNotification(
									ideas.get(i).author.id, ideas.get(i).id,
									"Idea", "this Idea is back to active");
						}
					}
				}
			}
		}
	}

	// /**
	// *
	// * @author ${Fady Amir}
	// *
	// *
	// * @param idea
	// * the idea that we check its date
	// * @return void
	// */
	//
	// public static void checkDate(Idea idea) {
	//
	// Comment lastComment = idea.commentsList.get(idea.commentsList.size() -
	// 1);
	//
	// Date now = new Date();
	//
	// Date lastCommentDate = lastComment.commentDate;
	//
	// lastCommentDate.setDate(lastCommentDate.getDate() + 14);
	//
	// if (lastCommentDate.after(now)) {
	// List<User> user = new ArrayList<User>();
	// user.add(idea.author);
	//
	// String type = "idea ";
	// String desc = "This idea is inactive";
	// // Send notification
	//
	// for (int i = 0; i < user.size(); i++) {
	// Notifications.sendNotification(user.get(i).id, idea.id, type,
	// desc);
	// }
	// }
	//
	// }
	//
	// /**
	// *
	// * @author ${Fady Amir}
	// *
	// * checking the dates of all ideas
	// * @return void
	// *
	// */
	//
	// public static void getAllIdeas() {
	// List<Organization> listOfOrganizations = Organization.findAll();
	// for (int i = 0; i < listOfOrganizations.size(); i++) {
	// Organization org = listOfOrganizations.get(i);
	// List<User> users = Users.getEnrolledUsers(org);
	//
	// for (int j = 0; j < users.size(); j++) {
	// User user = users.get(j);
	// List<Idea> ideas = user.ideasCreated;
	//
	// for (int k = 0; k < ideas.size(); k++) {
	// Idea idea = ideas.get(k);
	// checkDate(idea);
	// }
	// }
	// }
	// }

}
