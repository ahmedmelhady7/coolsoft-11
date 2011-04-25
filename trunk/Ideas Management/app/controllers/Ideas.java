/**
 * 
 */
package controllers;

import java.util.ArrayList;

import models.Idea;
import models.Tag;
import models.Topic;
import models.User;

/**
 * @author ${Ahmed El-Hadi}
 * 
 */
public class Ideas extends CRUD {
	
	/*
	 * @author Abdalrahman Ali
	 * 
	 * this method saves an idea as a draft
	 * 
	 * @param title
	 * 		the title of the idea
	 * @param body
	 * 		the body of the idea
	 * @param topic
	 * 		the topic that the idea belongs to
	 * @param user
	 * 		the user saving this idea
	 * 
	 * @return void
	 * 
	 * */
	
	public static void saveDraft(String title,String body,Topic topic,User user )
	{
		Idea idea = new Idea(title,body,user,topic,true);
	}
	
	/*
	 * @author Abdalrahman Ali
	 * 
	 * this method posts an idea that was saved as a draft
	 * 
	 * @param idea
	 * 		the saved idea
	 * 
	 * @return void
	 * 
	 * */
	
	public static void postDraft(Idea idea)
	{
		idea.isDraft = false;
	}
}
