package controllers;

import java.util.List;

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
			if (Organizations.isDuplicateRequest(null, null, newName,
					organisationId, type, 0, requestType, relationId)) {
				redirect("MainEntitys.viewEntity", entityId, "Request created");
			}
			EntityRelationship relation = EntityRelationship
					.findById(relationId);
			RenameEndRelationshipRequest renameRequest = new RenameEndRelationshipRequest(
					user, organisation, relation, type, requestType, newName);
			renameRequest.save();
			Log.addUserLog(
					"<a href=\"/Users/viewProfile?userId="
							+ user.id
							+ "\">"
							+ user.username
							+ "</a>"
							+ " has requested to rename the following relationship between"
							+ "<a href=\"/MainEntitys/viewEntity?mainentityId="
							+ relation.source.id + "\">" + relation.source.name + "</a>" +" " 
							+ relation.name
							+ " " + "<a href=\"/MainEntitys/viewEntity?mainentityId="
							+ relation.destination.id + "\">" + relation.destination.name + "</a>" 
							+ " with " + newName, organisation);
			redirect("MainEntitys.viewEntity", entityId, "Request created");
		} else {
			if (Organizations.isDuplicateRequest(null, null, newName,
					organisationId, type, 0, requestType, relationId)) {
				redirect("Topics.show", topicId, "Request created");
			}
			TopicRelationship relation = TopicRelationship.findById(relationId);
			RenameEndRelationshipRequest renameRequest = new RenameEndRelationshipRequest(
					user, organisation, relation, type, requestType, newName);
			renameRequest.save();
			Log.addUserLog(
					"<a href=\"/Users/viewProfile?userId="
							+ user.id
							+ "\">"
							+ user.username
							+ "</a>"
							+ " has requested to rename the following relationship between topics: "
							+ "<a href=\"/Topics/show?topicId="
							+ relation.source.id + "\">" + relation.source.title + "</a>" +" " 
							+ relation.name
							+ " " + "<a href=\"/Topics/show?topicId="
							+ relation.destination.id + "\">" + relation.destination.title + "</a>" 
							+ " with " + newName, organisation);
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
			if (Organizations.isDuplicateRequest(null, null, null,
					organisationId, type, 0, requestType, relationId)) {
				redirect("MainEntitys.viewEntity", entityId, "Request created");
			}
			EntityRelationship relation = EntityRelationship
					.findById(relationId);
			RenameEndRelationshipRequest renameRequest = new RenameEndRelationshipRequest(
					user, organisation, relation, type, requestType, null);
			renameRequest.save();
			Log.addUserLog(
					"<a href=\"/Users/viewProfile?userId="
							+ user.id
							+ "\">"
							+ user.username
							+ "</a>"
							+ " has requested to delete the following relationship between"
							+ "<a href=\"/MainEntitys/viewEntity?mainentityId="
							+ relation.source.id + "\">" + relation.source.name + "</a>" +" " 
							+ relation.name
							+ " " + "<a href=\"/MainEntitys/viewEntity?mainentityId="
							+ relation.destination.id + "\">" + relation.destination.name + "</a>" 
							, organisation);		
			redirect("MainEntitys.viewEntity", entityId, "Request created");
		} else {
			if (Organizations.isDuplicateRequest(null, null, null,
					organisationId, type, 0, requestType, relationId)) {
				redirect("Topics.show", topicId, "Request created");
			}
			TopicRelationship relation = TopicRelationship.findById(relationId);
			RenameEndRelationshipRequest renameRequest = new RenameEndRelationshipRequest(
					user, organisation, relation, type, requestType, null);
			renameRequest.save();
			Log.addUserLog(
					"<a href=\"/Users/viewProfile?userId="
							+ user.id
							+ "\">"
							+ user.username
							+ "</a>"
							+ " has requested to delete the following relationship between topics: "
							+ "<a href=\"/Topics/show?topicId="
							+ relation.source.id + "\">" + relation.source.title + "</a>" 
							+" " + relation.name
							+ " " + "<a href=\"/Topics/show?topicId="
							+ relation.destination.id + "\">" + relation.destination.title + "</a>" 
							, organisation);
			redirect("Topics.show", topicId, "Request created");
		}
	}

	public static boolean delete(Long id) {
		RenameEndRelationshipRequest request = RenameEndRelationshipRequest
				.findById(id);
		List<User> users = User.findAll();
		List<Organization> organizations = Organization.findAll();
		List<EntityRelationship> entityRelations = EntityRelationship.findAll();
		List<TopicRelationship> topicRelations = TopicRelationship.findAll();
		int size = users.size();
		for (int i = 0; i < size; i++) {
			if(users.get(i).myRenameEndRelationshipRequests.contains(request)) {
				users.get(i).myRenameEndRelationshipRequests.remove(request);
				users.get(i).save();
			}
		}
		size = organizations.size();
		for (int i = 0; i < size; i++) {
			if (organizations.get(i).renameEndRelationshipRequest.contains(request)) {
				organizations.get(i).renameEndRelationshipRequest.remove(request);
				organizations.get(i).save();
			}
		}
		size = entityRelations.size();
		for (int i = 0; i < size; i++) {
			if(entityRelations.get(i).renameEndRequests.contains(request)) {
				entityRelations.get(i).renameEndRequests.remove(request);
				entityRelations.get(i).save();
			}
		}
		size = topicRelations.size();
		for (int i = 0; i < size; i++) {
			if(topicRelations.get(i).renameEndRequests.contains(request)) {
				topicRelations.get(i).renameEndRequests.remove(request);
				topicRelations.get(i).save();
			}
		}
		request.delete();
		return true;
	}

}
