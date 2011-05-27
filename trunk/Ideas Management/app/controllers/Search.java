package controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.search.SearchTerm;
import javax.swing.text.DateFormatter;

import com.sun.mail.handlers.text_html;
import com.sun.org.apache.bcel.internal.generic.NEW;

import models.Idea;
import models.Item;
import models.MainEntity;
import models.Organization;
import models.Plan;
import models.Tag;
import models.Topic;
import models.User;

import play.data.binding.types.DateBinder;
import play.db.jpa.Model;
import play.mvc.Controller;
import play.mvc.With;
import play.test.Fixtures;

import engines.AdvancedSearch;

/**
 * 
 * @author M Ghanem
 * 
 * @story C4SXX : Search Structure
 * 
 */
@With(Secure.class)
public class Search extends Controller {

	/**
	 * 
	 * @author M Ghanem
	 * 
	 *         list of results that will be displayed in the html page
	 *         searchResult.
	 * 
	 */
	public static List<Model> listOfResults = new ArrayList<Model>();

	public static List<Model> filterResult = new ArrayList<Model>();

	public static List<Model> sorted = new ArrayList<Model>();

	public static List<Model> toBePassed = listOfResults;

	/**
	 * 
	 * @author M Ghanem
	 * 
	 * @story C4S02 :: Advanced Search; display html page to take input and that
	 *        goes to advSR(...) method.
	 *  
	 */
	public static void advancedSearch() {
		User user = Security.getConnected();
		render(user);
	}

	/**
	 * @author Loaay Alkherbawy
	 * 
	 * @return void
	 * 
	 * @description Method that renders the searchResult View and divides the
	 *              listOfResults to 4 lists according to the type of the result
	 *              to render it
	 * 
	 * @return void
	 */
	public static void searchResult() {
		String connected = Security.connected();
		User user = Security.getConnected();
		List<Idea> ideasFound = new ArrayList<Idea>();
		List<Organization> organizationsFound = new ArrayList<Organization>();
		List<Topic> topicsFound = new ArrayList<Topic>();
		List<MainEntity> entitiesFound = new ArrayList<MainEntity>();
		List<Item> itemsFound = new ArrayList<Item>();
		List<Plan> plansFound = new ArrayList<Plan>();
		toBePassed = listOfResults;
		try {
			for (int i = 0; i < listOfResults.size(); i++) {
				if (listOfResults.get(i) instanceof Idea) {
					ideasFound.add((Idea) listOfResults.get(i));
				}
				if (listOfResults.get(i) instanceof Topic) {
					topicsFound.add((Topic) listOfResults.get(i));
				}
				if (listOfResults.get(i) instanceof MainEntity) {
					entitiesFound.add((MainEntity) listOfResults.get(i));
				}
				if (listOfResults.get(i) instanceof Organization) {
					organizationsFound.add((Organization) listOfResults.get(i));
				}
				if (listOfResults.get(i) instanceof Item) {
					itemsFound.add((Item) listOfResults.get(i));
				}
				if (listOfResults.get(i) instanceof Plan) {
					plansFound.add((Plan) listOfResults.get(i));
				}
			}
			download(user);
			List<Model> lof = listOfResults;
			render(user, connected, lof, ideasFound, organizationsFound,
					entitiesFound, topicsFound, plansFound, itemsFound);
		} catch (NullPointerException e) {
			render(user, connected, ideasFound, organizationsFound,
					entitiesFound, topicsFound, plansFound, itemsFound, user);
		}
	}
	
