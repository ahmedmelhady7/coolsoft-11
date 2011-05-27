package TestTopic;

import models.Idea;
import models.MainEntity;
import models.Organization;
import models.Role;
import models.Topic;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;
import controllers.Roles;
import controllers.UserRoleInOrganizations;

public class TestTopic  extends UnitTest {
	
	@Before
	public void setup() {
		Fixtures.deleteAll();
	}
	
	@Test
	public void topicConstructor(){
		// Create a new topic and all its necessary attributes

		Role.createIdeaDeveloperRole();
		Role.createOrganizerRole();
		Role.createAdminRole();
		Role.createOrganizationLeadRole();

		User admin = new User("admin@coolsoft.com", "admin", "1234", "Mr.",
				"admin", "What is our company's name?", "coolsoft", 0,
				"11/11/1990", "Egypt", "Prgrammer");
		admin.isAdmin = true;
		admin._save();

		User sharaf = new User("sharaf@eg.gov", "sharaf", "1234", "Asam",
				"Sharaf", "What is our company's name?", "coolsoft", 0,
				"2/14/1955", "Egypt", "Primenister").save();

		Organization egypt = new Organization("Egypt", admin, 2, false,
				"The National Egyptian Organization").save();

		Organization gov = new Organization("الحكومة المصرية", sharaf, 0, true,
				"").save();

		MainEntity elAhly = new MainEntity("El-Ahly", "Al-Ahly Club Lovers",
				egypt, true).save();

		MainEntity govHead = new MainEntity("الوزارة", "راسه مجلس الوزراء",
				gov, true).save();

		Topic matchChaos = new Topic(
				"Chaos in egyptian football matches",
				"try to avoid chaos and fights during matches due to different points of view",
				2, admin, elAhly, true).save();
		elAhly.topicList.add(matchChaos);
		elAhly._save();

		Topic govImp = new Topic("التعديلات الدستورية", "اقتراحات", 1, sharaf,
				govHead, false).save();
		govHead.topicList.add(govImp);
		govHead._save();
		
		//Tests
		
		assertNotNull(matchChaos);
		assertNotNull(govImp);
		assertEquals("Chaos in egyptian football matches", matchChaos.title);
		assertEquals("التعديلات الدستورية", govImp.title);
		assertEquals("try to avoid chaos and fights during matches due to different points of view", matchChaos.description);
		assertEquals("اقتراحات", govImp.description);
		assertEquals(2, matchChaos.privacyLevel);
		assertEquals(1, govImp.privacyLevel);
		assertEquals(admin, matchChaos.creator);
		assertEquals(sharaf, govImp.creator);
		assertEquals(true, matchChaos.createRelationship);
		assertEquals(false, govImp.createRelationship);
	}

	@Test
	public void canViewTopic() {
		// Create a new topic and all its necessary attributes

		Role.createIdeaDeveloperRole();
		Role.createOrganizerRole();
		Role.createAdminRole();
		Role.createOrganizationLeadRole();

		User admin = new User("admin@coolsoft.com", "admin", "1234", "Mr.",
				"admin", "What is our company's name?", "coolsoft", 0,
				"11/11/1990", "Egypt", "Prgrammer");
		admin.isAdmin = true;
		admin._save();

		User mai = new User("mai.jt4@gmail.com", "mai", "1234", "Mai", "Magdy",
				"What is our company's name?", "coolsoft", 0, "9/14/1990", "Egypt", "student");
		mai._save();

		User sharaf = new User("sharaf@eg.gov", "sharaf", "1234", "Asam",
				"Sharaf", "What is our company's name?", "coolsoft", 0,
				"2/14/1955", "Egypt", "Primenister").save();

		Organization egypt = new Organization("Egypt", admin, 2, false,
				"The National Egyptian Organization").save();

		Organization gov = new Organization("الحكومة المصرية", sharaf, 0, true,
				"").save();

		MainEntity elAhly = new MainEntity("El-Ahly", "Al-Ahly Club Lovers",
				egypt, true).save();

		MainEntity govHead = new MainEntity("الوزارة", "راسه مجلس الوزراء",
				gov, true).save();

		Role ideadeveloper = Roles.getRoleByName("idea developer");
		Role OrganizationLead = Roles.getRoleByName("organizationLead");

		Topic matchChaos = new Topic(
				"Chaos in egyptian football matches",
				"try to avoid chaos and fights during matches due to different points of view",
				2, admin, elAhly, true).save();
		elAhly.topicList.add(matchChaos);
		elAhly._save();

		Topic govImp = new Topic("التعديلات الدستورية", "اقتراحات", 1, sharaf,
				govHead, false).save();
		govHead.topicList.add(govImp);
		govHead._save();

		
		assertNotNull(matchChaos);
		assertEquals("Chaos in egyptian football matches", matchChaos.title);
		assertEquals(1, elAhly.topicList.size());
		
		assertEquals(true, matchChaos.canView(admin));

		assertEquals(false, govImp.canView(mai));

		assertEquals(true, govImp.canView(sharaf));
		UserRoleInOrganizations.addEnrolledUser(sharaf, gov, OrganizationLead);
		assertEquals(true, govImp.canView(sharaf));

		UserRoleInOrganizations.addEnrolledUser(mai, gov, ideadeveloper,
				govImp.id, "topic");

		assertEquals(true, matchChaos.canView(mai));

		assertEquals(true, govImp.isDeletable());

		Idea i7 = new Idea("Blank", "Blank", sharaf, govImp);
		i7.save();
		sharaf.save();
		govImp.ideas.add(i7);
		govImp.save();

		assertEquals(false, govImp.isDeletable());

	}
	
	@Test
	public void isDeletableTopic() {
		// Create a new topic and all its necessary attributes

		Role.createOrganizationLeadRole();

		User sharaf = new User("sharaf@eg.gov", "sharaf", "1234", "Asam",
				"Sharaf", "What is our company's name?", "coolsoft", 0,
				"2/14/1995", "Egypt", "Primenister").save();

		Organization gov = new Organization("الحكومة المصرية", sharaf, 0, true,
				"").save();

		MainEntity govHead = new MainEntity("الوزارة", "راسه مجلس الوزراء",
				gov, true).save();

		Role OrganizationLead = Roles.getRoleByName("organizationLead");
		
		UserRoleInOrganizations.addEnrolledUser(sharaf, gov, OrganizationLead);

		Topic govImp = new Topic("التعديلات الدستورية", "اقتراحات", 1, sharaf,
				govHead, false).save();
		govHead.topicList.add(govImp);
		govHead._save();

		assertEquals(true, govImp.isDeletable());

		Idea i7 = new Idea("Blank", "Blank", sharaf, govImp);
		i7.save();
		sharaf.save();
		govImp.ideas.add(i7);
		govImp.save();

		assertEquals(false, govImp.isDeletable());

	}

}
