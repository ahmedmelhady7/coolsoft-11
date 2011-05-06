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

	public static void markDuplicate(long idea1ID, long idea2ID, String des) {
		User u = Security.getConnected();
		List<LinkDuplicatesRequest> requests = u.receivedMarkingRequests;
		render(requests);
	}
	
	public static void sendRequest(long idea1ID, long idea2ID, String des) {
		User u = Security.getConnected();
		Idea i1 = Idea.findById(idea1ID);
		Idea i2 = Idea.findById(idea2ID);
		Long ideaOrg1 = i1.belongsToTopic.id;
		Long ideaOrg2 = i1.belongsToTopic.id;
		if (ideaOrg1 == ideaOrg2) {
			LinkDuplicatesRequest req = new LinkDuplicatesRequest(i1.belongsToTopic.creator, i1, i2, des);				
			req.save();
			u.sentMarkingRequests.add(req);
			u.save();
			for(int i = 0; i<i1.belongsToTopic.getOrganizer().size();i++){
				LinkDuplicatesRequest request = new LinkDuplicatesRequest(i1.belongsToTopic.getOrganizer().get(i), i1, i2, des);				
				request.save();
				i1.belongsToTopic.getOrganizer().get(i).receivedMarkingRequests.add(request);
				i1.belongsToTopic.getOrganizer().get(i).save();
				u.sentMarkingRequests.add(request);
				u.save();
			}
		}
	}
	
	public static void accept(long reqID){
		User u = Security.getConnected();
		LinkDuplicatesRequest req = LinkDuplicatesRequest.findById(reqID);
		Idea i1 = req.idea1;
		Idea i2 = req.idea2;
		i1.duplicateIdeas.add(i2);
		i2.duplicateIdeas.add(i1);
		User sender = req.sender;
	//	for(int i = 0;i<i1.belongsToTopic.creator.size();i++){
			i1.belongsToTopic.creator.receivedMarkingRequests.remove(req);
			i1.belongsToTopic.creator.save();
//			if(i1.belongsToTopic.getOrganizer().get(i).id != u.id){
//			Notifications.sendNotification(i1.belongsToTopic.getOrganizer().get(i).id, i1.id, "Idea",
//					"An organizer approved the duplicating request");
//			}			
//		}
		sender.sentMarkingRequests.remove(req);
		sender.save();
		req.delete();
		Notifications.sendNotification(sender.id, i1.id, "Idea",
		"An organizer approved your duplicating request");
	}
	
	public static void reject(long reqID){
		LinkDuplicatesRequest req = LinkDuplicatesRequest.findById(reqID);
		Idea i1 = req.idea1;
		Idea i2 = req.idea2;
		User u = Security.getConnected();
		i1.duplicateIdeas.add(i2);
		i2.duplicateIdeas.add(i1);
		User sender = req.sender;
		//for(int i = 0;i<i1.belongsToTopic.getOrganizer().size();i++){
			i1.belongsToTopic.creator.receivedMarkingRequests.remove(req);
			i1.belongsToTopic.creator.save();
//			if(i1.belongsToTopic.getOrganizer().get(i).id != u.id){
//			Notifications.sendNotification(i1.belongsToTopic.getOrganizer().get(i).id, i1.id, "Idea",
//					"An organizer rejected the duplicating request");
//			}
//		}
		sender.sentMarkingRequests.remove(req);
		sender.save();
		req.delete();
		Notifications.sendNotification(sender.id, i1.id, "Idea",
		"An organizer rejected your duplicating request");
	}
}
