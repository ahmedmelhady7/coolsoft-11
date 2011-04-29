package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Idea;
import models.Item;
import models.MainEntity;
import models.Organization;
import models.Plan;
import models.Tag;
import models.Topic;

import play.mvc.Controller;

/**
 * 
 * @author Mohamed Ghanem
 * 
 * @story C4SXX : Search Structure
 * 
 */
public class Search extends Controller {

	public static List listOfResults;

	public static List<Object> filterResult; // array list for the resultafter
												// filtering the search

	/**
	 * 
	 * @author Mohamed Ghanem
	 * 
	 * @story C4S02 :: Advanced Search V1.0
	 * 
	 * 
	 * 
	 */

	// Main
	public static void advancedSearch(int searchIn, String wantKey,
			String unwantKey, String searchWith) {
		//
		// List orgs = Organization.findAll();
		//
		// switch (searchIn) {
		// case 1: {
		// for (int i = 0; i < orgs.size(); i++) {
		// if (((Organization) orgs.get(i)).privacyLevel != 2) {
		// orgs.remove(i);
		// }
		// }
		// break;
		// }
		// case 2: {
		// for (int i = 0; i < orgs.size(); i++) {
		// if (((Organization) orgs.get(i)).privacyLevel != 1) {
		// orgs.remove(i);
		// }
		// }
		// break;
		// }
		// case 3: {
		// for (int i = 0; i < orgs.size(); i++) {
		// if (((Organization) orgs.get(i)).privacyLevel != 0) {
		// orgs.remove(i);
		// }
		// }
		// break;
		// }
		// case 4: {
		// for (int i = 0; i < orgs.size(); i++) {
		// if (((Organization) orgs.get(i)).privacyLevel == 0) {
		// orgs.remove(i);
		// }
		// }
		// break;
		// }
		// case 5: {
		// for (int i = 0; i < orgs.size(); i++) {
		// if (((Organization) orgs.get(i)).privacyLevel == 1) {
		// orgs.remove(i);
		// }
		// }
		// break;
		// }
		// case 6: {
		// for (int i = 0; i < orgs.size(); i++) {
		// if (((Organization) orgs.get(i)).privacyLevel == 2) {
		// orgs.remove(i);
		// }
		// }
		// break;
		// }
		// default: {
		//
		// break;
		// }
		// }
		//
		// // Organization
		// if (searchWith.charAt(0) == '1') {
		// for (int i = 0; i < orgs.size(); i++) {
		// if (!((Organization) orgs.get(i)).name.contains(unwantKey)) {
		// if (((Organization) orgs.get(i)).name.contains(wantKey)) {
		// listOfResults.add(orgs.get(i));
		// } else {
		// boolean add = true;
		// List<Tag> x = ((Organization) orgs.get(i)).relatedTags;
		// for (int j = 0; j < x.size(); j++) {
		// if (x.get(j).name.contains(unwantKey)) {
		// add = false;
		// }
		// }
		// if (add) {
		// for (int j = 0; j < x.size(); j++) {
		// if (x.get(j).name.contains(wantKey)) {
		// listOfResults.add(orgs.get(i));
		// break;
		// }
		// }
		// }
		// }
		// }
		// }
		// }
		//
		// // Entity
		// if (searchWith.charAt(1) == '1') {
		// for (int i = 0; i < orgs.size(); i++) {
		// searchWithEntities(((Organization) orgs.get(i)).entitiesList,
		// unwantKey, wantKey);
		// }
		// }
		//
		// // Topic
		// List topic = null;
		// if (searchWith.charAt(2) == '1') {
		// topic = new ArrayList<Topic>();
		// for (int i = 0; i < orgs.size(); i++) {
		// for (int j = 0; j < ((Organization) orgs.get(i)).entitiesList
		// .size(); j++) {
		// topic.add(((MainEntity) ((Organization) orgs.get(i)).entitiesList
		// .get(j)).topicList);
		// }
		// }
		// searchWithTopic(topic, unwantKey, wantKey);
		// }
		//
		// // Plans
		// List plans = null;
		// if (searchWith.charAt(3) == '1') {
		// plans = new ArrayList<Plan>();
		// for (int i = 0; i < topic.size(); i++) {
		// plans.add(((Topic) topic.get(i)).plan);
		// }
		// searchWithPlan(plans, unwantKey, wantKey);
		// }
		//
		// // Ideas
		// if (searchWith.charAt(4) == '1') {
		// List ideas = new ArrayList<Idea>();
		// for (int i = 0; i < topic.size(); i++) {
		// for (int j = 0; j < ((Topic) topic.get(i)).ideas.size(); j++) {
		// ideas.add(((Idea) ((Topic) topic.get(i)).ideas.get(j)));
		// }
		// }
		// searchWithIdea(ideas, unwantKey, wantKey);
		// }
		//
		// // Item
		// if (searchWith.charAt(5) == '1') {
		// List items = new ArrayList<Idea>();
		// for (int i = 0; i < plans.size(); i++) {
		// for (int j = 0; j < ((models.Plan) plans.get(i)).items.size(); j++) {
		// items.add((((models.Plan) plans.get(i)).ideas.get(j)));
		// }
		// }
		// searchWithItem(items, unwantKey, wantKey);
		// }
		//
		// // Comments
		//

		render(searchIn);
	}

