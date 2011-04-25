package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class AssignRequest extends Model {

	@ManyToOne
	public Item source;

	@ManyToOne
	public User destination;

	@ManyToOne
	public User sender;

	@Lob
	public String content;

	public AssignRequest(Item source, User destination, User sender,
			String content) {
		this.source = source;
		this.destination = destination;
		this.sender = sender;
		this.content = content;
	}

}
