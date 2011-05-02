/**
 * 
 */
package controllers;

import java.util.*;
import java.lang.reflect.*;
import java.lang.annotation.*;

import javax.persistence.Id;

import play.*;
import play.data.binding.*;
import play.mvc.*;
import play.utils.Java;
import play.db.Model;
import play.data.validation.*;
import play.exceptions.*;
import play.i18n.*;
import models.*;

/**
 * @author ${Ahmed El-Hadi}
 * 
 */
public class Ideas extends CRUD {

	/*
	 * @author Abdalrahman Ali
	 * 
	 * this method saves an idea as a draft
	 * 
	 * @param title the title of the idea
	 * 
	 * @param body the body of the idea
	 * 
	 * @param topic the topic that the idea belongs to
	 * 
	 * @param user the user saving this idea
	 * 
	 * @return void
	 */

	public static void saveDraft(String title, String body, Long topicId) {
		User user = Security.getConnected();
		Topic topic = Topic.findById(topicId);
		Idea idea = new Idea(title, body, user, topic, true).save();
		//String message = "you have saved the idea with tile"+ idea.title + "successfully";
		//redirect(request.controller + ".show", (idea).getId(),message);
	}
	/*
	 * @author Abdalrahman Ali
	 * 
	 * this method posts an idea that was saved as a draft
	 * 
	 * @param idea the saved idea
	 * 
	 * @return void
	 */

	public static void postDraft(long ideaId) {
		Idea idea = Idea.findById(ideaId);
		idea.isDraft = false;
		idea.save();
	}

	/*
	 * @author Abdalrahman Ali
	 * 
	 * this method returns all the ideas the user saved as draft in order to
	 * enable the user to choose one of them and post it.
	 * 
	 * @param user the user who saved the ideas
	 * 
	 * @return ArrayList<Idea> all the draft ideas saved by the user
	 */

	public static ArrayList<Idea> getDrafts(User user) {
		ArrayList<Idea> drafts = new ArrayList<Idea>();

		for (Idea idea : user.ideasCreated)
			if (idea.isDraft)
				drafts.add(idea);

		return drafts;
	}

	/**
	 * Overriding the CRUD method blank.
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S10
	 * 
	 * @param topicId
	 *            : id of the topic the idea belongs to
	 * 
	 * @param userId
	 *            : id of the user who wants to post an idea
	 * 
	 * @description This method renders the form for creating an idea
	 * 
	 * @throws Exception
	 * 
	 */
	public static void blank(long topicId, long userId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Topic topic = Topic.findById(topicId);
		User user = User.findById(userId);
		System.out.println("blank() entered entity " + topicId + " and user "
				+ userId);
		try {
			render(type, topic, user);

		} catch (TemplateNotFoundException e) {
			render("CRUD/blank.html", type);
		}

	}

	/**
	 * Overriding the CRUD method create.
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S10
	 * 
	 * 
	 * @description This method checks for the Validation of the information
	 *              inserted in the Add form of an Idea and if they are valid
	 *              the Idea is created and saved.
	 * @throws Exception
	 * 
	 */

