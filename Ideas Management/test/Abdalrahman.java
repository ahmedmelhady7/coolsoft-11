
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import models.CreateRelationshipRequest;
import models.Idea;
import models.Label;
import models.MainEntity;
import models.Organization;
import models.Topic;
import models.User;
import play.test.UnitTest;

public class Abdalrahman extends UnitTest
{
	@Test
	public void createDraft()
	{
		User user = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234", "Ashraf",
				"Mansoor", "What is our company's name?", "coolsoft", 0,
				"2/14/1995", "Egypt", "student").save();
		
		Organization guc = new Organization("GUC", user, 0, true, "guc").save();
		
		MainEntity met = new MainEntity("MET", "Media Engineering", guc, false).save();

		Topic topic = new Topic("T1", "t1", 2, user, met, true).save();
		
		Idea idea = new Idea("title", "sdfs", user, topic, true);
		
		assertNotNull(idea);
		assertTrue(idea.isDraft);
	}
	
	@Test
	public void createLabel()
	{
		User user = new User("noha", "coolsoft", "1234", "Abdalrahman","Ali", "uni?", "GUC", 0, "11/11/1990","Egypt","Engineer").save();

		Label label = new Label("kill", user).save();

		assertNotNull(label);
		assertEquals(label.user.email, user.email);
	}
	
	@Test
	public void createLabel2()
	{
		User user = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234", "Ashraf",
				"Mansoor", "What is our company's name?", "coolsoft", 0,
				"2/14/1995", "Egypt", "student").save();
		
		Organization guc = new Organization("GUC", user, 0, true, "guc").save();
		
		MainEntity met = new MainEntity("MET", "Media Engineering", guc, false).save();

		Topic topic = new Topic("T1", "t1", 2, user, met, true).save();
		
		Idea idea = new Idea("killw", "kill", user, topic);
		
		ArrayList<Idea> k = new ArrayList<Idea>();
		k.add(idea);
		
		Label label = new Label("sadf",user,k).save();
		
		assertNotNull(label);
		assertEquals(label.ideas.size(),1);
	}
}	
