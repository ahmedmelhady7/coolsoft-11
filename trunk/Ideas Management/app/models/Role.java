package models;

//Added public to attributes
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import play.data.validation.Required;
import play.db.jpa.*;

@Entity
public class Role extends CoolModel {

	@Required
	public String roleName;


	@Required
	@Lob
	public String actions;

	

	@OneToMany(mappedBy = "role")
	// , cascade = CascadeType.ALL)
	public List<UserRoleInOrganization> userRoleInOrganization;

	/**
	 * creates a new role given it's name and action string
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @param role
	 *           String role name
	 * @param actions
	 *            String actions string
	 */

	public Role(String role, String actions) {
		this.roleName = role;
		this.actions = actions;
		this.userRoleInOrganization = new ArrayList<UserRoleInOrganization>();

	}

	/**
	 * creates the Organizer role 
	 * 
	 * @ author nada ossama
	 * 
	 * @story C1S7
	 */
	public static void createOrganizerRole() {
	//	ArrayList<String> action = new ArrayList();
		String actions = "invite Organizer or Idea Developer to become Organizer or Idea Developer in an entity he/she manages;"
				+ "block a user from viewing or using a certain entity;"
				+ "Request to start a relationship with other items;"
				+ "Request to end a relationship with other items;"
				+ "Accept/Reject a request to start/end a relationship with other items;"
				+ "Create a sub-entity for entity he/she manages;"
				+ "Request to rename relationships that are related to any of the entities or topics he/she manages;"
				+ "Accept/Reject request to rename relationships that are related to any of the entities or topics I am managing;"
				+ "Create a certain topic and specify its type within entities that he/she manages;"
				+ "Receive notifications about any changes or updates within the entities that he/she created or he/she currently manages;"
				+ "Invite a user to join a private or secret organization;"
				+ "Add images and documents to the organization profile;"
				+ "post topics;"
				+
				// starting from here the actions are related to topics
				"tag ideas in my organization;"
				+ "delete other ideas;"
				+ "merge ideas;"
				+ "hide and delete an idea;"
				+ "delete and hide topics that have no ideas posted on them;"
				+ "rate/prioritize ideas;"
				+ "receive notifications about inactive ideas;"
				+ "confirm or reject marking requests;"
				+ "view the list of ideas;"
				+ "create an action plan to execute an idea;"
				+ "delete an action plan;"
				+ "edit an action plan;"
				+ "associate an idea or more to an already existing plan;"
				+ "assign one or many users to a to-do item in a plan;"
				+ "accept/Reject user request to volunteer to work on action item in a plan;"
				+ "tag topics;"
				+ "Accept/Reject requests to post in a private topic in entities he/she manages;"
				+ "close a topic and promote it to execution";

	      new Role("organizer", actions).save();
		
		

	}

	/**
	 * creates the Idea Developer Role
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 */

	public static void createIdeaDeveloperRole() {

		String actions = 
				
				
				 "can post ideas to a Topic;"
				+ "create a certain tag;"
				+ "can follow organization, entitie or topic;"
				+ "follow tags of his/her choice;"
				+ "request to join a private topic;"
				+ "request to join a private organization;"
				+ "tag his/her ideas;"
				+ "report an idea as a spam or abuse;"
				+ "leave an idea or topic in draft mode while creating it and publish it;"
				+ "request a topic suitable for positing his/her idea;"
				+ "report a topic or comment as spam;"
				+ "delete his/her ideas;"
				+ "add labels;"
				+ "delete labels;"
				+ "change labels;"
				+ "View it;"
				+ "comment or share any idea, plan;"
				+ "rate plans;"
				+ "request marking an idea as a duplicate;"
				+ "view an action plan;"
				+ "request to volunteer to work on an action item in a plan;"
				+ "view his/her assigned to-do items;"
				+ "mark his/her to-do item as done;";

		String actions2 = 
				 "use;"
		        + "view";
		        

		Role ideaDeveloper = new Role("idea developer", actions);
		ideaDeveloper.save();
		
	}

	/**
	 * creates the organization lead role
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 */
	
	public static void createOrganizationLeadRole(){
		  String action = "restrict the permission of an organizer;view the logs;create an organization and specify it's type;create organization profile;create entities;" +
		    "accept/reject join requests from users to join a private organization;" +
		    "enable/disable the user to create their own tags within an organization; " +
		    "rename any relationship within the organization;" +
		    "create relationships between entities/sub-entities/topics/tags;" +
		    "end a relationship between entities/sub-entities/topics/tags;";
		  Role OrganizationLead  = new Role("organizationLead" , action);
		     OrganizationLead.save();
		 }
	/**
	 * creates the admin role
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 */
		 
		 public static void createAdminRole(){
		  String action = "add a user;edit a user;delete a user;edit ideas/rates/requests/marks;" +
		    "Use all browsing and searching kinds;";
		  Role admin  = new Role("admin" , action);
		  admin.save();
		 }
	
	
	
	
	
	/**
	 * prints the role by role name
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S7
	 * 
	 * @return String which will be printed
	 */
	public String toString() {
		return this.roleName;
	}
	public boolean equals(Object o){
		Role r = (Role)o;
		
		if(this.roleName .equals(r.roleName)){
			return true;
		}
		return false;
	}

}
