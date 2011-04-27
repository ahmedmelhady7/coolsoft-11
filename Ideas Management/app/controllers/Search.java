package controllers;

import java.util.ArrayList;
import java.util.List;

import org.h2.table.Plan;

import com.sun.xml.internal.stream.Entity;

import models.Idea;
import models.MainEntity;
import models.Organization;
import models.Topic;

import play.mvc.Controller;

/**
 * 
 * @author Mohamed Ghanem
 * 
 * @story C4SXX
 * 
 */
public class Search extends Controller {

	public static void advancedSearch() {
		render();
	}

	/**
	 * 
	 * @auther Monica Yousry
	 * 
	 * @story c4s03(filter)
	 */

	// gom3a i need the list of result of the search to be like the filter
	// result so that i can use it directly in other places

	static private ArrayList<Object> filterResult; // array list for the result after
											// filtering the search

	public static Object quickSearch(String keyword, String userEmail) {
		String[] keywords = keyword.split(" ");
		List<Object> listOfResults = new ArrayList<Object>();
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
					if (!userEmail
							.equalsIgnoreCase(listOfOrganizations.get(i).enrolledUsers
									.get(k).email)) {
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
			List<MainEntity> listOfEntities = MainEntity.findAll();
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
			List<Idea> listOfIdeas = Idea.findAll();
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
				switch (listOfIdeas.get(i).privacyLevel) {
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
			List<Topic> listOfTopics = Topic.findAll();
			for (int i = 0; i < listOfTopics.size(); i++) { // Looping on the
															// list of
															// organization
				if (listOfTopics.get(i).title.equalsIgnoreCase(keywords[s])) {
					listOfResults.add(listOfTopics.get(i));
				} else {
					for (int j = 0; j < listOfTopics.get(i).tags.size(); j++) { // Looping
																				// on
																				// the
																				// list
																				// of
																				// Tags
						if (keywords[s]
								.equalsIgnoreCase(listOfTopics.get(i).tags
										.get(j).name)) {
							if (!listOfResults.contains(listOfTopics.get(i))) {
								listOfResults.add(listOfTopics.get(i));
							}
						}
					}
				}
				switch (listOfTopics.get(i).privacyLevel) {
				case 3:
					listOfTopics.remove(listOfIdeas.get(i));
					break;
				case 4:
					listOfTopics.remove(listOfIdeas.get(i));
					break;
				default:
					break;
				}

			}
		}
		return listOfResults;
	}

	// //////////////////////////////////////////////////////////////////
	// filter method by monica yousry
	// filtering on type (organisation,topic,idea,plan,entity,...etc)

	public static ArrayList<Object> filterSearchResults(
			ArrayList<Object> resultList, String filterOn) {
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
				if (resultList.get(i) instanceof Entity) {// shall i write here
															// entity or main
															// entity ????
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

		if(filterOn.equalsIgnoreCase("a")){
			filterResult=resultList;
		}
		return filterResult;
	}

	//this commented method with the static parametar will help in defining which list to pass for the filter method accrding to the user's choice (and or or )
	//it is commented for i am waiting for a parameter from other user so that it won't produce an error 
	
/*	static ArrayList<Object> tobepassed;

	public static void handelingOrAnd(char AndOr){
		if(AndOr=='a'){
		tobepassed=filterResult;
		}
		if(AndOr=='o'){
			tobepassed=resultlist;
		}
	}
*/
	
	
	
	
}
