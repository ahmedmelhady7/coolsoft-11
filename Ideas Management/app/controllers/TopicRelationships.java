package controllers;

import java.util.ArrayList;
import java.util.List;

import groovy.ui.text.FindReplaceUtility;

import javax.xml.transform.Source;

import play.db.jpa.Model;
import models.*;

/**
 * @author Mohamed Hisham
 * 
 * @story C2S5
 * 
 */
public class TopicRelationships extends CoolCRUD {

	/**
	 * creates a new relationship between two Topics
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S5
	 * 
	 * @param name
	 *            : name of the relationship
	 * 
	 * @param sourceId
	 *            : id of the first Topic to be related
	 * 
	 * @param destinationId
	 *            : id of the second Topic to be related
	 * 
	 * @return boolean
	 */
	public static boolean createRelationship(String name, long sourceId,
			long destinationId) {
		Topic source = Topic.findById(sourceId);
		Topic destination = Topic.findById(destinationId);

		TopicRelationship relation = new TopicRelationship(name, source,
				destination);
		if (!relationDuplicate(relation)) {
			relation.source.relationsSource.add(relation);
			relation.destination.relationsDestination.add(relation);
			if (!isDuplicate(name,
					relation.source.entity.organization.relationNames))
				relation.source.entity.organization.relationNames.add(name);

			relation.save();
			Organization organization = relation.source.entity.organization;
			organization.save();

			if (!relation.source.entity.equals(relation.destination.entity)) {
				for (int i = 0; i < Users.getEntityOrganizers(source.entity)
						.size(); i++) {
					Notifications.sendNotification(Users.getEntityOrganizers(
							source.entity).get(i).id,
							source.entity.organization.id, "Organization",
							"a new relation \"" + name
									+ "\" is created now between topics \""
									+ source.title + "\" and \""
									+ destination.title + "\".");
				}
			}
			for (int i = 0; i < Users.getEntityOrganizers(destination.entity)
					.size(); i++) {
				Notifications.sendNotification(Users.getEntityOrganizers(
						destination.entity).get(i).id,
						source.entity.organization.id, "Organization",
						"a new relation \"" + name
								+ "\" is created now between topics \""
								+ source.title + "\" and \""
								+ destination.title + "\".");
			}
			Log.addUserLog("User \"" + Security.getConnected().firstName + " "
					+ Security.getConnected().lastName + "\" "
					+ "created a relationship \"" + name
					+ "\" between topics \"" + source.title + "\" and \""
					+ destination.title + "\"", Security.getConnected(),
					source, destination, source.entity,
					source.entity.organization);
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
	public static boolean relationDuplicate(TopicRelationship relation) {
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
		TopicRelationship relation = TopicRelationship
				.findById(relationToBeRenamedId);
		if (relation.name != newName) {
			Log.addUserLog("User \"" + Security.getConnected().firstName + " "
					+ Security.getConnected().lastName + "\" "
					+ "renamed the relationship \"" + relation.name
					+ "\" between topics \"" + relation.source.title
					+ "\" and \"" + relation.destination.title + "\" to \""
					+ newName + "\"", Security.getConnected(), relation.source,
					relation.destination, relation.source.entity,
					relation.source.entity.organization);
			relation.name = newName;
			for (int i = 0; i < relation.renameEndRequests.size(); i++) {
				RenameEndRelationshipRequests.delete(relation.renameEndRequests
						.get(i).id);
			}
			relation.save();

			return true;
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
	 * @return boolean
	 */
	public static boolean delete(long relationId) {
		TopicRelationship relation = TopicRelationship.findById(relationId);
		Log.addUserLog("User \"" + Security.getConnected().firstName + " "
				+ Security.getConnected().lastName + "\" "
				+ "deleted the relationship \"" + relation.name
				+ "\" between topics \"" + relation.source.title + "\" and \""
				+ relation.destination.title + "\"", Security.getConnected(),
				relation.source, relation.destination, relation.source.entity,
				relation.source.entity.organization);
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
	 * @param topicId
	 *            : the id of the organization that the user belongs to
	 * 
	 * @return boolean
	 */

	public static boolean isAllowedTo(long topicId) {
		User user = Security.getConnected();
		if (Users
				.isPermitted(
						user,
						"create relationships between entities/sub-entities/topics/tags",
						topicId, "topic"))
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

	public static boolean deleteTR(long id) {

		TopicRelationship tr = TopicRelationship.findById(id);
		List<MainEntity> entities = MainEntity.findAll();
		int size = entities.size();
		for (int i = 0; i < size; i++) {
			if (entities.get(i).relationsSource.contains(tr)) {
				entities.get(i).relationsSource.remove(tr);
				entities.get(i).save();
			}
			if (entities.get(i).relationsDestination.contains(tr)) {
				entities.get(i).relationsDestination.remove(tr);
				entities.get(i).save();
			}
		}
		size = tr.renameEndRequests.size();
		for (int i = 0; i < size; i++) {
			RenameEndRelationshipRequests
					.delete(tr.renameEndRequests.get(i).id);
		}
		tr.delete();
		return true;
	}

}
