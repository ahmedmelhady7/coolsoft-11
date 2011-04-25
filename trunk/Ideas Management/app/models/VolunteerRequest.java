package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class VolunteerRequest extends Model {

	@ManyToOne
	public User sender;

	@ManyToOne
	public Item destination;

	@Lob
	public String justification;

	public VolunteerRequest(User sender, Item destination, String justification) {
		this.sender = sender;
		this.destination = destination;
		this.justification = justification;
	}
}
