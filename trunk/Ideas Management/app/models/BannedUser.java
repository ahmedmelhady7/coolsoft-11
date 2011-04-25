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
	public User bannedUser;

	@ManyToOne
	public Action action;

	@ManyToOne
	public Organization organization;

	public String resourceType;
	public int resourceID;

	// this constructor will be used if the action the user is banned from is
	// related to a certain object

	// @parm banned : The banned user
	// @parm action : The action he will be banned from
	// @parm org : The organization at which this user will be banned from that
	// action
	// @parm rSrcType: the type of the object the action is related to
	//@parm rSrcId : the Id of the Object of that type
	
	public BannedUser(User banned, Action action, Organization org,
			String rSrcType, int rSrcID) {
		this.bannedUser = banned;
		this.action = action;
		organization = org;
		resourceType = rSrcType;
		resourceID = rSrcID;

	}

	// this constructor will be used if the action the user is banned from is
	// related to a certain object

	// @parm banned : The banned user
	// @parm action : The action he will be banned from
	// @parm org : The organization at which this user will be banned from that
	// action
	
	
	public BannedUser(User banned, Action action, Organization org) {
		this.bannedUser = banned;
		this.action = action;
		organization = org;
	}

}
