package testPlan;

import java.util.Date;

import models.Item;
import models.MainEntity;
import models.Organization;
import models.Plan;
import models.Role;
import models.Topic;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class TestPlanProgress extends UnitTest {
	
	@Before
	public void setup() {
		Fixtures.deleteAll();
	}

	@Test
	public void TestCalculateProgress(){
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
		
		Plan plan = new Plan("S.U. heads", ashraf, new Date(111, 03, 20),
				new Date(111, 07, 30), "Plan for SU heads elections",
				gucMetStudentUnion, "summer break").save();
		Item firstItem = new Item(
				new Date(111, 05, 01),
				new Date(111, 04, 20),
				"One of the members who is not runing for elections should organize a debate between the candidates",
				plan, "Candidates Debate");
		firstItem.save();

		Item secondItem = new Item(
				new Date(111, 05, 01),
				new Date(111, 06, 20),
				"One of the members who is not runing for elections should organize a debate between the candidates",
				plan, "Candidates Debate");
		

		plan.items.add(firstItem);
		plan.items.add(secondItem);
		plan.save();
		secondItem.save();
		firstItem.status = 2;
		firstItem.save();
		assertEquals(50, plan.calculateProgress());
		
		secondItem.status = 1;
		secondItem.save();
		assertEquals(75, plan.calculateProgress());

	}

}
