package controllers;

import java.util.ArrayList;
import java.util.List;

import play.mvc.With;

import models.Item;
import models.MainEntity;
import models.Plan;
import models.Tag;
import models.User;
import models.*;

@With(Secure.class)
public class Items extends CRUD {

	/**
	 * 
	 * This Method renders the view of the list of assigned items of the logged
	 * in user
	 * 
	 * @author Salma Osama
	 * 
	 * @story C5S11
	 * 
	 */

	public static void viewAssignedItems() {
		User user = Security.getConnected();
		long userId = user.id;
		List<Item> assignedItems = user.itemsAssigned;
		render(assignedItems, userId, user);
	}

	/**
	 * 
	 * This method changes the item with the given id's status from "New" (0) to
	 * "In progress" (1).
	 * 
	 * @author Mohamed Mohie
	 * 
	 * @story C5S13
	 * 
	 * @param id
	 *            : the id of the item to be started
	 */
	public static void startItem(long id) {
		User user = Security.getConnected();
//		long itemId = Long.parseLong(id);
		Item item = Item.findById(id);
		List<User> userToNotifyList = new ArrayList<User>();
		userToNotifyList.addAll(item.plan.topic.getOrganizer());
		for (int i = 0; i < item.assignees.size(); i++) {
			if (item.assignees.get(i).id != user.id && !userToNotifyList.contains(item.assignees.get(i)))
				userToNotifyList.add(item.assignees.get(i));
		}
		if (item.status == 0) {
			System.out.println("it is started");
			item.status = 1;
			item.save();
			String description = "Work has started on the following item: " + item.summary
					+ " by user" + user.username + ".";
			for (User userToNotify : userToNotifyList) {
				Notifications.sendNotification(userToNotify.id, item.plan.id,
						"plan", description);
			}
		}
	}

	/**
	 * 
	 * This method changes the item with the given id's status from
	 * "In progress" (1) to "Done" (2) and vice versa.
	 * 
	 * @author Mohamed Mohie
	 * 
	 * @story C5S13
	 * 
	 * @param id
	 *            : the id of the item to be marked as done or in progress.
	 */
	public static boolean toggleItem(long id) {
		User user = Security.getConnected();
//		long itemId = Long.parseLong(id);
		Item item = Item.findById(id);
		List<User> userToNotifyList = new ArrayList<User>();
		userToNotifyList.addAll(item.plan.topic.getOrganizer());
		for (int i = 0; i < item.assignees.size(); i++) {
			if (item.assignees.get(i).id != user.id && !userToNotifyList.contains(item.assignees.get(i)))
				userToNotifyList.add(item.assignees.get(i));
		}
		switch (item.status) {
		case 1:
			item.status = 2;
			String description = item.summary + ": Item now marked done by user: " + user.username + "!";
			for (User userToNotify : userToNotifyList) {
				Notifications.sendNotification(userToNotify.id, item.plan.id,
						"plan", description);
			}
			break;
		case 2:
			item.status = 1;
			String descriptionIfInProgress = item.summary + ": Item now marked in progress by user: " + user.username + ".";
			for (User userToNotify : userToNotifyList) {
				Notifications.sendNotification(userToNotify.id, item.plan.id,
						"plan", descriptionIfInProgress);
			}
			break;
		default:
			break;
		}
		item.save();
        return true;
	}
	/**
	 * This method first checks if the user is allowed to edit in the plan so he can tag the item, searches
	 * for the tag in the global list of tags, if found it checks if
	 * the item had the same tag already or add the new one to the list if not
	 * it creates a new tag, save it and add it to the list 
	 * 
	 * @author Yasmine Elsayed
	 * 
	 * @story C5S15
	 * 
	 * @param itemID
	 *            : the item that is being tagged
	 * 
	 * @param tag
	 *            : the tag that is being added
	 * 
	 */
	public static void tagItem(long itemId, String tag) {

		boolean tagAlreadyExists = false;
		boolean userNotAllowed = false;
		boolean tagExists = false;
		List<Tag> listOfTags = new ArrayList<Tag>();
		List<Tag> globalListOfTags = new ArrayList<Tag>();
		globalListOfTags = Tag.findAll();
		System.out.println("global = " + globalListOfTags.size());
		User user = (User) Security.getConnected();
		Item item = (Item) Item.findById(itemId);
		Plan plan = item.plan;
		MainEntity entity = plan.topic.entity;

		if (!tag.equals("@@")) {

			if (Users.isPermitted(user, "edit an action plan", plan.topic.id,
			"topic")) {
				// user not allowed
				System.out.println("user not allowed");
				userNotAllowed = true;
			} else {
				for (int i = 0; i < globalListOfTags.size(); i++) {
					if (globalListOfTags.get(i).createdInOrganization.privacyLevel == 2
							|| plan.topic.entity.organization
									.equals(globalListOfTags.get(i).createdInOrganization)) {
						listOfTags.add(globalListOfTags.get(i));
					}
				}
				for (int i = 0; i < listOfTags.size(); i++) {
					if (listOfTags.get(i).getName().equalsIgnoreCase(tag)) {
						if (!item.tags.contains(listOfTags.get(i))) {
							item.tags.add(listOfTags.get(i));
							listOfTags.get(i).taggedItems.add(item);
							listOfTags.get(i).save();
							System.out.println("existing tag added");
							
						} else {
							// tag already exists error message
							System.out.println("tag already exists");
							tagAlreadyExists = true;
						}
						tagExists = true;
					}
				}

				if (!tagExists) {
					Tag temp = new Tag(tag,
							plan.topic.entity.organization, user);
					item.tags.add(temp);
					temp.taggedItems.add(item);
					temp.save();
					System.out.println("new tag created and added");
				}

				
			}

		}
		item.save();
		List<Tag> tags = item.tags;
		render(tagAlreadyExists, userNotAllowed, tags, itemId);
	}

}
