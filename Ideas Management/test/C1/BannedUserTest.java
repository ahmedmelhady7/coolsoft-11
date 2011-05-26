package C1;

import java.util.List;

import play.test.*;
import models.BannedUser;
import models.MainEntity;
import models.Organization;
import models.Role;
import models.Topic;
import models.User;
import models.UserRoleInOrganization;

import org.junit.*;

import controllers.Organizations;
import controllers.Roles;
import controllers.UserRoleInOrganizations;
import controllers.Users;

public class BannedUserTest extends UnitTest {
	@Test
	public void constructorAndDeleteTest() {
		List<User> bannedUsers = User.findAll();
		List<Organization> organizations = Organization.findAll();
		List<MainEntity> entities = MainEntity.findAll();
		if ((!bannedUsers.isEmpty()) && (!organizations.isEmpty())
				&& (!entities.isEmpty())) {
			User bannedUser = bannedUsers.get(0);
			Organization organization = organizations.get(0);
			long entityId = entities.get(0).getId();

			BannedUser newBannedUser = new BannedUser(bannedUser, organization,
					"testing action", "entity", entityId);

			newBannedUser.save();
			bannedUser.bannedUsers.add(newBannedUser);
			organization.bannedUsers.add(newBannedUser);

			BannedUser bannedUserTest = BannedUser
					.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceID = ? and bu.resourceType = ? ",
							bannedUser, organization, "testing action",
							entityId, "entity").first();
			assertNotNull(bannedUserTest);
			BannedUser.delete(bannedUserTest);
			BannedUser deletedBannedUserTest = BannedUser
					.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceID = ? and bu.resourceType = ? ",
							bannedUser, organization, "testing action",
							entityId, "entity").first();
			assertNull(deletedBannedUserTest);
		}

	}

	@Test
	public void constructorAndTopicOrEntityDeleteTest() {
		List<User> bannedUsers = User.findAll();
		List<Organization> organizations = Organization.findAll();
		List<Topic> topics = Topic.findAll();
		if ((!bannedUsers.isEmpty()) && (!organizations.isEmpty())
				&& (topics.size() > 1)) {

			User bannedUser = bannedUsers.get(0);
			Organization organization = organizations.get(0);
			long topicIdOne = topics.get(0).getId();
			long topicIdTwo = topics.get(1).getId();

			BannedUser bannedUserOne = new BannedUser(bannedUser, organization,
					"first testing action", "topic", topicIdOne);
			bannedUserOne.save();
			bannedUser.bannedUsers.add(bannedUserOne);
			organization.bannedUsers.add(bannedUserOne);

			BannedUser bannedUserTwo = new BannedUser(bannedUser, organization,
					"second testing action", "topic", topicIdTwo);
			bannedUserTwo.save();
			bannedUser.bannedUsers.add(bannedUserTwo);
			organization.bannedUsers.add(bannedUserTwo);

			BannedUser bannedUserThree = new BannedUser(bannedUser,
					organization, "third testing action", "topic", topicIdOne);
			bannedUserThree.save();
			bannedUser.bannedUsers.add(bannedUserThree);
			organization.bannedUsers.add(bannedUserThree);

			BannedUser bannedUserTestOne = BannedUser
					.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceID = ? and bu.resourceType = ? ",
							bannedUser, organization, "first testing action",
							topicIdOne, "topic").first();
			BannedUser bannedUserTestTwo = BannedUser
					.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceID = ? and bu.resourceType = ? ",
							bannedUser, organization, "second testing action",
							topicIdTwo, "topic").first();
			BannedUser bannedUserTestThree = BannedUser
					.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceID = ? and bu.resourceType = ? ",
							bannedUser, organization, "third testing action",
							topicIdOne, "topic").first();

			assertNotNull(bannedUserTestOne);
			assertNotNull(bannedUserTestTwo);
			assertNotNull(bannedUserTestThree);

			BannedUser.deleteEntityOrTopic(topicIdOne, "topic");
			BannedUser deletedBannedUserTestOne = BannedUser
					.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceID = ? and bu.resourceType = ? ",
							bannedUser, organization, "first testing action",
							topicIdOne, "topic").first();
			BannedUser deletedBannedUserTestTwo = BannedUser
					.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceID = ? and bu.resourceType = ? ",
							bannedUser, organization, "second testing action",
							topicIdTwo, "topic").first();
			BannedUser deletedBannedUserTestThree = BannedUser
					.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceID = ? and bu.resourceType = ? ",
							bannedUser, organization, "third testing action",
							topicIdOne, "topic").first();

			assertNull(deletedBannedUserTestOne);
			assertNull(deletedBannedUserTestThree);
			assertNotNull(deletedBannedUserTestTwo);
		}

	}

	@Test
	public void banFromActionInEntityTest() {
		List<User> users = User.findAll();
		List<MainEntity> entities = MainEntity.findAll();
		MainEntity entity = null;
		boolean topicsFlag = false;
		boolean subEntityFlag = false;
		if ((!users.isEmpty()) && (!entities.isEmpty())) {
			for (int i = 0; i < entities.size(); i++) {
				if ((!entities.get(i).subentities.isEmpty())
						&& (!entities.get(i).tagList.isEmpty())) {
					entity = entities.get(i);
					topicsFlag = true;
					subEntityFlag = true;
					break;
				}

			}

			if (!subEntityFlag && !topicsFlag) {

				for (int i = 0; i < entities.size(); i++) {

					if (!entities.get(i).subentities.isEmpty()) {
						entity = entities.get(i);
						subEntityFlag = true;
						break;
					}

				}
			}

			if (!subEntityFlag && !topicsFlag) {
				for (int i = 0; i < entities.size(); i++) {

					if (!entities.get(i).tagList.isEmpty()) {
						entity = entities.get(i);
						topicsFlag = true;
						break;
					}
				}

			}
			if (!subEntityFlag && !topicsFlag) {
				entity = entities.get(0);
			}

			User user = users.get(0);
			Organization organization = entity.organization;
			String action = "block a user from viewing or using a certain entity";
			String actionTwo = "merge ideas";
			BannedUser.banFromActionInEntity(user.id, organization.id, action,
					entity.id);
			BannedUser.banFromActionInEntity(user.id, organization.id,
					actionTwo, entity.id);
			BannedUser bannedUser = BannedUser
					.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceType = ? and bu.resourceID = ?",
							user, organization, action, "entity", entity.id)
					.first();
			assertNotNull(bannedUser);

			if (subEntityFlag && topicsFlag) {
				MainEntity subEntity = entity.subentities.get(0);
				Topic topic = entity.topicList.get(0);
				BannedUser bannedUserOne = BannedUser
						.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceType = ? and bu.resourceID = ?",
								user, organization, action, "entity",
								subEntity.id).first();
				BannedUser bannedUserTwo = BannedUser
						.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceType = ? and bu.resourceID = ?",
								user, organization, action, "topic", topic.id)
						.first();
				BannedUser bannedUserThree = BannedUser
						.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceType = ? and bu.resourceID = ?",
								user, organization, actionTwo, "topic",
								topic.id).first();

				assertNotNull(bannedUserOne);
				assertNull(bannedUserTwo);
				assertNotNull(bannedUserThree);

			}

			if (subEntityFlag && !topicsFlag) {
				MainEntity subEntity = entity.subentities.get(0);
				BannedUser bannedUserOne = BannedUser
						.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceType = ? and bu.resourceID = ?",
								user, organization, action, "entity",
								subEntity.id).first();
				assertNotNull(bannedUserOne);
			}

			if (!subEntityFlag && topicsFlag) {
				Topic topic = entity.topicList.get(0);
				BannedUser bannedUserTwo = BannedUser
						.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceType = ? and bu.resourceID = ?",
								user, organization, action, "topic", topic.id)
						.first();
				BannedUser bannedUserThree = BannedUser
						.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceType = ? and bu.resourceID = ?",
								user, organization, actionTwo, "topic",
								topic.id).first();

				assertNull(bannedUserTwo);
				assertNotNull(bannedUserThree);

			}

		}

	}

	@Test
	public void banFromActionInTopicTest() {
		List<BannedUser> bannedUsers = BannedUser.findAll();
		for (int i = 0 ; i < bannedUsers.size() ; i++){
			BannedUser.delete(bannedUsers.get(i));
		}
		
		List<Topic> topics = Topic.findAll();
		List<User> users = User.findAll();

		if ((!topics.isEmpty()) && (!users.isEmpty())) {
			Topic topic = topics.get(0);
			Organization organization = topic.entity.organization;
			User user = users.get(0);
			String action = "tag topics";
			BannedUser.banFromActionInTopic(user.id, organization.id, action,
					topic.id);
			BannedUser bannedUserTest = BannedUser
					.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceType = ? and bu.resourceID = ?",
							user, topic.entity.organization, action, "topic",
							topic.id).first();
			/**
			 * testing that the record is saved
			 */
			assertNotNull(bannedUserTest);
			
			
			BannedUser.banFromActionInTopic(user.id, organization.id, action,
					topic.id);
			List<BannedUser> bannedUserCountTest = BannedUser
					.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceType = ? and bu.resourceID = ?",
							user, topic.entity.organization, action, "topic",
							topic.id).fetch();
			/**
			 * testing that the same record is not inserted twice
			 */
           assertEquals(1,bannedUserCountTest.size());
		}

	}
	
	@Test
	public void derestrictFromTopicActionNoEntityCascade(){
		BannedUser.deleteAll();
		List<Topic> topics = Topic.findAll();
		List<User>  users = User.findAll();
		String action = "tag topics";
		if((!topics.isEmpty()) && (!users.isEmpty())){
			User user = users.get(0);
			Topic topic = topics.get(0);
			/**
			 * nothing will change in the DB 
			 */
			BannedUser.banFromActionInTopic(user.id, topic.entity.organization.id, action,
					topic.id);
			
			BannedUser.deRestrictFromTopic(user.id,action,topic.id);
			BannedUser bannedUserTest = BannedUser
			.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceType = ? and bu.resourceID = ?",
					user, topic.entity.organization, action, "topic",
					topic.id).first();
			/**
			 * the entry will be removed from the DB
			 */
			assertNull(bannedUserTest);
			
		}
	}
	
	
	@Test
	public void derestrictFromTopicActionWithEntityCascade(){
		BannedUser.deleteAll();
		List<Topic> topics = Topic.findAll();
		List<User>  users = User.findAll();
		String action = "tag topics";
		if((!topics.isEmpty()) && (!users.isEmpty())){
			User user = users.get(0);
			Topic topic = topics.get(0);
			MainEntity entity = topic.entity;
			/**
			 * cascade a restriction from  the MainEntity entity to the Topic topic
			 */
			BannedUser.banFromActionInEntity(user.id, entity.organization.id, action, entity.id);
			/**
			 * cascade the de-restriction from the topic to entity
			 */
			BannedUser.deRestrictFromTopic(user.id,action,topic.id);
			BannedUser bannedUserTestOne = BannedUser
			.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceType = ? and bu.resourceID = ?",
					user, topic.entity.organization, action, "topic",
					topic.id).first();
			BannedUser bannedUserTestTwo = BannedUser
			.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceType = ? and bu.resourceID = ?",
					user, topic.entity.organization, action, "entity",
					entity.id).first();
			/**
			 * the entry will be removed from the DB from the topic as well as the Entity of that topic
			 */
			assertNull(bannedUserTestOne);
			assertNull(bannedUserTestTwo);
			
		}
	}
}
