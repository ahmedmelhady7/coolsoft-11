package TestUser;

/*
 * Mai Magdy
 */

import models.Invitation;
import models.MainEntity;
import models.Organization;
import models.Topic;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class TestAddInvitation  extends UnitTest{
		
		@Before
		public void setUp() {
			Fixtures.deleteAll();
		}
		
		@Test
		public void addInvitationTest(){
			
			String email = "mai.jt4@gmail.com";
			User slim = new User("testslim.abdennadher@guc.edu.eg", "mezo",
					"1234", "Slim", "Abd EL Nadder", "What is our company's name?", "coolsoft",
					0, "11/11/2010",
					"Egypt", "Professor").save();
			Organization guc = new Organization("GUC",slim, 0, false, "guc").save();
			MainEntity met = new MainEntity("MET", "Media Engineering", guc, false).save();
			String role = "organizer";
			Topic studentUnion = new Topic("Student union",
					"Suggestions", 2, slim, met, true).save();
			
			User user=User.find("byUsername", "mezo").first();
			assertNotNull(user);
			Organization organization=Organization.find("byName", "GUC").first();
			assertNotNull(organization);
			MainEntity entity=MainEntity.find("byName", "MET").first();
			assertNotNull(entity);
			Topic topic=Topic.find("byTitle", "Student union").first();
			assertNotNull(topic);
			
			assertEquals(1, User.count());
		    assertEquals(1, Organization.count());
		    assertEquals(1, MainEntity.count());
		    assertEquals(1, Topic.count());
		    
			slim.addInvitation(email, role, guc, null,null);
			assertEquals(1, Invitation.count());
			assertEquals(1, slim.invitation.size());
			assertEquals(0, met.invitationList.size());
			assertEquals(1, guc.invitation.size());
			assertEquals(0, studentUnion.invitations.size());
			
			slim.addInvitation(email, role, guc, met,null);
			assertEquals(2, Invitation.count());
			assertEquals(2, slim.invitation.size());
			assertEquals(1, met.invitationList.size());
			assertEquals(2, guc.invitation.size());
			assertEquals(0, studentUnion.invitations.size());
			
			slim.addInvitation(email, role, guc, met,studentUnion);
			assertEquals(3, Invitation.count());
			assertEquals(3, slim.invitation.size());
			assertEquals(2, met.invitationList.size());
			assertEquals(3, guc.invitation.size());
			assertEquals(1, studentUnion.invitations.size());
			
			
		}
	}



