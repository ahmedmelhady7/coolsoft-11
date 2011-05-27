package TestUser;

import java.util.Date;

import models.Item;
import models.MainEntity;
import models.Organization;
import models.Plan;
import models.Role;
import models.Topic;
import models.User;
import models.VolunteerRequest;

import org.junit.Before;
import org.junit.Test;

import controllers.Roles;
import controllers.UserRoleInOrganizations;

import play.test.Fixtures;
import play.test.UnitTest;

public class TestUserPlan extends UnitTest {

	@Before
	public void setup() {
		Fixtures.deleteAll();
	}

	@Test
	public void userPlanTest() {
		Role.createIdeaDeveloperRole();
		Role.createOrganizerRole();
		Role.createAdminRole();
		Role.createOrganizationLeadRole();
		Role organizer = Roles.getRoleByName("organizer");
		Role ideadeveloper = Roles.getRoleByName("idea developer");
		Role OrganizationLead = Roles.getRoleByName("organizationLead");
		
		User rf = new User("aymaestro@gmail.com", "majic", "1234", "Ahmed",
				"Maged", "What is our company's name?", "coolsoft", 0,
				"11/11/2010", "Egypt", "Programmer");
		rf.save();

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
				new Date(111, 04, 20),
				"One of the members who is not runing for elections should organize a debate between the candidates",
				p1, "Candidates Debate");
		item1.save();
		
		Item item2 = new Item(
				new Date(111, 05, 04),
				new Date(111, 05, 14),
				"One of the SU members should reserve a room for the debate",
				p1, "Room reservation");
		item2.save();
		
		p1.items.add(item1);
		p1.items.add(item2);
		
		p1.save();
		Item item3 = new Item(
				new Date(111, 04, 01),
				new Date(111, 05, 28),
				"One of the members should set up an electronic election process",
				p1, "Election Process Setup");

		item3.save();

		p1.items.add(item3);
		p1.save();

		UserRoleInOrganizations.addEnrolledUser(mai, guc, ideadeveloper,
				gucMetStudentUnion.id, "topic");
		//Tests
		VolunteerRequest volunteerRequest = new VolunteerRequest(
				mai, item3, "because I want to").save();
		
		item3.addVolunteerRequest(volunteerRequest);
		
		mai.addVolunteerRequest(volunteerRequest);


		assertEquals(true, mai.canVolunteer(item2.id));
		assertEquals(false, mai.canVolunteer(item3.id));

		assertEquals(false, rf.canVolunteer(item2.id));
		assertEquals(false, ashraf.canVolunteer(item2.id));
		assertEquals(false, mai.canWork(item2.id));
		assertEquals(false, rf.canWork(item2.id));
		assertEquals(true, ashraf.canWork(item2.id));
		
		assertEquals(true, mai.pendingVolunteerRequest(item3.id));
		
	}
}

