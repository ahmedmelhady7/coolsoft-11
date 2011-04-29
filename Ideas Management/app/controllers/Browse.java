package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Idea;
import models.Item;
import models.MainEntity;
import models.Organization;
import models.Tag;
import models.Topic;


import play.mvc.Controller;

/**
 * 
 * @author Fady Amir
 * 
 * @story C4S05 : browse
 * 
 */
public class Browse extends Controller 
{
	
	public static List<Organization> listOfOrganizations = Organization.findAll();
	static Organization x ;
	
	public static void index() 
	{
        render();
    }
    
    public static void browse() 
    { 
    	
        //List<Organization> listOfOrganizations = Organization.findAll();
        
        
        //listOfOrganizations = Organization.findAll();
        render(listOfOrganizations);
        
        
        /*find by id
        x = Organization.findById(0);
        render(x);
        */
    }
}

