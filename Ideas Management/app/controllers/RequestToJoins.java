package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Log;
import models.MainEntity;
import models.Organization;
import models.RequestToJoin;
import models.Role;
import models.Topic;
import models.User;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class RequestToJoins extends CoolCRUD {

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
		List<RequestToJoin> requests = new ArrayList<RequestToJoin>();
		String name;
		Topic topic = null;
		Organization organization = null;
		User user = Security.getConnected();
		if (type == 1) {
			topic = Topic.findById(id);
			notFoundIfNull(topic);
			if (!topic.hidden)
				requests = topic.requestsToJoin;
			name = topic.title;
		} else {
			organization = Organization.findById(id);
			notFoundIfNull(organization);
			requests = organization.joinRequests;
			name = organization.name;

		}
		User requester;
		for (int i = 0; i < requests.size(); i++) {
			requester = requests.get(i).source;
			if (requester.state.equals("n")) {
				requests.remove(i);
				i--;
			}
		}
		if (type == 1) {
			if (topic.privacyLevel == 1
					&& Users.isPermitted(
							user,
							"Accept/Reject requests to post in a private topic in entities he/she manages",
							id, "topic") || user.isAdmin)
				render(requests, type, id, name, user);
		} else {
			if (organization.privacyLevel == 1
					&& Users.isPermitted(
							user,
							"accept/reject join requests from users to join a private organization",
							id, "organization") || user.isAdmin)
				render(requests, type, id, name, user);
		}
		BannedUsers.unauthorized();
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

	public static void respondToRequest(int status, long requestId) {
		RequestToJoin request = RequestToJoin.findById(requestId);
		notFoundIfNull(request);
		User user = request.source;
		List<User> users = new ArrayList<User>();
		User reciever = Security.getConnected();
		if (status == 1) {
			if (request.organization != null) {

				Organization organization = request.organization;
				Role role = Roles.getRoleByName("idea developer");
				UserRoleInOrganizations.addEnrolledUser(user, organization,
						role);
				organization.joinRequests.remove(request);
				String logDescription = "<a href=\"http://localhost:9008/users/viewprofile?userId="
						+ reciever.id
						+ "\">"
						+ reciever.username
						+ " "
						+ "</a>"
						+ " has accepted user "
						+ "<a href=\"http://localhost:9008/users/viewprofile?userId="
						+ user.id
						+ "\">"
						+ user.username
						+ " "
						+ "</a>"
						+ " request to join "
						+ "<a href=\"http://localhost:9008/organizations/viewProfile?id="
						+ organization.id
						+ "\">"
						+ organization.name
						+ "</a>"
						+ " organization";

				Log.addUserLog(logDescription, user, reciever, organization);
				Notifications.sendNotification(user.id, organization.id,
						"Organization", "Your Request has been approved");

			} else {
				Topic topic = request.topic;
				MainEntity entity = topic.entity;
				List<User> organizers = Users.getEntityOrganizers(entity);
				Organization organization = entity.organization;

				organizers.remove(reciever);
				// if(!organizers.contains(organization.creator))
				organizers.add(organization.creator);
				Role role = Roles.getRoleByName("idea developer");
				UserRoleInOrganizations.addEnrolledUser(user, organization,
						role, topic.id, "topic");
				topic.requestsToJoin.remove(request);
				String logDescription = "<a href=\"http://localhost:9008/users/viewprofile?userId="
						+ reciever.id
						+ "\">"
						+ reciever.username
						+ " "
						+ "</a>"
						+ " has accepted user "
						+ "<a href=\"http://localhost:9008/users/viewprofile?userId="
						+ user.id
						+ "\">"
						+ user.username
						+ "</a>"
						+ " request to join "
						+ "<a href=\"http://localhost:9008/topics/show?id="
						+ topic.id + "\">" + topic.title + "</a>" + " topic";
				Log.addUserLog(logDescription, organization, entity, topic);

				for (int i = 0; i < organizers.size(); i++)
					Notifications.sendNotification(organizers.get(i).id,
							topic.id, "Topic", " A new User " + user.username
									+ " has joined topic " + topic.title);

				Notifications.sendNotification(user.id, topic.id, "Topic",
						"Your Request has been approved");

			}

		} else {
			if (request.organization != null) {
				Organization organization = request.organization;
				organization.joinRequests.remove(request);
				String logDescription = "<a href=\"http://localhost:9008/users/viewprofile?userId="
						+ reciever.id
						+ "\">"
						+ reciever.username
						+ " "
						+ "</a>"
						+ " has rejected user "
						+ "<a href=\"http://localhost:9008/users/viewprofile?userId="
						+ user.id
						+ "\">"
						+ user.username
						+ " "
						+ "</a>"
						+ " request to join "
						+ "<a href=\"http://localhost:9008/organizations/viewProfile?id="
						+ organization.id
						+ "\">"
						+ organization.name
						+ "</a>"
						+ " organization";

				Log.addUserLog(logDescription, user, reciever, organization);
				Notifications.sendNotification(user.id, organization.id,
						"Organization", "Your Request has been rejected");

			} else {
				Topic topic = request.topic;
				MainEntity entity = topic.entity;
				Organization organization = entity.organization;
				topic.requestsToJoin.remove(request);
				String logDescription = "<a href=\"http://localhost:9008/users/viewprofile?userId="
						+ reciever.id
						+ "\">"
						+ reciever.username
						+ " "
						+ "</a>"
						+ " has rejected user "
						+ "<a href=\"http://localhost:9008/users/viewprofile?userId="
						+ user.id
						+ "\">"
						+ user.username
						+ " "
						+ "</a>"
						+ "request to join "
						+ "<a href=\"http://localhost:9008/topics/show?id="
						+ topic.id + "\">" + topic.title + "</a>" + " topic";
				Log.addUserLog(logDescription, organization, entity, topic);

				Notifications.sendNotification(user.id, topic.id, "Topic",
						"Your Request has been rejected");

			}

		}
		request.delete();

	}

	/**
	 * This method allows the user to request to join a specific organization
	 * only if the organization is a private one
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S15
	 * 
	 * @param orgId
	 *            The Id of the desired organization
	 * 
	 * @param description
	 *            The message to be sent when sending the request
	 */

	public static void requestToJoinOrganization(long orgId, String description) {
		User requester = Security.getConnected();
		Organization organization = Organization.findById(orgId);
		notFoundIfNull(organization);
		if ((!Users.getEnrolledUsers(organization).contains(requester))
				&& (organization.privacyLevel == 1)) {
			RequestToJoin request = new RequestToJoin(requester, null,
					organization, description).save();
			organization.joinRequests.add(request);
			Log.addUserLog(
					"<a href=\"http://localhost:9008/users/viewprofile?userId="
							+ requester.id
							+ "\">"
							+ requester.firstName
							+ " "
							+ requester.lastName
							+ "</a>"
							+ " has requested to join the organisation ("
							+ "<a href=\"http://localhost:9008/organizations/viewprofile?id="
							+ organization.id + "\">" + organization.name
							+ "</a>" + ")", requester, organization);
			flash.success("Your request has been successfully been sent!!");
			Login.homePage();
		}
	}
}
