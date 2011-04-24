package models;

import java.util.ArrayList;

import play.db.jpa.Model;

public class MainEntity extends Model{
	String name;
	MainEntity parent;
	int privacyLevel;
	String description;
	ArrayList<MainEntity> subentities;
	
	//Organization organization;
	//ArrayList<User> organizer;
	//ArrayList<Topic> topicList;	
	//ArrayList<Tag> tagList;
	//ArrayList<Relationship> relationshipList;
	//ArrayList<Request> requestList;
	//ArrayList<Invitation> invitationList;	
	//Arraylist<RequestOfRelationship>
	
	public MainEntity (String n, String d, int p) {
		this.name = n;
		this.description = d;
		this.privacyLevel = p;
		this.parent = null;
	}
	
	public MainEntity (String n, String d, int p, MainEntity parent) {
		this.name = n;
		this.description = d;
		this.privacyLevel = p;
		this.parent = parent;
		parent.subentities.add(this);
	}
}
