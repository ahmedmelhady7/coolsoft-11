package TestIdea;

import static org.junit.Assert.*;

import java.util.Date;

import models.Idea;
import models.MainEntity;
import models.Organization;
import models.Plan;
import models.Role;
import models.Topic;
import models.User;

import org.junit.Before;
import org.junit.Test;

import controllers.Roles;
import controllers.UserRoleInOrganizations;

import play.test.Fixtures;
import play.test.UnitTest;

public class TestIdea extends UnitTest {
	
	@Before
	public void setup() {
		Fixtures.deleteAll();
	}
	
	@Test
	public void IdeaTest(){
		
		Role.createIdeaDeveloperRole();
		Role.createOrganizerRole();
		Role.createAdminRole();
		Role.createOrganizationLeadRole();
		Role organizer = Roles.getRoleByName("organizer");
		Role ideadeveloper = Roles.getRoleByName("idea developer");
		Role OrganizationLead = Roles.getRoleByName("organizationLead");
		//create a new User
		User hadi = new User("elhadiahmed3@gmail.com", "hadi.18","notreal", "Ahmed", "El-Hadi","What is our company's name?", "coolsoft", 0, "11/11/2010",null, "");
		hadi._save();
		//create a new User
		new User("Ashraf@guc.edu.eg", "ElKbeer", "1234","Ashraf", "Mansoor", "What is our company's name?", "coolsoft",0, "11/11/2010", "Egypt","student").save();
		
		// Retrieve the user with his email
		User ashraf = User.find("byEmail", "Ashraf@guc.edu.eg").first();
		
		// Test 
        assertNotNull(hadi);
        assertNotNull(ashraf);
        
		//create new Organization
        Organization guc = new Organization("GUC", ashraf, 1, true,"The German University in Cairo").save();
        
        //create new Entity
        MainEntity gucMet = new MainEntity("MET","Media Engineering and technology", guc, true).save();
        
        //create new Topic
        Topic gucMetStudentUnion = new Topic("Student union","Suggestions", 2, ashraf, gucMet, true).save();

        assertEquals(false, gucMetStudentUnion.canView(hadi));
        
        UserRoleInOrganizations.addEnrolledUser(hadi, guc, ideadeveloper,gucMetStudentUnion.id, "topic");
        
        assertEquals(true, gucMetStudentUnion.canView(hadi));
        
        UserRoleInOrganizations.addEnrolledUser(ashraf, guc,OrganizationLead);
        
        Idea i3 = new Idea("C7 Labs","These kinds of labs should be working 24/7 so i suggest to have maintanence man from the IT for every lab it won't be costy and it will save a lot of time ",hadi, gucMetStudentUnion);
		i3.save();
		hadi.save();
		
		assertEquals(true, i3.author.equals(hadi));
		assertEquals(1, hadi.ideasCreated.size());
		
		Idea i5 = new Idea("CA Course","this course must be more organized and Dr. Amr T. should be more respectful to our mentallity as adults",hadi, gucMetStudentUnion);
		i5.save();
		hadi.save();
		Idea i6 = new Idea("SU is Important","We Can Make our University better", hadi,gucMetStudentUnion);
		i6.save();
		hadi.save();

        //Test 
        assertNotNull(guc);
        assertNotNull(gucMet);
        assertNotNull(gucMetStudentUnion);
        assertNotNull(i3);
        assertNotNull(i5);
        assertNotNull(i6);
        assertEquals("C7 Labs", i3.title);
        i3.delete();
        assertFalse(gucMetStudentUnion.ideas.contains(i3));
        i5.hidden=true;
        assertTrue(i5.hidden);
        Plan p1 = new Plan("S.U. heads", ashraf, new Date(111, 03, 20),
				new Date(111, 07, 30), "Plan for SU heads elections",
				gucMetStudentUnion, "summer break").save();
		gucMetStudentUnion.plan = p1;
		gucMetStudentUnion.openToEdit = false;
		gucMetStudentUnion.save();
		ashraf.planscreated.add(p1);
		ashraf.save();

		i6.plan = p1;
		i6._save();
		p1.addIdea(i6);
		p1.save();
		i6.delete();
		assertNotNull(i6);        
		assertEquals(true, i3.belongsToTopic.canView(hadi));
        
	}
}
