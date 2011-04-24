package models;

import java.util.*;

import javax.persistence.*;

public class User {
	@OneToMany(mappedBy="creator", cascade=CascadeType.ALL)
	public ArrayList<Topic> topicsCreated;
	
	@ManyToMany(mappedBy="organizers", cascade=CascadeType.ALL)
	public ArrayList<Topic> topicsIOrganize;


	@OneToMany(mappedBy="creator", cascade=CascadeType.ALL)
	public ArrayList<Idea> IdeasCreated;
	
}
