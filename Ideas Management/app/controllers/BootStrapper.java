package controllers;

import java.util.Date;

import models.MainEntity;
import models.NotificationProfile;
import models.Organization;
import models.RequestToJoin;
import models.Tag;
import models.Topic;
import models.User;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class BootStrapper extends Job {

	// use for simple fast testing
	public void addDefaults() {
		// Fixtures.loadModels("data.yml");
		User u1 = new User("h@gmail.com", "1234", "Hamed", "EL-Akhdar",
				"hamedcool", 0, new Date("1990-11-11"), "Egypt", "Student");
		u1._save();
		Organization org1 = new Organization("HamedSoft", u1, (short) 0, true);
		org1._save();
		MainEntity me1 = new MainEntity("Hamed yntlek",
				"ideas to help hamed become like billgates");
		me1._save();
		Topic t1 = new Topic("What to do?", "Help me please", (short) 0, u1,
				me1);
		t1._save();
		Tag tag = new Tag("Sports");
		tag._save();
		RequestToJoin request = new RequestToJoin(u1, t1, org1, "I would like to join your organization..");
		request._save();
	}
}
