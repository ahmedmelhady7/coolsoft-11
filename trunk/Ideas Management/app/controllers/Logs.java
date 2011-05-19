
package controllers;

import play.*;
import play.data.validation.Required;
import play.mvc.*;

import java.util.*;
import java.lang.reflect.*;

import com.google.gson.JsonObject;

import models.*;
 

@With(Secure.class)
public class Logs extends CRUD {
	
	
	/*public static void passId() {
		viewLogs(1);
	}*/
        /**
         * Lists the logs of this organization
         * @param organizationId organization Id to list logs of
         * 
         */
        public static void viewLogs(long organizationId) {
        	
               Organization organization = Organization.findById(organizationId);
               System.out.println(organizationId + "3aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                List<Log> toFilter = new ArrayList<Log> ();
                if (organization == null) {
                     if(Security.getConnected().isAdmin)
                      toFilter = Log.findAll();
                } else {
                	 
                       if(Security.getConnected() == organization.creator || Security.getConnected().isAdmin)
                    	   toFilter = organization.logs;
                       
                       else {
                    	   System.out.println("you are not authorized to view....................");
                       }
                }
                
                
               
                render(toFilter,organizationId);
        }
        public static void searchLog(@Required String keyword, long id) {
        	ArrayList<Log> filtered = new ArrayList <Log> () ;
        	Organization organization = Organization.findById(id);
            System.out.println(id + "searchLog");
             List<Log> toFilter = new ArrayList<Log> ();
             if (organization == null) {
            	 
                  if(Security.getConnected().isAdmin)
                   toFilter = Log.findAll();
             } else {
             	 
                    if(Security.getConnected() == organization.creator || Security.getConnected().isAdmin)
                 	   toFilter = organization.logs;
                    
                    else {
                 	   System.out.println("you are not authorized to view....................");
                    }
             }
             System.out.println(id + "organization id");
             	System.out.println("111111"+ toFilter.size());
            	 for(int i = 0; i < toFilter.size(); i++) {
            		 if((toFilter.get(i).actionDescription).toLowerCase().contains(keyword.toLowerCase())) {
            			 System.out.println("mohsen" + toFilter.get(i).actionDescription);
            			 filtered.add(toFilter.get(i));
            		 }
            	 }
            	 System.out.println(filtered.size() + ">>>>>>>>>");
            	 JsonObject json = new JsonObject();
            	 String actions="";
            	 String time="";
            	 for(int i=0;i<filtered.size();i++){
            		 actions= actions + filtered.get(i).actionDescription + ",";
            		 time= time + filtered.get(i).time + ",";
            	 }
            	 
            	 json.addProperty("actions", actions);
            	 json.addProperty("time", time);
            	 
            	 renderJSON(json.toString());
             
        	
        }
        
        /**
         * Shows the log with its details, or the whole list of logs in this project
         * @param organizationId the organization's id
         * @param logId the log's id
         */
       /* public static void view(long organizationId, long logId) {
        	  Organization organization = Organization.findById(organizationId);
                if (logId == 0 && organizationId != 0) {
                     //   Organization organization = Organization.findById(organizationId);
                        List<Log> logs = null;
                        if (organization == null) {
                                if(Security.getConnected().isAdmin)
                                logs = Log.findAll();
                        } else {
                               if(Security.getConnected().equals(organization.creator))
                                logs = organization.logs;
                        }
                        render(logs, organizationId);
                } else if(logId != 0) {
                        Log log = Log.findById(logId);
                      if(Security.getConnected().equals(organization.creator)) {
                        LogInfo[] logInfo = organizationId > 0 ? 
                                                                new LogInfo[] 
                                                                        {new LogInfo("User", User.class),
                                                                        // new LogInfo("Project", Project.class),
                                                                        new LogInfo("Comment", Comment.class),
                                                                        new LogInfo("Idea", Idea.class),
                                                                        new LogInfo("Invitation", Invitation.class),
                                                                        new LogInfo("Item", Item.class),
                                                                        new LogInfo("LinkDuplicatesRequest", LinkDuplicatesRequest.class),
                                                                        new LogInfo("MainEntity", MainEntity.class),
                                                                        new LogInfo("Notification", Notification.class),
                                                                        new LogInfo("NotificationProfile", NotificationProfile.class),
                                                                        new LogInfo("Plan", Plan.class),
                                                                        new LogInfo("RequestToJoin", RequestToJoin.class),
                                                                        new LogInfo("Topic", Topic.class),
                                                                        new LogInfo("VolunteerRequest", VolunteerRequest.class)} : 
                                                                new LogInfo[] {
                                                                            new LogInfo("Comment", Comment.class),
                                                                            new LogInfo("Idea", Idea.class),
                                                                            new LogInfo("Invitation", Invitation.class),
                                                                            new LogInfo("Item", Item.class),
                                                                            new LogInfo("LinkDuplicatesRequest", LinkDuplicatesRequest.class),
                                                                            new LogInfo("MainEntity", MainEntity.class),
                                                                            new LogInfo("Notification", Notification.class),
                                                                            new LogInfo("NotificationProfile", NotificationProfile.class),
                                                                            new LogInfo("Plan", Plan.class),
                                                                            new LogInfo("RequestToJoin", RequestToJoin.class),
                                                                            new LogInfo("Topic", Topic.class),
                                                                            new LogInfo("VolunteerRequest", VolunteerRequest.class)};
                                                                
                        render(log, logInfo);
                        
                }
                }
        }*/
}