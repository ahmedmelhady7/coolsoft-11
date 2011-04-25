package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Role extends Model {
	String roleName;

	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	List<Action> actions;

	@OneToMany(mappedBy = "role" , cascade = CascadeType.ALL)
	List<UserRoleInOrganization> userRoleInOrganization;

	public Role(String role) {
		this.roleName = role;
		this.actions = new ArrayList<Action>();
		this.userRoleInOrganization = new ArrayList<UserRoleInOrganization> ();
	//	this.userRoleInOrganization = userRoleInOrganization;
	}

}
