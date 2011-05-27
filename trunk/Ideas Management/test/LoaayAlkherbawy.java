import models.Idea;
import models.LinkDuplicatesRequest;
import models.MainEntity;
import models.Organization;
import models.Topic;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class LoaayAlkherbawy extends UnitTest {

	@Before
	public void setUp() {
		Fixtures.deleteAll();
	}

	@Test
	public void createDuplicateRequest() {
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
		user.save();
		Idea idea2 = new Idea(
				"C7 Labs",
				"These kinds of labs should be working 24/7 so i suggest to have maintanence man from the IT for every lab it won't be costy and it will save a lot of time ",
				user, gucMetStudentUnion);
		idea2.save();
		user.save();
		LinkDuplicatesRequest test = new LinkDuplicatesRequest(user, idea1,
				idea2, "Description of the request");
		assertNotNull(test);
	}

}
