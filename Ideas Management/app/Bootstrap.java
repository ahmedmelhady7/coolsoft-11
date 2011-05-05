import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.Timer;

import controllers.Roles;
import controllers.TimerCall;
import controllers.UserRoleInOrganizations;

import play.*;
import play.jobs.*;
import play.test.*;

import models.*;

/**
 * 
 * @author DebugTeam
 * @version 1.5
 * 
 */

@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob() {

		if (User.count() < 5) {
			System.out.println("in");
			Role.createIdeaDeveloperRole();
			Role.createOrganizerRole();
			Role.createAdminRole();
			Role.createOrganizationLeadRole();
			// add all roles and map them to users in org1
			User u1 = new User("h@gmail.com","hamedcool", "1234", "Hamed", "EL-Akhdar",
					 0, new Date(), "egypt", "student");
			u1._save();
			User u2 = new User("bob@gmail.com", "Gomaa","1234", "Mohamed", "Gomaa",
					 0, new Date(), "egypt", "student");
			u2._save();
			User u3 = new User("teet@gmail.com", "IA","1234", "Ibrahim","EL-khayat",
					  0, new Date(), "egypt", "student");
			u3._save();
			User u4 = new User("mai.jt4@gmail.com","Gunners", "1234", "Omar", "Faruki",
					 0, new Date(), "egypt", "student");
			u4._save();
			User u5 = new User("mostafa@gmail.com", "Sasa","1234", "mostafa","abo el atta",
					  0, new Date(), "egypt", "student");
			u5._save();
			User u6 = new User("Ibrahim@gmail.com","Ibrahim", "1234", "Ibrahim","Safwat",
					  0, new Date(), "egypt", "student");
			u6._save();
			// System.out.println("in2");

			Organization org1 = new Organization("GUC", u1, (short) 2, true);
			org1.enrolledUsers.add(u1);
			//org1.enrolledUsers.add(u2);
			org1.enrolledUsers.add(u3);
			org1.enrolledUsers.add(u4);

			// System.out.println("in3");
			org1._save();

			Tag t1 = new Tag("Education", org1).save();
			Tag t2 = new Tag("GUC", org1).save();
			Tag t3 = new Tag("Egypt", org1).save();
			Tag t4 = new Tag("Sports", org1).save();

			org1.relatedTags.add(t2);
			org1.relatedTags.add(t3);

			MainEntity me1 = new MainEntity("MET",
					"Media Engineering and technology", org1);
			//me1.organizers.add(u2);
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

			Topic to2 = new Topic("Sports area", "upgrades", 2, u1, me1);
			to2.tags.add(t4);
			to2._save();

			System.out.println("in5");
			Idea i1 = new Idea("Football courts",
					"bulding new football courts", u2, to2);
			i1.tagsList.add(t4);
			i1.usersRated.add(u4);
			i1.save();
			u3.ideasReported.add(i1);
			u3.save();

			Idea i2 = new Idea("Tennis courts", "bulding new football courts",
					u5, to2);
			i2.tagsList.add(t4);
			i2.usersRated.add(u4);
			i2.save();
			u2.ideasReported.add(i2);
			u2.save();

			Idea i3 = new Idea("Windows 7", "Install windows 7", u5, to2);

			i3.tagsList.add(t4);
			i3.usersRated.add(u4);
			i3.save();
			u3.ideasReported.add(i3);
			u3.save();

			Plan p1 = new Plan("Improving Labs", u1, new Date(2011, 07, 01),
					new Date(2012, 07, 01), "Plan for improving the CS labs",
					to2, "new software").save();

			i3.plan = p1;
			i3._save();

			p1.usersRated.add(u2);
			p1.rating = 1;
			i2.plan = p1;
			i2._save();
			i1.plan = p1;
			i1._save();
			p1.addIdea(i3);
			p1.addIdea(i2);
			p1.addIdea(i1);
			p1.save();
			// p1._save();

			Comment c1 = new Comment("i prefer new Computers ", i1, u1,
					new Date()).save();
			c1.commentedIdea = i2;
			c1._save();

			Comment c2 = new Comment("good idea", p1, u2, new Date()).save();
			c2.commentedPlan = p1;
			c2._save();

			Item item1 = new Item(new Date(2011, 01, 01),
					new Date(2011, 05, 10),
					"this is the second item in the plan", p1, "item1");
			Item item2 = new Item(new Date(2011, 02, 01),
					new Date(2011, 06, 10),
					"this is the third item in the plan", p1, "item2");
			Item item3 = new Item(new Date(2011, 03, 01),
					new Date(2011, 12, 10),
					"this is the first item in the plan", p1, "item3");
			item1.save();
			item2.save();
			item3.save();
			p1.items.add(item1);
			p1.items.add(item2);
			p1.items.add(item3);
			p1.save();
			p1.usersRated.add(u2);
			// p1.rating = 1;

			p1._save();
			u1.itemsAssigned.add(item1);
			u1.itemsAssigned.add(item2);
			u1.save();
			u2.itemsAssigned.add(item1);
			u2.save();
			item1.assignees.add(u1);
			item1.assignees.add(u2);
			item1.save();
			System.out.println(item1.assignees.get(0).email);
			item2.assignees.add(u2);
			item2.save();

			RequestToJoin request = new RequestToJoin(u2,null, org1,
					"I would like to join your organization..");
			request._save();
			RequestToJoin request2 = new RequestToJoin(u2, to1,null,
			"I would like to post on ur topic.");
	        request2._save();

			Invitation inv1 = new Invitation("Ibrahim@gmail.com", null, org1,
					"idea developer", u1).save();
		
		//	u6.invitation.add(inv1);
		//	u6.save();
			
			Role r1 = Roles.getRoleByName("organizer");
			long id1 = me1.getId();
			UserRoleInOrganizations.addEnrolledUser(u1, org1, r1, id1, "entity");
			
			Role r2 = Roles.getRoleByName("idea developer");
			UserRoleInOrganizations.addEnrolledUser(u4, org1, r2);
			
			//UserRoleInOrganizations.addEnrolledUser(u2,, role, entityOrTopicId, type)

		}
	}
}
