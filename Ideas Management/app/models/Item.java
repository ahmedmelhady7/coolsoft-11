package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Item extends Model {

	public Date startDate;
	public Date endDate;
	public String status;

	@Lob
	public String description;

	@OneToMany(mappedBy="Item", cascade=CascadeType.ALL)
	public List<Tag> tags;

	@OneToMany(mappedBy = "destination", cascade = CascadeType.ALL)
	public List<VolunteerRequest> volunteerRequests;

	@OneToMany(mappedBy = "source", cascade = CascadeType.ALL)
	public List<AssignRequest> assignRequests;

	@ManyToMany(mappedBy = "itemsAssigned", cascade = CascadeType.PERSIST)
	public List<User> assignees;

	@ManyToOne
	public Plan plan;

	public Item(Date startDate, Date endDate, String status,
			String description, Plan plan) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = "new";
		this.description = description;
		this.volunteerRequests = new ArrayList<VolunteerRequest>();
		this.assignRequests = new ArrayList<AssignRequest>();
		this.assignees = new ArrayList<User>();
		this.plan = plan;
		this.assignees = new ArrayList<User>();
		this.tags = new ArrayList<Tag>();
	}

}