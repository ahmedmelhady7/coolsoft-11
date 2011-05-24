package controllers;

import java.util.ArrayList;
import java.util.List;

import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;
import models.Idea;
import models.Item;
import models.Log;
import models.MainEntity;
import models.Organization;
import models.Tag;
import models.Topic;
import models.User;

@With(Secure.class)
public class Tags extends CoolCRUD {

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
//		List<Tag> allTags = new ArrayList<Tag>();
		int i = 0;
//		while (i < org.relatedTags.size()) {
//			allTags.add(org.relatedTags.get(i));
//			i++;
//		}
//		i = 0;
//		while (i < allOrg.size()) {
//			int j = 0;
//			if ((allOrg.get(i).privacyLevel == 2)
//					&& (!allOrg.get(i).name.equals(org.name))) {
//				while (j < allOrg.get(i).createdTags.size()) {
//					allTags.add(allOrg.get(i).createdTags.get(j));
//					j++;
//				}
//			}
//			i++;
//		}
		boolean duplicate = false;
		if(org.privacyLevel == 2) {
			while(i < org.relatedTags.size()) {
				if (org.relatedTags.get(i).name.equalsIgnoreCase(name)) {
					duplicate = true;
					break;
				}
				i++;
			}
			if(!duplicate) {
				i = 0;
				while (i < allOrg.size()) {
					if (allOrg.get(i).privacyLevel == 2) {
						int j = 0;
						while (j < allOrg.get(i).relatedTags.size()) {
							if (allOrg.get(i).relatedTags.get(j).name.equalsIgnoreCase(name)) {
								duplicate = true;
								break;
							}
							j++;
						}
					}
					i++;
				}
			}
		}
		else {
			i = 0;
			while (i < org.relatedTags.size()) {
				if (org.relatedTags.get(i).name.equalsIgnoreCase(name)) {
					duplicate = true;
					break;
				}
				i++;
			}
		}
//		i = 0;
//		while (i < allTags.size()) {
//			if (allTags.get(i).name.equalsIgnoreCase(name)) {
//				duplicate = true;
//				break;
//			}
//			i++;
//		}
		if (!duplicate) {
			Tag tag = new Tag(name, org, user);
			tag.organizations.add(org);
			tag.save();
			org.relatedTags.add(tag);
			org.createdTags.add(tag);
			org.save();
			user.createdTags.add(tag);
			user.save();
			Log.addUserLog("User " + user.username + " created a new Tag \"" + name + "\" in Organization \"" + org.name + "\"", user,org,tag);
			String description = user.username + " has created a new tag \""
					+ name + "\" in organization " + org.name;
			Notifications.sendNotification(org.creator.id, tag.id, "Tag",
					description);
			flash.success("Your tag has been created!!");
		}
		else {
			flash.error("There is already a tag with that same name!!");
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
		boolean allowed = false;
		if (user.isAdmin || user.equals(tag.createdInOrganization.creator) || user.equals(tag.creator)) {
			allowed = true;
		}
		boolean follower = user.followingTags.contains(tag);
		boolean canCreateRelationship = TagRelationships.isAllowedTo(tagId);
		render(tag, followers, topics, organizations, entities, ideas, follower, user, canCreateRelationship, allowed);
	}
	
