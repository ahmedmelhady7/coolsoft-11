package controllers;

import java.util.ArrayList;

import groovy.ui.text.FindReplaceUtility;

import javax.xml.transform.Source;

import play.db.jpa.Model;
import models.*;

/**
 * @author Mohamed
 * 
 * @story C2S5
 * 
 */
public class TagRelationships extends CoolCRUD {

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
	 *            : id of the first Tag to be related
	 * 
	 * @param destinationId
	 *            : id of the second Tag to be related
	 * 
	 * @return boolean
	 */
	public static boolean createRelationship(String name, long sourceId,
			long destinationId) {

		Tag source = Tag.findById(sourceId);
		notFoundIfNull(source);
		Tag destination = Tag.findById(destinationId);
		notFoundIfNull(destination);
		TagRelationship relation = new TagRelationship(name, source,
				destination);
		Organization organization = relation.source.createdInOrganization;
		if (!relationDuplicate(relation)) {
			relation.source.relationsSource.add(relation);
			relation.destination.relationsDestination.add(relation);
			
			Organizations.addRelationName(organization.id, name);


			organization.save();
			relation.save();

			if (!source.creator.equals(destination.creator)) {
				Notifications.sendNotification(source.creator.id,
						source.createdInOrganization.id, "Organization",
						"a new relation \"" + name
								+ "\" is created now between tags \""
								+ source.name + "\" and \"" + destination.name
								+ "\".");
			}

			Notifications.sendNotification(destination.creator.id,
					source.createdInOrganization.id, "Organization",
					"a new relation \"" + name
							+ "\" is created now between tags \"" + source.name
							+ "\" and \"" + destination.name + "\".");

			Log.addUserLog("<a href=\"/Users/viewProfile?userId="
					+ Security.getConnected().id + "\">"
					+ Security.getConnected().username + "</a>"
					+ "created a relationship " + name
					+ " between tags " + "<a href=\"/Tags/mainPage?tagId="
					+ source.id + "\">" + source.name
					+ "</a>" + "and" + "<a href=\"/Tags/mainPage?tagId="
					+ destination.id + "\">" + destination.name + "</a>",
					Security.getConnected(), source, destination,
					source.createdInOrganization);

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
	public static boolean relationDuplicate(TagRelationship relation) {
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
		TagRelationship relation = TagRelationship
				.findById(relationToBeRenamedId);
		notFoundIfNull(relation);
		TagRelationship relationTemp = new TagRelationship(newName,
				relation.source, relation.destination);
		if (relation.name != newName) {
			if (!relationDuplicate(relationTemp)) {
				Log.addUserLog("<a href=\"/Users/viewProfile?userId="
						+ Security.getConnected().id + "\">"
						+ Security.getConnected().username+ "</a>"
						+ " renamed the relationship " + relation.name
						+ " between tags "+ "<a href=\"/Tags/mainPage?tagId="
						+ relation.source.id + "\">" + relation.source.name
						+ "</a>" + "and"  + "<a href=\"/Tags/mainPage?tagId="
						+ relation.destination.id + "\">"
						+ relation.destination.name + "</a>" + "to" + "" + newName
						, Security.getConnected(), relation.source,
						relation.destination,
						relation.source.createdInOrganization);
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
	 * @return boolean
	 */
	public static boolean delete(long relationId) {
		TagRelationship relation = TagRelationship.findById(relationId);
		notFoundIfNull(relation);
		Log.addUserLog("<a href=\"/Users/viewProfile?userId="
				+ Security.getConnected().id + "\">"
				+ Security.getConnected().username + "</a>"
				+ " deleted the relationship " + relation.name
				+ " between tags " + "<a href=\"/Tags/mainPage?tagId="
				+ relation.source.id + "\">" + relation.source.name
				+ "</a>" + "and" + "<a href=\"/Tags/mainPage?tagId="
				+ relation.destination.id + "\">" + relation.destination.name
				+ "</a>", Security.getConnected(), relation.source,
				relation.destination, relation.source.createdInOrganization);
		relation.source.relationsSource.remove(relation);
		relation.destination.relationsDestination.remove(relation);
		relation.delete();
		return true;
	}

	/**
	 * checks if the user is able to create relationships or not
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @param tagId
	 *            : the id of the tag that the user belongs to
	 * 
	 * @return boolean
	 */

	public static boolean isAllowedTo(long tagId) {
		User user = Security.getConnected();
		Tag tag = Tag.findById(tagId);
		notFoundIfNull(tag);
		if (user == tag.createdInOrganization.creator || user.isAdmin)
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
}
