package testItemConstructor;

import java.util.Date;

import models.Item;
import models.MainEntity;
import models.Notification;
import models.Organization;
import models.Plan;
import models.Topic;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class TestItemConstructor extends UnitTest{

	@Before
	public void setup() {
		Fixtures.deleteAll();
	}
	@Test(timeout = 1000)
	public void constructorTest() {
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
		Item item1 = new Item(
				new Date(111, 05, 01),
				new Date(111, 04, 20),
				"One of the members who is not runing for elections should organize a debate between the candidates",
				p1, "Candidates Debate");
		item1.save();
		assertNotNull(item1);
		assertEquals(new Date(111, 05, 01),item1.startDate);
		assertEquals(new Date(111, 04, 20),item1.endDate);
		assertEquals("One of the members who is not runing for elections should organize a debate between the candidates",item1.description);
		assertEquals(p1,item1.plan);
		assertEquals("Candidates Debate",item1.summary);
	}
}
