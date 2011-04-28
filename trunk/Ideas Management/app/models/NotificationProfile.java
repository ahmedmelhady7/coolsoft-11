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
public class NotificationProfile extends Model {

	// The ID of the source
	public int notifiableId;
	
	// The type of the source
	public String notifiableType;
	
	@ManyToOne
	public User user;
	
	// The preference of the user
	public boolean enabled;
	
	public NotificationProfile(int nId, String nType) {
		notifiableId = nId;
		notifiableType = nType;
		enabled = true;
	}
	
	public void disableNotification() {
		enabled = false;
	}
}
