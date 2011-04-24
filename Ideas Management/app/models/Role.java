package models;

import java.util.ArrayList;

import javax.persistence.*;
 
import play.db.jpa.*;
 
@Entity
public class Role extends Model {
 String roleName;
 
 @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
 ArrayList  <Action> actions;

public Role (String role , ArrayList<Action> actions){
	this.roleName = role;
	this.actions = actions; 
}

}
