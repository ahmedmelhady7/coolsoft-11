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
	 * renders the related tag and the list of other tags in the organization to the view
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S5
	 * 
	 * @param tagId : id of the tag being related
	 * 
	 * @param organizationId : id of the organization the tag belongs to
	 */
	public static void createRelation(long tagId, long organizationId) {

		Tag tag = Tag.findById(tagId);
		Organization organization = Organization.findById(organizationId);
		List<Tag> tagList = null;

		if (organization.createdTags != null) {
			tagList = organization.createdTags;
			tagList.remove(tag);
		}
		
		render(tag, tagList);
	}

	/**
	 * This Method adds a user to the list of followers in a given tag
	 * 
	 * @author Mohamed Hisham
	 * 
	 * @story C2S11
	 * 
	 * @param tagId
	 *            : the id of the tag that the user is following
	 * 
	 * @param userId
	 *            : the id of the user who follows
	 * 
	 */
	public static void followTag(long tagId) {
		Tag tag = Tag.findById(tagId);
		User user = Security.getConnected();
		if (!user.followingTags.contains(tag)) {
			tag.follow(user);
			user.follow(tag);
			user.save();
			tag.save();
		}
	}

	/**
	 * This method opens the page with all the forms for creating a new tag
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S9
	 * 
	 * @param orgId
	 *            the current organization that the user wants to create a tag
	 *            in
	 */

	public static void createTag(long orgId) {
		Organization org = Organization.findById(orgId);
		render(org);
		// if (validation.hasErrors()) {
		// params.flash();
		// validation.keep();
		// render(org);
		// }
		//
		//
		// flash.success("Your tag has been created.");
	}

	/**
	 * This method is responsible for creating a new Tag
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S9
	 * 
	 * @param name
	 *            The name of the tag
	 * 
	 * @param orgId
	 *            The organization id of the organization where the tag will
	 *            be created
	 */
	public static void createTagg(String name, long orgId) {
		User user = Security.getConnected();
		Organization org = Organization.findById(orgId);
		List<Organization> allOrg = Organization.findAll();
		List<Tag> allTags = new ArrayList<Tag>();
		int i = 0;
		while (i < org.createdTags.size()) {
			allTags.add(org.createdTags.get(i));
			i++;
		}
		i = 0;
		while (i < allOrg.size()) {
			int j = 0;
			if ((allOrg.get(i).privacyLevel == 2)
					&& (!allOrg.get(i).name.equals(org.name))) {
				while (j < allOrg.get(i).createdTags.size()) {
					allTags.add(allOrg.get(i).createdTags.get(j));
					j++;
				}
			}
			i++;
		}
		boolean duplicate = false;
		i = 0;
		while (i < allTags.size()) {
			if (allTags.get(i).name.equalsIgnoreCase(name)) {
				duplicate = true;
				break;
			}
			i++;
		}
		if (!duplicate) {
			Tag tag = new Tag(name, org, user);
			tag.save();
			System.out.println(tag.id);
			String description = user.username + " has created a new tag \""
					+ name + "\" in organization " + org.name;
			Notifications.sendNotification(org.creator.id, tag.id, "Tag",
					description);
		}
		Organizations.viewProfile(orgId);
	}

	/**
	 * This is the main page for any tag the user clicks on, it displays all
	 * info for a specific tag
	 * 
	 * @author Omar Faruki
	 * 
	 * @param tagId
	 *             The id of a specific tag
	 */
	public static void mainPage(long tagId) {
		Tag tag = Tag.findById(tagId);
		List<User> followers = tag.followers;
		List<Topic> topics = tag.taggedTopics;
		List<Organization> organizations = tag.organizations;
		List<MainEntity> entities = tag.entities;
		List<Idea> ideas = tag.taggedIdeas;
		User user = Security.getConnected();
		boolean follower = user.followingTags.contains(tag);
//		boolean canCreateRelationship = TagRelationships.isAllowedTo(tagId);
		render(tag, followers, topics, organizations, entities, ideas, follower, user/*, canCreateRelationship*/);
	}
}
