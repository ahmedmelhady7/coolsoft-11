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
	 * 
	 * @description: this method renders the view of the duplicate requests
	 * 
	 * @return void
	 */

	public static void markDuplicate() {
		User user = Security.getConnected();
		List<LinkDuplicatesRequest> allRequests = LinkDuplicatesRequest
				.findAll();
		List<LinkDuplicatesRequest> requests = new ArrayList<LinkDuplicatesRequest>();
		for (int i = 0; i < allRequests.size(); i++) {
			if (user.id == allRequests.get(i).idea1.belongsToTopic.creator.id) {
				requests.add(allRequests.get(i));
			}
			for (int j = 0; j < allRequests.get(i).idea1.belongsToTopic
					.getOrganizer().size(); j++) {
				if (user.id == allRequests.get(i).idea1.belongsToTopic
						.getOrganizer().get(j).id
						&& allRequests.get(i).idea1.belongsToTopic
								.getOrganizer().get(j).id != allRequests.get(i).idea1.belongsToTopic.creator.id) {
					requests.add(allRequests.get(i));
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
	 * 
	 * @return int : 0 indicating that the request is sent 1 indicating that
	 *         ideas belong to different topics 2 indicating that a request is
	 *         already sent to organizer 01 indicating a failure
	 * 
	 * @description: this method sends a request to the organizers
	 * 
	 */

	public static int sendRequest(long idea1ID, long idea2ID, String des) {
		User user = Security.getConnected();
		Idea i1 = Idea.findById(idea1ID);
		Idea i2 = Idea.findById(idea2ID);
		notFoundIfNull(i1);
		notFoundIfNull(i2);
		Long ideaOrg1 = i1.belongsToTopic.id;
		Long ideaOrg2 = i2.belongsToTopic.id;
		List<LinkDuplicatesRequest> requests = LinkDuplicatesRequest.findAll();
		int returnint = -1;
		if (requests.size() == 0) {
			if (ideaOrg1 == ideaOrg2) {
				LinkDuplicatesRequest request = new LinkDuplicatesRequest(user,
						i1, i2, des);
				request.save();
				user.sentMarkingRequests.add(request);
				user.save();
				i1.belongsToTopic.creator.receivedMarkingRequests.add(request);
				i1.belongsToTopic.creator.save();
				for (int j = 0; j < i1.belongsToTopic.getOrganizer().size(); j++) {
					i1.belongsToTopic.getOrganizer().get(j).receivedMarkingRequests
							.add(request);
					i1.belongsToTopic.getOrganizer().get(j).save();
					user.sentMarkingRequests.add(request);
					user.save();
				}
				returnint = 0;
			} else {
				returnint = 1;
			}
		} else {
			for (int i = 0; i < requests.size(); i++) {
				if ((requests.get(i).idea1.id == i1.id && requests.get(i).idea2.id == i2.id)
						|| (requests.get(i).idea1.id == i2.id && requests
								.get(i).idea2.id == i1.id)) {
					return 2;
				}
			}
			if (ideaOrg1 == ideaOrg2) {
				LinkDuplicatesRequest request = new LinkDuplicatesRequest(user,
						i1, i2, des);
				request.save();
				user.sentMarkingRequests.add(request);
				user.save();
				i1.belongsToTopic.creator.receivedMarkingRequests.add(request);
				i1.belongsToTopic.creator.save();
				for (int j = 0; j < i1.belongsToTopic.getOrganizer().size(); j++) {
					i1.belongsToTopic.getOrganizer().get(j).receivedMarkingRequests
							.add(request);
					i1.belongsToTopic.getOrganizer().get(j).save();
					user.sentMarkingRequests.add(request);
					user.save();
				}
				returnint = 0;
			} else {
				returnint = 1;
			}
		}
		return returnint;
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
	 * 
	 * @return void
	 * 
	 * @descriptions: makes the link between the two ideas on accepting the
	 *                request
	 */

	public static void accept(long reqID) {
		User user = Security.getConnected();
		LinkDuplicatesRequest request = LinkDuplicatesRequest.findById(reqID);
		Idea i1 = request.idea1;
		Idea i2 = request.idea2;
		i1.duplicateIdeas.add(i2);
		i1.save();
		i2.duplicateIdeas.add(i1);
		i2.save();
		User sender = request.sender;
		i1.belongsToTopic.creator.receivedMarkingRequests.remove(request);
		i1.belongsToTopic.creator.save();
		for (int i = 0; i < i1.belongsToTopic.getOrganizer().size(); i++) {
			if (i1.belongsToTopic.getOrganizer().get(i).id != user.id) {
				Notifications.sendNotification(i1.belongsToTopic.getOrganizer()
						.get(i).id, i1.id, "Idea",
						"An organizer approved the duplicating request");
			}
		}
		sender.sentMarkingRequests.remove(request);
		sender.save();
		request.delete();
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
	 * 
	 * @return void
	 * 
	 * @descriptions: rejects the request and removes it from the database
	 */

	public static void reject(long reqID) {
		LinkDuplicatesRequest request = LinkDuplicatesRequest.findById(reqID);
		Idea i1 = request.idea1;
		Idea i2 = request.idea2;
		User user = Security.getConnected();
		i1.duplicateIdeas.add(i2);
		i2.duplicateIdeas.add(i1);
		User sender = request.sender;
		i1.belongsToTopic.creator.receivedMarkingRequests.remove(request);
		i1.belongsToTopic.creator.save();
		for (int i = 0; i < i1.belongsToTopic.getOrganizer().size(); i++) {
			if (i1.belongsToTopic.getOrganizer().get(i).id != user.id) {
				Notifications.sendNotification(i1.belongsToTopic.getOrganizer()
						.get(i).id, i1.id, "Idea",
						"An organizer rejected the duplicating request");
			}
		}
		sender.sentMarkingRequests.remove(request);
		sender.save();
		request.delete();
		Notifications.sendNotification(sender.id, i1.id, "Idea",
				"An organizer rejected your duplicating request");
	}
}
