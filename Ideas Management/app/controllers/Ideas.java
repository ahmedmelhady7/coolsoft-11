package controllers;

import java.util.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.Collections;

import javax.persistence.Id;

import com.google.gson.JsonObject;

import controllers.CoolCRUD.ObjectType;

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
public class Ideas extends CoolCRUD {

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
	 * 
	 * @return long the method returns the id of the created draft idea
	 */

	public static long createDraft(String title, String description,
			long topicId) {
		User user = Security.getConnected();
		Topic topic = Topic.findById(topicId);
		Idea idea = new Idea(title, description, user, topic, true).save();
		return idea.id;
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
		notFoundIfNull(user);
		notFoundIfNull(topic);
		render(topic, user);
	}

	public static void doCreateIdea(long topicId, String title, String body) {
		User user = Security.getConnected();
		Topic topic = Topic.findById(topicId);
		Idea idea = new Idea(title, body, user, topic);
		idea.isDraft = false;
	}

	public static void discardIdea(long ideaId) {
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
		User user = Security.getConnected();
		Idea idea = Idea.findById(ideaId);
		idea.isDraft = false;
		idea.title = title;
		idea.description = description;
		idea.author.communityContributionCounter++;
		idea.author.save();
		idea.save();
		String logDescription = "<a href=\"/Users/viewProfile?userId="
				+ user.id + "\">" + user.username + "</a>" + " posted an idea "
				+ "<a href=\"/Ideas/show?ideaId=" + ideaId + "\">" + idea.title
				+ "</a>";
		Log.addLog(logDescription, user, idea, idea.plan, idea.belongsToTopic,
				idea.belongsToTopic.entity,
				idea.belongsToTopic.entity.organization);
		redirect("/Topics/show?topicId=" + idea.belongsToTopic.id);
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
		JsonObject json = new JsonObject();

		json.addProperty("id", idea.id);
		renderJSON(json.toString());
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
		List<Topic> draftTopics = new ArrayList<Topic>();

		for (Idea idea : user.ideasCreated)
			if (idea.isDraft)
				drafts.add(idea);

		for (Topic topic : user.topicsCreated)
			if (topic.isDraft)
				draftTopics.add(topic);

		notFoundIfNull(user);
		notFoundIfNull(draftTopics);
		notFoundIfNull(drafts);

		render(drafts, draftTopics, user);
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
		notFoundIfNull(user);
		notFoundIfNull(idea);
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
	 * @description This method renders the form for creating an idea
	 * 
	 * 
	 */
	public static void blank(long topicId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Topic topic = Topic.findById(topicId);
		User user = Security.getConnected();
		List<MainEntity> listOfEntities = MainEntity.findAll();
		if (!user.isAdmin) {
			try {
				render(type, topic, user, topicId);

			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", type, topicId);
			}
		} else {
			if (user.isAdmin) {
				try {
					render("CoolCRUD/blank.html", type, user, listOfEntities);

				} catch (Exception exception) {
					render("CoolCRUD/blank.html", type, user, listOfEntities);
				}
			}

			else {
				BannedUsers.unauthorized();
			}
		}

	}

	/**
	 * Overriding the CRUD method create.
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S10
	 * 
	 * @param topicId
	 *            : the id of the topic the idea will be posted in
	 * 
	 * @description This method checks for the Validation of the information
	 *              inserted in the Add form of an Idea and if they are valid
	 *              the Idea is created and saved and the community contribution
	 *              counter of the author is incremented.
	 * @throws Exception
	 * 
	 */

	public static void create(long topicId, String title, String description)
			throws Exception {
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
		idea.title = title;
		idea.description = description;
		idea.save();
		if (!author.isAdmin) {
			idea.belongsToTopic = topic;
		} else {
			topic = idea.belongsToTopic;
		}
		idea.author = author;
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

			}

			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						type, idea.title, idea.belongsToTopic,
						idea.description, idea.commentsList, message, topicId);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", type, topicId);
			}
		}

		author.communityContributionCounter++;
		author.save();
		object._save();
		String anothermessage = "you have created a new idea with title "
				+ idea.title + " and with description " + idea.description;

		String logDescription = "<a href=\"/Users/viewProfile?userId="
				+ author.id + "\">" + author.username + "</a>"
				+ " created an idea " + "<a href=\"/Ideas/show?ideaId="
				+ idea.id + "\">" + idea.title + "</a>";
		Log.addLog(logDescription, author, idea, idea.belongsToTopic,
				idea.belongsToTopic.entity,
				idea.belongsToTopic.entity.organization);
		flash.success(Messages.get("crud.created", type.modelName,
				((Idea) object).getId()));
		if (params.get("_save") != null) {

			redirect("/Ideas/show?ideaId=" + ((Idea) object).getId());
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
		String justification = "";
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		Idea idea = (Idea) object;
		Topic topic = idea.belongsToTopic;
		User user = Security.getConnected();
		try {
			// Logs.addLog( user, "delete", "Task", topic.id,
			// topic.taskStory.componentID.project, cal.getTime() );
			String message = user.username + " has hidden the idea "
					+ idea.title + " Justification : " + justification;
			Notifications.sendNotification(idea.author.id, idea.id, "Idea",
					message);
			idea.hidden = true;
			idea.save();

		} catch (Exception e) {
			flash.error(Messages.get("crud.delete.error", type.modelName));
			redirect(request.controller + ".show", object._key());
		}
		flash.success(Messages.get("crud.deleted", type.modelName));
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
	 */
	@SuppressWarnings("unused")
	public static void show(long ideaId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(ideaId);
		notFoundIfNull(object);
		Idea idea = (Idea) object;
		String rate = idea.rating;
		String priority = idea.priority;
		// List<Tag> tags = i.tagsList;
		User user = Security.getConnected();
		List<Comment> comments = idea.commentsList;
		int numberOfComments = comments.size();
		User latest = null;
		long lastId = 0;
		if (numberOfComments > 0) {
			latest = comments.get(numberOfComments - 1).commenter;
			lastId = comments.get(numberOfComments - 1).id;
		}
		if (latest != null && numberOfComments > 1 && latest.isAdmin)
			latest = comments.get(numberOfComments - 2).commenter;
		Plan plan = idea.plan;
		Topic topic = idea.belongsToTopic;
		long topicId = topic.id;
		// boolean openToEdit = i.openToEdit;
		boolean canReport = Users.isPermitted(user, "use", topicId, "topic");
		boolean isAuthor = user.toString().equals(idea.author.toString());
		String deletemessage = "Are you Sure you want to delete the task ?!";
		// boolean deletable = i.isDeletable();
		boolean canDelete = Users.isPermitted(user, "hide and delete an idea",
				topicId, "topic");
		boolean ideaAlreadyReported = false;
		User author = idea.author;
		boolean notAuthor = !idea.author.username.equals(user.username);
		String username = idea.author.username;
		String loggedInUsername = user.username;
		long userId = idea.author.id;
		long loggedInId = user.id;
		boolean notBlockedFromUsing = Users.isPermitted(user, "use", topicId,
				"topic");
		ArrayList<Label> ideasLabels = new ArrayList<Label>();
		boolean notInPlan = (idea.plan == null);
		boolean active = idea.author.state.equals("a");
		boolean isAdmin = user.isAdmin;
		idea.incrmentViewed();
		idea.save();

		for (Label label : user.myLabels)
			if (label.ideas.contains(object))
				ideasLabels.add(label);
		boolean canUse = Users.canDelete(user, "use", ideaId, "idea", topicId);
		List<User> allUsers = User.findAll();
		List<String> userNames = new ArrayList<String>();
		String s;
		for (int i = 0; i < allUsers.size(); i++) {
			s = allUsers.get(i).username;
			userNames.add(s);
		}
		Collections.sort(userNames);
		userNames.remove(user.username);
		boolean checkPermitted = Users.isPermitted(user,
				"rate/prioritize ideas", topicId, "topic");
		boolean checkNotRated;
		if (idea.usersRated.contains(user))
			checkNotRated = false;
		else
			checkNotRated = true;
		for (int i = 0; i < idea.reporters.size(); i++) {
			if (idea.reporters.size() > 0
					&& (user.toString()
							.equals(idea.reporters.get(i).toString()))) {
				ideaAlreadyReported = true;
				break;
			} else
				ideaAlreadyReported = false;
		}

		try {
		
			boolean permittedToTagIdea = user.equals(idea.author)
					|| Users.isPermitted(user, "tag ideas in my organization",
							topicId, "topic")
					|| Users.isPermitted(user, "use", topicId, "topic");
			List<Tag> tags = idea.tagsList;
			render(type, ideasLabels, object, tags, user, isAdmin, username,
					loggedInUsername, userId, canReport, canDelete, loggedInId,
					latest, comments, lastId, numberOfComments, topic, plan,
					permittedToTagIdea,
					/* openToEdit, */topicId, notInPlan, ideaAlreadyReported,
					canUse, deletemessage, /*
											 * deletable ,
											 */
					ideaId, rate, idea, priority, author, userNames,
					checkPermitted, checkNotRated, notBlockedFromUsing);
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
	 */

	public static void edit(long ideaId) {

		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(ideaId);
		notFoundIfNull(object);
		Idea i = (Idea) object;
		flash.success(Messages.get("crud.edit", type.modelName,
				((Idea) object).getId()));
		redirect("/Ideas/show?ideaId=" + ((Idea) object).getId(), i);
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
	 * @description This method is responsible for viewing an idea
	 * 
	 * 
	 */
	public static void view(long ideaId) {

		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(ideaId);
		notFoundIfNull(object);
		Idea idea = (Idea) object;
		// List<Tag> tags = i.tagsList;
		User author = idea.author;
		List<Comment> comments = idea.commentsList;
		Plan plan = idea.plan;
		Topic topic = idea.belongsToTopic;
		// boolean openToEdit = i.openToEdit;
		String deletemessage = "Are you Sure you want to delete the task ?!";
		// boolean deletable = i.isDeletable();
		try {
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
		Model object = Idea.findById(Long.parseLong(ideaid));
		notFoundIfNull(object);
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		Idea i = (Idea) object;
		Topic topic = Topic.findById((long) 1);
		String message = "";
		i.belongsToTopic = topic;
		User myUser = User.findById((long) 1);
		i.author = myUser;
		if (validation.hasErrors()) {
			if (i.description.equals("")) {
				message = "A Topic must have a description";

			}

			try {
				render(request.controller.replace(".", "/") + "/show.html",
						topic, type, i.title, i.belongsToTopic, i.description,
						i.tagsList, message, object, ideaid);
			} catch (TemplateNotFoundException e) {
				render("CRUD/show.html", type);
			}
		}

		object._save();
		String logDescription = "<a href=\"/Users/viewProfile?userId="
				+ myUser.id + "\">" + myUser.username + "</a>"
				+ " edited the idea  " + "<a href=\"/Ideas/show?ideaId=" + i.id
				+ "\">" + i.title + "</a>";
		Log.addLog(logDescription, myUser, i, i.belongsToTopic,
				i.belongsToTopic.entity, i.belongsToTopic.entity.organization);
		flash.success(Messages.get("crud.saved", type.modelName,
				((Idea) object).getId()));
		if (params.get("_save") != null) {
			redirect("/ideas/view?ideaId=" + ((Idea) object).getId());
		}
		redirect(request.controller + ".show", ((Idea) object).getId());
	}

	/**
	 * Overriding the CRUD method delete.
	 * 
	 * @author ${Ahmed EL-Hadi}
	 * 
	 *         C3S6/C3S17
	 * 
	 * @param ideaId
	 *            : id of the idea to be deleted
	 * 
	 * @description This method deletes and idea from the database
	 * 
	 */
	public static boolean delete(long ideaId, String justification) {
		Idea idea = Idea.findById(ideaId);
		Topic topic = idea.belongsToTopic;
		long topicId = topic.id;
		String title = idea.title;
		String topicTitle = topic.title;
		String message = "your idea " + idea.title + " has been deleted by "
				+ Security.getConnected().username + " Justification : "
				+ justification;

		notFoundIfNull(idea);
		if (Security.getConnected().equals(idea.author)) {
			List<Comment> commentslist = Comment.find("byCommentedIdea", idea)
					.fetch();
			for (int i = 0; i < commentslist.size(); i++) {
				commentslist.get(i).delete();
				idea.save();
			}
			try {
				if (idea.plan == null) {
					
					idea.delete();
					topic.save();
					return true;
				}
			} catch (Exception e) {
				redirect("/Topics/show?topicId=" + idea.belongsToTopic.id);
			}
		} else {
			List<Comment> commentslist = Comment.find("byCommentedIdea", idea)
					.fetch();
			for (int i = 0; i < commentslist.size(); i++) {
				commentslist.get(i).delete();
				idea.save();
			}
			try {
				if (idea.plan == null) {
					idea.delete();
					topic.save();
					Notifications.sendNotification(idea.author.id, idea.id,
							"Idea", message);

				}
			} catch (Exception e) {
				redirect("/Topics/show?topicId=" + idea.belongsToTopic.id);
			}

		}
		String logDescription = "<a href=\"Users/viewProfile?userId="
				+ Security.getConnected().id + "\">"
				+ Security.getConnected().username + "</a>"
				+ " deleted the idea " + title + "that belongs to topic  "
				+ "<a href=\"/Topics/show?topicId=" + topicId + "\">"
				+ topicTitle + "</a>";
		Log.addLog(logDescription, Security.getConnected(), topic,
				topic.entity.organization, topic.entity);
		return false;
	}

	public static void deleteIdea(long ideaId, String justification) {
		Idea idea = Idea.findById(ideaId);
		String ideaTitle = idea.title;
		Topic ideaTopic = idea.belongsToTopic;
		delete(ideaId, justification);
		redirect("/Topics/show?topicId=" + idea.belongsToTopic.id);
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
	 * @param ideaId
	 *            : the idea that is being tagged
	 * 
	 * @param tag
	 *            : the tag that is being added
	 * 
	 */

	public static void tagIdea(long ideaId, String tag) {
		Tag tempTag = null;
		List<Tag> listOfTags = getAvailableTags(ideaId);
		boolean tagAlreadyExists = false;
		boolean tagExists = false;
		User user = (User) Security.getConnected();
		Idea idea = (Idea) Idea.findById(ideaId);
		notFoundIfNull(idea);

		for (int i = 0; i < listOfTags.size(); i++) {
			if (listOfTags.get(i).getName().equalsIgnoreCase(tag)) {
				tempTag = listOfTags.get(i);
				if (!idea.tagsList.contains(tempTag)) {
					idea.tagsList.add(tempTag);
					tempTag.taggedIdeas.add(idea);
					tempTag.save();

					for (int j = 0; j < tempTag.followers.size(); j++) {
						Notifications.sendNotification(
								tempTag.followers.get(j).id, tempTag.getId(),
								"tag", "This topic has been tagged as " + tag);
					}
				} else {
					tagAlreadyExists = true;
				}
				tagExists = true;
			}
		}

		if (!tagExists && idea.belongsToTopic.entity.organization.createTag) {
			tempTag = new Tag(tag, idea.belongsToTopic.entity.organization,
					user);
			idea.tagsList.add(tempTag);
			tempTag.taggedIdeas.add(idea);
			tempTag.save();
		}

		if (!tagAlreadyExists) {
			Notifications.sendNotification(idea.author.id, ideaId, "idea",
					"This idea has been tagged as " + tag);
			String logDescription = "<a href=\"/Users/viewProfile?userId="
					+ user.id + "\">" + user.username + "</a>"
					+ " added the tag " + "<a href=\"/Tags/mainPage?tagId="
					+ tempTag.id + "\">" + tempTag.name + "</a>"
					+ " to the idea " + "<a href=\"/Ideas/show?ideaId="
					+ idea.id + "\">" + idea.title + "</a>";
			Log.addUserLog(logDescription, idea, idea.belongsToTopic,
					idea.belongsToTopic.entity,
					idea.belongsToTopic.entity.organization);
		}
		idea.save();
		JsonObject json = new JsonObject();
		json.addProperty("tagName", tempTag.name);
		json.addProperty("tagId", tempTag.id);

		renderJSON(json.toString());
	}

	/**
	 * 
	 * @description this method checks if the given tag is already in the list
	 *              of tags related to this idea to avoid duplication
	 * 
	 * @author Mostafa Yasser El Monayer
	 * 
	 * @story C3S3, C3S11
	 * 
	 * @param ideaId
	 *            : the idea that is being tagged
	 * 
	 * @param tag
	 *            : the tag that is being added
	 * 
	 */

	public static void checkIfTagAlreadyExists(long ideaId, String tag) {
		Idea idea = (Idea) Idea.findById(ideaId);
		notFoundIfNull(idea);
		boolean tagAlreadyExists = false;
		List<Tag> listOfTags = idea.tagsList;
		for (int i = 0; i < listOfTags.size(); i++) {
			if (listOfTags.get(i).getName().equalsIgnoreCase(tag)) {
				tagAlreadyExists = true;
				break;
			} else {
				tagAlreadyExists = false;
			}
		}
		JsonObject json = new JsonObject();
		json.addProperty("tagAlreadyExists", tagAlreadyExists);
		renderJSON(json.toString());
	}

	/**
	 * 
	 * @description this method returns the list of tags that are available for
	 *              that idea to be tagged with
	 * 
	 * @author Mostafa Yasser El Monayer
	 * 
	 * @story C3S3, C3S11
	 * 
	 * @param ideaId
	 *            : the idea that is being tagged
	 * 
	 * @return list of tags
	 * 
	 */

	public static List<Tag> getAvailableTags(long ideaId) {
		List<Tag> listOfTags = new ArrayList<Tag>();
		List<Tag> globalListOfTags = new ArrayList<Tag>();
		globalListOfTags = Tag.findAll();
		notFoundIfNull(globalListOfTags);
		Idea idea = Idea.findById(ideaId);
		notFoundIfNull(idea);
		for (int i = 0; i < globalListOfTags.size(); i++) {
			if (globalListOfTags.get(i).createdInOrganization.privacyLevel == 2
					|| idea.belongsToTopic.entity.organization
							.equals(globalListOfTags.get(i).createdInOrganization)) {
				listOfTags.add(globalListOfTags.get(i));
			}
		}
		return listOfTags;
	}

	/**
	 * This method generates the average of the user-entered rating and the
	 * rating of the specified idea
	 * 
	 * @author Ibrahim Safwat
	 * 
	 * @story C4S13
	 * 
	 * @param ideaId
	 *            : the id of the idea the user wants to rate
	 * 
	 * @param rating
	 *            : user-entered rating
	 * 
	 * @return : void
	 */

	public static void rate(long ideaId, int rating) {
		User userLoggedIn = Security.getConnected();
		Idea ideaInUse = Idea.findById(ideaId);
		if (ideaInUse.usersRated.contains(userLoggedIn)) {
			renderText("failure");
			return;
		}
		if (ideaInUse.rating.equals("0")) {
			ideaInUse.rating = Double.toString(rating);
		} else {
			double oldRating = Double.parseDouble(ideaInUse.rating);
			double newRating = (oldRating + rating) / 2;
			ideaInUse.rating = Double.toString(newRating);
		}
		ideaInUse.usersRated.add(userLoggedIn);
		ideaInUse.save();
		renderText(ideaInUse.rating);
	}

	/**
	 * This method allows users to share ideas through notifications
	 * 
	 * @author Ibrahim Safwat
	 * 
	 * @story C4S08
	 * 
	 * @param userName
	 *            : username of the user that the notification will be sent to
	 * 
	 * @param ideaId
	 *            : the ID of the idea that the notification will be sent from
	 * 
	 * @return void
	 */
	public static void shareIdea(String userName, long ideaId) {

		User userChosen = User.find("byUsername", userName).first();
		User userLoggedIn = Security.getConnected();
		String desc = userLoggedIn.firstName + " " + userLoggedIn.lastName
				+ " shared an Idea with you";
		Notifications.sendNotification(userChosen.id, ideaId, "Idea", desc);

	}

	/**
	 * This method allows organizers to assign ideas certain priorities
	 * 
	 * @author Ibrahim Safwat
	 * 
	 * @story C4S13
	 * 
	 * @param priority
	 *            : the priority to be set
	 * 
	 * @param ideaId
	 *            : the ID of the idea to prioritize
	 * 
	 * @return void
	 */
	@SuppressWarnings("unused")
	public static void setPriority(String priority, long ideaId) {
		User userLoggedIn = Security.getConnected();
		Idea ideaInUse = Idea.findById(ideaId);
		long topicId = ideaInUse.belongsToTopic.id;
		ideaInUse.priority = priority;
		ideaInUse.save();
		redirect("/Ideas/show?ideaId=" + ideaId);
	}

	/**
	 * @description The method renders the ideas the organizer wants to merge
	 * 
	 * @author Mostafa Aboul-Atta
	 * 
	 * @story C3S7
	 * 
	 * @param selectedIdeas
	 *            : array of the selected ideas ids
	 * 
	 * @param topicId
	 *            : the id of the topic t which the ideas belongs
	 */

	public static void displayIdeasToMerge(long[] selectedIdeasIds, long topicId) {

		Topic targetTopic = Topic.findById(topicId);
		notFoundIfNull(targetTopic);
		List<Idea> selectedIdeas = new ArrayList<Idea>();
		// List<Idea> allIdeas = targetTopic.getIdeas();
		String selectedIdeasString = "";
		User user = Security.getConnected();
		selectedIdeas = Ideas.getIdeasFromIds(selectedIdeasIds, topicId);

		for (int i = 0; i < selectedIdeasIds.length; i++) {
			selectedIdeasString = selectedIdeasString + selectedIdeasIds[i]
					+ "%";
		}
		long idd = selectedIdeas.get(0).getId();

		render(selectedIdeas, topicId, selectedIdeasString, idd, user);
	}

	/**
	 * @description method to merge ideas, overwrites the first idea with the
	 *              new description and title then deletes the other ideas, the
	 *              authors of the deleted ideas are added in the description as
	 *              contributors and the merger is added to the description as
	 *              the merger
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @stroy C3S7
	 * 
	 * @param topicId
	 *            : the id of the topic to which the ideas belong
	 * @param oldIdeas
	 *            : ideas to be merged
	 * @param newTitle
	 *            : title of the resulted idea
	 * @param newDescription
	 *            : description of the resulted idea
	 * 
	 */
	public static void mergeIdeas(long topicId, String oldIdeas,
			String newTitle, String newDescription) {

		String allContributors = "";
		Topic targetTopic = Topic.findById(topicId);
		User merger = Security.getConnected();
		ArrayList<String> contributorsList = new ArrayList<String>();
		User user = Security.getConnected();

		String[] ideasIdsString = oldIdeas.split("%");
		long[] ideasIds = new long[ideasIdsString.length];
		List<Idea> selectedIdeas = new ArrayList<Idea>();
		Idea ideaToKeep;

		for (int i = 0; i < ideasIdsString.length; i++) {
			ideasIds[i] = Long.parseLong(ideasIdsString[i]);
		}

		selectedIdeas = Ideas.getIdeasFromIds(ideasIds, topicId);
		ideaToKeep = selectedIdeas.get(0);
		contributorsList.add(ideaToKeep.author.username);

		if (isDifferentContributors(selectedIdeas)) {
			newDescription = newDescription + " " + "\nContributers: ";

			for (int i = 1; i < selectedIdeas.size(); i++) {
				String contributor = selectedIdeas.get(i).author.username;
				if (i == selectedIdeas.size() - 1) {
					if (contributorsList.indexOf(contributor) == -1) {
						allContributors = allContributors + contributor;
						contributorsList.add(contributor);
					}
				} else {
					if (contributorsList.indexOf(contributor) == -1) {
						allContributors = allContributors + contributor + ", ";
						contributorsList.add(contributor);
					}
				}
			}

		}
		allContributors = allContributors + " \n" + "Merger: "
				+ merger.username;
		newDescription = newDescription + allContributors;

		ideaToKeep.description = newDescription;
		ideaToKeep.title = newTitle;

		for (int i = 1; i < selectedIdeas.size(); i++) {
			Idea ideaToDelete = selectedIdeas.get(i);
			ideaToDelete.delete();
		}

		ideaToKeep.save();
		targetTopic.save();

		String log = "<a href=\"/Users/viewProfile?userId=" + user.id + "\">"
				+ user.username + "</a>" + " merged ideas "
				+ "<a href=\"/Ideas/show?ideaId=" + ideaToKeep.id + "\">"
				+ ideaToKeep.title + "</a>" + " of the topic "
				+ "<a href=\"/Topics/show?topicId=" + targetTopic.id + "\">"
				+ targetTopic.title + "</a>";

		MainEntity entity = targetTopic.entity;
		Organization organization = entity.organization;

		Log.addUserLog(log, user, ideaToKeep, targetTopic, entity, organization);

	}

	/**
	 * @description takes an array of ideas ids and the topic id they belong to
	 *              and returns a list of the ideas
	 * 
	 * @author Mostafa Aboul-Atta
	 * 
	 * @param selectedIdeasIds
	 *            : ideas ids that their ideas to be returned
	 * @param topicId
	 *            : id of the topic to which the ideas belong
	 * @return : list of Idea
	 */
	public static List<Idea> getIdeasFromIds(long[] selectedIdeasIds,
			long topicId) {
		Topic targetTopic = Topic.findById(topicId);
		List<Idea> selectedIdeas = new ArrayList<Idea>();
		List<Idea> allIdeas = targetTopic.ideas;
		// String selectedIdeasString = "";

		Arrays.sort(selectedIdeasIds);

		for (int i = 0; i < allIdeas.size(); i++) {

			// checks if all selected ideas are already in the new list
			if (selectedIdeas.size() == selectedIdeasIds.length) {
				break;
			}

			Idea currentIdea = allIdeas.get(i);

			// checks if the current idea is indeed selected
			if (Arrays.binarySearch(selectedIdeasIds, currentIdea.getId()) >= 0) {
				selectedIdeas.add(currentIdea);
			}

		}

		return selectedIdeas;
	}

	/**
	 * @author ${Ibrahim safwat}
	 * 
	 * @param ideaID
	 *            ID of the idea that the user wants to add the comment to
	 * @param comment
	 *            Comment to be added to list of comments of the idea addes a
	 *            comment to an idea
	 */
	public static void addCommentToIdea(long ideaID, String comment) {
		Idea i = Idea.findById(ideaID);
		User user = Security.getConnected();
		Comment c = new Comment(comment, i, user).save();
		long cID = c.id;
		user.hisComments.add(c);
		user.save();
		i.commentsList.add(c);
		i.save();

		JsonObject json = new JsonObject();
		json.addProperty("commentMsg", comment);
		json.addProperty("cID", cID);
		json.addProperty("commentUser", user.username);
		json.addProperty("commentDate", c.commentDate + "");
		json.addProperty("userPic", user.profilePictureId + "");
		renderJSON(json.toString());
	}

	/**
	 * @description checks if all contributors are different from the author
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @story C3S7
	 * 
	 * @param selectedIdeas
	 *            the list of selected ideas
	 * @return true if yes false if no
	 */
	private static boolean isDifferentContributors(List<Idea> selectedIdeas) {

		String author = selectedIdeas.get(0).author.username;
		for (int i = 1; i < selectedIdeas.size(); i++) {
			String contributor = selectedIdeas.get(i).author.username;
			if (author != contributor) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Author ${Ahmed El-Hadi}
	 */
	public static boolean isCommenter(long commentId) {
		Comment c = Comment.findById(commentId);
		User user = Security.getConnected();
		if (c.commenter.username.equals(user.username))
			return true;
		return false;
	}

	/**
	 * Author ${Ahmed El-Hadi}
	 */
	public static void delComments(long commentId) {
		Comment c = Comment.findById(commentId);
		Idea idea = c.commentedIdea;
		List<Comment> commentslist = Comment.find("byCommentedIdea", idea)
				.fetch();
		int listSize = commentslist.size();
		User commenter = null;
		for (int i = 0; i < commentslist.size(); i++) {
			commenter = commentslist.get(i).commenter;
			if (commentslist.get(i).id == commentId) {
				commentslist.get(i).delete();
				commentslist.clear();
				commenter.save();
				idea.save();
				i--;
				if (commenter.id != Security.getConnected().id)
					Notifications.sendNotification(commenter.id, idea.id,
							"Idea", "Your comment on the idea " + idea.title
									+ " has been Deleted by The user "
									+ Security.getConnected().username);
			}
			idea.save();
		}
		int size = idea.commentsList.size();
		for (int i = 0; i < size; i++) {
			// Comments.deleteComment(idea.commentsList.get(i).id);
			idea.commentsList.get(i).commenter.hisComments.clear();
			idea.commentsList.get(i).commenter.save();
			idea.commentsList.clear();
			idea.save();
			idea.belongsToTopic.save();
			idea.belongsToTopic.entity.save();
			idea.belongsToTopic.entity.organization.save();
		}
		idea.save();
		redirect("/Ideas/show?ideaId=" + idea.id);
	}
}
