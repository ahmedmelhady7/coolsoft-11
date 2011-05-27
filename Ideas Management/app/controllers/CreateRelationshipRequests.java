package controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.chainsaw.Main;

import models.CreateRelationshipRequest;

import models.EntityRelationship;
import models.Log;
import models.MainEntity;
import models.Organization;
import models.RenameEndRelationshipRequest;
import models.RequestToJoin;
import models.Tag;
import models.Topic;
import models.TopicRelationship;
import models.User;

import models.CreateRelationshipRequest;
import models.MainEntity;
import models.Organization;
import models.Topic;
import models.User;

import play.mvc.With;

@With(Secure.class)
public class CreateRelationshipRequests extends CoolCRUD {

	/**
	 * 
	 * shows the list of relationship requests of an organization .
	 * 
	 * @author {Fadwa.sakr}
	 * 
	 * @story C2S19
	 * 
	 * @param id
	 *            : long id of the organization the requests belong to
	 */

	public static void viewRequests(long id) {

		List<RenameEndRelationshipRequest> deletionRequests = new ArrayList<RenameEndRelationshipRequest>();
		List<RenameEndRelationshipRequest> renamingRequests = new ArrayList<RenameEndRelationshipRequest>();

		User user = Security.getConnected();
		Organization organization = Organization.findById(id);
		notFoundIfNull(organization);
		String name = organization.name;
		List<CreateRelationshipRequest> requests = organization.createRelationshipRequest;
		List<RenameEndRelationshipRequest> allRequests = organization.renameEndRelationshipRequest;

		for (int i = 0; i < requests.size(); i++) {
			if ((requests.get(i).requester.state.equals("n"))
					|| (requests.get(i).type == 1 && (requests.get(i).sourceTopic.hidden || requests
							.get(i).destinationTopic.hidden))) {
				requests.remove(i);
				i--;
			}
		}
		for (int i = 0; i < allRequests.size(); i++) {
			if (allRequests.get(i).requestType == 0)
				deletionRequests.add(allRequests.get(i));
			else
				renamingRequests.add(allRequests.get(i));
		}
		for (int i = 0; i < renamingRequests.size(); i++) {
			if ((renamingRequests.get(i).requester.state.equals("n"))
					|| (renamingRequests.get(i).type == 1 && (renamingRequests
							.get(i).topicRelationship.source.hidden || renamingRequests
							.get(i).topicRelationship.destination.hidden))) {
				renamingRequests.remove(i);
				i--;
			}
		}
		for (int i = 0; i < deletionRequests.size(); i++) {
			if ((deletionRequests.get(i).requester.state.equals("n"))
					|| (deletionRequests.get(i).type == 1 && (deletionRequests
							.get(i).topicRelationship.source.hidden || deletionRequests
							.get(i).topicRelationship.destination.hidden))) {
				deletionRequests.remove(i);
				i--;
			}
		}
		if (Users
				.isPermitted(
						user,
						"Accept/Reject a request to start/end a relationship with other items",
						organization.id, "organization")
				|| user.isAdmin)
			render(requests, name, id, user, deletionRequests, renamingRequests);
		else
			BannedUsers.unauthorized();

	}

	/**
	 * 
	 * Performs the action of responding to a request sent by any user to an
	 * organization by accepting or rejecting the request and either way this
	 * request should be deleted from the DB upon responding to it.
	 * 
	 * @author {Fadwa.sakr}
	 * 
	 * @story C2S19
	 * 
	 * @param status
	 *            : int status to show whether the respond taken was accept (1)
	 *            or rejected (0)
	 * 
	 * @param id
	 *            : long id of the request to be responded to
	 * 
	 * @param type
	 *            : int type of the request whether it concerns an entity(0) or
	 *            a topic (1)
	 * 
	 * @param whichList
	 *            : int whichList the request belongs to (0) create relationship
	 *            request or (1)rename/end relationship request
	 */

