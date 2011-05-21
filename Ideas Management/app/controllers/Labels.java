package controllers;

import java.util.ArrayList;
import java.util.List;

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
		
		if(ideas != null)
		{
			System.out.println("not null");
			for(long ideaId : ideas)
			{
				Idea tmpIdea = Idea.findById(ideaId);
				choosenIdeas.add(tmpIdea);
				System.out.println("Idea added to Label "+name+" : "+tmpIdea.id);
			}
			
			Label label = new Label(name,user,choosenIdeas);
			label.save();
		}
		else
		{
			System.out.println("tsadda2 kanet null ya m3alem");
			Label label = new Label(name,user);
			label.save();
		}
	}
	
	public static void deleteLabel(long labelId)
	{
		Label label = Label.findById(labelId);
		label.deleteLabel();
		showAllLabels();
	}
	
	public static void showLabel(long labelId)
	{
		User user = Security.getConnected();
		Label label = Label.findById(labelId);
		
		List<Idea> otherIdeas = user.ideasCreated;
		
		for(int i =0;i<otherIdeas.size();i++)
			if(label.ideas.contains(otherIdeas.get(i)))
				otherIdeas.remove(i);
		
		render(user,label,otherIdeas);
	}
	
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
	
	public static void changeName(long labelId,String newName)
	{
		Label label = Label.findById(labelId);
		label.name = newName;
		label.save();
		//redirect("/labels/showLabel?labelId="+labelId);
	}
	
	
}
