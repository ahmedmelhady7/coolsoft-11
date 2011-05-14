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
public class TopicRelationship extends Model {

	/**
	 * the name of the Relationship
	 */
	@Required
	public String name;

	/**
	 * the first related Topic
	 */
	@Required
	public Topic source;

	/**
	 * the second related Topic
	 */
	@Required
	public Topic destination;

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
	 *            : the first related Topic
	 * 
	 * @param destination
	 *            : the second related Topic
	 */
	public TopicRelationship(String name, Topic source, Topic destination) {
		this.name = name;
		this.source = source;
		this.destination = destination;
		source.entity.organization.relationNames.add(name);
	}

}
