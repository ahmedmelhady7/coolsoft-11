package controllers;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

import play.data.validation.Required;
import play.mvc.With;

import models.BannedUser;
import models.MainEntity;
import models.Organization;
import models.Role;
import models.Topic;
import models.User;
@With(Secure.class)
public class BannedUsers extends CRUD {
	@Before
	public static void restrictOrganizerrr(){
//		User u = (User) User.findAll().get(0);
//		Organization org = new Organization("coooolSoft", u);
//		MainEntity e = (MainEntity) MainEntity.findAll().get(0);
//		long id = e.getId();
//		System.out.println(id);
       List<Organization> organizations = Organization.findAll();
     //  System.out.println(o.isEmpty() + "yaaaaaaaaaaaaaaaaaaaaaaaaaaaah");
       Organization organization = organizations.get(0);
		System.out.println(organizations .isEmpty());
	//	System.out.println("ya raaaaaaaaaaaab" + organization.name + "  " );
		long organizationID = organizations.get(0).getId();
	//	System.out.println(organizationID);
		restrictOrganizer(organizationID);
	}
	/**
	 *  restrict the permissions of a certain
	 * Organizer in an organization , this restriction will be in the whole
	 * entity or in a specific topic , this restriction is done by the
	 * organization lead
	 * 
	 * this method specifically renders the list of organizers in an
	 * organization
	 * 
	 * @author Nada-Ossama
	 * 
	 * @story C1S7
	 * 
	 * 
	 * @param orgID
	 *            long id of the organization
	 */
	public static void restrictOrganizer(long orgId) {
		
		Organization organization = Organization.findById(orgId);
	//	System.out.println((org == null) + "OFFFFFFFFFFFFFf");
		List<User> users = Users.searchOrganizer(organization);
//		List<User> u = User.findAll();
//		users.add(u.get(0));
	long	organizationID = orgId;
		render(users, organizationID);
	}

	/**
	 * takes the Id of the organization (the same as the previous
	 * method) and the ID of the User to be restricted,and renders the list of
	 * entities the selected user is enrolled in and it will give the
	 * organization lead to restrict him from the whole selected entity or from
	 * a specific topic
	 * 
	 * @story C1S7
	 * @author Nada Ossama
	 * 
	 * @param  userID
	 *              long id of the user to be restricted
	 * @param  organizationId 
	 *               long id of the organization he will be
	 *        restricted in
	 */
	public static void entitiesEnrolledIn(@Required long userId,
			long organizationId) {
    
		if (validation.hasErrors()|| userId == 0) {
			flash.error("Oops, please select one the Organizers");
			restrictOrganizer(organizationId);
		}
		else{
    // System.out.println(organizationId);
		Organization organization = Organization.findById(organizationId);
		User user = User.findById(userId);
		List<MainEntity> entities = Users.getEntitiesOfOrganizer(organization, user);
	//	System.out.println("++++" + entities.isEmpty());
		render(user, organizationId, entities);
		
		}
	}

	/**
	 * checks if the organization lead has chosen to restrict
	 * the organizer from the whole selected entity or from a topic in it and
	 * accordingly it will render the list of actions related or it will
	 * redirect to other page to select the topic
	 * 
	 * @author Nada ossama
	 * 
	 * @story C1S7
	 * @param entityId
	 *            : the selected entity id
	 * @param topic
	 *            : String flag if it is equal to true then restrict from a certain
	 *            topic o.w.from the whole entity
	 * @param organizationId
	 *            :long id of the organization entered as before
	 * @param userId
	 *            : long userId to be restricted
	 */

	public static void topicsEnrolledInOrRedirect(@Required long entityId, @Required String topic,
			long organizationId, long userId) {

		if (validation.hasErrors() || entityId == 0) {
			flash.error("Oops, please select atleast one choise ");
			entitiesEnrolledIn(userId,organizationId);
		}
		if (topic.equalsIgnoreCase("true")) {
			MainEntity entity = MainEntity.findById(entityId);
			List<Topic> entityTopics = entity.topicList;
			render(entityTopics, organizationId, userId);
		} else {
			entityActions(entityId, organizationId, userId);
		}

	}

	/**
	 *  block the organizer from the whole entity so the method will render the set of actions
	 * 
	 * 
	 * @author Nada Ossama
	 * @story C1S7
	 * 
	 * @param entityId
	 *            : long entityId to be restricted in
	 * @param organizationId
	 *            : long Id of the organization passed before
	 * @param userId
	 *            : long userId to be restricted
	 */

	public static void entityActions(long entityId, long organizationId,
			long userId) {
		
		
		List<String> entityActions = Roles.getRoleActions("organizer");
		MainEntity entity = MainEntity.findById(entityId);
		Organization organization = entity.organization;
		User user = User.findById(userId);
		
		

		List<String> restricted = BannedUser
				.find("select bu.action from BannedUser bu where bu.organization = ? and bu.bannedUser = ? and bu.resourceType like ? and resourceID = ? ",
						organization, user, "entity", entityId).fetch();
		for(int i = 0; i<entityActions.size() ; i++){
			if (restricted.contains(entityActions.get(i))){
				System.out.println(entityActions.get(i));
				entityActions.remove(i);
			}
		}
		render(entityActions, entityId, userId);
	}

