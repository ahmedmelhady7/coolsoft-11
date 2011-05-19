package controllers;

import java.util.*;
import java.lang.reflect.*;
import java.lang.annotation.*;

import javax.persistence.Id;

import controllers.CRUD.ObjectType;

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
@With(Secure.class)
public class Ideas extends CRUD {

	/**
	 * @author Abdalrahman Ali
	 * 
	 * @story C3S13
	 * 
	 *        this method saves an idea as a draft for the first time
	 * 
	 * @param title
	 *            the title of the idea
	 * 
	 * @param body
	 *            the body of the idea
	 * 
	 * @param topicId
	 *            the topic that the idea belongs to
	 */

	public static void createDraft(String title, String description,
			long topicId) {
		User user = Security.getConnected();
		Topic topic = Topic.findById(topicId);
		Idea idea = new Idea(title, description, user, topic, true).save();
		// String message = "you have saved the idea with title successfully";
		// redirect(request.controller + ".show", idea.getId(),message);
	}

	/**
	 * @author Abdalrahman Ali
	 * 
	 * @story C3S13
	 * 
	 *        this method allows the user to create/save an idea
	 * 
	 * @param topicId
	 *            the topic that the idea belongs to
	 */

	public static void createIdea(long topicId) {
		User user = Security.getConnected();
		Topic topic = Topic.findById(topicId);
		render(topic, user);
	}

	public static void doCreateIdea(long topicId, String title, String body) {
		System.out.println("De Te Kill " + topicId);
		User user = Security.getConnected();
		Topic topic = Topic.findById(topicId);
		System.out.println(topic.title + " b " + user.email);
		Idea idea = new Idea(title, body, user, topic);
		idea.isDraft = false;
		if (idea != null)
			System.out.println("mesh null");
		else
			System.out.println("NUll ya fale7");
	}
	
	public static void discardIdea(long ideaId)
	{
		Idea idea = Idea.findById(ideaId);
		idea.delete();
	}

	/**
	 * @author Abdalrahman Ali
	 * 
	 * @story C3S13
	 * 
	 *        this method posts an idea that was saved as a draft
	 * 
	 * @param ideaId
	 *            the saved idea
	 * 
	 * @param title
	 *            the idea's title
	 * 
	 * @param description
	 *            the idea's description
	 */

	public static void postDraft(long ideaId, String title, String description) {

		Idea idea = Idea.findById(ideaId);
		idea.isDraft = false;
		idea.title = title;
		idea.description = description;
		idea.save();
	}

	/**
	 * @author Abdalrahman Ali
	 * 
	 * @story C3S13
	 * 
	 *        this method saves a change in a draft idea
	 * 
	 * @param ideaId
	 *            the saved idea
	 * 
	 * @param title
	 *            the idea's title
	 * 
	 * @param description
	 *            the idea's description
	 */

	public static void saveDraft(long ideaId, String title, String description) {
		Idea idea = Idea.findById(ideaId);
		idea.title = title;
		idea.description = description;
		idea.save();
		// flash.success("aho");
		// redirect("/ideas/editdraft?ideaId=" + ideaId);
	}

	/**
	 * @author Abdalrahman Ali
	 * 
	 * @story C3S13
	 * 
	 *        this method returns all the ideas the user saved as draft in order
	 *        to enable the user to choose one of them and post it.
	 */

	public static void getDrafts() {

		User user = Security.getConnected();

		List<Idea> drafts = new ArrayList<Idea>();

		for (Idea idea : user.ideasCreated)
			if (idea.isDraft)
				drafts.add(idea);

		render(drafts, user);
	}

	/**
	 * @author Abdalrahman Ali
	 * 
	 * @story C3S13
	 * 
	 *        this method just directs the user to a page where he can handle
	 *        his drafts
	 */

