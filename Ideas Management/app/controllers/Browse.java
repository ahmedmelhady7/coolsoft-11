package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Idea;
import models.Item;
import models.MainEntity;
import models.Organization;
import models.Tag;
import models.Topic;
import models.User;

import play.mvc.Controller;
import play.mvc.With;

/**
 * 
 * @author Fady Amir
 * 
 * @story C4S05 : browse
 * 
 */
@With(Secure.class)
public class Browse extends Controller {

	// public static List<Organization> listOfOrganizations =
	// Organization.findAll();

	public static void index() {
		render();
	}

	/**
	 * @Author Fady Amir
	 * @Story C4S05
	 * 
	 *        this method loops on the list of organizations to browse them
	 *        appropiatly
	 * 
	 */
	public static void browse() {
		User u = Security.getConnected();
		List<Organization> listOfOrganizations = Organization.findAll();
		List<Organization> temp = listOfOrganizations;
		for (int i = 0; i < listOfOrganizations.size(); i++) {
			if (!Users.getEnrolledUsers(listOfOrganizations.get(i)).contains(u)) {
				switch (listOfOrganizations.get(i).privacyLevel) {
				case 0:
					temp.remove(listOfOrganizations.get(i));
					break;
				default:
					break;
				}
			}
		}
		listOfOrganizations = temp;
		render(listOfOrganizations);

	}

}
