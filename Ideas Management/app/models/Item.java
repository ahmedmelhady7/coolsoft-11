package models;

import java.util.*;

import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Item extends Model {

	public Date startDate;

	public Date endDate;

	public int status;
	// 0 for new, 1 for in progress and 2 for done

	public String summary;

	@Lob
	public String description;

	@ManyToMany
	// , cascade = CascadeType.ALL)
	public List<Tag> tags;

	@OneToMany(mappedBy = "destination")
	// , cascade = CascadeType.ALL)
	public List<VolunteerRequest> volunteerRequests;

	@OneToMany(mappedBy = "source")
	// , cascade = CascadeType.ALL)
	public List<AssignRequest> assignRequests;

	@ManyToMany(mappedBy = "itemsAssigned" )
	// , cascade = CascadeType.PERSIST)
	public List<User> assignees;

	@ManyToOne
	public Plan plan;
	/**
	 * This is the constructor for Item
	 * 
	 * @story C5S1
	 * 
	 * @author Hassan Ziko
	 * 
	 * @param startDate
	 *            The date when the item should be started
	 * @param endDate 
	 *            The date when the item should be done
	 * @param description
	 * 			  The description of the item
	 * @param plan
	 * 			  The plan that this item is in
	 * @param summary
	 * 			  The summary of the description
	 * 
	 */
	public Item(Date startDate, Date endDate, String description, Plan plan,
			String summary) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = 0;
		this.summary = summary;
		this.description = description;
		this.volunteerRequests = new ArrayList<VolunteerRequest>();
		this.assignRequests = new ArrayList<AssignRequest>();
		this.assignees = new ArrayList<User>();
		this.plan = plan;
		this.assignees = new ArrayList<User>();
		this.tags = new ArrayList<Tag>();
	}

	/**
	 * 
	 * This Method adds a volunteer request to work on this item to the list of
	 * volunteer requests of this item given the volunteer request
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S10
	 * 
	 * @param volunteerRequest
	 *            : the VolunteerRequest that needs to be added to the list of
	 *            volunteer requests of this item
	 */

	public void addVolunteerRequest(VolunteerRequest volunteerRequest) {
		volunteerRequests.add(volunteerRequest);
		this.save();

	}

	/**
	 * 
	 * This Method adds an assign request to work on this item to the list of
	 * assign requests of this item given the assign request
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S4
	 * 
	 * @param assignRequest
	 *            : the AssignRequest that needs to be added to the list of
	 *            assign requests of this Item
	 */
	public void addAssignRequest(AssignRequest assignRequest) {
		assignRequests.add(assignRequest);
		this.save();
	}
	
	/**
	 * 
	 * This Method returns the list of assigned users to this item
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S5, C5S10
	 * 
	 * @return List <User>
	 */
	public List <User> getAssignees() {
		return assignees;
	}
	
	/**
	 * 
	 * This Method checks if the endDate of the item has passed
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S5, C5S10
	 * 
	 * @return boolean
	 */
	public boolean afterEndDate() {
		Date d = new Date();
		if(this.endDate.compareTo(d) > 0) {
			return false;
		}
		return true;
	}

}