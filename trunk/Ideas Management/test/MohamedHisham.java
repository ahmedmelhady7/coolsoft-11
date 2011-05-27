import models.EntityRelationship;
import models.MainEntity;
import models.Organization;
import models.Tag;
import models.TagRelationship;
import models.Topic;
import models.TopicRelationship;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;


public class MohamedHisham extends UnitTest{
	
	@Before
	public void setUp() {
	    Fixtures.deleteAll();
	}
	
	@Test
	public void createEntityRelationship(){
		User user = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234", "Ashraf",
				"Mansoor", "What is our company's name?", "coolsoft", 0,
				"2/14/1995", "Egypt", "student").save();
	
		Organization guc = new Organization("GUC", user, 0, true, "guc").save();
	
		MainEntity met = new MainEntity("MET", "Media Engineering", guc, true).save();
		MainEntity dmet = new MainEntity("DMET", "Media Engineering", guc, true).save();
		
		EntityRelationship relation1 = new EntityRelationship("relation1", met, dmet);
		
		assertNotNull(relation1);
		assertEquals(relation1.name, "relation1");
		assertEquals(relation1.source, met);
		assertEquals(relation1.destination, dmet);
		
	}
	
	@Test
	public void createTopicRelationship(){
		User user = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234", "Ashraf",
				"Mansoor", "What is our company's name?", "coolsoft", 0,
				"2/14/1995", "Egypt", "student").save();
	
		Organization guc = new Organization("GUC", user, 0, true, "guc").save();
	
		MainEntity met = new MainEntity("MET", "Media Engineering", guc, true).save();
		
		Topic topic1 = new Topic("T1", "t1", 2, user, met, true).save();
		Topic topic2 = new Topic("T2", "t2", 2, user, met, true).save();
		
		TopicRelationship relation1 = new TopicRelationship("relation1", topic1, topic2);
		
		assertNotNull(relation1);
		assertEquals(relation1.name, "relation1");
		assertEquals(relation1.source, topic1);
		assertEquals(relation1.destination, topic2);
		
	}
	
	@Test
	public void createTagRelationship(){
		User user = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234", "Ashraf",
				"Mansoor", "What is our company's name?", "coolsoft", 0,
				"2/14/1995", "Egypt", "student").save();
	
		Organization guc = new Organization("GUC", user, 0, true, "guc").save();
		
		Tag tag1 = new Tag("tag1", guc, user).save();
		Tag tag2 = new Tag("tag2", guc, user).save();
		
		TagRelationship relation1 = new TagRelationship("relation1", tag1, tag2);
		
		assertNotNull(relation1);
		assertEquals(relation1.name, "relation1");
		assertEquals(relation1.source, tag1);
		assertEquals(relation1.destination, tag2);
		
	}
	
	@Test
	public void followTag(){
		User user = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234",
				"Ashraf", "Mansoor", "What is our company's name?", "coolsoft",
				0, "2/14/1995", "Egypt", "student").save();

		Organization guc = new Organization("GUC", user, 1, true,
				"The German University in Cairo").save();

		Tag tagEducation = new Tag("Education", guc, user).save();
		
		user.follow(tagEducation);
		tagEducation.follow(user);
		assertTrue(user.followingTags.contains(tagEducation));
		assertTrue(tagEducation.followers.contains(user));
	}
	
	

}
