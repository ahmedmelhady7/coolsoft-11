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

	public static void viewRequests(short type, long id) {

		List<RequestToJoin> requests;
		if (type == 1) {
			Topic topic = Topic.findById(id);
			notFoundIfNull(topic);
			requests = topic.requestsToJoin;
		} else {
			Organization organization = Organization.findById(id);
			notFoundIfNull(organization);
			requests = organization.joinRequests;
		}

		render(requests);
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
	 * @param type
	 *            : to specify whether the request belongs to a topic or an
	 *            organization
	 * 
	 * @param reqId
	 *            : the id of the request to be responded to
	 */

	public static void respond(short status, short type, long reqId) {
		RequestToJoin request = RequestToJoin.findById(reqId);
		notFoundIfNull(request);
		User user = request.source;
		List<User> users = new ArrayList<User>();
		users.add(user);
		if (status == 1) {
			if (type == 0) {

				Organization organization = request.organization;
				Role role = Roles.getRoleByName("idea developer");
				UserRoleInOrganizations.addEnrolledUser(user, organization,
						role);
				organization.joinRequests.remove(request);
				Notifications.sendNotification(users, organization.id,
						"organization", "Your Request has been approved");

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
				Notifications.sendNotification(organizers, topic.id, "topic",
						" A new User has joined topic " + topic.title);

				Notifications.sendNotification(users, topic.id, "topic",
						"Your Request has been approved");
			}

		} else {
			if (type == 0) {

				Organization organization = request.organization;
				organization.joinRequests.remove(request);
				Notifications.sendNotification(users, organization.id,
						"organization", "Your Request has been rejected");

			} else {
				Topic topic = request.topic;
				topic.requestsToJoin.remove(request);
				Notifications.sendNotification(users, topic.id, "topic",
						"Your Request has been rejected");
			}

		}

	}
}
