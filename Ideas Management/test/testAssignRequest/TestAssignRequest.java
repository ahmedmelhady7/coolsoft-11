package testAssignRequest;

import java.util.Date;

import models.AssignRequest;
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

public class TestAssignRequest extends UnitTest {

	@Before
	public void setup() {
		Fixtures.deleteAll();
	}

	@Test
	public void assignRequestConstructor() {
		Role.createIdeaDeveloperRole();
		Role.createOrganizerRole();
		Role.createAdminRole();
		Role.createOrganizationLeadRole();

		User ashraf = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234",
				"Ashraf", "Mansoor", "What is our company's name?", "coolsoft",
				0, "11/11/2010", "Egypt", "student").save();
		User mai = new User("mai.jt4@gmail.com", "mai", "1234", "Mai",
				"Magdy", "What is our company's name?", "coolsoft", 0,
				"11/11/2010", "Egypt", "student");
		mai._save();
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
				new Date(111, 05, 20),
				"One of the members who is not runing for elections should organize a debate between the candidates",
				p1, "Candidates Debate");
		item1.save();
		
		
		//Tests
		AssignRequest assignRequest = new AssignRequest(item1,
				mai, ashraf, "come work on this item");

		assignRequest.save();
		
		item1.addAssignRequest(assignRequest);
		ashraf.addSentAssignRequest(assignRequest);
		mai.addReceivedAssignRequest(assignRequest);
		
		assertNotNull(assignRequest);
		assertEquals("come work on this item", assignRequest.content);
		assertEquals(ashraf, assignRequest.sender);
		assertEquals(mai, assignRequest.destination);
		assertEquals(item1, assignRequest.source);
		
		assertEquals(1, item1.assignRequests.size());
		assertEquals(1, ashraf.sentAssignRequests.size());
		assertEquals(1, mai.receivedAssignRequests.size());
		
		
		
	}
}
