package C1;

import java.util.List;

import play.test.*;
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

public class UserRoleInOrganizationTest extends UnitTest {

	@Test
	public void deleteTest() {
		List<UserRoleInOrganization> uros = UserRoleInOrganization.findAll();
		UserRoleInOrganization uro = null;
		if (!uros.isEmpty()) {
			uro = uros.get(0);
			assertEquals(true, UserRoleInOrganization.delete(uro));
			List<UserRoleInOrganization> test = UserRoleInOrganization
					.findAll();

			assertEquals(false, test.contains(uro));

		}

	}

	@Test
	public void cascadeEntityDelete() {

		List<MainEntity> entities = MainEntity.findAll();
		if (entities != null && (!entities.isEmpty())) {
			MainEntity entity = entities.get(0);
			long entityId = entity.getId();

			String type = "entity";
			UserRoleInOrganization.deleteEntityOrTopic(entityId, type);
			List<UserRoleInOrganization> entityTest = UserRoleInOrganization
					.find("select uro from UserRoleInOrganization uro where uro.entityTopicID = ? and uro.type = ?",
							entityId, type).fetch();
			assertTrue(entityTest.isEmpty());
		}

	}

	@Test
	public void cascadeTopicDelete() {

		List<Topic> topics = Topic.findAll();
		if (topics != null && (!topics.isEmpty())) {
			Topic topic = topics.get(0);
			long entityId = topic.getId();
			String type = "topic";
			List<User> users = User.findAll();
			User user = users.get(0);
			List<Organization> organizations = Organization.findAll();
			Organization organization = organizations.get(0);
			Role role = Roles.getRoleByName("organizer");
			UserRoleInOrganizations.addEnrolledUser(user, organization, role,
					entityId, "topic");
			UserRoleInOrganization.deleteEntityOrTopic(entityId, type);
			List<UserRoleInOrganization> topicTest = UserRoleInOrganization
					.find("select uro from UserRoleInOrganization uro where uro.entityTopicID = ? and uro.type = ?",
							entityId, type).fetch();
			assertTrue(topicTest.isEmpty());
		}

	}

	@Test
	public void cascadeOrganizationDelete() {
		List<User> users = User.findAll();
		User user = users.get(0);
		List<Organization> organizations = Organization.findAll();
		Organization organization = organizations.get(0);
		Organization organizationTwo = organizations.get(1);
		List<MainEntity> entities = MainEntity.findAll();
		MainEntity entity1 = entities.get(0);
		long entityId = entity1.getId();
		Role role = Roles.getRoleByName("idea developer");
		Role roleTwo = Roles.getRoleByName("organizer");
		UserRoleInOrganization.deleteAll();
		UserRoleInOrganizations.addEnrolledUser(user, organization, role);
		UserRoleInOrganizations.addEnrolledUser(user, organizationTwo, roleTwo,
				entityId, "entity");
		
		UserRoleInOrganization userRoleInOrganization = UserRoleInOrganization
		.find("select uro from UserRoleInOrganization uro where uro.enrolled = ? and uro.role = ? and uro.organization = ? ",
				user, role, organization).first();
		
		UserRoleInOrganization userRoleInOrganizationTwo = UserRoleInOrganization
		.find("select uro from UserRoleInOrganization uro where uro.enrolled = ? and uro.role = ? and uro.organization = ? and uro.entityTopicID = ? and uro.type = ?",
				user, roleTwo, organizationTwo , entityId , "entity").first();
		
		assertNotNull(userRoleInOrganization);
		assertNotNull(userRoleInOrganizationTwo);
		assertEquals(true, UserRoleInOrganization.delete(userRoleInOrganization));
		UserRoleInOrganization test1 = UserRoleInOrganization
		.find("select uro from UserRoleInOrganization uro where uro.enrolled = ? and uro.role = ? and uro.organization = ? ",
				user, role, organization).first();
		UserRoleInOrganization test2 = UserRoleInOrganization
		.find("select uro from UserRoleInOrganization uro where uro.enrolled = ? and uro.role = ? and uro.organization = ? and uro.entityTopicID = ? and uro.type = ?",
				user, roleTwo, organizationTwo , entityId , "entity").first();
		assertNull(test1);
		assertNotNull(test2);

	}

	@Test
	public void addEnrolledUserTest() {
		List<User> users = User.findAll();
		User user = users.get(0);
		List<Organization> organizations = Organization.findAll();
		Organization organization = organizations.get(0);
		Role role = Roles.getRoleByName("idea developer");
		UserRoleInOrganizations.addEnrolledUser(user, organization, role);
		UserRoleInOrganization userRoleInOrganization = UserRoleInOrganization
				.find("select uro from UserRoleInOrganization uro where uro.enrolled = ? and uro.role = ? and uro.organization = ? ",
						user, role, organization).first();
		assertNotNull(userRoleInOrganization);
	}

}
