package controllers;

import java.lang.reflect.Constructor;
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
 * @author Mohamed
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
//		System.out.println("WASALNA!! " + sourceId + "|" + destinationId);
		
		MainEntity source = MainEntity.findById(sourceId);
		MainEntity destination = MainEntity.findById(destinationId);
		
		EntityRelationship relation = new EntityRelationship(name, source,
				destination);
		relation.save();

		source.relations.add(relation);
		destination.relations.add(relation);
		
		System.out.println("Creation DONE!!");
	//	render(name,source, destination);

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
		relation.delete();

	}

	/**
	 * checks if the user is able to create relationships or not
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @param organizationId
	 *            : the id of the organization that the user belongs to
	 * 
	 * @return boolean value whether the user is allowed(True) or not(False)
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
}