	public static void respondToRequests(int status, long id, int type,
			int whichList) {
		Organization organization;
		User user = Security.getConnected();
		CreateRelationshipRequest createRequest;
		RenameEndRelationshipRequest renameRequest;
		if (whichList == 0) {
			createRequest = CreateRelationshipRequest.findById(id);
			organization = createRequest.organisation;
			if (type == 0) {

				MainEntity source = createRequest.sourceEntity;
				MainEntity destination = createRequest.destinationEntity;
	
				if (status == 1) {
					EntityRelationships.createRelationship(createRequest.name,
							source.id, destination.id);
					String logDescription = "<a href=\"http://localhost:9008/users/viewprofile?userId="
							+ user.id
							+ "\">"
							+ user.username
							+ " "
							+ "</a>"
							+ " has accepted request to create relationship "
							+ createRequest.name
							+" between entities "
							+ "<a href=\"http://localhost:9008/mainentitys/viewentity?id="
							+ source.id
							+ "\">"
							+ source.name
							+ "</a>"
							+ " and "
							+ "<a href=\"http://localhost:9008/mainentitys/viewentity?id="
							+ destination.id
							+ "\">"
							+ destination.name
							+ "</a>";

					Log.addUserLog(logDescription, organization, createRequest,
							destination, source);

				} else {
					String logDescription = "<a href=\"http://localhost:9008/users/viewprofile?userId="
							+ user.id
							+ "\">"
							+ user.username
							+ " "
							+ "</a>"
							+ " has rejectted request to create relationship between entities "
							+ "<a href=\"http://localhost:9008/mainentitys/viewentity?id="
							+ source.id
							+ "\">"
							+ source.name
							+ "</a>"
							+ " and "
							+ "<a href=\"http://localhost:9008/mainentitys/viewentity?id="
							+ destination.id
							+ "\">"
							+ destination.name
							+ "</a>";

					Log.addUserLog(logDescription, organization, createRequest,
							destination);
				}
			}

			else {
				
				Topic source = createRequest.sourceTopic;
				Topic destination = createRequest.destinationTopic;
				if (status == 1) {
					TopicRelationships.createRelationship(createRequest.name,
							source.id, destination.id);
					String logDescription = "<a href=\"http://localhost:9008/users/viewprofile?userId="
							+ user.id
							+ "\">"
							+ user.username
							+ " "
							+ "</a>"
							+ " has accepted request to create relationship " 
							+ createRequest.name 
							+" between topics "
							+ "<a href=\"http://localhost:9008/topics/show?id="
							+ source.id
							+ "\">"
							+ source.title
							+ "</a>"
							+ " and "
							+ "<a href=\"http://localhost:9008/topics/show?id="
							+ destination.id
							+ "\">"
							+ destination.title
							+ "</a>";

					Log.addUserLog(logDescription, organization, user,
							destination, source, source.entity);
				} else {
					String logDescription = "<a href=\"http://localhost:9008/users/viewprofile?userId="
							+ user.id
							+ "\">"
							+ user.username
							+ " "
							+ "</a>"
							+ " has accepted rejected to create relationship between topics "
							+ "<a href=\"http://localhost:9008/topics/show?id="
							+ source.id
							+ "\">"
							+ source.title
							+ "</a>"
							+ " and "
							+ "<a href=\"http://localhost:9008/topics/show?id="
							+ destination.id
							+ "\">"
							+ destination.title
							+ "</a>";

					Log.addUserLog(logDescription, organization, user,
							destination, source, source.entity);

				}
			}
			if (status == 1) {
				Notifications.sendNotification(createRequest.requester.id,
						organization.id, "organization",
						"Your Request has been accepted");
			} else {
				Notifications.sendNotification(createRequest.requester.id,
						organization.id, "organization",
						"Your Request has been rejected");

			}
			createRequest.delete();
		}

		else {
			renameRequest = RenameEndRelationshipRequest.findById(id);
			
			organization = renameRequest.organisation;
			if (type == 0) {
				EntityRelationship relation = renameRequest.entityRelationship;
				String oldName = relation.name;
				
				if (status == 1) {
					if (renameRequest.requestType == 0) {
				

						for (int i = 0; i < relation.renameEndRequests.size(); i++) {
							if (!relation.renameEndRequests.get(i).equals(
									renameRequest)) {
								relation.renameEndRequests.get(i).delete();
								relation.save();
							}
						}
						
						relation.source.relationsSource.remove(relation);
						relation.source.save();
						relation.destination.relationsDestination
								.remove(relation);
						relation.destination.save();
						String logDescription = "<a href=\"http://localhost:9008/users/viewprofile?userId="
								+ user.id
								+ "\">"
								+ user.username
								+ " "
								+ "</a>"
								+ " has accepted request to end relationship between entities "
								+ "<a href=\"http://localhost:9008/mainentitys/viewentity?id="
								+ relation.source.id
								+ "\">"
								+ relation.source.name
								+ "</a>"
								+ " and "
								+ "<a href=\"http://localhost:9008/mainentitys/viewentity?id="
								+ relation.destination.id
								+ "\">"
								+ relation.destination.name + "</a>";

						Log.addUserLog(logDescription, organization,
								relation.destination, relation.source);
						relation.renameEndRequests.remove(renameRequest);
						relation.logs.clear();
						relation.save();
						renameRequest.delete();

						relation.delete();

					} else {
					
						relation.name = renameRequest.newName;
						relation.save();
						for (int i = 0; i < relation.renameEndRequests.size(); i++) {
							relation.renameEndRequests.get(i).delete();
							relation.save();
						}

						String logDescription = "<a href=\"http://localhost:9008/users/viewprofile?userId="
								+ user.id
								+ "\">"
								+ user.username
								+ " "
								+ "</a>"
								+ " has accepted request to change the name of relationship "
								+ oldName
								+ " to"
								+ renameRequest.newName
								+ "between entities"
								+ "<a href=\"http://localhost:9008/mainentitys/viewentity?id="
								+ relation.source.id
								+ "\">"
								+ relation.source.name
								+ "</a>"
								+ " and "
								+ "<a href=\"http://localhost:9008/mainentitys/viewentity?id="
								+ relation.destination.id
								+ "\">"
								+ relation.destination.name + "</a>";

						Log.addUserLog(logDescription, organization,
								relation.destination, relation.source);
					}
				} else {
					if (renameRequest.requestType == 0) {
						String logDescription = "<a href=\"http://localhost:9008/users/viewprofile?userId="
								+ user.id
								+ "\">"
								+ user.username
								+ " "
								+ "</a>"
								+ " has rejected request to end relationship "
								+ relation.name
								+ " between entities "
								+ "<a href=\"http://localhost:9008/mainentitys/viewentity?id="
								+ relation.source.id
								+ "\">"
								+ relation.source.name
								+ "</a>"
								+ " and "
								+ "<a href=\"http://localhost:9008/mainentitys/viewentity?id="
								+ relation.destination.id
								+ "\">"
								+ relation.destination.name + "</a>";

						Log.addUserLog(logDescription, organization,
								relation.destination, relation.source);

					} else {
						String logDescription = "<a href=\"http://localhost:9008/users/viewprofile?userId="
								+ user.id
								+ "\">"
								+ user.username
								+ " "
								+ "</a>"
								+ " has rejected request to change the name of relationship "
								+ oldName
								+ " to "
								+ renameRequest.newName
								+ " between entities "
								+ "<a href=\"http://localhost:9008/mainentitys/viewentity?id="
								+ relation.source.id
								+ "\">"
								+ relation.source.name
								+ "</a>"
								+ " and "
								+ "<a href=\"http://localhost:9008/mainentitys/viewentity?id="
								+ relation.destination.id
								+ "\">"
								+ relation.destination.name + "</a>";

						Log.addUserLog(logDescription, organization,
								relation.destination, relation.source);

					}
					renameRequest.delete();
				}

			} else {
				TopicRelationship relation = renameRequest.topicRelationship;
				String oldName = relation.name;
				if (status == 1) {
					if (renameRequest.requestType == 0) {

					
						for (int i = 0; i < relation.renameEndRequests.size(); i++) {
							if (!relation.renameEndRequests.get(i).equals(
									renameRequest))
								relation.renameEndRequests.get(i).delete();
							relation.save();
						}
						relation.source.relationsSource.remove(relation);
						relation.source.save();
						relation.destination.relationsDestination
								.remove(relation);
						relation.destination.save();
						String logDescription = "<a href=\"http://localhost:9008/users/viewprofile?userId="
								+ user.id
								+ "\">"
								+ user.username
								+ " "
								+ "</a>"
								+ " has accepted request to end relationship between topics "
								+ "<a href=\"http://localhost:9008/topicss/show?id="
								+ relation.source.id
								+ "\">"
								+ relation.source.title
								+ "</a>"
								+ " and "
								+ "<a href=\"http://localhost:9008/mainentitys/viewentity?id="
								+ relation.destination.id
								+ "\">"
								+ relation.destination.title + "</a>";

						Log.addUserLog(logDescription, organization,
								relation.destination, relation.source,
								relation.source.entity,
								relation.destination.entity);

						renameRequest.delete();
						relation.delete();

					} else {
						
						relation.name = renameRequest.newName;
						relation.save();
						for (int i = 0; i < relation.renameEndRequests.size(); i++) {
							relation.renameEndRequests.get(i).delete();
							relation.save();
						}

						String logDescription = "<a href=\"http://localhost:9008/users/viewprofile?userId="
								+ user.id
								+ "\">"
								+ user.username
								+ " "
								+ "</a>"
								+ " has accepted request to change the name of relationship "
								+ oldName
								+ " to"
								+ renameRequest.newName
								+ "between topicss"
								+ "<a href=\"http://localhost:9008/topics/show?id="
								+ relation.source.id
								+ "\">"
								+ relation.source.title
								+ "</a>"
								+ " and "
								+ "<a href=\"http://localhost:9008/topics/show?id="
								+ relation.destination.id
								+ "\">"
								+ relation.destination.title + "</a>";

						Log.addUserLog(logDescription, organization,
								relation.destination, relation.source,
								relation.source.entity,
								relation.destination.entity);
					}
				} else {
					if (renameRequest.requestType == 0) {
						String logDescription = "<a href=\"http://localhost:9008/users/viewprofile?userId="
								+ user.id
								+ "\">"
								+ user.username
								+ " "
								+ "</a>"
								+ " has rejected request to end relationship between topics "
								+ "<a href=\"http://localhost:9008/topicss/show?id="
								+ relation.source.id
								+ "\">"
								+ relation.source.title
								+ "</a>"
								+ " and "
								+ "<a href=\"http://localhost:9008/mainentitys/viewentity?id="
								+ relation.destination.id
								+ "\">"
								+ relation.destination.title + "</a>";

						Log.addUserLog(logDescription, organization,
								relation.destination, relation.source,
								relation.source.entity,
								relation.destination.entity);

					} else {
						String logDescription = "<a href=\"http://localhost:9008/users/viewprofile?userId="
								+ user.id
								+ "\">"
								+ user.username
								+ " "
								+ "</a>"
								+ " has rejected request to change the name of relationship "
								+ oldName
								+ " to"
								+ renameRequest.newName
								+ "between topicss"
								+ "<a href=\"http://localhost:9008/topics/show?id="
								+ relation.source.id
								+ "\">"
								+ relation.source.title
								+ "</a>"
								+ " and "
								+ "<a href=\"http://localhost:9008/topics/show?id="
								+ relation.destination.id
								+ "\">"
								+ relation.destination.title + "</a>";

						Log.addUserLog(logDescription, organization,
								relation.destination, relation.source,
								relation.source.entity,
								relation.destination.entity);

					}
					renameRequest.delete();
				}

			}
			if (status == 1) {
				Notifications.sendNotification(renameRequest.requester.id,
						organization.id, "organization",
						"Your Request has been accepted");

			} else {
				Notifications.sendNotification(renameRequest.requester.id,
						organization.id, "organization",
						"Your Request has been rejected");
			}

		}
	}

