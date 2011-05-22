package controllers;

import models.EntityRelationship;
import models.MainEntity;
import models.Organization;
import models.RenameEndRelationshipRequest;
import models.Topic;
import models.TopicRelationship;
import models.User;
import play.mvc.With;

@With(Secure.class)
public class RenameEndRelationshipRequests extends CRUD {
	
	public static void renameRequest(long userId, long organisationId,
			long entityId, long topicId,
			long relationId, int type, int requestType, String newName) {
		User user = User.findById(userId);
		Organization organisation = Organization.findById(organisationId);
		if (type == 0) {
//			if (Organizations.isDuplicateRequest(null, null, null, organisationId, type, 0,
//					requestType, relationId)) {
//				System.out.println(organisation.renameEndRelationshipRequest.size());
//				redirect("MainEntitys.viewEntity", entityId, "Request created");
//				return;
//			}
			EntityRelationship relation = EntityRelationship.findById(relationId);
			RenameEndRelationshipRequest renameRequest = new RenameEndRelationshipRequest(user, organisation,
					relation, type, requestType, newName);
			renameRequest.save();
			System.out.println(organisation.renameEndRelationshipRequest.size());
			redirect("MainEntitys.viewEntity", entityId, "Request created");
		} else {
//			if (Organizations.isDuplicateRequest(null, null, null, organisationId, type, 0,
//					requestType, relationId)) {
//				System.out.println(organisation.renameEndRelationshipRequest.size());
//				redirect("Topics.show", topicId, "Request created");
//				return;
//			}
			TopicRelationship relation = TopicRelationship.findById(relationId);
			RenameEndRelationshipRequest renameRequest = new RenameEndRelationshipRequest(
					user, organisation,relation, type, requestType,newName);
			renameRequest.save();
			System.out.println(organisation.renameEndRelationshipRequest.size());
			redirect("Topics.show", topicId, "Request created");
		}		
	}
	
	public static void deleteRequest(long userId, long organisationId,
			long entityId, long topicId,
			long relationId, int type, int requestType) {
		User user = User.findById(userId);
		Organization organisation = Organization.findById(organisationId);
		if (type == 0) {
			EntityRelationship relation = EntityRelationship.findById(relationId);
			RenameEndRelationshipRequest renameRequest = new RenameEndRelationshipRequest(user, organisation,
					relation, type, requestType, null);
			renameRequest.save();
			System.out.println(organisation.renameEndRelationshipRequest.size());
			redirect("MainEntitys.viewEntity", entityId, "Request created");
		} else {
			TopicRelationship relation = TopicRelationship.findById(relationId);
			RenameEndRelationshipRequest renameRequest = new RenameEndRelationshipRequest(
					user, organisation,relation, type, requestType,null);
			renameRequest.save();
			System.out.println(organisation.renameEndRelationshipRequest.size());
			redirect("Topics.show", topicId, "Request created");
		}
	}
	
	

}
