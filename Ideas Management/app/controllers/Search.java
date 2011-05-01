package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import controllers.Secure.Security;

import javax.mail.search.SearchTerm;

import com.sun.mail.handlers.text_html;

import models.Idea;
import models.Item;
import models.MainEntity;
import models.Organization;
import models.Plan;
import models.Tag;
import models.Topic;

import play.mvc.Controller;
import play.test.Fixtures;

/**
 * 
 * @author M Ghanem
 * 
 * @story C4SXX : Search Structure
 * 
 */
public class Search extends Controller {

	/**
	 * 
	 * @author M Ghanem
	 * 
	 * @story C4S02 :: Advanced Search; list of results that will be displayed
	 *        in the html page searchResult.
	 * 
	 */
	public static List<Object> listOfResults = new ArrayList<Object>();

	public static List<Object> filterResult;

	public static List sorted;

	/**
	 * 
	 * @author M Ghanem
	 * 
	 * @story C4S02 :: Advanced Search; display html page to take input and that
	 *        goes to advSR(...) method.
	 * 
	 * @return void
	 * 
	 */
	public static void advancedSearch() {
		render();
	}

	/**
	 * 
	 * @author M Ghanem
	 * 
	 * @story C4S02 Advanced Search Result; search for any Entity in the DB
	 *        according to the given parameters.
	 * 
	 * @param searchIn
	 *            :: "int"; that determines what privacy level of the
	 *            organizations does the user care about.
	 * 
	 * @param wantKey
	 *            :: "String"; of the keyword that the user is searching for.
	 * 
	 * @param unWantKey
	 *            :: "String"; of the word where user want to avoid it within
	 *            the result of searching.
	 * 
	 * @param searchWith
	 *            :: "String"; of 0s & 1s of length 6 each bit represents a
	 *            choice either to result ~>'1' or not~>'0' each of the
	 *            following 1st bit represents Organization, 2nd->MainEntity,
	 *            3rd->Topic, 4th->Plan, 5th->Idea, 6th->Tag.
	 * 
	 * @param before
	 *            :: "Date"; where the user needs all the result initialized
	 *            before this date.
	 * 
	 * @param after
	 *            :: "Date"; where the user needs all the result initialized
	 *            after this date.
	 * 
	 * @param exact
	 *            :: "Date"; where the user needs all the result initialized in
	 *            this date.
	 * 
	 * @return void.
	 * 
	 */
	public static void searchResult2(Date wantKey) {		
		render(wantKey);
	}
	
//	public static void searchResult(String searchIn, String wantKey, String unWantKey,
//			String searchWith, String before, String after, String exact) {
//
//		int sIn= Integer.parseInt(searchIn);
//		Date b = new Date(before);
//		Date a = new Date(after);
//		Date e = new Date(exact);
//		
//		
//		List orgs = Organization.findAll();
//
//		switch (sIn) {
//		case 1: {
//			for (int i = 0; i < orgs.size(); i++) {
//				if (((Organization) orgs.get(i)).privacyLevel != 2) {
//					orgs.remove(i);
//				}
//			}
//			break;
//		}
//		case 2: {
//			for (int i = 0; i < orgs.size(); i++) {
//				if (((Organization) orgs.get(i)).privacyLevel != 1) {
//					orgs.remove(i);
//				}
//			}
//			break;
//		}
//		case 3: {
//			for (int i = 0; i < orgs.size(); i++) {
//				if (((Organization) orgs.get(i)).privacyLevel != 0) {
//					orgs.remove(i);
//				}
//			}
//			break;
//		}
//		case 4: {
//			for (int i = 0; i < orgs.size(); i++) {
//				if (((Organization) orgs.get(i)).privacyLevel == 0) {
//					orgs.remove(i);
//				}
//			}
//			break;
//		}
//		case 5: {
//			for (int i = 0; i < orgs.size(); i++) {
//				if (((Organization) orgs.get(i)).privacyLevel == 1) {
//					orgs.remove(i);
//				}
//			}
//			break;
//		}
//		case 6: {
//			for (int i = 0; i < orgs.size(); i++) {
//				if (((Organization) orgs.get(i)).privacyLevel == 2) {
//					orgs.remove(i);
//				}
//			}
//			break;
//		}
//		default: {
//
//			break;
//		}
//		}
//
//		// Organization
//		if (searchWith.charAt(0) == '1') {
//			for (int i = 0; i < orgs.size(); i++) {
//				if (!((Organization) orgs.get(i)).name.contains(unWantKey)) {
//					if (((Organization) orgs.get(i)).name.contains(wantKey)) {
//						listOfResults.add(orgs.get(i));
//					} else {
//						boolean add = true;
//						List<Tag> x = ((Organization) orgs.get(i)).relatedTags;
//						for (int j = 0; j < x.size(); j++) {
//							if (x.get(j).name.contains(unWantKey)) {
//								add = false;
//							}
//						}
//						if (add) {
//							for (int j = 0; j < x.size(); j++) {
//								if (x.get(j).name.contains(wantKey)) {
//									listOfResults.add(orgs.get(i));
//									break;
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//
//		// Entity
//		if (searchWith.charAt(1) == '1') {
//			for (int i = 0; i < orgs.size(); i++) {
//				searchWithEntities(((Organization) orgs.get(i)).entitiesList,
//						unWantKey, wantKey);
//			}
//		}
//
//		// Topic
//		List topic = null;
//		if (searchWith.charAt(2) == '1') {
//			topic = new ArrayList<Topic>();
//			for (int i = 0; i < orgs.size(); i++) {
//				for (int j = 0; j < ((Organization) orgs.get(i)).entitiesList
//						.size(); j++) {
//					topic.add(((MainEntity) ((Organization) orgs.get(i)).entitiesList
//							.get(j)).topicList);
//				}
//			}
//			searchWithTopic(topic, unWantKey, wantKey);
//		}
//
//		// Plans
//		List plans = null;
//		if (searchWith.charAt(3) == '1') {
//			plans = new ArrayList<Plan>();
//			for (int i = 0; i < topic.size(); i++) {
//				plans.add(((Topic) topic.get(i)).plan);
//			}
//			searchWithPlan(plans, unWantKey, wantKey);
//		}
//
//		// Ideas
//		if (searchWith.charAt(4) == '1') {
//			List ideas = new ArrayList<Idea>();
//			for (int i = 0; i < topic.size(); i++) {
//				for (int j = 0; j < ((Topic) topic.get(i)).ideas.size(); j++) {
//					ideas.add(((Idea) ((Topic) topic.get(i)).ideas.get(j)));
//				}
//			}
//			searchWithIdea(ideas, unWantKey, wantKey);
//		}
//
//		// Item
//		if (searchWith.charAt(5) == '1') {
//			List items = new ArrayList<Idea>();
//			for (int i = 0; i < plans.size(); i++) {
//				for (int j = 0; j < ((models.Plan) plans.get(i)).items.size(); j++) {
//					items.add((((models.Plan) plans.get(i)).ideas.get(j)));
//				}
//			}
//			searchWithItem(items, unWantKey, wantKey, b, a, e);
//		}
//
//		// Comments
//		
//		
//		render();
//	}

