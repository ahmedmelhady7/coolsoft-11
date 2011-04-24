package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Notification extends Model {
	
	public String type;
	@ManyToOne
	public User directed_to;
	public boolean seen;
	
	public Notification(String t, User u) {
		type = t;
		directed_to = u;
		seen = false;
	}

}
