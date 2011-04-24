package models;
 
import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
 
@Entity
public class Plan extends Model {
	
	public String title;
	//public User user;
	public int rate;
	public String status;
	public float progress;
	public Date startDate;
	public ArrayList<String> requirements;
	public Date endDate;
	//public Topic topic;
	
	//@OneToMany(mappedBy="plan", cascade=CascadeType.ALL)
	//public List<Tag> tags;
	//@OneToMany(mappedBy="plan", cascade=CascadeType.ALL)
	//public List<Comment> comments;
	//@OneToMany(mappedBy="plan", cascade=CascadeType.ALL)
	//public List<Idea> ideas; 
    @Lob
    public String description;
    
    
    
    public Plan(String title, int rate, String status, Date startDate, Date endDate, String description) {
    	this.title = title;
    	this.rate = rate;
    	this.status = "new";
    	this.progress = 0;
    	this.startDate = startDate;
    	this.endDate = endDate;
    	this.description = description;
    	this.requirements = new ArrayList<String>();
//    	still need to add topic and user
//    	this.tags = new ArrayList<Tag>();
//    	this.comments = new ArrayList<Comment>();
//    	this.ideas = new ArrayList<Idea>();
      
    }
 
}