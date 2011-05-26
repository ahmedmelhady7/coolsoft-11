import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.test.*;
import models.BannedUser;
import models.Organization;
import models.RequestToJoin;
import models.Role;
import models.Tag;
import models.User;

import org.junit.*;

import controllers.Roles;
import controllers.UserRoleInOrganizations;

public class OmarFaruki extends UnitTest {

	@Before
	public void setUp() {
		Fixtures.deleteAll();
	}

	/**
	 * This method is used to test the getOrganizations method in User model
	 */
	@Test
	public void getAllOrganizations() {
		User ashraf = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234",
				"Ashraf", "Mansoor", "What is our company's name?", "coolsoft",
				0, "2/14/1995", "Egypt", "student").save();
		User faruki = new User("Faruki@guc.edu.eg", "Faruki", "1234", "Omar",
				"Faruki", "Favourite course?", "Faruki", 0, "2/14/1995",
				"Egypt", "professor").save();
		Role.createIdeaDeveloperRole();
		Role.createOrganizationLeadRole();
		Organization guc = new Organization("GUC", ashraf, 1, true,
				"The German University in Cairo").save();

		Organization auc = new Organization("AUC", faruki, 1, true,
				"The American University in Cairo").save();

		Role role1 = Roles.getRoleByName("organizationLead");
		UserRoleInOrganizations.addEnrolledUser(ashraf, auc, role1);

		Role role = Roles.getRoleByName("idea developer");
		UserRoleInOrganizations.addEnrolledUser(ashraf, guc, role);

		UserRoleInOrganizations.addEnrolledUser(faruki, auc, role);
		List<Organization> organizations = new ArrayList<Organization>();

		organizations.add(auc);
		organizations.add(guc);
		assertEquals(organizations, ashraf.getOrganizations());

		List<Organization> organizations1 = new ArrayList<Organization>();
		organizations1.add(auc);
		assertEquals(organizations1, faruki.getOrganizations());
	}

	/**
	 * This method is used to test the organisation model constructor
	 */
	@Test
	public void createOrganization() {
		User faruki = new User("Faruki@guc.edu.eg", "Faruki", "1234", "Omar",
				"Faruki", "Favourite course?", "Faruki", 0, "2/14/1995",
				"Egypt", "professor").save();
		Organization auc = new Organization("AUC", faruki, 1, true,
				"The American University in Cairo").save();
		assertEquals("AUC", auc.name);
		assertEquals(faruki, auc.creator);
		assertEquals(1, auc.privacyLevel);
		assertEquals(true, auc.createTag);
		assertEquals("The American University in Cairo", auc.description);
		assertEquals(true, auc.followers.isEmpty());
		assertEquals(true, auc.bannedUsers.isEmpty());
		assertEquals(true, auc.createRelationshipRequest.isEmpty());
		assertEquals(true, auc.entitiesList.isEmpty());
		assertEquals(true, auc.followers.isEmpty());
		assertEquals(true, auc.invitation.isEmpty());
		assertEquals(true, auc.joinRequests.isEmpty());
		assertEquals(true, auc.relatedTags.isEmpty());
		assertEquals(true, (auc.relationNames.contains("has many")
				&& auc.relationNames.contains("manages") && auc.relationNames
				.contains("belongs to")));
		assertEquals(true, auc.renameEndRelationshipRequest.isEmpty());
		assertEquals(true, auc.userRoleInOrg.isEmpty());
	}

	/**
	 * This method is used to test the tag model constructor
	 */
	@Test
	public void createTag() {
		User faruki = new User("Faruki@guc.edu.eg", "Faruki", "1234", "Omar",
				"Faruki", "Favourite course?", "Faruki", 0, "2/14/1995",
				"Egypt", "professor").save();
		Organization auc = new Organization("AUC", faruki, 1, true,
				"The American University in Cairo").save();
		Tag tag = new Tag("TAG", auc, faruki);
		assertEquals(auc, tag.createdInOrganization);
		assertEquals(faruki, tag.creator);
		assertEquals(true, tag.entities.isEmpty());
		assertEquals(true, tag.followers.isEmpty());
		assertEquals("TAG", tag.name);
		assertEquals(true, tag.organizations.isEmpty());
		assertEquals(true, tag.relationsDestination.isEmpty());
		assertEquals(true, tag.relationsSource.isEmpty());
		assertEquals(true, tag.taggedIdeas.isEmpty());
		assertEquals(true, tag.taggedItems.isEmpty());
		assertEquals(true, tag.taggedTopics.isEmpty());
	}

	/**
	 * This method is used to test the RequestToJoin Model constructor
	 * responsible for requesting to join a certain organisation
	 */
	@Test
	public void requestToJoinOrg() {
		User faruki = new User("Faruki@guc.edu.eg", "Faruki", "1234", "Omar",
				"Faruki", "Favourite course?", "Faruki", 0, "2/14/1995",
				"Egypt", "professor").save();
		Organization auc = new Organization("AUC", faruki, 1, true,
				"The American University in Cairo").save();
		RequestToJoin request = new RequestToJoin(faruki, null, auc,
				"I want to join");
		assertEquals("I want to join", request.description);
		assertEquals(auc, request.organization);
		assertEquals(faruki, request.source);
		assertEquals(null, request.topic);
	}
}
