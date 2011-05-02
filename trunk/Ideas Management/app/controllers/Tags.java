package controllers;

import play.data.validation.Required;
import play.mvc.Controller;
import models.MainEntity;
import models.Organization;
import models.Tag;
import models.User;

public class Tags extends Controller {
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
	 * @param org
	 *            : the current organization that the user is in
	 */

	public static void createTag(@Required String name, Organization org) {
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			render(org.createTag);
		}
		Tag existing_tag = Tag.find("name like '" + name + "'").first();
		if (existing_tag != null) {
			flash.error("Tag already exists!" + "\n\t\t"
					+ "Please choose another tag name.");
			render(org.createTag);
		}
		Tag tag = new Tag(name);
		tag.save();
		flash.success("Your tag has been created.");
	}
}
