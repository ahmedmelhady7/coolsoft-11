package models;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
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
	
	public int ID;
		
	@OneToMany
	String postedOn;
	
	@OneToMany
	User commenter;
	
	public Comment(String comment, int ID, String postedOn, User commenter)
	{
		this.comment=comment;
		this.ID=ID;
		this.postedOn=postedOn;
		this.commenter=commenter;
	}

}
