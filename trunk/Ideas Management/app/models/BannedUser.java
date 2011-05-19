package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import controllers.Roles;

import play.db.jpa.Model;

@Entity
public class BannedUser extends CoolModel {

	@ManyToOne
	public Organization organization;

	@ManyToOne
	public User bannedUser;

	public String action;
	public String resourceType;
	public long resourceID;

	/**
	 *used if the action the user is banned from is related
	 * to a certain object
	 * 
	 * @autor:Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * @param banned : User The banned user
	 * 
	 * @param action :String action he will be banned from
	 * 
	 * @param org : Organization org at which this user will be banned from that
	 *       action
	 * 
	 * @param resourceType: the type of the object the action is related to
	 * 
	 * @param resourceId : the Id of the Object of that type
	 */

	public BannedUser(User banned, Organization org, String action,
			String resourceType, long resourceId) {
		this.bannedUser = banned;
		this.action = action;
		organization = org;
		this.resourceType = resourceType;
		resourceID = resourceId;

	}

	/**
	 * used if  the action the user is banned from is
	 * related to a certain object
	 * 
	 * @author:Nada Ossama
	 * 
	 * @story:C1S7
	 * 
	 * @param banned : User The banned user
	 * 
	 * @param action : String The action he will be banned from
	 * 
	 * @param org : Organization at which this user will be banned from that
	 * action
	 */

	public BannedUser(User banned, Organization org, String action) {
		this.bannedUser = banned;
		this.action = action;
		organization = org;
	}

