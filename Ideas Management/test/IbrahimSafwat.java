import models.Idea;
import models.Plan;
import models.Comment;
import models.LinkDuplicatesRequest;
import models.MainEntity;
import models.Organization;
import models.Topic;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

import java.util.Date;


public class IbrahimSafwat extends UnitTest {

	@Before
	public void setUp() {
		Fixtures.deleteAll();
	}
	
	@Test
	public void createComments(){
		User user = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234", "Ashraf",
				"Mansoor", "What is our company's name?", "coolsoft", 0,
				"2/14/1995", "Egypt", "student").save();
		Organization guc = new Organization("GUC", user, 0, false, "guc")
				.save();
		MainEntity gucMet = new MainEntity("MET", "Media Engineering", guc,
				false).save();
		Topic gucMetStudentUnion = new Topic("Student union", "Suggestions", 2,
				user, gucMet, true).save();
		gucMetStudentUnion._save();
		Idea idea1 = new Idea(
				"CA Course",
				"this course must be more organized and Dr. Amr T. should be more respectful to our mentallity as adults",
				user, gucMetStudentUnion);
		idea1.save();
		
		
		Plan p1 = new Plan("S.U. heads", user, new Date(111, 03, 20),
				new Date(111, 07, 30), "Plan for SU heads elections",
				gucMetStudentUnion, "summer break").save();
		p1.save();
		user.save();
		
		Comment c1 = new Comment("comment 1 ", p1, user);
		Comment c2 = new Comment("comment 2 ", idea1, user);
		c1.save();
		c2.save();
		
		assertNotNull(c1);
	}

}	