	/**
	 * 
	 * @author M Ghanem
	 * 
	 * @story C4S02 Advanced Search Result; search for any Entity in the DB
	 *        according to the given parameters.
	 * 
	 * @param wKs
	 *            'String' Contains un-exact wanted keys
	 * 
	 * @param wKi
	 *            'String' Contains un-exact wanted keys's =search in= type.
	 * 
	 * @param eK
	 *            'String' Contains exact wanted key
	 * 
	 * @param oT
	 *            'String' wanted organizations types.
	 * 
	 * @param rOfS
	 *            'String' types wanted to be shown in the result.
	 * 
	 * @param dateA
	 *            'String' contains the After date
	 * 
	 * @param dateB
	 *            'String' contains the Before date
	 * 
	 * @param dateE
	 *            'String' contains the Exact date
	 * 
	 */
	public static void advSearch(String wKs, String wKi, String eK, String oT,
			String rOfS, String dateB, String dateA, String dateE) {
		// Initialize and empty the list of search result.
		listOfResults = new ArrayList<Model>();
		// Get the Organizations according to the user request.
		List<Organization> orgs = Organization.findAll();
		Organization.findByType(orgs, oT);
		if (orgs == null)
			return;
		// Search in the Models user needs.
		String[] showROf = rOfS.split(",");
		// Formulating the search query of the user.
		boolean isExact = false;
		String[] keyWords;
		String[] keyWordsIn;
		if (eK.length() > 1) {
			String[] temp = eK.substring(0, eK.length() - 1).split(" ");
			wKs = "";
			wKi = "";
			for (int i = 0; i < temp.length; i++) {
				wKs += "2," + temp[i] + ",";
				wKi += eK.substring(eK.length() - 1, eK.length()) + ",";
			}
			isExact = true;
		} else {
			String[] temp1 = wKs.split(",");
			String[] temp1i = wKi.split(",");
			wKs = "";
			wKi = "";
			for (int i = 0; i < temp1.length - 1; i += 2) {
				String[] temp2 = temp1[i + 1].split(" ");
				for (int j = 0; j < temp2.length; j++) {
					wKs += temp1[i] + "," + temp2[j] + ",";
					if (wKi.compareTo("") != 0)
						wKi += temp1i[i / 2] + ",";
				}
			}
		}
		keyWords = wKs.split(",", 0);
		if (wKi.compareTo("") == 0) {
			keyWordsIn = new String[keyWords.length / 2];
			for (int i = 0; i < keyWordsIn.length; i++) {
				keyWordsIn[i] = "0";
			}
		} else {
			keyWordsIn = wKi.split(",", 0);
		}
		// Container for the current search-in Models.
		List<Model> currentModels = null;
		// Organizations.....
		if (Boolean.parseBoolean(showROf[1])) {
			currentModels = new ArrayList<Model>();
			currentModels.addAll(orgs);
			AdvancedSearch.solvingOrganizationQuery(keyWords, keyWordsIn,
					isExact, listOfResults, currentModels);
		}
		// MainEntities.....
		if (Boolean.parseBoolean(showROf[2])) {
			currentModels = new ArrayList<Model>();
			for (int i = 0; i < orgs.size(); i++) {
				currentModels.addAll(orgs.get(i).entitiesList);
			}
			AdvancedSearch.solvingEntityQuery(keyWords, keyWordsIn, isExact,
					listOfResults, currentModels);
		}
		// Topics.....
		if (Boolean.parseBoolean(showROf[3])) {
			currentModels = new ArrayList<Model>();
			for (int i = 0; i < orgs.size(); i++) {
				for (int j = 0; j < orgs.get(i).entitiesList.size(); j++)
					currentModels
							.addAll(orgs.get(i).entitiesList.get(j).topicList);
			}
			AdvancedSearch.solvingTopicQuery(keyWords, keyWordsIn, isExact,
					listOfResults, currentModels);
		}
		// Plans.....
		if (Boolean.parseBoolean(showROf[4])) {
			currentModels = new ArrayList<Model>();

			for (int i = 0; i < orgs.size(); i++) {
				for (int j = 0; j < orgs.get(i).entitiesList.size(); j++)
					for (int k = 0; k < orgs.get(i).entitiesList.get(j).topicList
							.size(); k++)
						currentModels
								.add(orgs.get(i).entitiesList.get(j).topicList
										.get(k).plan);
			}
			AdvancedSearch.solvingPlanQuery(keyWords, keyWordsIn, isExact,
					listOfResults, currentModels);
		}
		// Idea.....
		if (Boolean.parseBoolean(showROf[5])) {
			currentModels = new ArrayList<Model>();
			for (int i = 0; i < orgs.size(); i++) {
				for (int j = 0; j < orgs.get(i).entitiesList.size(); j++)
					for (int k = 0; k < orgs.get(i).entitiesList.get(j).topicList
							.size(); k++)
						currentModels
								.addAll(orgs.get(i).entitiesList.get(j).topicList
										.get(i).ideas);
			}
			AdvancedSearch.solvingIdeaQuery(keyWords, keyWordsIn, isExact,
					listOfResults, currentModels);
		}
		// Item.....
		if (Boolean.parseBoolean(showROf[5])) {
			currentModels = new ArrayList<Model>();

			for (int i = 0; i < orgs.size(); i++) {
				for (int j = 0; j < orgs.get(i).entitiesList.size(); j++) {
					for (int k = 0; k < orgs.get(i).entitiesList.get(j).topicList
							.size(); k++) {
						if (orgs.get(i).entitiesList.get(j).topicList.get(k).plan != null) {
							currentModels.addAll(orgs.get(i).entitiesList
									.get(j).topicList.get(k).plan.items);
						}
					}
				}
			}
			AdvancedSearch.solvingItemQuery(keyWords, keyWordsIn, isExact,
					listOfResults, currentModels);
		}
		// Applying Date constrains by User.
		AdvancedSearch.constrainTime(dateA, dateB, dateE, listOfResults);
	}

