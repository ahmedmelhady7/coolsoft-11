package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Idea;
import models.MainEntity;
import models.NotificationProfile;
import models.Organization;
import models.Plan;
import models.Tag;
import models.Topic;
import models.User;
import play.mvc.Controller;
import play.mvc.With;

/**
 * This class is the notification profile's controller.
 * 
 * @author Ahmed Maged
 *
 */

@With(Secure.class)
public class NotificationProfiles extends CoolCRUD {
	
	/**
	 * Renders the view were the user can change his preferences.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 */	
	
	public static void alterPreferences(String type) {
		User user = Security.getConnected();
		List<NotificationProfile> npList = Users.getNotificationProfilesOf(type);
		render(user, npList);
	}
	
	/**
	 * Enables the preferences of the user according to the list
	 * provided as input to the method.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @param b 
	 * 		long[] The list of notification sources to be disabled.	
	 * 
	 */
	
	public static void enablePreferences(long[] b) {
		for(int i = 0; i < b.length; i++) {
			NotificationProfile np = NotificationProfile.findById(b[i]);
			np.enabled = true;
			np.save();
			}			
	}
	
	/**
	 * This method disables the preferences of the user according to the list
	 * provided as input to the method.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @param a 
	 * 		long[] The list of notification sources to be disabled.
	 * 
	 */
	public static void disablePreferences(long[] a, boolean cascade) {		
		for(int i = 0; i < a.length; i++) {
			NotificationProfile notificationProfile = NotificationProfile.findById(a[i]);
			notificationProfile.enabled = false;
			notificationProfile.save();
			}			
	}
	
	/**
	 * Enables the preferences of the user according to the list
	 * provided as input to the method with cascading.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @param b 
	 * 		long[] The list of notification sources to be disabled.	
	 * 
	 */
	
