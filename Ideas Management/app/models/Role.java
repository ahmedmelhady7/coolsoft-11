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
public class Role extends Model {

	@Required
	public String roleName;

	@Required
	@Lob
	public String actions;

	// @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	// public List<Action> actions;

	@OneToMany(mappedBy = "role")
	// , cascade = CascadeType.ALL)
	public List<UserRoleInOrganization> userRoleInOrganization;

	public Role(String role, String actions) {
		this.roleName = role;
		this.actions = actions;
		this.userRoleInOrganization = new ArrayList<UserRoleInOrganization>();

	}

	/**
	 * method that creates the Organizer role @ author nada ossama
	 * 
	 */
	public static void createOrganizerRole() {
		ArrayList<String> action = new ArrayList();
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

		Role Organizer = new Role("organizer", actions);
		Organizer.save();

	}

	/**
	 * this method creates the Idea Developer Role
	 * 
	 * @author Nada Ossama
	 * 
	 */

	public static void createIdeaDeveloperRole() {
		String actions = "View his/her profile;"
				+ "edit his/her profile"
				+ "View/edit his/her preferences that enable him/her to get notifications on any contributing ideas;"
				+ "can post ideas to a Topic;"
				+ "accept/Reject the invitation to be an Organizer/Ideas Developer;"
				+ "can deactivate/ reactivate his/her account;"
				+ "create a certain tag;"
				+ "can follow organization/entities/topics;"
				+ "follow tags of his/her choice;"
				+ "unfollow any of the organizations/entities/sub-entities/topics/tags that he/she was following;"
				+ "request to post ideas in a private topic;"
				+ "post ideas directly in any organization, entity , topic;"
				+ "request to join a private organization;"
				+ "Accept/Reject invitation to join an organization;"
				+ "tag his/her ideas;"
				+ "report an idea as a spam or abuse;"
				+ "leave an idea or topic in draft mode while creating it and publish it;"
				+ "request a topic suitable for positing my idea;"
				+ "report a topic or comment as spam;"
				+ "delete his/her ideas;"
				+ "add, delete and change labels, in addition to the, already created, tags and entities;"
				+ "use quick search/browse by keyword(s) in tags, entities or topic title;"
				+ "use advanced search;"
				+ "filter searching results;"
				+ "sort searching results;"
				+ "View public organizations;"
				+ "comment or share any idea, plan;"
				+ "rate plans if they;"
				+ "request marking an idea as a duplicate;"
				+ "view contribution communities;"
				+ "download search or browsing result;"
				+ "view an action plan;"
				+ "request to volunteer to work on an action item in a plan;"
				+ "view his/her assigned to-do items;"
				+ "accept/Reject assignments to work on an item in an action plan;"
				+ "mark his/her to-do item as done;";
		Role ideaDeveloper = new Role("idea developer", actions);
		ideaDeveloper.save();
	}

	/**
	 * the toString method that prints the role name
	 * 
	 * @author Nada Ossama
	 * @return String which is the role Name
	 */
	public String toString() {
		return this.roleName;
	}

}