	/**
	 * 
	 * @author Loaay Alkherbawy
	 * 
	 * @story C4S01 Searching for organizations, entities, ideas and topics
	 *        using a given keyword
	 * 
	 * @param keyword
	 *            : the keyword the user enters to search for
	 * 
	 * @description the method that calls all the helper methods
	 *              searchForOrganization, searchForEntity, searchForIdea,
	 *              searchForTopic and sums up the results in one list
	 * 
	 * @return void
	 */
	public static void quickSearch(String keyword) {
		listOfResults = new ArrayList<Model>();
		if (keyword.equals(" ")) {
		} else {
			// Adding Organizations to search result
			List<Organization> organizationsList = searchForOrganization(keyword);
			for (int i = 0; i < organizationsList.size(); i++) {
				listOfResults.add(organizationsList.get(i));
			}
			// Adding Entities to search result
			List<MainEntity> EntitiesList = searchForEntity(keyword);
			for (int i = 0; i < EntitiesList.size(); i++) {
				listOfResults.add(EntitiesList.get(i));
			}
			// Adding Ideas to search result
			List<Idea> IdeasList = searchForIdea(keyword);
			for (int i = 0; i < IdeasList.size(); i++) {
				listOfResults.add(IdeasList.get(i));
			}
			// Adding Topics to search result
			List<Topic> TopicsList = searchForTopic(keyword);
			for (int i = 0; i < TopicsList.size(); i++) {
				listOfResults.add(TopicsList.get(i));
			}
		}
	}

	/**
	 * 
	 * @author Loaay Alkherbawy
	 * 
	 * @story C4S01 Searching for organizations using a given keyword
	 * 
	 * @param keyword
	 *            : the keyword the user enters to search for
	 * 
	 * @return List<Object>
	 * 
	 * @description the method that searches for organizations with the keyword
	 *              given
	 */
	public static List<Organization> searchForOrganization(String keyword) {
		User user = Security.getConnected();
		String[] keywords = { keyword };
		try {
			keywords = keyword.split("\\s+");
		} catch (NullPointerException e) {

		}
		List<Organization> listOfOrgs = new ArrayList<Organization>();
		for (int s = 0; s < keywords.length; s++) {
			List<Organization> listOfOrganizations = Organization.findAll();
			for (int i = 0; i < listOfOrganizations.size(); i++) {
				if (listOfOrganizations.get(i).name.toLowerCase().contains(
						keywords[s].toLowerCase())) {
					if (!listOfOrgs.contains(listOfOrganizations.get(i))) {
						listOfOrgs.add(listOfOrganizations.get(i));
					}
				} else {
					for (int j = 0; j < listOfOrganizations.get(i).relatedTags
							.size(); j++) {
						if (keywords[s].toLowerCase()
								.contains(
										listOfOrganizations.get(i).relatedTags
												.get(j).name.toLowerCase())) {
							if (!listOfOrgs
									.contains(listOfOrganizations.get(i))) {
								listOfOrgs.add(listOfOrganizations.get(i));
							}
						}
					}
				}
				if (!Users.isPermitted(user, "view",
						listOfOrganizations.get(i).id, "organization")) {
					listOfOrgs.remove(listOfOrganizations.get(i));
				}
			}
		}
		return listOfOrgs;
	}

