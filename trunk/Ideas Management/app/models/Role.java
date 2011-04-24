package models;

import java.util.ArrayList;
import java.util.Set;

import javax.persistence.*;
 
import play.db.jpa.*;
 
@Entity
public class Role extends Model {
 String roleName;
 
 @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
 Set  <Action> actions;

public Role (String role , Set<Action> actions){
	this.roleName = role;
	this.actions = actions; 
}

}
