 package controllers;

import java.util.ArrayList;
import java.util.List;

import models.MainEntity;
import models.Organization;
import models.Role;
import models.Topic;
import models.User;
import models.UserRoleInOrganization;

public class UserRoleInOrganizations extends CRUD {

 
 /*
  * This method adds a new enrolled user in the organization where his role
  * is NOT related to a specific topic, entity .. etc
  * 
  * @author Nada Ossama
  * 
  * @story :C1S7
  * 
  * @param user: is the enrolled user
  * 
  * @param org: is the organization the User user is enrolled in
  * 
  * @param role: the role of that user in this organization
  * 
  * return boolean indicating the successfulness of the operation
  */

 public static boolean addEnrolledUser(User user, Organization org, Role role) {
  new UserRoleInOrganization(user, org, role).save();

  return true;
 }
 
 /*
  * This method adds a new enrolled user in the organization where his role
  * is related to a specific topic, entity .. etc
  * 
  * @author Nada Ossama
  * 
  * @story :C1S7
  * 
  * @parm user: is the enrolled user
  * 
  * @parm org: is the organization the User user is enrolled in
  * 
  * @parm role: the role of that user in this organization
  * 
  * @parm entityOrTopicId : the id of the entity or topic the role of that
  * user is related to
  * 
  * @parm type: the type (entity / topic)
  * 
  * return boolean indicating the successfulness of the operation
  */

 public static boolean addEnrolledUser(User user, Organization org,
   Role role, long entityOrTopicId, String type) {
  
  new UserRoleInOrganization(user, org, role, entityOrTopicId, type)
    .save();

  return true;
 }
 
 
 public  boolean isOrganizer(User user, long sourceID , String sourceType){
   
   if(sourceType.equalsIgnoreCase("Organization")){
    Organization o  = Organization.findById(sourceID);
    
    if ((Users.searchOrganizer(o)).contains(user)){
     return true;
    }
   }
   if(sourceType.equalsIgnoreCase("entity")){
    MainEntity e = MainEntity.findById(sourceID);
    
    if ((Users.getEntityOrganizers(e)).contains(user)){
     return true;
    }
   }
   if(sourceType.equalsIgnoreCase("topic")){
    Topic e = Topic.findById(sourceID);
    //if(Users.ge)
    
   
   }
   return false;
   
  }
  
  
 


 

}