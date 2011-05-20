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
		// type=1;
		System.out.println("will view");
		List<RequestToJoin> requests;
		String name;
		if (type == 1) {
			Topic topic = Topic.findById(id);
			notFoundIfNull(topic);
			requests = topic.requestsToJoin;
			name = topic.title;
			for(int i =0;i<requests.size();i++){
				if(requests.get(i).topic.hidden){
					requests.remove(i);
					i--;
				}
		}
		} else {
			Organization organization = Organization.findById(id);
			System.out.println(organization);
			notFoundIfNull(organization);
			requests = organization.joinRequests;
			name = organization.name;
		
		}
		User user;
		for (int i = 0; i < requests.size(); i++) {
			user = requests.get(i).source;
			if (user.state.equals("n")) {
				requests.remove(i);
				i--;
			}
		}

		render(requests, type, id, name);
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
		System.out.println("da5alt");
		RequestToJoin request = RequestToJoin.findById(requestId);
		System.out.println(request);
		notFoundIfNull(request);
		User user = request.source;
		List<User> users = new ArrayList<User>();
		// users.add(user);
		System.out.println(users);
		User reciever = Security.getConnected();
		if (status == 1) {
			System.out.println("request will be accepeted");
			if (request.organization != null) {

				Organization organization = request.organization;
				Role role = Roles.getRoleByName("idea developer");
				UserRoleInOrganizations.addEnrolledUser(user, organization,
						role);
				organization.joinRequests.remove(request);
				System.out.println(organization.joinRequests);
				System.out.println(users);
				Log.addUserLog("User " + reciever.firstName + " "
						+ reciever.lastName + " has accepted user "
						+ user.firstName + " " + user.lastName
						+ " request to join organization " + organization.name,
						organization, request);
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

				Log.addUserLog("User " + reciever.firstName + " "
						+ reciever.lastName + " has accepted user "
						+ user.firstName + " " + user.lastName
						+ " request to join topic " + topic.title,
						organization, request,topic);

				for (int i = 0; i < organizers.size(); i++)
					Notifications.sendNotification(organizers.get(i).id,
							topic.id, "Topic", " A new User " + user.username
									+ " has joined topic " + topic.title);

				Notifications.sendNotification(user.id, topic.id, "Topic",
						"Your Request has been approved");

			}

		} else {
			if (request.organization != null) {
				System.out.println("request will be rejected");
				Organization organization = request.organization;
				organization.joinRequests.remove(request);
				Log.addUserLog("User " + reciever.firstName + " "
						+ reciever.lastName + " has rejected user "
						+ user.firstName + " " + user.lastName
						+ " request to join organization " + organization.name,
						organization, request,reciever,user);
				Notifications.sendNotification(user.id, organization.id,
						"Organization", "Your Request has been rejected");

			} else {
				Topic topic = request.topic;
				MainEntity entity = topic.entity;
				Organization organization = entity.organization;
				topic.requestsToJoin.remove(request);
				Log.addUserLog("User " + reciever.firstName + " "
						+ reciever.lastName + " has rejected user "
						+ user.firstName + " " + user.lastName
						+ " request to join organization " + topic.title,
						topic, request,reciever,user,organization);

				// Notifications.sendNotification(users, topic.id, "Topic",
				// "Your Request has been rejected");

				Notifications.sendNotification(user.id, topic.id, "Topic",
						"Your Request has been rejected");

			}

		}
		request.delete();
		System.out.println("request will be deleted");

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
		if ((!Users.getEnrolledUsers(organization).contains(requester))
				&& (organization.privacyLevel == 1)) {
			RequestToJoin request = new RequestToJoin(requester, null,
					organization, description).save();
			organization.joinRequests.add(request);
			Login.homePage();
		}
	}
}
