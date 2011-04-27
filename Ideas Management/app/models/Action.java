//package models;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.persistence.*;
//
//import play.db.jpa.*;
//
//@Entity
//public class Action extends Model {
//	@Lob
//	String description;
//
//	@ManyToOne
//	Role role;
//
//	@OneToMany
//	List<BannedUser> bannedUsers;
//	
//	
//
//	public Action(String description, Role role) {
//		this.description = description;
//		this.role = role;
//		bannedUsers = new ArrayList<BannedUser>();
//	}
//
//	// equals takes an action and returns whether this action equals to the
//	// action the method is invoked on
//	// @Parm a Action is the action given
//	// returns boolean true if the passed action equals the invoked one 
//	// or false if they are not equal
//	
//	public boolean equals(Action a) {
//		if (this.description.equals(a.description)) {
//			return true;
//		}
//
//		return false;
//
//	}
//
//}