	/**
	 * 
	 * @author Loaay Alkherbawy
	 * 
	 * @story C4S01 Searching for entities using a given keyword
	 * 
	 * @param keyword
	 *            : the keyword the user enters to search for
	 * 
	 * @return List<Object>
	 * 
	 * @description the method that searches for entities with the keyword given
	 */
	public static List<MainEntity> searchForEntity(String keyword) {
		User user = Security.getConnected();
		String[] keywords = { keyword };
		try {
			keywords = keyword.split("\\s+");
		} catch (NullPointerException e) {
		}
		List<MainEntity> listOfEnts = new ArrayList<MainEntity>();
		List<MainEntity> listOfEntities = MainEntity.findAll();
		for (int s = 0; s < keywords.length; s++) {
			for (int i = 0; i < listOfEntities.size(); i++) {
				if (listOfEntities.get(i).name.toLowerCase().contains(
						keywords[s].toLowerCase())
						|| listOfEntities.get(i).description.toLowerCase()
								.contains(keywords[s].toLowerCase())) {
					if (!listOfEnts.contains(listOfEntities.get(i))) {
						listOfEnts.add(listOfEntities.get(i));
					}
				} else {
					for (int j = 0; j < listOfEntities.get(i).tagList.size(); j++) {
						if (keywords[s].toLowerCase().contains(
								listOfEntities.get(i).tagList.get(j).name
										.toLowerCase())) {
							if (!listOfEnts.contains(listOfEntities.get(i))) {
								listOfEnts.add(listOfEntities.get(i));
							}
						}
					}
				}
				if (!Users.isPermitted(user, "view", listOfEntities.get(i).id,
						"Entity")) {
					listOfEnts.remove(listOfEntities.get(i));
				}
			}
		}
		return listOfEnts;
	}

	/**
	 * 
	 * @author Loaay Alkherbawy
	 * 
	 * @story C4S01 Searching for ideas using a given keyword
	 * 
	 * @param keyword
	 *            : the keyword the user enters to search for
	 * 
	 * @return List<Object>
	 * 
	 * @description the method that searches for ideas with the keyword given
	 */
	public static List<Idea> searchForIdea(String keyword) {
		User user = Security.getConnected();
		String[] keywords = { keyword };
		try {
			keywords = keyword.split("\\s+");
		} catch (NullPointerException e) {
		}
		List<Idea> listOfIdss = new ArrayList<Idea>();
		List<Idea> listOfIdeas = Idea.findAll();
		for (int s = 0; s < keywords.length; s++) {
			for (int i = 0; i < listOfIdeas.size(); i++) {
				if (listOfIdeas.get(i).title.toLowerCase().contains(
						keywords[s].toLowerCase())
						|| listOfIdeas.get(i).description.toLowerCase()
								.contains(keywords[s].toLowerCase())) {
					if (!listOfIdss.contains(listOfIdeas.get(i))) {
						listOfIdss.add(listOfIdeas.get(i));
					}
				} else {
					for (int j = 0; j < listOfIdeas.get(i).tagsList.size(); j++) {
						if (keywords[s].toLowerCase().contains(
								listOfIdeas.get(i).tagsList.get(j).name
										.toLowerCase())) {
							if (!listOfIdss.contains(listOfIdeas.get(i))) {
								listOfIdss.add(listOfIdeas.get(i));
							}
						}
					}
				}
				if (!Users.isPermitted(user, "view",
						listOfIdeas.get(i).belongsToTopic.id, "topic")) {
					listOfIdss.remove(listOfIdeas.get(i));
				}
				if (listOfIdeas.get(i).isDraft
						&& listOfIdeas.get(i).author.id != user.id) {
					listOfIdss.remove(listOfIdeas.get(i));
				}
			}
		}
		return listOfIdss;
	}

