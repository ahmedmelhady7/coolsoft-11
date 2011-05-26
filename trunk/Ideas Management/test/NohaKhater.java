import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.test.*;
import models.CreateRelationshipRequest;
import models.EntityRelationship;
import models.MainEntity;
import models.Organization;
import models.RenameEndRelationshipRequest;
import models.Role;
import models.Topic;
import models.TopicRelationship;
import models.User;

import org.junit.*;

import controllers.Roles;
import controllers.UserRoleInOrganizations;

public class NohaKhater extends UnitTest {

	@Before
	public void setUp() {
		Fixtures.deleteAll();
	}

	@Test
	public void createEntity() {
		User user = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234", "Ashraf",
				"Mansoor", "What is our company's name?", "coolsoft", 0,
				"2/14/1995", "Egypt", "student").save();
		Organization guc = new Organization("GUC", user, 0, false, "guc")
				.save();
		MainEntity met = new MainEntity("MET", "Media Engineering", guc, false)
				.save();
		assertNotNull(met);
		assertFalse(met.createRelationship);
		assertTrue(guc.entitiesList.contains(met));
	}

	@Test
	public void createSubEntity() {
		User user = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234", "Ashraf",
				"Mansoor", "What is our company's name?", "coolsoft", 0,
				"2/14/1995", "Egypt", "student").save();
		Organization guc = new Organization("GUC", user, 0, false, "guc")
				.save();
		MainEntity met = new MainEntity("MET", "Media Engineering", guc, false)
				.save();
		MainEntity cs = new MainEntity("CS", "cs", met, guc, false).save();
		assertNotNull(cs);
		assertFalse(cs.createRelationship);
		assertTrue(met.subentities.contains(cs));
		assertSame(met, cs.parent);
	}

	@Test
	public void requestRelation1() {
		User user = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234", "Ashraf",
				"Mansoor", "What is our company's name?", "coolsoft", 0,
				"2/14/1995", "Egypt", "student").save();
		Organization guc = new Organization("GUC", user, 0, true, "guc")
				.save();
		MainEntity met = new MainEntity("MET", "Media Engineering", guc, false)
				.save();
		MainEntity cs = new MainEntity("CS", "cs", met, guc, true).save();
		CreateRelationshipRequest request = new CreateRelationshipRequest(user,
				met, cs, "manages", guc, 0).save();
		Topic t1 = new Topic("T1", "t1", 2, user, met, true).save();
		Topic t2 = new Topic("T2", "t2", 2, user, met, true).save();
		CreateRelationshipRequest request2 = new CreateRelationshipRequest(user,
				t1, t2, "related to", guc, 1).save();
		assertNotNull(request);
		assertTrue(guc.createRelationshipRequest.contains(request));
		assertSame(guc, request.organisation);
		assertNotNull(request2);
		assertTrue(guc.createRelationshipRequest.contains(request2));
		assertSame(guc, request2.organisation);
	}
	
	@Test
	public void requestRelation2() {
		User user = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234", "Ashraf",
				"Mansoor", "What is our company's name?", "coolsoft", 0,
				"2/14/1995", "Egypt", "student").save();
		Organization guc = new Organization("GUC", user, 0, true, "guc")
				.save();
		MainEntity met = new MainEntity("MET", "Media Engineering", guc, false)
				.save();
		MainEntity cs = new MainEntity("CS", "cs", met, guc, true).save();
		EntityRelationship relation = new EntityRelationship("manages", met, cs).save();
		RenameEndRelationshipRequest request = new RenameEndRelationshipRequest(user, guc, relation, 0, 1, "has").save();
		RenameEndRelationshipRequest request2 = new RenameEndRelationshipRequest(user, guc, relation, 0, 0, null).save();
		Topic t1 = new Topic("T1", "t1", 2, user, met, true).save();
		Topic t2 = new Topic("T2", "t2", 2, user, met, true).save();
		TopicRelationship relation2 = new TopicRelationship("has", t1, t2).save();
		RenameEndRelationshipRequest request3 = new RenameEndRelationshipRequest(user, guc, relation2, 1, 1, "belongs to").save();
		RenameEndRelationshipRequest request4 = new RenameEndRelationshipRequest(user, guc, relation2, 1, 0, null).save();
		assertNotNull(request);
		assertNotNull(request2);
		assertNotNull(request3);
		assertNotNull(request4);
		assertTrue(guc.renameEndRelationshipRequest.contains(request));
		assertTrue(guc.renameEndRelationshipRequest.contains(request2));
		assertTrue(guc.renameEndRelationshipRequest.contains(request3));
		assertTrue(guc.renameEndRelationshipRequest.contains(request4));
		assertSame(guc, request.organisation);		
		assertSame(guc, request2.organisation);
		assertSame(guc, request3.organisation);
		assertSame(guc, request4.organisation);
	}
}
