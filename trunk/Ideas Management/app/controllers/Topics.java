package controllers;

import java.util.ArrayList;
import java.util.List;

import models.BannedUser;
import models.Idea;
import models.MainEntity;
import models.Organization;
import models.Tag;
import models.Topic;
import models.User;
import models.UserRoleInOrganization;

import java.lang.*;
import java.lang.reflect.*;
import java.util.*;

import controllers.CRUD.ObjectType;
import controllers.Secure.Security;

import play.data.binding.*;
import play.db.*;
import play.exceptions.*;
import play.i18n.*;
import models.*;

public class Topics extends CRUD {

	/**
	 * This Method returns true if the tag exists in the global list of tags and
	 * checks if it the topic is already tagged with the same tag (returns true
	 * also), false if the tag needs to be created.
	 * 
	 * @author Mostafayasser.1991
	 * 
	 * @story C3S2
	 * 
	 * @param tag
	 *            : the tag that is being added
	 * 
	 * @param user
	 *            : the user who is tagging the topic
	 * 
	 * @param topicID
	 *            : the topic that is being tagged
	 * 
	 * @return boolean
	 */

	public static boolean tagTopic(int topicID, String tag, User user) {
		boolean tagAlreadyExists;
		ArrayList<Tag> listOfTags = (ArrayList) Tag.findAll();
		for (int i = 0; i < listOfTags.size(); i++) {
			if (listOfTags.get(i).getName().equalsIgnoreCase(tag)) {
				Topic topic = Topic.findById(topicID);

				if (!topic.tags.contains(listOfTags.get(i))) {
					topic.tags.add(listOfTags.get(i));
					// send notification to followers of the topic
					// send notification to topic organizers
					// send notification to organization lead
				} else {
					// error message
					tagAlreadyExists = true;
				}
				return true;

			}
		}
		return false;
	}

	/**
	 * 
	 * This method is responsible for posting an idea to a topic by a certain
	 * user
	 * 
	 * @author ${Ahmed El-Hadi}
	 * 
	 * @story C3S10
	 * 
	 * @param user
	 *            : the user who posted the idea
	 * 
	 * @param topic
	 *            : the topic which the idea belongs/added to
	 * 
	 * @param title
	 *            : the title of the idea
	 * 
	 * @param description
	 *            : the description of the idea
	 * 
	 * @param privacyLevel
	 *            : the level of privacy of the idea
	 * 
	 */

	public static void postIdea(User user, Topic topic, String title,
			String description) {
		Idea idea = new Idea(title, description, user, topic);
		idea.privacyLevel = topic.privacyLevel;
	}

	/**
	 * 
	 * This method reopens a closed topic, used after its plan gets deleted
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @story C3S22
	 * 
	 * @param topicId
	 *            : the id of the topic that to be reopened
	 * 
	 * @return void
	 */
	public static boolean reopen(long topicId, User actor) {

		Topic targetTopic = Topic.findById(topicId);

		if (!targetTopic.organizers.contains(actor)) {
			return false;
		}

		targetTopic.openToEdit = true;
		/* TODO: buttons to be adjusted in view */

		return true;
	}

	/**
	 * This Method returns a list of all closed topics
	 * 
	 * @author aliaelbolock
	 * 
	 * @story C3S1
	 * 
	 * @return ArrayList<Topics>
	 */
	public static ArrayList<Topic> closedTopics() {
		List closedtopics = (List) new ArrayList<Topic>();
		closedtopics = (List) Topic.find("openToEdit", false).fetch();
		return (ArrayList<Topic>) closedtopics;
	}

	/**
	 * 
	 * This method gets a list of followers for a certain topic
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S29
	 * 
	 * @param id
	 *            : id of the topic
	 * 
	 * @return void
	 */
	public static void viewFollowers(long id) {
		Topic topic = Topic.findById(id);
		notFoundIfNull(topic);
		List<User> follow = topic.followers;
		render(follow);
	}

	/**
	 * This Method sends a request to post on a topic for a user to the
	 * organizer
	 * 
	 * @author ibrahim.al.khayat
	 * 
	 * @story C2S13
	 * 
	 * @param topicId
	 *            : the id of the topic
	 * 
	 * @param user
	 *            : the user who request to post
	 * 
	 * @return void
	 */

