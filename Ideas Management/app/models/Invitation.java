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
    public String role;
    
 
    
    @ManyToOne
    public User sender;
    @ManyToOne
    public Organization organization;
    @ManyToOne
    public MainEntity entity;
	
   
    public Invitation(String destination,MainEntity entity,Organization organization,String role,
    		User sender){
        this.destination=destination;
        this.organization = organization;
        this.entity=entity;
        this.sender = sender;
        this.role=role;
        this.date_of_sending=new Date();
        
    }

   
    
    
    
    
    
}