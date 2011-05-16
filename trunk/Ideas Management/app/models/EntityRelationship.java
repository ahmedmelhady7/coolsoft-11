package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * This class defines the Relationship between two Entities
 * 
 * @author Mohamed Hisham
 * 
 * @story C2S5
 * 
 */
@Entity
public class EntityRelationship extends Model {

	/**
	 * the name of the Relationship
	 */
	@Required
	public String name;

	/**
	 * the first related Entity
	 */
	@Required
	@ManyToOne
	public MainEntity source;

	/**
	 * the second related Entity
	 */
	@Required
	@ManyToOne
	public MainEntity destination;

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
	 *            : the first related Entity
	 * 
	 * @param destination
	 *            : the second related Entity
	 */
	public EntityRelationship(String name, MainEntity source, MainEntity destination) {
		this.name = name;
		this.source = source;
		this.destination = destination;
	}

}
