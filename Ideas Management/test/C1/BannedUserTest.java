package C1;
import java.util.List;

import play.test.*;
import models.BannedUser;
import models.MainEntity;
import models.Organization;
import models.Role;
import models.Topic;
import models.User;
import models.UserRoleInOrganization;

import org.junit.*;

import controllers.Organizations;
import controllers.Roles;
import controllers.UserRoleInOrganizations;
import controllers.Users;

public class BannedUserTest extends UnitTest {
	@Test
	public void constructorAndDeleteTest(){
		List<User> bannedUsers = User.findAll();
		List<Organization> organizations = Organization.findAll();
		List <MainEntity> entities = MainEntity.findAll();
		if ((!bannedUsers.isEmpty()) &&(!organizations.isEmpty()) &&(!entities.isEmpty())){
			User bannedUser = bannedUsers.get(0);
			Organization organization = organizations.get(0);
			long entityId = entities.get(0).getId();
			
			BannedUser newBannedUser = new BannedUser(bannedUser,organization ,
					"testing action", "entity", entityId);
			
			newBannedUser.save();
	    	bannedUser.bannedUsers.add(newBannedUser);
			organization.bannedUsers.add(newBannedUser);
			
			BannedUser bannedUserTest  = BannedUser.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceID = ? and bu.resourceType = ? ", bannedUser,organization, "testing action" , entityId , "entity").first();
			assertNotNull(bannedUserTest);
			BannedUser.delete(bannedUserTest);
			BannedUser deletedBannedUserTest  = BannedUser.find("select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action = ? and bu.resourceID = ? and bu.resourceType = ? ", bannedUser,organization, "testing action" , entityId , "entity").first();
            assertNull(deletedBannedUserTest);
			}
		
	}
}
