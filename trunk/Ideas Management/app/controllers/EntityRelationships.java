package controllers;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import groovy.ui.text.FindReplaceUtility;

import javax.xml.transform.Source;

import controllers.CoolCRUD.ObjectType;

import play.data.binding.Binder;
import play.db.jpa.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.With;
import models.*;

/**
 * @author Mohamed Hisham
 * 
 * @story C2S5
 * 
 */
@With(Secure.class)
public class EntityRelationships extends CoolCRUD {

	/**
	 * creates a new relationship between two Entities
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S5
	 * 
	 * @param name
	 *            : name of the relationship
	 * 
	 * @param sourceId
	 *            : id of the first Entity to be related
	 * 
	 * @param destinationId
	 *            : id of the second Entity to be related
	 * 
	 * @return boolean
	 */
	public static boolean createRelationship(String name, long sourceId,
			long destinationId) {

		MainEntity source = MainEntity.findById(sourceId);
		MainEntity destination = MainEntity.findById(destinationId);

		EntityRelationship relation = new EntityRelationship(name, source,
				destination);
		if (!relationDuplicate(relation)) {
			relation.source.relationsSource.add(relation);
			relation.destination.relationsDestination.add(relation);
			if (!isDuplicate(name, relation.source.organization.relationNames))
				relation.source.organization.relationNames.add(name);

			relation.save();
			Organization organization = relation.source.organization;
			organization.save();

			for (int i = 0; i < Users.getEntityOrganizers(source).size(); i++) {
				Notifications.sendNotification(Users
						.getEntityOrganizers(source).get(i).id,
						source.organization.id, "Organization",
						"a new relation \"" + name
								+ "\" is created now between entities \""
								+ source.name + "\" and \"" + destination.name
								+ "\".");
			}
			for (int i = 0; i < Users.getEntityOrganizers(destination).size(); i++) {
				Notifications.sendNotification(Users.getEntityOrganizers(
						destination).get(i).id, source.organization.id,
						"Organization", "a new relation \"" + name
								+ "\" is created now between entities \""
								+ source.name + "\" and \"" + destination.name
								+ "\".");
			}
			Log
					.addUserLog(
							"User \"<a href=\"/users/viewprofile?userId="
									+ Security.getConnected().id
									+ "\">"
									+ Security.getConnected().firstName
									+ " "
									+ Security.getConnected().lastName
									+ "</a>\" "
									+ "created a relationship \""
									+ name
									+ "\" between entities \"<a href=\"/mainentitys/viewentity?id="
									+ source.id
									+ "\">"
									+ source.name
									+ "\"</a> and \"<a href=\"/mainentitys/viewentity?id="
									+ destination.id + "\">" + destination.name
									+ "</a>\"", Security.getConnected(),
							source, destination, source.organization);
			return true;
		}
		return false;
	}