	/**
	 * 
	 * @author M Ghanem
	 * 
	 * @story C4S02 Advanced Search Result; search for Items according to the
	 *        given parameters.
	 * 
	 * @param items
	 *            :: "List"; of Item that we need to search in.
	 * 
	 * @param wantKey
	 *            :: "String"; of the keyword that the user is searching for.
	 * 
	 * @param unWantKey
	 *            :: "String"; of the word where user want to avoid it within
	 *            the result of searching.
	 * 
	 * @param befor
	 *            :: "Date"; where the search result is before this date.
	 * 
	 * @param after
	 *            :: "Date"; where the search result is after this date.
	 * 
	 * @param exact
	 *            :: "Date"; where the search result is in this date.
	 * 
	 * @return void.
	 * 
	 */
	public static void searchWithItem(List items, String unwantKey,
			String wantKey, Date before, Date after, Date exact) {
		for (int i = 0; i < items.size(); i++) {
			if (!((Item) items.get(i)).summary.contains(unwantKey)
					&& !((Item) items.get(i)).description.contains(unwantKey)) {
				if (((Item) items.get(i)).summary.contains(wantKey)) {
					listOfResults.add(items.get(i));
				} else {
					if (((Item) items.get(i)).description.contains(wantKey)) {
						listOfResults.add(items.get(i));
					} else {
						boolean add = true;
						List<Tag> x = ((Item) items.get(i)).tags;
						for (int j = 0; j < x.size(); j++) {
							if (x.get(j).name.contains(unwantKey)) {
								add = false;
							}
						}
						if (add) {
							for (int j = 0; j < x.size(); j++) {
								if (x.get(j).name.contains(wantKey)) {
									listOfResults.add(items.get(i));
									break;
								}
							}
						}
					}
				}
			}
		}

	}

	/**
	 * 
	 * @author M Ghanem
	 * 
	 * @story C4S02 Advanced Search Result; search for Ideas according to the
	 *        given parameters.
	 * 
	 * @param items
	 *            :: "List"; of Idea that we need to search in.
	 * 
	 * @param wantKey
	 *            :: "String"; of the keyword that the user is searching for.
	 * 
	 * @param unWantKey
	 *            :: "String"; of the word where user want to avoid it within
	 *            the result of searching.
	 * 
	 * @param befor
	 *            :: "Date"; where the search result is before this date.
	 * 
	 * @param after
	 *            :: "Date"; where the search result is after this date.
	 * 
	 * @param exact
	 *            :: "Date"; where the search result is in this date.
	 * 
	 * @return void.
	 * 
	 */
	public static void searchWithIdea(List ideas, String unwantKey,
			String wantKey) {
		for (int i = 0; i < ideas.size(); i++) {
			if (!((Idea) ideas.get(i)).title.contains(unwantKey)
					&& !((Idea) ideas.get(i)).description.contains(unwantKey)) {
				if (((Idea) ideas.get(i)).title.contains(wantKey)) {
					listOfResults.add(ideas.get(i));
				} else {
					if (((Idea) ideas.get(i)).description.contains(wantKey)) {
						listOfResults.add(ideas.get(i));
					} else {
						boolean add = true;
						List<Tag> x = ((Idea) ideas.get(i)).tagsList;
						for (int j = 0; j < x.size(); j++) {
							if (x.get(j).name.contains(unwantKey)) {
								add = false;
							}
						}
						if (add) {
							for (int j = 0; j < x.size(); j++) {
								if (x.get(j).name.contains(wantKey)) {
									listOfResults.add(ideas.get(i));
									break;
								}
							}
						}
					}
				}
			}
		}

	}