	public static void createRequest(long userId, String source,
			String destination, String name, long organisationId,
			long entityId, long topicId, int type) {
		User user = User.findById(userId);
		Organization organisation = Organization.findById(organisationId);
		if (type == 0) {

			if (Organizations.isDuplicateRequest(source, destination, name,
					organisationId, type, 1, 0, 0)) {
				System.out.println(organisation.createRelationshipRequest
						.size());

				redirect("MainEntitys.viewEntity", entityId, "Request created");
				return;
			}

			MainEntity sourceEntity = MainEntity.find("byNameAndOrganization",
					source, organisation).first();
			MainEntity destinationEntity = MainEntity.find(
					"byNameAndOrganization", destination, organisation).first();
			CreateRelationshipRequest relationRequest = new CreateRelationshipRequest(
					user, sourceEntity, destinationEntity, name, organisation,
					type);
			relationRequest.save();
			Log.addUserLog(
					"User "
							+ user.firstName
							+ " "
							+ user.lastName
							+ " has requested to create the following relationship between"
							+ " entities: (" + sourceEntity.name + " " + name
							+ " " + destinationEntity.name + ")", sourceEntity,
					destinationEntity, relationRequest, organisation, user);
			System.out.println(organisation.createRelationshipRequest.size());
			redirect("MainEntitys.viewEntity", entityId, "Request created");
		} else {
			if (Organizations.isDuplicateRequest(source, destination, name,
					organisationId, type, 1, 0, 0)) {
				System.out.println(organisation.createRelationshipRequest
						.size());

				redirect("MainEntitys.viewEntity", entityId, "Request created");
				return;
			}
			MainEntity entity = MainEntity.findById(entityId);
			Topic sourceTopic = Topic.find("byTitleAndEntity", source, entity)
					.first();
			Topic destinationTopic = Topic.find("byTitleAndEntity",
					destination, entity).first();
			CreateRelationshipRequest relationRequest = new CreateRelationshipRequest(
					user, sourceTopic, destinationTopic, name, organisation,
					type);
			relationRequest.save();
			Log.addUserLog(
					"User "
							+ user.firstName
							+ " "
							+ user.lastName
							+ " has requested to create the following relationship between"
							+ " topics: (" + sourceTopic.title + " " + name
							+ " " + destinationTopic.title + ")", sourceTopic,
					destinationTopic, relationRequest, organisation, user);
			System.out.println(organisation.createRelationshipRequest.size());
			redirect("Topics.show", topicId, "Request created");

		}
	}

