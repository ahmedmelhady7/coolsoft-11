package controllers;

import models.EntityRelationship;
import models.Log;
import models.MainEntity;
import models.Organization;
import models.RenameEndRelationshipRequest;
import models.Topic;
import models.TopicRelationship;
import models.User;
import play.mvc.With;

@With(Secure.class)
public class RenameEndRelationshipRequests extends CoolCRUD {

	/**
	 * creates a request for renaming an entity or topic relationship
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
	 * 
	 * @param userId
	 *            the id of the user making the request.
	 * 
	 * @param organisationId
	 *            the id of the organisation to which the entity or topic
	 *            belongs.
	 * 
	 * @param entityId
	 *            the id of the entity from which the request page was accessed.
	 * 
	 * @param topicId
	 *            the id of the topic from which the request page was accessed.
	 * 
	 * @param relationId
	 *            the id of the relationship to be renamed.
	 * 
	 * @param type
	 *            the type of the relationship request whether for an entity or
	 *            topic. (0=entity, 1=topic).
	 * 
	 * @param requestType
	 *            the type of the request whether rename or delete. (0=delete,
	 *            1=rename).
	 * 
	 * @param newName
	 *            the new name for the relationship to be renamed.
	 */
	public static void renameRequest(long userId, long organisationId,
			long entityId, long topicId, long relationId, int type,
			int requestType, String newName) {
		User user = User.findById(userId);
		Organization organisation = Organization.findById(organisationId);
		if (type == 0) {
			// if (Organizations.isDuplicateRequest(null, null, null,
			// organisationId, type, 0,
			// requestType, relationId)) {
			// System.out.println(organisation.renameEndRelationshipRequest.size());
			// redirect("MainEntitys.viewEntity", entityId, "Request created");
			// return;
			// }
			EntityRelationship relation = EntityRelationship
					.findById(relationId);
			RenameEndRelationshipRequest renameRequest = new RenameEndRelationshipRequest(
					user, organisation, relation, type, requestType, newName);
			renameRequest.save();
			Log.addUserLog(
					"User "
							+ user.firstName
							+ " "
							+ user.lastName
							+ " has requested to rename the following relationship between"
							+ " entities: (" + relation.source.name + " "
							+ relation.name + " " + relation.destination.name
							+ ") with " + newName, relation, renameRequest,
					organisation, user);

			System.out
					.println(organisation.renameEndRelationshipRequest.size());
			redirect("MainEntitys.viewEntity", entityId, "Request created");
		} else {
			// if (Organizations.isDuplicateRequest(null, null, null,
			// organisationId, type, 0,
			// requestType, relationId)) {
			// System.out.println(organisation.renameEndRelationshipRequest.size());
			// redirect("Topics.show", topicId, "Request created");
			// return;
			// }
			TopicRelationship relation = TopicRelationship.findById(relationId);
			RenameEndRelationshipRequest renameRequest = new RenameEndRelationshipRequest(
					user, organisation, relation, type, requestType, newName);
			renameRequest.save();
			Log.addUserLog(
					"User "
							+ user.firstName
							+ " "
							+ user.lastName
							+ " has requested to rename the following relationship between"
							+ " topics: (" + relation.source.title + " "
							+ relation.name + " " + relation.destination.title
							+ ") with " + newName, relation, renameRequest,
					organisation, user);
			System.out
					.println(organisation.renameEndRelationshipRequest.size());
			redirect("Topics.show", topicId, "Request created");
		}
	}

	/**
	 * creates a request for deleting an entity or topic relationship
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
	 * 
	 * @param userId
	 *            the id of the user making the request.
	 * 
	 * @param organisationId
	 *            the id of the organisation to which the entity or topic
	 *            belongs.
	 * 
	 * @param entityId
	 *            the id of the entity from which the request page was accessed.
	 * 
	 * @param topicId
	 *            the id of the topic from which the request page was accessed.
	 * 
	 * @param relationId
	 *            the id of the relationship to be deleted.
	 * 
	 * @param type
	 *            the type of the relationship request whether for an entity or
	 *            topic. (0=entity, 1=topic).
	 * 
	 * @param requestType
	 *            the type of the request whether rename or delete. (0=delete,
	 *            1=rename).
	 * 
	 */
	public static void deleteRequest(long userId, long organisationId,
			long entityId, long topicId, long relationId, int type,
			int requestType) {
		User user = User.findById(userId);
		Organization organisation = Organization.findById(organisationId);
		if (type == 0) {
			EntityRelationship relation = EntityRelationship
					.findById(relationId);
			RenameEndRelationshipRequest renameRequest = new RenameEndRelationshipRequest(
					user, organisation, relation, type, requestType, null);
			renameRequest.save();
			Log.addUserLog(
					"User "
							+ user.firstName
							+ " "
							+ user.lastName
							+ " has requested to delete the following relationship between"
							+ " entities: (" + relation.source.name + " "
							+ relation.name + " " + relation.destination.name
							+ ")", relation, renameRequest, organisation, user);
			System.out
					.println(organisation.renameEndRelationshipRequest.size());
			redirect("MainEntitys.viewEntity", entityId, "Request created");
		} else {
			TopicRelationship relation = TopicRelationship.findById(relationId);
			RenameEndRelationshipRequest renameRequest = new RenameEndRelationshipRequest(
					user, organisation, relation, type, requestType, null);
			renameRequest.save();
			Log.addUserLog(
					"User "
							+ user.firstName
							+ " "
							+ user.lastName
							+ " has requested to delete the following relationship between"
							+ " topics: (" + relation.source.title + " "
							+ relation.name + " " + relation.destination.title
							+ ")", relation, renameRequest, organisation, user);
			System.out
					.println(organisation.renameEndRelationshipRequest.size());
			redirect("Topics.show", topicId, "Request created");
		}
	}

}
