package controllers;

import play.*;
import models.*;

public class Roles extends CRUD{

	/*
	 * retrieve the role by name
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param: role is the name of the role
	 * 
	 * @return Role
	 */

	public static Role getRoleByName(String role) {

		return Role.find("byRoleName", role).first();

	}
}
