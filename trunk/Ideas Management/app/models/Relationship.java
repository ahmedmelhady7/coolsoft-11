//package models;
//
//import java.util.Date;
//
//import play.db.jpa.Model;
//
///**
// * This class defines the Relationship between two Entities/Topics/Tags
// * 
// * @author Mohamed Hisham
// * 
// */
//public class Relationship extends Model {
//
//	/**
//	 * the name of the Relationship
//	 */
//	public String name;
//
//	/**
//	 * the first related Entity/Topic/Tag
//	 */
//	public Object source;
//
//	/**
//	 * the second related Entity/Topic/Tag
//	 */
//	public Object destination;
//
//	/**
//	 * The constructor for assigning Relationships
//	 * 
//	 * @author Mohamed Hisham
//	 * 
//	 * @param name
//	 *            : the name of the Relationship
//	 * 
//	 * @param source
//	 *            : the first related Entity/Topic/Tag
//	 * 
//	 * @param destination
//	 *            : the second related Entity/Topic/Tag
//	 */
//	public Relationship(String name, Object source, Object destination) {
//		this.name = name;
//		this.source = source;
//		this.destination = destination;
//	}
//
//}
