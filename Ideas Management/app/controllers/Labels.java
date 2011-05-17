package controllers;

import java.util.ArrayList;

import models.Idea;
import models.Label;
import models.User;

public class Labels extends CRUD
{
	
	public static void showAllLabels()
	{
		User user = Security.getConnected();
		render(user);
	}
	
	public static void createLabel()
	{
		User user = Security.getConnected();
		render(user);
	}
	
	public static void doCreateLabel(String name,long [] ideas)
	{
		User user = Security.getConnected();
		
		ArrayList<Idea> choosenIdeas = new ArrayList<Idea>();
		
		for(long ideaId : ideas)
		{
			Idea tmpIdea = Idea.findById(ideaId);
			choosenIdeas.add(tmpIdea);
		}
		
		Label label = new Label(name,user,choosenIdeas);
		label.save();
		//showAllLabels();
		//redirect("/Labels/showAllLabels");
	}
	
	public static void deleteLabel(long labelId)
	{
		Label label = Label.findById(labelId);
		label.deleteLabel();
		showAllLabels();
	}
	
	public static void showLabel(long labelId)
	{
		Label label = Label.findById(labelId);
		render(label);
	}
	
	public static void removeIdeas(long labelId, ArrayList<Idea> ideas)
	{
		Label label = Label.findById(labelId);
		
		for(Idea idea : ideas)
			label.removeIdea(idea);
		
		label.save();
		showLabel(labelId);
	}
	
	public static void addIdeas(long labelId, ArrayList<Idea> ideas)
	{
		Label label = Label.findById(labelId);
		
		for(Idea idea : ideas)
			label.addIdea(idea);
		
		label.save();
		showLabel(labelId);
	}
	
	public static void changeName(long labelId,String newName)
	{
		Label label = Label.findById(labelId);
		label.name = newName;
		label.save();
		//showLabel(labelId);
	}
	
	
}
