package jobs;

import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.Timer;

import controllers.Roles;
import controllers.UserRoleInOrganizations;

import play.*;
import play.jobs.*;
import play.test.*;

import models.*;

/**
 * 
 * @author DebugTeam
 * @version 2.0
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

			User rf = new User("aymaestro@gmail.com", "majic", "1234", "Ahmed",
					"Maged", "What is our company's name?", "coolsoft", 0, "11/11/2010", "Egypt", "Programmer");
			// System.out.println(rf.topicsReported.size());
			rf.save();		
			
			User admin = new User("admin@coolsoft.com", "admin", "1234", "Mr.",
					"admin", "What is our company's name?", "coolsoft", 0, "11/11/2010", "Egypt", "Programmer");
			admin.isAdmin = true;
			admin._save();
			
			User lama = new User("lama@coolsoft.com", "lama", "lama", "lama",
					"ashraf", "What is our company's name?", "coolsoft", 0, "11/06/1990", "Egypt", "Programmer");
			lama.isAdmin = true;
			lama._save();
			
			User noha = new User("noha@gmail.com", "noha",
					"1234", "noha", "khater", "What is our company's name?", "coolsoft",
					0, "11/11/2010",
					null, "").save();
			
			User alia = new User("alia@gmail.com", "alia",
					"1234", "alia", "el-bolock", "What is our company's name?", "coolsoft",
					0, "11/11/2010",
					null, "").save();
			

			User hadi = new User("elhadiahmed3@gmail.com", "hadi.18",
					"notreal", "Ahmed", "El-Hadi", "What is our company's name?", "coolsoft",
					0, "11/11/2010",
					null, "");
			
			hadi._save();

			User barnasa = new User("brns@gmail.com", "brns", "1234",
					"Abdelrahman", "Ali", "What is our company's name?", "coolsoft", 
					0, "11/11/2010", "egypt", "student");
			barnasa._save();

			User sharaf = new User("sharaf@eg.gov", "sharaf", "1234", "Asam",
					"Sharaf", "What is our company's name?", "coolsoft",
					0, "11/11/2010", "Egypt", "Primenister")
					.save();

			User gamal = new User("gmal@gov.eg", "gmal", "1234", "Yehya",
					"El Gamal", "What is our company's name?", "coolsoft",
					0, "11/11/2010", "Egypt", "menister")
					.save();

			User khalifa = new User("khalifa@gov.eg", "khalifa", "1234",
					"Abd El Kawi", "Khalifa", "What is our company's name?", "coolsoft",
					0, "11/11/2010",
					"Egypt", "Governer").save();

			User gom3a = new User("mgmohamedganem@gmail.com", "Ghanem", "1234",
					"Mohamed", "Gomaa", "What is our company's name?", "coolsoft",
					0, "11/11/2010", "Egypt",
					"student");
			gom3a._save();

			User khayat = new User("ibrahim.al.khayat@gmail.com", "ialk",
					"1234", "Ibrahim", "EL-khayat", "What is our company's name?", "coolsoft",
					0, "11/11/2010",
					"Egypt", "student");
			khayat._save();

			User mai = new User("mai.jt4@gmail.com", "mai", "1234", "Mai",
					"Magdy", "What is our company's name?", "coolsoft",
					0, "11/11/2010", "Egypt", "student");
			mai._save();

			User u5 = new User("mostafa.aly0@gmail.com", "s3eed", "1234",
					"mostafa", "Ali", "What is our company's name?", "coolsoft",
					0, "11/11/2010", "Egypt",
					"student");
			u5._save();

			User u6 = new User("Ibrahim@gmail.com", "Ibrahim", "1234",
					"Ibrahim", "Safwat", "What is our company's name?", "coolsoft",
					0, "11/11/2010", "Egypt",
					"student");
			u6._save();

			User fatma = new User("testfatma.meawad@guc.edu.eg", "fmeawad",
					"1234", "Fatma", "Meawad", "What is our company's name?", "coolsoft",
					0, "11/11/2010",
					"Egypt", "Professor").save();

			User slim = new User("testslim.abdennadher@guc.edu.eg", "mezo",
					"1234", "Slim", "Abd EL Nadder", "What is our company's name?", "coolsoft",
					0, "11/11/2010",
					"Egypt", "Professor").save();

			User ashraf = new User("Ashraf@guc.edu.eg", "ElKbeer", "1234",
					"Ashraf", "Mansoor", "What is our company's name?", "coolsoft",
					0, "11/11/2010", "Egypt",
					"student").save();

			Organization guc = new Organization("GUC", ashraf, 1, true,
					"The German University in Cairo").save();
			
			//faruki
			MainEntity defaultEntity = new MainEntity("Default", "", guc, false);
			defaultEntity.save();
			//faruki

			Organization egypt = new Organization("Egypt", admin, 2, false,
					"The National Egyptian Organization").save();
			
			//faruki
			MainEntity defaultEntity1 = new MainEntity("Default", "", egypt, false);
			defaultEntity.save();
			//faruki

			Organization gov = new Organization("الحكومة المصرية", sharaf, 0,
					true, "").save();
			
			//faruki
			MainEntity defaultEntity2 = new MainEntity("Default", "", gov, false);
			defaultEntity.save();
			//faruki

			Tag tagEducation = new Tag("Education", guc, slim).save();
			Tag tagGuc = new Tag("GUC", guc, ashraf).save();

			Tag tagEgypt = new Tag("Egypt", gov, sharaf).save();
			Tag tagGov = new Tag("Government", gov, sharaf).save();
			
			Tag tag1 = new Tag("Cairo", guc, ashraf).save();
			Tag tag2 = new Tag("Germany", guc, ashraf).save();
			Tag tag3 = new Tag("DAAD", guc, ashraf).save();
			Tag tag4 = new Tag("University", guc, ashraf).save();
			Tag tag5 = new Tag("Egypt", guc, ashraf).save();

			
			guc.relatedTags.add(tagGuc);
			guc.relatedTags.add(tagEducation);
			guc.relatedTags.add(tag5);
			guc.relatedTags.add(tag2);
			guc.relatedTags.add(tag3);
			guc.relatedTags.add(tag4);
			guc.relatedTags.add(tag1);
			guc.save();

			gov.relatedTags.add(tagEgypt);
			gov.relatedTags.add(tagGov);
			gov.save();

			MainEntity gucMet = new MainEntity("MET",
					"Media Engineering and technology", guc, true).save();
			gucMet.tagList.add(tagEducation);
			gucMet._save();
			
			
			MainEntity gucCs = new MainEntity("CS",
					"Computer Science and Engineering", gucMet, guc, false)
					.save();
			gucCs.tagList.add(tagGuc);
			gucCs._save();

			MainEntity govHead = new MainEntity("الوزارة", "راسه مجلس الوزراء",
					gov, true).save();
			gucMet.tagList.add(tagEducation);
			gucMet._save();

			MainEntity elAhly = new MainEntity("El-Ahly",
					"Al-Ahly Club Lovers", egypt, true).save();

			Role organizer = Roles.getRoleByName("organizer");
			Role ideadeveloper = Roles.getRoleByName("idea developer");
			Role OrganizationLead = Roles.getRoleByName("organizationLead");

			UserRoleInOrganizations.addEnrolledUser(ashraf, guc,
					OrganizationLead);

			UserRoleInOrganizations.addEnrolledUser(slim, guc, organizer,
					gucMet.id, "entity");

			UserRoleInOrganizations.addEnrolledUser(fatma, guc, organizer,
					gucCs.id, "entity");

			UserRoleInOrganizations.addEnrolledUser(sharaf, gov,
					OrganizationLead);

			UserRoleInOrganizations.addEnrolledUser(gamal, gov, organizer,
					govHead.id, "entity");

			Topic gucMetStudentUnion = new Topic("Student union",
					"Suggestions", 2, ashraf, gucMet, true).save();
			gucMetStudentUnion._save();
			gucMetStudentUnion.tags.add(tagGuc);
			gucMetStudentUnion._save();
			
			Topic gucDraft = new Topic("Draft",
					"Suggestions", 2, ashraf, gucMet, false, true).save();
			gucMetStudentUnion._save();

			UserRoleInOrganizations.addEnrolledUser(khayat, guc, ideadeveloper,
					gucMetStudentUnion.id, "topic");

			
			UserRoleInOrganizations.addEnrolledUser(gom3a, guc, ideadeveloper,
					gucMetStudentUnion.id, "topic");

			UserRoleInOrganizations.addEnrolledUser(mai, guc, ideadeveloper,
					gucMetStudentUnion.id, "topic");

			UserRoleInOrganizations.addEnrolledUser(u5, guc, ideadeveloper,
					gucMetStudentUnion.id, "topic");

			Topic govImp = new Topic("التعديلات الدستورية", "اقتراحات", 1,
					sharaf, govHead, false).save();
			Topic govImp2 = new Topic("ثوره 25 يناير", "اقتراحات", 1, sharaf,
					govHead, true).save();
			govHead.topicList.add(govImp);
			govHead.topicList.add(govImp2);
			govHead._save();
			Topic egyahlymatch = new Topic("Ahly vs Zamalek",
					"for football lovers", 2, admin, elAhly, false).save();
			elAhly.topicList.add(egyahlymatch);
			elAhly._save();
			Topic innovation = new Topic("Create. Innovate",
					"how to encourage inventors and enhance creativity", 1,
					sharaf, govHead, true).save();
			govHead.topicList.add(innovation);
			govHead._save();
			Topic matchChaos = new Topic(
					"Chaos in egyptian football matches",
					"try to avoid chaos and fights during matches due to different points of view",
					2, admin, elAhly, true).save();
			elAhly.topicList.add(matchChaos);
			elAhly._save();

			Idea i1 = new Idea("Law Improvement",
					"Adding restrcitions on businessmen", barnasa, govImp, true);
			i1.save();
			barnasa.save();

			Idea i2 = new Idea("Human Rights",
					"All Humans Are Equal No Matter What", gom3a, govImp);
			i2.save();
			gom3a.save();
			Idea i3 = new Idea(
					"C7 Labs",
					"These kinds of labs should be working 24/7 so i suggest to have maintanence man from the IT for every lab it won't be costy and it will save a lot of time ",
					hadi, gucMetStudentUnion);
			i3.save();
			hadi.save();
			Idea i4 = new Idea(
					"Chaos",
					"That should never happen in this country and safety must be maintained",
					hadi, matchChaos);
			i4.save();
			hadi.save();
			Idea i5 = new Idea(
					"CA Course",
					"this course must be more organized and Dr. Amr T. should be more respectful to our mentallity as adults",
					hadi, gucMetStudentUnion);
			i5.save();
			hadi.save();
			Idea i6 = new Idea("SU is Important",
					"We Can Make our University better", hadi,
					gucMetStudentUnion);
			i6.save();
			hadi.save();

			Idea i7 = new Idea("Blank", "Blank", sharaf, govImp);
			i7.save();
			sharaf.save();

			Idea i8 = new Idea("Blank 2 ", "Blank 2 ", sharaf, govImp);
			i8.save();
			sharaf.save();

			Idea i9 = new Idea("Blank 3 ", "Blank 3 ", sharaf, govImp2);
			i9.save();
			sharaf.save();

			Idea i10 = new Idea("Blank 4", "Blank 4", sharaf, govImp2);
			i10.save();
			sharaf.save();

			UserRoleInOrganizations.addEnrolledUser(hadi, egypt, ideadeveloper,
					matchChaos.id, "topic");
			
			UserRoleInOrganizations.addEnrolledUser(hadi, guc, ideadeveloper,
					gucMetStudentUnion.id, "topic");
			
			Plan p1 = new Plan("S.U. heads", ashraf, new Date(111, 03, 20),
					new Date(111, 07, 30), "Plan for SU heads elections",
					gucMetStudentUnion, "summer break").save();
			gucMetStudentUnion.plan = p1;
			gucMetStudentUnion.openToEdit = false;
			gucMetStudentUnion.save();
			ashraf.planscreated.add(p1);
			ashraf.save();
			
			i6.plan = p1;
			i6._save();
			p1.addIdea(i6);
			p1.save();

			Item item1 = new Item(
					new Date(111, 05, 01),
					new Date(111, 05, 20),
					"One of the members who is not runing for elections should organize a debate between the candidates",
					p1, "Candidates Debate");

			Item item2 = new Item(
					new Date(111, 05, 04),
					new Date(111, 05, 14),
					"One of the SU members should reserve a room for the debate",
					p1, "Room reservation");

			Item item3 = new Item(
					new Date(111, 04, 01),
					new Date(111, 05, 28),
					"One of the members should set up an electronic election process",
					p1, "Election Process Setup");

			item1.save();
			item2.save();
			item3.save();

			p1.items.add(item1);
			p1.items.add(item2);
			p1.items.add(item3);
			p1.save();

			khayat.itemsAssigned.add(item1);
			khayat.itemsAssigned.add(item2);
			khayat.save();

			gom3a.itemsAssigned.add(item1);
			gom3a.save();

			item1.assignees.add(khayat);
			item1.assignees.add(gom3a);
			item1.save();
			item2.assignees.add(khayat);
			item2.save();

			new Comment("here i accept", i1, gom3a).save();
			new Comment("i love SE course", p1, hadi).save();

		}
	}
}
