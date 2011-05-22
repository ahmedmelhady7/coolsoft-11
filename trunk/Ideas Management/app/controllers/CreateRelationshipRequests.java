package controllers;

import models.CreateRelationshipRequest;
import models.MainEntity;
import models.Organization;
import models.Topic;
import models.User;
import play.mvc.With;

@With(Secure.class)
public class CreateRelationshipRequests extends CRUD {

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
			System.out.println(organisation.createRelationshipRequest.size());
			redirect("Topics.show", topicId, "Request created");

		}
	}

}
