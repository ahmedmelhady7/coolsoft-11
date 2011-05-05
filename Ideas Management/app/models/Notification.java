package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

/**
 * This class represents the notification entity, whenever a notification is sent
 * it is stored in this model
 * 
 * @author Ahmed Maged
 * 
 */

@Entity
public class Notification extends Model {
	
	// The notification type
	public String type;
	
	// The title of the notification
	public String title;
	
	@Lob
	public String description;
	
	// The status of the notification
	public String status;
	
	@ManyToOne
	public User directedTo;
	
	// The status of the notification
	public boolean seen;
	
	public Notification(String t, String ti, User u, String desc) {
		type = t;
		title = ti;
		directedTo = u;
		description = desc;
		seen = false;
		status = "New";
	}

}