	/**
	 * 
	 * @author M Ghanem
	 * 
	 * @story C4S02 Advanced Search Result; search for Plan according to the
	 *        given parameters.
	 * 
	 * @param items
	 *            :: "List"; of Plan that we need to search in.
	 * 
	 * @param wantKey
	 *            :: "String"; of the keyword that the user is searching for.
	 * 
	 * @param unWantKey
	 *            :: "String"; of the word where user want to avoid it within
	 *            the result of searching.
	 * 
	 * @param befor
	 *            :: "Date"; where the search result is before this date.
	 * 
	 * @param after
	 *            :: "Date"; where the search result is after this date.
	 * 
	 * @param exact
	 *            :: "Date"; where the search result is in this date.
	 * 
	 * @return void.
	 * 
	 */
	public static void searchWithPlan(List plans, String unwantKey,
			String wantKey) {
		for (int i = 0; i < plans.size(); i++) {
			if (!((models.Plan) plans.get(i)).title.contains(unwantKey)) {
				if (((models.Plan) plans.get(i)).title.contains(wantKey)) {
					listOfResults.add(plans.get(i));
				}
			}
		}

	}

	/**
	 * 
	 * @author M Ghanem
	 * 
	 * @story C4S02 Advanced Search Result; search for Topic according to the
	 *        given parameters.
	 * 
	 * @param items
	 *            :: "List"; of Topic that we need to search in.
	 * 
	 * @param wantKey
	 *            :: "String"; of the keyword that the user is searching for.
	 * 
	 * @param unWantKey
	 *            :: "String"; of the word where user want to avoid it within
	 *            the result of searching.
	 * 
	 * @param befor
	 *            :: "Date"; where the search result is before this date.
	 * 
	 * @param after
	 *            :: "Date"; where the search result is after this date.
	 * 
	 * @param exact
	 *            :: "Date"; where the search result is in this date.
	 * 
	 * @return void.
	 * 
	 */
	public static void searchWithTopic(List<Topic> topics, String unwantKey,
			String wantKey) {
		for (int i = 0; i < topics.size(); i++) {
			if (!((Topic) topics.get(i)).title.contains(unwantKey)
					&& !((Topic) topics.get(i)).description.contains(unwantKey)) {
				if (((Topic) topics.get(i)).title.contains(wantKey)) {
					listOfResults.add(topics.get(i));
				} else {
					if (((Topic) topics.get(i)).description.contains(wantKey)) {
						listOfResults.add(topics.get(i));
					} else {
						boolean add = true;
						List<Tag> x = ((Topic) topics.get(i)).tags;
						for (int j = 0; j < x.size(); j++) {
							if (x.get(j).name.contains(unwantKey)) {
								add = false;
							}
						}
						if (add) {
							for (int j = 0; j < x.size(); j++) {
								if (x.get(j).name.contains(wantKey)) {
									listOfResults.add(topics.get(i));
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @author M Ghanem
	 * 
	 * @story C4S02 Advanced Search Result; search for Entities and sub-entities
	 *        according to the given parameters.
	 * 
	 * @param items
	 *            :: "List"; of MainEntity that we need to search in.
	 * 
	 * @param wantKey
	 *            :: "String"; of the keyword that the user is searching for.
	 * 
	 * @param unWantKey
	 *            :: "String"; of the word where user want to avoid it within
	 *            the result of searching.
	 * 
	 * @param befor
	 *            :: "Date"; where the search result is before this date.
	 * 
	 * @param after
	 *            :: "Date"; where the search result is after this date.
	 * 
	 * @param exact
	 *            :: "Date"; where the search result is in this date.
	 * 
	 * @return void.
	 * 
	 */
	public static void searchWithEntities(List<MainEntity> entity,
			String unwantKey, String wantKey) {
		for (int i = 0; i < entity.size(); i++) {
			List subentity = ((MainEntity) entity.get(i)).subentities;
			if (!((MainEntity) entity.get(i)).name.contains(unwantKey)
					&& !((MainEntity) entity.get(i)).description
							.contains(unwantKey)) {
				if (((MainEntity) entity.get(i)).name.contains(wantKey)) {
					listOfResults.add(entity.get(i));
				} else {
					if (((MainEntity) entity.get(i)).description
							.contains(wantKey)) {
						listOfResults.add(entity.get(i));
					} else {
						boolean add = true;
						List<Tag> x = ((MainEntity) entity.get(i)).tagList;
						for (int j = 0; j < x.size(); j++) {
							if (x.get(j).name.contains(unwantKey)) {
								add = false;
							}
						}
						if (add) {
							for (int j = 0; j < x.size(); j++) {
								if (x.get(j).name.contains(wantKey)) {
									listOfResults.add(entity.get(i));
									break;
								}
							}
						}
					}
				}
			}
			if (subentity != null) {
				searchWithEntities(subentity, unwantKey, wantKey);
			}
		}
	}

	/**
	 * 
	 * @author Loaay Alkherbawy
	 * 
	 * @story C4S01 Searching for organizations, entities, ideas and topics using a given keyword
	 * 
	 * @param keyword: the keyword the user enters to search for
	 * 
	 */

	public static void quickSearch(String keyword) {
		System.out.println(keyword);
		listOfResults = new ArrayList<Object>();
		// Adding Organizations to search result
		List<Object> organizationsList = searchForOrganization(keyword);
		for (int i = 0; i < organizationsList.size(); i++) {
			listOfResults.add(organizationsList.get(i));
		}
		// Adding Entities to search result
		List<Object> EntitiesList = searchForEntity(keyword);
		for (int i = 0; i < EntitiesList.size(); i++) {
			listOfResults.add(EntitiesList.get(i));
		}
		// Adding Ideas to search result
		List<Object> IdeasList = searchForIdea(keyword);
		for (int i = 0; i < IdeasList.size(); i++) {
			listOfResults.add(IdeasList.get(i));
		}
		// Adding Topics to search result
		List<Object> TopicsList = searchForTopic(keyword);
		for (int i = 0; i < TopicsList.size(); i++) {
			listOfResults.add(TopicsList.get(i));
		}
	}

	/**
	 * 
	 * @author Loaay Alkherbawy
	 * 
	 * @story C4S01 Searching for organizations using a given keyword
	 * 
	 * @param keyword: the keyword the user enters to search for
	 * 
	 * @return List<Object> 
	 */
	public static List<Object> searchForOrganization(String keyword) {
		String userId = Security.connected();
		String[] keywords = { keyword };
		try {
			keywords = keyword.split("\\s+");
		} catch (NullPointerException e) {
		}
		listOfResults = new ArrayList<Object>();
		for (int s = 0; s < keywords.length; s++) {
			List<Organization> listOfOrganizations = Organization.findAll();
			for (int i = 0; i < listOfOrganizations.size(); i++) { // Looping on
																	// the list
																	// of
																	// organization
				if (listOfOrganizations.get(i).name
						.equalsIgnoreCase(keywords[s])) {
					listOfResults.add(listOfOrganizations.get(i));
				} else {
					for (int j = 0; j < listOfOrganizations.get(i).relatedTags
							.size(); j++) { // Looping on the list of Tags
						if (keywords[s].equalsIgnoreCase(listOfOrganizations
								.get(i).relatedTags.get(j).name)) {
							if (!listOfResults.contains(listOfOrganizations
									.get(i))) {
								listOfResults.add(listOfOrganizations.get(i));
							}
						}
					}
				}
				for (int k = 0; k < listOfOrganizations.get(i).enrolledUsers
						.size(); k++) { // Looping on the list of users
					if (userId
							.compareTo(listOfOrganizations.get(i).enrolledUsers
									.get(k).username) != 0) {
						switch (listOfOrganizations.get(i).privacyLevel) {
						case 3:
							listOfOrganizations.remove(listOfOrganizations
									.get(i));
							break;
						case 4:
							listOfOrganizations.remove(listOfOrganizations
									.get(i));
							break;
						default:
							break;
						}
					}
				}
			}
		}
		return listOfResults;
	}

	/**
	 * 
	 * @author Loaay Alkherbawy
	 * 
	 * @story C4S01 Searching for entities using a given keyword
	 * 
	 * @param keyword: the keyword the user enters to search for
	 * 
	 * @return List<Object>
	 */
	public static List<Object> searchForEntity(String keyword) {
		String userId = Security.connected();
		String[] keywords = { keyword };
		try {
			keywords = keyword.split("\\s+");
		} catch (NullPointerException e) {
		}
		listOfResults = new ArrayList<Object>();
		List<MainEntity> listOfEntities = MainEntity.findAll();
		for (int s = 0; s < keywords.length; s++) {
			for (int i = 0; i < listOfEntities.size(); i++) { // Looping on the
																// list of
																// organization
				if (listOfEntities.get(i).name.equalsIgnoreCase(keywords[s])) {
					listOfResults.add(listOfEntities.get(i));
				} else {
					for (int j = 0; j < listOfEntities.get(i).tagList.size(); j++) { // Looping
																						// on
																						// the
																						// list
																						// of
																						// Tags
						if (keywords[s]
								.equalsIgnoreCase(listOfEntities.get(i).tagList
										.get(j).name)) {
							if (!listOfResults.contains(listOfEntities.get(i))) {
								listOfResults.add(listOfEntities.get(i));
							}
						}
					}
				}
				for (int k = 0; k < listOfEntities.get(i).followers.size(); k++) { // Looping
																					// on
																					// the
																					// list
																					// of
																					// users
					if (userId
							.compareTo(listOfEntities.get(i).followers.get(k).username) != 0) {
						switch (listOfEntities.get(i).organization.privacyLevel) {
						case 3:
							listOfEntities.remove(listOfEntities.get(i));
							break;
						case 4:
							listOfEntities.remove(listOfEntities.get(i));
							break;
						default:
							break;
						}
					}
				}
			}
		}
		return listOfResults;
	}

	/**
	 * 
	 * @author Loaay Alkherbawy
	 * 
	 * @story C4S01 Searching for ideas using a given keyword
	 * 
	 * @param keyword: the keyword the user enters to search for
	 * 
	 * @return List<Object>
	 */
	public static List<Object> searchForIdea(String keyword) {
		String userId = Security.connected();
		String[] keywords = { keyword };
		try {
			keywords = keyword.split("\\s+");
		} catch (NullPointerException e) {
		}
		listOfResults = new ArrayList<Object>();
		List<Idea> listOfIdeas = Idea.findAll();
		for (int s = 0; s < keywords.length; s++) {
			for (int i = 0; i < listOfIdeas.size(); i++) { // Looping on the
															// list of
															// organization
				if (listOfIdeas.get(i).title.equalsIgnoreCase(keywords[s])
						|| listOfIdeas.get(i).description.contains(keywords[s])) {
					listOfResults.add(listOfIdeas.get(i));
				} else {
					for (int j = 0; j < listOfIdeas.get(i).tagsList.size(); j++) { // Looping
																					// on
																					// the
																					// list
																					// of
																					// Tags
						if (keywords[s]
								.equalsIgnoreCase(listOfIdeas.get(i).tagsList
										.get(j).name)) {
							if (!listOfResults.contains(listOfIdeas.get(i))) {
								listOfResults.add(listOfIdeas.get(i));
							}
						}
					}
				}
				for (int k = 0; k < listOfIdeas.get(i).belongsToTopic.entity.organization.enrolledUsers
						.size(); k++) { // Looping on the list of users
					if (userId
							.compareTo(listOfIdeas.get(i).belongsToTopic.entity.organization.enrolledUsers
									.get(k).username) != 0) {
						switch (listOfIdeas.get(i).belongsToTopic.entity.organization.privacyLevel) {
						case 3:
							listOfIdeas.remove(listOfIdeas.get(i));
							break;
						case 4:
							listOfIdeas.remove(listOfIdeas.get(i));
							break;
						default:
							break;
						}
					}
				}

			}
		}
		return listOfResults;
	}

	/**
	 * 
	 * @author Loaay Alkherbawy
	 * 
	 * @story C4S01 Searching for topics using a given keyword
	 * 
	 * @param keyword: the keyword the user enters to search for
	 * 
	 * @return List<Object>
	 */
	public static List<Object> searchForTopic(String keyword) {
		String userId = Security.connected();
		String[] keywords = { keyword };
		try {
			keywords = keyword.split("\\s+");
		} catch (NullPointerException e) {
		}
		listOfResults = new ArrayList<Object>();
		List<Topic> listOfTopics = Topic.findAll();
		for (int s = 0; s < keywords.length; s++) {
			for (int i = 0; i < listOfTopics.size(); i++) { // Looping on the
															// list of
															// organization
				if (listOfTopics.get(i).title.equalsIgnoreCase(keywords[s])) {
					listOfResults.add(listOfTopics.get(i));
				} else {
					for (int j = 0; j < listOfTopics.get(i).tags.size(); j++) {
						if (keywords[s]
								.equalsIgnoreCase(listOfTopics.get(i).tags
										.get(j).name)) {
							if (!listOfResults.contains(listOfTopics.get(i))) {
								listOfResults.add(listOfTopics.get(i));
							}
						}
					}
				}
				for (int k = 0; k < listOfTopics.get(i).entity.organization.enrolledUsers
						.size(); k++) { // Looping on the list of users
					if (userId
							.compareTo(listOfTopics.get(i).followers.get(k).username) != 0) {
						switch (listOfTopics.get(i).entity.organization.privacyLevel) {
						case 3:
							listOfTopics.remove(listOfTopics.get(i));
							break;
						case 4:
							listOfTopics.remove(listOfTopics.get(i));
							break;
						default:
							break;
						}
					}
				}

			}
		}
		return listOfResults;
	}

	/**
	 * 
	 * @author Loaay Alkherbawy
	 * 
	 * @story C4S01 Searching for organizations using a given keyword
	 * 
	 * this method renders the searchResult view
	 * 
	 */
	public static void searchResult() {
		String connected = Security.connected();
		render(listOfResults,connected);
	}

	/**
	 * method handelingOrAnd decides which list is to be passed for the filter
	 * method if the choice is and so we will need to filter on the filtered
	 * list but if or then we filter on the main list
	 * 
	 * @author Monica Yousry
	 * 
	 * @story C4S03 filtering results after sort
	 * @param AndOr
	 *            :: "char"; of the filter option view as input from user
	 * 
	 * @return void.
	 * 
	 */
	// //////////////////////////////////////////////////////////////////
	// filter method by monica yousry
	// filtering on type (organisation,topic,idea,plan,entity,...etc)
	//
	// this commented method with the static parametar will help in defining
	// which list to pass for the filter method accrding to the user's choice
	// (and or or )
	static List<Object> tobepassed;

	public static void handelingOrAnd(char AndOr) {
		if (AndOr == 'a') {
			tobepassed = filterResult;
		}
		if (AndOr == 'o') {
			tobepassed = listOfResults;
		}
	}

	/**
	 * filterSearchResults method takes a list and something to filter
	 * on(criteria) if it is type only not specific input from user but only
	 * choice
	 * 
	 * @author Monica Yousry
	 * 
	 * @story C4S03 filtering results after sort
	 * @param filterOn
	 *            :: "String"; the criteria to filter on taken from filter
	 *            option view as input from user
	 * @param resultList
	 *            :: "List";the result list to be filtered taken from previous
	 *            method (search methods )
	 * @return void.
	 * 
	 */

	public static void filterSearchResults(List<Object> resultList,
			String filterOn) {
		filterResult = new ArrayList<Object>();
		if (filterOn.equalsIgnoreCase("o")) {// filtering on organizations
			for (int i = 0; i < resultList.size(); i++) {// loop on the whole
															// search result
				if (resultList.get(i) instanceof Organization) {// if found an
																// organization
					filterResult.add(resultList.get(i));// add it to the list
				}
			}
		}

		// similar to the previous part

		if (filterOn.equalsIgnoreCase("i")) {// filtering on ideas
			for (int i = 0; i < resultList.size(); i++) {
				if (resultList.get(i) instanceof Idea) {
					filterResult.add(resultList.get(i));
				}
			}
		}

		if (filterOn.equalsIgnoreCase("t")) { // filter on topic
			for (int i = 0; i < resultList.size(); i++) {
				if (resultList.get(i) instanceof Topic) {
					filterResult.add(resultList.get(i));
				}
			}
		}
		if (filterOn.equalsIgnoreCase("e")) { // filter on entity
			for (int i = 0; i < resultList.size(); i++) {
				if (resultList.get(i) instanceof MainEntity) {

					filterResult.add(resultList.get(i));
				}
			}
		}

		if (filterOn.equalsIgnoreCase("p")) { // filter on plan
			for (int i = 0; i < resultList.size(); i++) {
				if (resultList.get(i) instanceof Plan) {
					filterResult.add(resultList.get(i));
				}
			}
		}

		if (filterOn.equalsIgnoreCase("a")) {
			filterResult = resultList;
		}

	}

	/**
	 * filterSearchResults method takes a list and something to filter
	 * on(criteria) similar to previous method but takes input with the criteria
	 * 
	 * @author Monica Yousry
	 * 
	 * @story C4S03 filtering results after sort
	 * @param filterOn
	 *            :: "String"; the criteria to filter on taken from filter
	 *            option view as input from user
	 * @param resultList
	 *            :: "List";the result list to be filtered taken from previous
	 *            method (search methods )
	 * 
	 * @param input
	 *            :: "String";input is to be taken from the user from the form
	 *            in the filter options view
	 * @return void.
	 * 
	 */
	public static void filterSearchResults(List<Object> resultList,
			String filterOn, String input) {

		filterResult = new ArrayList<Object>();

		if (filterOn.equalsIgnoreCase("name")
				|| filterOn.equalsIgnoreCase("title")) {
			for (int i = 0; i < resultList.size(); i++) {

				if (resultList.get(i) instanceof models.Idea) {
					models.Idea temp = (models.Idea) resultList.get(i);
					if (temp.title.equalsIgnoreCase(input)) {
						filterResult.add(resultList.get(i));
					}

				}

				if (resultList.get(i) instanceof models.Plan) {
					models.Plan temp = (models.Plan) resultList.get(i);
					if (temp.title.equalsIgnoreCase(input)) {
						filterResult.add(resultList.get(i));
					}

				}

				if (resultList.get(i) instanceof models.Organization) {
					models.Organization temp = (models.Organization) resultList
							.get(i);
					if (temp.name.equalsIgnoreCase(input)) {
						filterResult.add(resultList.get(i));
					}

				}

				if (resultList.get(i) instanceof models.Topic) {
					models.Topic temp = (models.Topic) resultList.get(i);
					if (temp.title.equalsIgnoreCase(input)) {
						filterResult.add(resultList.get(i));
					}

				}

				if (resultList.get(i) instanceof models.MainEntity) {
					models.MainEntity temp = (models.MainEntity) resultList
							.get(i);
					if (temp.name.equalsIgnoreCase(input)) {
						filterResult.add(resultList.get(i));
					}

				}

			}
		}

		if (filterOn.equalsIgnoreCase("description")) {
			for (int i = 0; i < resultList.size(); i++) {

				if (resultList.get(i) instanceof models.Idea) {
					models.Idea temp = (models.Idea) resultList.get(i);
					if (temp.description.contains(input)) {
						filterResult.add(resultList.get(i));
					}

				}

				if (resultList.get(i) instanceof models.Plan) {
					models.Plan temp = (models.Plan) resultList.get(i);
					if (temp.description.contains(input)) {
						filterResult.add(resultList.get(i));
					}

				}

				if (resultList.get(i) instanceof models.Topic) {
					models.Topic temp = (models.Topic) resultList.get(i);
					if (temp.description.contains(input)) {
						filterResult.add(resultList.get(i));
					}

				}

				if (resultList.get(i) instanceof models.MainEntity) {
					models.MainEntity temp = (models.MainEntity) resultList
							.get(i);
					if (temp.description.contains(input)) {
						filterResult.add(resultList.get(i));
					}

				}

			}
		}

	}

	/**
	 * 
	 * sortA method sorts according to votes or rates (known from input) in ascending order 
	 * 
	 * task.
	 * 
	 * 
	 * @author Monica Yousry
	 * 
	 * @story C4S04 sort ascending the search result according to votes or rates 
	 * 
	 * @param voteOrRate
	 * 
	 *            "char": to decide whether to sort by vote or rate .
	 * 
	 * @param searchresult
	 * 
	 *            "list": The search result List that should be sorted .
	 * 
	 * 
	 * @return void
	 * 
	 * 
	 */

	public static void sortA(char voteOrRate, List<Object> searchresult) {// ascending
		List<Object> tosort = new ArrayList<Object>();
		List<Object> nottosort = new ArrayList<Object>();

		if (voteOrRate == 'r' || voteOrRate == 'R') { // sorting by rate

			/*
			 * a for loop to find all objects having attribute rate and add them
			 * to a list called to sort and the rest of the search results are
			 * to be in a list not to be sorted
			 */

			for (int i = 0; i < searchresult.size(); i++) {
				if (searchresult.get(i) instanceof Idea
						|| searchresult.get(i) instanceof Plan) {
					tosort.add(searchresult.get(i));
				} else {
					nottosort.add(searchresult.get(i));
				}
			}

			for (int j = 0; j < tosort.size(); j++) {
				int rate1 = 0;
				int rate2 = 0;
				if (tosort.get(j) instanceof Idea) {
					Idea temp1 = (Idea) tosort.get(j);
					rate1 = temp1.rating;
				} else {
					Plan temp1 = (Plan) tosort.get(j);
					rate1 = temp1.rating;
				}
				for (int k = 0; k < tosort.size(); k++) {
					if (tosort.get(k) instanceof Idea) {
						Idea temp2 = (Idea) tosort.get(k);
						rate2 = temp2.rating;
					} else {
						Plan temp2 = (Plan) tosort.get(k);
						rate2 = temp2.rating;
					}

					if (rate1 > rate2) { // sorting

						Object temp = (Object) tosort.get(k);
						tosort.set(k, tosort.get(j));
						tosort.set(j, temp);

					}

				}

			}

			sorted = new ArrayList<Object>();// final sorted list
			for (int m = 0; m < tosort.size(); m++) {
				sorted.add(tosort.get(m));
			}
			for (int m = 0; m < nottosort.size(); m++) {
				sorted.add(nottosort.get(m));
			}
			// the previous too loops is to append to lists in one
		}

		// waiting for the vote to be done in order to sort by voting
		/*
		 * if (voteOrRate == 'v' || voteOrRate == 'v') { // sorting by rate
		 * 
		 * 
		 * // a for loop to find all objects having attribute rate and add them
		 * // to a list called to sort and the rest of the search results are //
		 * to be in a list not to be sorted
		 * 
		 * 
		 * for (int i = 0; i < searchresult.size(); i++) { if
		 * (searchresult.get(i) instanceof Idea || searchresult.get(i)
		 * instanceof Plan) { tosort.add(searchresult.get(i)); } else {
		 * nottosort.add(searchresult.get(i)); } }
		 * 
		 * for (int j = 0; j < tosort.size(); j++) { int vote1 = 0; int vote2 =
		 * 0; if (tosort.get(j) instanceof Idea) { Idea temp1 = (Idea)
		 * tosort.get(j); vote1 = temp1.voting; } else { Plan temp1 = (Plan)
		 * tosort.get(j); vote1 = temp1.voting; } for (int k = 0; k <
		 * tosort.size(); k++) { if (tosort.get(k) instanceof Idea) { Idea temp2
		 * = (Idea) tosort.get(k); vote2 = temp2.voting; } else { Plan temp2 =
		 * (Plan) tosort.get(k); vote2 = temp2.voting; }
		 * 
		 * if (vote1 > vote2) { //sorting
		 * 
		 * Object temp = (Object) tosort.get(k); tosort.set(k, tosort.get(j));
		 * tosort.set(j, temp);
		 * 
		 * }
		 * 
		 * }
		 * 
		 * }
		 * 
		 * sorted= new ArrayList<Object>();//final sorted list for(int
		 * m=0;m<tosort.size();m++){ sorted.add(tosort.get(m)); } for (int
		 * m=0;m<nottosort.size();m++){ sorted.add(nottosort.get(m)); } //the
		 * previous too loops is to append to lists in one }
		 */

	}


	/**
	 * 
	 * sortD method sorts according to votes or rates (known from input) in descending order 
	 * 
	 * task.
	 * 
	 * 
	 * @author Monica Yousry
	 * 
	 * @story C4S04 sort descending the search result according to votes or rates 
	 * 
	 * @param voteOrRate
	 * 
	 *            "char": to decide whether to sort by vote or rate .
	 * 
	 * @param searchresult
	 * 
	 *            "list": The search result List that should be sorted .
	 * 
	 * 
	 * @return void
	 * 
	 * 
	 */

	
	public static void sortD(char voteOrRate, List<Object> searchresult) {// descending
		List<Object> tosort = new ArrayList<Object>();
		List<Object> nottosort = new ArrayList<Object>();

		if (voteOrRate == 'r' || voteOrRate == 'R') { // sorting by rate

			/*
			 * a for loop to find all objects having attribute rate and add them
			 * to a list called to sort and the rest of the search results are
			 * to be in a list not to be sorted
			 */

			for (int i = 0; i < searchresult.size(); i++) {
				if (searchresult.get(i) instanceof Idea
						|| searchresult.get(i) instanceof Plan) {
					tosort.add(searchresult.get(i));
				} else {
					nottosort.add(searchresult.get(i));
				}
			}

			for (int j = 0; j < tosort.size(); j++) {
				int rate1 = 0;
				int rate2 = 0;
				if (tosort.get(j) instanceof Idea) {
					Idea temp1 = (Idea) tosort.get(j);
					rate1 = temp1.rating;
				} else {
					Plan temp1 = (Plan) tosort.get(j);
					rate1 = temp1.rating;
				}
				for (int k = 0; k < tosort.size(); k++) {
					if (tosort.get(k) instanceof Idea) {
						Idea temp2 = (Idea) tosort.get(k);
						rate2 = temp2.rating;
					} else {
						Plan temp2 = (Plan) tosort.get(k);
						rate2 = temp2.rating;
					}

					if (rate1 < rate2) { // sorting

						Object temp = (Object) tosort.get(k);
						tosort.set(k, tosort.get(j));
						tosort.set(j, temp);

					}

				}

			}

			sorted = new ArrayList<Object>();// final sorted list
			for (int m = 0; m < tosort.size(); m++) {
				sorted.add(tosort.get(m));
			}
			for (int m = 0; m < nottosort.size(); m++) {
				sorted.add(nottosort.get(m));
			}
			// the previous too loops is to append to lists in one
		}

		// waiting for the vote to be done in order to sort by voting
		/*
		 * if (voteOrRate == 'v' || voteOrRate == 'v') { // sorting by rate
		 * 
		 * 
		 * // a for loop to find all objects having attribute rate and add them
		 * // to a list called to sort and the rest of the search results are //
		 * to be in a list not to be sorted
		 * 
		 * 
		 * for (int i = 0; i < searchresult.size(); i++) { if
		 * (searchresult.get(i) instanceof Idea || searchresult.get(i)
		 * instanceof Plan) { tosort.add(searchresult.get(i)); } else {
		 * nottosort.add(searchresult.get(i)); } }
		 * 
		 * for (int j = 0; j < tosort.size(); j++) { int vote1 = 0; int vote2 =
		 * 0; if (tosort.get(j) instanceof Idea) { Idea temp1 = (Idea)
		 * tosort.get(j); vote1 = temp1.voting; } else { Plan temp1 = (Plan)
		 * tosort.get(j); vote1 = temp1.voting; } for (int k = 0; k <
		 * tosort.size(); k++) { if (tosort.get(k) instanceof Idea) { Idea temp2
		 * = (Idea) tosort.get(k); vote2 = temp2.voting; } else { Plan temp2 =
		 * (Plan) tosort.get(k); vote2 = temp2.voting; }
		 * 
		 * if (vote1 < vote2) { //sorting
		 * 
		 * Object temp = (Object) tosort.get(k); tosort.set(k, tosort.get(j));
		 * tosort.set(j, temp);
		 * 
		 * }
		 * 
		 * }
		 * 
		 * }
		 * 
		 * sorted= new ArrayList<Object>();//final sorted list for(int
		 * m=0;m<tosort.size();m++){ sorted.add(tosort.get(m)); } for (int
		 * m=0;m<nottosort.size();m++){ sorted.add(nottosort.get(m)); } //the
		 * previous too loops is to append to lists in one }
		 */

	}

}