	public static void enableCascadePreferences(long[] bb) {
		if (bb == null) {
			System.out.println("Null22222");
		}
		User user = Security.getConnected();
		if (bb == null) {
			System.out.println("Null");
		}
		for(int i = 0; i < bb.length; i++) {
			NotificationProfile notificationProfile = NotificationProfile.findById(bb[i]);
			if (notificationProfile.notifiableType.equals("Organization")) {
				Organization organization = Organization.findById(notificationProfile.notifiableId);
				for (int j = 0; j < organization.entitiesList.size(); j++) {
					MainEntity entity = organization.entitiesList.get(j);
					if (entity == null) 
						continue;
					boolean containEntity = false;
					for (int n = 0; n < user.notificationProfiles.size(); n++) {
						if (user.notificationProfiles.get(n).notifiableType.equals("Entity")
								&& user.notificationProfiles.get(n).notifiableId == entity.id) {
							NotificationProfile userNotificationProfile = user.notificationProfiles.get(n);
							userNotificationProfile.enabled = true;
							userNotificationProfile.save();
							containEntity = true;
						}
					}
					if (!containEntity) {
						NotificationProfile notificationProfile2 = new NotificationProfile(entity.id, "Entity", entity.name, user);
						notificationProfile2.save();
					}
					for (int k = 0; k < entity.topicList.size(); k++) {
						Topic topic = entity.topicList.get(k);
						if (topic == null) 
							continue;
						boolean containTopic = false;
						for (int n = 0; n < user.notificationProfiles.size(); n++) {
							if (user.notificationProfiles.get(n).notifiableType.equals("Topic")
									&& user.notificationProfiles.get(n).notifiableId == topic.id) {
								NotificationProfile userNotificationProfile = user.notificationProfiles.get(n);
								userNotificationProfile.enabled = true;
								userNotificationProfile.save();
								containTopic = true;
							}
						}
						if (!containTopic) {
							NotificationProfile notificationProfile2 = new NotificationProfile(topic.id, "Topic", topic.title, user);
							notificationProfile2.save();
						}
						for (int l = 0; l < topic.ideas.size(); l++) {
							Idea idea = topic.ideas.get(l);
							if (idea == null) 
								continue;
							boolean containIdea = false;
							for (int n = 0; n < user.notificationProfiles.size(); n++) {
								if (user.notificationProfiles.get(n).notifiableType.equals("Idea")
										&& user.notificationProfiles.get(n).notifiableId == idea.id) {
									NotificationProfile userNotificationProfile = user.notificationProfiles.get(n);
									userNotificationProfile.enabled = true;
									userNotificationProfile.save();
									containIdea = true;
								}
							}
							if (!containIdea) {
								NotificationProfile notificationProfile2 = new NotificationProfile(idea.id, "Idea", idea.title, user);
								notificationProfile2.save();
							}
						}
					}
					for (int z = 0; z < entity.tagList.size(); z++) {
						Tag tag = entity.tagList.get(z);
						if (tag == null) 
							continue;
						boolean containTag = false;
						for (int n = 0; n < user.notificationProfiles.size(); n++) {
							if (user.notificationProfiles.get(n).notifiableType.equals("Tag")
									&& user.notificationProfiles.get(n).notifiableId == tag.id) {
								NotificationProfile userNotificationProfile = user.notificationProfiles.get(n);
								userNotificationProfile.enabled = true;
								userNotificationProfile.save();
								containTag = true;
							}
						}
						if (!containTag) {
							NotificationProfile notificationProfile2 = new NotificationProfile(tag.id, "Tag", tag.name, user);
							notificationProfile2.save();
						}
					}
				}
			} else {
				if (notificationProfile.notifiableType.equals("Entity")) {
					MainEntity entity = MainEntity.findById(notificationProfile.notifiableId);
					for (int k = 0; k < entity.topicList.size(); k++) {
						Topic topic = entity.topicList.get(k);
						if (topic == null) 
							continue;
						boolean containTopic = false;
						for (int n = 0; n < user.notificationProfiles.size(); n++) {
							if (user.notificationProfiles.get(n).notifiableType.equals("Topic")
									&& user.notificationProfiles.get(n).notifiableId == topic.id) {
								NotificationProfile userNotificationProfile = user.notificationProfiles.get(n);
								userNotificationProfile.enabled = true;
								userNotificationProfile.save();
								containTopic = true;
							}
						}
						if (!containTopic) {
							NotificationProfile notificationProfile2 = new NotificationProfile(topic.id, "Topic", topic.title, user);
							notificationProfile2.save();
						}
						for (int l = 0; l < topic.ideas.size(); l++) {
							Idea idea = topic.ideas.get(l);
							if (idea == null) 
								continue;
							boolean containIdea = false;
							for (int n = 0; n < user.notificationProfiles.size(); n++) {
								if (user.notificationProfiles.get(n).notifiableType.equals("Idea")
										&& user.notificationProfiles.get(n).notifiableId == idea.id) {
									NotificationProfile userNotificationProfile = user.notificationProfiles.get(n);
									userNotificationProfile.enabled = true;
									userNotificationProfile.save();
									containIdea = true;
								}
							}
							if (!containIdea) {
								NotificationProfile notificationProfile2 = new NotificationProfile(idea.id, "Idea", idea.title, user);
								notificationProfile2.save();
							}
						}
					}
					for (int z = 0; z < entity.tagList.size(); z++) {
						Tag tag = entity.tagList.get(z);
						if (tag == null) 
							continue;
						boolean containTag = false;
						for (int n = 0; n < user.notificationProfiles.size(); n++) {
							if (user.notificationProfiles.get(n).notifiableType.equals("Tag")
									&& user.notificationProfiles.get(n).notifiableId == tag.id) {
								NotificationProfile userNotificationProfile = user.notificationProfiles.get(n);
								userNotificationProfile.enabled = true;
								userNotificationProfile.save();
								containTag = true;
							}
						}
						if (!containTag) {
							NotificationProfile notificationProfile2 = new NotificationProfile(tag.id, "Tag", tag.name, user);
							notificationProfile2.save();
						}
					}
				} else {
					if (notificationProfile.notifiableType.equals("Topic")) {
						Topic topic = Topic.findById(notificationProfile.notifiableId);
						for (int l = 0; l < topic.ideas.size(); l++) {
							Idea idea = topic.ideas.get(l);
							if (idea == null) 
								continue;
							boolean containIdea = false;
							for (int n = 0; n < user.notificationProfiles.size(); n++) {
								if (user.notificationProfiles.get(n).notifiableType.equals("Idea")
										&& user.notificationProfiles.get(n).notifiableId == idea.id) {
									NotificationProfile userNotificationProfile = user.notificationProfiles.get(n);
									userNotificationProfile.enabled = true;
									userNotificationProfile.save();
									containIdea = true;
								}
							}
							if (!containIdea) {
								NotificationProfile notificationProfile2 = new NotificationProfile(idea.id, "Idea", idea.title, user);
								notificationProfile2.save();
							}						
						}
					} 
				}
			}
			notificationProfile.enabled = true;
			notificationProfile.save();
		}
	}
	
