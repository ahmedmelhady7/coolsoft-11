package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Label extends CoolModel
{
	@Required
	public String name;
	
	/**
	 * @author Abdalrahman Ali
	 * 
	 * the User created this Label
	 * */
	
	@Required
	@ManyToOne
	public User user;
	
	/**
	 * @author Abdalrahman Ali
	 * 
	 * Ideas mapped to this label
	 * */
	
	public ArrayList<Idea> ideas;
	
	
	
	/**
	 * @author Abdalrahman Ali
	 * 
	 * @param name
	 * 		the label name
	 * @param user
	 * 		the user created this label
	 */
	
	public Label(String name, User user)
	{
		this.name = name;
		this.user = user;
		this.ideas = new ArrayList<Idea>();
	}
	
	public Label(String name, User user, ArrayList<Idea> ideas)
	{
		this.name = name;
		this.user = user;
		this.ideas = ideas;
	}
	
	/**
	 * @author Abdalrahman Ali
	 * @param idea
	 * 		idea to be labeled by this label
	 */
	public void addIdea(Idea idea)
	{
		ideas.add(idea);
	}
	
	/**
	 * @author Abdalrahman Ali
	 * @param idea
	 * 		idea to be removed from this label
	 * @return
	 * 		true if the idea is found and deleted, false otherwise
	 */
	public boolean removeIdea(Idea idea)
	{
		if(ideas.contains(idea))
		{
			ideas.remove(idea);
			return true;
		}
		else return false;
	}
	
	public void deleteLabel()
	{
		this.user.myLabels.remove(this);
		this.delete();
	}
	
}
