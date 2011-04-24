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
    
//    @OneToMany(mappedBy="plan", cascade=CascadeType.ALL)
//    public List<Tag> tags;
//    @OneToMany(mappedBy="plan", cascade=CascadeType.ALL)
//    public List<Item> items;
    
    
    public Item(Date starDate, Date endDate,String status, String description) {
    	this.startDate = startDate;
    	this.endDate = endDate;
    	this.status = status;
    	this.description = description;
//    	this.tags = new ArrayList<Tag>();
//    	this.items = new ArrayList<Item>();
        
    }
 
}