	/**
	 * 
	 * @author Loaay Alkherbawy
	 * 
	 * @story C4S01 Searching for topics using a given keyword
	 * 
	 * @param keyword
	 *            : the keyword the user enters to search for
	 * 
	 * @return List<Object>
	 * 
	 * @description the method that searches for Topics with the keyword given
	 */
	public static List<Topic> searchForTopic(String keyword) {
		User user = Security.getConnected();
		String[] keywords = { keyword };
		try {
			keywords = keyword.split("\\s+");
		} catch (NullPointerException e) {
		}
		List<Topic> listOfTopis = new ArrayList<Topic>();
		List<Topic> listOfTopics = Topic.findAll();
		for (int s = 0; s < keywords.length; s++) {
			for (int i = 0; i < listOfTopics.size(); i++) { // Looping on the
															// list of
															// organization
				if (listOfTopics.get(i).title.toLowerCase().contains(
						keywords[s].toLowerCase())
						|| listOfTopics.get(i).description.toLowerCase()
								.contains(keywords[s].toLowerCase())) {
					if (!listOfTopis.contains(listOfTopics.get(i))) {
						listOfTopis.add(listOfTopics.get(i));
					}
				} else {
					for (int j = 0; j < listOfTopics.get(i).tags.size(); j++) {
						if (keywords[s].toLowerCase().contains(
								listOfTopics.get(i).tags.get(j).name
										.toLowerCase())) {
							if (!listOfTopis.contains(listOfTopics.get(i))) {
								listOfTopis.add(listOfTopics.get(i));
							}
						}
					}
				}
				if (!Users.isPermitted(user, "view", listOfTopics.get(i).id,
						"topic")) {
					listOfTopis.remove(listOfTopics.get(i));
				}
			}
		}
		return listOfTopis;
	}

	/**
	 * @description: method handelingOrAnd decides which list is to be passed
	 *               for the filter method if the choice is and so we will need
	 *               to filter on the filtered list but if or then we filter on
	 *               the main list
	 * 
	 * @author Monica Yousry
	 * 
	 * @story C4S03 filtering results after sort
	 * 
	 * @param andOr
	 *            :: "char"; of the filter option view as input from user
	 * 
	 * @return void.
	 * 
	 */

	public static void handelingOrAnd(char andOr) {
		if (andOr == 'a') {
			toBePassed = filterResult;
		}
		if (andOr == 'o') {
			toBePassed = listOfResults;
		}
	}

	/**
	 * 
	 * @description: sortA method sorts according to rates (known from input) in
	 *               ascending order
	 * 
	 *               task.
	 * 
	 * 
	 * @author Monica Yousry
	 * 
	 * @story C4S04 sort ascending the search result according to rates
	 * 
	 * 
	 * 
	 * @return void
	 * 
	 */

	public static void sortA() {// char voteOrRate) {// ascending
		List<Model> toSort = new ArrayList<Model>();
		List<Model> notToSort = new ArrayList<Model>();

		/*
		 * a for loop to find all objects having attribute rate and add them to
		 * a list called to sort and the rest of the search results are to be in
		 * a list not to be sorted
		 */

		for (int i = 0; i < listOfResults.size(); i++) {
			if (listOfResults.get(i) instanceof Idea
					|| listOfResults.get(i) instanceof Plan) {
				toSort.add(listOfResults.get(i));
			} else {
				notToSort.add(listOfResults.get(i));
			}
		}

		for (int j = 0; j < toSort.size(); j++) {
			String rate1 = "0";
			String rate2 = "0";
			if (toSort.get(j) instanceof Idea) {
				Idea temp1 = (Idea) toSort.get(j);
				rate1 = temp1.rating;
				if (rate1.equalsIgnoreCase("Not yet rated"))
					rate1 = "0";
			} else {
				Plan temp1 = (Plan) toSort.get(j);
				rate1 = temp1.rating;

				if (rate1.equalsIgnoreCase("Not yet rated"))
					rate1 = "0";
			}
			for (int k = 0; k < toSort.size(); k++) {
				if (toSort.get(k) instanceof Idea) {
					Idea temp2 = (Idea) toSort.get(k);
					rate2 = temp2.rating;

					if (rate2.equalsIgnoreCase("Not yet rated"))
						rate2 = "0";
				} else {
					Plan temp2 = (Plan) toSort.get(k);
					rate2 = temp2.rating;

					if (rate2.equalsIgnoreCase("Not yet rated"))
						rate2 = "0";
				}

				if (Integer.parseInt(rate1) > Integer.parseInt(rate2)) { // sorting
					Model temp = (Model) toSort.get(k);
					toSort.set(k, toSort.get(j));
					toSort.set(j, temp);
				}
			}
		}

		sorted = new ArrayList<Model>();// final sorted list
		for (int m = 0; m < toSort.size(); m++) {
			sorted.add(toSort.get((m)));
		}
		for (int m = 0; m < notToSort.size(); m++) {
			sorted.add(notToSort.get((m)));
		}
		listOfResults = toSort;
		searchResult();

	}

