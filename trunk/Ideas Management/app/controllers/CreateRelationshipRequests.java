package controllers;

import models.CreateRelationshipRequest;
import models.MainEntity;
import models.Organization;
import models.Topic;
import models.User;
import play.mvc.With;

@With(Secure.class)
public class CreateRelationshipRequests extends CRUD {

	/**
	 * creates a request for creating a relationship between entities or topics.
	 * 
	 * @author Noha Khater
	 * 
	 * @Story C2S18
	 * 
	 * @param userId
	 *            the id of the user making the request.
	 * 
	 * @param source
	 *            the name of the source entity or topic.
	 * 
	 * @param destination
	 *            the name of the destination entity or topic.
	 * 
	 * @param name
	 *            the name of the relationship.
	 * 
	 * @param organisationId
	 *            the id of the organisation in which the request was made.
	 * 
	 * @param entityId
	 *            the id of the entity from which the request page was accessed.
	 * 
	 * @param topicId
	 *            the id of the topic from which the request page was accessed.
	 * 
	 * @param type
	 *            the type of the request whether it's for an entity or topic
	 *            (0=entity, 1=topic).
	 * 
	 */
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
			System.out.println(organisation.createRelationshipRequest.size());
			redirect("Topics.show", topicId, "Request created");

		}
	}

}
