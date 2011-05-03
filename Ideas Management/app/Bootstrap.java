import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.Timer;

import controllers.TimerCall;

import play.*;
import play.jobs.*;
import play.test.*;

import models.*;

@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob() {
		System.out.println("DOJOB");
		// if (Topic.count() == 0) {
		System.out.println("in");
		User u1 = new User("h@gmail.com", "1234", "Hamed", "EL-Akhdar",
				"hamedcool", 0, new Date(), "egypt", "student");
		u1._save();
		User u2 = new User("bob@gmail.com", "1234", "Mohamed", "Gomaa",
				"Gomaa", 0, new Date(), "egypt", "student");
		u2._save();
		User u3 = new User("teet@gmail.com", "1234", "Ibrahim", "EL-khayat",
				"IA", 0, new Date(), "egypt", "student");
		u3._save();
		User u4 = new User("faruki@gmail.com", "1234", "Omar", "Faruki",
				"Gunners", 0, new Date(), "egypt", "student");
		u4._save();
		User u5 = new User("mostafa@gmail.com", "1234", "mostafa",
				"abo el atta", "Sasa", 0, new Date(), "egypt", "student");
		u5._save();
		User u6 = new User("Ibrahim@gmail.com", "1234", "Ibrahim", "Safwat",
				"Ibrahim", 0, new Date(), "egypt", "student");
		u6._save();
		System.out.println("in2");

		
		Organization org1 = new Organization("GUC", u1, (short) 0, true);
		org1.enrolledUsers.add(u1);
		org1.enrolledUsers.add(u2);
		org1.enrolledUsers.add(u3);
		org1.enrolledUsers.add(u4);
		
		System.out.println("in3");
		org1._save();
		
		Tag t1 = new Tag("Education",org1).save();
		Tag t2 = new Tag("GUC",org1).save();
		Tag t3 = new Tag("Egypt",org1).save();
		Tag t4 = new Tag("Sports",org1).save();
		
		org1.relatedTags.add(t2);
		org1.relatedTags.add(t3);

		MainEntity me1 = new MainEntity("MET",
				"Media Engineering and technology", org1);
		me1.organizers.add(u2);
		me1.organizers.add(u1);
		me1.tagList.add(t1);
		me1._save();

		MainEntity me2 = new MainEntity("CS",
				"Computer Science and Engineering", me1, org1);
		me2.organizers.add(u2);
		me2.organizers.add(u1);
		me2.tagList.add(t2);
		me2._save();
		System.out.println("in4");

		Topic to1 = new Topic("Student union", "Suggestions", 0, u1, me1);
		// to1.followers.add(u5);
		// to1.followers.add(u6);
		to1._save();
		to1.tags.add(t2);
		to1._save();
		u5.topicsIFollow.add(to1);
		u5.save();

		Topic to2 = new Topic("Sports area", "upgrades", 0, u1, me1);
		to2.tags.add(t4);
		to2._save();

		System.out.println("in5");
		Idea i1 = new Idea("Football courts", "bulding new football courts",
				u2, to2);
		i1.tagsList.add(t4);
		i1.usersRated.add(u4);
		i1.save();
		u3.ideasReported.add(i1);
		u3.save();

		Idea i2 = new Idea("Tennis courts", "bulding new football courts", u5,
				to2);
		i2.tagsList.add(t4);
		i2.usersRated.add(u4);
		i2.save();
		u2.ideasReported.add(i2);
		u2.save();
		
		Idea i3 = new Idea("Windows 7", "Install windows 7", u5,
				to2);
		
		i3.tagsList.add(t4);
		i3.usersRated.add(u4);
		i3.save();
		u3.ideasReported.add(i3);
		u3.save();

		Plan p1 = new Plan("Improving Labs", u1, new Date(2011,07,01),
				new Date(2012,07,01), "Plan for improving the CS labs", to2,
				"new software").save();
		
		i3.plan = p1;
		i3._save();
		
		p1.usersRated.add(u2);
		p1.rating = 1;
		
		p1._save();
		
		Comment c1 = new Comment("i prefer new Computers ", i1, u1, new Date()).save();
		c1.commentedIdea = i2;
		c1._save();
		
		
		Comment c2 = new Comment("good idea", p1, u2, new Date()).save();
		c2.commentedPlan = p1;
		c2._save();
		

		RequestToJoin request = new RequestToJoin(u1, to1, org1,
				"I would like to join your organization..");
		request._save();

	}
}
