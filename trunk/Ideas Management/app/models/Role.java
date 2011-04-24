package models;

import java.util.ArrayList;

import javax.persistence.*;
 
import play.db.jpa.*;
 
@Entity
public class Role extends Model {
 String role;
 
 @ManyToMany
 ArrayList  <Action> actions;

public Role (String role , ArrayList<Action> actions){
	this.role = role;
	this.actions = actions; 
}

}
