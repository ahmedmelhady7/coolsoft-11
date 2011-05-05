package controllers;

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


	public static void createTag(@Required String name, long orgId) {
		User user = Security.getConnected();
		Organization org = Organization.findById(orgId);
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			render(org);
		}
		Tag existing_tag = Tag.find("name like '" + name + "'").first();
		if (existing_tag != null) {
			flash.error("Tag already exists!" + "\n\t\t"
					+ "Please choose another tag name.");
			render(org);
		}
		Tag tag = new Tag(name,org);
		String description = user.username + " has created a new tag \"" + name + "\" in organization " + org.name;
		Notifications.sendNotification(org.creator.id, tag.id, "tag", description);
        tag.save();
		
		flash.success("Your tag has been created.");
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
