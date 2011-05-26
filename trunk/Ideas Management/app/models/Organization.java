package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import controllers.CRUD;
import controllers.Roles;
import controllers.Security;
import controllers.UserRoleInOrganizations;
import controllers.Users;

import play.data.validation.*;
import play.db.jpa.Model;

/**
 * This is the Organization model class responsible for creating an organization
 * 
 * @author Omar Faruki
 */
@Entity
public class Organization extends CoolModel {

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
	 * **********
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
	 * list of the created relations names in the organization
	 */
	public ArrayList<String> relationNames;

	/**
	 * list of invitations that sent by the organization
	 */
	@OneToMany(mappedBy = "organization")
	public List<Invitation> invitation;

	/**
	 * id of the profile picture
	 */
	public long profilePictureId;

	/**
	 * The description of an organization
	 */
	@Lob
	public String description;

	/**
	 * The list of relationship creation requests.
	 */
	@OneToMany(mappedBy = "organisation")
	public List<CreateRelationshipRequest> createRelationshipRequest;

	/**
	 * The list of relationship renaming or deletion requests.
	 */
	@OneToMany(mappedBy = "organisation")
	public List<RenameEndRelationshipRequest> renameEndRelationshipRequest;

	/**
	 * @auther monica counter to check how many times this topic is viewed to be
	 *         used in sorting
	 */
	public int viewed;

	/**
	 * Organization Class Constructor
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S1
	 * 
	 * @param name
	 *            Name of the organization of type String
	 * 
	 * @param creator
	 *            Creator of the organization of type User
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
		this.viewed = 0;

		bannedUsers = new ArrayList<BannedUser>();
		relationNames = new ArrayList<String>() {
			{
				add("manages");
				add("has many");
				add("belongs to");
			}
		};
		profilePictureId = -1;

		// for testing
		// relationNames.add("manages");
		// relationNames.add("hates");
		// relationNames.add("controlls");
		// relationNames.add("lead by");

		// topicRelationNames = new ArrayList<String>();
		// tagRelationNames = new ArrayList<String>();

		userRoleInOrg = new ArrayList<UserRoleInOrganization>();
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
	 *            Name of the organization of type String
	 * 
	 * @param creator
	 *            Creator of the organization of type User
	 * 
	 * @param privacyLevel
	 *            privacy level of the organization of type int
	 * 
	 * @param createTag
	 *            enable or disable ability of user to create tags of type
	 *            boolean
	 */
	public Organization(String name, User creator, int privacyLevel,
			boolean createTag, String description) {
		this.name = name;
		this.creator = creator;
		intializedIn = new Date();
		// added by nada ossama
		// UserRoleInOrganizations.addEnrolledUser(this.creator, this,
		// Roles.getRoleByName("Organization Lead"));
		this.privacyLevel = privacyLevel;
		this.createTag = createTag;
		invitation = new ArrayList<Invitation>();
		this.entitiesList = new ArrayList<MainEntity>();
		this.followers = new ArrayList<User>();
		this.relatedTags = new ArrayList<Tag>();

		relationNames = new ArrayList<String>() {
			{
				add("manages");
				add("has many");
				add("belongs to");
			}
		};
		profilePictureId = -1;
		// for testing
		// relationNames.add("manages");
		// relationNames.add("hates");
		// relationNames.add("controlls");
		// relationNames.add("lead by");

		// topicRelationNames = new ArrayList<String>();
		// tagRelationNames = new ArrayList<String>();

		// this.enrolledUsers = new ArrayList<User>();
		// bannedUsers = new ArrayList<BannedUser>();
		userRoleInOrg = new ArrayList<UserRoleInOrganization>();
		joinRequests = new ArrayList<RequestToJoin>();
		this.createRelationshipRequest = new ArrayList<CreateRelationshipRequest>();
		this.renameEndRelationshipRequest = new ArrayList<RenameEndRelationshipRequest>();
		this.description = description;
	}

	/**
	 * This Method removes a user from the list of followers
	 * 
	 * @author Ibrahim al-khayat
	 * 
	 * @story C2S12
	 * 
	 * @param user
	 *            the user who follows
	 * 
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

	/**
	 * gets the list of documents owned by the organization
	 * 
	 * @author Ibrahim Al-Kahayat
	 * 
	 * @story C2S28
	 * 
	 * @return List<Document>
	 */

