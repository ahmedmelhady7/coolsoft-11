package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class NotificationProfile extends Model {

	public int notifiableId;
	public String notifiableType;
	@ManyToOne
	public User user;
	public boolean enabled;
	
	public NotificationProfile(int nId, String nType) {
		notifiableId = nId;
		notifiableType = nType;
		enabled = true;
	}
}
