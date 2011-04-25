package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Item extends Model {

	public Date startDate;
	public Date endDate;
	public short status;
	public String summary;

	@Lob
	public String description;

	@ManyToMany(mappedBy="taggedItems", cascade=CascadeType.ALL)
	public List<Tag> tags;

	@OneToMany(mappedBy = "destination", cascade = CascadeType.ALL)
	public List<VolunteerRequest> volunteerRequests;

	@OneToMany(mappedBy = "source", cascade = CascadeType.ALL)
	public List<AssignRequest> assignRequests;

	@ManyToMany(mappedBy = "itemsAssigned", cascade = CascadeType.PERSIST)
	public List<User> assignees;

	@ManyToOne
	public Plan plan;

	public Item(Date startDate, Date endDate, short status,
			String description, Plan plan, String summary) {
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

}