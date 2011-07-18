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

/**
 * 
 * @author Mohamed Ghanem
 * 
 */
@On("0 0 12 * * ?")
public class IdeaCase extends Job {

	/**
	 * @author Mohamed Ghanem
	 * 
	 * @see play.jobs.Job#doJob()
	 */
	@SuppressWarnings("deprecation")
	public void doJob() {
		List<Idea> ideas = Idea.findAll();
		for (int i = 0; i < ideas.size(); i++) {
			List<Comment> tempComments = ideas.get(i).commentsList;
			for (int j = 0; j < tempComments.size(); j++) {
				Date tempDate14daysAgoDate = new Date(
						tempComments.get(j).commentDate.getYear(),
						tempComments.get(j).commentDate.getMonth(),
						tempComments.get(j).commentDate.getDay() - 14);
				if (ideas.get(i).active
						&& (new Date()).after(tempDate14daysAgoDate)) {
					ideas.get(i).active = false;
					Notifications.sendNotification(ideas.get(i).author.id,
							ideas.get(i).id, "Idea",
							"This Idea now is inactive!!");
				} else {
					if (!ideas.get(i).active) {
						if (tempDate14daysAgoDate
								.after(ideas.get(i).commentsList.get(j).commentDate)) {
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

}
