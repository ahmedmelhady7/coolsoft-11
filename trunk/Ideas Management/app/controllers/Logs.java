package controllers;
/**
 * 
 * @author Lama Ashraf
 */
import play.*;
import play.data.validation.Required;
import play.mvc.*;

import java.util.*;
import java.lang.reflect.*;

import com.google.gson.JsonObject;

import models.*;

@With(Secure.class)
public class Logs extends CoolCRUD {
	/**
	 * 
	 * responsible for displaying the logs of a certain organization only for the admin and organization lead
	 * 
	 * @author ${lama ashraf}
	 * 
	 * @story C1S8
	 * 
	 * @param organizationId
	 *            : long id of the organization
	 * 
	 */
	public static void viewLogs(long organizationId, int pageNumber, int offset, String position, int max) {
		Organization organization = Organization.findById(organizationId);
		notFoundIfNull(organization);
		if(Security.getConnected().isAdmin || Security.getConnected().equals(organization.creator)) {
		User user = Security.getConnected();
		
		List<MainEntity> entities = organization.entitiesList;
		
		entities.remove(0);
		List<Log> logList = new ArrayList<Log>();
		List<Log> reversed = new ArrayList<Log>();
		List<Log> toFilter= new ArrayList<Log>(); 
		

			if (Security.getConnected() == organization.creator
					|| Security.getConnected().isAdmin)
				reversed = organization.logs;

			
			
			for(int i = 1; i <= reversed.size(); i++) {
				logList.add(reversed.get(reversed.size() - i));
			}
			
			int i = 0;
			if(position.equals("next") || position.equals("")) {
				
				pageNumber ++;
				max++;
				offset = offset + 3;
				
				i = offset-4;
				
				while(i < logList.size() && i< offset){
					toFilter.add(logList.get(i));
					i++;
					
				}
				}
				else {
					if(position.equals("previous")) {
						pageNumber --;
						max--;
						offset = offset - 3;
						
						i = offset-4;
						
						while(i < logList.size() && i< offset){
							toFilter.add(logList.get(i));
							i++;
							
						}
					}
				}
			boolean maxPage = true;
			if(max-1 == logList.size() /4) {
				maxPage = false;
			}
			
		int flag = 0;
		if (Security.getConnected().isAdmin) {
			flag = 1;
		}
		
		render(toFilter, organizationId, user, entities, organization, flag, pageNumber, offset, max, maxPage);
		}
		else {
			BannedUsers.unauthorized();
		}
			
		
	}

	/**
	 * 
	 * responsible for searching in the logs of a specific organization, with a specific keyword in a specific entity
	 * 
	 * @author ${lama ashraf}
	 * 
	 * @story C1S8
	 * 
	 * @param keyword
	 *            : String keyword for searching for in the logs
	 * 
	 * @param id
	 *            : long organization id
	 *@param entityId
	 *            : long entity id
	 * 
	 */
	public static void searchLog(@Required String keyword, long id,
			long entityId, int pageNumber, int offset, String position) {
		Organization organization = Organization.findById(id);
		notFoundIfNull(organization);
		if(Security.getConnected().isAdmin || Security.getConnected().equals(organization.creator)) {
		
		
		ArrayList<Log> filtered = new ArrayList<Log>();
		ArrayList<Log> finalLog = new ArrayList<Log>();
		
		MainEntity entity = MainEntity.findById(entityId);
		//notFoundIfNull(entity);
		
		List<Log> toFilter = new ArrayList<Log>();
		List<Log> reversed = new ArrayList<Log>();
		
		if (organization == null) {

			if (Security.getConnected().isAdmin)
				reversed = Log.findAll();
		} else {

			if (Security.getConnected() == organization.creator
					|| Security.getConnected().isAdmin)
				reversed = organization.logs;

			
		}
		for(int i = 1; i <= reversed.size(); i++) {
			toFilter.add(reversed.get(reversed.size() - i));
		}
		
		
		for (int i = 0; i < toFilter.size(); i++) {
			if ((toFilter.get(i).actionDescription).toLowerCase().contains(
					keyword.toLowerCase())) {
				
				filtered.add(toFilter.get(i));
			}
		}

		
		if (entityId == 0) {
			toFilter.clear();
			

			reversed = organization.logs;
			
			
		} else {
			toFilter.clear();
			
			if (Security.getConnected() == organization.creator
					|| Security.getConnected().isAdmin)
				reversed = entity.logs;

		
		}
		
		for(int i = 1; i <= reversed.size(); i++) {
			toFilter.add(reversed.get(reversed.size() - i));
		}
		
		filtered.clear();
		for (int i = 0; i < toFilter.size(); i++) {
			if ((toFilter.get(i).actionDescription).toLowerCase().contains(
					keyword.toLowerCase())) {
				
				filtered.add(toFilter.get(i));
			}
		}
		
		JsonObject json = new JsonObject();
		String actions = "";
		String time = "";
		for (int i = 0; i < finalLog.size(); i++) {
			if (i == finalLog.size() - 1) {
				actions = actions + finalLog.get(i).actionDescription + "";
				time = time + finalLog.get(i).time + "";
			}
			else {
			actions = actions + finalLog.get(i).actionDescription + "" + "|";
			time = time + finalLog.get(i).time + "|";
			}
 
		}

		json.addProperty("actions", actions);
		json.addProperty("time", time);

		renderJSON(json.toString());
		}
		else {
			BannedUsers.unauthorized();
		}

	}

