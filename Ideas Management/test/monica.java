

import java.util.List;

import play.test.*;
import models.Idea;
import models.MainEntity;
import models.Organization;
import models.Topic;

import org.junit.*;

public class monica extends UnitTest {
	@Test
	public void checkIncrementView() {
		List<Topic> topics = Topic.findAll();
		Topic topic = topics.get(0);
		int viewbefore = topic.viewed;
		topic.incrmentViewed();
		topic.save();
		int viewafter = topic.viewed;
		assertEquals(viewafter, viewbefore + 1);

		List<Idea> ideas = Idea.findAll();
		Idea idea = ideas.get(0);
		viewbefore = idea.viewed;
		idea.incrmentViewed();
		idea.save();
		viewafter = idea.viewed;
		assertEquals(viewafter, viewbefore + 1);

		List<Organization> orgs = Organization.findAll();
		Organization org = orgs.get(0);
		viewbefore = org.viewed;
		org.incrmentViewed();
		org.save();
		viewafter = org.viewed;
		assertEquals(viewafter, viewbefore + 1);

		List<MainEntity> entities = MainEntity.findAll();
		MainEntity entity = entities.get(0);
		viewbefore = entity.viewed;
		entity.incrmentViewed();
		entity.save();
		viewafter = entity.viewed;
		assertEquals(viewafter, viewbefore + 1);
	}

}