	/**
	 * 
	 * @description: sortD_Views method sorts according to views in descending
	 *               order
	 * 
	 *               task.
	 * 
	 * 
	 * @author Monica Yousry
	 * 
	 * @story C4S04 sort descending the search result according to views
	 * 
	 * 
	 * @return void
	 * 
	 * 
	 */

	public static void sortD_Views() {// descending

		List<Model> toSort = new ArrayList<Model>();
		List<Model> notToSort = new ArrayList<Model>();

		/*
		 * a for loop to find all objects having attribute rate and add them to
		 * a list called to sort and the rest of the search results are to be in
		 * a list not to be sorted
		 */

		for (int i = 0; i < listOfResults.size(); i++) {
			if (listOfResults.get(i) instanceof Idea
					|| listOfResults.get(i) instanceof Topic) {
				toSort.add(listOfResults.get(i));
			} else {
				notToSort.add(listOfResults.get(i));
			}
		}
		toSort = listOfResults;

		for (int j = 0; j < toSort.size(); j++) {
			int view1 = 0;
			int view2 = 0;
			if (toSort.get(j) instanceof Idea) {
				Idea temp1 = (Idea) toSort.get(j);
				view1 = temp1.viewed;
			} else {
				if (toSort.get(j) instanceof Topic) {
					Topic temp1 = (Topic) toSort.get(j);
					view1 = temp1.viewed;
				} else {
					if (toSort.get(j) instanceof Plan) {
						Plan temp1 = (Plan) toSort.get(j);
						view1 = temp1.viewed;
					} else {
						if (toSort.get(j) instanceof Organization) {
							Organization temp1 = (Organization) toSort.get(j);
							view1 = temp1.viewed;
						} else {
							if (toSort.get(j) instanceof MainEntity) {
								MainEntity temp1 = (MainEntity) toSort.get(j);
								view1 = temp1.viewed;
							}
						}
					}
				}
			}
			for (int k = 0; k < toSort.size(); k++) {
				if (toSort.get(k) instanceof Idea) {
					Idea temp2 = (Idea) toSort.get(k);
					view2 = temp2.viewed;

				} else {
					if (toSort.get(k) instanceof Topic) {
						Topic temp2 = (Topic) toSort.get(k);
						view2 = temp2.viewed;
					} else {
						if (toSort.get(k) instanceof Plan) {
							Plan temp2 = (Plan) toSort.get(k);
							view2 = temp2.viewed;
						} else {
							if (toSort.get(k) instanceof Organization) {
								Organization temp2 = (Organization) toSort
										.get(k);
								view2 = temp2.viewed;
							} else {
								if (toSort.get(k) instanceof MainEntity) {
									MainEntity temp2 = (MainEntity) toSort
											.get(k);
									view2 = temp2.viewed;
								}
							}
						}
					}
				}

				if (view1 < view2) { // sorting
					Model temp = (Model) toSort.get(k);
					toSort.set(k, toSort.get(j));
					toSort.set(j, temp);
				}
			}
		}

		sorted = new ArrayList<Model>();// final sorted list
		for (int m = 0; m < toSort.size(); m++) {
			sorted.add(toSort.get((m)));
		}
		for (int m = 0; m < notToSort.size(); m++) {
			sorted.add(notToSort.get((m)));
		}

		listOfResults = toSort;
		searchResult();

	}

