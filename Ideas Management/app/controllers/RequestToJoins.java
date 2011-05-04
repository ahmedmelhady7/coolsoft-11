package controllers;

import java.util.ArrayList;
import java.util.List;

import models.MainEntity;
import models.Organization;
import models.RequestToJoin;
import models.Role;
import models.Topic;
import models.User;
import play.mvc.Controller;

public class RequestToJoins extends CRUD {

	/**
	 * 
	 * This method shows the list of requests of an organization or a topic
	 * 
	 * @author Fadwa sakr
	 * 
	 * @story C2S3 , C2S21
	 * 
	 * @param type
	 *            : to specify whether the given id belong to a topic or an
	 *            organization
	 * 
	 * @param id
	 *            : the id of the organization or topic the requests belong to
	 */

	public static void viewRequests(int type, long id) {
             type=1;
		List<RequestToJoin> requests;
		if (type == 1) {
			Topic topic = Topic.findById((long)1);
			notFoundIfNull(topic);
			requests = topic.requestsToJoin;
		} else {
			Organization organization = Organization.findById(id);
			notFoundIfNull(organization);
			requests = organization.joinRequests;
		}

		render(requests,type);
	}

	/**
	 * 
	 * This method performs the action of responding to a request sent by any
	 * user to an organization or a topic either by accepting or rejecting the
	 * request and either way this request should be deleted from the DB upon
	 * responding to it.
	 * 
	 * @author Fadwa sakr
	 * 
	 * @story C2S3 , C2S21
	 * 
	 * @param status
	 *            : to show whether the respond taken was accept (1) or rejected
	 *            (0)
	 * 
	 * @param reqId
	 *            : the id of the request to be responded to
	 */

	public static void respondToRequest(int status, long reqId) {
		System.out.println("da5alt");
		RequestToJoin request = RequestToJoin.findById(reqId);
		System.out.println(status + "" + reqId);
		System.out.println(request);
		notFoundIfNull(request);
		User user = request.source;
		List<User> users = new ArrayList<User>();
		users.add(user);
		System.out.println(users);
		if (status == 1) {
			System.out.println("request will be accepeted");
			if (request.organization!= null) {

				Organization organization = request.organization;
				//Role role = Roles.getRoleByName("idea developer");
				//UserRoleInOrganizations.addEnrolledUser(user, organization,
					//	role);
				organization.joinRequests.remove(request);
				System.out.println(organization.joinRequests);
				System.out.println(users);
			//	Notifications.sendNotification(users, organization.id,
				//		"Organization", "Your Request has been approved");

			} else {
				Topic topic = request.topic;
				MainEntity entity = topic.entity;
				List organizers = Users.getEntityOrganizers(entity);
				Organization organization = entity.organization;
				organizers.add(organization.creator);
				Role role = Roles.getRoleByName("idea developer");
				UserRoleInOrganizations.addEnrolledUser(user, organization,
						role, topic.id, "Topic");
				topic.requestsToJoin.remove(request);
				Notifications.sendNotification(organizers, topic.id, "Topic",
						" A new User has joined topic " + topic.title);

				Notifications.sendNotification(users, topic.id, "Topic",
						"Your Request has been approved");
			}

		} else {
			if (request.organization!=null) {
                   System.out.println("request will be rejected");
				Organization organization = request.organization;
				organization.joinRequests.remove(request);
				//Notifications.sendNotification(users, organization.id,
				//		"Organization", "Your Request has been rejected");

			} else {
				Topic topic = request.topic;
				topic.requestsToJoin.remove(request);
				Notifications.sendNotification(users, topic.id, "Topic",
						"Your Request has been rejected");
			}

		}
		request.delete();
		System.out.println("request will be deleted");

	}
	/**
	 * This method allows the user to request to join a specific organization only if the organization is a private one
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S15
	 * 
	 * @param requester : The user sending the request
	 * 
	 * @param organization : The organization that the user is requesting to join
	 * 
	 * @param description : A text message that will be sent along with the request
	 */
	public static void requestToJoinOrganization(User requester, Organization organization, String description) {
		if (!organization.enrolledUsers.contains(requester) && (organization.privacyLevel == 1)) {
		RequestToJoin r = new RequestToJoin(requester, null, organization, description).save();
		organization.joinRequests.add(r);
		}
	}
}
