package models;

import java.util.Date;
import javax.persistence.*;

import play.data.validation.Required;
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

	@Required
	public String comment;

	@Required
	public Date commentDate;

	@ManyToOne
	public Topic commentedTopic;

	@ManyToOne
	public Idea commentedIdea;

	@ManyToOne
	public Plan commentedPlan;

	@OneToOne
	public User commenter;

	public Comment(String comment, Topic commentedTopic, User commenter) {
		this.comment = comment;
		this.commentedTopic = commentedTopic;
		this.commenter = commenter;
		this.commentDate = new Date();
	}

	public Comment(String comment, Plan commentedPlan, User commenter) {
		this.comment = comment;
		this.commentedPlan = commentedPlan;
		this.commenter = commenter;
		this.commentDate = new Date();
	}

	public Comment(String comment, Idea commentedIdea, User commenter) {
		this.comment = comment;
		this.commentedIdea = commentedIdea;
		this.commenter = commenter;
		this.commentDate = new Date();
	}





}
