package C2;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.test.*;
import models.Organization;
import models.Role;
import models.User;

import org.junit.*;

import controllers.Roles;
import controllers.UserRoleInOrganizations;

public class OmarFaruki extends UnitTest{

	@Before
	public void setUp() {
	    Fixtures.deleteAll();
	}
	
	@Test
	public void getAllOrganizations() {
		User ashraf = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234",
				"Ashraf", "Mansoor", "What is our company's name?", "coolsoft",
				0, "2/14/1995", "Egypt",
				"student").save();
		User faruki = new User("Faruki@guc.edu.eg", "Faruki", "1234",
				"Omar", "Faruki", "Favourite course?", "Faruki",
				0, "2/14/1995", "Egypt",
				"professor").save();
		Role.createIdeaDeveloperRole();
		Role.createOrganizationLeadRole();
		Organization guc = new Organization("GUC", ashraf, 1, true,
		"The German University in Cairo").save();
		
		Organization auc = new Organization("AUC", faruki, 1, true,
		"The American University in Cairo").save();
		
		Role role1 = Roles.getRoleByName("organizationLead");
		UserRoleInOrganizations.addEnrolledUser(ashraf, auc,
				role1);
		
		Role role = Roles.getRoleByName("idea developer");
		UserRoleInOrganizations.addEnrolledUser(ashraf, guc,
				role);
		
		UserRoleInOrganizations.addEnrolledUser(faruki, auc,
				role);
		List<Organization> organizations = new ArrayList<Organization>();
		
		organizations.add(auc);
		organizations.add(guc);
		assertEquals(organizations, ashraf.getOrganizations());
		
		List<Organization> organizations1 = new ArrayList<Organization>();
		organizations1.add(auc);
		assertEquals(organizations1, faruki.getOrganizations());
	}
}
