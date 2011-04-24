package models;


import java.util.ArrayList;

import javax.persistence.*;
 
import play.db.jpa.*;
 
@Entity
public class Action extends Model {
	String description;
    @ManyToOne
     Role role;
    
public Action (String description,Role role){
	this.description = description;
	this.role = role;
}

}