	public static void create() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Model object = (Model) constructor.newInstance();
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		Topic topic = Topic.findById((long) 1);
		User author = User.findById((long) 1);
		Idea i = (Idea) object;
		i.belongsToTopic = topic;
		i.author = author;
		i.privacyLevel = topic.privacyLevel;
		// ArrayList<Comment> ideaComments = (ArrayList<Comment>)
		// i.commentsList;
		// ArrayList<Tag> ideaTags = (ArrayList<Tag>) i.tagsList;
		String message = "";
		if (i.belongsToTopic == null) {
			message = "An Idea must belong to a Topic";
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						type, message);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", type, message);
			}
		}

		if (validation.hasErrors()) {
			if (i.title.equals("")) {
				message = "An Idea must have a title";
			} else if (i.description.equals("")) {
				message = "An Idea must have a description";

			}

			try {
				System.out.println("foo2 Render");
				render(request.controller.replace(".", "/") + "/blank.html",
						type, i.title, i.belongsToTopic, i.description,
						i.commentsList, /* i.tagsList, */message);
				System.out.println("rendered 5alas");
			} catch (TemplateNotFoundException e) {
				System.out.println("fel catch templatenotfound");
				render("CRUD/blank.html", type);
			}
		}

		object._save();
		System.out.println("3ada el save");
		String anothermessage = "you have created a new idea with title "
				+ i.title + " and with description " + i.description;
		flash.success(Messages.get("crud.created", type.modelName,
				((Idea) object).getId()));
		System.out.println("foo2 el if");
		if (params.get("_save") != null) {
			System.out.println("gowa el if");
			System.out
					.println("/ideas/view?ideasid=" + ((Idea) object).getId());

			redirect("/ideas/view?ideaId=" + ((Idea) object).getId());
			if (params.get("_saveAndAddAnother") != null) {
				redirect(request.controller + ".blank", anothermessage);
			}
			redirect(request.controller + ".show", ((Idea) object).getId(),
					anothermessage);

		}
	}

	/**
	 * Overriding the CRUD method show.
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S10
	 * 
	 * @param Ideaid
	 *            : id of the idea to be show
	 * 
	 * @description This method renders the form for editing and viewing an idea
	 * 
	 * @throws Exception
	 * 
	 */
	public static void show(long ideaId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(ideaId);
		notFoundIfNull(object);
		Idea i = (Idea) object;
		// List<Tag> tags = i.tagsList;
		User author = i.author;
		List<Comment> comments = i.commentsList;
		Plan plan = i.plan;
		Topic topic = i.belongsToTopic;
		// boolean openToEdit = i.openToEdit;
		int privacyLevel = i.privacyLevel;
		String deletemessage = "Are you Sure you want to delete the task ?!";
		// boolean deletable = i.isDeletable();

		try {
			System.out.println("show() done, about to render");
			render(type, object, /* tags, */author, comments, topic, plan,
			/* openToEdit, */privacyLevel, deletemessage, /* deletable, */ideaId);
		} catch (TemplateNotFoundException e) {
			render("CRUD/show.html", type, object);
		}
	}

	/**
	 * Overriding the CRUD method edit.
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S10
	 * 
	 * @param Ideaid
	 *            : id of the idea to be show
	 * 
	 * @description This method is resposible for editing an idea
	 * 
	 * @throws Exception
	 * 
	 */

	public static void edit(long ideaId) {

		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(ideaId);
		notFoundIfNull(object);
		Idea i = (Idea) object;
		flash.success(Messages.get("crud.edit", type.modelName,
				((Idea) object).getId()));
		redirect("/ideas/show?ideaId=" + ((Idea) object).getId(), i);
	}

	/**
	 * Overriding the CRUD method view.
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S10
	 * 
	 * @param Ideaid
	 *            : id of the idea to be show
	 * 
	 * @description This method is resposible for viewing an idea
	 * 
	 * @throws Exception
	 * 
	 */
	public static void view(long ideaId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(ideaId);
		notFoundIfNull(object);
		Idea i = (Idea) object;
		// List<Tag> tags = i.tagsList;
		User author = i.author;
		List<Comment> comments = i.commentsList;
		Plan plan = i.plan;
		Topic topic = i.belongsToTopic;
		// boolean openToEdit = i.openToEdit;
		int privacyLevel = i.privacyLevel;
		String deletemessage = "Are you Sure you want to delete the task ?!";
		// boolean deletable = i.isDeletable();

		try {
			render(type, object, /* tags, */author, comments, topic, plan,
			/* openToEdit, */privacyLevel, deletemessage, /* deletable, */ideaId);
		} catch (TemplateNotFoundException e) {
			render("/ideas/view.html", type, object, /* tags, */comments, topic,
					plan, /* openToEdit, */privacyLevel, deletemessage, /*
																	 * deletable,
																	 */ideaId);
		}
	}

	/**
	 * Overriding the CRUD method list.
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S10
	 * 
	 * @param page
	 *            : page of the list we are in
	 * 
	 * @param search
	 *            : search string
	 * 
	 * @param searchFields
	 *            : the fields we want to search
	 * 
	 * @param orderBy
	 *            : criteria to order list by
	 * 
	 * @param order
	 *            : the order of the list
	 * 
	 * @description This method renders the list of topics, with search and sort
	 *              options
	 * 
	 * @throws Exception
	 * 
	 */
	public static void list(int page, String search, String searchFields,
			String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		List<Model> objects = type.findPage(page, search, searchFields,
				orderBy, order, (String) request.args.get("where"));
		Long count = type.count(search, searchFields,
				(String) request.args.get("where"));
		Long totalCount = type.count(null, null,
				(String) request.args.get("where"));
		try {
			render(type, objects, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			render("CRUD/list.html", type, objects, count, totalCount, page,
					orderBy, order);
		}
	}

	/**
	 * Overriding the CRUD method save.
	 * 
	 * @author ${Ahmed EL-Hadi}
	 * 
	 * @story C3S10
	 * 
	 * @param ideaId
	 *            : id of the idea we are inside
	 * 
	 * @description This method renders the form for editing an Idea and saving
	 *              it
	 * 
	 * @throws Exception
	 * 
	 */
	public static void save(String ideaid) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		System.out.println(ideaid);
		Model object = Idea.findById(Long.parseLong(ideaid));
		notFoundIfNull(object);
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		Idea i = (Idea) object;
		Topic topic = Topic.findById((long) 1); // temporary; for testing
		String message = ""; // purposes
		i.belongsToTopic = topic;
		// User myUser = Security.getConnected();
		User myUser = User.findById((long) 1);// temporary; for testing purposes
		i.author = myUser;
		// ArrayList<Tag> topicTags = (ArrayList<Tag>) tmp.tags;
		// Organization topicOrganization = topic.organization;
		if (validation.hasErrors()) {
			if (i.description.equals("")) {
				message = "A Topic must have a description";

			}

			else if (i.privacyLevel < 0 || i.privacyLevel > 10) {
				message = "The privary level must be within 0 and 10";

			}

			/*
			 * else if( !Users.isPermitted(myUser, "edit topics",
			 * topicEntity.getId(), "entity")) { message =
			 * "Sorry but you are not allowed to edit topics in this entity"; }
			 */
			try {
				render(request.controller.replace(".", "/") + "/show.html",
						topic, type, i.title, i.belongsToTopic, i.description,
						i.tagsList, message, object, ideaid);
			} catch (TemplateNotFoundException e) {
				render("CRUD/show.html", type);
			}
		}

		System.out.println("about to save() topic");
		object._save();
		Calendar cal = new GregorianCalendar();
		// Logs.addLog( myUser, "add", "Task", tmp.id, tmp.entity.organization,
		// cal.getTime() );
		// String message3 = myUser.username + " has editted the topic " +
		/*
		 * List users = Users.getEntityOrganizers(tmp.entity);
		 * users.add(tmp.entity.organization.creator);
		 * Notifications.sendNotification(users, tmp.id, "Topic", "User " +
		 * myUser.firstName + " has edited topic  " + tmp.title);
		 */
		System.out.println("save() done, not redirected yet");

		flash.success(Messages.get("crud.saved", type.modelName,
				((Idea) object).getId()));
		if (params.get("_save") != null) {
			redirect("/ideas/view?ideaId=" + ((Idea) object).getId());
			System.out.println("save() done, redirected to ideas/view?topicid");
			// redirect( "/storys/liststoriesinproject?projectId=" +
			// tmp.taskStory.componentID.project.id + "&storyId=" +
			// tmp.taskStory.id );
		}
		redirect(request.controller + ".show", ((Idea) object).getId());
		System.out.println("save() done, redirected to default show.html");
	}

	/**
	 * This method first checks if the user is allowed to tag the idea, searches
	 * for the tag in the global list of tags, if found => check if it already
	 * the idea had the same tag already or add the new one to the list if not
	 * => create a new tag, save it to db, add it to the list send notifications
	 * to followers of the tag and the creator of the idea
	 * 
	 * @author Mostafa Yasser El Monayer
	 * 
	 * @story C3S3, C3S11
	 * 
	 * @param ideaID
	 *            : the idea that is being tagged
	 * 
	 * @param tag
	 *            : the tag that is being added
	 * 
	 */

	public static void tagIdea(long ideaID, String tag) {

		boolean tagAlreadyExists = false;
		boolean userNotAllowed = false;
		boolean tagExists = false;
		List<Tag> listOfTags = Tag.findAll();
		User user = Security.getConnected();
		Idea idea = Idea.findById(ideaID);

		if (!idea.belongsToTopic.organizers.contains(user)
				|| !user.equals(idea.author)) {
			// user not allowed
			userNotAllowed = true;
		} else {
			for (int i = 0; i < listOfTags.size(); i++) {
				if (listOfTags.get(i).getName().equalsIgnoreCase(tag)) {
					if (!idea.tagsList.contains(listOfTags.get(i))) {
						idea.tagsList.add(listOfTags.get(i));
						Notifications.sendNotification(
								listOfTags.get(i).followers,
								idea.tagsList.get(i).getId(), "tag",
								"This idea has been tagged as " + tag);
					} else {
						// tag already exists error message
						tagAlreadyExists = true;
					}
					tagExists = true;
				}
			}

			if (!tagExists) {
				Tag temp = new Tag(tag);
				temp.save();
				idea.tagsList.add(temp);
			}

			if (!tagAlreadyExists) {
				if (user.equals(idea.author)) {
					List<User> list1 = new ArrayList<User>();
					list1.add(idea.author);
					Notifications.sendNotification(list1, ideaID, "idea",
							"This idea has been tagged as " + tag);
				}
			}
		}
		render(tagAlreadyExists, tagExists, userNotAllowed, idea.tagsList);
	}
	/**
	 * @author ${Ibrahim Safwat}
	 * 
	 * @param userToCheck
	 *            User to be checked if he/she is in the list usersRated
	 * @return
	 */
	public boolean checkRated(User userToCheck,long ideaID) {
		Idea idea = Idea.findById(ideaID);
		for (int i = 0; i < idea.usersRated.size(); i++) {
			if (userToCheck == idea.usersRated.get(i))
				return true;
		}
		return false;
	}

	/**
	 * @author ${Ibrahim Safwat}
	 * 
	 * @param rate
	 *            rating taken from the user
	 * @param ideaID
	 *            idea that the user wants to rate
	 */
	public void rate(int rating, int ideaID) {
		
		User user = Security.getConnected();
		if (!checkRated(user, ideaID)) {
			Idea idea = Idea.findById(ideaID);
			int oldRating = idea.rating;
			int newRating = (oldRating + rating) / 2;
			render(newRating);
		}
	}

}
