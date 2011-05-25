package controllers;

import play.*;
import play.data.validation.Required;
import play.mvc.*;

import java.util.*;
import java.lang.reflect.*;

import com.google.gson.JsonObject;

import models.*;

@With(Secure.class)
public class Logs extends CoolCRUD {

	public static void passId() {
		viewUserLogs();
	}

	/**
	 * 
	 * responsible for displaying the logs of a certain organization
	 * 
	 * @author ${lama ashraf}
	 * 
	 * @story C1S8
	 * 
	 * @param organizationId
	 *            : long id of the organization
	 * 
	 */
	public static void viewLogs(long organizationId) {
		User user = Security.getConnected();
		Organization organization = Organization.findById(organizationId);
		List<MainEntity> entities = organization.entitiesList;
		System.out.println(organizationId + "3aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		List<Log> toFilter = new ArrayList<Log>();
		List<Log> reversed = new ArrayList<Log>();
		

			if (Security.getConnected() == organization.creator
					|| Security.getConnected().isAdmin)
				reversed = organization.logs;

			else {
				System.out
						.println("you are not authorized to view....................");
			}
			
			for(int i = 1; i <= reversed.size(); i++) {
				toFilter.add(reversed.get(reversed.size() - i));
			}
		int flag = 0;
		if (Security.getConnected().isAdmin) {
			flag = 1;
		}

		render(toFilter, organizationId, user, entities, organization, flag);
	}

	/**
	 * 
	 * responsible for searching in the logs of a specific organization
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
	 * 
	 */
	public static void searchLog(@Required String keyword, long id,
			long entityId) {
		System.out.println("entity id = " + entityId);
		ArrayList<Log> filtered = new ArrayList<Log>();
		Organization organization = Organization.findById(id);
		MainEntity entity = MainEntity.findById(entityId);
		System.out.println(id + "searchLog");
		List<Log> toFilter = new ArrayList<Log>();
		List<Log> reversed = new ArrayList<Log>();
		
		if (organization == null) {

			if (Security.getConnected().isAdmin)
				reversed = Log.findAll();
		} else {

			if (Security.getConnected() == organization.creator
					|| Security.getConnected().isAdmin)
				reversed = organization.logs;

			else {
				System.out
						.println("you are not authorized to view....................");
			}
		}
		for(int i = 1; i <= reversed.size(); i++) {
			toFilter.add(reversed.get(reversed.size() - i));
		}
		
		System.out.println(id + "organization id");
		System.out.println("111111" + toFilter.size());
		for (int i = 0; i < toFilter.size(); i++) {
			if ((toFilter.get(i).actionDescription).toLowerCase().contains(
					keyword.toLowerCase())) {
				System.out
						.println("mohsen" + toFilter.get(i).actionDescription);
				filtered.add(toFilter.get(i));
			}
		}

		
		if (entityId == 0) {
			toFilter.clear();
			System.out.println("entity id  = 0");

			reversed = organization.logs;
			//System.out.println(toFilter.size() + "tofilter size");
		} else {
			toFilter.clear();
			System.out.println("entity id  != 0");
			if (Security.getConnected() == organization.creator
					|| Security.getConnected().isAdmin)
				reversed = entity.logs;

			else {
				System.out
						.println("you are not authorized to view....................");
			}
		}
		
		for(int i = 1; i <= reversed.size(); i++) {
			toFilter.add(reversed.get(reversed.size() - i));
		}
		System.out.println(id + "organization id");
		System.out.println("111111" + toFilter.size());
		filtered.clear();
		for (int i = 0; i < toFilter.size(); i++) {
			if ((toFilter.get(i).actionDescription).toLowerCase().contains(
					keyword.toLowerCase())) {
				System.out
						.println("mohsen" + toFilter.get(i).actionDescription);
				filtered.add(toFilter.get(i));
			}
		}
		
		System.out.println(filtered.size() + "hellooo");

		System.out.println(filtered.size() + ">>>>>>>>>");
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

	/**
	 * 
	 * responsible for viewing of the logs of users
	 * 
	 * @author ${lama ashraf}
	 * 
	 * @story C1S8
	 * 
	 * @param userId
	 *            : long id of the user
	 * 
	 */
	public static void viewUserLogs() {
		User user = Security.getConnected();
		List<Log> reversed = new ArrayList<Log>();
		List<Log> toFilter = new ArrayList<Log>();

		if (Security.getConnected().isAdmin)
			reversed = Log.findAll();
		List<Organization> organization = Organization.findAll();
		
		for(int i = 1; i <= reversed.size(); i++) {
			toFilter.add(reversed.get(reversed.size() - i));
		}
		render(toFilter, user, organization);
		
	}

	/**
	 * 
	 * responsible for searching in the logs
	 * 
	 * @author ${lama ashraf}
	 * 
	 * @story C1S8
	 * 
	 * @param keyword
	 *            : String keyword for searching for in the logs
	 * 
	 * @param id
	 *            : long user id
	 * 
	 */
	public static void searchUserLog(@Required String keyword) {
		ArrayList<Log> filtered = new ArrayList<Log>();
		List <Log> reversed =  new ArrayList<Log>();
		List<Log> toFilter = new ArrayList<Log>();

		reversed = Log.findAll();
		for(int i = 1; i <= reversed.size(); i++) {
			toFilter.add(reversed.get(reversed.size() - i));
		}
		System.out.println("111111" + toFilter.size());
		for (int i = 0; i < toFilter.size(); i++) {
			if ((toFilter.get(i).actionDescription).toLowerCase().contains(
					keyword.toLowerCase())) {

				filtered.add(toFilter.get(i));
			}
		}
		System.out.println(filtered.size() + ">>>>>>>>>");
		JsonObject json = new JsonObject();
		String actions = "";
		String time = "";
		for (int i = 0; i < filtered.size(); i++) {
			if (i == filtered.size() - 1) {
				actions = actions + filtered.get(i).actionDescription + "|";
				time = time + filtered.get(i).time + "";
			}
			else {
			actions = actions + filtered.get(i).actionDescription + "" + "|";
			time = time + filtered.get(i).time + "|";
			}
 
		}
		System.out.println("filtered" + filtered.size());
		json.addProperty("actions", actions);
		json.addProperty("time", time);

		renderJSON(json.toString());

	}
}