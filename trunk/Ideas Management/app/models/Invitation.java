package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
 import javax.persistence.Entity;
import play.db.jpa.Model;




@Entity
public class Invitation extends Model{

	public String destination;
    public Date date_of_sending;
    String role;
    
    
    
    @Lob
    public String comment;
    
    @ManyToOne
    public User sender;
    @ManyToOne
    public Organization organization;
    @ManyToOne
    public MainEntity entity;
    @ManyToOne
    public Topic topic;
	
   
    public Invitation(String comment,String destination,MainEntity entity,Organization organization,String role,
    		User sender,Topic topic){
        this.destination=destination;
        this.organization = organization;
        this.entity=entity;
        this.topic=topic;
        this.sender = sender;
        this.comment=comment;
        this.role=role;
        this.date_of_sending=new Date();
        
    }

   
    
    
    
    
    
}