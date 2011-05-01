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

	String action;
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
	 * @parm banned : The banned user
	 * 
	 * @parm action : The action he will be banned from
	 * 
	 * @parm org : The organization at which this user will be banned from that
	 *       action
	 * 
	 * @parm rSrcType: the type of the object the action is related to
	 * 
	 * @parm rSrcId : the Id of the Object of that type
	 */

	public BannedUser(User banned, Organization org, String action,
			String rSrcType, long rSrcID) {
		this.bannedUser = banned;
		this.action = action;
		organization = org;
		resourceType = rSrcType;
		resourceID = rSrcID;

	}

	/*
	 * this constructor will be used if the action the user is banned from is
	 * related to a certain object
	 * 
	 * @author:Nada Ossama
	 * 
	 * @story:C1S7
	 * 
	 * @parm banned : The banned user
	 * 
	 * @parm action : The action he will be banned from
	 * 
	 * @parm org : The organization at which this user will be banned from that
	 * action
	 */

	public BannedUser(User banned, Organization org, String action) {
		this.bannedUser = banned;
		this.action = action;
		organization = org;
	}

	/*
	 * This method is used so as to block a user from a certain entity
	 * 
	 * @author: Nada Ossama
	 * 
	 * @story:C1S7
	 * 
	 * @parm userID : is the ID of the User to be blocked
	 * 
	 * @parm organizationID : is the id that this entity belongs to
	 * 
	 * @parm entityID : id the ID of that entity
	 * 
	 * returns boolean false if found banned otherwise return true
	 */
	public boolean blockFromEntity(long userID, long organizationID,
			long entityID) {

		User myBannedUser = User.findById(userID);
		Organization myOrganization = Organization.findById(organizationID);

		BannedUser test = BannedUser.find(
				"select * from BannedUser bu where bu.bannedUser = ?"
						+ " and bu.organization = ? and bu.action like ?"
						+ "and bu.resourceType like ? and bu.resourceID = ?",
				myBannedUser, myOrganization, "All", "entity", entityID)
				.first();

		if (test != null) {
			return false;
		}

		BannedUser newBannedUser = new BannedUser(myBannedUser, myOrganization,
				"All", "entity", entityID);
		newBannedUser.save();
		return true;
	}

	/*
	 * This method is used so as to block a user from a certain action within an
	 * entity
	 * 
	 * @author: Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * @parm userID : is the ID of the User to be blocked
	 * 
	 * @parm organizationID : is the id that this entity belongs to
	 * 
	 * @parm action : is the action that the user will be banned from
	 * 
	 * @parm entityID : id the ID of that entity
	 * 
	 * returns boolean false if found banned otherwise return true
	 */
	public boolean banFromActionInEntity(long userID, long organizationID,
			String action, long entityID) {
		User myBannedUser = User.findById(userID);
		Organization myOrganization = Organization.findById(organizationID);

		BannedUser test = BannedUser.find(
				"select * from BannedUser bu where bu.bannedUser = ?"
						+ " and bu.organization = ? and bu.action like ?"
						+ "and bu.resourceType like ? and bu.resourceID = ?",
				myBannedUser, myOrganization, action, "entity", entityID)
				.first();

		if (test != null) {
			return false;
		}

		BannedUser newBannedUser = new BannedUser(myBannedUser, myOrganization,
				action, "entity", entityID);
		newBannedUser.save();
		return true;
	}

	/*
	 * This method is used so as to block a user from a certain action within a
	 * topic
	 * 
	 * @author : Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * @parm userID : is the ID of the User to be blocked
	 * 
	 * @parm organizationID : is the id that this entity belongs to
	 * 
	 * @parm action : is the action that the user will be banned from
	 * 
	 * @parm topicID : id the ID of that topic
	 * 
	 * returns boolean to indicates the successfulness of the operation
	 */
	public static boolean banFromActionInTopic(long userID,
			long organizationID, String action, long topicID) {
		User myBannedUser = User.findById(userID);
		Organization myOrganization = Organization.findById(organizationID);
		BannedUser test = BannedUser.find(
				"select * from BannedUser bu where bu.bannedUser = ?"
						+ " and bu.organization = ? and bu.action like ?"
						+ "and bu.resourceType like ? and bu.resourceID = ?",
				myBannedUser, myOrganization, action, "topic", topicID)
				.first();

		if (test != null) {
			return false;
		}
		
		
		BannedUser newBannedUser = new BannedUser(myBannedUser, myOrganization,
				action, "topic", topicID);
		newBannedUser.save();
		return true;
	}

	/*
	 * This method is used so as to block a user from a certain entity
	 * 
	 * @author : Nada Ossama
	 * 
	 * @story: C1S7
	 * 
	 * @parm userID : is the ID of the User to be blocked
	 * 
	 * @parm organizationID : is the id that this entity belongs to
	 * 
	 * @parm entityID : id the ID of that entity
	 * 
	 * returns boolean to indicates the successfulness of the operation
	 */

	public boolean blockFromTopic(long userID, long organizationID, long topicID) {
		User myBannedUser = User.findById(userID);
		Organization myOrganization = Organization.findById(organizationID);
		
		BannedUser test = BannedUser.find(
				"select * from BannedUser bu where bu.bannedUser = ?"
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
		return true;
	}

}
