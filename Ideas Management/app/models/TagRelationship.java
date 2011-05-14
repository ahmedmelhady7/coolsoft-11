package models;

import java.util.Date;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * This class defines the Relationship between two Tags
 * 
 * @author Mohamed Hisham
 * 
 * @story C2S5
 * 
 */
@Entity
public class TagRelationship extends Model {

	/**
	 * the name of the Relationship
	 */
	@Required
	public String name;

	/**
	 * the first related Tag
	 */
	@Required
	public Tag source;

	/**
	 * the second related Tag
	 */
	@Required
	public Tag destination;

	/**
	 * The constructor for assigning Relationships
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S5
	 * 
	 * @param name
	 *            : the name of the Relationship
	 * 
	 * @param source
	 *            : the first related Tag
	 * 
	 * @param destination
	 *            : the second related Tag
	 */
	public TagRelationship(String name, Tag source, Tag destination) {
		this.name = name;
		this.source = source;
		this.destination = destination;
		source.createdInOrganization.relationNames.add(name);
	}

}
