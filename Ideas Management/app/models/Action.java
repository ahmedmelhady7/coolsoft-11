package models;


import java.util.ArrayList;

import javax.persistence.*;
 
import play.db.jpa.*;
 
@Entity
public class Action extends Model {
	String description;
    @ManyToMany 
    ArrayList<Role> roles;
    
public Action (String description){
	this.description = description;
}

}


