package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import play.db.jpa.Model;
@Entity
public class Organization extends Model{
	String name;
	public boolean createTag;
	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
	public List<MainEntity> entitiesList;
	@ManyToOne
	public User creator;
	@ManyToMany(mappedBy = "followingOrganizations", cascade = CascadeType.ALL)
	public List<User> followers;
	@ManyToMany(mappedBy = "enrolled", cascade = CascadeType.ALL)
	public List<User> enrolledUsers;
	@ManyToMany(mappedBy = "organizations", cascade = CascadeType.ALL)
	public List<Tag> relatedTags;
	
	@OneToMany (mappedBy = "organization" , cascade = CascadeType.ALL)
	public List<BannedUser> bannedUsers;
	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
	public List<UserRoleInOrganization> userRoleInOrg ; 
	

//	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL) commented until issue is resolved
//	public ArrayList<RequestToJoin> joinRequests;

	public short privacyLevel;

	

	
	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
	public List<Invitation> invitation;

	@OneToMany (mappedBy = "organization", cascade = CascadeType.PERSIST)
	public List<Log> logsList;
	//to which constructor should the logList, bannedUser and UserRoleInOrganazation be added.
	
	public Organization(String name, User creator) {
		this.name = name;
		this.creator = creator;
		this.privacyLevel = 2;	//default privacylevel is public
		this.createTag = true;  //default users are allowed to create tags
		invitation = new ArrayList<Invitation>();
		this.entitiesList = new ArrayList<MainEntity>();
		this.followers = new ArrayList<User>();
		this.relatedTags = new ArrayList<Tag>();
		this.enrolledUsers = new ArrayList<User>();
		bannedUsers = new ArrayList<BannedUser>();
		userRoleInOrg =  new ArrayList<UserRoleInOrganization>();
		logsList = new ArrayList<Log>();
		

	}
	public Organization(String name, User creator, short privacyLevel ,boolean createTag) {
		this.name = name;
		this.creator = creator;
		this.privacyLevel = privacyLevel;
		this.createTag = createTag;
		invitation = new ArrayList<Invitation>();
		this.entitiesList = new ArrayList<MainEntity>();
		this.followers = new ArrayList<User>();
		this.relatedTags = new ArrayList<Tag>();
		this.enrolledUsers = new ArrayList<User>();
		bannedUsers = new ArrayList<BannedUser>();
		userRoleInOrg =  new ArrayList<UserRoleInOrganization>();
		logsList = new ArrayList<Log>();
	}
	
	/**
	 * This Method removes a user from the list of followers
	 * 
	 * @author 	Ibrahim.al.khayat
	 * 
	 * @story 	C2S12
	 * 
	 * @param  	user 	: the user who follows
	 * 
	 * @return	void
	 */
	
	public void unfollow(User user) {
		followers.remove(user);
	}
	
	

}