	public static void editDraft(long ideaId) {
		Idea idea = Idea.findById(ideaId);
		User user = Security.getConnected();
		// flash.success("aho");
		render(idea, user);
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
	public static void blank(long topicId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Topic topic = Topic.findById(topicId);
		User user = Security.getConnected();
		try {
			render(type, topic, user, topicId);

		} catch (TemplateNotFoundException e) {
			render("CRUD/blank.html", type, topicId);
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
	 *              the Idea is created and saved and the community contribution
	 *              counter of the author is incremented.
	 * @throws Exception
	 * 
	 */

	public static void create(long topicId) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Model object = (Model) constructor.newInstance();
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		Topic topic = Topic.findById(topicId);// topicId);
		User author = Security.getConnected();
		Idea idea = (Idea) object;
		idea.belongsToTopic = topic;
		idea.author = author;
		// i.privacyLevel = topic.privacyLevel;
		// ArrayList<Comment> ideaComments = (ArrayList<Comment>)
		// i.commentsList;
		// ArrayList<Tag> ideaTags = (ArrayList<Tag>) i.tagsList;
		String message = "";
		if (idea.belongsToTopic == null) {
			message = "An Idea must belong to a Topic";
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						type, message, topicId);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", type, message, topicId);
			}
		}

		if (validation.hasErrors()) {
			if (idea.title.equals("")) {
				message = "An Idea must have a title";
			} else if (idea.description.equals("")) {
				message = "An Idea must have a description";

			}

			try {
				System.out.println("foo2 Render");
				render(request.controller.replace(".", "/") + "/blank.html",
						type, idea.title, idea.belongsToTopic,
						idea.description, idea.commentsList, /* i.tagsList, */
						message, topicId);
				System.out.println("rendered 5alas");
			} catch (TemplateNotFoundException e) {
				System.out.println("fel catch templatenotfound");
				render("CRUD/blank.html", type, topicId);
			}
		}

		object._save();
		author.communityContributionCounter++;
		author.save();
		System.out.println("3ada el save");
		String anothermessage = "you have created a new idea with title "
				+ idea.title + " and with description " + idea.description;
		flash.success(Messages.get("crud.created", type.modelName,
				((Idea) object).getId()));
		System.out.println("foo2 el if");
		if (params.get("_save") != null) {
			System.out.println("gowa el if");
			System.out
					.println("/ideas/view?ideasid=" + ((Idea) object).getId());

			redirect("/ideas/show?ideaId=" + ((Idea) object).getId());
			if (params.get("_saveAndAddAnother") != null) {
				redirect(request.controller + ".blank", anothermessage);
			}
			redirect(request.controller + ".show", ((Idea) object).getId(),
					anothermessage);

		}
	}

	/**
	 * Hide an idea
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S8
	 * 
	 * @param id
	 *            : the id of the idea to be hidden
	 */
	public static void hide(long id) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		Idea idea = (Idea) object;
		Topic topic = idea.belongsToTopic;
		User user = Security.getConnected();

