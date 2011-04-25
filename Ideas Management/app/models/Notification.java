package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

/**
 * @author ${Ahmed Maged}
 * 
 */

@Entity
public class Notification extends Model {
	
	public String type;
	@ManyToOne
	public User directedTo;
	public boolean seen;
	
	public Notification(String t, User u) {
		type = t;
		directedTo = u;
		seen = false;
	}

}
