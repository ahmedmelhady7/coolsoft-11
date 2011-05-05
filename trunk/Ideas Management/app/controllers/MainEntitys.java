package controllers;

import java.util.List;

import controllers.CRUD.ObjectType;

import play.data.validation.Required;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.With;
import models.MainEntity;
import models.Organization;
import models.Topic;
import models.User;

@With(Secure.class)
public class MainEntitys extends CRUD {

	/**
	 * The method that allows a user to follow a certain entity
	 * 
	 * @author Noha Khater
	 * 
	 * @Stroy C2S10
	 * 
	 * @param entityId
	 *            : The id of the entity that the user wants to follow
	 * 
	 */

	public static void followEntity(long entityId) {
		User user = Security.getConnected();
		MainEntity e = MainEntity.findById(entityId);
		if (Users.isPermitted(user, "follow", entityId, "entity")) {
			e.followers.add(user);
			user.followingEntities.add(e);
		} else {
			System.out.println("Sorry! Action cannot be performed");
		}
	}

	/**
	 * This method adds entity2 to the list of entities in entity
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @param entity
	 *            : first entity to be related
	 * 
	 * @param entity2
	 *            : second entity to be related
	 */
	public static void relateEntity(MainEntity entity, MainEntity entity2) {
//		entity.relatedEntities.add(entity2);
	}

	 public static void createEntity(String name, String description, 
			 long orgId) {
		 Organization org = Organization.findById(orgId);
		 MainEntity entity = new MainEntity(name, description, org);
		 entity.save();
	 }
	 
	 public static void createSubEntity(String name, String description, 
			 long parentId, long orgId) {
		 Organization org = Organization.findById(orgId);
		 MainEntity parent = MainEntity.findById(parentId); 
		 MainEntity entity = new MainEntity(name, description, parent, org);
		 entity.save();
	 }
	 
	 public static void goToCreateEntity(long orgid) {
		 Organization org = Organization.findById(orgid);
		 render(org);
		 }
	 
	 public static void goToCreateSubEntity(long orgid, long pId) {
		 Organization org = Organization.findById(orgid);
		 MainEntity parent = MainEntity.findById(pId);
		 render(org, parent);
		 }

	public static void viewEntity(long id) {
		User user = Security.getConnected();
		MainEntity entity = MainEntity.findById(id);
		Organization org = entity.organization;
		List<MainEntity> subentities = entity.subentities;
		List<Topic> topicList = entity.topicList;
		int permission =1;
		int invite=0;

				 
				 if( !Users.isPermitted(user , "post topics",
						 entity.id, "entity")) {
					 permission =0;
				 }
				 if(Users.isPermitted(user , "invite Organizer or Idea Developer to become Organizer or Idea Developer in an entity he/she manages",
						 entity.id, "entity")) {
					 invite =1;
				 }
		
		render(user, org, entity, subentities, topicList, permission,invite);
	}
	

}
