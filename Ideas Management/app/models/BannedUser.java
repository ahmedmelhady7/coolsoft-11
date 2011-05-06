package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class BannedUser extends Model {

	@ManyToOne
	public Organization organization;

	@ManyToOne
	public User bannedUser;

	public String action;
	public String resourceType;
	public long resourceID;

	/**
	 * constructor will be used if the action the user is banned from is related
	 * to a certain object
	 * 
	 * @autor:Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * @param banned : The banned user
	 * 
	 * @param action : The action he will be banned from
	 * 
	 * @param org : The organization at which this user will be banned from that
	 *       action
	 * 
	 * @param rSrcType: the type of the object the action is related to
	 * 
	 * @param rSrcId : the Id of the Object of that type
	 */

	public BannedUser(User banned, Organization org, String action,
			String resourceType, long resourceId) {
		this.bannedUser = banned;
		this.action = action;
		organization = org;
		resourceType = resourceType;
		resourceID = resourceId;

	}

	/**
	 * this constructor will be used if the action the user is banned from is
	 * related to a certain object
	 * 
	 * @author:Nada Ossama
	 * 
	 * @story:C1S7
	 * 
	 * @param banned : The banned user
	 * 
	 * @param action : The action he will be banned from
	 * 
	 * @param org : The organization at which this user will be banned from that
	 * action
	 */

	public BannedUser(User banned, Organization org, String action) {
		this.bannedUser = banned;
		this.action = action;
		organization = org;
	}

	/**
	 * This method is used so as to block a user from a certain entity
	 * 
	 * @author: Nada Ossama
	 * 
	 * @story:C1S7
	 * 
	 * @param userID : is the ID of the User to be blocked
	 * 
	 * @param organizationID : is the id that this entity belongs to
	 * 
	 * @param entityID : id the ID of that entity
	 * 
	 * @return boolean false if found banned otherwise return true
	 */
	public static boolean blockFromEntity(long userID, long organizationID,
			long entityID) {

		User myBannedUser = User.findById(userID);
		Organization myOrganization = Organization.findById(organizationID);
                     
		BannedUser test = BannedUser.find(
				"select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action like ? and bu.resourceType like ? and bu.resourceID = ?",
				myBannedUser, myOrganization, "All", "entity", entityID)
				.first();

		if (test != null) {
			return false;
		}

		BannedUser newBannedUser = new BannedUser(myBannedUser, myOrganization,
				"All", "entity", entityID);
		
		newBannedUser.save();
    	myBannedUser.bannedUsers.add(newBannedUser);
		myOrganization.bannedUsers.add(newBannedUser);
		return true;
	}

	/**
	 * This method is used so as to block a user from a certain action within an
	 * entity
	 * 
	 * @author: Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * @param userID : is the ID of the User to be blocked
	 * 
	 * @param organizationID : is the id that this entity belongs to
	 * 
	 * @param action : is the action that the user will be banned from
	 * 
	 * @param entityID : id the ID of that entity
	 * 
	 * @return boolean false if found banned otherwise return true
	 * 
	 */
	public static boolean banFromActionInEntity(long userID, long organizationID,
			String action, long entityID) {
		User myBannedUser = User.findById(userID);
		Organization myOrganization = Organization.findById(organizationID);

		BannedUser test = BannedUser.find(
				"select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action like ? and bu.resourceType like ? and bu.resourceID = ?",
				myBannedUser, myOrganization, action, "entity", entityID)
				.first();

		if (test != null) {
			return false;
		}

		BannedUser newBannedUser = new BannedUser(myBannedUser, myOrganization,
				action, "entity", entityID);
		newBannedUser.save();
    	myBannedUser.bannedUsers.add(newBannedUser);
		myOrganization.bannedUsers.add(newBannedUser);
		return true;
	}

	/**
	 * This method is used so as to block a user from a certain action within a
	 * topic
	 * 
	 * @author : Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * @param userID : is the ID of the User to be blocked
	 * 
	 * @param organizationID : is the id that this entity belongs to
	 * 
	 * @param action : is the action that the user will be banned from
	 * 
	 * @param topicID : id the ID of that topic
	 * 
	 * @return boolean to indicates the successfulness of the operation
	 */
	public static boolean banFromActionInTopic(long userID,
			long organizationID, String action, long topicID) {
		User myBannedUser = User.findById(userID);
		Organization myOrganization = Organization.findById(organizationID);
		BannedUser test = BannedUser.find(
				"select bu from BannedUser bu where bu.bannedUser = ? and bu.organization = ? and bu.action like ? and bu.resourceType like ? and bu.resourceID = ?",
				myBannedUser, myOrganization, action, "topic", topicID)
				.first();

		if (test != null) {
			return false;
		}
		
		
		BannedUser newBannedUser = new BannedUser(myBannedUser, myOrganization,
				action, "topic", topicID);
		newBannedUser.save();
    	myBannedUser.bannedUsers.add(newBannedUser);
		myOrganization.bannedUsers.add(newBannedUser);
		return true;
	}

	/**
	 * This method is used so as to block a user from a certain entity
	 * 
	 * @author : Nada Ossama
	 * 
	 * @story: C1S7
	 * 
	 * @param userID : is the ID of the User to be blocked
	 * 
	 * @param organizationID : is the id that this entity belongs to
	 * 
	 * @param entityID : id the ID of that entity
	 * 
	 * @return boolean to indicates the successfulness of the operation
	 */

	public static boolean blockFromTopic(long userID, long organizationID, long topicID) {
		User myBannedUser = User.findById(userID);
		Organization myOrganization = Organization.findById(organizationID);
		
		BannedUser test = BannedUser.find(
				"select bu from BannedUser bu where bu.bannedUser = ?"
						+ " and bu.organization = ? and bu.action like ?"
						+ "and bu.resourceType like ? and bu.resourceID = ?",
				myBannedUser, myOrganization, "All", "Topic", topicID)
				.first();

		if (test != null) {
			return false;
		}
		BannedUser newBannedUser = new BannedUser(myBannedUser, myOrganization,
				"All", "Topic", topicID);
		newBannedUser.save();
		myBannedUser.bannedUsers.add(newBannedUser);
		myOrganization.bannedUsers.add(newBannedUser);
		return true;
	}

}
