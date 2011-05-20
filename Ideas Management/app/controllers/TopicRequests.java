package controllers;

import java.lang.reflect.Constructor;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import controllers.CRUD.ObjectType;
import models.MainEntity;
import models.Organization;
import models.RequestToJoin;
import models.Topic;
import models.TopicRequest;
import models.User;
import models.UserRoleInOrganization;

public class TopicRequests extends CRUD{
	
	

	
	 /** Overriding the CRUD method create.
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S15
	 * 
	 * 
	 * @description This method checks for the Validation of the info inserted
	 *              in the Request form of a TopicRequest and if they are valid the object
	 *              is created and saved.
	 *              
	 * @param entityId
	 *             : the id of the entity the request is in
	 *             
	 * @throws Exception
	 * 
	 */
	public static void create(long entityId) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Model object = (Model) constructor.newInstance();
		Binder.bind(object, "object", params.all());
		validation.valid(object);
		String message = "";
		TopicRequest temporaryTopicRequest = (TopicRequest) object; // we temporarily save the object created by
									// the form in temporaryTopicRequest to validate it before
									// saving
		System.out.println("create() entered");
		MainEntity entity = MainEntity.findById(entityId);
		temporaryTopicRequest.entity = entity;
		User requester = Security.getConnected();
		temporaryTopicRequest.requester = requester;
		System.out
				.println("the topic before validation check" + temporaryTopicRequest.toString());

		if (temporaryTopicRequest.entity == null) {
			message = "A Topic must belong to an entity";
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						type, message, entityId);
			} catch (TemplateNotFoundException exception) {
				render("CRUD/blank.html", type, message, entityId);
			}
		}
		System.out
		.println("111");

		
		if (validation.hasErrors()) {
			System.out
			.println("entered validation check");
			if (temporaryTopicRequest.title.equals("")) {
				System.out
				.println(message);
				message = "A Topic must have a title";
				try {
					render(request.controller.replace(".", "/") + "/blank.html",
							entityId, type, temporaryTopicRequest.title, temporaryTopicRequest.entity, temporaryTopicRequest.description,
							temporaryTopicRequest.message, message);
					System.out
					.println("try" + message);
				} 
				
				catch (TemplateNotFoundException exception) {
					render("CRUD/blank.html", type, entityId);
					System.out
					.println("catch" + message);
				}
				
			} else if (temporaryTopicRequest.description.equals("")) {
				message = "A Topic must have a description";
				System.out
				.println(message);
				
				try {
					render(request.controller.replace(".", "/") + "/blank.html",
							entityId, type, temporaryTopicRequest.title, temporaryTopicRequest.entity, temporaryTopicRequest.description,
							temporaryTopicRequest.message, message);
					System.out
					.println("try" + message);
				} 
				
				catch (TemplateNotFoundException exception) {
					render("CRUD/blank.html", type, entityId);
					System.out
					.println("catch" + message);
				}

			}
		}
		
		System.out
		.println("222");

		if (temporaryTopicRequest.privacyLevel < 1 || temporaryTopicRequest.privacyLevel > 2) {
			message = "The privary level must be either 1 or 2";
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						entityId, type, temporaryTopicRequest.title, temporaryTopicRequest.entity, temporaryTopicRequest.description,
						temporaryTopicRequest.message, message);
			} catch (TemplateNotFoundException exception) {
				render("CRUD/blank.html", type, entityId);
			}

		}
		
		System.out
		.println("333");

		System.out.println("create() about to save object");
		object._save();
		TopicRequest topicRequest = (TopicRequest) object;
		entity.topicRequests.add(topicRequest);
		requester.topicRequests.add(topicRequest);
		System.out.println("create() object saved");
		message = "Your request has been sent... an organizer will review it shortly";
		
		flash.success(Messages.get("crud.created", type.modelName,
				((TopicRequest) object).getId()));
		if (params.get("_save") != null) {
			redirect("mainentitys.viewentity", entityId, message);
		}
	
	}

	/**
	 * Overriding the CRUD method blank.
	 * 
	 * @author Alia el Bolock
	 * 
	 * @story C3S15
	 * 
	 * @param entityId
	 *            : id of the entity the request is in
	 * 
	 * @description This method renders the form for requesting a topic in the
	 *              entity
	 * 
	 */
	public static void blank(long entityId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		MainEntity entity = MainEntity.findById(entityId);
		User user = Security.getConnected();
		System.out.println("blank() for TopicRequests entered entity " + entityId + " and user "
				+ user.toString());
		try {
			System.out.println("blank() for TopicRequests done about to render");
			render(type, entityId, user);

		} catch (TemplateNotFoundException exception) {
			System.out
					.println("blank() for TopicRequests done with exception about to render CRUD/blank.html");
			render("CRUD/blank.html", type, entityId);
		}

	}
}


