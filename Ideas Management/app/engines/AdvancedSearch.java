package engines;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Comment;
import models.Idea;
import models.Item;
import models.MainEntity;
import models.Organization;
import models.Plan;
import models.Tag;
import models.Topic;

import play.db.jpa.Model;

/**
 * 
 * @author Mohamed Ghanem
 * 
 */

public class AdvancedSearch {

	/**
	 * 
	 * @author Mohamed Ghanem
	 * 
	 * @story C4S02 advanced search
	 * 
	 * @Description this method search for Organizations required from list tOrg
	 *              and using keyword key and constrains exKey, in, or.
	 * 
	 * @param listOfResults
	 *            'List<Model>'; is a memory reference for list contains the
	 *            current search result to add or remove the required search
	 *            results in/from.
	 * 
	 * @param tOrgs
	 *            'List<Model>'; is the Organization list that the result could
	 *            be taken to listOfResult from.
	 * 
	 * @param key
	 *            'String'; the search key word.
	 * 
	 * @param exKey
	 *            'boolean'; that used to determined that the key is an exact
	 *            key or not.
	 * 
	 * @param in
	 *            'int'; that used to determined the type to search in( 0 for
	 *            All, 1 for Title, 2 for Description, 3 for Comment, 4 for
	 *            Tags.
	 * 
	 * @param or
	 *            'boolean'; that used to determined that the key is an or key
	 *            or not.
	 * 
	 */
	public static void searchingOrganization(List<Model> listOfResults,
			List<Model> tOrgs, String key, boolean exKey, int in, boolean or) {
		if (or) {
			if (exKey) {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Organization) {
							if (((Organization) tOrgs.get(i)).name
									.equalsIgnoreCase(key)) {
								listOfResults.add(tOrgs.get(i));
								tOrgs.remove(i);
								i--;
							} else {
								if (((Organization) tOrgs.get(i)).description
										.equalsIgnoreCase(key)) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								} else {
									if (in == 1) {
										// comments
									} else {
										List<Tag> x = ((Organization) tOrgs
												.get(i)).relatedTags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.equalsIgnoreCase(key)) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Organization) {
								if (((Organization) tOrgs.get(i)).name
										.equalsIgnoreCase(key)) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Organization) {
									if (((Organization) tOrgs.get(i)).description
											.equalsIgnoreCase(key)) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								// comments
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Organization) {
										List<Tag> x = ((Organization) tOrgs
												.get(i)).relatedTags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.equalsIgnoreCase(key)) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Organization) {
							if (((Organization) tOrgs.get(i)).name
									.toLowerCase().contains(key.toLowerCase())) {
								listOfResults.add(tOrgs.get(i));
								tOrgs.remove(i);
								i--;
							} else {
								if (((Organization) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								} else {
									if (in == 1) {
										// comments
									} else {
										List<Tag> x = ((Organization) tOrgs
												.get(i)).relatedTags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Organization) {
								if (((Organization) tOrgs.get(i)).name
										.toLowerCase().contains(
												key.toLowerCase())) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Organization) {
									if (((Organization) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								// comments
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Organization) {
										List<Tag> x = ((Organization) tOrgs
												.get(i)).relatedTags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} else {
			if (exKey) {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Organization) {
							if (((Organization) tOrgs.get(i)).name
									.toLowerCase().contains(key.toLowerCase())) {
								tOrgs.remove(i);
								i--;
							} else {
								if (((Organization) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								} else {
									if (in == 1) {
										// comments
									} else {
										List<Tag> x = ((Organization) tOrgs
												.get(i)).relatedTags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Organization) {
								if (((Organization) tOrgs.get(i)).name
										.toLowerCase().contains(
												key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Organization) {
									if (((Organization) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								// comments
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Organization) {
										List<Tag> x = ((Organization) tOrgs
												.get(i)).relatedTags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Organization) {
							if (((Organization) tOrgs.get(i)).name
									.toLowerCase().contains(key.toLowerCase())) {
								// tOrgs.remove(i);
								// i--;
							} else {
								if (((Organization) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									// tOrgs.remove(i);
									// i--;
								} else {
									if (in == 1) {
										// comments
									} else {
										boolean b = false;
										List<Tag> x = ((Organization) tOrgs
												.get(i)).relatedTags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												b = true;
												break;
											}
										}
										if (b) {
											// tOrgs.remove(i);
											// i--;
										} else {
											tOrgs.remove(i);
											i--;
										}
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Organization) {
								if (!((Organization) tOrgs.get(i)).name
										.toLowerCase().contains(
												key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Organization) {
									if (!((Organization) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								// comments
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Organization) {
										boolean b = false;
										List<Tag> x = ((Organization) tOrgs
												.get(i)).relatedTags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												b = true;
												break;
											}
										}
										if (b) {
											tOrgs.remove(i);
											i--;
										}
									}
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
	 * @author Mohamed Ghanem
	 * 
	 * @story C4S02 advanced search
	 * 
	 * @Description this method search for MainEntity required from list tOrg
	 *              and using keyword key and constrains exKey, in, or.
	 * 
	 * @param listOfResults
	 *            'List<Model>'; is a memory reference for list contains the
	 *            current search result to add or remove the required search
	 *            results in/from.
	 * 
	 * @param tOrgs
	 *            'List<Model>'; is the MainEntity list that the result could be
	 *            taken to listOfResult from.
	 * 
	 * @param key
	 *            'String'; the search key word.
	 * 
	 * @param exKey
	 *            'boolean'; that used to determined that the key is an exact
	 *            key or not.
	 * 
	 * @param in
	 *            'int'; that used to determined the type to search in( 0 for
	 *            All, 1 for Title, 2 for Description, 3 for Comment, 4 for
	 *            Tags.
	 * 
	 * @param or
	 *            'boolean'; that used to determined that the key is an or key
	 *            or not.
	 * 
	 */
	public static void searchingMainEntity(List<Model> listOfResults,
			List<Model> tOrgs, String key, boolean exKey, int in, boolean or) {
		if (or) {
			if (exKey) {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof MainEntity) {
							if (((MainEntity) tOrgs.get(i)).name
									.equalsIgnoreCase(key)) {
								listOfResults.add(tOrgs.get(i));
								tOrgs.remove(i);
								i--;
							} else {
								if (((MainEntity) tOrgs.get(i)).description
										.equalsIgnoreCase(key)) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								} else {
									if (in == 1) {
										// comments
									} else {
										List<Tag> x = ((MainEntity) tOrgs
												.get(i)).tagList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.equalsIgnoreCase(key)) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof MainEntity) {
								if (((MainEntity) tOrgs.get(i)).name
										.equalsIgnoreCase(key)) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof MainEntity) {
									if (((MainEntity) tOrgs.get(i)).description
											.equalsIgnoreCase(key)) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								// comments
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof MainEntity) {
										List<Tag> x = ((MainEntity) tOrgs
												.get(i)).tagList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.equalsIgnoreCase(key)) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof MainEntity) {
							if (((MainEntity) tOrgs.get(i)).name.toLowerCase()
									.contains(key.toLowerCase())) {
								listOfResults.add(tOrgs.get(i));
								tOrgs.remove(i);
								i--;
							} else {
								if (((MainEntity) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								} else {
									if (in == 1) {
										// comments
									} else {
										List<Tag> x = ((MainEntity) tOrgs
												.get(i)).tagList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof MainEntity) {
								if (((MainEntity) tOrgs.get(i)).name
										.toLowerCase().contains(
												key.toLowerCase())) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof MainEntity) {
									if (((MainEntity) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								// comments
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof MainEntity) {
										List<Tag> x = ((MainEntity) tOrgs
												.get(i)).tagList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} else {
			if (exKey) {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof MainEntity) {
							if (((MainEntity) tOrgs.get(i)).name.toLowerCase()
									.contains(key.toLowerCase())) {
								tOrgs.remove(i);
								i--;
							} else {
								if (((MainEntity) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								} else {
									if (in == 1) {
										// comments
									} else {
										List<Tag> x = ((MainEntity) tOrgs
												.get(i)).tagList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof MainEntity) {
								if (((MainEntity) tOrgs.get(i)).name
										.toLowerCase().contains(
												key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof MainEntity) {
									if (((MainEntity) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								// comments
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof MainEntity) {
										List<Tag> x = ((MainEntity) tOrgs
												.get(i)).tagList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof MainEntity) {
							if (((MainEntity) tOrgs.get(i)).name.toLowerCase()
									.contains(key.toLowerCase())) {
								// tOrgs.remove(i);
								// i--;
							} else {
								if (((MainEntity) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									// tOrgs.remove(i);
									// i--;
								} else {
									if (in == 1) {
										// comments
									} else {
										boolean b = false;
										List<Tag> x = ((MainEntity) tOrgs
												.get(i)).tagList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												b = true;
												break;
											}
										}
										if (b) {
											// tOrgs.remove(i);
											// i--;
										} else {
											tOrgs.remove(i);
											i--;
										}
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof MainEntity) {
								if (!((MainEntity) tOrgs.get(i)).name
										.toLowerCase().contains(
												key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof MainEntity) {
									if (!((MainEntity) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								// comments
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof MainEntity) {
										boolean b = false;
										List<Tag> x = ((MainEntity) tOrgs
												.get(i)).tagList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												b = true;
												break;
											}
										}
										if (b) {
											tOrgs.remove(i);
											i--;
										}
									}
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
	 * @author Mohamed Ghanem
	 * 
	 * @story C4S02 advanced search
	 * 
	 * @Description this method search for Topic required from list tOrg and
	 *              using keyword key and constrains exKey, in, or.
	 * 
	 * @param listOfResults
	 *            'List<Model>'; is a memory reference for list contains the
	 *            current search result to add or remove the required search
	 *            results in/from.
	 * 
	 * @param tOrgs
	 *            'List<Model>'; is the Topic list that the result could be
	 *            taken to listOfResult from.
	 * 
	 * @param key
	 *            'String'; the search key word.
	 * 
	 * @param exKey
	 *            'boolean'; that used to determined that the key is an exact
	 *            key or not.
	 * 
	 * @param in
	 *            'int'; that used to determined the type to search in( 0 for
	 *            All, 1 for Title, 2 for Description, 3 for Comment, 4 for
	 *            Tags.
	 * 
	 * @param or
	 *            'boolean'; that used to determined that the key is an or key
	 *            or not.
	 * 
	 */
	public static void searchingTopic(List<Model> listOfResults,
			List<Model> tOrgs, String key, boolean exKey, int in, boolean or) {
		if (or) {
			if (exKey) {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Topic) {
							if (((Topic) tOrgs.get(i)).title
									.equalsIgnoreCase(key)) {
								listOfResults.add(tOrgs.get(i));
								tOrgs.remove(i);
								i--;
							} else {
								if (((Topic) tOrgs.get(i)).description
										.equalsIgnoreCase(key)) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								} else {
									boolean b = true;
									List<Comment> x = ((Topic) tOrgs.get(i)).commentsOn;
									for (int j = 0; j < x.size(); j++) {
										if (x.get(j).comment
												.equalsIgnoreCase(key)) {
											listOfResults.add(tOrgs.get(i));
											tOrgs.remove(i);
											i--;
											b = false;
											break;
										}
									}
									if (b) {
										List<Tag> y = ((Topic) tOrgs.get(i)).tags;
										for (int j = 0; j < y.size(); j++) {
											if (y.get(j).name
													.equalsIgnoreCase(key)) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Topic) {
								if (((Topic) tOrgs.get(i)).title
										.equalsIgnoreCase(key)) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Topic) {
									if (((Topic) tOrgs.get(i)).description
											.equalsIgnoreCase(key)) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Topic) {
										List<Comment> x = ((Topic) tOrgs.get(i)).commentsOn;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).comment
													.equalsIgnoreCase(key)) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Topic) {
										List<Tag> x = ((Topic) tOrgs.get(i)).tags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.equalsIgnoreCase(key)) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Topic) {
							if (((Topic) tOrgs.get(i)).title.toLowerCase()
									.contains(key.toLowerCase())) {
								listOfResults.add(tOrgs.get(i));
								tOrgs.remove(i);
								i--;
							} else {
								if (((Topic) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								} else {
									boolean b = true;
									List<Comment> x = ((Topic) tOrgs.get(i)).commentsOn;
									for (int j = 0; j < x.size(); j++) {
										if (x.get(j).comment.toLowerCase()
												.contains(key.toLowerCase())) {
											listOfResults.add(tOrgs.get(i));
											tOrgs.remove(i);
											b = false;
											i--;
											break;
										}
									}
									if (b) {
										List<Tag> y = ((Topic) tOrgs.get(i)).tags;
										for (int j = 0; j < y.size(); j++) {
											if (y.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}

								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Topic) {
								if (((Topic) tOrgs.get(i)).title.toLowerCase()
										.contains(key.toLowerCase())) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Topic) {
									if (((Topic) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								for (int i = 0; i < tOrgs.size(); i++) {
									List<Comment> x = ((Topic) tOrgs.get(i)).commentsOn;
									for (int j = 0; j < x.size(); j++) {
										if (x.get(j).comment.toLowerCase()
												.contains(key.toLowerCase())) {
											listOfResults.add(tOrgs.get(i));
											tOrgs.remove(i);
											i--;
											break;
										}
									}
								}
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Topic) {
										List<Tag> x = ((Topic) tOrgs.get(i)).tags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} else {
			if (exKey) {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Topic) {
							if (((Topic) tOrgs.get(i)).title.toLowerCase()
									.contains(key.toLowerCase())) {
								tOrgs.remove(i);
								i--;
							} else {
								if (((Topic) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								} else {
									boolean b = true;
									List<Comment> x = ((Topic) tOrgs.get(i)).commentsOn;
									for (int j = 0; j < x.size(); j++) {
										if (x.get(j).comment.toLowerCase()
												.contains(key.toLowerCase())) {
											tOrgs.remove(i);
											i--;
											b = false;
											break;
										}
									}
									if (b) {
										List<Tag> y = ((Topic) tOrgs.get(i)).tags;
										for (int j = 0; j < y.size(); j++) {
											if (y.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Topic) {
								if (((Topic) tOrgs.get(i)).title.toLowerCase()
										.contains(key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Topic) {
									if (((Topic) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Topic) {
										List<Comment> x = ((Topic) tOrgs.get(i)).commentsOn;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).comment
													.toLowerCase()
													.contains(key.toLowerCase())) {
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Topic) {
										List<Tag> x = ((Topic) tOrgs.get(i)).tags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Topic) {
							if (((Topic) tOrgs.get(i)).title.toLowerCase()
									.contains(key.toLowerCase())) {
								// tOrgs.remove(i);
								// i--;
							} else {
								if (((Topic) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									// tOrgs.remove(i);
									// i--;
								} else {
									if (in == 1) {
										// comments
									} else {
										boolean b = false;
										List<Tag> x = ((Topic) tOrgs.get(i)).tags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												b = true;
												break;
											}
										}
										if (b) {
											// tOrgs.remove(i);
											// i--;
										} else {
											tOrgs.remove(i);
											i--;
										}
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Topic) {
								if (!((Topic) tOrgs.get(i)).title.toLowerCase()
										.contains(key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Topic) {
									if (!((Topic) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Topic) {
										boolean b = false;
										List<Comment> x = ((Topic) tOrgs.get(i)).commentsOn;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).comment
													.toLowerCase()
													.contains(key.toLowerCase())) {
												b = true;
												break;
											}
										}
										if (b) {
											tOrgs.remove(i);
											i--;
										}
									}
								}
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Topic) {
										boolean b = false;
										List<Tag> x = ((Topic) tOrgs.get(i)).tags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												b = true;
												break;
											}
										}
										if (b) {
											tOrgs.remove(i);
											i--;
										}
									}
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
	 * @author Mohamed Ghanem
	 * 
	 * @story C4S02 advanced search
	 * 
	 * @Description this method search for Idea required from list tOrg and
	 *              using keyword key and constrains exKey, in, or.
	 * 
	 * @param listOfResults
	 *            'List<Model>'; is a memory reference for list contains the
	 *            current search result to add or remove the required search
	 *            results in/from.
	 * 
	 * @param tOrgs
	 *            'List<Model>'; is the Idea list that the result could be taken
	 *            to listOfResult from.
	 * 
	 * @param key
	 *            'String'; the search key word.
	 * 
	 * @param exKey
	 *            'boolean'; that used to determined that the key is an exact
	 *            key or not.
	 * 
	 * @param in
	 *            'int'; that used to determined the type to search in( 0 for
	 *            All, 1 for Title, 2 for Description, 3 for Comment, 4 for
	 *            Tags.
	 * 
	 * @param or
	 *            'boolean'; that used to determined that the key is an or key
	 *            or not.
	 * 
	 */
	public static void searchingIdea(List<Model> listOfResults,
			List<Model> tOrgs, String key, boolean exKey, int in, boolean or) {
		if (or) {
			if (exKey) {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Idea) {
							if (((Idea) tOrgs.get(i)).title.equals(key)) {
								listOfResults.add(tOrgs.get(i));
								tOrgs.remove(i);
								i--;
							} else {
								if (((Idea) tOrgs.get(i)).description
										.equals(key)) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								} else {
									boolean b = true;
									List<Comment> y = ((Idea) tOrgs.get(i)).commentsList;
									for (int j = 0; j < y.size(); j++) {
										if (y.get(j).comment.equals(key)) {
											listOfResults.add(tOrgs.get(i));
											tOrgs.remove(i);
											i--;
											b = false;
											break;
										}
									}
									if (b) {
										List<Tag> x = ((Idea) tOrgs.get(i)).tagsList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name.equals(key)) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Idea) {
								if (((Idea) tOrgs.get(i)).title.equals(key)) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Idea) {
									if (((Idea) tOrgs.get(i)).description
											.equals(key)) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Idea) {
										List<Comment> x = ((Idea) tOrgs.get(i)).commentsList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).comment.equals(key)) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Idea) {
										List<Tag> x = ((Idea) tOrgs.get(i)).tagsList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name.equals(key)) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Idea) {
							if (((Idea) tOrgs.get(i)).title.toLowerCase()
									.contains(key.toLowerCase())) {
								listOfResults.add(tOrgs.get(i));
								tOrgs.remove(i);
								i--;
							} else {
								if (((Idea) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								} else {
									List<Tag> x = ((Idea) tOrgs.get(i)).tagsList;
									for (int j = 0; j < x.size(); j++) {
										if (x.get(j).name.toLowerCase()
												.contains(key.toLowerCase())) {
											listOfResults.add(tOrgs.get(i));
											tOrgs.remove(i);
											i--;
											break;
										}
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Idea) {
								if (((Idea) tOrgs.get(i)).title.toLowerCase()
										.contains(key.toLowerCase())) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Idea) {
									if (((Idea) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Idea) {
										List<Comment> x = ((Idea) tOrgs.get(i)).commentsList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).comment
													.toLowerCase()
													.contains(key.toLowerCase())) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Idea) {
										List<Tag> x = ((Idea) tOrgs.get(i)).tagsList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} else {
			if (exKey) {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Idea) {
							if (((Idea) tOrgs.get(i)).title.toLowerCase()
									.contains(key.toLowerCase())) {
								tOrgs.remove(i);
								i--;
							} else {
								if (((Idea) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
								List<Tag> x = ((Idea) tOrgs.get(i)).tagsList;
								for (int j = 0; j < x.size(); j++) {
									if (x.get(j).name.toLowerCase().contains(
											key.toLowerCase())) {
										tOrgs.remove(i);
										i--;
										break;
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Idea) {
								if (((Idea) tOrgs.get(i)).title.toLowerCase()
										.contains(key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Idea) {
									if (((Idea) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Idea) {
										List<Comment> x = ((Idea) tOrgs.get(i)).commentsList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).comment
													.toLowerCase()
													.contains(key.toLowerCase())) {
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Idea) {

										List<Tag> x = ((Idea) tOrgs.get(i)).tagsList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Idea) {
							if (!((Idea) tOrgs.get(i)).title.toLowerCase()
									.contains(key.toLowerCase())) {
								tOrgs.remove(i);
								i--;
							} else {
								if (!((Idea) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}

								boolean b = false;

								List<Comment> y = ((Idea) tOrgs.get(i)).commentsList;

								for (int k = 0; k < y.size(); k++) {
									if (y.get(k).comment.toLowerCase()
											.contains(key.toLowerCase())) {
										b = true;
										break;
									}
								}
								if (!b) {
									List<Tag> x = ((Idea) tOrgs.get(i)).tagsList;

									for (int j = 0; j < x.size(); j++) {
										if (x.get(j).name.toLowerCase()
												.contains(key.toLowerCase())) {
											b = true;
											break;
										}
									}
								}
								if (b) {
									tOrgs.remove(i);
									i--;
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Idea) {
								if (!((Idea) tOrgs.get(i)).title.toLowerCase()
										.contains(key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Idea) {
									if (!((Idea) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Idea) {
										boolean b = false;
										List<Comment> x = ((Idea) tOrgs.get(i)).commentsList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).comment
													.toLowerCase()
													.contains(key.toLowerCase())) {
												b = true;
												break;
											}
										}
										if (b) {
											tOrgs.remove(i);
											i--;
										}
									}
								}
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Idea) {
										boolean b = false;
										List<Tag> x = ((Idea) tOrgs.get(i)).tagsList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												b = true;
												break;
											}
										}
										if (b) {
											tOrgs.remove(i);
											i--;
										}
									}
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
	 * @author Mohamed Ghanem
	 * 
	 * @story C4S02 advanced search
	 * 
	 * @Description this method search for Item required from list tOrg and
	 *              using keyword key and constrains exKey, in, or.
	 * 
	 * @param listOfResults
	 *            'List<Model>'; is a memory reference for list contains the
	 *            current search result to add or remove the required search
	 *            results in/from.
	 * 
	 * @param tOrgs
	 *            'List<Model>'; is the Item list that the result could be taken
	 *            to listOfResult from.
	 * 
	 * @param key
	 *            'String'; the search key word.
	 * 
	 * @param exKey
	 *            'boolean'; that used to determined that the key is an exact
	 *            key or not.
	 * 
	 * @param in
	 *            'int'; that used to determined the type to search in( 0 for
	 *            All, 1 for Title, 2 for Description, 3 for Comment, 4 for
	 *            Tags.
	 * 
	 * @param or
	 *            'boolean'; that used to determined that the key is an or key
	 *            or not.
	 * 
	 */
	public static void searchingItem(List<Model> listOfResults,
			List<Model> tOrgs, String key, boolean exKey, int in, boolean or) {
		if (or) {
			if (exKey) {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Item) {
							if (((Item) tOrgs.get(i)).summary
									.equalsIgnoreCase(key)) {
								listOfResults.add(tOrgs.get(i));
								tOrgs.remove(i);
								i--;
							} else {
								if (((Item) tOrgs.get(i)).description
										.equalsIgnoreCase(key)) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								} else {
									if (in == 1) {
										// comments
									} else {
										List<Tag> x = ((Item) tOrgs.get(i)).tags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.equalsIgnoreCase(key)) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Item) {
								if (((Item) tOrgs.get(i)).summary
										.equalsIgnoreCase(key)) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Item) {
									if (((Item) tOrgs.get(i)).description
											.equalsIgnoreCase(key)) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								// comments
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Item) {
										List<Tag> x = ((Item) tOrgs.get(i)).tags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.equalsIgnoreCase(key)) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Item) {
							if (((Item) tOrgs.get(i)).summary.toLowerCase()
									.contains(key.toLowerCase())) {
								listOfResults.add(tOrgs.get(i));
								tOrgs.remove(i);
								i--;
							} else {
								if (((Item) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								} else {
									if (in == 1) {
										// comments
									} else {
										List<Tag> x = ((Item) tOrgs.get(i)).tags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Item) {
								if (((Item) tOrgs.get(i)).summary.toLowerCase()
										.contains(key.toLowerCase())) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Item) {
									if (((Item) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								// comments
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Item) {
										List<Tag> x = ((Item) tOrgs.get(i)).tags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												listOfResults.add(tOrgs.get(i));
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} else {
			if (exKey) {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Item) {
							if (((Item) tOrgs.get(i)).summary.toLowerCase()
									.contains(key.toLowerCase())) {
								tOrgs.remove(i);
								i--;
							} else {
								if (((Item) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								} else {
									if (in == 1) {
										// comments
									} else {
										List<Tag> x = ((Item) tOrgs.get(i)).tags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Item) {
								if (((Item) tOrgs.get(i)).summary.toLowerCase()
										.contains(key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Item) {
									if (((Item) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								// comments
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Item) {
										List<Tag> x = ((Item) tOrgs.get(i)).tags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												tOrgs.remove(i);
												i--;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Item) {
							if (((Item) tOrgs.get(i)).summary.toLowerCase()
									.contains(key.toLowerCase())) {
								// tOrgs.remove(i);
								// i--;
							} else {
								if (((Item) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									// tOrgs.remove(i);
									// i--;
								} else {
									if (in == 1) {
										// comments
									} else {
										boolean b = false;
										List<Tag> x = ((Item) tOrgs.get(i)).tags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												b = true;
												break;
											}
										}
										if (b) {
											// tOrgs.remove(i);
											// i--;
										} else {
											tOrgs.remove(i);
											i--;
										}
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Item) {
								if (!((Item) tOrgs.get(i)).summary
										.toLowerCase().contains(
												key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Item) {
									if (!((Item) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								// comments
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Item) {
										boolean b = false;
										List<Tag> x = ((Item) tOrgs.get(i)).tags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												b = true;
												break;
											}
										}
										if (b) {
											tOrgs.remove(i);
											i--;
										}
									}
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
	 * @author Mohamed Ghanem
	 * 
	 * @story C4S02 advanced search
	 * 
	 * @Description this method search for Plan required from list tOrg and
	 *              using keyword key and constrains exKey, in, or.
	 * 
	 * @param listOfResults
	 *            'List<Model>'; is a memory reference for list contains the
	 *            current search result to add or remove the required search
	 *            results in/from.
	 * 
	 * @param tOrgs
	 *            'List<Model>'; is the Plan list that the result could be taken
	 *            to listOfResult from.
	 * 
	 * @param key
	 *            'String'; the search key word.
	 * 
	 * @param exKey
	 *            'boolean'; that used to determined that the key is an exact
	 *            key or not.
	 * 
	 * @param in
	 *            'int'; that used to determined the type to search in( 0 for
	 *            All, 1 for Title, 2 for Description, 3 for Comment, 4 for
	 *            Tags.
	 * 
	 * @param or
	 *            'boolean'; that used to determined that the key is an or key
	 *            or not.
	 * 
	 */
	public static void searchingPlan(List<Model> listOfResults,
			List<Model> tOrgs, String key, boolean exKey, int in, boolean or) {
		if (or) {
			if (exKey) {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Plan) {
							if (((Plan) tOrgs.get(i)).title
									.equalsIgnoreCase(key)) {
								listOfResults.add(tOrgs.get(i));
								tOrgs.remove(i);
								i--;
							} else {
								if (((Plan) tOrgs.get(i)).description
										.equalsIgnoreCase(key)) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								} else {
									if (in == 1) {
										// comments
									}// else { // Tags
										// List<Tag> x = ((Plan) tOrgs
									// .get(i)).tags;
									// for (int j = 0; j < x.size(); j++) {
									// if (x.get(j).name
									// .equalsIgnoreCase(key)) {
									// listOfResults.add(tOrgs.get(i));
									// tOrgs.remove(i);
									// i--;
									// break;
									// }
									// }
									// }
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Plan) {
								if (((Plan) tOrgs.get(i)).title
										.equalsIgnoreCase(key)) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Plan) {
									if (((Plan) tOrgs.get(i)).description
											.equalsIgnoreCase(key)) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								// comments
							} else { // tags
								// for (int i = 0; i < tOrgs.size(); i++) {
								// if (tOrgs.get(i) instanceof Plan) {
								// List<Tag> x = ((Plan) tOrgs
								// .get(i)).tags;
								// for (int j = 0; j < x.size(); j++) {
								// if (x.get(j).name
								// .equalsIgnoreCase(key)) {
								// listOfResults.add(tOrgs.get(i));
								// tOrgs.remove(i);
								// i--;
								// break;
								// }
								// }
								// }
								// }
							}
						}
					}
				}
			} else {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Plan) {
							if (((Plan) tOrgs.get(i)).title.toLowerCase()
									.contains(key.toLowerCase())) {
								listOfResults.add(tOrgs.get(i));
								tOrgs.remove(i);
								i--;
							} else {
								if (((Plan) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								} else {
									if (in == 1) {
										// comments
									} else { // tags
										// List<Tag> x = ((Plan) tOrgs
										// .get(i)).tags;
										// for (int j = 0; j < x.size(); j++) {
										// if (x.get(j).name
										// .toLowerCase()
										// .contains(key.toLowerCase())) {
										// listOfResults.add(tOrgs.get(i));
										// tOrgs.remove(i);
										// i--;
										// break;
										// }
										// }
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Plan) {
								if (((Plan) tOrgs.get(i)).title.toLowerCase()
										.contains(key.toLowerCase())) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Plan) {
									if (((Plan) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								// comments
							} else { // tags
								// for (int i = 0; i < tOrgs.size(); i++) {
								// if (tOrgs.get(i) instanceof Plan) {
								// List<Tag> x = ((Plan) tOrgs
								// .get(i)).tags;
								// for (int j = 0; j < x.size(); j++) {
								// if (x.get(j).name
								// .toLowerCase()
								// .contains(key.toLowerCase())) {
								// listOfResults.add(tOrgs.get(i));
								// tOrgs.remove(i);
								// i--;
								// break;
								// }
								// }
								// }
								// }
							}
						}
					}
				}
			}
		} else {
			if (exKey) {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Plan) {
							if (((Plan) tOrgs.get(i)).title.toLowerCase()
									.contains(key.toLowerCase())) {
								tOrgs.remove(i);
								i--;
							} else {
								if (((Plan) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								} else {
									if (in == 1) {
										// comments
									} else { // tags
										// List<Tag> x = ((Plan) tOrgs
										// .get(i)).tags;
										// for (int j = 0; j < x.size(); j++) {
										// if (x.get(j).name
										// .toLowerCase()
										// .contains(key.toLowerCase())) {
										// tOrgs.remove(i);
										// i--;
										// break;
										// }
										// }
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Plan) {
								if (((Plan) tOrgs.get(i)).title.toLowerCase()
										.contains(key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Plan) {
									if (((Plan) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								// comments
							} else { // tags
								// for (int i = 0; i < tOrgs.size(); i++) {
								// if (tOrgs.get(i) instanceof Plan) {
								// List<Tag> x = ((Plan) tOrgs
								// .get(i)).tags;
								// for (int j = 0; j < x.size(); j++) {
								// if (x.get(j).name
								// .toLowerCase()
								// .contains(key.toLowerCase())) {
								// tOrgs.remove(i);
								// i--;
								// break;
								// }
								// }
								// }
								// }
							}
						}
					}
				}
			} else {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Plan) {
							if (((Plan) tOrgs.get(i)).title.toLowerCase()
									.contains(key.toLowerCase())) {
								// tOrgs.remove(i);
								// i--;
							} else {
								if (((Plan) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									// tOrgs.remove(i);
									// i--;
								} else {
									if (in == 1) {
										// comments
									} else { // tags
										// boolean b = false;
										// List<Tag> x = ((Plan) tOrgs
										// .get(i)).tags;
										// for (int j = 0; j < x.size(); j++) {
										// if (x.get(j).name
										// .toLowerCase()
										// .contains(key.toLowerCase())) {
										// b = true;
										// break;
										// }
										// }
										// if (b) {
										// // tOrgs.remove(i);
										// // i--;
										// } else {
										// tOrgs.remove(i);
										// i--;
										// }
									}
								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Plan) {
								if (!((Plan) tOrgs.get(i)).title.toLowerCase()
										.contains(key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
							}
						}
					} else {
						if (in == 2) {
							for (int i = 0; i < tOrgs.size(); i++) {
								if (tOrgs.get(i) instanceof Plan) {
									if (!((Plan) tOrgs.get(i)).description
											.toLowerCase().contains(
													key.toLowerCase())) {
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
								// comments
							} else { // tags
								// for (int i = 0; i < tOrgs.size(); i++) {
								// if (tOrgs.get(i) instanceof Plan) {
								// boolean b = false;
								// List<Tag> x = ((Plan) tOrgs
								// .get(i)).tags;
								// for (int j = 0; j < x.size(); j++) {
								// if (x.get(j).name
								// .toLowerCase()
								// .contains(key.toLowerCase())) {
								// b = true;
								// break;
								// }
								// }
								// if (b) {
								// tOrgs.remove(i);
								// i--;
								// }
								// }
								// }
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @author Mohamed Ghanem
	 * 
	 * @story C4S02 advanced search
	 * 
	 * @Description this method search for Plan required from list tOrg and
	 *              using keyword key and constrains exKey, in, or.
	 * 
	 * @param t
	 *            'char'; character that determine
	 * 
	 * @param listOfResults
	 *            List<Model>; is the list to check weather the t character is
	 *            in or not.
	 * 
	 * @return 'boolean' true iff the listOfResult contains the type of char 't'
	 * 
	 */
	public static boolean checkType(char t, List<Model> listOfResults) {
		switch (t) {
		case 'o':
			for (int i = 0; i < listOfResults.size(); i++)
				if (listOfResults.get(i) instanceof Organization) {
					return true;
				}
			break;
		case 'e':
			for (int i = 0; i < listOfResults.size(); i++)
				if (listOfResults.get(i) instanceof MainEntity) {
					return true;
				}
			break;
		case 't':
			for (int i = 0; i < listOfResults.size(); i++)
				if (listOfResults.get(i) instanceof Topic) {
					return true;
				}
			break;
		case 'p':
			for (int i = 0; i < listOfResults.size(); i++)
				if (listOfResults.get(i) instanceof Plan) {
					return true;
				}
			break;
		case 'd':
			for (int i = 0; i < listOfResults.size(); i++)
				if (listOfResults.get(i) instanceof Idea) {
					return true;
				}
			break;
		case 'i':
			for (int i = 0; i < listOfResults.size(); i++)
				if (listOfResults.get(i) instanceof Item) {
					return true;
				}
			break;
		default:
			break;
		}
		return false;
	}

	/**
	 * @author Mohamed Ghanem
	 * 
	 * @story C4S02 advanced search
	 * 
	 * @Description this method solve the Query the user entered to search with
	 *              by finding the required Organizations to the result .
	 * 
	 * @param keyWords
	 *            'String[]' contains the key words needed
	 * 
	 * @param keyWordsIn
	 *            'String[]' contains the key word's type to search in
	 * 
	 * @param isExact
	 *            'boolean' determine if the keywords is/are exact or not.
	 * 
	 * @param listOfResults
	 *            'List<Model>' a reference for the listOfresults.
	 * 
	 * @param currentModels
	 *            'List<Model>' a list of current Models used to find the search
	 *            result in.
	 * 
	 */
	public static void solvingOrganizationQuery(String[] keyWords,
			String[] keyWordsIn, boolean isExact, List<Model> listOfResults,
			List<Model> currentModels) {
		// Or parts
		for (int i = 0; i < keyWords.length - 1; i += 2) {
			if (Integer.parseInt(keyWords[i]) == 2) {
				if (keyWords[i + 1].trim().compareTo("") != 0) {
					AdvancedSearch.searchingOrganization(listOfResults,
							currentModels, keyWords[i + 1], isExact,
							Integer.parseInt(keyWordsIn[i / 2]), true);
				}
			}
		}
		// And parts
		if (!isExact) {
			boolean fAnd = true;
			for (int i = 0; i < keyWords.length - 1; i += 2) {
				if (Integer.parseInt(keyWords[i]) == 1) {
					if (keyWords[i + 1].trim() != "") {
						if (fAnd && !checkType('o', listOfResults)) {
							AdvancedSearch.searchingOrganization(listOfResults,
									currentModels, keyWords[i + 1], false,
									Integer.parseInt(keyWordsIn[i / 2]), true);
							fAnd = false;
						} else {
							if (checkType('o', listOfResults)) {
								AdvancedSearch.searchingOrganization(
										listOfResults, listOfResults,
										keyWords[i + 1], false,
										Integer.parseInt(keyWordsIn[i / 2]),
										false);
							} else {
								break;
							}
						}
					}
				}
			}
		}

		// Not parts
		if (!isExact) {
			if (checkType('o', listOfResults)) {
				for (int i = 0; i < keyWords.length - 1; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 3) {
						if (keyWords[i + 1].trim() != "") {
							AdvancedSearch.searchingOrganization(listOfResults,
									listOfResults, keyWords[i + 1], true, 0,
									false);
						}
					}
				}
			}
		}
	}

	/**
	 * @author Mohamed Ghanem
	 * 
	 * @story C4S02 advanced search
	 * 
	 * @Description this method solve the Query the user entered to search with
	 *              by finding the required Entities to the result .
	 * 
	 * @param keyWords
	 *            'String[]' contains the key words needed
	 * 
	 * @param keyWordsIn
	 *            'String[]' contains the key word's type to search in
	 * 
	 * @param isExact
	 *            'boolean' determine if the keywords is/are exact or not.
	 * 
	 * @param listOfResults
	 *            'List<Model>' a reference for the listOfresults.
	 * 
	 * @param currentModels
	 *            'List<Model>' a list of current Models used to find the search
	 *            result in.
	 * 
	 */
	public static void solvingEntityQuery(String[] keyWords,
			String[] keyWordsIn, boolean isExact, List<Model> listOfResults,
			List<Model> currentModels) {
		// Or parts
		for (int i = 0; i < keyWords.length - 1; i += 2) {
			if (Integer.parseInt(keyWords[i]) == 2) {
				if (keyWords[i + 1].trim().compareTo("") != 0) {
					AdvancedSearch.searchingMainEntity(listOfResults,
							currentModels, keyWords[i + 1], isExact,
							Integer.parseInt(keyWordsIn[i / 2]), true);
				}
			}
		}
		// And parts
		if (!isExact) {
			boolean fAnd = true;
			for (int i = 0; i < keyWords.length - 1; i += 2) {
				if (Integer.parseInt(keyWords[i]) == 1) {
					if (keyWords[i + 1].trim() != "") {
						if (fAnd && !checkType('e', listOfResults)) {
							AdvancedSearch.searchingMainEntity(listOfResults,
									currentModels, keyWords[i + 1], false,
									Integer.parseInt(keyWordsIn[i / 2]), true);
							fAnd = false;
						} else {
							if (checkType('e', listOfResults)) {
								AdvancedSearch.searchingMainEntity(
										listOfResults, listOfResults,
										keyWords[i + 1], false,
										Integer.parseInt(keyWordsIn[i / 2]),
										false);
							} else {
								break;
							}
						}
					}
				}
			}
		}
		// Not parts
		if (!isExact) {
			if (checkType('e', listOfResults)) {
				for (int i = 0; i < keyWords.length - 1; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 3) {
						if (keyWords[i + 1].trim() != "") {
							AdvancedSearch.searchingMainEntity(listOfResults,
									listOfResults, keyWords[i + 1], true, 0,
									false);
						}
					}
				}
			}
		}
	}

	/**
	 * @author Mohamed Ghanem
	 * 
	 * @story C4S02 advanced search
	 * 
	 * @Description this method solve the Query the user entered to search with
	 *              by finding the required Topics to the result .
	 * 
	 * @param keyWords
	 *            'String[]' contains the key words needed
	 * 
	 * @param keyWordsIn
	 *            'String[]' contains the key word's type to search in
	 * 
	 * @param isExact
	 *            'boolean' determine if the keywords is/are exact or not.
	 * 
	 * @param listOfResults
	 *            'List<Model>' a reference for the listOfresults.
	 * 
	 * @param currentModels
	 *            'List<Model>' a list of current Models used to find the search
	 *            result in.
	 * 
	 */
	public static void solvingTopicQuery(String[] keyWords,
			String[] keyWordsIn, boolean isExact, List<Model> listOfResults,
			List<Model> currentModels) {
		// Or parts
		for (int i = 0; i < keyWords.length - 1; i += 2) {
			if (Integer.parseInt(keyWords[i]) == 2) {
				if (keyWords[i + 1].trim().compareTo("") != 0) {
					AdvancedSearch.searchingTopic(listOfResults, currentModels,
							keyWords[i + 1], isExact,
							Integer.parseInt(keyWordsIn[i / 2]), true);
				}
			}
		}

		// And parts
		if (!isExact) {
			boolean fAnd = true;
			for (int i = 0; i < keyWords.length - 1; i += 2) {
				if (Integer.parseInt(keyWords[i]) == 1) {
					if (keyWords[i + 1].trim() != "") {
						if (fAnd && !checkType('t', listOfResults)) {
							AdvancedSearch.searchingTopic(listOfResults,
									currentModels, keyWords[i + 1], false,
									Integer.parseInt(keyWordsIn[i / 2]), true);
							fAnd = false;
						} else {
							if (checkType('t', listOfResults)) {
								AdvancedSearch.searchingTopic(listOfResults,
										listOfResults, keyWords[i + 1], false,
										Integer.parseInt(keyWordsIn[i / 2]),
										false);
							} else {
								break;
							}
						}
					}
				}
			}
		}

		// Not parts
		if (!isExact) {
			if (checkType('t', listOfResults)) {
				for (int i = 0; i < keyWords.length - 1; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 3) {
						if (keyWords[i + 1].trim() != "") {
							AdvancedSearch.searchingTopic(listOfResults,
									listOfResults, keyWords[i + 1], true, 0,
									false);
						}
					}
				}
			}
		}
	}

	/**
	 * @author Mohamed Ghanem
	 * 
	 * @story C4S02 advanced search
	 * 
	 * @Description this method solve the Query the user entered to search with
	 *              by finding the required Plan to the result .
	 * 
	 * @param keyWords
	 *            'String[]' contains the key words needed
	 * 
	 * @param keyWordsIn
	 *            'String[]' contains the key word's type to search in
	 * 
	 * @param isExact
	 *            'boolean' determine if the keywords is/are exact or not.
	 * 
	 * @param listOfResults
	 *            'List<Model>' a reference for the listOfresults.
	 * 
	 * @param currentModels
	 *            'List<Model>' a list of current Models used to find the search
	 *            result in.
	 * 
	 */
	public static void solvingPlanQuery(String[] keyWords, String[] keyWordsIn,
			boolean isExact, List<Model> listOfResults,
			List<Model> currentModels) {
		// Or parts
		for (int i = 0; i < keyWords.length - 1; i += 2) {
			if (Integer.parseInt(keyWords[i]) == 2) {
				if (keyWords[i + 1].trim().compareTo("") != 0) {
					AdvancedSearch.searchingPlan(listOfResults, currentModels,
							keyWords[i + 1], isExact,
							Integer.parseInt(keyWordsIn[i / 2]), true);
				}
			}
		}

		// And parts
		if (!isExact) {
			boolean fAnd = true;
			for (int i = 0; i < keyWords.length - 1; i += 2) {
				if (Integer.parseInt(keyWords[i]) == 1) {
					if (keyWords[i + 1].trim() != "") {
						if (fAnd && !checkType('p', listOfResults)) {
							AdvancedSearch.searchingPlan(listOfResults,
									currentModels, keyWords[i + 1], false,
									Integer.parseInt(keyWordsIn[i / 2]), true);
							fAnd = false;
						} else {
							if (checkType('p', listOfResults)) {
								AdvancedSearch.searchingPlan(listOfResults,
										listOfResults, keyWords[i + 1], false,
										Integer.parseInt(keyWordsIn[i / 2]),
										false);
							} else {
								break;
							}
						}
					}
				}
			}
		}

		// Not parts
		if (!isExact) {
			if (checkType('p', listOfResults)) {
				for (int i = 0; i < keyWords.length - 1; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 3) {
						if (keyWords[i + 1].trim() != "") {
							AdvancedSearch.searchingPlan(listOfResults,
									listOfResults, keyWords[i + 1], true, 0,
									false);
						}
					}
				}
			}
		}
	}

	/**
	 * @author Mohamed Ghanem
	 * 
	 * @story C4S02 advanced search
	 * 
	 * @Description this method solve the Query the user entered to search with
	 *              by finding the required Ideas to the result .
	 * 
	 * @param keyWords
	 *            'String[]' contains the key words needed
	 * 
	 * @param keyWordsIn
	 *            'String[]' contains the key word's type to search in
	 * 
	 * @param isExact
	 *            'boolean' determine if the keywords is/are exact or not.
	 * 
	 * @param listOfResults
	 *            'List<Model>' a reference for the listOfresults.
	 * 
	 * @param currentModels
	 *            'List<Model>' a list of current Models used to find the search
	 *            result in.
	 * 
	 */
	public static void solvingIdeaQuery(String[] keyWords, String[] keyWordsIn,
			boolean isExact, List<Model> listOfResults,
			List<Model> currentModels) {
		// Or parts
		for (int i = 0; i < keyWords.length - 1; i += 2) {
			if (Integer.parseInt(keyWords[i]) == 2) {
				if (keyWords[i + 1].trim().compareTo("") != 0) {
					searchingIdea(listOfResults, currentModels,
							keyWords[i + 1], isExact,
							Integer.parseInt(keyWordsIn[i / 2]), true);
				}
			}
		}

		// And parts
		if (!isExact) {
			boolean fAnd = true;
			for (int i = 0; i < keyWords.length - 1; i += 2) {
				if (Integer.parseInt(keyWords[i]) == 1) {
					if (keyWords[i + 1].trim() != "") {
						if (fAnd && !checkType('d', listOfResults)) {
							searchingIdea(listOfResults, currentModels,
									keyWords[i + 1], false,
									Integer.parseInt(keyWordsIn[i / 2]), true);
							fAnd = false;
						} else {
							if (checkType('d', listOfResults)) {
								searchingIdea(listOfResults, listOfResults,
										keyWords[i + 1], false,
										Integer.parseInt(keyWordsIn[i / 2]),
										false);
							} else {
								break;
							}
						}
					}
				}
			}
		}

		// Not parts
		if (!isExact) {
			if (checkType('d', listOfResults)) {
				for (int i = 0; i < keyWords.length - 1; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 3) {
						if (keyWords[i + 1].trim() != "") {
							searchingIdea(listOfResults, listOfResults,
									keyWords[i + 1], true, 0, false);
						}
					}
				}
			}
		}
	}

	/**
	 * @author Mohamed Ghanem
	 * 
	 * @story C4S02 advanced search
	 * 
	 * @Description this method solve the Query the user entered to search with
	 *              by finding the required Items to the result .
	 * 
	 * @param keyWords
	 *            'String[]' contains the key words needed
	 * 
	 * @param keyWordsIn
	 *            'String[]' contains the key word's type to search in
	 * 
	 * @param isExact
	 *            'boolean' determine if the keywords is/are exact or not.
	 * 
	 * @param listOfResults
	 *            'List<Model>' a reference for the listOfresults.
	 * 
	 * @param currentModels
	 *            'List<Model>' a list of current Models used to find the search
	 *            result in.
	 * 
	 */
	public static void solvingItemQuery(String[] keyWords, String[] keyWordsIn,
			boolean isExact, List<Model> listOfResults,
			List<Model> currentModels) {
		// Or parts
		for (int i = 0; i < keyWords.length - 1; i += 2) {
			if (Integer.parseInt(keyWords[i]) == 2) {
				if (keyWords[i + 1].trim().compareTo("") != 0) {
					searchingItem(listOfResults, currentModels,
							keyWords[i + 1], isExact,
							Integer.parseInt(keyWordsIn[i / 2]), true);
				}
			}
		}

		// And parts
		if (!isExact) {
			boolean fAnd = true;
			for (int i = 0; i < keyWords.length - 1; i += 2) {
				if (Integer.parseInt(keyWords[i]) == 1) {
					if (keyWords[i + 1].trim() != "") {
						if (fAnd && !checkType('i', listOfResults)) {
							searchingItem(listOfResults, currentModels,
									keyWords[i + 1], false,
									Integer.parseInt(keyWordsIn[i / 2]), true);
							fAnd = false;
						} else {
							if (checkType('i', listOfResults)) {
								searchingItem(listOfResults, listOfResults,
										keyWords[i + 1], false,
										Integer.parseInt(keyWordsIn[i / 2]),
										false);
							} else {
								break;
							}
						}
					}
				}
			}
		}

		// Not parts
		if (!isExact) {
			if (checkType('i', listOfResults)) {
				for (int i = 0; i < keyWords.length - 1; i += 2) {
					if (Integer.parseInt(keyWords[i]) == 3) {
						if (keyWords[i + 1].trim() != "") {
							searchingItem(listOfResults, listOfResults,
									keyWords[i + 1], true, 0, false);
						}
					}
				}
			}
		}
	}

	/**
	 * @author Mohamed Ghanem
	 * 
	 * @story C4S02 advanced search
	 * 
	 * @Description this method solve the Query the user entered to search with
	 *              by finding the required Items to the result .
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
	 * @param listOfResults 'List<Model>' list of result 
	 * 
	 */
	public static void constrainTime(String dateA, String dateB, String dateE,
			List<Model> listOfResults) {
		Date after = null;
		Date before = null;
		if (dateE.compareTo("") == 0) {
			if (dateA.compareTo("") != 0) {
				String[] dA = dateA.split("/");
				after = new Date(Integer.parseInt(dA[2]),
						Integer.parseInt(dA[0]), Integer.parseInt(dA[1]));
			}
			if (dateB.compareTo("") != 0) {
				String[] dB = dateB.split("/");
				before = new Date(Integer.parseInt(dB[2]),
						Integer.parseInt(dB[0]), Integer.parseInt(dB[1]));
			}
		} else {
			String[] dE = dateE.split("/");
			after = new Date(Integer.parseInt(dE[2]),
					Integer.parseInt(dE[0]) - 1, Integer.parseInt(dE[1]));
			before = new Date(Integer.parseInt(dE[2]),
					Integer.parseInt(dE[0]) + 1, Integer.parseInt(dE[1]));
		}
		if (after != null) {
			for (int i = listOfResults.size() - 1; i >= 0; i--) {
				if (listOfResults.get(i) instanceof Organization) {
					if (after
							.before(((Organization) listOfResults.get(i)).intializedIn)) {
						listOfResults.remove(i);
					}
				} else if (listOfResults.get(i) instanceof MainEntity) {
					if (after
							.before(((MainEntity) listOfResults.get(i)).intializedIn)) {
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
					if (after.before(((Item) listOfResults.get(i)).startDate)) {
						listOfResults.remove(i);
					}
				}
			}
		}
		if (before != null) {
			for (int i = listOfResults.size() - 1; i > 0; i--) {
				if (listOfResults.get(i) instanceof Organization) {
					if (before
							.after(((Organization) listOfResults.get(i)).intializedIn)) {
						listOfResults.remove(i);
					}
				} else if (listOfResults.get(i) instanceof MainEntity) {
					if (before
							.after(((MainEntity) listOfResults.get(i)).intializedIn)) {
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
					if (before.after(((Item) listOfResults.get(i)).endDate)) {
						listOfResults.remove(i);
					}
				}
			}
		}
	}

}
