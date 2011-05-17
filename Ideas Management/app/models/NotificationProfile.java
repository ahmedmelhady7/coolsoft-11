package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

/**
 * This class represents the user's notification profile entity and his preferences
 * 
 * @author Ahmed Maged
 * 
 */

@Entity
public class NotificationProfile extends CoolModel {

	// The ID of the source
	public long notifiableId;
	
	// The type of the source
	public String notifiableType;
	
	// The title of the notification related to the profile
	public String title;
	
	@ManyToOne
	public User user;
	
	// The preference of the user
	public boolean enabled;
	
	
	// Constructor for the NotificationProfile
	public NotificationProfile(long notifiableId, String notifiableType, String title, User user) {
		this.notifiableId = notifiableId;
		this.notifiableType = notifiableType;
		this.title = title;
		this.user = user;
		this.enabled = true;
	}
}