		try {
			// Logs.addLog( myUser, "delete", "Task", temporaryTopic.id,
			// temporaryTopic.taskStory.componentID.project, cal.getTime() );
			String message = user.username + " has hidden the topic "
					+ idea.title;
			List<User> users = Users.getEntityOrganizers(topic.entity);
			// for (int i = 0; i < users.size(); i++)
			Notifications.sendNotification(topic.creator.id/*
															 * users.get(i).id
															 */, idea.id,
					"Idea", message);
			// for (int i = 0; i < topic.followers.size(); i++)
			// Notifications.sendNotification(topic.followers.get(i).getId(),
			// topic.getId(), "entity", message);
			idea.hidden = true;
			idea.save();
			System.out.println("hidden");
			System.out.println("leaving try");

		} catch (Exception e) {
			System.out.println("entered catch");
			flash.error(Messages.get("crud.delete.error", type.modelName));
			redirect(request.controller + ".show", object._key());
		}
		flash.success(Messages.get("crud.deleted", type.modelName));
		System.out.println("flash.success");
		// redirect(request.controller + ".list");
		redirect("topics.show", topic.id);
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
	 * 
	 */
	public static void show(long ideaId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(ideaId);
		notFoundIfNull(object);
		Idea idea = (Idea) object;
		int rate = idea.rating;
		// List<Tag> tags = i.tagsList;
		User user = Security.getConnected();
		List<Comment> comments = idea.commentsList;
		Plan plan = idea.plan;
		Topic topic = idea.belongsToTopic;
		long topicId = topic.id;
		// boolean openToEdit = i.openToEdit;
		String deletemessage = "Are you Sure you want to delete the task ?!";
		// boolean deletable = i.isDeletable();
		boolean canDelete = Users.isPermitted(user, "hide and delete an idea", topicId, "topic");
		boolean alreadyReported = false;
		System.out.println("false alreadyreoprted");
		for (int i = 0; i < idea.reporters.size()
				|| i < user.ideasReported.size(); i++) {
			System.out.println("gowa el loop");
			if (idea.reporters.size() > 0
					&& (user.toString()
							.equals(idea.reporters.get(i).toString()) || idea
							.toString()
							.equals(idea.reporters.get(i).toString()))) {
				alreadyReported = true;
				System.out
						.println("3mlha w 5ala el already reported b true****************************************************************************************************************************************************************");
			} else
				alreadyReported = false;
		}
		try {
			System.out.println("show() done, about to render");
			// System.out.println("x is " + x);
			boolean permittedToTagIdea = Users.isPermitted(user, "tag his/her ideas", ideaId, "idea") || Users.isPermitted(user, "tag ideas in my organization", ideaId, "idea");
			render(type, object, /* tags, */user, canDelete, comments, topic,
					plan, permittedToTagIdea,
					/* openToEdit, */topicId, alreadyReported, deletemessage, /*
																			 * deletable
																			 * ,
																			 */
					ideaId, rate, idea);
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
	 * 
	 */
	public static void view(long ideaId) {
		System.out.println("hadi foo222");

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
		String deletemessage = "Are you Sure you want to delete the task ?!";
		// boolean deletable = i.isDeletable();
		System.out.println("haadi");
		try {
			System.out.println("try");
			render(type, object, /* tags, */author, comments, topic, plan,
			/* openToEdit, */deletemessage, /* deletable, */ideaId);
		} catch (TemplateNotFoundException e) {
			render("/ideas/view.html", type, object, /* tags, */comments, topic,
					plan, /* openToEdit, */deletemessage, /*
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
				orderBy, order, (String) request.args.get("where "));

		if (objects != null) {
			for (int i = 0; i < objects.size(); i++) {
				Idea o = (Idea) objects.get(i);
				if (o.isDraft)
					objects.remove(i);
			}
		}

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
	 * Overriding the CRUD method delete.
	 * 
	 * @author ${Ahmed EL-Hadi}
	 * 
	 * 
	 * @param ideaId
	 *            : id of the idea to be deleted
	 * 
	 * @description This method deletes and idea from the database
	 * 
	 * 
	 */
	public static void delete(long ideaId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(ideaId);
		notFoundIfNull(object);
		try {
			object._delete();
		} catch (Exception e) {
			flash.error(Messages.get("crud.delete.error", type.modelName));
			redirect(request.controller + ".show", object._key());
		}
		flash.success(Messages.get("crud.deleted", type.modelName));
		redirect(request.controller + ".list");
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

	public static void tagIdea(long ideaId, String tag) {

		boolean tagAlreadyExists = false;
		boolean userNotAllowed = false;
		boolean tagExists = false;
		List<Tag> listOfTags = new ArrayList<Tag>();
		List<Tag> globalListOfTags = new ArrayList<Tag>();
		globalListOfTags = Tag.findAll();
		System.out.println("global = " + globalListOfTags.size());
		User user = (User) Security.getConnected();
		Idea idea = (Idea) Idea.findById(ideaId);
		Topic topic = idea.belongsToTopic;
		MainEntity entity = topic.entity;

		if (!tag.equals("@@")) {

			if (!Users.isPermitted(user, "tag ideas in my organization",
					entity.id, "entity") || !Users.isPermitted(user, "tag his/her ideas",
							topic.id, "topic")) {
				// user not allowed
				System.out.println("user not allowed");
				userNotAllowed = true;
			} else {
				for (int i = 0; i < globalListOfTags.size(); i++) {
					if (globalListOfTags.get(i).createdInOrganization.privacyLevel == 2
							|| idea.belongsToTopic.entity.organization
									.equals(globalListOfTags.get(i).createdInOrganization)) {
						listOfTags.add(globalListOfTags.get(i));
					}
				}
				for (int i = 0; i < listOfTags.size(); i++) {
					if (listOfTags.get(i).getName().equalsIgnoreCase(tag)) {
						if (!idea.tagsList.contains(listOfTags.get(i))) {
							idea.tagsList.add(listOfTags.get(i));
							listOfTags.get(i).taggedIdeas.add(idea);
							listOfTags.get(i).save();
							System.out.println("existing tag added");
							for (int j = 0; j < listOfTags.get(i).followers
									.size(); j++) {
								Notifications.sendNotification(
										listOfTags.get(i).followers.get(j).id,
										idea.tagsList.get(i).getId(), "tag",
										"This idea has been tagged as " + tag);
							}
						} else {
							// tag already exists error message
							System.out.println("tag already exists");
							tagAlreadyExists = true;
						}
						tagExists = true;
					}
				}

				if (!tagExists) {
					Tag temp = new Tag(tag,
							idea.belongsToTopic.entity.organization, user);
					idea.tagsList.add(temp);
					temp.taggedIdeas.add(idea);
					temp.save();	
					System.out.println("new tag created and added");
				}

				if (!tagAlreadyExists) {
					Notifications.sendNotification(idea.author.id, ideaId,
							"idea", "This idea has been tagged as " + tag);
				}
			}

		}
		idea.save();
		List<Tag> tags = idea.tagsList;
		render(tagAlreadyExists, userNotAllowed, tags, ideaId);
	}

	/**
	 * @author ${Ibrahim Safwat}
	 * 
	 * @param rating
	 *            rating taken from the user
	 * @param ideaID
	 *            idea that the user wants to rate
	 * @description rates an idea if the user is an organizer
	 */

	public static void rate(int rating, long ideaID) {
		Idea i = Idea.findById(ideaID);
		User user = Security.getConnected();
		List organizers = i.belongsToTopic.getOrganizer();
		if (organizers.contains(user)) {
			Idea idea = Idea.findById(ideaID);
			int oldRating = idea.rating;
			int newRating = (oldRating + rating) / 2;
		}
	}

	/**
	 * @author ${Ibrahim safwat}
	 * 
	 * @param userName
	 *            User that wants to share the idea
	 * @param ideaID
	 *            ID of the idea to be shared
	 * @description shares a given idea with the given user
	 */
	public static void shareIdea(String userName, long ideaId) {

		User U = User.find("byUsername", userName).first();
		String type = "Idea";
		User user = Security.getConnected();
		String desc = user.firstName + " " + user.lastName
				+ " shared an Idea with you" + "\n" + "Copy this link into your address bar to view the shared Idea : http://localhost:9008/ideas/show?ideaId="+ideaId;
		long notId = ideaId;
		long userId = U.id;
		Notifications.sendNotification(userId, notId, type, desc);
		redirect("/ideas/show?ideaId="+ideaId);

	}

	/**
	 * @author ${Ibrahim Safwat}
	 * 
	 * @param priority
	 *            the priority to be set
	 * @param ideaID
	 *            the ID of the idea to prioritize
	 * @description sets the priority if the user is an organizer
	 */
	public static void setPriority(String priority, long ideaID) {
		System.out.println("Dakhalt setPriority");
		Idea i = Idea.findById(ideaID);
		Organization O = i.belongsToTopic.entity.organization;
		User user = Security.getConnected();
		List organizers = Users.searchOrganizer(O);
		if (organizers.contains(user))
			i.priority = priority;
	}

	/**
	 * Overriding the CRUD method save.
	 * 
	 * @author ${Ahmed EL-Hadi}
	 * 
	 * @story C3S10
	 * 
	 * @param entityId
	 *            the id of the entity where the user would like to post
	 * 
	 * @description This method renders the list of ideas the connected user can
	 *              post in
	 * 
	 * @return void
	 */

	public void topicsList(long entityId) {
		User user = Security.getConnected();
		MainEntity m = MainEntity.findById(entityId);
		List<Topic> all = m.topicList;
		Topic temp;
		List<Topic> topics = new ArrayList<Topic>();
		for (int i = 0; i < all.size(); i++) {
			temp = all.get(i);
			if (!Users.isPermitted(user, "can post ideas to a Topic", temp.id,
					"topic")) {
				topics.add(temp);
			}
		}
		render(topics);
	}

	/**
	 * @author Loaay Alkherbawy
	 * @param ideaId
	 *            : the Id of the idea that the user likes
	 * @description: this method sends a notification to the creator telling him
	 *               who liked his idea
	 */

	public static void like(long ideaId) {
		System.out.println("Notification about to be sent");
		Idea idea = Idea.findById(ideaId);
		Notifications.sendNotification(idea.author.id, ideaId, "Idea",
				Security.getConnected().username + " liked your idea "
						+ idea.title);
		System.out.println("Notification sent");
	}

	/**
	 * @author Loaay Alkherbawy
	 * @param ideaId
	 *            : the Id of the idea that the user dislikes
	 * @description: this method sends a notification to the creator telling him
	 *               who disliked his idea
	 */
	
	public static void disLike(long ideaId) {
		Idea idea = Idea.findById(ideaId);
		Notifications.sendNotification(idea.author.id, ideaId, "Idea",
				Security.getConnected().username + " dis liked your idea "
						+ idea.title);
	}
	
	/**
	 * The method renders the ideas the organizer wants to merge
	 * 
	 * @author Mostafa Aboul-Atta
	 * 
	 * @story C3S7
	 * 
	 * @param selectedIdeas
	 * 				: array of the selected ideas ids
	 * 
	 * @param TopicId
	 * 				: the id of the topic t which the ideas belongs
	 */
	
	public static void displayIdeasToMerge(long[] selectedIdeasIds, long topicId) {
		
		Topic targetTopic = Topic.findById(topicId);
		List<Idea> selectedIdeas = new ArrayList<Idea>();
		//List<Idea> allIdeas = targetTopic.getIdeas();
		String selectedIdeasString = "";
		
		selectedIdeas = Ideas.getIdeasFromIds(selectedIdeasIds, topicId);
		
		for(int i = 0; i < selectedIdeasIds.length; i++) {
			selectedIdeasString = selectedIdeasString + selectedIdeasIds[i] + "%";
		}
		long idd = selectedIdeas.get(0).getId();
		System.out.println(selectedIdeasString + " " + idd);
		render(selectedIdeas, topicId, selectedIdeasString, idd);
	}
	
	
	/**
	 * method to merge ideas, overwrites the first idea with the new description and title
	 * then deletes the other ideas, the authors of the deleted ideas are added in the description
	 * as contributors and the merger is added to the description as the merger
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @stroy C3S7
	 * 
	 * @param topicId
	 * 			: the id of the topic to which the ideas belong
	 * @param oldIdeas
	 * 			: ideas to be merged
	 * @param newTitle
	 * 			: title of the resulted idea
	 * @param newDescription
	 * 			: description of the resulted idea
	 * 
	 */
	public static void mergeIdeas(long topicId, String oldIdeas, String newTitle,String newDescription) {
		
		System.out.println("I entered mergeIdeas()");
		
		Topic targetTopic = Topic.findById(topicId);
		User merger = Security.getConnected();
		String[] ideasIdsString = oldIdeas.split("%");
		long[] ideasIds = new long[ideasIdsString.length - 1];
		List<Idea> selectedIdeas = new ArrayList<Idea>();
		Idea ideaToKeep;
		
		for(int i = 0; i < ideasIdsString.length  - 1; i++) {
			ideasIds[i] = Long.parseLong(ideasIdsString[i]);
		}
		
		selectedIdeas = Ideas.getIdeasFromIds(ideasIds, topicId);
		
		newDescription = newDescription + "/n" + "Contributers: ";
		
		for(int i = 1; i < selectedIdeas.size(); i++) {
			String contributor = selectedIdeas.get(i).author.username;
			if(i == selectedIdeas.size() - 1) {
				newDescription.concat(contributor);
			}
			
			else{
				newDescription.concat(contributor + ", ");
			}
		}
		
		newDescription.concat("/n + Merger: " + merger.username);
		
		ideaToKeep = selectedIdeas.get(0);
		
		ideaToKeep.description = newDescription;
		ideaToKeep.title = newTitle;
		ideaToKeep.save();
		
		for(int i = 1; i < selectedIdeas.size(); i++) {
			Idea ideaToDelete = selectedIdeas.get(i);
			ideaToDelete.delete();
		}
		
		targetTopic.save();
		
		render("topics/show?topicId=" + topicId);
	}
	
	/**
	 * takes an array of ideas ids and the topic id they belong to and 
	 * returns a list of the ideas
	 * 
	 * @author Mostafa Aboul-Atta
	 * 
	 * @param selectedIdeasIds
	 * 				: ideas ids that their ideas to be returned
	 * @param topicId
	 * 				: id of the topic to which the ideas belong
	 * @return
	 * 				: list of Idea
	 */
	public static List<Idea> getIdeasFromIds(long[] selectedIdeasIds, long topicId) {
		Topic targetTopic = Topic.findById(topicId);
		List<Idea> selectedIdeas = new ArrayList<Idea>();
		List<Idea> allIdeas = targetTopic.getIdeas();
		//String selectedIdeasString = "";
		
		for(int i = 0; i < allIdeas.size(); i++) {
			
			//checks if all selected ideas are already in the new list
			if(selectedIdeas.size() == selectedIdeasIds.length) {
				break;
			}
			
			Idea currentIdea = allIdeas.get(i);
			Arrays.sort(selectedIdeasIds);
			
			//checks if the current idea is indeed selected
			if(Arrays.binarySearch(selectedIdeasIds, currentIdea.getId()) >= 0) {
				selectedIdeas.add(currentIdea);
			}
			
		}
		
		return selectedIdeas;
	}

}
