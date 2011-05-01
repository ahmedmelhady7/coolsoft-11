package controllers;

import java.util.List;

import play.data.validation.Required;

import models.BannedUser;
import models.MainEntity;
import models.Organization;
import models.Role;
import models.Topic;
import models.User;

public class BannedUsers extends CRUD {

	public void restrictOrganizer(long organizationID) {
		Organization org = Organization.findById(organizationID);
		List<User> users = Users.searchOrganizer(org);
		render(users, organizationID);
	}

	public void restrictOrganizerHelper1(@Required long userId,
			long organizationId) {

		if (validation.hasErrors()) {
			flash.error("Oops, please select one the Organizers");
			restrictOrganizer(organizationId);
		}

		Organization org = Organization.findById(organizationId);
		User user = User.findById(userId);
		List<MainEntity> entities = Users.getOrganizerEntities(org, user);
		render(user, organizationId, entities);
	}

	public void restrictOrganizerHelper2(@Required long entityId, String topic,
			long organizationId, long userId) {

		if (validation.hasErrors()) {
			flash.error("Oops, please select one the Entities ");
			restrictOrganizer(organizationId);
		}
		if (topic.equalsIgnoreCase("true")) {
			MainEntity e = MainEntity.findById(entityId);
			List<Topic> entityTopics = e.topicList;
			render(entityTopics, organizationId, userId);
		} else {
			restrictOrganizerHelper3(entityId, organizationId, userId);
		}

	}

	public void restrictOrganizerHelper3(long entityId, long organizationId,
			long userId) {
		List<String> entityActions = Roles.getRoleActions("organizer");
		render(entityActions, entityId, userId);
	}

	public void restrictOrganizerHelper4(long topicId, long userId) {
		List<String> topicActions = Roles.getOrganizerTopicActions();
		render(topicActions, topicId, userId);
	}

	public void restrictOrganizerHelper5(String action, String type,
			long entityTopicId, long userId) {

		boolean changed = true;
		if (type.equalsIgnoreCase("topic")) {

			Topic topic = Topic.findById(entityTopicId);
			MainEntity entity = topic.entity;
			Organization org = entity.organization;
			long organizationId = org.getId();
			changed = BannedUser.banFromActionInTopic(userId, organizationId,
					action, entityTopicId);
		}

		else {
			MainEntity entity = MainEntity.findById(entityTopicId);
			Organization org = entity.organization;
			long organizationId = org.getId();
			changed = BannedUser.banFromActionInTopic(userId, organizationId,
					action, entityTopicId);
		}

	}
}