	/**
	 * render the list of actions the organizer is
	 * allowed to do in that topic
	 * 
	 * @author Nada Ossama
	 * 
	 * @story :C1S7
	 * 
	 * 
	 * @param topicId
	 *            : long Id of the topic to be restricted in
	 * @param userId
	 *            :long Id of the user  to be restricted
	 */

	public static void topicActions(@Required long topicId, long userId) {
		
		
		
		List<String> topicActions = Roles.getOrganizerTopicActions();
		Topic topic = (Topic.findById(topicId));
		MainEntity entity = topic.entity;
		Organization organization = entity.organization;
		User user = User.findById(userId);
		
		
		if (validation.hasErrors() || topicId == 0) {
			flash.error("Oops, please select atleast one choise ");
			topicsEnrolledInOrRedirect(entity.getId(), "topic",
					organization.getId(), user.getId());
		}

		List<String> restricted = BannedUser
				.find("select bu.action from BannedUser bu where bu.organization = ? and bu.bannedUser = ? and bu.resourceType like ? and resourceID = ? ",
						organization, user, "topic", topicId).fetch();
		for(int i = 0; i<topicActions.size() ; i++){
			if (restricted.contains(topicActions.get(i))){
				topicActions.remove(i);
			}
		}

		render(topicActions, topicId, userId);
	}
	/**
	 * restricts the user from a certain action according to the selection
	 * @story C1S7
	 * @author Nada Ossama
	 * @param action:
	 *               String to be restricted from
	 * @param type : 
	 *              String topic or entity
	 * @param entityTopicId : 
	 *                 long  id of the topic or entity
	 * @param userId : long Id of the user to be restricted
	 */

	public static void restrictFinal(@Required String actionToDo, String type,
			long entityTopicId, long userId) {
      System.out.println(actionToDo);
		boolean changed = true;
		if (type.equalsIgnoreCase("topic")) {

			Topic topic = Topic.findById(entityTopicId);
			MainEntity entity = topic.entity;
			Organization org = entity.organization;
			long organizationId = org.getId();
			
			if (validation.hasErrors() || actionToDo == null) {
				flash.error("Oops, please select atleast one choise ");
				topicActions(entityTopicId, userId);
			}
			
			
			
			changed = BannedUser.banFromActionInTopic(userId, organizationId,
					actionToDo, entityTopicId);
			
			Notifications.sendNotification(userId, Security.getConnected().getId(),
					"user", "you have been restricted from the following action :" + actionToDo  +" In organization  : " + org +" In Entity :" + entity + " In Topic :" + topic );

		}

		else {
			MainEntity entity = MainEntity.findById(entityTopicId);
			Organization org = entity.organization;
			long organizationId = org.getId();
			
			
			if (validation.hasErrors()|| actionToDo == null) {
				flash.error("Oops, please select atleast one choise ");
				entityActions(entityTopicId,organizationId, userId);
			}
			
			changed = BannedUser.banFromActionInEntity(userId, organizationId,
					actionToDo, entityTopicId);
			
			Notifications.sendNotification(userId, Security.getConnected().getId(),
					"user", "you have been restricted from the following action :" + actionToDo  +" In organization  : " + org +" In Entity :" + entity );

		}
		
		
		
		
	}
	
	/**
	 * render the list of idea developers to be blocked/unblocked
	 * 
	 * @author Mai Magdy
	 * 
	 * @story :C1S16
	 * 
	 * @param topId
	 *                long Id of the topic/entity that ll view the ideadevelopers
	 *                
	 * @param num
	 *                int num as 1 if the source is topic or 0 if entity
	 *                
	 */
	
	public static void viewUsers(long topId,int num){
		
		if(num==0){
			
		List <User> users=Users.getIdeaDevelopers(topId , "entity");
		MainEntity topic=MainEntity.findById(topId);
		List <Integer> count=new ArrayList <Integer>();
		for(int i=0;i<users.size();i++){
		    BannedUser banned1=BannedUser.find("byBannedUserAndAction", users.get(i),"view").first();
            BannedUser banned2=BannedUser.find("byBannedUserAndAction", users.get(i),"use").first();
		    if(banned1==null&&banned2==null)
		    	count.add(0);
		    else
		    	if(banned1!=null&&banned2!=null)
		    		       count.add(3);
				  else if(banned2!=null)
						    count.add(2);
					 else if(banned1!=null)
						      count.add(1);
					 
		}
		
		render(users,count,topic,num);
		}
		else{
			List <User> users=Users.getIdeaDevelopers(topId , "topic");
			Topic topic=Topic.findById(topId);
			List <Integer> count=new ArrayList <Integer>();
			for(int i=0;i<users.size();i++){
			    BannedUser banned1=BannedUser.find("byBannedUserAndAction", users.get(i),"view").first();
	            BannedUser banned2=BannedUser.find("byBannedUserAndAction", users.get(i),"use").first();
			    if(banned1==null&&banned2==null)
			    	count.add(0);
			    else
			    	if(banned1!=null&&banned2!=null)
			    		       count.add(3);
					  else if(banned2!=null)
							    count.add(2);
						 else if(banned1!=null)
							      count.add(1);
						 
			}
			
			render(users,count,topic,num);
			
			 
		}
		   
			
		
	}
	
	
	/**
	 * block/unblock an idea developer from viewing/using a certain topic
	 * calls doBlock() to block from entity
	 * 
	 * @author Mai Magdy
	 * 
	 * @story :C1S16
	 * 
	 * @param userId
	 *                long Id of user that ll be blocked/unblocked
	 * @param type
	 *                int type 0 if view and 1 if use
	 *@param topId
	 *               long Id of the topic/entity that user ll be blocked/unblocked from
	 * @param id
	 *               int id , 0 if entity and 1 if topic                
	 * @param m
	 *               String m that is the text on the button to the action if (block or unblock)
	 *                
	 */
	
