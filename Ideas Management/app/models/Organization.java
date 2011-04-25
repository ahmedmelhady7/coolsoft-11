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
	@OneToMany(mappedBy = "name")
	public ArrayList<MainEntity> entitiesList;
	@ManyToOne
	public User creator;
	@ManyToMany
	public ArrayList<User> followers;
	@ManyToMany
	public ArrayList<User> enrolledUsers;
	@ManyToMany
	public ArrayList<Tag> relatedTags;
	
	@OneToMany (mappedBy = "organization" , cascade = CascadeType.ALL)
	public List<BannedUser> bannedUsers;
	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
	public List<UserRoleInOrganization> userRoleInOrg ; 
	
	public short privacyLevel;
	
	
	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
	 List<Invitation> invitation;
	
	//Request_to_Join??
	
	@OneToMany (mappedBy = "organization", cascade = CascadeType.PERSIST)
	public List<Log> logsList;
	//to which constructor should the logList, bannedUser and UserRoleInOrganazation be added.
	
	public Organization(String name, User creator) {
		this.name = name;
		this.creator = creator;
		this.privacyLevel = 2;	//default privacylevel is public
		bannedUsers = new ArrayList<BannedUser>();
		userRoleInOrg =  new ArrayList<UserRoleInOrganization>();
		logsList = new ArrayList<Log>();
		
	}
	public Organization(String name, User creator, short privacyLevel) {
		this.name = name;
		this.creator = creator;
		this.privacyLevel = privacyLevel;
		invitation = new ArrayList<Invitation>();
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
