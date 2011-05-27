package testPlanConstructor;

import java.util.Date;

import models.MainEntity;
import models.Notification;
import models.Organization;
import models.Plan;
import models.Role;
import models.Topic;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class TestPlanConstructor extends UnitTest{
	@Before
	public void setup() {
		Fixtures.deleteAll();
	}
	@Test(timeout = 1000)
	public void constructorTest() {
		Role.createIdeaDeveloperRole();
		Role.createOrganizerRole();
		Role.createAdminRole();
		Role.createOrganizationLeadRole();

		User ashraf = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234",
				"Ashraf", "Mansoor", "What is our company's name?", "coolsoft",
				0, "11/11/2010", "Egypt", "student").save();


		Organization guc = new Organization("GUC", ashraf, 1, true,
				"The German University in Cairo").save();

		MainEntity gucMet = new MainEntity("MET",
				"Media Engineering and technology", guc, true).save();
		gucMet._save();
		Topic gucMetStudentUnion = new Topic("Student union", "Suggestions", 2,
				ashraf, gucMet, true).save();
		gucMetStudentUnion._save();
		Plan p1 = new Plan("S.U. heads", ashraf, new Date(111, 03, 20),
				new Date(111, 07, 30), "Plan for SU heads elections",
				gucMetStudentUnion, "summer break").save();
		assertNotNull(p1);
		assertEquals("S.U. heads",p1.title);
		assertEquals(ashraf,p1.madeBy);
		assertEquals(new Date(111, 03, 20),p1.startDate);
		assertEquals(new Date(111, 07, 30),p1.endDate);
		assertEquals("Plan for SU heads elections",p1.description);
		assertEquals(gucMetStudentUnion,p1.topic);
		assertEquals("summer break",p1.requirement);
		
	}
}