	/**
	 * 
	 * responsible for viewing of the logs of all users for the admin only
	 * 
	 * @author ${lama ashraf}
	 * 
	 * @story C1S8
	 * 
	 */
	public static void viewUserLogs(int pageNumber, int offset, String position, int max) {
		if(Security.getConnected().isAdmin) {
		User user = Security.getConnected();
		List<Log> reversed = new ArrayList<Log>();
		List<Log> logList = new ArrayList<Log>();
		List<Log> toFilter = new ArrayList<Log>();

		if (Security.getConnected().isAdmin)
			reversed = Log.findAll();
		List<Organization> organization = Organization.findAll();
		
		for(int i = 1; i <= reversed.size(); i++) {
			logList.add(reversed.get(reversed.size() - i));
		}
		
		int i = 0;
		if(position.equals("next") || position.equals("")) {
		
		pageNumber ++;
		max++;
	
		offset = offset + 3;
		
		 i = offset-4;
		
		while(i < logList.size() && i< offset){
			toFilter.add(logList.get(i));
			i++;
			
		}
		}
		else {
			if(position.equals("previous")) {
				pageNumber --;
				max--;
				offset = offset - 3;
				
				i = offset-4;
				
				while(i < logList.size() && i< offset){
					toFilter.add(logList.get(i));
					i++;
					
				}
			}
		}
		boolean maxPage = true;
		if(max-1 == logList.size() /4) {
			maxPage = false;
		}
		
		
		
		render(toFilter, user, organization, pageNumber, offset, max, maxPage); 
		}
		else {
			BannedUsers.unauthorized();
		}
		
	}

	/**
	 * 
	 * responsible for searching in the logs, only the admin is permitted to do so 
	 * 
	 * @author ${lama ashraf}
	 * 
	 * @story C1S8
	 * 
	 * @param keyword
	 *            : String keyword for searching for in the logs
	 * 
	 */
	public static void searchUserLog(@Required String keyword) {
		if(Security.getConnected().isAdmin) {
		ArrayList<Log> filtered = new ArrayList<Log>();
		List <Log> reversed =  new ArrayList<Log>();
		List<Log> toFilter = new ArrayList<Log>();

		reversed = Log.findAll();
		for(int i = 1; i <= reversed.size(); i++) {
			toFilter.add(reversed.get(reversed.size() - i));
		}
		
		for (int i = 0; i < toFilter.size(); i++) {
			if ((toFilter.get(i).actionDescription).toLowerCase().contains(
					keyword.toLowerCase())) {

				filtered.add(toFilter.get(i));
			}
		}
		
		JsonObject json = new JsonObject();
		String actions = "";
		String time = "";
		for (int i = 0; i < filtered.size(); i++) {
			if (i == filtered.size() - 1) {
				actions = actions + filtered.get(i).actionDescription + "";
				time = time + filtered.get(i).time + "";
			}
			else {
			actions = actions + filtered.get(i).actionDescription + "" + "|";
			time = time + filtered.get(i).time + "|";
			}
 
		}
		
		json.addProperty("actions", actions);
		json.addProperty("time", time);

		renderJSON(json.toString());
		}
		else {
			BannedUsers.unauthorized();
		}

	}
}