	/**
	 * checks if a relation is duplicate(returns true) or not(returns false)
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @param relation
	 *            : the relation being checked for duplicate
	 * 
	 * @return boolean
	 */
	public static boolean relationDuplicate(EntityRelationship relation) {
		for (int i = 0; i < relation.source.relationsSource.size(); i++) {
			if (relation.source.relationsSource.get(i).source
					.equals(relation.source)
					&& relation.source.relationsSource.get(i).destination
							.equals(relation.destination)
					&& relation.source.relationsSource.get(i).name
							.equals(relation.name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * renames a relationship after its created
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S6
	 * 
	 * @param relationToBeRenamedId
	 *            : the relation id that is being renamed
	 * 
	 * @param newName
	 *            : the new name to be set to the relationship
	 * 
	 * @return boolean
	 */
	public static boolean renameRelationship(long relationToBeRenamedId,
			String newName) {
		EntityRelationship relation = EntityRelationship
				.findById(relationToBeRenamedId);
		EntityRelationship relationTemp = new EntityRelationship(newName,
				relation.source, relation.destination);
		if (relation.name != newName) {
			if (!relationDuplicate(relationTemp)) {
				Log
						.addUserLog(
								"User \"<a href=\"/users/viewprofile?userId="
										+ Security.getConnected().id
										+ "\">"
										+ Security.getConnected().firstName
										+ " "
										+ Security.getConnected().lastName
										+ "</a>\" "
										+ "renamed the relationship \""
										+ relation.name
										+ "\" between entities \"<a href=\"/mainentitys/viewentity?id="
										+ relation.source.id
										+ "\">"
										+ relation.source.name
										+ "</a>\" and \"<a href=\"/mainentitys/viewentity?id="
										+ relation.destination.id + "\">"
										+ relation.destination.name
										+ "</a>\" to \"" + newName + "\"",
								Security.getConnected(), relation.source,
								relation.destination,
								relation.source.organization);
				for (int i = 0; i < relation.renameEndRequests.size(); i++) {
					RenameEndRelationshipRequests
							.delete(relation.renameEndRequests.get(i).id);
				}
				relation.name = newName;
				relation.save();
				return true;
			}
		}
		return false;
	}

	/**
	 * ends a relationship by deleting it
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S7
	 * 
	 * @param relationId
	 *            : the id of the relation being deleted
	 * 
	 * @return boolean
	 */
	public static boolean delete(long relationId) {
		EntityRelationship relation = EntityRelationship.findById(relationId);
		Log.addUserLog("User \"<a href=\"/users/viewprofile?userId="
				+ Security.getConnected().id + "\">"
				+ Security.getConnected().firstName + " "
				+ Security.getConnected().lastName + "</a>\" "
				+ "deleted the relationship \"" + relation.name
				+ "\" between entities \"<a href=\"/mainentitys/viewentity?id="
				+ "\">" + relation.source.name
				+ "</a>\" and \"<a href=\"/mainentitys/viewentity?id="
				+ relation.destination.id + "\">" + relation.destination.name
				+ "</a>\"", Security.getConnected(), relation.source,
				relation.destination, relation.source.organization);
		relation.source.relationsSource.remove(relation);
		relation.destination.relationsDestination.remove(relation);
		for (int i = 0; i < relation.renameEndRequests.size(); i++) {
			RenameEndRelationshipRequests.delete(relation.renameEndRequests
					.get(i).id);
		}
		relation.delete();
		return true;
	}

	/**
	 * checks if the user is able to create relationships or not
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @param entityId
	 *            : the id of the entity that the user belongs to
	 * 
	 * @return boolean
	 */

	public static boolean isAllowedTo(long entityId) {
		User user = Security.getConnected();
		if (Users
				.isPermitted(
						user,
						"create relationships between entities/sub-entities/topics/tags",
						entityId, "entity"))
			return true;
		return false;
	}

	/**
	 * checks relation name for duplicate
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @param relationName
	 *            : name of the relation
	 * 
	 * @param relationNames
	 *            : list of relation names in the organization
	 * 
	 * @return boolean
	 */
	public static boolean isDuplicate(String relationName,
			ArrayList<String> relationNames) {
		for (int i = 0; i < relationNames.size(); i++) {
			if (relationName.equals(relationNames.get(i)))
				return true;
		}
		return false;
	}

	// public static boolean deleteER(long rId) {
	// EntityRelationship er = EntityRelationship.findById(rId);
	// List<MainEntity> entities = MainEntity.findAll();
	// int size = entities.size();
	// for (int i = 0; i < size; i++) {
	// if (entities.get(i).relationsSource.contains(er)) {
	// entities.get(i).relationsSource.remove(er);
	// entities.get(i).save();
	// }
	// if (entities.get(i).relationsDestination.contains(er)) {
	// entities.get(i).relationsDestination.remove(er);
	// entities.get(i).save();
	// }
	// }
	// size = er.renameEndRequests.size();
	// for (int i = 0; i < size; i++) {
	// RenameEndRelationshipRequests
	// .delete(er.renameEndRequests.get(i).id);
	// }
	// er.delete();
	// return true;
	// }
}
