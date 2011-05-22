package controllers;

import java.util.ArrayList;
import java.util.List;

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
public class CreateRelationshipRequests extends CRUD {

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
		String name = organization.name;
		List<CreateRelationshipRequest> requests = organization.createRelationshipRequest;
		List<RenameEndRelationshipRequest> allRequests =organization.renameEndRelationshipRequest;

		for (int i = 0; i < requests.size(); i++) {
			if (requests.get(i).requester.state.equals("n")) {
				requests.remove(i);
				i--;
			}
		}
		for(int i=0;i<allRequests.size();i++){
		if(allRequests.get(i).requestType==0)
			deletionRequests.add(allRequests.get(i));
		else
			renamingRequests.add(allRequests.get(i));
		}
			for (int i = 0; i < renamingRequests.size(); i++) {
				if (renamingRequests.get(i).requester.state.equals("n")) {
					renamingRequests.remove(i);
					i--;
				}
		}
			for (int i = 0; i < deletionRequests.size(); i++) {
				if (deletionRequests.get(i).requester.state.equals("n")) {
					deletionRequests.remove(i);
					i--;
				}
		}
		render(requests, name, id, user,deletionRequests,renamingRequests);

	}

	public static void cancelRequest(int type, long id, int list) {
		List<User> sourceOrganizers = new ArrayList<User>();
		List<User> destinationOrganizers = new ArrayList<User>();
		Organization organization;
		User user = Security.getConnected();
		CreateRelationshipRequest request = CreateRelationshipRequest
				.findById(id);
		if (type == 0) {
			MainEntity source = request.sourceEntity;
			MainEntity destination = request.destinationEntity;
			source.relationshipRequestsSource.remove(request);
			destination.relationshipRequestsDestination.remove(request);
			organization = source.organization;
			sourceOrganizers = Users.getEntityOrganizers(source);
			destinationOrganizers = Users.getEntityOrganizers(destination);
			for (int i = 0; i < sourceOrganizers.size(); i++)
				Notifications
						.sendNotification(
								sourceOrganizers.get(i).id,
								source.id,
								"entity",
								" User "
										+ user.username
										+ " has Cancelled pending request to create relationship with entity "
										+ destination.name);
			for (int i = 0; i < destinationOrganizers.size(); i++)
				Notifications
						.sendNotification(
								destinationOrganizers.get(i).id,
								destination.id,
								"entity",
								" User "
										+ user.username
										+ " has Cancelled pending request to create relationship with entity "
										+ destination.name);
			Log.addLog(
					"User "
							+ user.firstName
							+ " "
							+ user.lastName
							+ " canceled pending request to create relationship with entity "
							+ destination.name, organization, user, source,
					destination);
		} else if (type == 1) {
			Topic source = request.sourceTopic;
			Topic destination = request.destinationTopic;
			source.relationshipRequestsSource.remove(request);
			destination.relationshipRequestsDestination.remove(request);
			MainEntity sourceEntity = source.entity;
			MainEntity destinantionEntity = destination.entity;
			organization = sourceEntity.organization;
			if (!sourceEntity.equals(destinantionEntity)) {
				sourceOrganizers = Users.getEntityOrganizers(sourceEntity);
				destinationOrganizers = Users
						.getEntityOrganizers(destinantionEntity);
				for (int i = 0; i < sourceOrganizers.size(); i++)

					Notifications
							.sendNotification(
									sourceOrganizers.get(i).id,
									source.id,
									"topic",
									" User "
											+ user.username
											+ " has cancelled pending request to create relationship with topic "
											+ destination.title);
				for (int i = 0; i < destinationOrganizers.size(); i++)
					Notifications
							.sendNotification(
									destinationOrganizers.get(i).id,
									destination.id,
									"topic",
									" User "
											+ user.username
											+ " has cancelled pending request to create relationship with topic "
											+ destination.title);

			} else {
				destinationOrganizers = Users
						.getEntityOrganizers(destinantionEntity);
				for (int i = 0; i < destinationOrganizers.size(); i++)

					Notifications
							.sendNotification(
									destinationOrganizers.get(i).id,
									source.id,
									"topic",
									" User "
											+ user.username
											+ " has cancelled pending request to create relationship with topic "
											+ destination.title);

			}
			Log.addLog(
					"User "
							+ user.firstName
							+ " "
							+ user.lastName
							+ " canceled pending request to create relationship with topic "
							+ destination.title, organization, user, source,
					destination);

		} else {
			// tags
		}

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
		System.out.println("here");
		System.out.println(status);
		System.out.println(id);
		System.out.println(type);
		System.out.println(whichList);
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
				// organization = source.organization;
				if (status == 1) {
					EntityRelationships.createRelationship(createRequest.name,
							source.id, destination.id);
					System.out.println(user);
					System.out.println(organization);
					Log.addLog(
							"User "
									+ user.firstName
									+ " "
									+ user.lastName
									+ " accepted request to create relationship between entities "
									+ destination.name + " and " + source.name,
							organization, user, source, destination);
					System.out.println("logs");

				} else {
					Log.addLog(
							"User "
									+ user.firstName
									+ " "
									+ user.lastName
									+ " rejected request to create relationship between entities "
									+ destination.name + " and " + source.name,
							organization, user, source, destination);

				}
			}

			else {
				System.out.println("topic");
				Topic source = createRequest.sourceTopic;
				Topic destination = createRequest.destinationTopic;
				// MainEntity sourceEntity = source.entity;
				// organization = sourceEntity.organization;
				if (status == 1) {
					TopicRelationships.createRelationship(createRequest.name,
							source.id, destination.id);
					Log.addLog(
							"User "
									+ user.firstName
									+ " "
									+ user.lastName
									+ " accepted request to create relationship between topics "
									+ destination.title + " and "
									+ source.title, organization, user, source,
							destination);
				} else {
					Log.addLog(
							"User "
									+ user.firstName
									+ " "
									+ user.lastName
									+ " rejected request to create relationship between topics "
									+ destination.title + " and "
									+ source.title, organization, user, source,
							destination);
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
			System.out.println(renameRequest);
            organization=renameRequest.organisation;
			if (type == 0) {
				EntityRelationship relation = renameRequest.entityRelationship;
				String oldName = relation.name;
				System.out.println("oldName");
				if (status == 1) {
					if (renameRequest.requestType == 0) {
						System.out.println(relation);
					for(int i=0;i<relation.renameEndRequests.size();i++){
						if(!relation.renameEndRequests.get(i).equals(renameRequest))
							relation.renameEndRequests.get(i).delete();
					}
					relation.source.relationsSource.remove(relation);
					relation.destination.relationsDestination.remove(relation);
						relation.delete();
						//EntityRelationships.delete(relation.id);
					Log.addLog("User " + user.firstName + " "
							+ user.lastName
							+ " accepted request to end relationship  "
								+ oldName, user,organization);
						
					} else {
						EntityRelationships.renameRelationship(relation.id,
								renameRequest.newName);
						Log.addLog(
								"User "
										+ user.firstName
										+ " "
										+ user.lastName
										+ " accepted request to change the name of relationship  "
										+ oldName + " to "
										+ renameRequest.newName, user,organization,relation);
					
					}
				} else {
					if (renameRequest.requestType == 0) {
						Log.addLog("User " + user.firstName + " "
								+ user.lastName
								+ " rejected request to end relationship  "
								+ oldName, user,organization,relation);
					
					} else {
						Log.addLog(
								"User "
										+ user.firstName
										+ " "
										+ user.lastName
										+ " rejected request to change the name of relationship  "
										+ oldName + " to "
										+ renameRequest.newName, user,organization,relation);
					}

				}
			} else {
				TopicRelationship relation = renameRequest.topicRelationship;
				String oldName = relation.name;
				if (status == 1) {
					if (renameRequest.requestType == 0) {
						TopicRelationships.delete(relation.id);
						Log.addLog("User " + user.firstName + " "
								+ user.lastName
								+ " accepted request to end relationship  "
								+ oldName, user,organization,relation);
					} else {
						TopicRelationships.renameRelationship(relation.id,
								renameRequest.newName);
						Log.addLog(
								"User "
										+ user.firstName
										+ " "
										+ user.lastName
										+ " accepted request to change the name of relationship  "
										+ oldName + " to "
										+ renameRequest.newName, user,organization,relation);
	
					}
				} else {
					if (renameRequest.requestType == 0) {
						Log.addLog("User " + user.firstName + " "
								+ user.lastName
								+ " rejected request to end relationship  "
								+ oldName, user,organization,relation);
			
					} else {
						Log.addLog(
								"User "
										+ user.firstName
										+ " "
										+ user.lastName
										+ " rejected request to change the name of relationship  "
										+ oldName + " to "
										+ renameRequest.newName, user,organization,relation);
					}

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

			renameRequest.delete();

		}
	}


	public static void createRequest(long userId, String source,
			String destination, String name, long organisationId,
			long entityId, long topicId, int type) {
		User user = User.findById(userId);
		Organization organisation = Organization.findById(organisationId);
		if (type == 0) {

			if (Organizations.isDuplicateRequest(source, destination, name, organisationId, type, 1,
					0, 0)) {
				System.out.println(organisation.createRelationshipRequest.size());

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
			if (Organizations.isDuplicateRequest(source, destination, name, organisationId, type, 1,
					0, 0)) {
				System.out.println(organisation.createRelationshipRequest.size());

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

	}
