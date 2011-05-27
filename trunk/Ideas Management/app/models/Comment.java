package models;

import java.util.ArrayList;
import java.util.Date;
import javax.persistence.*;

import controllers.Security;

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
public class Comment extends CoolModel {

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

	/**
	 * the reporters of the comment
	 * 
	 * @author ${Ahmed El-Hadi}
	 */

	public String reporters;
	
	public Comment(String comment, Topic commentedTopic, User commenter) {
		this.comment = comment;
		this.commentedTopic = commentedTopic;
		this.commenter = commenter;
		this.commentDate = new Date();
		this.reporters = "";
	}

	public Comment(String comment, Plan commentedPlan, User commenter) {
		this.comment = comment;
		this.commentedPlan = commentedPlan;
		this.commenter = commenter;
		this.commentDate = new Date();
		this.reporters = "";
	}

	public Comment(String comment, Idea commentedIdea, User commenter) {
		this.comment = comment;
		this.commentedIdea = commentedIdea;
		this.commenter = commenter;
		this.commentDate = new Date(); //new Date(2000,5,5); //for testing in the demo; @M Ghanem
		this.reporters = "";
	}

/**
 * This method overrides toString to display:- First and last name of the user that commented, the comment itself, and the date of the comment
 * 
 * @author Ibrahim Safwat
 * 
 * @story C4S08
 * 
 * @param void
 * 
 * @return String
 */
	public String toString()
	{
		int year = this.commentDate.getYear() + 1900;
		String display = this.commenter.firstName + " " + this.commenter.lastName + " : " + this.comment + " on " + this.commentDate.getDay() + "/" + this.commentDate.getMonth()+ "/" + year;
		return display;
	}

	public boolean reported(long reporterId){
		User reporter = User.findById(reporterId);
		ArrayList<User> commentReporters = new ArrayList<User>();
		String[] commentReportersId = { "0" };
		System.out.println("ana hena fel comment" + reporterId);
		long reporterId2=0;
		if (reporters != null) {
			commentReportersId = reporters.split(",");
			System.out.println(commentReportersId.length);
			System.out.println(reporters + " hhhhhhhhhhhhhhhhh");
			if (!reporters.isEmpty()) {
				for (int i = 0; i < commentReportersId.length
						&& reporters != ""; i++) {
					reporterId2 = Long.parseLong(commentReportersId[i]);
					System.out.println("reporters" + reporters);
					if (reporterId2 == reporterId) {
						return true;
					}
				}
			}
			return false;
		}
		return false;
	}

}
