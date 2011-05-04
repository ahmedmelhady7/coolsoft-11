//package controllers;
//
//import groovy.ui.text.FindReplaceUtility;
//
//import javax.xml.transform.Source;
//
//import play.db.jpa.Model;
//import models.*;
//
//public class Relationships extends CRUD {
//
//	/**
//	 * This method creates a new relationship between two similar objects and
//	 * render it to the view
//	 * 
//	 * @author Mohamed Hisham
//	 * 
//	 * @param name
//	 *            : name of the relationship
//	 * 
//	 * @param sourceId
//	 *            : id of the first object to be related
//	 * 
//	 * @param destinationId
//	 *            : id of the second object to be related
//	 */
//	public static void createRelationship(String name, long sourceId,
//			long destinationId) {
//		Object source = Model.findById(sourceId);
//		Object destination = Model.findById(destinationId);
//
//		if (source instanceof MainEntity && destination instanceof MainEntity) {
//			MainEntity srcEntity = (MainEntity) source;
//			MainEntity destEntity = (MainEntity) destination;
//
//			if (srcEntity.parent != null && destEntity.parent != null) {
//				Relationship relation = new Relationship(name, source,
//						destination);
//				MainEntitys.relateEntity(srcEntity, destEntity);
//				MainEntitys.relateEntity(destEntity, srcEntity);
//				render(relation);
//
//			} else if (srcEntity.parent == null && destEntity.parent == null) {
//				Relationship relation = new Relationship(name, source,
//						destination);
//
//				MainEntitys.relateEntity(srcEntity, destEntity);
//				MainEntitys.relateEntity(destEntity, srcEntity);
//
//				render(relation);
//
//			} else {
//				System.out.println("Cannot relate Entity to Sub-Entity!!");
//				return;
//			}
//
//		} else if (source instanceof Topic && destination instanceof Topic) {
//			Topic srcTopic = (Topic) source;
//			Topic destTopic = (Topic) destination;
//
//			Relationship relation = new Relationship(name, srcTopic, destTopic);
//
//			Topics.relateTopic(srcTopic, destTopic);
//			Topics.relateTopic(destTopic, srcTopic);
//
//			render(relation);
//
//		} else if (source instanceof Tag && destination instanceof Tag) {
//			Tag srcTag = (Tag) source;
//			Tag destTag = (Tag) destination;
//
//			Relationship relation = new Relationship(name, srcTag, destTag);
//
//			Tags.relateTag(srcTag, destTag);
//			Tags.relateTag(destTag, srcTag);
//
//			render(relation);
//
//		} else {
//			System.out.println("Types don't match!!");
//			return;
//		}
//
//	}
//
//	/**
//	 * this method is used to rename a relationship after its created
//	 * 
//	 * @author Mohamed Hisham
//	 * 
//	 * @param relationToBeRenamedId
//	 *            : the relation id that is being renamed
//	 * 
//	 * @param newName
//	 *            : the new name to be set to the relationship
//	 */
//	public static void renameRelationship(long relationToBeRenamedId,
//			String newName) {
//		Relationship relation = Relationship.findById(relationToBeRenamedId);
//		relation.name = newName;
//
//	}
//
//	public static void show(long relationshipId) {
//		Relationship relationship = Relationship.findById(relationshipId);
//
//		render(relationship);
//	}
//
//	/**
//	 * this method ends a relationship by deleting it
//	 * 
//	 * @author Mohamed Hisham
//	 * 
//	 * @param relationId
//	 *            : the id of the relation being deleted
//	 */
//	public static void delete(long relationId) {
//		Relationship relation = Relationship.findById(relationId);
//		relation.delete();
//
//	}
//
//	/**
//	 * this method checks if the user is able to create relationships or not
//	 * 
//	 * @author Mohamed Hisham
//	 * 
//	 * @param userId
//	 *            : the if of the user being checked of allowance to assign
//	 *            relationships
//	 * 
//	 * @param organizationId
//	 *            : the id of the organization that the user belongs to
//	 * 
//	 * @return boolean value whether the user is allowed(True) or not(False)
//	 */
//
//	public static boolean isAllowedTo(long userId, long organizationId) {
//		User user = User.findById(userId);
//		Organization organization = Organization.findById(organizationId);
//
//		if (user.equals(organization.creator))
//			return true;
//		return false;
//	}
//
//}
