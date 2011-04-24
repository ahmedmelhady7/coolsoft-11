package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.*;
 import javax.persistence.Entity;





@Entity
public class Invitation extends Model{

	public String dest;
    public Date sendingdate;
    String role;
    
    @OneToOne(cascade=CascadeType.ALL)
    public User source;
    
    @Lob
    public String comment;
    
    @ManyToOne(cascade=CascadeType.ALL)
    public Organization organization;
    public MainEntity entity;
    public Topic topic;
	
   
    public Invitation(String dest,Date sendingdate,String role,Organization organization,
    		MainEntity entity,Topic topic,User source,String comment){
        this.dest=dest;
        this.sendingdate=sendingdate;
        this.role=role;
        this.organization = organization;
        this.entity=entity;
        this.topic=topic;
        this.source = source;
        this.comment=comment;
        
    }

   
    
    
    
    
    
}