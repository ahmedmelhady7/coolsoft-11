package controllers;

import java.util.ArrayList;
import java.util.List;

import play.*;
import play.mvc.With;
import models.*;

@With(Secure.class)
public class Roles extends CoolCRUD {

	/**
	 * retrieve the role by it's name
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param: role 
	 *             String the name of the role
	 * 
	 * @return the Role required
	 */

	public static Role getRoleByName(String role) {

		return Role.find("byRoleName", role).first();

	}

	/**
	 * returns all the default actions permitted to a certain role
	 * 
	 * @author nada ossama
	 * 
	 * @story C1S7
	 * 
	 * @param roleName
	 *            String name of the role
	 * @return list<String> 
	 *             list actions of that role
	 */

	public static List<String> getRoleActions(String roleName) {
////changeeeeeeeeeeeeeee
		//if(roleName.equals("organizer")){
		
			Role role = Role.find(
					"select r from Role r where r.roleName = ? ",roleName).first();
		//}
		//if(roleName.equals("organization lead")){
			
		//}
		//if(roleName.equals("admin")){
			
			
		//}
		//if(roleName.equals("idea"))
		
		String actionsString  = role.actions;
		String[] actionsArray = actionsString.split(";");
		List<String> actionsList = new ArrayList();
		for (int i = 0; i < actionsArray.length; i++) {
			actionsList.add(actionsArray[i]);
		}
		return actionsList;
	}

	// 13
	/**
	 * returns the list of Organizer actions that are related to
	 * Topics not the whole entity
	 * 
	 * @author nada ossama
	 *  
	 * @story C1S7
	 *  
	 * @return List<String> which is the list of actions
	 */
	public static List<String> getOrganizerTopicActions() {

		List<String> allActions = getRoleActions("organizer");
		List<String> topicActions = new ArrayList<String>();
		for (int i = 13; i < allActions.size(); i++) {
			topicActions.add(allActions.get(i));
		}
		return topicActions;
	}
}