	/**
	 * This method edits a certain tag
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S38
	 * 
	 * @param tagId
	 * 			The id of the tag
	 * 
	 * @param name
	 * 			The new name of the tag
	 */
	public static void edit(long tagId, String name) {
		User user = Security.getConnected();
		if(!name.equals("")) {
		Tag tag = Tag.findById(tagId);
		Organization tagOrganization = tag.createdInOrganization;
		List<Tag> tags = new ArrayList<Tag>();
		List<Organization> allOrganizations = Organization.findAll();
		boolean duplicate = false;
		int i = 0;
		while(i < allOrganizations.size()) {
			if (allOrganizations.get(i).privacyLevel == 2) {
				int j = 0;
				while (j < allOrganizations.get(i).createdTags.size()) {
					tags.add(allOrganizations.get(i).createdTags.get(j));
					j++;
				}
			}
			i++;
		}
		i = 0;
		if((tagOrganization.privacyLevel == 2)) {
			while (i < tags.size()) {
				if (tags.get(i).name.equalsIgnoreCase(name)) {
					duplicate = true;
					break;
				}
				i++;
			}
		}
		else {
			while (i < tagOrganization.createdTags.size()) {
				if (tagOrganization.createdTags.get(i).name.equalsIgnoreCase(name)) {
					duplicate = true;
					break;
				}					
			i++;
			}
		}
		if(!duplicate) {
			String oldName = tag.name;
			tag.name = name;
			tag.save();
			Log.addUserLog("User " + user.username + " changed the name of the Tag \"" + oldName + "\" to \"" + tag.name + "\"", user,tag,tagOrganization);
			flash.success("DONE");
		}
		else {
			flash.error("There is already a tag with the same name");
		}
		}
		else {
			flash.error("Please insert a valid name");
		}
		Tags.mainPage(tagId);
	}
	
	/**
	 * This method deletes a tag
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S38
	 * 
	 * @param tagId
	 * 			The id of the tag that will be deleted
	 */
	public static boolean delete(long tagId) {
		Tag tag = Tag.findById(tagId);
		Organization organization = tag.createdInOrganization;
		User user = Security.getConnected();
		List<Organization> allOrganizations = Organization.findAll();
		List<Topic> topics = Topic.findAll();
		List<Item> items = Item.findAll();
		List<Idea> ideas = Idea.findAll();
		List<User> followers = User.findAll();
		int i = 0;
		while (i < allOrganizations.size()) {
			if(allOrganizations.get(i).relatedTags.contains(tag)) {
				allOrganizations.get(i).relatedTags.remove(tag);
				allOrganizations.get(i).save();
			}
			i++;
		}
		tag.creator.createdTags.remove(tag);
		tag.creator.save();
		i = 0;
		while (i < tag.entities.size()) {
			tag.entities.get(i).tagList.remove(tag);
			tag.entities.get(i).save();
			i++;
		}
		i = 0;
		while (i < followers.size()) {
			if(followers.get(i).followingTags.contains(tag)) {
				followers.get(i).followingTags.remove(tag);
				followers.get(i).save();
			}
			i++;
		}
		i = 0;
		while (i < topics.size()) {
			if(topics.get(i).tags.contains(tag)) {
				topics.get(i).tags.remove(tag);
				topics.get(i).save();
			}
			i++;
		}
		i = 0;
		while (i < ideas.size()) {
			if(ideas.get(i).tagsList.contains(tag)) {
				ideas.get(i).tagsList.remove(tag);
				ideas.get(i).save();
			}
			i++;
		}
		i = 0;
		while (i < items.size()) {
			if(items.get(i).tags.contains(tag)) {
				items.get(i).tags.remove(tag);
				items.get(i).save();
			}
			i++;
		}
		organization.createdTags.remove(tag);
		organization.save();
		
		for(int j = 0; j < tag.relationsSource.size(); j++){
			TagRelationships.delete(tag.relationsSource.get(j).id);
		}
		for(int j = 0; j < tag.relationsDestination.size(); j++){
			TagRelationships.delete(tag.relationsDestination.get(j).id);
		}
		Log.addUserLog("User " + user.username + " deleted the Tag \"" + tag.name + "\"", user,organization);
		tag.delete();
		return true;
	}
	
	/**
	 * This method is used to call the delete method and then renders the organization page
	 * 
	 * @author Omar Faruki
	 * 
	 * @story C2S38
	 * 
	 * @param tagId
	 * 			The id of the deleted tag
	 */
	public static void deleteTag(long tagId) {
		Tags.delete(tagId);
		Organizations.mainPage();
	}
}
