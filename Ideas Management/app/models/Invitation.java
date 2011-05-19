package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
 import javax.persistence.Entity;
import play.db.jpa.Model;




@Entity
public class Invitation extends CoolModel{

	
	/**
	 *  String email that will receive the invitation
	 */
	public String email;
	
	/**
	 *  Date dateOfSending of sending the invitation
	 */
    public Date dateOfSending;
    
    /**
     *  String role that will be assigned to the user if accept the invitation
     */
    public String role;
    
 
   /**
    *   User sender that sent the invitation    
    */
    @ManyToOne
    public User sender;
    
    /**
     *   Organization organization that sent the invitation    
     */ 
    @ManyToOne
    public Organization organization;
    
    /**
     *   MainEntity entity that sent the invitation    
     */ 
    @ManyToOne
    public MainEntity entity;
    /**
     *   Topic topic that sends the invitation    
     */ 
    @ManyToOne
    public Topic topic;
	
    
    /**
	 * Constructor that initializes the destination (email) of the invitation and
	 * the entity,the organization that sends the invitation and the user
	 * who sent the invitation
	 * 
	 * @author Mai Magdy
	 * 
	 * @param email
	 *              String email that represents the destination
	 *
	 * @param entity
	 *             MainEntity that sents the invitation
	 *             
	 * @param organization
	 *             Organization that sends the invitation
	 *             
	 * @param role
	 *            String role that ll be assigned to the user if accept
	 *                         
	 * @param sender
	 *              User that has sent the invitations
	 */
   
    public Invitation(String email,MainEntity entity,Organization organization,String role,
    		User sender,Topic topic){
        this.email=email;
        this.organization = organization;
        this.entity=entity;
        this.sender = sender;
        this.role=role;
        this.dateOfSending=new Date();
        this.topic=topic;
        
    }

   
    
    
    
    
    
}