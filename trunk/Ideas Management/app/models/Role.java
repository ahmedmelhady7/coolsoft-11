package models;

//Added public to attributes
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Role extends Model {

	public String roleName;

	public ArrayList<String> actions;

	// @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	// public List<Action> actions;

	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	public List<UserRoleInOrganization> userRoleInOrganization;

	public Role(String role) {
		this.roleName = role;
		this.actions = new ArrayList<String>();
		this.userRoleInOrganization = new ArrayList<UserRoleInOrganization>();

	}

	public Role(String role, ArrayList<String> actions) {
		this.roleName = role;
		this.actions = actions;
		this.userRoleInOrganization = new ArrayList<UserRoleInOrganization>();

	}

	/*
	 * retrieve the role by name
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @pram: role is the name of the role
	 * 
	 * @return Role
	 */

	public static Role getRoleByName(String role) {

		return Role.find("byRoleName", role).first();

	}

}
