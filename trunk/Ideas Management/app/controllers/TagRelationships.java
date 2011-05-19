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
public class TagRelationships extends CRUD {

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
	 */
	public static void createRelationship(String name, long sourceId,
			long destinationId) {
		
		Tag source = Tag.findById(sourceId);
		Tag destination = Topic.findById(destinationId);

		TagRelationship relation = new TagRelationship(name, source,
				destination);
		
		relation.source.relationsSource.add(relation);
		relation.destination.relationsDestination.add(relation);
		if(!isDuplicate(name, relation.source.createdInOrganization.relationNames))
			relation.source.createdInOrganization.relationNames.add(name);
		
		relation.save();
		
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
		TagRelationship relation = TagRelationship
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
		TagRelationship relation = TagRelationship.findById(relationId);
		relation.source.relationsSource.remove(relation);
		relation.destination.relationsDestination.remove(relation);
		relation.delete();
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
		if (user == tag.createdInOrganization.creator)
			return true;
		return false;
	}
	
	/**
	 * checks relation name for duplicate
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @param relationName : name of the relation
	 * 
	 * @param relationNames : list of relation names in the organization
	 * 
	 * @return boolean
	 */
	public static boolean isDuplicate(String relationName, ArrayList<String> relationNames){
		for(int i = 0; i < relationNames.size(); i++){
			if(relationName.equals(relationNames.get(i)))
				return true;	
		}
		return false;
	}

}
