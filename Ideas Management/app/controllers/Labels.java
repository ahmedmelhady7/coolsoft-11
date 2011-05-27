package controllers;

import java.util.ArrayList;
import java.util.List;

import play.mvc.With;

import models.Idea;
import models.Label;
import models.User;

/**
 * @author Abdalrahman Ali
 */

@With(Secure.class)
public class Labels extends CoolCRUD
{
	
	/**
	 * This method renders a page that shows all the user's labels
	 */
	public static void showAllLabels()
	{
		User user = Security.getConnected();
		notFoundIfNull(user);
		render(user);
	}
	
	/**
	 * This method renders the page that allows the user to create a label
	 */
	public static void createLabel()
	{
		User user = Security.getConnected();
		notFoundIfNull(user);
		render(user);
	}
	
	/**
	 * This method creates a label
	 * 
	 * @param name
	 * 		the name of the label to be created
	 * @param ideas
	 * 		array of the ids of the Ideas added to this label
	 * @return
	 * 		returns 1 upon creating a label and 0 if exists a label with the same name
	 */
	public static int doCreateLabel(String name,long [] ideas)
	{
		User user = Security.getConnected();
		
		for(Label label : user.myLabels)
			if(label.name.equals(name))
				return 0;
		
		ArrayList<Idea> choosenIdeas = new ArrayList<Idea>();
		
		if(ideas != null)
		{
			for(long ideaId : ideas)
			{
				Idea tmpIdea = Idea.findById(ideaId);
				choosenIdeas.add(tmpIdea);
			}
			
			Label label = new Label(name,user,choosenIdeas);
			label.save();
			return 1;
		}
		else
		{
			Label label = new Label(name,user);
			label.save();
			return 1;
		}
	}
	
	/**
	 * This method deletes a label 
	 * 
	 * @param labelId
	 * 		the id of the label to be deleted
	 */
	public static void deleteLabel(long labelId)
	{
		Label label = Label.findById(labelId);
		label.deleteLabel();
		showAllLabels();
	}
	
	/**
	 * this method renders the page that shows a label 
	 * 
	 * @param labelId
	 * 		the id of the label to be shown
	 */
	public static void showLabel(long labelId)
	{
		User user = Security.getConnected();
		Label label = Label.findById(labelId);
		
		List<Idea> otherIdeas = user.ideasCreated;
		
		for(int i =0;i<otherIdeas.size();i++)
			if(label.ideas.contains(otherIdeas.get(i)))
				otherIdeas.remove(i);
		
		notFoundIfNull(user);
		notFoundIfNull(label);
		notFoundIfNull(otherIdeas);
		
		render(user,label,otherIdeas);
	}
	
	/**
	 * this method removes Ideas from a label
	 * 
	 * @param labelId
	 * 		the label to remove the ideas from
	 * @param ideas
	 * 		an array of the ids of the ideas to be removed from the label
	 */
	public static void removeIdeas(long labelId, long [] ideas)
	{
		Label label = Label.findById(labelId);
		
		for(long ideaId : ideas)
		{
			Idea tmpIdea = Idea.findById(ideaId);
			label.removeIdea(tmpIdea);
		}
		
		label.save();
	}
	
	/**
	 * this method adds ideas to a label
	 * 
	 * @param labelId
	 * 		the label to add the ideas to
	 * @param ideas
	 * 		an array of the ids of the ideas to be added to the label
	 */
	public static void addIdeas(long labelId, long [] ideas)
	{
		Label label = Label.findById(labelId);
		
		for(long ideaId : ideas)
		{
			Idea tmpIdea = Idea.findById(ideaId);
			label.addIdea(tmpIdea);
		}
		
		label.save();
	}
	
	/**
	 * this method changes a label's name
	 * 
	 * @param labelId
	 * 		the label to change it's name
	 * @param newName
	 * 		the new name to assign to the label
	 */
	public static void changeName(long labelId,String newName)
	{
		Label label = Label.findById(labelId);
		label.name = newName;
		label.save();
	}
	
	
}
