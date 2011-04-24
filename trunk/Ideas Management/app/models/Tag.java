package models;

import java.util.ArrayList;

import javax.persistence.Entity;

import play.db.jpa.Model;
@Entity
public class Tag extends Model{
	String name;
	ArrayList<Tag> related_tags;
	//String creator;	We might still need this attribute;
	
	public Tag(String name) {
		this.name = name;
	}
	public Tag(String name, ArrayList<Tag> tags) {
		this.name = name;
		this.related_tags = tags;
	}
}
