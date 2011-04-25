package models;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;
@Entity
public class Log extends Model{
	public String actionDescription;
	public Timestamp time;
	
	@ManyToOne
	public Organization organization;
	
	
	

}