	public static void requestToBost(long topicId, User user) {
		Topic t = Topic.findById(topicId);
		t.requestFromUserToPost(user);
	}

	/**
	 * This renders the RequestToPost.html to show the list of all topics where
	 * the user is not allowed to post within an organization
	 * 
	 * @author ibrahim.al.khayat
	 * 
	 * @story C2S13
	 * 
	 * @param org
	 *            : The organization where the topics are
	 * 
	 * @param user
	 *            : the user who wants to request
	 * 
	 * @return void
	 */

	public static void requestTopicList(User user, Organization org) {
		List<MainEntity> e = org.entitiesList;
		List<Topic> topics = new ArrayList<Topic>();
		List<Topic> temp;
		for (int i = 0; i < e.size(); i++) {
			temp = e.get(i).topicList;
			for (int j = 0; j < temp.size(); j++) {
				if (user.topicsIOrganize.indexOf(temp.get(j)) < 0) {
					topics.add(temp.get(j));
				}
			}
		}
		render(topics, user);
	}

	
	/**
	 * This method searches for unblocked users who are allowed to post
	 * in a certain topic
	 * 
	 * @author lama.ashraf
	 * 
	 * @story C1S13
	 * 
	 * @param topicId
	 *            : the id of the topic to search in
	 * 
	 * @return List<User>
	 */
	public static List<User> searchByTopic(long id) {

		Topic topic = Topic.findById(id);
		MainEntity entity = topic.entity;
		Organization org = entity.organization;
		ArrayList<User> searchList = (ArrayList) User.find("byIsAdmin", true)
				.fetch();
		searchList.add(org.creator);
		// searchList.add(topic.creator);
		
		ArrayList<User> organizer = (ArrayList) Users
				.getEntityOrganizers(entity);
		searchList.addAll(organizer);
		
		List<BannedUser> bannedUserT = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"all", "topic", id).fetch();                                  // List of blocked users from a topic
		List<BannedUser> bannedUserE = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"all", "entity", entity.id).fetch();                          // list of blocked users from an entity
		List<BannedUser> bannedUserO = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"all", "organization", org.id).fetch();                      // list of blocked user from an organization
		List<BannedUser> bannedUserP = BannedUser.find(
				"byOrganizationAndActionAndResourceTypeAndResourceID", org,
				"post idea", "topic", id).fetch();                           // list of users banned from posting ideas in the topic
		
		List<User> bUser = new ArrayList<User>();
		List<User> user = new ArrayList<User>();
		List<BannedUser> bannedUser = new ArrayList<BannedUser>();        // list appending all the previous banneduser lists
		bannedUser.addAll(bannedUserT);
		bannedUser.addAll(bannedUserE);
		bannedUser.addAll(bannedUserO);
		bannedUser.addAll(bannedUserP);

		for (int i = 0; i < bannedUser.size(); i++) {
			bUser.add((bannedUser.get(i)).bannedUser);
		}

		List<UserRoleInOrganization> allUser = new ArrayList<UserRoleInOrganization>();

		if ((org.privacyLevel == 0 || org.privacyLevel == 1)
				&& (topic.privacyLevel == 0 || topic.privacyLevel == 1)) {
			allUser = (List<UserRoleInOrganization>) UserRoleInOrganization
					.find("select uro.enrolled from UserRoleInOrganization uro, Role r where uro.Role = r and uro.organization = ? and uro.entityTopicID = ? and r.roleName like ? and and uro.type like ?",
							org, id, "idea developer", "topic");

			for (int i = 0; i < allUser.size(); i++) {
				user.add((allUser.get(i)).enrolled);
			}

			for (int i = 0; i < bUser.size(); i++) {
				if (user.contains(bUser.get(i))) {
					user.remove(bUser.get(i));

				}
			}
		} else {

			if ((org.privacyLevel == 0 || org.privacyLevel == 1)
					&& (topic.privacyLevel == 2)) {
				allUser = (List<UserRoleInOrganization>) UserRoleInOrganization
						.find("select uro.enrolled from UserRoleInOrganization uro, Role r where uro.Role = r and uro.organization = ? and uro.entityTopicID = ? and r.roleName like ? and and uro.type like ?",
								org, -1, "idea developer", "none");
				
				for (int i = 0; i < allUser.size(); i++) {
					user.add((allUser.get(i)).enrolled);
				}

				for (int i = 0; i < bUser.size(); i++) {
					if (user.contains(bUser.get(i))) {
						user.remove(bUser.get(i));

					}
				}
			}

			else {
				if ((org.privacyLevel == 2)
						&& (topic.privacyLevel == 0 || topic.privacyLevel == 1)) {
					allUser = (List<UserRoleInOrganization>) UserRoleInOrganization
							.find("select uro.enrolled from UserRoleInOrganization uro, Role r where uro.Role = r and uro.organization = ? and uro.entityTopicID = ? and r.roleName like ? and and uro.type like ?",
									org, id, "idea developer", "topic");
					

					for (int i = 0; i < allUser.size(); i++) {
						user.add((allUser.get(i)).enrolled);
					}

					for (int i = 0; i < bUser.size(); i++) {
						if (user.contains(bUser.get(i))) {
							user.remove(bUser.get(i));

						}
					}
				} else {
					if ((org.privacyLevel == 2) && (topic.privacyLevel == 2)) {

						user = User.findAll();

						for (int i = 0; i < bUser.size(); i++) {
							if (user.contains(bUser.get(i))) {
								user.remove(bUser.get(i));

							}
						}
					}
				}
			}
		}
		searchList.addAll(user);
		return searchList;

	}

	/**
	 * This method closes a topic, return true if was successful, returns false
	 * if the there was ideas and doesn't close the topic
	 * 
	 * @author Mostafa Aboul Atta
	 * 
	 * @story C3S4
	 * 
	 * @param topicId
	 *            : the id of the topic to be closed
	 * 
	 * @return boolean
	 */
	public static boolean closeTopic(long topicId, User actor) {
		Topic targetTopic = Topic.findById(topicId);

		// checks if topic is empty or the user is not an organizer
		if (targetTopic.ideas.size() == 0
				|| !targetTopic.organizers.contains(actor)) {
			return false;
		}
		
		//closing the topic to editing
		targetTopic.openToEdit = false;

		// Sending Notifications
		String notificationDescription = "Topic " + targetTopic.title + 
			" has been closed and promoted to execution.";
		
		//send notification to organizers
		Notifications.sendNotification(targetTopic.organizers, 
				targetTopic.id, "Topic", notificationDescription);
		
		//send notification to followers
		Notifications.sendNotification(targetTopic.followers, 
				targetTopic.id, "Topic", notificationDescription);
		
		// TODO: edit buttons in view

		return true;

	}

	
	/**
	 * Overriding the CRUD method create.
	 * 
	 * @author aliaelbolock
	 * 
	 * @story C3S1
	 *
	 * 
	 * @description This method checks for the Validation of the info inserted in the Add
	 *              form of a Topic and if they are valid the object is
	 *              created and saved.
	 * @throws Exception
	 * 
	 * @return void
	 */
	public static void create() throws Exception {
		ObjectType type = ObjectType.get( getControllerClass() );
		notFoundIfNull( type );
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Model object = (Model) constructor.newInstance();
		Binder.bind(object, "object", params.all());
		validation.valid( object);
		String message = "";
		Topic tmp = (Topic) object;
		System.out.println( "create() entered");
		MainEntity topicEntity = tmp.entity;
		
		if( tmp.entity == null )
		{
			message = "A Topic must belong to an entity";
			try
			{
				render( request.controller.replace( ".", "/" ) + "/blank.html", type, message );
			}
			catch( TemplateNotFoundException e )
			{
				render( "CRUD/blank.html", type, message );
			}
		}
		
		Organization topicOrganization = topicEntity.organization;
		//ArrayList<Tag> topicTags = (ArrayList<Tag>) tmp.tags;
		if(!(topicEntity.followers.size() == 0 ||topicOrganization.followers.size() == 0))
		tmp.followers =User.find("byFollowingEntitiesAndFollowingOrganizations", topicEntity,topicOrganization).fetch();
		 

		if( validation.hasErrors() )
		{
			if( tmp.title.equals( "" ) )
			{
				message = "A Topic must have a title";
			}
			else if( tmp.description.equals("") )
			{
				message = "A Topic must have a description";

			}
			else if( tmp.creator == null)
			{
				message = "A task must have a creator";
				try
				{
					render( request.controller.replace( ".", "/" ) + "/blank.html", topicEntity , type, tmp.title, tmp.entity, tmp.description, tmp.followers, tmp.tags, message);
				}
				catch( TemplateNotFoundException e )
				{
					render( "CRUD/blank.html", type );
				}
			}
			else if(tmp.creator != User.find( "byEmail", Security.connected() ).first())
			{
				message = "You must be the creator of the topic";
				try
				{
					render( request.controller.replace( ".", "/" ) + "/blank.html", topicEntity , type, tmp.title, tmp.entity, tmp.description, tmp.followers, tmp.tags, message);
				}
				catch( TemplateNotFoundException e )
				{
					render( "CRUD/blank.html", type );
				}
			}
			else if( !Users.isPermitted(tmp.creator , "post", topicEntity.getId(), "entity"))
			{
				message = "Sorry but you are not allowed to post topics in this entity";
			}
			try
			{
				render( request.controller.replace( ".", "/" ) + "/blank.html", topicEntity , type, tmp.title, tmp.entity, tmp.description, tmp.followers, tmp.tags, message);
			}
			catch( TemplateNotFoundException e )
			{
				render( "CRUD/blank.html", type );
			}
		}
		if( tmp.plan != null )
		{
			message = "The topic can't be assigned to a plan yet, as it is in the process of being created";
			try
			{
				render( request.controller.replace( ".", "/" ) + "/blank.html", topicEntity , type, tmp.title, tmp.entity, tmp.description, tmp.followers, tmp.tags, message);
			}
			catch( TemplateNotFoundException e )
			{
				render( "CRUD/blank.html", type );
			}
		}

		
		System.out.println( "create() about to save object" );
		object._save();
		System.out.println( "create() object saved" );
		tmp = (Topic) object;
		Calendar cal = new GregorianCalendar();
		//Logs.addLog( tmp.creator, "add", "Task", tmp.id, tmp.entity.organization, cal.getTime() );
		String message2 = tmp.creator.username + " has Created the topic " + tmp.title + " in " + tmp.entity;
		//Notifications.notifyUsers( Users.getEntityOrganizers(tmp.entity), "Topic Created", message2, (byte) 1 );

		// tmp.init();
		flash.success( Messages.get( "crud.created", type.modelName, ((Topic) object).getId()) );
		if( params.get( "_save" ) != null )
		{
			System.out.println( "create() done will redirect to topics/show?topicid " + message2  );
			redirect("/topics/show?topicid=" + ((Topic) object).getId());
			
			//redirect("/topics/show?" + ((Topic) object).getId(), message2);
			//redirect( "/storys/liststoriesinproject?projectId=" + tmp.taskStory.componentID.project.id + "&storyId=" + tmp.taskStory.id );
		}
		if( params.get( "_saveAndAddAnother" ) != null )
		{
			System.out.println( "create() done will redirect to blank.html to add another " + message2  );
			redirect( request.controller + ".blank", message2 );
		}
		System.out.println( "create() done will redirect to show.html to show created" + message2  );
		redirect( request.controller + ".show",  ((Topic) object).getId(), message2) ;
	}

	/**
	 * Overriding the CRUD method blank.
	 * 
	 * @author aliaelbolock
	 * 
	 * @story C3S1
	 *
	 * @param entityid
	 *            : id of the entity the topic is in
	 *            
	 * @param userid
	 *            : id of the current user
	 * 
	 * @description This method renders the form for creating a topic in the entity
	 * 
	 * @throws Exception
	 * 
	 * @return void
	 */
	public static void blank( long entityid, long userid )
	{
		ObjectType type = ObjectType.get( getControllerClass() );
		notFoundIfNull( type );
		MainEntity entity = MainEntity.findById( entityid );
		User user = User.findById( userid);
		System.out.println( "blank() entered entity " + entityid + " and user " + userid );
		//List<User> followers = entity.followers;
		//ArrayList<MainEntity> entitiesFollowed = (ArrayList<MainEntity>) user.followingEntities;//display some of them on the side for quick navigation
		
		//handle permissions, depending on the privacyLevel the user will be directed to a different page 

		try
		{   System.out.println( "blank() done about to render" );
			render( type, entity, user/*, followers, entitiesFollowed*/ );

		}
		catch( TemplateNotFoundException e )
		{
			System.out.println( "blank() done with exception about to render CRUD/blank.html" );
			render( "CRUD/blank.html", type );
		}

	}

	/**
	 * Overriding the CRUD method show.
	 * 
	 * @author aliaelbolock
	 * 
	 * @story C3S1
	 *
	 *@param topicid
	 *         : id of the topic we want to show
	 * 
	 * @description This method renders the form for editing and viewing a topic
	 * 
	 * @throws Exception
	 * 
	 * @return void
	 */
	public static void show( String topicid )
	{
		ObjectType type = ObjectType.get( getControllerClass() );
		notFoundIfNull( type );
		Model object = type.findById(topicid);
		notFoundIfNull(object);
		System.out.println( "entered show() for topic " + topicid );
		Topic tmp = (Topic) object;
		System.out.println( tmp.title );
		System.out.println( tmp.description );
		List<Tag> tags = tmp.tags;
		User creator = tmp.creator;
		List<User> followers = tmp.followers;
		List<Idea> ideas = tmp.ideas;
		List<Comment> comments = tmp.commentsOn;
		MainEntity entity = tmp.entity;
		Plan plan = tmp.plan;
		boolean openToEdit = tmp.openToEdit;
		short privacyLevel = tmp.privacyLevel;
		String deletemessage = "Are you Sure you want to delete the task ?!";
		boolean deletable = tmp.isDeletable();
        
		

		try
		{
			System.out.println( "show() done, about to render" );
			render( type, object, tags, creator, followers, ideas, comments, entity, plan, openToEdit, privacyLevel, deletemessage, deletable);
		}
		catch( TemplateNotFoundException e )
		{
			System.out.println( "show() done with exception, rendering to CRUD/show.html" );
			render( "CRUD/show.html", type, object );
		}
	}
	
	/**
	 * Overriding the CRUD method list.
	 * 
	 * @author aliaelbolock
	 * 
	 * @story C3S1
	 * 
	 * @param page
	 *         : page of the list we are in
	 * 
	 * @param search
	 *         : search string
	 *         
	 * @param searchFields
	 *        : the fields we want to search
	 *        
	 * @param orderBy
	 *        : criteria to order list by
	 *        
	 * @param order
	 *        : the order of the list
	 * 
	 * @description This method renders the list of topics, with search and sort options
	 * 
	 * @throws Exception
	 * 
	 * @return void
	 */
	public static void list(int page, String search, String searchFields,
			String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		System.out.println( "list() entered ");
		List<Model> objects = type.findPage(page, search, searchFields,
				orderBy, order, (String) request.args.get("where"));
		Long count = type.count(search, searchFields,
				(String) request.args.get("where"));
		Long totalCount = type.count(null, null,
				(String) request.args.get("where"));
		try {
			System.out.println( "list() done, will render ");
			render(type, objects, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			System.out.println( "list() done with exceptions, will render CRUD/list.html ");
			render("CRUD/list.html", type, objects, count, totalCount, page,
					orderBy, order);
		}
	}
	
	/**
	 * closedTopicsList
	 * 
	 * @author aliaelbolock
	 * 
	 * @story C3S1
	 *
	 * @param page
	 *         : page of the list we are in
	 * 
	 * @param search
	 *         : search string
	 *         
	 * @param searchFields
	 *        : the fields we want to search
	 *        
	 * @param orderBy
	 *        : criteria to order list by
	 *        
	 * @param order
	 *        : the order of the list
	 * 
	 * @description This method renders the list of closed topics, with search and sort options
	 * 
	 * @throws Exception
	 * 
	 * @return void
	 */
	public static void closedTopicslist(int page, String search, String searchFields,
			String orderBy, String order) {
			ObjectType type = ObjectType.get(getControllerClass());
			notFoundIfNull(type);
			if (page < 1) {
			page = 1;
			}
			List<Topic> opentopics = Topic.find("openToEdit", false).fetch();
			Long totalCount = (long)  opentopics.size();
			Long count = (long) opentopics.size();
			try {
			render(type, opentopics, count, totalCount, page, orderBy, order);
			} catch (TemplateNotFoundException e) {
			render("CRUD/list.html", type, opentopics, count, totalCount, page,
			orderBy, order);
			}
			}
	

	/**
	 * Overriding the CRUD method save.
	 * 
	 * @author aliaelbolock
	 * 
	 * @story C3S1
	 *
	 * @param topicid
	 *          : id of the topic we're in
	 * 
	 * @description This method renders the form for editing a topic and saving it
	 * 
	 * @throws Exception
	 * 
	 * @return void
	 */
	public static void save( String topicid) throws Exception
	{
		ObjectType type = ObjectType.get( getControllerClass() );
		notFoundIfNull( type );
		Model object = type.findById(topicid);
		notFoundIfNull(object);
		System.out.println( "entered save() for " + topicid );
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		Topic tmp = (Topic) object;
		User myUser = User.find( "byEmail", Security.connected() ).first();
		MainEntity topicEntity = tmp.entity;
		ArrayList<Tag> topicTags = (ArrayList<Tag>) tmp.tags;
		Organization topicOrganization = topicEntity.organization;
		if(!(topicEntity.followers.size() == 0 ||topicOrganization.followers.size() == 0))
			tmp.followers =User.find("byFollowingEntitiesAndFollowingOrganizations", topicEntity,topicOrganization).fetch();
		String message = "";

		if( validation.hasErrors() )
		{
			if( tmp.title.equals( "" ) )
			{
				message ="A Topic must have a title";
			}
			else if( tmp.description.equals("") )
			{
				message = "A Topic must have a description";

			}
			else if( tmp.creator == null)
			{
				message = "A task must have a creator";
				try
				{
					render( request.controller.replace( ".", "/" ) + "/show.html", topicEntity , type, tmp.title, tmp.entity, tmp.description, tmp.followers, tmp.tags, message);
				}
				catch( TemplateNotFoundException e )
				{
					render( "CRUD/show.html", type );
				}
			}
			
			else if( !Users.isPermitted(myUser, "edit", topicEntity.getId(), "entity"))
			{
				message = "Sorry but you are not allowed to edit topics in this entity";
			}
			try
			{
				render( request.controller.replace( ".", "/" ) + "/show.html", topicEntity , type, tmp.title, tmp.entity, tmp.description, tmp.followers, tmp.tags, message);
			}
			catch( TemplateNotFoundException e )
			{
				render( "CRUD/show.html", type );
			}
		}
		if( tmp.plan != null )
		{
			message = "The topic can't be assigned to a plan yet, as it is in the process of being created";
			try
			{
				render( request.controller.replace( ".", "/" ) + "/blank.html", topicEntity , type, tmp.title, tmp.entity, tmp.description, tmp.followers, tmp.tags, message);
			}
			catch( TemplateNotFoundException e )
			{
				render( "CRUD/blank.html", type );
			}
		}
		
		System.out.println( "about to save() topic" );
		object._save();
		Calendar cal = new GregorianCalendar();
		//Logs.addLog( myUser, "add", "Task", tmp.id, tmp.entity.organization, cal.getTime() );
		//String message3 = myUser.username + " has editted the topic " + tmp.title + " in " + tmp.entity;
		//Notifications.notifyUsers( Users.getEntityOrganizers(tmp.entity), "Topic Created", message3, (byte) 1 );
		System.out.println( "save() done, not redirected yet" );
		
		flash.success( Messages.get( "crud.saved", type.modelName, ((Topic) object).getId()) );
		if( params.get( "_save" ) != null )
		{
			redirect("/topics/show?topicid=" + ((Topic) object).getId());
			System.out.println( "save() done, redirected to topics/show?topicid" );
			//redirect( "/storys/liststoriesinproject?projectId=" + tmp.taskStory.componentID.project.id + "&storyId=" + tmp.taskStory.id );
		}
		redirect( request.controller + ".show",  ((Topic) object).getId()) ;
		System.out.println( "save() done, redirected to default show.html" );
	}


}
