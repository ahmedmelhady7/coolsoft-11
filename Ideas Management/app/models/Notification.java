package models;

import java.util.Date;

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
public class Notification extends CoolModel {
	
	// The notification source ID
	public long sourceID;
	
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
	
	public Date time;
	
	public Notification(long sourceID, String type, String title, User user, String description) {
		this.sourceID = sourceID;
		this.type =type;
		this.title = title;
		this.directedTo = user;
		this.description = description;
		this.seen = false;		
		this.status = "New";
		this.time=new Date();
	   
	}
}
