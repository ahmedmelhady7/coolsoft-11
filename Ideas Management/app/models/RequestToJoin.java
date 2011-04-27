package models;

import javax.persistence.ManyToOne;

import play.db.jpa.Model;

public class RequestToJoin extends Model{
	@ManyToOne
	public User source;
	@ManyToOne
	public Topic topic;
	@ManyToOne
	public Organization organization;
	
	public RequestToJoin() {
		
	}
}