	public static boolean delete(Long id) {
		CreateRelationshipRequest request = CreateRelationshipRequest
				.findById(id);
		List<User> users = User.findAll();
		List<Organization> organizations = Organization.findAll();
		List<MainEntity> entities = MainEntity.findAll();
		List<Topic> topics = Topic.findAll();

		int size = users.size();
		for (int i = 0; i < size; i++) {
			if (users.get(i).myRelationshipRequests.contains(request)) {
				users.get(i).myRelationshipRequests.remove(request);
				users.get(i).save();
			}
		}
		for (int i = 0; i < size; i++) {
			if (organizations.get(i).createRelationshipRequest
					.contains(request)) {
				organizations.get(i).createRelationshipRequest.remove(request);
				organizations.get(i).save();
			}
		}
		for (int i = 0; i < size; i++) {
			if (entities.get(i).relationshipRequestsSource.contains(request)) {
				entities.get(i).relationshipRequestsSource.remove(request);
				entities.get(i).save();
			}
			if (entities.get(i).relationshipRequestsDestination
					.contains(request)) {
				entities.get(i).relationshipRequestsDestination.remove(request);
				entities.get(i).save();
			}
		}
		for (int i = 0; i < size; i++) {
			if (topics.get(i).relationshipRequestsSource.contains(request)) {
				topics.get(i).relationshipRequestsSource.remove(request);
				topics.get(i).save();
			}
			if (topics.get(i).relationshipRequestsDestination.contains(request)) {
				topics.get(i).relationshipRequestsDestination.remove(request);
				topics.get(i).save();
			}
		}
		request.delete();
		return true;
	}

}
