package models;


import java.util.ArrayList;

import javax.persistence.*;
 
import play.db.jpa.*;
 
@Entity
public class Action extends Model {
	String description;
    @ManyToOne
    ArrayList<Role> roles;
    
public Action (String description, ArrayList<Role> roles){
	this.description = description;
	this.roles = roles;
}

}


