package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.search.SearchTerm;

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
	 * @story C4S02 :: Advanced Search; list of results that will be displayed
	 *        in the html page searchResult.
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
	 * @return void
	 * 
	 */
	public static void advancedSearch() {
		System.out.println("in advanced search");
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
					entitiesFound, topicsFound);
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
	 * @param org
	 *            :: "int"; int of 0 or 1 , 0 for searching in organization 1
	 *            for not searching in organizations.
	 * 
	 * @param entity
	 *            :: "int"; int of 0 or 1 , 0 for searching in MainEntity 1 for
	 *            not searching in MainEntity.
	 * 
	 * @param topic
	 *            :: "int"; int of 0 or 1 , 0 for searching in Topic 1 for not
	 *            searching in Topic.
	 * 
	 * @param plan
	 *            :: "int"; int of 0 or 1 , 0 for searching in Plan 1 for not
	 *            searching in Plan.
	 * 
	 * @param idea
	 *            :: "int"; int of 0 or 1 , 0 for searching in Idea 1 for not
	 *            searching in Idea.
	 * 
	 * @param item
	 *            :: "int"; int of 0 or 1 , 0 for searching in item 1 for not
	 *            searching in item.
	 * 
	 * @param comm
	 *            :: "int"; int of 0 or 1 , 0 for searching in comment 1 for not
	 *            searching in comment.
	 * 
	 * @param dayB
	 *            :: "int" representing day of the "Date"; where the user needs
	 *            all the result initialized before this date.
	 * 
	 * @param monthB
	 *            ::"int" representing month of the "Date"; where the user needs
	 *            all the result initialized before this date.
	 * 
	 * @param yearB
	 *            ::"int" representing year of the "Date"; where the user needs
	 *            all the result initialized before this date.
	 * 
	 * @param dayA
	 *            :: "int" representing day of the "Date"; where the user needs
	 *            all the result initialized after this date.
	 * 
	 * @param monthA
	 *            :: "int" representing month of the "Date"; where the user
	 *            needs all the result initialized after this date.
	 * 
	 * @param yearA
	 *            :: "int" representing year of the "Date"; where the user needs
	 *            all the result initialized after this date.
	 * 
	 * @param dayE
	 *            :: "int" representing day of the "Date"; where the user needs
	 *            all the result initialized exactly in this date.
	 * 
	 * @param monthE
	 *            :: "int" representing month of the "Date"; where the user
	 *            needs all the result initialized exactly in this date.
	 * 
	 * @param yearE
	 *            :: "int" representing year of the "Date"; where the user needs
	 *            all the result initialized exactly in this date.
	 * 
	 */

	public static void advSearch(String wKs, String wKi, String eK, String oT,
			String rOfS, String dateB, String dateA, String dateE) {

		listOfResults = new ArrayList<Model>();

		List<Organization> orgs = Organization.findAll();

		// remove unwanted Organization type(s)
		int searchIn = 0;
		String[] str = oT.split(",");
		if (!Boolean.parseBoolean(str[0])) {
			if (Boolean.parseBoolean(str[1])) {
				if (Boolean.parseBoolean(str[2])) {
					searchIn = 4; // public & private
				} else {
					if (Boolean.parseBoolean(str[3])) {
						searchIn = 5; // public & secrete
					} else {
						searchIn = 1; // public
					}
				}
			} else {
				if (Boolean.parseBoolean(str[2])) {
					if (Boolean.parseBoolean(str[3])) {
						searchIn = 6; // private & secrete
					} else {
						searchIn = 2; // private
					}
				} else {
					searchIn = 3; // secrete
				}
			}
		}

		switch (searchIn) {
		case 1: {
			for (int i = 0; i < orgs.size(); i++) {
				if (((Organization) orgs.get(i)).privacyLevel != 2) {
					orgs.remove(i);
				}
			}
			break;
		}
		case 2: {
			for (int i = 0; i < orgs.size(); i++) {
				if (((Organization) orgs.get(i)).privacyLevel != 1) {
					orgs.remove(i);
				}
			}
			break;
		}
		case 3: {
			for (int i = 0; i < orgs.size(); i++) {
				if (!(((Organization) orgs.get(i)).privacyLevel == 0 && Users
						.getEnrolledUsers((Organization) orgs.get(i)).contains(
								Security.getConnected()))
						|| !Security.getConnected().isAdmin) {
					orgs.remove(i);
				}
			}
			break;
		}
		case 4: {
			for (int i = 0; i < orgs.size(); i++) {
				if (((Organization) orgs.get(i)).privacyLevel == 0) {
					orgs.remove(i);
				}
			}
			break;
		}
		case 5: {
			for (int i = 0; i < orgs.size(); i++) {
				if (((Organization) orgs.get(i)).privacyLevel == 1) {
					orgs.remove(i);
				} else {
					if (!(((Organization) orgs.get(i)).privacyLevel == 0 && Users
							.getEnrolledUsers((Organization) orgs.get(i))
							.contains(Security.getConnected()))
							|| !Security.getConnected().isAdmin) {
						orgs.remove(i);
					}
				}
			}
			break;
		}
		case 6: {
			for (int i = 0; i < orgs.size(); i++) {
				if (((Organization) orgs.get(i)).privacyLevel == 2) {
					orgs.remove(i);
				} else {
					if (!(((Organization) orgs.get(i)).privacyLevel == 0 && Users
							.getEnrolledUsers((Organization) orgs.get(i))
							.contains(Security.getConnected()))
							|| !Security.getConnected().isAdmin) {
						orgs.remove(i);
					}
				}
			}
			break;
		}
		default: {
			break;
		}
		}

		// // .... ////

		str = rOfS.split(",");

		System.out.println("this is wKi" + wKi.getClass() + "###");

		boolean isIn = (wKi.compareTo("") != 0);
		System.out.println(isIn);

		wKs = "2," + eK.substring(0, eK.length() - 1) + ",2," + wKs;
		wKi = Integer.parseInt("" + eK.charAt(eK.length() - 1)) + "," + wKi;
		String[] keyWords = wKs.split(",");
		String[] keyWordsIn = wKi.split(",", 0);

		System.out.println("+++++++++++++>>>" + keyWords.length + "  "
				+ keyWordsIn.length);
		System.out.println(orgs.size());
		for (int k = 0; k < orgs.size(); k++) {
			System.out.println(orgs.get(k).name);
		}

		List<Model> tOrgs = null;

		tOrgs = new ArrayList<Model>();
		tOrgs.addAll(orgs);
		if (Boolean.parseBoolean(str[1]) && tOrgs.size() > 0) { // Organizations.....
			// Or parts
			for (int i = 0; i < keyWords.length; i += 2) {
				if (Integer.parseInt(keyWords[i]) == 2) {
					System.out.println("pooooooooooo");
					if (keyWords[i + 1].trim().compareTo("") != 0) {
						System.out.println("1");
						if (isIn) {
							System.out.println("2");
							AdvancedSearch.searchingOrganization(listOfResults,
									tOrgs, keyWords[i + 1], (i == 0),
									Integer.parseInt(keyWordsIn[i / 2]), true);
						} else {
							System.out.println("3");
							AdvancedSearch.searchingOrganization(listOfResults,
									tOrgs, keyWords[i + 1], (i == 0), 0, true);
						}
					}
				}
			}

			// And parts
			if (listOfResults.size() == 0) {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 1) {
						if (keyWords[i + 1].trim() != "") {
							AdvancedSearch.searchingOrganization(listOfResults,
									tOrgs, keyWords[i], false,
									Integer.parseInt(keyWordsIn[i / 2]), true);
							for (int j = i; j < keyWords.length; j += 2) {
								if (Integer.parseInt(keyWords[j]) == 1) {
									if (keyWords[j + 1].trim() != "") {
										if (isIn)
											AdvancedSearch
													.searchingOrganization(
															listOfResults,
															tOrgs,
															keyWords[i],
															(i == 0),
															Integer.parseInt(keyWordsIn[i / 2]),
															true);
										else
											AdvancedSearch
													.searchingOrganization(
															listOfResults,
															tOrgs, keyWords[i],
															(i == 0), 0, true);
									}
								}
							}
							break;
						}
					}
				}
			} else {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 1) {
						if (keyWords[i + 1].trim() != "") {
							if (isIn)
								AdvancedSearch.searchingOrganization(
										listOfResults, tOrgs, keyWords[i],
										(i == 0),
										Integer.parseInt(keyWordsIn[i / 2]),
										true);
							else
								AdvancedSearch.searchingOrganization(
										listOfResults, tOrgs, keyWords[i],
										(i == 0), 0, true);
						}
					}
				}
			}

			// Not parts
			if (listOfResults.size() != 0) {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 3) {
						if (keyWords[i + 1].trim() != "") {
							if (isIn)
								AdvancedSearch.searchingOrganization(
										listOfResults, tOrgs, keyWords[i],
										(i == 0),
										Integer.parseInt(keyWordsIn[i / 2]),
										true);
							else
								AdvancedSearch.searchingOrganization(
										listOfResults, tOrgs, keyWords[i],
										(i == 0), 0, true);
						}
					}
				}
			}
		}

		if (Boolean.parseBoolean(str[2])) { // Entities
			tOrgs = new ArrayList<Model>();

			for (int i = 0; i < orgs.size(); i++) {
				tOrgs.addAll(orgs.get(i).entitiesList);
			}

			// Or parts
			for (int i = 0; i < keyWords.length; i += 2) {
				if (Integer.parseInt(keyWords[i]) == 2) {
					if (keyWords[i + 1].trim() != "") {
						if (isIn)
							AdvancedSearch.searchingMainEntity(listOfResults,
									tOrgs, keyWords[i + 1], (i == 0),
									Integer.parseInt(keyWordsIn[i / 2]), true);
						else
							AdvancedSearch.searchingMainEntity(listOfResults,
									tOrgs, keyWords[i + 1], (i == 0), 0, true);
					}
				}
			}

			// And parts
			if (listOfResults.size() == 0) {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 1) {
						if (keyWords[i + 1].trim() != "") {
							AdvancedSearch.searchingMainEntity(listOfResults,
									tOrgs, keyWords[i], false,
									Integer.parseInt(keyWordsIn[i / 2]), true);
							for (int j = i; j < keyWords.length; j += 2) {
								if (Integer.parseInt(keyWords[j]) == 1) {
									if (keyWords[j + 1].trim() != "") {
										if (isIn)
											AdvancedSearch
													.searchingMainEntity(
															listOfResults,
															tOrgs,
															keyWords[i],
															(i == 0),
															Integer.parseInt(keyWordsIn[i / 2]),
															true);
										else
											AdvancedSearch.searchingMainEntity(
													listOfResults, tOrgs,
													keyWords[i], (i == 0), 0,
													true);
									}
								}
							}
							break;
						}
					}
				}
			} else {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 1) {
						if (keyWords[i + 1].trim() != "") {
							if (isIn)
								AdvancedSearch.searchingMainEntity(
										listOfResults, tOrgs, keyWords[i],
										(i == 0),
										Integer.parseInt(keyWordsIn[i / 2]),
										true);
							else
								AdvancedSearch.searchingMainEntity(
										listOfResults, tOrgs, keyWords[i],
										(i == 0), 0, true);
						}
					}
				}
			}

			// Not parts
			if (listOfResults.size() != 0) {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 3) {
						if (keyWords[i + 1].trim() != "") {
							if (isIn)
								AdvancedSearch.searchingMainEntity(
										listOfResults, tOrgs, keyWords[i],
										(i == 0),
										Integer.parseInt(keyWordsIn[i / 2]),
										true);
							else
								AdvancedSearch.searchingMainEntity(
										listOfResults, tOrgs, keyWords[i],
										(i == 0), 0, true);
						}
					}
				}
			}

		}

		if (Boolean.parseBoolean(str[3])) { // Topic
			tOrgs = new ArrayList<Model>();

			for (int i = 0; i < orgs.size(); i++) {
				for (int j = 0; j < orgs.get(i).entitiesList.size(); j++)
					tOrgs.addAll(orgs.get(i).entitiesList.get(j).topicList);
			}

			// Or parts
			for (int i = 0; i < keyWords.length; i += 2) {
				if (Integer.parseInt(keyWords[i]) == 2) {
					if (keyWords[i + 1].trim() != "") {
						if (isIn)
							AdvancedSearch.searchingTopic(listOfResults, tOrgs,
									keyWords[i + 1], (i == 0),
									Integer.parseInt(keyWordsIn[i / 2]), true);
						else
							AdvancedSearch.searchingTopic(listOfResults, tOrgs,
									keyWords[i + 1], (i == 0), 0, true);
					}
				}
			}

			// And parts
			if (listOfResults.size() == 0) {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 1) {
						if (keyWords[i + 1].trim() != "") {
							AdvancedSearch.searchingTopic(listOfResults, tOrgs,
									keyWords[i], false,
									Integer.parseInt(keyWordsIn[i / 2]), true);
							for (int j = i; j < keyWords.length; j += 2) {
								if (Integer.parseInt(keyWords[j]) == 1) {
									if (keyWords[j + 1].trim() != "") {
										if (isIn)
											AdvancedSearch
													.searchingTopic(
															listOfResults,
															tOrgs,
															keyWords[i],
															(i == 0),
															Integer.parseInt(keyWordsIn[i / 2]),
															true);
										else
											AdvancedSearch.searchingTopic(
													listOfResults, tOrgs,
													keyWords[i], (i == 0), 0,
													true);
									}
								}
							}
							break;
						}
					}
				}
			} else {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 1) {
						if (keyWords[i + 1].trim() != "") {
							if (isIn)
								AdvancedSearch.searchingTopic(listOfResults,
										tOrgs, keyWords[i], (i == 0),
										Integer.parseInt(keyWordsIn[i / 2]),
										true);
							else
								AdvancedSearch.searchingTopic(listOfResults,
										tOrgs, keyWords[i], (i == 0), 0, true);
						}
					}
				}
			}

			// Not parts
			if (listOfResults.size() != 0) {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 3) {
						if (keyWords[i + 1].trim() != "") {
							if (isIn)
								AdvancedSearch.searchingTopic(listOfResults,
										tOrgs, keyWords[i], (i == 0),
										Integer.parseInt(keyWordsIn[i / 2]),
										true);
							else
								AdvancedSearch.searchingTopic(listOfResults,
										tOrgs, keyWords[i], (i == 0), 0, true);
						}
					}
				}
			}

		}

		if (Boolean.parseBoolean(str[4])) { // Plan
			tOrgs = new ArrayList<Model>();

			for (int i = 0; i < orgs.size(); i++) {
				for (int j = 0; j < orgs.get(i).entitiesList.size(); j++)
					for (int k = 0; k < orgs.get(i).entitiesList.get(j).topicList
							.size(); k++)
						tOrgs.add(orgs.get(i).entitiesList.get(j).topicList
								.get(k).plan);
			}

			// Or parts
			for (int i = 0; i < keyWords.length; i += 2) {
				if (Integer.parseInt(keyWords[i]) == 2) {
					if (keyWords[i + 1].trim() != "") {
						if (isIn)
							AdvancedSearch.searchingPlan(listOfResults, tOrgs,
									keyWords[i + 1], (i == 0),
									Integer.parseInt(keyWordsIn[i / 2]), true);
						else
							AdvancedSearch.searchingPlan(listOfResults, tOrgs,
									keyWords[i + 1], (i == 0), 0, true);
					}
				}
			}

			// And parts
			if (listOfResults.size() == 0) {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 1) {
						if (keyWords[i + 1].trim() != "") {
							AdvancedSearch.searchingPlan(listOfResults, tOrgs,
									keyWords[i], false,
									Integer.parseInt(keyWordsIn[i / 2]), true);
							for (int j = i; j < keyWords.length; j += 2) {
								if (Integer.parseInt(keyWords[j]) == 1) {
									if (keyWords[j + 1].trim() != "") {
										if (isIn)
											AdvancedSearch
													.searchingPlan(
															listOfResults,
															tOrgs,
															keyWords[i],
															(i == 0),
															Integer.parseInt(keyWordsIn[i / 2]),
															true);
										else
											AdvancedSearch.searchingPlan(
													listOfResults, tOrgs,
													keyWords[i], (i == 0), 0,
													true);
									}
								}
							}
							break;
						}
					}
				}
			} else {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 1) {
						if (keyWords[i + 1].trim() != "") {
							if (isIn)
								AdvancedSearch.searchingPlan(listOfResults,
										tOrgs, keyWords[i], (i == 0),
										Integer.parseInt(keyWordsIn[i / 2]),
										true);
							else
								AdvancedSearch.searchingPlan(listOfResults,
										tOrgs, keyWords[i], (i == 0), 0, true);
						}
					}
				}
			}

			// Not parts
			if (listOfResults.size() != 0) {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 3) {
						if (keyWords[i + 1].trim() != "") {
							if (isIn)
								AdvancedSearch.searchingPlan(listOfResults,
										tOrgs, keyWords[i], (i == 0),
										Integer.parseInt(keyWordsIn[i / 2]),
										true);
							else
								AdvancedSearch.searchingPlan(listOfResults,
										tOrgs, keyWords[i], (i == 0), 0, true);
						}
					}
				}
			}
		}

		if (Boolean.parseBoolean(str[5])) { // Idea
			tOrgs = new ArrayList<Model>();

			for (int i = 0; i < orgs.size(); i++) {
				for (int j = 0; j < orgs.get(i).entitiesList.size(); j++)
					for (int k = 0; k < orgs.get(i).entitiesList.get(j).topicList
							.size(); k++)
						tOrgs.addAll(orgs.get(i).entitiesList.get(j).topicList
								.get(i).ideas);
			}

			// Or parts
			for (int i = 0; i < keyWords.length; i += 2) {
				if (Integer.parseInt(keyWords[i]) == 2) {
					if (keyWords[i + 1].trim() != "") {
						if (isIn)
							AdvancedSearch.searchingIdea(listOfResults, tOrgs,
									keyWords[i + 1], (i == 0),
									Integer.parseInt(keyWordsIn[i / 2]), true);
						else
							AdvancedSearch.searchingIdea(listOfResults, tOrgs,
									keyWords[i + 1], (i == 0), 0, true);
					}
				}
			}

			// And parts
			if (listOfResults.size() == 0) {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 1) {
						if (keyWords[i + 1].trim() != "") {
							AdvancedSearch.searchingIdea(listOfResults, tOrgs,
									keyWords[i], false,
									Integer.parseInt(keyWordsIn[i / 2]), true);
							for (int j = i; j < keyWords.length; j += 2) {
								if (Integer.parseInt(keyWords[j]) == 1) {
									if (keyWords[j + 1].trim() != "") {
										if (isIn)
											AdvancedSearch
													.searchingIdea(
															listOfResults,
															tOrgs,
															keyWords[i],
															(i == 0),
															Integer.parseInt(keyWordsIn[i / 2]),
															true);
										else
											AdvancedSearch.searchingIdea(
													listOfResults, tOrgs,
													keyWords[i], (i == 0), 0,
													true);
									}
								}
							}
							break;
						}
					}
				}
			} else {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 1) {
						if (keyWords[i + 1].trim() != "") {
							if (isIn)
								AdvancedSearch.searchingIdea(listOfResults,
										tOrgs, keyWords[i], (i == 0),
										Integer.parseInt(keyWordsIn[i / 2]),
										true);
							else
								AdvancedSearch.searchingIdea(listOfResults,
										tOrgs, keyWords[i], (i == 0), 0, true);
						}
					}
				}
			}

			// Not parts
			if (listOfResults.size() != 0) {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 3) {
						if (keyWords[i + 1].trim() != "") {
							if (isIn)
								AdvancedSearch.searchingIdea(listOfResults,
										tOrgs, keyWords[i], (i == 0),
										Integer.parseInt(keyWordsIn[i / 2]),
										true);
							else
								AdvancedSearch.searchingIdea(listOfResults,
										tOrgs, keyWords[i], (i == 0), 0, true);
						}
					}
				}
			}
		}

		if (Boolean.parseBoolean(str[6])) { // Item
			tOrgs = new ArrayList<Model>();

			for (int i = 0; i < orgs.size(); i++) {
				for (int j = 0; j < orgs.get(i).entitiesList.size(); j++)
					for (int k = 0; k < orgs.get(i).entitiesList.get(j).topicList
							.size(); k++)
						tOrgs.addAll(orgs.get(i).entitiesList.get(j).topicList
								.get(k).plan.items);
			}

			// Or parts
			for (int i = 0; i < keyWords.length; i += 2) {
				if (Integer.parseInt(keyWords[i]) == 2) {
					if (keyWords[i + 1].trim() != "") {
						if (isIn)
							AdvancedSearch.searchingItem(listOfResults, tOrgs,
									keyWords[i + 1], (i == 0),
									Integer.parseInt(keyWordsIn[i / 2]), true);
						else
							AdvancedSearch.searchingItem(listOfResults, tOrgs,
									keyWords[i + 1], (i == 0), 0, true);
					}
				}
			}

			// And parts
			if (listOfResults.size() == 0) {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 1) {
						if (keyWords[i + 1].trim() != "") {
							AdvancedSearch.searchingItem(listOfResults, tOrgs,
									keyWords[i], false,
									Integer.parseInt(keyWordsIn[i / 2]), true);
							for (int j = i; j < keyWords.length; j += 2) {
								if (Integer.parseInt(keyWords[j]) == 1) {
									if (keyWords[j + 1].trim() != "") {
										if (isIn)
											AdvancedSearch
													.searchingItem(
															listOfResults,
															tOrgs,
															keyWords[i],
															(i == 0),
															Integer.parseInt(keyWordsIn[i / 2]),
															true);
										else
											AdvancedSearch.searchingItem(
													listOfResults, tOrgs,
													keyWords[i], (i == 0), 0,
													true);
									}
								}
							}
							break;
						}
					}
				}
			} else {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 1) {
						if (keyWords[i + 1].trim() != "") {
							if (isIn)
								AdvancedSearch.searchingItem(listOfResults,
										tOrgs, keyWords[i], (i == 0),
										Integer.parseInt(keyWordsIn[i / 2]),
										true);
							else
								AdvancedSearch.searchingItem(listOfResults,
										tOrgs, keyWords[i], (i == 0), 0, true);
						}
					}
				}
			}

			// Not parts
			if (listOfResults.size() != 0) {
				for (int i = 0; i < keyWords.length; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 3) {
						if (keyWords[i + 1].trim() != "") {
							if (isIn)
								AdvancedSearch.searchingItem(listOfResults,
										tOrgs, keyWords[i], (i == 0),
										Integer.parseInt(keyWordsIn[i / 2]),
										true);
							else
								AdvancedSearch.searchingItem(listOfResults,
										tOrgs, keyWords[i], (i == 0), 0, true);
						}
					}
				}
			}
		}
		/*
		 * constrainTime(before, after, exact);
		 */

	}

	/**
	 * 
	 * @author M Ghanem
	 * 
	 * @story C4S02 Advanced Search Result; it removes any of the result that
	 *        violates the constrains of time before rendering it.
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
	 */

	public static void constrainTime(Date before, Date after, Date exact) {

		if (exact.compareTo(new Date(2010, 0, 0)) == 0) {
			if (exact.compareTo(after) != 0 || exact.compareTo(before) != 0) {
				if (before.before(after)) {
					if (after.compareTo(new Date(2010, 0, 0)) != 0) {
						for (int i = listOfResults.size() - 1; i > 0; i--) {
							if (listOfResults.get(i) instanceof Organization) {
								if (after.before(((Organization) listOfResults
										.get(i)).intializedIn)) {
									listOfResults.remove(i);
								}
							} else if (listOfResults.get(i) instanceof MainEntity) {
								if (after.before(((MainEntity) listOfResults
										.get(i)).intializedIn)) {
									listOfResults.remove(i);
								}
							} else if (listOfResults.get(i) instanceof Topic) {
								if (after
										.before(((Topic) listOfResults.get(i)).intializedIn)) {
									listOfResults.remove(i);
								}
							} else if (listOfResults.get(i) instanceof Plan) {
								if (after
										.before(((Plan) listOfResults.get(i)).intializedIn)) {
									listOfResults.remove(i);
								}
							} else if (listOfResults.get(i) instanceof Idea) {
								if (after
										.before(((Idea) listOfResults.get(i)).intializedIn)) {
									listOfResults.remove(i);
								}
							} else if (listOfResults.get(i) instanceof Item) {
								if (after
										.before(((Item) listOfResults.get(i)).startDate)) {
									listOfResults.remove(i);
								}
							}
						}
					}
					if (before.compareTo(new Date(2010, 0, 0)) != 0) {
						for (int i = listOfResults.size() - 1; i > 0; i--) {
							if (listOfResults.get(i) instanceof Organization) {
								if (before.after(((Organization) listOfResults
										.get(i)).intializedIn)) {
									listOfResults.remove(i);
								}
							} else if (listOfResults.get(i) instanceof MainEntity) {
								if (before.after(((MainEntity) listOfResults
										.get(i)).intializedIn)) {
									listOfResults.remove(i);
								}
							} else if (listOfResults.get(i) instanceof Topic) {
								if (before
										.after(((Topic) listOfResults.get(i)).intializedIn)) {
									listOfResults.remove(i);
								}
							} else if (listOfResults.get(i) instanceof Plan) {
								if (before
										.after(((Plan) listOfResults.get(i)).intializedIn)) {
									listOfResults.remove(i);
								}
							} else if (listOfResults.get(i) instanceof Idea) {
								if (before
										.after(((Idea) listOfResults.get(i)).intializedIn)) {
									listOfResults.remove(i);
								}
							} else if (listOfResults.get(i) instanceof Item) {
								if (before
										.after(((Item) listOfResults.get(i)).endDate)) {
									listOfResults.remove(i);
								}
							}
						}
					}
				}
			}
		} else {
			constrainTime(
					new Date(exact.getYear(), exact.getMonth(),
							exact.getDay() - 1), new Date(exact.getYear(),
							exact.getMonth(), exact.getDay() + 1), new Date(
							2010, 0, 0));
		}
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
	 * method handelingOrAnd decides which list is to be passed for the filter
	 * method if the choice is and so we will need to filter on the filtered
	 * list but if or then we filter on the main list
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
	// //////////////////////////////////////////////////////////////////
	// filter method by monica yousry
	// filtering on type (organisation,topic,idea,plan,entity,...etc)
	//
	// this commented method with the static parametar will help in defining
	// which list to pass for the filter method accrding to the user's choice
	// (and or or )

	public static void handelingOrAnd(char andOr) {
		if (andOr == 'a') {
			toBePassed = filterResult;
		}
		if (andOr == 'o') {
			toBePassed = listOfResults;
		}
	}

	/**
	 * this method filters the result by organization
	 * 
	 * @author Monica Yousry
	 * 
	 * @story C4S03 filtering results after sort
	 * @return void
	 */
	public static void filterO() {
		System.out.print("filterO");
		for (int i = 0; i < toBePassed.size(); i++) {// loop on the whole
			// search result
			if (toBePassed.get(i) instanceof Organization) {// if found an
				// organization
				filterResult.add(toBePassed.get(i));// add it to the list
			}
		}
		// }
	}

	/**
	 * this method filters the result by Ideas
	 * 
	 * @author: monica yousry
	 * @return: void
	 */
	public static void filterI() {

		System.out.println("filterI");
		for (int i = 0; i < toBePassed.size(); i++) {
			if (toBePassed.get(i) instanceof Idea) {
				filterResult.add(toBePassed.get(i));
			}
		}

	}

	/**
	 * this method filters the result by Topic
	 * 
	 * @author: monica yousry
	 * @return: void
	 */
	public static void filterT() {

		System.out.println("filterT");
		for (int i = 0; i < toBePassed.size(); i++) {
			if (toBePassed.get(i) instanceof Topic) {
				filterResult.add(toBePassed.get(i));
			}
		}
	}

	/**
	 * this method filters the result by Entity
	 * 
	 * @author: monica yousry
	 * @return: void
	 */
	public static void filterE() {

		System.out.println("filterE");
		for (int i = 0; i < toBePassed.size(); i++) {
			if (toBePassed.get(i) instanceof MainEntity) {

				filterResult.add(toBePassed.get(i));
			}
		}
	}

	/**
	 * this method filters the result by Plan
	 * 
	 * @author: monica yousry
	 * @return: void
	 */
	public static void filterP() {
		System.out.println("filterP");
		for (int i = 0; i < toBePassed.size(); i++) {
			if (toBePassed.get(i) instanceof Plan) {
				filterResult.add(toBePassed.get(i));
			}
		}
	}

	/**
	 * 
	 * sortA method sorts according to rates (known from input) in ascending
	 * order
	 * 
	 * task.
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
	 * 
	 */

	public static void sortA() {// char voteOrRate) {// ascending
		List<Model> toSort = new ArrayList<Model>();
		List<Model> notToSort = new ArrayList<Model>();
		// if (voteOrRate == 'r' || voteOrRate == 'R') { // sorting by rate

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
		// the previous too loops is to append to lists in one
		// }

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
		 * m=0;m<tosort.size();m++){ sorted.add(tosort.get((e))); } for (int
		 * m=0;m<nottosort.size();m++){ sorted.add(nottosort.get((e))); } //the
		 * previous too loops is to append to lists in one }
		 */

	}

	/**
	 * 
	 * sortD method sorts according to rates in descending order
	 * 
	 * task.
	 * 
	 * 
	 * @author Monica Yousry
	 * 
	 * @story C4S04 sort descending the search result according to rates
	 * 
	 * 
	 * @return void
	 * 
	 * 
	 */

	public static void sortD() {// char voteOrRate) {// descending

		List<Model> toSort = new ArrayList<Model>();
		List<Model> notToSort = new ArrayList<Model>();

		// if (voteOrRate == 'r' || voteOrRate == 'R') { // sorting by rate

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
		// the previous too loops is to append to lists in one
		// }

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
		 * m=0;m<tosort.size();m++){ sorted.add(tosort.get((e))); } for (int
		 * m=0;m<nottosort.size();m++){ sorted.add(nottosort.get((e))); } //the
		 * previous too loops is to append to lists in one }
		 */

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
			return file;
		} catch (IOException e) {
			return null;
		}
	}
}