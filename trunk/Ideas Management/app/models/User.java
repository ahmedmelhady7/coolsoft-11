package models;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class User extends Model {
	public String email;
	public String password;
	public String firstName;
	public String lastName;
	public String username;
	public String country;
	public Date dateofBirth;
	public int communityContributionCounter;

	// Arraylist<roles> roles;
	// Arraylist<NP> nofications;
	// Arraylist<Request> requests;
	// Arraylist<Comment> commentsPosted
	// Arraylist<Notifications> notifications ;
	// Arraylist<LinkDuplicates> linkDuplicates;
	// Arraylist<AssignRequests> assignRequests;
	// Arraylist<volunteerRequests> volunteerRequests;
	// Arraylist<Invitation> invitations;
	// Arraylist<RequestToJoin> requestsToJoin;
	// Arraylist<RequestOfRelationship> requestRelationship;
	// Arraylist<TopicInvitation> topicInvitations;

	public User(String email, String password, String firstName,
			String lastName, String username, int communityContributionCounter,
			Date dateofBirth, String country) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.communityContributionCounter = communityContributionCounter;
		this.dateofBirth = dateofBirth;
		this.country = country;
		this.lastName = lastName;

	}
}
