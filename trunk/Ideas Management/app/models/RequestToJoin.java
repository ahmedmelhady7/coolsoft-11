package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class RequestToJoin extends Model{
	@ManyToOne
	public User source;
	
	@ManyToOne
	public Topic topic;
	
	@ManyToOne
	public Organization organization;
	
	public String description;
	
	public RequestToJoin(User source, Topic topic, Organization organization, String des) {
		this.source = source;
		this.topic = topic;
		this.organization = organization;
		this.description = des;
	}
}
