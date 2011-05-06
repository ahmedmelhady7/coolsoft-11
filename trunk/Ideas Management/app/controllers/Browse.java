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

	public static void browse() {

		List<Organization> listOfOrganizations = Organization.findAll();
		for(int i=0;i< listOfOrganizations.size();i++)
		{
			
			
		}
		render(listOfOrganizations);

	}

}