	/**
	 *  used so as to block a user from a certain entity
	 * 
	 * @author: Nada Ossama
	 * 
	 * @story:C1S7
	 * 
	 * @param userID :long  ID of the User to be blocked
	 * 
	 * @param organizationID : long id that this entity belongs to
	 * 
	 * @param entityID : long  ID of that entity
	 * 
	 * @return boolean:  false if found banned otherwise return true
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
	 * block a user from a certain action within an entity and cascade that
	 * restriction to the rest of the topics within this entity if the action
	 * was topic related
	 * 
	 * @author: Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * @param userID
	 *            : long ID of the User to be blocked
	 * 
	 * @param organizationID
	 *            : long id that this entity belongs to
	 * 
	 * @param action
	 *            : String action that the user will be banned from
	 * 
	 * @param entityID
	 *            : long id the ID of that entity
	 * 
	 * @return boolean : false if found banned otherwise return true
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
		
		ArrayList<String> topicActions = (ArrayList<String>) Roles.getOrganizerTopicActions();
		/**
		 * cascade the restriction if the action is topic related
		 */
		if(topicActions.contains(action)){
			MainEntity entity = MainEntity.findById(entityID);
			List<Topic> entityTopics = entity.topicList;
			for(int i = 0 ; i < entityTopics.size() ; i++){
				long topicId = entityTopics.get(i).getId();
				banFromActionInTopic(userID, organizationID, action, topicId);
			}
			
		}
		return true;
	}

	/**
	 * block a user from a certain action within a
	 * topic
	 * 
	 * @author : Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * @param userID : long ID of the User to be blocked
	 * 
	 * @param organizationID : long id that this entity belongs to
	 * 
	 * @param action : String action that the user will be banned from
	 * 
	 * @param topicID : long id the ID of that topic
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
	 *block a user from a certain topic
	 * 
	 * @author : Nada Ossama
	 * 
	 * @story: C1S7
	 * 
	 * @param userID : long ID of the User to be blocked
	 * 
	 * @param organizationID : long id that this entity belongs to
	 * 
	 * @param entityID : long ID of that entity
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
	/**
	 * de-restricts an organizer from a certain action within a specified topic and cascades the 
	 * de-restriction to the rest of the entity
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S19
	 * 
	 * @param userID : long user ID is the id of the organizer to be direstricted
	 * @param action : String action to be de restricted from
	 * @param topicID : long topicID is the id of the topic to be derestricted from
	 */
	
	
	public static void deRestrictFromTopic(long userID  ,String action , long topicID ){
		User user = User.findById(userID);
		Topic topic = Topic.findById(topicID);
		MainEntity entity = topic.entity;
		long entityID = entity.getId();
		Organization organization = entity.organization;
		
		BannedUser restricted = BannedUser.find( "select bu from BannedUser bu where bu.organization = ? and bu.bannedUser = ?" +
				" and bu.action like ? and bu.resourceType = ? and bu.resourceID = ? "  ,organization,user,action,"topic",topicID ).first();
		
		boolean flag = false;
		if(restricted == null){
			flag = true;
		}
		System.out.println( "null restricted" +flag+ "user:"+userID + "action" + action +"topicID:" + topicID );
		
		if(restricted != null){
			user.bannedUsers.remove(restricted);
			organization.bannedUsers.remove(restricted);
			restricted.delete();
		}
		
		
		
		BannedUser restrictedInEntity = BannedUser.find( "select bu from BannedUser bu where bu.organization = ? and bu.bannedUser = ?" +
				" and bu.action like ? and bu.resourceType = ? and bu.resourceID = ? "  ,organization,user,action,"entity",entityID ).first();
		if(restrictedInEntity != null){
			deRestrictFromEntityNoCascading(userID, action, entityID);
		}
		
		
		
	}
	
	/**
	 * de-restricts a certain user from a certain action in a certain entity
	 * without cascading the derestricton process to the topics within that
	 * entity
	 * 
	 * @author: Nada Ossama
	 * 
	 * @story: C1S16
	 * 
	 * 
	 * @param userID
	 *            : long userID is the id of the user to be de-restricted
	 * @param action
	 *            :String action is the action to be de-restricted from
	 * @param entityID
	 *            : long entityID is the id of the entity to be de-restricted
	 *            from
	 */
	public static void deRestrictFromEntityNoCascading(long userID , String action , long entityID){
	
		User user = User.findById(userID);
		MainEntity entity = MainEntity.findById(entityID);
		Organization organization = entity.organization;
		
		BannedUser restricted = BannedUser.find( "select bu from BannedUser bu where bu.organization = ? and bu.bannedUser = ?" +
				" and bu.action like ? and bu.resourceType = ? and bu.resourceID = ? "  ,organization,user,action,"entity",entityID).first();
		
		boolean flag = false;
		if(restricted == null){
			flag = true;
		}
		System.out.println( "null restricted" +flag+ "user:"+userID + "action" + action +"entityID:" + entityID );
		
		if(restricted !=null){
			user.bannedUsers.remove(restricted);
			organization.bannedUsers.remove(restricted);
			restricted.delete();
			
		}
		
		
		System.out.println("DELETEDDDD");
		
	}

	/**
	 * de-restrict a user from a certain action in the whole entity and the
	 * de-restriction process is cascaded to all the topics within that entity
	 * 
	 * @author Nada Ossama
	 * 
	 * @story C1S16
	 * 
	 * @param userID
	 *            : long userID is the id of the user to be de-restricted
	 * @param action
	 *            : String action is the action to be de-restricted from
	 * @param entityID
	 *            : long entityID is the id of the entity to be de-restricted in
	 */
	
	public static void deRestrictFromEntityWithCascading(long userID , String action , long entityID){
		
		User user = User.findById(userID);
		MainEntity entity = MainEntity.findById(entityID);
		Organization organization = entity.organization;
		List<Topic> topicsList = entity.topicList;
		List<String> topicActions = Roles.getOrganizerTopicActions();
		
		
		/**
		 * if the action passed belongs to the list of topicActions then cascade
		 * the de-restriction
		 */
		if(topicActions.contains(action)){
			for(int i = 0 ; i < topicsList.size(); i++ ){
				Topic topic = topicsList.get(i);
				long topicID = topic.getId();
				deRestrictFromTopic(userID, action, topicID);
				
			}
		}
		/**
		 * whether the action belongs to topics or not it will be de-restricted in the entity 
		 */
		
		BannedUser restricted = BannedUser.find( "select bu from BannedUser bu where bu.organization = ? and bu.bannedUser = ?" +
				" and bu.action like ? and bu.resourceType = ? and bu.resourceID = ? "  ,organization,user,action,"entity",entityID).first();
		
		boolean flag = false;
		if(restricted == null){
			flag = true;
		}
		System.out.println( "null restricted" +flag+ "user:"+userID + "action" + action +"entityID:" + entityID );
		
		if(restricted != null){
			user.bannedUsers.remove(restricted);
			organization.bannedUsers.remove(restricted);
			restricted.delete();
		}
		
		System.out.println("DELETEDDDD");
		
	}

}