	public static void block(long userId,int type,long topId,int id,String m){
		   
		System.out.println("here");
		System.out.println(m);
		   System.out.println(userId);
		   System.out.println(type);
		   System.out.println(topId);
		User user=User.findById(userId);
		
		if(id==0){
		//Topic topic=Topic.findById(topId);
		MainEntity entity=MainEntity.findById(topId);
		
		if(type==0){
			if(m.equals("Block from viewing")){
				doBlock(user,entity,0,"view");
			}
			else{
				doBlock(user,entity,1,"view");
				   
			}
		}
		  else{
			  if(m.equals("Block from using")){
				  doBlock(user,entity,0,"use");
			  }
			  else{
				    doBlock(user,entity,1,"use");
					   
				}
		  }
		}
		else
		{
			Topic topic=Topic.findById(topId);
			
			if(type==0){
				if(m.equals("Block from viewing")){
				BannedUser block=new BannedUser(user, topic.entity.organization, "view topic",
					"topic", topId);
			    block.save();
				}
			}
			  else{
				  if(m.equals("Block from using")){
				  BannedUser block=new BannedUser(user, topic.entity.organization, "use",
							"topic", topId);
					    block.save();
				  }
			  }
			}
			  
			  
	}
	
	/**
	 * block/unblock an idea developer from viewing/using this entity and its all sub-entities and topics
	 * 
	 * @author Mai Magdy
	 * 
	 * @story :C1S16
	 * 
	 * @param user
	 *                User user that 
	 *                
	 * @param entity
	 *                MainEntity entity that user ll be blocked/unblocked from
	 *                
	 * @param choice
	 *                int choice as 0 if view or 1 if use
	 
	 * @param action
	 *                String action that user ll be blocked/unblocked from 
	 *                
	 */
	
	public static void doBlock(User user,MainEntity entity,int choice,String action){
		
		 Long entId=entity.id;
		if(choice==0){
			
			BannedUser block=new BannedUser(user, entity.organization, action,
				"entity", entId);
		    block.save();
		    for(int j = 0;j < entity.topicList.size();j++){
				BannedUser block1=new BannedUser(user, entity.organization, action,
						"topic", entity.topicList.get(j).id);
				block1.save();
			}
		    
		    List <MainEntity> subentity=entity.subentities;
			for(int j = 0;j < subentity.size();j++){
				BannedUser block1=new BannedUser(user, entity.organization, action,
						"entity", subentity.get(j).id);
				block1.save();
				
				for(int k = 0;k < subentity.get(j).topicList.size();k++){
					BannedUser block2=new BannedUser(user, entity.organization, action,
							"topic", subentity.get(j).topicList.get(k).id);
					block2.save();
				}
			}
			
		    
			}
			else{
				BannedUser unblock=BannedUser.find("byBannedUserAndActionAndResourceTypeAndResourceID", 
						                user,action,"entity",entity.id).first();
				   unblock.delete();
				   
				   for(int j = 0;j < entity.topicList.size();j++){
					   BannedUser unblock1=BannedUser.find("byBannedUserAndActionAndResourceTypeAndResourceID", 
						 user, entity.organization, action,"topic", entity.topicList.get(j).id).first();
						 unblock1.save();
					}
				    
				    List <MainEntity> subentity=entity.subentities;
					for(int j = 0;j < subentity.size();j++){
						BannedUser unblock2=BannedUser.find("byBannedUserAndActionAndResourceTypeAndResourceID", 
							user, entity.organization, action,"entity", subentity.get(j).id).first();
						
						unblock2.save();
						
						for(int k = 0;k < subentity.get(j).topicList.size();k++){
							 BannedUser unblock1=BannedUser.find("byBannedUserAndActionAndResourceTypeAndResourceID", 
							  user, entity.organization, action,"topic", subentity.get(j).topicList.get(k).id).first();
							
							  unblock1.save();
						}
					}
				   
			}
		}
		
	
	
	
	
	
}
