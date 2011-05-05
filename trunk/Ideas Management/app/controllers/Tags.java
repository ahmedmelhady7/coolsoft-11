package controllers;

import java.util.ArrayList;
import java.util.List;

import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;
import models.Idea;
import models.Item;
import models.MainEntity;
import models.Organization;
import models.Tag;
import models.Topic;
import models.User;

@With(Secure.class)
public class Tags extends CRUD {
	/**
	 * This method creates a new tag
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S9
	 * 
	 * @param name
	 *            : name of the tag that is going to be created
	 * 
	 * @param orgId 
	 * 				: the current organization that the user is enrolled in
	 */


	public static void createTag(long orgId) {
		Organization org = Organization.findById(orgId);
		render(org);
//		if (validation.hasErrors()) {
//			params.flash();
//			validation.keep();
//			render(org);
//		}
//		
//		
//		flash.success("Your tag has been created.");
	}
	public static void createTagg(String name, long orgId) {
		User user = Security.getConnected();
		Organization org = Organization.findById(orgId);
		List<Organization> allOrg = Organization.findAll();
		List<Tag> allTags = new ArrayList<Tag>();
		int i = 0;
		while(i < org.createdTags.size()) {
			allTags.add(org.createdTags.get(i));
			i++;
		}
		i = 0;
		while(i < allOrg.size()) {
			int j = 0;
			if((allOrg.get(i).privacyLevel == 2) && (!allOrg.get(i).name.equals(org.name))) {
				while(j < allOrg.get(i).createdTags.size()) {
					allTags.add(allOrg.get(i).createdTags.get(j));
					j++;
				}
			}
			i++;
		}
		boolean duplicate = false;
		i = 0;
		while(i < allTags.size()){
			if(allTags.get(i).name.equalsIgnoreCase(name)) {
				duplicate = true;
				break;
			}
			i++;
		}
		if(!duplicate) {
		Tag tag = new Tag(name,org);
		tag.save();
		System.out.println(tag.id);
		String description = user.username + " has created a new tag \"" + name + "\" in organization " + org.name;
		Notifications.sendNotification(org.creator.id, tag.id, "Tag", description);
		}

	}
	/**
	 * This is the main page for any tag the user clicks on
	 * 
	 * @author Omar Faruki
	 * 
	 * @param tagId
	 */
	public static void mainPage(long tagId) {
		Tag tag = Tag.findById(tagId);
		List<User> followers = tag.followers;
		List<Topic> topics = tag.taggedTopics;
		List<Organization> organizations = tag.organizations;
		List<MainEntity> entities = tag.entities;
		List<Idea> ideas = tag.taggedIdeas;
		render(tag,followers,topics,organizations,entities,ideas);
	}
}