	/**
	 * @author Loaay Alkherbawy
	 * 
	 * @param User
	 *            user: the User connected now
	 * 
	 * @return File the file containing the search result
	 * 
	 * @description this method generates the .csv file that will be downloaded
	 *              and puts it in the public folder
	 */

	public static File download(User user) {
		try {
			String path = Search.class.getProtectionDomain().getCodeSource()
					.getLocation().getPath();
			File root = new File(path + "/public/");
			File file = new File(root, "searchResults.csv");
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write("Search results for," + user.firstName + ","
					+ user.lastName);
			out.newLine();
			out.write("Type,Result,Description");
			out.newLine();
			for (int i = 0; i < listOfResults.size(); i++) {
				if (listOfResults.get(i) instanceof Idea) {
					Idea item = (Idea) listOfResults.get(i);
					out.write("Idea," + item.title + "," + item.description);
				}
				if (listOfResults.get(i) instanceof Topic) {
					Topic item = (Topic) listOfResults.get(i);
					out.write("Topic," + item.title + "," + item.description);
				}
				if (listOfResults.get(i) instanceof Organization) {
					Organization item = (Organization) listOfResults.get(i);
					out.write("Organization," + item.name
							+ ",no description for organizations");
				}
				if (listOfResults.get(i) instanceof MainEntity) {
					MainEntity item = (MainEntity) listOfResults.get(i);
					out.write("Entity," + item.name + "," + item.description);
				}
				if (listOfResults.get(i) instanceof Plan) {
					Plan item = (Plan) listOfResults.get(i);
					out.write("Entity," + item.title + "," + item.description);
				}
				if (listOfResults.get(i) instanceof Item) {
					Item item = (Item) listOfResults.get(i);
					out.write("Entity," + item.summary + "," + item.description);
				}
				out.newLine();
			}
			out.close();
			File zip = new File(root, "results.zip");
			if (!zip.exists()) {
				zip.createNewFile();
			}
			ZipOutputStream zOut = new ZipOutputStream(
					new BufferedOutputStream(new FileOutputStream(zip)));
			byte[] data = new byte[1000];
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(file));
			int count;
			zOut.putNextEntry(new ZipEntry(file.getName()));
			while ((count = in.read(data, 0, 1000)) != -1) {
				zOut.write(data, 0, count);
			}
			in.close();
			zOut.flush();
			zOut.close();
			return file;
		} catch (IOException e) {
			return null;
		}
	}

	public static void sortD() {// descending

		List<Model> toSort = new ArrayList<Model>();
		List<Model> notToSort = new ArrayList<Model>();

		/*
		 * a for loop to find all objects having attribute rate and add them to
		 * a list called to sort and the rest of the search results are to be in
		 * a list not to be sorted
		 */

		for (int i = 0; i < listOfResults.size(); i++) {
			if (listOfResults.get(i) instanceof Idea
					|| listOfResults.get(i) instanceof Plan) {
				toSort.add(listOfResults.get(i));
			} else {
				notToSort.add(listOfResults.get(i));
			}
		}

		for (int j = 0; j < toSort.size(); j++) {
			String rate1 = "0";
			String rate2 = "0";
			if (toSort.get(j) instanceof Idea) {
				Idea temp1 = (Idea) toSort.get(j);
				rate1 = temp1.rating;

				if (rate1.equalsIgnoreCase("Not yet rated"))
					rate1 = "0";
			} else {
				Plan temp1 = (Plan) toSort.get(j);
				rate1 = temp1.rating;

				if (rate1.equalsIgnoreCase("Not yet rated"))
					rate1 = "0";
			}
			for (int k = 0; k < toSort.size(); k++) {
				if (toSort.get(k) instanceof Idea) {
					Idea temp2 = (Idea) toSort.get(k);
					rate2 = temp2.rating;

					if (rate2.equalsIgnoreCase("Not yet rated"))
						rate2 = "0";
				} else {
					Plan temp2 = (Plan) toSort.get(k);
					rate2 = temp2.rating;

					if (rate2.equalsIgnoreCase("Not yet rated"))
						rate2 = "0";
				}

				if (Integer.parseInt(rate1) < Integer.parseInt(rate2)) { // sorting
					Model temp = (Model) toSort.get(k);
					toSort.set(k, toSort.get(j));
					toSort.set(j, temp);
				}
			}
		}

		sorted = new ArrayList<Model>();// final sorted list
		for (int m = 0; m < toSort.size(); m++) {
			sorted.add(toSort.get((m)));
		}
		for (int m = 0; m < notToSort.size(); m++) {
			sorted.add(notToSort.get((m)));
		}

		listOfResults = toSort;
		searchResult();

	}

	/**
	 * 
	 * @description: sortA_Views method sorts according to views in ascending
	 *               order
	 * 
	 *               task.
	 * 
	 * 
	 * @author Monica Yousry
	 * 
	 * @story C4S04 sort ascending the search result according to views
	 * 
	 * @return void
	 * 
	 * 
	 */

	public static void sortA_Views() {// descending

		List<Model> toSort = new ArrayList<Model>();
		List<Model> notToSort = new ArrayList<Model>();

		/*
		 * a for loop to find all objects having attribute rate and add them to
		 * a list called to sort and the rest of the search results are to be in
		 * a list not to be sorted
		 */

		for (int i = 0; i < listOfResults.size(); i++) {
			if (listOfResults.get(i) instanceof Idea
					|| listOfResults.get(i) instanceof Topic) {
				toSort.add(listOfResults.get(i));
			} else {
				notToSort.add(listOfResults.get(i));
			}
		}
		toSort = listOfResults;

		for (int j = 0; j < toSort.size(); j++) {
			int view1 = 0;
			int view2 = 0;
			if (toSort.get(j) instanceof Idea) {
				Idea temp1 = (Idea) toSort.get(j);
				view1 = temp1.viewed;
			} else {
				if (toSort.get(j) instanceof Topic) {
					Topic temp1 = (Topic) toSort.get(j);
					view1 = temp1.viewed;
				} else {
					if (toSort.get(j) instanceof Plan) {
						Plan temp1 = (Plan) toSort.get(j);
						view1 = temp1.viewed;
					} else {
						if (toSort.get(j) instanceof Organization) {
							Organization temp1 = (Organization) toSort.get(j);
							view1 = temp1.viewed;
						} else {
							if (toSort.get(j) instanceof MainEntity) {
								MainEntity temp1 = (MainEntity) toSort.get(j);
								view1 = temp1.viewed;
							}
						}
					}
				}
			}
			for (int k = 0; k < toSort.size(); k++) {
				if (toSort.get(k) instanceof Idea) {
					Idea temp2 = (Idea) toSort.get(k);
					view2 = temp2.viewed;

				} else {
					if (toSort.get(k) instanceof Topic) {
						Topic temp2 = (Topic) toSort.get(k);
						view2 = temp2.viewed;
					} else {
						if (toSort.get(k) instanceof Plan) {
							Plan temp2 = (Plan) toSort.get(k);
							view2 = temp2.viewed;
						} else {
							if (toSort.get(k) instanceof Organization) {
								Organization temp2 = (Organization) toSort
										.get(k);
								view2 = temp2.viewed;
							} else {
								if (toSort.get(k) instanceof MainEntity) {
									MainEntity temp2 = (MainEntity) toSort
											.get(k);
									view2 = temp2.viewed;
								}
							}
						}
					}
				}

				if (view1 > view2) { // sorting
					Model temp = (Model) toSort.get(k);
					toSort.set(k, toSort.get(j));
					toSort.set(j, temp);
				}
			}
		}

		sorted = new ArrayList<Model>();// final sorted list
		for (int m = 0; m < toSort.size(); m++) {
			sorted.add(toSort.get((m)));
		}
		for (int m = 0; m < notToSort.size(); m++) {
			sorted.add(notToSort.get((m)));
		}

		listOfResults = toSort;
		searchResult();

	}

}