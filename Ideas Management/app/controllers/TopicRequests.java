package controllers;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.IFNONNULL;

import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import controllers.CoolCRUD.ObjectType;
import models.Log;
import models.MainEntity;
import models.Organization;
import models.RequestToJoin;
import models.Topic;
import models.TopicRequest;
import models.User;
import models.UserRoleInOrganization;

public class TopicRequests extends CoolCRUD{
	
	
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
									                                // the form in temporaryTopicRequest to validate
		                                                            //it before saving
		MainEntity entity = MainEntity.findById(entityId);
		temporaryTopicRequest.entity = entity;
		User user = Security.getConnected();
		temporaryTopicRequest.requester = user;
		if (temporaryTopicRequest.entity == null) {
			message = "A Topic must belong to an entity";
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						type, message, entityId);
			} catch (TemplateNotFoundException exception) {
				render("CRUD/blank.html", type, message, entityId);
			}
		}
		if (validation.hasErrors()) {
			if (temporaryTopicRequest.title.equals("")) {
				message = "A Topic must have a title";
				try {
					render(request.controller.replace(".", "/") + "/blank.html",
							entityId, type, temporaryTopicRequest.title, temporaryTopicRequest.entity, temporaryTopicRequest.description,
							temporaryTopicRequest.message, message);
				} 
				
				catch (TemplateNotFoundException exception) {
					render("CRUD/blank.html", type, entityId);
				}
				
			} else if (temporaryTopicRequest.description.equals("")) {
				message = "A Topic must have a description";
				
				try {
					render(request.controller.replace(".", "/") + "/blank.html",
							entityId, type, temporaryTopicRequest.title, temporaryTopicRequest.entity, temporaryTopicRequest.description,
							temporaryTopicRequest.message, message);
				} 
				
				catch (TemplateNotFoundException exception) {
					render("CRUD/blank.html", type, entityId);
				}

			}
		}
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
		
		String logDescription = "<a href=\"/Users/viewProfile?userId=" + user.id +"\">" 
		+ user.username +  "</a>"
        + " requested a topic " +"<a href=\"/Topics/show?topicId=" + temporaryTopicRequest.id 
        +"\">" + temporaryTopicRequest.title + "</a>"
        + " in entity " + "<a href=\"/MainEntitys/viewEntity?id=" 
        + entity.id +"\">" + entity.name + "</a>";
		Log.addUserLog(logDescription, temporaryTopicRequest, user, entity,
				entity.organization);
		
		object._save();
		TopicRequest topicRequest = (TopicRequest) object;
		entity.topicRequests.add(topicRequest);
		user.topicRequests.add(topicRequest);
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
	public static void requestsBlank(long entityId) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		MainEntity entity = MainEntity.findById(entityId);
		notFoundIfNull(entity);
		User user = Security.getConnected();
		int canRequest = 0;
		if (Users.isPermitted(user, "use", entity.id, "entity"))
			canRequest = 1;
		
		if(canRequest==1){
		try {
			render(type, entityId, user);

		} catch (TemplateNotFoundException exception) {
			render("CRUD/blank.html", type, entityId);
		}
		}
	}


/**
 * 
 * @author Mostafa Yasser El Monayer
 * 
 * @story C3S5
 * 
 * @param entityId
 *            : id of the entity the request is in
 * 
 * @description This method renders the list of topic requests related to a given entity
 * 
 */
public static void requestsList(long entityId) {
	MainEntity entity = MainEntity.findById(entityId);
	User user = Security.getConnected();
	List<TopicRequest> listOfTopicsToBeRendered = new ArrayList<TopicRequest>(); 
	List<TopicRequest> listOfTopicRequests = TopicRequest.findAll();
	for (int i =0; i < listOfTopicRequests.size(); i++){
		if (listOfTopicRequests.get(i).entity.equals(entity)){
			listOfTopicsToBeRendered.add(listOfTopicRequests.get(i));
		}
	}
	try {
		if(entity != null){
		String entityName = entity.name;
		render(entityId, user, entityName, listOfTopicsToBeRendered);}
		else{
			render(entityId, user, listOfTopicsToBeRendered);
		}
		

	} catch (TemplateNotFoundException exception) {
		render("CRUD/list.html", entityId, user);
	}

}

/**
 * 
 * @author Mostafa Yasser El Monayer
 * 
 * @story C3S5
 * 
 * @param topicRequestId
 *            : id of the topic request to be related
 * 
 * @param topicDescription
 *            : description of the topic request in case it was rephrased
 *            
 * @descritpion the method takes topic request as input and its description in case it is rephrased as inputs,
 * 				creates a new topic with the same attributes as the topic request, and deletes it from the DB 
 * 
 */

public static void acceptRequest(long topicRequestId, String topicDescription) {
	TopicRequest request = (TopicRequest) TopicRequest.findById(topicRequestId);
	Topic topic = new Topic(request.title, topicDescription, request.privacyLevel, request.requester, request.entity, true);
	topic.save();
	MainEntity entity = request.entity;
	request.delete();
	entity.save();
	Notifications.sendNotification(
			request.requester.id, topic.id, "topic",
			"The topic request named " + request.title + " has been accepted.");
	String logDescription = "<a href=\"/Users/viewProfile?userId=" 
		+ Security.getConnected().id +"\">" + Security.getConnected().username + "</a>"
    + " accepted a topic request with the title " + request.title +" in <a href=\"/MainEntitys/viewEntity?id=" 
    + request.entity.id +"\">" +  request.entity.name + "</a>";
	Log.addUserLog(logDescription, request.entity, request.entity.organization, Security.getConnected());
}

/**
 * 
 * @author Mostafa Yasser El Monayer
 * 
 * @story C3S5
 * 
 * @param topicRequestId
 *            : id of the topic request to be related
 *            
 * @descritpion the method takes topic request as input and deletes it from the DB 
 * 
 */

public static void rejectRequest(long topicRequestId) {
	TopicRequest request = (TopicRequest) TopicRequest.findById(topicRequestId);
	MainEntity entity = request.entity;
	request.delete();
	entity.save();
	Notifications.sendNotification(
			request.requester.id, entity.id, "entity",
			"The topic request named " + request.title + " has been rejected.");
	String logDescription = "<a href=\"/Users/viewProfile?userId=" 
		+ Security.getConnected().id +"\">" + Security.getConnected().username + "</a>"
    + " rejected a topic request with the title " + request.title +" in <a href=\"/MainEntitys/viewEntity?id=" 
    + request.entity.id +"\">" +  request.entity.name + "</a>";
	Log.addUserLog(logDescription, request.entity, request.entity.organization, Security.getConnected());
}

}

