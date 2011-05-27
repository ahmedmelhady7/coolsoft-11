package testInvitation;

import models.Invitation;
import models.MainEntity;
import models.Organization;
import models.Topic;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class TestInvitationConstructor extends UnitTest {
	
	@Before
	public void setUp() {
		Fixtures.deleteAll();
	}
	
	@Test
	public void invitationTest(){
		String email = "fadwasakr@gmail.com";
		User slim = new User("testslim.abdennadher@guc.edu.eg", "mezo",
				"1234", "Slim", "Abd EL Nadder", "What is our company's name?", "coolsoft",
				0, "11/11/2010",
				"Egypt", "Professor").save();
		Organization guc = new Organization("GUC",slim, 0, false, "guc")
		.save();
		MainEntity met = new MainEntity("MET", "Media Engineering", guc, false)
		.save();
		String role = "idea developer";
		Topic gucMetStudentUnion = new Topic("Student union",
				"Suggestions", 2, slim, met, true).save();
		gucMetStudentUnion._save();
		Invitation entityInvitation = new Invitation(email,met,guc,role,slim,null).save();
		assertNotNull(entityInvitation);
		Invitation topicInvitation = new Invitation(email,null,guc,role,slim,gucMetStudentUnion).save();
		assertNotNull(topicInvitation);
		Invitation organizationInvitation = new Invitation(email,null,guc,role,slim,null).save();
		assertNotNull(organizationInvitation);
		
	}
}
