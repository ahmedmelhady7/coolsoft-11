package controllers;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import groovy.ui.text.FindReplaceUtility;

import javax.xml.transform.Source;

import controllers.CRUD.ObjectType;

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
public class EntityRelationships extends CRUD {

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
	 */
	public static void createRelationship(String name, long sourceId,
			long destinationId) {
		// System.out.println("WASALNA!! " + sourceId + "|" + destinationId);

		MainEntity source = MainEntity.findById(sourceId);
		MainEntity destination = MainEntity.findById(destinationId);

		EntityRelationship relation = new EntityRelationship(name, source,
				destination);
		relation.source.relationsSource.add(relation);
		relation.destination.relationsDestination.add(relation);
		if (!isDuplicate(name, relation.source.organization.relationNames))
			relation.source.organization.relationNames.add(name);

		relation.save();
		Organization organization = relation.source.organization;
		organization.save();

		for (int i = 0; i < Users.getEntityOrganizers(source).size(); i++) {
			Notifications.sendNotification(Users.getEntityOrganizers(source)
					.get(i).id, source.organization.id, "Organization",
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

		System.out.println("Creation DONE!!");
		// render(name,source, destination);

		// } else {
		// System.out.println("Cannot relate Entity to Sub-Entity!!");
		// return;
		// }

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
	 */
	public static void renameRelationship(long relationToBeRenamedId,
			String newName) {
		EntityRelationship relation = EntityRelationship
				.findById(relationToBeRenamedId);
		relation.name = newName;
		relation.save();

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
	 */
	public static void delete(long relationId) {
		EntityRelationship relation = EntityRelationship.findById(relationId);
		relation.source.relationsSource.remove(relation);
		relation.destination.relationsDestination.remove(relation);
		relation.delete();
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
}
