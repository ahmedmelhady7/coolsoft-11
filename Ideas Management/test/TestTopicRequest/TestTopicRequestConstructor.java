package TestTopicRequest;

import models.MainEntity;
import models.Organization;
import models.Role;
import models.Topic;
import models.TopicRequest;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class TestTopicRequestConstructor extends UnitTest {
	@Before
	public void setup() {
		Fixtures.deleteAll();
	}
	
	@Test
	public void topicRequestConstructor(){
		// Create a new topicRequest and all its necessary attributes

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

		TopicRequest matchChaos = new TopicRequest(admin, elAhly, "Chaos in egyptian football matches",
				"try to avoid chaos and fights during matches due to different points of view", 2).save();
		
		TopicRequest govImp = new TopicRequest(sharaf, govHead, "التعديلات الدستورية",
				"اقتراحات", 1, "Because").save();
		
		//Tests
		
		assertNotNull(matchChaos);
		assertNotNull(govImp);
		assertEquals("Chaos in egyptian football matches", matchChaos.title);
		assertEquals("التعديلات الدستورية", govImp.title);
		assertEquals("try to avoid chaos and fights during matches due to different points of view", matchChaos.description);
		assertEquals("اقتراحات", govImp.description);
		assertEquals(2, matchChaos.privacyLevel);
		assertEquals(1, govImp.privacyLevel);
		assertEquals(admin, matchChaos.requester);
		assertEquals(sharaf, govImp.requester);
		assertEquals("", matchChaos.message);
		assertEquals("Because", govImp.message);
	}


}
