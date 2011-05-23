package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Idea;
import models.LinkDuplicatesRequest;
import models.MainEntity;
import models.User;
import play.mvc.Controller;
import play.mvc.With;

/**
 * 
 * @author Loaay Alkherbawy
 * 
 * @story C4S10 : Marking two Ideas as a duplicate
 * 
 */
@With(Secure.class)
public class MarkRequest extends Controller {

	String error;

	/**
	 * @author Loaay Alkherbawy
	 * 
	 * @Story C4S10: marking of ideas as duplicate
	 * 
	 *        method rendering the requests view
	 */

	public static void markDuplicate() {
		User user = Security.getConnected();
		List<LinkDuplicatesRequest> r = LinkDuplicatesRequest.findAll();
		List<LinkDuplicatesRequest> requests = new ArrayList<LinkDuplicatesRequest>();
		for (int i = 0; i < r.size(); i++) {
			if (user.id == r.get(i).idea1.belongsToTopic.creator.id) {
				requests.add(r.get(i));
			}
			for (int j = 0; j < r.get(i).idea1.belongsToTopic.getOrganizer()
					.size(); j++) {
				if (user.id == r.get(i).idea1.belongsToTopic.getOrganizer()
						.get(j).id
						&& r.get(i).idea1.belongsToTopic.getOrganizer().get(j).id != r
								.get(i).idea1.belongsToTopic.creator.id) {
					requests.add(r.get(i));
				}
			}
		}
		render(requests, user);
	}

	/**
	 * @author Loaay Alkherbawy
	 * 
	 * @Story C4S10: marking of ideas as duplicate
	 * 
	 * @param idea1
	 *            : Idea one
	 * @param idea2
	 *            : Idea relevant to Idea one
	 * @param des
	 *            : description of where the duplication is
	 */

	public static void sendRequest(long idea1ID, long idea2ID, String des) {
		User user = Security.getConnected();
		Idea i1 = Idea.findById(idea1ID);
		Idea i2 = Idea.findById(idea2ID);
		notFoundIfNull(i1);
		notFoundIfNull(i2);
		Long ideaOrg1 = i1.belongsToTopic.id;
		Long ideaOrg2 = i2.belongsToTopic.id;
		List<LinkDuplicatesRequest> requests = LinkDuplicatesRequest.findAll();
		if (requests.size() == 0) {
			if (ideaOrg1 == ideaOrg2) {
				LinkDuplicatesRequest req = new LinkDuplicatesRequest(user, i1,
						i2, des);
				req.save();
				user.sentMarkingRequests.add(req);
				user.save();
				i1.belongsToTopic.creator.receivedMarkingRequests.add(req);
				i1.belongsToTopic.creator.save();
				for (int j = 0; j < i1.belongsToTopic.getOrganizer().size(); j++) {
					i1.belongsToTopic.getOrganizer().get(j).receivedMarkingRequests
							.add(req);
					i1.belongsToTopic.getOrganizer().get(j).save();
					user.sentMarkingRequests.add(req);
					user.save();
				}
			}
		} else {
			for (int i = 0; i < requests.size(); i++) {
				if ((requests.get(i).idea1.id == i1.id && requests.get(i).idea2.id == i2.id)
						|| (requests.get(i).idea1.id == i2.id && requests
								.get(i).idea2.id == i1.id)) {
				} else {
					if (ideaOrg1 == ideaOrg2) {
						LinkDuplicatesRequest req = new LinkDuplicatesRequest(
								user, i1, i2, des);
						req.save();
						user.sentMarkingRequests.add(req);
						user.save();
						i1.belongsToTopic.creator.receivedMarkingRequests
								.add(req);
						i1.belongsToTopic.creator.save();
						for (int j = 0; j < i1.belongsToTopic.getOrganizer()
								.size(); j++) {
							i1.belongsToTopic.getOrganizer().get(j).receivedMarkingRequests
									.add(req);
							i1.belongsToTopic.getOrganizer().get(j).save();
							user.sentMarkingRequests.add(req);
							user.save();
						}
					}
				}
			}
		}
	}

	/**
	 * @author Loaay Alkherbawy
	 * 
	 * @Story C4S10: marking of ideas as duplicate
	 * 
	 * @param reqID
	 *            : the id of the request
	 * 
	 *            this method performs the accepting action
	 */

	public static void accept(long reqID) {
		User user = Security.getConnected();
		LinkDuplicatesRequest req = LinkDuplicatesRequest.findById(reqID);
		Idea i1 = req.idea1;
		Idea i2 = req.idea2;
		i1.duplicateIdeas.add(i2);
		i1.save();
		i2.duplicateIdeas.add(i1);
		i2.save();
		User sender = req.sender;
		i1.belongsToTopic.creator.receivedMarkingRequests.remove(req);
		i1.belongsToTopic.creator.save();
		for (int i = 0; i < i1.belongsToTopic.getOrganizer().size(); i++) {
			if (i1.belongsToTopic.getOrganizer().get(i).id != user.id) {
				Notifications.sendNotification(i1.belongsToTopic.getOrganizer()
						.get(i).id, i1.id, "Idea",
						"An organizer approved the duplicating request");
			}
		}
		sender.sentMarkingRequests.remove(req);
		sender.save();
		req.delete();
		Notifications.sendNotification(sender.id, i1.id, "Idea",
				"An organizer approved your duplicating request");
	}

	/**
	 * @author Loaay Alkherbawy
	 * 
	 * @Story C4S10: marking of ideas as duplicate
	 * 
	 * @param reqID
	 *            : the id of the request
	 * 
	 *            this method performs the rejecting action
	 */

	public static void reject(long reqID) {
		LinkDuplicatesRequest req = LinkDuplicatesRequest.findById(reqID);
		Idea i1 = req.idea1;
		Idea i2 = req.idea2;
		User user = Security.getConnected();
		i1.duplicateIdeas.add(i2);
		i2.duplicateIdeas.add(i1);
		User sender = req.sender;
		i1.belongsToTopic.creator.receivedMarkingRequests.remove(req);
		i1.belongsToTopic.creator.save();
		for (int i = 0; i < i1.belongsToTopic.getOrganizer().size(); i++) {
			if (i1.belongsToTopic.getOrganizer().get(i).id != user.id) {
				Notifications.sendNotification(i1.belongsToTopic.getOrganizer()
						.get(i).id, i1.id, "Idea",
						"An organizer rejected the duplicating request");
			}
		}
		sender.sentMarkingRequests.remove(req);
		sender.save();
		req.delete();
		Notifications.sendNotification(sender.id, i1.id, "Idea",
				"An organizer rejected your duplicating request");
	}
}