	public List<Document> getDocuments() {
		List<Document> documents = Document.find("byUserOrganizationId", id)
				.fetch();
		for (int i = 0; i < documents.size(); i++) {
			if (!documents.get(i).isOrganization) {
				documents.remove(i);
			}
		}
		return documents;
	}

	/**
	 * @author Mohamed Ghanem
	 * 
	 * @story C4S02 Advanced Search
	 * 
	 * @description this method find the Organizations with their type
	 * 
	 * @param orgs
	 *            'List<Organization>' list of organizations as result
	 * 
	 * @param oT
	 *            'String' types needed by the user.
	 * 
	 */
	public static void findByType(List<Organization> orgs, String oT) {
		int searchIn = 0;
		String[] showROf = oT.split(",");
		if (!Boolean.parseBoolean(showROf[0])) {
			if (Boolean.parseBoolean(showROf[1])) {
				if (Boolean.parseBoolean(showROf[2])) {
					searchIn = 4; // public & private
				} else {
					if (Boolean.parseBoolean(showROf[3])) {
						searchIn = 5; // public & secrete
					} else {
						searchIn = 1; // public
					}
				}
			} else {
				if (Boolean.parseBoolean(showROf[2])) {
					if (Boolean.parseBoolean(showROf[3])) {
						searchIn = 6; // private & secrete
					} else {
						searchIn = 2; // private
					}
				} else {
					searchIn = 3; // secrete
				}
			}
		}
		switch (searchIn) {
		case 1: {
			for (int i = 0; i < orgs.size(); i++) {
				if (((Organization) orgs.get(i)).privacyLevel != 2) {
					orgs.remove(i);
				}
			}
			break;
		}
		case 2: {
			for (int i = 0; i < orgs.size(); i++) {
				if (((Organization) orgs.get(i)).privacyLevel != 1) {
					orgs.remove(i);
				}
			}
			break;
		}
		case 3: {
			for (int i = 0; i < orgs.size(); i++) {
				if ((((Organization) orgs.get(i)).privacyLevel == 0 && !Users
						.getEnrolledUsers((Organization) orgs.get(i)).contains(
								Security.getConnected()))
						&& !Security.getConnected().isAdmin) {
					orgs.remove(i);
				}
			}
			break;
		}
		case 4: {
			for (int i = 0; i < orgs.size(); i++) {
				if (((Organization) orgs.get(i)).privacyLevel == 0) {
					orgs.remove(i);
				}
			}
			break;
		}
		case 5: {
			for (int i = 0; i < orgs.size(); i++) {
				if (((Organization) orgs.get(i)).privacyLevel == 1) {
					orgs.remove(i);
				} else {
					if ((((Organization) orgs.get(i)).privacyLevel == 0 && !Users
							.getEnrolledUsers((Organization) orgs.get(i))
							.contains(Security.getConnected()))
							&& !Security.getConnected().isAdmin) {
						orgs.remove(i);
					}
				}
			}
			break;
		}
		case 6: {
			for (int i = 0; i < orgs.size(); i++) {
				if (((Organization) orgs.get(i)).privacyLevel == 2) {
					orgs.remove(i);
				} else {
					if ((((Organization) orgs.get(i)).privacyLevel == 0 && !Users
							.getEnrolledUsers((Organization) orgs.get(i))
							.contains(Security.getConnected()))
							&& !Security.getConnected().isAdmin) {
						orgs.remove(i);
					}
				}
			}
			break;
		}
		default: {
			for (int i = 0; i < orgs.size(); i++) {
				if ((((Organization) orgs.get(i)).privacyLevel == 0 && !Users
						.getEnrolledUsers((Organization) orgs.get(i)).contains(
								Security.getConnected()))
						&& !Security.getConnected().isAdmin) {
					orgs.remove(i);
				}
			}
			break;
		}
		}
	}

	/**
	 * @author monica yousry this method increments the counter viewed
	 * @return:void
	 */
	public void incrmentViewed() {
		this.viewed++;
	}

}
