package controllers;

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
	public static List<Model> listOfResults;

	public static List<Model> filterResult = new ArrayList<Model>();

	public static List<Model> sorted = new ArrayList<Model>();

	public static List<Model> tobepassed = listOfResults;

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
		render();
	}

	/**
	 * @auther monica
	 * 
	 *         this method is to render the filter_option page
	 * 
	 */

	public static void filter_options() {

		List<Model> lof = listOfResults;

		render(lof, filterResult, tobepassed);
	}

	/**
	 * @author monica
	 * 
	 *         this method renders to show reslts after filter
	 */

	public static void showAfterFilter() {
		String connected = Security.connected();
		List<Idea> ideasFound = new ArrayList<Idea>();
		List<Organization> organizationsFound = new ArrayList<Organization>();
		List<Topic> topicsFound = new ArrayList<Topic>();
		List<MainEntity> entitiesFound = new ArrayList<MainEntity>();
		for (int i = 0; i < filterResult.size(); i++) {
			if (filterResult.get(i) instanceof Idea) {
				ideasFound.add((Idea) filterResult.get(i));
			}
			if (filterResult.get(i) instanceof Topic) {
				topicsFound.add((Topic) filterResult.get(i));
			}
			if (filterResult.get(i) instanceof MainEntity) {
				entitiesFound.add((MainEntity) filterResult.get(i));
			}
			if (filterResult.get(i) instanceof Organization) {
				organizationsFound.add((Organization) filterResult.get(i));
			}
		}

		List<Model> lof = filterResult;

		render(connected, lof, ideasFound, organizationsFound, entitiesFound,
				topicsFound, filterResult, tobepassed);
	}

	/**
	 * @author Loaay Alkherbawy
	 * 
	 *         Method that renders the searchResult View
	 */
	public static void searchResult() {
		String connected = Security.connected();
		User u = Security.getConnected();
		List<Idea> ideasFound = new ArrayList<Idea>();
		List<Organization> organizationsFound = new ArrayList<Organization>();
		List<Topic> topicsFound = new ArrayList<Topic>();
		List<MainEntity> entitiesFound = new ArrayList<MainEntity>();
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
		}
		for (int i = 0; i < listOfResults.size(); i++) {
			System.out.println(listOfResults.get(i).toString());
		}
		System.out.println(organizationsFound.size() + " /organizations found");
		List<Model> lof = listOfResults;
		render(u, connected, lof, ideasFound, organizationsFound,
				entitiesFound, topicsFound);
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

	public static void advSearch(int searchIn, String wantKey,
			String unWantKey, int org, int entity, int topic, byte plan,
			int idea, int item, int comm, int dayB, int monthB, int yearB,
			int dayA, int monthA, int yearA, int dayE, int monthE, int yearE) {

		listOfResults = new ArrayList<Model>();

		Date before = new Date(yearB + 2010, monthB, dayB);
		Date after = new Date(yearA + 2010, monthA, dayA);
		Date exact = new Date(yearE + 2010, monthE, dayE);

		List<Model> orgs = Organization.findAll();

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
				if (((Organization) orgs.get(i)).privacyLevel != 0
						|| Users.getEnrolledUsers((Organization) orgs.get(i))
								.contains(Security.getConnected())) {
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
				}
			}
			break;
		}
		case 6: {
			for (int i = 0; i < orgs.size(); i++) {
				if (((Organization) orgs.get(i)).privacyLevel == 2) {
					orgs.remove(i);
				}
			}
			break;
		}
		default: {
			break;
		}
		}

		System.out.println("Wanted Key ==> " + wantKey);
		System.out.println("un Wanted Key ==> " + unWantKey);

		// Organization
		if (org == 0) {
			for (int i = 0; i < orgs.size(); i++) {
				if ((!((Organization) orgs.get(i)).name.toLowerCase().contains(
						unWantKey.toLowerCase()))
						|| unWantKey.equals("")) {
					if (((Organization) orgs.get(i)).name.toLowerCase()
							.contains(wantKey.toLowerCase())) {
						listOfResults.add(orgs.get(i));
						System.out.println(orgs.get(i) + " ADDDDDDED");
					} else {
						boolean add = true;
						List<Tag> x = ((Organization) orgs.get(i)).relatedTags;
						for (int j = 0; j < x.size(); j++) {
							if (x.get(j).name.toLowerCase().contains(
									unWantKey.toLowerCase())) {
								add = false;
							}
						}
						if (add) {
							for (int j = 0; j < x.size(); j++) {
								if (x.get(j).name.toLowerCase().contains(
										wantKey.toLowerCase())) {
									listOfResults.add(orgs.get(i));
									System.out.println(orgs.get(i)
											+ " ADDDDDDED");
									break;
								}
							}
						}
					}
				}
			}
		}
		System.out.println("-------------");
		for (int i = 0; i < orgs.size(); i++) {
			System.out.println(orgs.get(i));
		}
		System.out.println("-------------");
		System.out.println("search entity");
		// Entity
		if (entity == 0) {
			for (int i = 0; i < orgs.size(); i++) {
				if (((Organization) orgs.get(i)).entitiesList != null) {
					List<MainEntity> entityz = ((Organization) orgs.get(i)).entitiesList;

					System.out.println(entityz.size());

					for (int j = 0; j < entityz.size(); j++) {
						if ((!((MainEntity) entityz.get(j)).name.toLowerCase()
								.contains(unWantKey) && !((MainEntity) entityz
								.get(j)).description.contains(unWantKey))
								|| unWantKey.equals("")) {
							if (((MainEntity) entityz.get(j)).name
									.toLowerCase().contains(wantKey)) {
								listOfResults.add(entityz.get(j));
								System.out.println(entityz.get(j)
										+ " ADDDDDDED");
							} else {
								if (((MainEntity) entityz.get(j)).description
										.toLowerCase().contains(wantKey)) {
									listOfResults.add(entityz.get(j));
								} else {
									boolean add = true;
									List<Tag> x = ((MainEntity) entityz.get(j)).tagList;
									for (int k = 0; k < x.size(); k++) {
										if (x.get(k).name.toLowerCase()
												.contains(unWantKey)) {
											add = false;
										}
									}
									if (add) {
										for (int k = 0; k < x.size(); k++) {
											if (x.get(k).name.toLowerCase()
													.contains(wantKey)) {
												listOfResults.add(entityz
														.get(j));
												System.out.println(entityz
														.get(j) + " ADDDDDDED");
												break;
											}
										}
									}
								}
							}
						}

					}
					System.out.println(i);
				}
			}
		}
		System.out.println("search Topic");

		// Topic

		List topics = null;
		if (topic == 0) {
			topics = new ArrayList<Topic>();
			for (int i = 0; i < orgs.size(); i++) {
				for (int j = 0; j < ((Organization) orgs.get(i)).entitiesList
						.size(); j++) {
					for (int k = 0; k < ((MainEntity) ((Organization) orgs
							.get(i)).entitiesList.get(j)).topicList.size(); k++) {
						topics.add(((MainEntity) ((Organization) orgs.get(i)).entitiesList
								.get(j)).topicList.get(k));
					}
				}
			}
			for (int z = 0; z < topics.size(); z++) {
				if (!((Topic) topics.get(z)).title.toLowerCase().contains(
						unWantKey)
						&& !((Topic) topics.get(z)).description.toLowerCase()
								.contains(unWantKey)) {
					if (((Topic) topics.get(z)).title.toLowerCase().contains(
							wantKey)) {
						listOfResults.add((Topic) topics.get(z));
					} else {
						if (((Topic) topics.get(z)).description.toLowerCase()
								.contains(wantKey)) {
							listOfResults.add((Topic) topics.get(z));
						} else {
							boolean add = true;
							List<Tag> x = ((Topic) topics.get(z)).tags;
							for (int jz = 0; jz < x.size(); jz++) {
								if (x.get(jz).name.toLowerCase().contains(
										unWantKey)) {
									add = false;
								}
							}
							if (add) {
								for (int jz = 0; jz < x.size(); jz++) {
									if (x.get(jz).name.toLowerCase().contains(
											wantKey)) {
										listOfResults
												.add((Topic) topics.get(z));
										break;
									}
								}
							}
						}
					}
				}
			}
		}

		// Plans
		List plans = null;
		if (plan == 0) {
			plans = new ArrayList<Plan>();
			for (int i = 0; i < topics.size(); i++) {
				plans.add(((Topic) topics.get(i)).plan);
			}
			for (int iz = 0; iz < plans.size(); iz++) {
				if (((Plan) plans.get(iz)) != null
						&& !((Plan) plans.get(iz)).title.toLowerCase()
								.contains(unWantKey)) {
					if (((Plan) plans.get(iz)).title.toLowerCase().contains(
							wantKey)) {
						listOfResults.add((Plan) plans.get(iz));
					}
				}
			}
		}

		// Ideas
		if (idea == 0) {
			List ideas = new ArrayList<Idea>();
			for (int i = 0; i < topics.size(); i++) {
				for (int j = 0; j < ((Topic) topics.get(i)).ideas.size(); j++) {
					ideas.add(((Idea) ((Topic) topics.get(i)).ideas.get(j)));
				}
			}
			for (int iz = 0; iz < ideas.size(); iz++) {
				if (((Idea) ideas.get(iz)) != null
						&& !((Idea) ideas.get(iz)).title.contains(unWantKey)
						&& !((Idea) ideas.get(iz)).description.toLowerCase()
								.contains(unWantKey)) {
					if (((Idea) ideas.get(iz)).title.toLowerCase().contains(
							wantKey)) {
						listOfResults.add((Idea) ideas.get(iz));
					} else {
						if (((Idea) ideas.get(iz)).description.toLowerCase()
								.contains(wantKey)) {
							listOfResults.add((Idea) ideas.get(iz));
						} else {
							boolean add = true;
							List<Tag> x = ((Idea) ideas.get(iz)).tagsList;
							for (int j = 0; j < x.size(); j++) {
								if (x.get(j).name.toLowerCase().contains(
										unWantKey)) {
									add = false;
								}
							}
							if (add) {
								for (int j = 0; j < x.size(); j++) {
									if (x.get(j).name.toLowerCase().contains(
											wantKey)) {
										listOfResults.add((Idea) ideas.get(iz));
										break;
									}
								}
							}
						}
					}
				}
			}
		}

		// Item
		if (item == 0) {
			List items = new ArrayList<Idea>();
			for (int i = 0; i < plans.size(); i++) {
				if (((models.Plan) plans.get(i)) != null) {
					for (int j = 0; j < ((models.Plan) plans.get(i)).items
							.size(); j++) {
						items.add((((models.Plan) plans.get(i)).items.get(j)));
					}
				}
			}
			for (int i = 0; i < items.size(); i++) {
				if (!((Item) items.get(i)).summary.toLowerCase().contains(
						unWantKey)
						&& !((Item) items.get(i)).description.toLowerCase()
								.contains(unWantKey)) {
					if (((Item) items.get(i)).summary.contains(wantKey)) {
						listOfResults.add((Item) items.get(i));
					} else {
						if (((Item) items.get(i)).description.toLowerCase()
								.contains(wantKey)) {
							listOfResults.add((Item) items.get(i));
						} else {
							boolean add = true;
							List<Tag> x = ((Item) items.get(i)).tags;
							for (int j = 0; j < x.size(); j++) {
								if (x.get(j).name.toLowerCase().contains(
										unWantKey)) {
									add = false;
								}
							}
							if (add) {
								for (int j = 0; j < x.size(); j++) {
									if (x.get(j).name.toLowerCase().contains(
											wantKey)) {
										listOfResults.add((Item) items.get(i));
										break;
									}
								}
							}
						}
					}
				}
			}
		}

		// Comments

		constrainTime(before, after, exact);

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
	 */
	public static void quickSearch(String keyword) {
		System.out.println(keyword);
		listOfResults = new ArrayList<Model>();
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
		System.out.println(listOfResults.size() + "fin");
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
	 */
	public static List<Organization> searchForOrganization(String keyword) {
		String userId = Security.connected();
		User u = Security.getConnected();
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
				System.out.println(listOfOrgs.toString() + "----------");
				if (!Users.getEnrolledUsers(listOfOrganizations.get(i))
						.contains(u)) {
					switch (listOfOrganizations.get(i).privacyLevel) {
					case 0:
						listOfOrgs.remove(listOfOrganizations.get(i));
						break;
					default:
						break;
					}
				}
				// for (int k = 0; k < enrolledUsers.size(); k++) {
				// if (userId.compareTo(enrolledUsers.get(k).username) != 0) {
				// switch (listOfOrganizations.get(i).privacyLevel) {
				// case 0:
				// listOfOrgs.remove(listOfOrganizations.get(i));
				// break;
				// default:
				// break;
				// }
				// }
				// }
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
	 */
	public static List<MainEntity> searchForEntity(String keyword) {
		String userId = Security.connected();
		User u = Security.getConnected();
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
				if (!listOfEntities.get(i).followers.contains(u)) {
					switch (listOfEntities.get(i).organization.privacyLevel) {
					case 0:
						listOfEnts.remove(listOfEntities.get(i));
						break;
					default:
						break;
					}
				}
				// for (int k = 0; k < listOfEntities.get(i).followers.size();
				// k++) {
				// if (userId
				// .compareTo(listOfEntities.get(i).followers.get(k).username)
				// != 0) {
				// switch (listOfEntities.get(i).organization.privacyLevel) {
				// case 0:
				// listOfEnts.remove(listOfEntities.get(i));
				// break;
				// default:
				// break;
				// }
				// }
				// }
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
	 */
	public static List<Idea> searchForIdea(String keyword) {
		String userId = Security.connected();
		User u = Security.getConnected();
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
				List<User> enrolledUsers = Users.getEnrolledUsers(listOfIdeas
						.get(i).belongsToTopic.entity.organization);
				if (!listOfIdeas.get(i).belongsToTopic.followers.contains(u)) {
					switch (listOfIdeas.get(i).belongsToTopic.entity.organization.privacyLevel) {
					case 0:
						listOfIdss.remove(listOfIdeas.get(i));
						break;
					default:
						break;
					}
				}
				// for (int k = 0; k < enrolledUsers.size(); k++) {
				// if (userId.compareTo(enrolledUsers.get(k).username) != 0) {
				// switch
				// (listOfIdeas.get(i).belongsToTopic.entity.organization.privacyLevel)
				// {
				// case 0:
				// listOfIdss.remove(listOfIdeas.get(i));
				// break;
				// default:
				// break;
				// }
				// }
				// }

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
	 */
	public static List<Topic> searchForTopic(String keyword) {
		String userId = Security.connected();
		User u = Security.getConnected();
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
				if (!listOfTopics.get(i).followers.contains(u)) {
					switch (listOfTopics.get(i).privacyLevel) {
					case 0:
						listOfTopis.remove(listOfTopics.get(i));
						break;
					default:
						break;
					}
				}
				// for (int k = 0; k < listOfTopics.get(i).followers.size();
				// k++) {
				// if (userId
				// .compareTo(listOfTopics.get(i).followers.get(k).username) !=
				// 0) {
				// switch (listOfTopics.get(i).privacyLevel) {
				// case 0:
				// listOfTopis.remove(listOfTopics.get(i));
				// break;
				// default:
				// break;
				// }
				// }
				// }

			}
		}
		return listOfTopis;
	}

	//
	// // public static void searchResult() {
	// // String connected = Security.connected();
	// // render(listOfResults,connected);
	// // }
	// /**
	// *
	// * @author Loaay Alkherbawy
	// *
	// * @story C4S01 Searching for organizations using a given keyword
	// *
	// * this method renders the searchResult view
	// *
	// */
	//
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
	/**
	 * method handelingOrAnd decides which list is to be passed for the filter
	 * method if the choice is and so we will need to filter on the filtered
	 * list but if or then we filter on the main list
	 * 
	 * @author Monica Yousry
	 * 
	 * @story C4S03 filtering results after sort
	 * 
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
	 * @return void.
	 * 
	 */

	public static void filterSearchResults(String filterOn) {
		filterResult = new ArrayList<Model>();
		if (filterOn.equalsIgnoreCase("o")) {// filtering on organizations
			for (int i = 0; i < tobepassed.size(); i++) {// loop on the whole
				// search result
				if (tobepassed.get(i) instanceof Organization) {// if found an
					// organization
					filterResult.add(tobepassed.get(i));// add it to the list
				}
			}
		}

		// similar to the previous part

		if (filterOn.equalsIgnoreCase("i")) {// filtering on ideas
			for (int i = 0; i < tobepassed.size(); i++) {
				if (tobepassed.get(i) instanceof Idea) {
					filterResult.add(tobepassed.get(i));
				}
			}
		}

		if (filterOn.equalsIgnoreCase("t")) { // filter on topic
			for (int i = 0; i < tobepassed.size(); i++) {
				if (tobepassed.get(i) instanceof Topic) {
					filterResult.add(tobepassed.get(i));
				}
			}
		}
		if (filterOn.equalsIgnoreCase("e")) { // filter on entity
			for (int i = 0; i < tobepassed.size(); i++) {
				if (tobepassed.get(i) instanceof MainEntity) {

					filterResult.add(tobepassed.get(i));
				}
			}
		}

		if (filterOn.equalsIgnoreCase("p")) { // filter on plan
			for (int i = 0; i < tobepassed.size(); i++) {
				if (tobepassed.get(i) instanceof Plan) {
					filterResult.add(tobepassed.get(i));
				}
			}
		}

		if (filterOn.equalsIgnoreCase("a")) {
			filterResult = tobepassed;
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
	 * 
	 * @param input
	 *            :: "String";input is to be taken from the user from the form
	 *            in the filter options view
	 * @return void.
	 * 
	 */
	public static void filterSearchResults(String filterOn, String input) {

		filterResult = new ArrayList<Model>();

		if (filterOn.equalsIgnoreCase("name")
				|| filterOn.equalsIgnoreCase("title")) {
			for (int i = 0; i < tobepassed.size(); i++) {

				if (tobepassed.get(i) instanceof models.Idea) {
					models.Idea temp = (models.Idea) tobepassed.get(i);
					if (temp.title.equalsIgnoreCase(input)) {
						filterResult.add(tobepassed.get(i));
					}

				}

				if (tobepassed.get(i) instanceof models.Plan) {
					models.Plan temp = (models.Plan) tobepassed.get(i);
					if (temp.title.equalsIgnoreCase(input)) {
						filterResult.add(tobepassed.get(i));
					}

				}

				if (tobepassed.get(i) instanceof models.Organization) {
					models.Organization temp = (models.Organization) tobepassed
							.get(i);
					if (temp.name.equalsIgnoreCase(input)) {
						filterResult.add(tobepassed.get(i));
					}

				}

				if (tobepassed.get(i) instanceof models.Topic) {
					models.Topic temp = (models.Topic) tobepassed.get(i);
					if (temp.title.equalsIgnoreCase(input)) {
						filterResult.add(tobepassed.get(i));
					}

				}

				if (tobepassed.get(i) instanceof models.MainEntity) {
					models.MainEntity temp = (models.MainEntity) tobepassed
							.get(i);
					if (temp.name.equalsIgnoreCase(input)) {
						filterResult.add(tobepassed.get(i));
					}

				}

			}
		}

		if (filterOn.equalsIgnoreCase("description")) {
			for (int i = 0; i < tobepassed.size(); i++) {

				if (tobepassed.get(i) instanceof models.Idea) {
					models.Idea temp = (models.Idea) tobepassed.get(i);
					if (temp.description.contains(input)) {
						filterResult.add(tobepassed.get(i));
					}

				}

				if (tobepassed.get(i) instanceof models.Plan) {
					models.Plan temp = (models.Plan) tobepassed.get(i);
					if (temp.description.contains(input)) {
						filterResult.add(tobepassed.get(i));
					}

				}

				if (tobepassed.get(i) instanceof models.Topic) {
					models.Topic temp = (models.Topic) tobepassed.get(i);
					if (temp.description.contains(input)) {
						filterResult.add(tobepassed.get(i));
					}

				}

				if (tobepassed.get(i) instanceof models.MainEntity) {
					models.MainEntity temp = (models.MainEntity) tobepassed
							.get(i);
					if (temp.description.contains(input)) {
						filterResult.add(tobepassed.get(i));
					}

				}

			}
		}

	}

	/**
	 * 
	 * sortA method sorts according to votes or rates (known from input) in
	 * ascending order
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
	 * 
	 * @return void
	 * 
	 * 
	 */

	public static void sortA(char voteOrRate) {// ascending
		List<Model> tosort = new ArrayList<Model>();
		List<Model> nottosort = new ArrayList<Model>();

		System.out.println("2ooo2oooiiii 2ooo 2aaa2aa 2na hena f sort A");
		if (voteOrRate == 'r' || voteOrRate == 'R') { // sorting by rate

			/*
			 * a for loop to find all objects having attribute rate and add them
			 * to a list called to sort and the rest of the search results are
			 * to be in a list not to be sorted
			 */

			for (int i = 0; i < listOfResults.size(); i++) {
				if (listOfResults.get(i) instanceof Idea
						|| listOfResults.get(i) instanceof Plan) {
					tosort.add(listOfResults.get(i));
				} else {
					nottosort.add(listOfResults.get(i));
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

						Model temp = (Model) tosort.get(k);
						tosort.set(k, tosort.get(j));
						tosort.set(j, temp);

					}

				}

			}

			sorted = new ArrayList<Model>();// final sorted list
			for (int m = 0; m < tosort.size(); m++) {
				sorted.add(tosort.get((m)));
			}
			for (int m = 0; m < nottosort.size(); m++) {
				sorted.add(nottosort.get((m)));
			}
			listOfResults = sorted;
			searchResult();
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
		 * m=0;m<tosort.size();m++){ sorted.add(tosort.get((e))); } for (int
		 * m=0;m<nottosort.size();m++){ sorted.add(nottosort.get((e))); } //the
		 * previous too loops is to append to lists in one }
		 */

	}

	/**
	 * 
	 * sortD method sorts according to votes or rates (known from input) in
	 * descending order
	 * 
	 * task.
	 * 
	 * 
	 * @author Monica Yousry
	 * 
	 * @story C4S04 sort descending the search result according to votes or
	 *        rates
	 * 
	 * @param voteOrRate
	 * 
	 *            "char": to decide whether to sort by vote or rate .
	 * 
	 * 
	 * @return void
	 * 
	 * 
	 */

	public static void sortD(char voteOrRate) {// descending

		List<Model> tosort = new ArrayList<Model>();

		List<Model> nottosort = new ArrayList<Model>();

		System.out.print("in sortD");

		if (voteOrRate == 'r' || voteOrRate == 'R') { // sorting by rate

			/*
			 * a for loop to find all objects having attribute rate and add them
			 * to a list called to sort and the rest of the search results are
			 * to be in a list not to be sorted
			 */

			for (int i = 0; i < listOfResults.size(); i++) {
				if (listOfResults.get(i) instanceof Idea
						|| listOfResults.get(i) instanceof Plan) {
					tosort.add(listOfResults.get(i));
				} else {
					nottosort.add(listOfResults.get(i));
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

						Model temp = (Model) tosort.get(k);
						tosort.set(k, tosort.get(j));
						tosort.set(j, temp);

					}

				}

			}

			sorted = new ArrayList<Model>();// final sorted list
			for (int m = 0; m < tosort.size(); m++) {
				sorted.add(tosort.get((m)));
			}
			for (int m = 0; m < nottosort.size(); m++) {
				sorted.add(nottosort.get((m)));
			}

			listOfResults = sorted;
			searchResult();
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
		 * m=0;m<tosort.size();m++){ sorted.add(tosort.get((e))); } for (int
		 * m=0;m<nottosort.size();m++){ sorted.add(nottosort.get((e))); } //the
		 * previous too loops is to append to lists in one }
		 */
	}

}