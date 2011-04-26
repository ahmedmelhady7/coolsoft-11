package models;

import javax.persistence.*;

import play.db.jpa.Model;

/**
 * @author ${Ibrahim Safwat}
 * 
 */

/**
* Comments to be posted on ideas/plans/requests
*/

@Entity
public class Comment extends Model {
	
	String comment;
	
	//public int ID;
	
	@ManyToOne 
	public Topic commentedTopic;
		
	//@OneToMany
	String postedOn;
	
	//@OneToOne
	//User commenter;
	
	public Comment(String comment, int ID, String postedOn, User commenter)
	{
		this.comment=comment;
		//this.ID=ID;
		this.postedOn=postedOn;
		//this.commenter=commenter;
	}

}
