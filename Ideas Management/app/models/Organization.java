package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import controllers.Roles;
import controllers.UserRoleInOrganizations;

import play.data.validation.*;
import play.db.jpa.Model;

@Entity
public class Organization extends Model {

	/**
	 * Organization name
	 */
	@Required
	public String name;

	/**
	 * @author Mohamed Ghanem
	 * 
	 *         Organization initialization date
	 */
	public Date intializedIn;

	/**
	 * Whether or not the users enrolled have the ability to create tags
	 */
	public boolean createTag;

	/**
	 * List of entities belonging to an organization
	 */
	@OneToMany(mappedBy = "organization")
	// , cascade = CascadeType.ALL)
	public List<MainEntity> entitiesList;

	/**
	 * Creator of that organization
	 */
	@ManyToOne
	public User creator;

	/**
	 * Privacy Level for organization (0 = secret, 1 = private, 2 = public)
	 */
	public int privacyLevel;

	/**
	 * List of followers for the organization
	 */
	@ManyToMany(mappedBy = "followingOrganizations")
	// , cascade = CascadeType.ALL)
	public List<User> followers;

	/**
	 * List of tags related to that organization
	 */
	@ManyToMany
	public List<Tag> relatedTags;

	// ERD change
	/**
	 * The list of tags that were created in this organization
	 */
	@OneToMany(mappedBy = "createdInOrganization")
	public List<Tag> createdTags;

	/**
	 * List of banned users from the organization
	 */
	@OneToMany(mappedBy = "organization")
	public List<BannedUser> bannedUsers;

	/**
	 * ***********
	 */
	@OneToMany(mappedBy = "organization")
	public List<UserRoleInOrganization> userRoleInOrg;

	@OneToMany(mappedBy = "organization")
	// , cascade = CascadeType.ALL)
	public List<RequestToJoin> joinRequests;

	/**
	 * *********
	 */
	@OneToMany(mappedBy = "organization")
	// , cascade = CascadeType.ALL)
	public List<Invitation> invitation;

	/**
	 * **********
	 */
	@OneToMany(mappedBy = "organization")
	// , cascade = CascadeType.PERSIST)
	public List<Log> logsList;

	// to which constructor should the logList, bannedUser and
	// UserRoleInOrganazation be added.

	/**
	 * Organization Class Constructor
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S1
	 * 
	 * @param name
	 *            : Name of the organization of type String
	 * 
	 * @param creator
	 *            : Creator of the organization of type User
	 */

	public Organization(String name, User creator) {
		this.name = name;
		this.creator = creator;
		intializedIn = new Date();
		// added by nada ossama
		// UserRoleInOrganizations.addEnrolledUser(this.creator, this,
		// Roles.getRoleByName("Organization Lead"));

		this.privacyLevel = 2; // default privacylevel is public
		this.createTag = true; // default users are allowed to create tags
		invitation = new ArrayList<Invitation>();
		this.entitiesList = new ArrayList<MainEntity>();
		this.followers = new ArrayList<User>();
		this.relatedTags = new ArrayList<Tag>();
		// this.enrolledUsers = new ArrayList<User>();
		bannedUsers = new ArrayList<BannedUser>();
		userRoleInOrg = new ArrayList<UserRoleInOrganization>();
		logsList = new ArrayList<Log>();
		joinRequests = new ArrayList<RequestToJoin>();
		// ERD change
		this.createdTags = new ArrayList<Tag>();
	}

	// public Organization(String name, User creator) {
	// this.name = name;
	// this.creator = creator;
	// // added by nada ossama
	// UserRoleInOrganization.addEnrolledUser(this.creator, this,
	// Roles.getRoleByName("Organization Lead"));
	//
	// this.privacyLevel = 2; // default privacylevel is public
	// this.createTag = true; // default users are allowed to create tags
	// invitation = new ArrayList<Invitation>();
	// this.entitiesList = new ArrayList<MainEntity>();
	// this.followers = new ArrayList<User>();
	// this.relatedTags = new ArrayList<Tag>();
	// this.enrolledUsers = new ArrayList<User>();
	// bannedUsers = new ArrayList<BannedUser>();
	// userRoleInOrg = new ArrayList<UserRoleInOrganization>();
	// logsList = new ArrayList<Log>();
	// joinRequests = new ArrayList<RequestToJoin>();
	// }

	/**
	 * Organization Class Constructor
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S1
	 * 
	 * @param name
	 *            : Name of the organization of type String
	 * 
	 * @param creator
	 *            : Creator of the organization of type User
	 * 
	 * @param privacyLevel
	 *            : privacy level of the organization of type short
	 * 
	 * @param createTag
	 *            : enable or disable ability of user to create tags of type
	 *            boolean
	 */
	public Organization(String name, User creator, int privacyLevel,
			boolean createTag) {
		this.name = name;
		this.creator = creator;
		// added by nada ossama
		// UserRoleInOrganizations.addEnrolledUser(this.creator, this,
		// Roles.getRoleByName("Organization Lead"));
		this.privacyLevel = privacyLevel;
		this.createTag = createTag;
		invitation = new ArrayList<Invitation>();
		this.entitiesList = new ArrayList<MainEntity>();
		this.followers = new ArrayList<User>();
		this.relatedTags = new ArrayList<Tag>();
		// this.enrolledUsers = new ArrayList<User>();
		// bannedUsers = new ArrayList<BannedUser>();
		userRoleInOrg = new ArrayList<UserRoleInOrganization>();
		logsList = new ArrayList<Log>();
		joinRequests = new ArrayList<RequestToJoin>();
	}

	/**
	 * This Method removes a user from the list of followers
	 * 
	 * @author Ibrahim.al.khayat
	 * 
	 * @story C2S12
	 * 
	 * @param user
	 *            : the user who follows
	 * 
	 * @return void
	 */

	public void unfollow(User user) {
		followers.remove(user);
		_save();
	}

	/**
	 * Override the toString method to see name for easier accessibility
	 * 
	 * @author Omar Faruki
	 * 
	 * @return String
	 */

	public String toString() {
		return this.name;
	}

}