	// Item
	public static void searchWithItem(List items, String unwantKey,
			String wantKey) {
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

	// Idea
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

	// Plan
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

	// Topic
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

	// DFS Entity and Sub-Entity
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
	 * @author Loaay
	 * 
	 * @C4S1
	 * 
	 */

	/*
	 * quickSearch method by Loaay Alkherbawy searching for entities,
	 * organizations, entities, topics and ideas using a keyword and the
	 * userEmail to define how the result will appear to him
	 */

	public static List<Object> quickSearch(String keyword, int userId) {
		listOfResults = new ArrayList<Object>();
		// Adding Organizations to search result
		List<Object> organizationsList = searchForOrganization(keyword, userId);
		for (int i = 0; i < organizationsList.size(); i++) {
			listOfResults.add(organizationsList.get(i));
		}
		// Adding Entities to search result
		List<Object> EntitiesList = searchForEntity(keyword, userId);
		for (int i = 0; i < EntitiesList.size(); i++) {
			listOfResults.add(EntitiesList.get(i));
		}
		// Adding Ideas to search result
		List<Object> IdeasList = searchForIdea(keyword, userId);
		for (int i = 0; i < IdeasList.size(); i++) {
			listOfResults.add(IdeasList.get(i));
		}
		// Adding Topics to search result
		List<Object> TopicsList = searchForTopic(keyword, userId);
		for (int i = 0; i < TopicsList.size(); i++) {
			listOfResults.add(TopicsList.get(i));
		}
		return listOfResults;
	}

	// method that searches for organizations
	public static List<Object> searchForOrganization(String keyword, int userId) {
		String[] keywords = {keyword};
		try{
			keywords = keyword.split("\\s+");
		}
		catch(NullPointerException e){
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
					if (userId != listOfOrganizations.get(i).enrolledUsers
							.get(k).id) {
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

	// method that searches for entities
	public static List<Object> searchForEntity(String keyword, int userId) {
		String[] keywords = {keyword};
		try{
			keywords = keyword.split("\\s+");
		}
		catch(NullPointerException e){
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
					if (userId != listOfEntities.get(i).followers.get(k).id) {
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

	// method that searches for ideas
	public static List<Object> searchForIdea(String keyword, int userId) {
		String[] keywords = {keyword};
		try{
			keywords = keyword.split("\\s+");
		}
		catch(NullPointerException e){
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
					if (userId != listOfIdeas.get(i).belongsToTopic.entity.organization.enrolledUsers
							.get(k).id) {
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

	// method that searches for topics
	public static List<Object> searchForTopic(String keyword, int userId) {
		String[] keywords = {keyword};
		try{
			keywords = keyword.split("\\s+");
		}
		catch(NullPointerException e){
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
					if (userId != listOfTopics.get(i).followers.get(k).id) {
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

	public static void viewResult() {
		render(listOfResults);
	}

	/**
	 * 
	 * @auther Monica Yousry
	 * 
	 * @story c4s03(filter)
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

	// this filter method filters on type and takes only the type wanted
	public static List<Object> filterSearchResults(List<Object> resultList,
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
		return filterResult;
	}

	// this method takes the criteria and input from user to filter on
	public static List<Object> filterSearchResults(List<Object> resultList,
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

		return filterResult;
	}

}
