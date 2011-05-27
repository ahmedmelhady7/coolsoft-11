package TestLog;

/**
* Lama Ashraf
**/

import models.Log;
import models.MainEntity;
import models.Organization;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class LogTest extends UnitTest{
	@Before
	public void setup() {
		Fixtures.deleteAll();
	}
	
	@Test
	public void addLogTest() {
		User slim = new User("testslim.abdennadher@guc.edu.eg", "mezo",
				"1234", "Slim", "Abd EL Nadder", "What is our company's name?", "coolsoft",
				0, "11/11/2010",
				"Egypt", "Professor").save();
		Organization guc = new Organization("GUC",slim, 0, false, "guc")
		.save();
		MainEntity met = new MainEntity("MET", "Media Engineering", guc, false).save();
		String description = "slim is the organization lead of the guc";
		String description2 = "slim is the organization lead of the guc2";
		assertEquals(0, guc.logs.size());
		assertEquals(0, met.logs.size());
		assertEquals(0, slim.logs.size());
		Log.addLog(description, guc, met, slim);
		assertEquals(1, guc.logs.size());
		assertEquals(1, met.logs.size());
		assertEquals(1, slim.logs.size());
		Log.addLog(description, guc, slim);
		assertEquals(2, guc.logs.size());
		assertEquals(1, met.logs.size());
		assertEquals(2, slim.logs.size());

		
}
	
	
}