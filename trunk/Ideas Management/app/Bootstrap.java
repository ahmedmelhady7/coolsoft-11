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
 * @version 1.7
 * 
 */

@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob() {

		if (User.count() < 5) {

			Role.createIdeaDeveloperRole();
			Role.createOrganizerRole();
			Role.createAdminRole();
			Role.createOrganizationLeadRole();

			User admin = new User("admin@coolsoft.com", "admin", "1234", "Mr.",
					"admin", 0, new Date(1990, 11, 11), "Egypt", "Prgrammer");
			admin.isAdmin = true;
			admin._save();

			User sharaf = new User("sharaf@eg.gov", "sharaf", "1234", "Asam",
					"Sharaf", 0, new Date(1955, 2, 14), "Egypt", "Primenister")
					.save();

			User gamal = new User("gmal@gov.eg", "gmal", "1234", "Yehya",
					"El Gamal", 0, new Date(1955, 5, 6), "Egypt", "menister")
					.save();

			User khalifa = new User("khalifa@gov.eg", "khalifa", "1234",
					"Abd El Kawi", "Khalifa", 0, new Date(1955, 7, 15),
					"Egypt", "Governer").save();

			User gom3a = new User("mgmohamedganem@gmail.com", "ghanem", "1234",
					"Mohamed", "Gomaa", 0, new Date(1991, 6, 4), "Egypt",
					"student");
			gom3a._save();
			
			User barnasa = new User("brns@gmail.com", "brns", "1234",
					"nano", "someone", 0, new Date(), "egypt", "student");
			barnasa._save();

			User khayat = new User("ibrahim.al.khayat@gmail.com", "ialk",
					"1234", "Ibrahim", "EL-khayat", 0, new Date(1991, 1, 19),
					"Egypt", "student");
			khayat._save();

			User mai = new User("mai.jt4@gmail.com", "mai", "1234", "Mai",
					"Magdy", 0, new Date(1990, 9, 14), "Egypt", "student");
			mai._save();

			User u5 = new User("mostafa.aly0@gmail.com", "s3eed", "1234",
					"mostafa", "Ali", 0, new Date(1990, 11, 11), "Egypt",
					"student");
			u5._save();

			User u6 = new User("Ibrahim@gmail.com", "Ibrahim", "1234",
					"Ibrahim", "Safwat", 0, new Date(1989, 1, 11), "Egypt",
					"student");
			u6._save();

			User fatma = new User("fatma.meawad@guc.edu.eg", "fmeawad", "1234",
					"Fatma", "Meawad", 0, new Date(1978, 4, 17), "Egypt",
					"Professor").save();

			User slim = new User(" slim.abdennadher@guc.edu.eg", "mezo",
					"1234", "Slim", "Abd EL Nadder", 0, new Date(1969, 4, 17),
					"Egypt", "Professor").save();

			User ashraf = new User("Ashraf@guc.edu.eg", "1234", "ElKbeer",
					"Ashraf", "Mansoor", 0, new Date(1960, 11, 11), "Egypt",
					"student").save();

			Organization guc = new Organization("GUC", ashraf, 1, true).save();

			Organization egypt = new Organization("Egypt", admin, 2, false)
					.save();

			Organization gov = new Organization("الحكومة المصرية", ashraf, 0,
					true).save();

			Tag tagEducation = new Tag("Education", guc).save();
			Tag tagGuc = new Tag("GUC", guc).save();

			Tag tagEgypt = new Tag("Egypt", gov).save();
			Tag tagGov = new Tag("Government", gov).save();

			guc.relatedTags.add(tagGuc);
			guc.relatedTags.add(tagEducation);
			guc.save();

			gov.relatedTags.add(tagEgypt);
			gov.relatedTags.add(tagGov);
			gov.save();

			MainEntity gucMet = new MainEntity("MET",
					"Media Engineering and technology", guc).save();
			gucMet.tagList.add(tagEducation);
			gucMet._save();

			MainEntity gucCs = new MainEntity("CS",
					"Computer Science and Engineering", gucMet, guc).save();
			gucCs.tagList.add(tagGuc);
			gucCs._save();

			MainEntity govHead = new MainEntity("الوزارة", "راسه مجلس الوزراء",
					gov).save();
			gucMet.tagList.add(tagEducation);
			gucMet._save();

			MainEntity elAhly = new MainEntity("El-Ahly",
					"Al-Ahly Club Lovers", egypt).save();

			Role organizer = Roles.getRoleByName("organizer");
			Role ideadeveloper = Roles.getRoleByName("idea developer");
			Role OrganizationLead = Roles.getRoleByName("organizationLead");

			UserRoleInOrganizations.addEnrolledUser(ashraf, guc,
					OrganizationLead);

			UserRoleInOrganizations.addEnrolledUser(slim, guc, organizer,
					gucMet.id, "entity");

			UserRoleInOrganizations.addEnrolledUser(khayat, guc, ideadeveloper,
					gucMet.id, "entity");

			UserRoleInOrganizations.addEnrolledUser(fatma, guc, organizer,
					gucCs.id, "entity");

			UserRoleInOrganizations.addEnrolledUser(mai, guc, ideadeveloper,
					gucCs.id, "entity");

			UserRoleInOrganizations.addEnrolledUser(sharaf, gov,
					OrganizationLead);

			UserRoleInOrganizations.addEnrolledUser(gamal, gov, organizer,
					gucMet.id, "entity");

			UserRoleInOrganizations.addEnrolledUser(khalifa, gov,
					ideadeveloper, gucMet.id, "entity");

			Topic gucMetStudentUnion = new Topic("Student union",
					"Suggestions", 0, ashraf, gucMet).save();

			gucMetStudentUnion._save();
			gucMetStudentUnion.tags.add(tagGuc);
			gucMetStudentUnion._save();

			Topic govImp = new Topic("التعديلات الدستورية", "اقتراحات", 1,
					sharaf, govHead).save();

			Topic egyahlymatch = new Topic("Ahly vs Zamalek",
					"for football lovers", 2, admin, elAhly).save();

			Topic innovation = new Topic("Create. Innovate",
					"how to encourage inventors and enhance creativity", 1,
					sharaf, govHead).save();

			Topic matchChaos = new Topic(
					"Chaos in egyptian football matches",
					"try to avoid chaos and fights during matches due to different points of view",
					2, admin, elAhly).save();

			// hadi add ideas here
			Idea alaga = new Idea("Kill_7", "like7", barnasa, innovation, true);
			alaga.save();
			barnasa.save();

			Plan p1 = new Plan("S.U. heads", ashraf, new Date(2011, 07, 01),
					new Date(2012, 07, 01), "Plan for SU heads elections",
					gucMetStudentUnion, "summer break").save();

			// i3.plan = p1;
			// i3._save();

			// p1.usersRated.add(gom3a);
			// p1.rating = 1;
			// i2.plan = p1;
			// i2._save();
			// i1.plan = p1;
			// i1._save();
			// p1.addIdea(i3);
			// p1.addIdea(i2);
			// p1.addIdea(i1);
			// p1.save();
			// // p1._save();
			//
			// Comment c1 = new Comment("i prefer new Computers ", i1, ashraf)
			// .save();
			// c1.commentedIdea = i2;
			// c1._save();
			//
			// Comment c2 = new Comment("good idea", p1, gom3a).save();
			// c2.commentedPlan = p1;
			// c2._save();
			//
			Item item1 = new Item(
					new Date(2011, 05, 01),
					new Date(2011, 05, 14),
					"One of the members who is not runing for elections should organize a debate between the candidates",
					p1, "Candidates Debate");
			Item item2 = new Item(
					new Date(2011, 05, 04),
					new Date(2011, 05, 14),
					"One of the SU members should reserve a room for the debate",
					p1, "Room reservation");
			Item item3 = new Item(
					new Date(2011, 04, 01),
					new Date(2011, 05, 28),
					"One of the members should set up an electronic election process",
					p1, "Election Process Setup");
			item1.save();
			item2.save();
			item3.save();
			p1.items.add(item1);
			p1.items.add(item2);
			p1.items.add(item3);
			p1.save();
			// p1.usersRated.add(gom3a);
			// // p1.rating = 1;
			//
			// p1._save();
			khayat.itemsAssigned.add(item1);
			khayat.itemsAssigned.add(item2);
			khayat.save();
			gom3a.itemsAssigned.add(item1);
			gom3a.save();
			item1.assignees.add(khayat);
			item1.assignees.add(gom3a);
			item1.save();
			// System.out.println(item1.assignees.get(0).email);
			item2.assignees.add(khayat);
			item2.save();
			//
			// RequestToJoin request = new RequestToJoin(gom3a, null, guc,
			// "I would like to join your organization..");
			// request._save();
			// RequestToJoin request2 = new RequestToJoin(gom3a,
			// gucMetStudentUnion, null,
			// "I would like to post on ur topic.");
			// request2._save();
			//
			// Invitation inv1 = new Invitation("Ibrahim@gmail.com", null, guc,
			// "idea developer", ashraf).save();
			//
			// // u6.invitation.add(inv1);
			// // u6.save();
			//
			// // UserRoleInOrganizations.addEnrolledUser(u2,, role,
			// // entityOrTopicId, type)

		}
	}
}
