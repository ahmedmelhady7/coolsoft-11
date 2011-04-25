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
	
	@OneToMany (mappedBy = "Organization" , cascade = CascadeType.ALL)
	public List<BannedUser> bannedUsers;
	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
	public List<UserRoleInOrganization> userRoleInOrg ; 
	
	public short privacyLevel;
	
	
	//Request_to_Join??
	
	//@OneToMany//(mappedBy = Pending class creation) 
	//public ArrayList<Invitation> invitations;
	//@OneToMany//(mappedBy = Pending class creation)
	//public ArrayList<Log> logsList;
	
	public Organization(String name, User creator) {
		this.name = name;
		this.creator = creator;
		this.privacyLevel = 2;	//default privacylevel is public
	}
	public Organization(String name, User creator, short privacyLevel) {
		this.name = name;
		this.creator = creator;
		this.privacyLevel = privacyLevel;
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
