

import java.util.List;

import play.test.*;
import models.Idea;
import models.MainEntity;
import models.Organization;
import models.Topic;
import models.User;

import org.junit.*;

public class monica extends UnitTest {
	@Before
	public void setUp() {
		Fixtures.deleteAll();
	} 
	@Test
	public void checkIncrementView() {
		
		User user = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234", "Ashraf",
				"Mansoor", "What is our company's name?", "coolsoft", 0,
				"2/14/1995", "Egypt", "student").save();
		
		Organization org = new Organization("GUC", user, 0, true, "guc").save();
		
		MainEntity entity = new MainEntity("MET", "Media Engineering", org, false).save();

		Topic topic = new Topic("T1", "t1", 2, user, entity, true).save();
		
		Idea idea = new Idea("title", "sdfs", user, topic, true);
		

		int viewbefore = topic.viewed;
		topic.incrmentViewed();
		topic.save();
		int viewafter = topic.viewed;
		assertEquals(viewafter, viewbefore + 1);

		viewbefore = idea.viewed;
		idea.incrmentViewed();
		idea.save();
		viewafter = idea.viewed;
		assertEquals(viewafter, viewbefore + 1);

		viewbefore = org.viewed;
		org.incrmentViewed();
		org.save();
		viewafter = org.viewed;
		assertEquals(viewafter, viewbefore + 1);

		viewbefore = entity.viewed;
		entity.incrmentViewed();
		entity.save();
		viewafter = entity.viewed;
		assertEquals(viewafter, viewbefore + 1);
	}

}
