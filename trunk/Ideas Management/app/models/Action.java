package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Action extends Model {
	@Lob
	String description;
	
	@ManyToOne
	Role role;

	@OneToMany
	List<BannedUser> bannedUsers;

	public Action(String description, Role role) {
		this.description = description;
		this.role = role;
		bannedUsers = new ArrayList<BannedUser>();
	}

}
