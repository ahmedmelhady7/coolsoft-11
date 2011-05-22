package models;

import play.db.jpa.Model;


import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;


@Entity
public class TopicRequest extends CoolModel{
	
		/**
		 * The user requesting the topic
		 */
		@Required
		@ManyToOne
		public User requester;

		/**
		 * The entity that the user wants the requested topic to be posted in
		 */
		@Required
		@ManyToOne
		public MainEntity entity;

	
		/**
		 * A title of the requested topic
		 */
		@Required
		public String title;
		
		/**
		 * The description of the requested topic
		 */
		@Required
		public String description;
		
		/**
		 * The privacy level of the requested topic
		 */
		@Required
		public int privacyLevel;
		
		/**
		 * The message the requester adds to his request
		 */
		public String message;
		
		
		
		
		

		/**
		 * Constructor for TopicRequest
		 * 
		 * @author Alia El Bolock
		 * 
		 * @param requester
		 * 
		 * @param entity
		 * 
		 * @param title
		 * 
		 * @param description
		 * 
		 * @param privacyLevel
		 * 
		 * @param message
		 */
		public TopicRequest(User requester, MainEntity entity, 
				String title, String description, int privacyLevel, String message) {
			this.requester = requester;
			this.entity = entity;
			this.title = title;
			this.description = description;
			this.privacyLevel = privacyLevel;
			this.message = message;
		}
		//TODO

		/**
		 * Overrides the toString method to get the title of the requested topic and the requester of the topic
		 * 
		 * @author Alia El Bolock
		 * 
		 * @return String
		 */
		public String toString() {
			
			String topicRequest = "Title: " + this.title + " Requester: " + this.requester;
			return topicRequest;
		}
		
	}