	/**
	 * This method disables the preferences of the user according to the list
	 * provided as input to the method with cascading.
	 * 
	 * @author Ahmed Maged
	 * 
	 * @story C1S14
	 * 
	 * @param a 
	 * 		long[] The list of notification sources to be disabled.
	 * 
	 */
	public static void disableCascadePreferences(long[] aa) {
			User user = Security.getConnected();
			for(int i = 0; i < aa.length; i++) {
				NotificationProfile notificationProfile = NotificationProfile.findById(aa[i]);
				if (notificationProfile.notifiableType.equals("Organization")) {					
					Organization organization = Organization.findById(notificationProfile.notifiableId);
					for (int j = 0; j < organization.entitiesList.size(); j++) {
						MainEntity entity = organization.entitiesList.get(j);						
						boolean containEntity = false;
						for (int n = 0; n < user.notificationProfiles.size(); n++) {
							if (user.notificationProfiles.get(n).notifiableType.equals("Entity")
									&& user.notificationProfiles.get(n).notifiableId == entity.id) {
								NotificationProfile userNotificationProfile = user.notificationProfiles.get(n);
								userNotificationProfile.enabled = false;
								userNotificationProfile.save();
								containEntity = true;
							}
						}
						if (!containEntity) {
							NotificationProfile notificationProfile2 = new NotificationProfile(entity.id, "Entity", entity.name, user);
							notificationProfile2.enabled = false;
							notificationProfile2.save();
						}
						for (int k = 0; k < entity.topicList.size(); k++) {
							Topic topic = entity.topicList.get(k);
							boolean containTopic = false;
							for (int n = 0; n < user.notificationProfiles.size(); n++) {
								if (user.notificationProfiles.get(n).notifiableType.equals("Topic")
										&& user.notificationProfiles.get(n).notifiableId == topic.id) {
									NotificationProfile userNotificationProfile = user.notificationProfiles.get(n);
									userNotificationProfile.enabled = false;
									userNotificationProfile.save();
									containTopic = true;
								}
							}
							if (!containTopic) {
								NotificationProfile notificationProfile2 = new NotificationProfile(topic.id, "Topic", topic.title, user);
								notificationProfile2.enabled = false;
								notificationProfile2.save();
							}
							for (int l = 0; l < topic.ideas.size(); l++) {
								Idea idea = topic.ideas.get(l);
								boolean containIdea = false;
								for (int n = 0; n < user.notificationProfiles.size(); n++) {
									if (user.notificationProfiles.get(n).notifiableType.equals("Idea")
											&& user.notificationProfiles.get(n).notifiableId == idea.id) {
										NotificationProfile userNotificationProfile = user.notificationProfiles.get(n);
										userNotificationProfile.enabled = false;
										userNotificationProfile.save();
										containIdea = true;
									}
								}
								if (!containIdea) {
									NotificationProfile notificationProfile2 = new NotificationProfile(idea.id, "Idea", idea.title, user);
									notificationProfile2.enabled = false;
									notificationProfile2.save();
								}
							}
						}
						for (int z = 0; z < entity.tagList.size(); z++) {
							Tag tag = entity.tagList.get(z);
							boolean containTag = false;
							for (int n = 0; n < user.notificationProfiles.size(); n++) {
								if (user.notificationProfiles.get(n).notifiableType.equals("Tag")
										&& user.notificationProfiles.get(n).notifiableId == tag.id) {
									NotificationProfile userNotificationProfile = user.notificationProfiles.get(n);
									userNotificationProfile.enabled = false;
									userNotificationProfile.save();
									containTag = true;
								}
							}
							if (!containTag) {
								NotificationProfile notificationProfile2 = new NotificationProfile(tag.id, "Tag", tag.name, user);
								notificationProfile2.enabled = false;
								notificationProfile2.save();
							}
						}						
					}
				} else {
					if (notificationProfile.notifiableType.equals("Entity")) {
						MainEntity entity = MainEntity.findById(notificationProfile.notifiableId);
						for (int k = 0; k < entity.topicList.size(); k++) {
							Topic topic = entity.topicList.get(k);
							boolean containTopic = false;
							for (int n = 0; n < user.notificationProfiles.size(); n++) {
								if (user.notificationProfiles.get(n).notifiableType.equals("Topic")
										&& user.notificationProfiles.get(n).notifiableId == topic.id) {
									NotificationProfile userNotificationProfile = user.notificationProfiles.get(n);
									userNotificationProfile.enabled = false;
									userNotificationProfile.save();
									containTopic = true;
								}
							}
							if (!containTopic) {
								NotificationProfile notificationProfile2 = new NotificationProfile(topic.id, "Topic", topic.title, user);
								notificationProfile2.enabled = false;
								notificationProfile2.save();
							}
							for (int l = 0; l < topic.ideas.size(); l++) {
								Idea idea = topic.ideas.get(l);
								boolean containIdea = false;
								for (int n = 0; n < user.notificationProfiles.size(); n++) {
									if (user.notificationProfiles.get(n).notifiableType.equals("Idea")
											&& user.notificationProfiles.get(n).notifiableId == idea.id) {
										NotificationProfile userNotificationProfile = user.notificationProfiles.get(n);
										userNotificationProfile.enabled = false;
										userNotificationProfile.save();
										containIdea = true;
									}
								}
								if (!containIdea) {
									NotificationProfile notificationProfile2 = new NotificationProfile(idea.id, "Idea", idea.title, user);
									notificationProfile2.enabled = false;
									notificationProfile2.save();
								}
							}
						}
						for (int z = 0; z < entity.tagList.size(); z++) {
							Tag tag = entity.tagList.get(z);
							boolean containTag = false;
							for (int n = 0; n < user.notificationProfiles.size(); n++) {
								if (user.notificationProfiles.get(n).notifiableType.equals("Tag")
										&& user.notificationProfiles.get(n).notifiableId == tag.id) {
									NotificationProfile userNotificationProfile = user.notificationProfiles.get(n);
									userNotificationProfile.enabled = false;
									userNotificationProfile.save();
									containTag = true;
								}
							}
							if (!containTag) {
								NotificationProfile notificationProfile2 = new NotificationProfile(tag.id, "Tag", tag.name, user);
								notificationProfile2.enabled = false;
								notificationProfile2.save();
							}
						}
					} else {
						if (notificationProfile.notifiableType.equals("Topic")) {
							Topic topic = Topic.findById(notificationProfile.notifiableId);
							for (int l = 0; l < topic.ideas.size(); l++) {
								Idea idea = topic.ideas.get(l);
								boolean containIdea = false;
								for (int n = 0; n < user.notificationProfiles.size(); n++) {
									if (user.notificationProfiles.get(n).notifiableType.equals("Idea")
											&& user.notificationProfiles.get(n).notifiableId == idea.id) {
										NotificationProfile userNotificationProfile = user.notificationProfiles.get(n);
										userNotificationProfile.enabled = false;
										userNotificationProfile.save();
										containIdea = true;
									}
								}
								if (!containIdea) {
									NotificationProfile notificationProfile2 = new NotificationProfile(idea.id, "Idea", idea.title, user);
									notificationProfile2.enabled = false;
									notificationProfile2.save();
								}						
							}
						} 
					}
				}
				notificationProfile.enabled = false;
				notificationProfile.save();
			}
	}
}
