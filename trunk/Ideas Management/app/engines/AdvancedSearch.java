package engines;

import java.util.ArrayList;
import java.util.List;

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
	 * @param listOfResults
	 * @param tOrgs
	 * @param key
	 * @param exKey
	 * @param in
	 * @param or
	 */
	public static void searchingOrganization(List<Model> listOfResults,
			List<Model> tOrgs, String key, boolean exKey, int in, boolean or) {
		if (or) {
			if (exKey) {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Organization) {
							if (((Organization) tOrgs.get(i)).name.equals(key)) {
								listOfResults.add(tOrgs.get(i));
								tOrgs.remove(i);
								i--;
							} else {
								if (((Organization) tOrgs.get(i)).description
										.equals(key)) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								}
								List<Tag> x = ((Organization) tOrgs.get(i)).relatedTags;
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
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Organization) {
								if (((Organization) tOrgs.get(i)).name
										.equals(key)) {
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
											.equals(key)) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Organization) {
										List<Tag> x = ((Organization) tOrgs
												.get(i)).relatedTags;
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
				System.out.println("AAA");
				if (in == 0) {
					System.out.println("BBB");
					for (int i = 0; i < tOrgs.size(); i++) {
						System.out.println("CCC" + i);
						if (tOrgs.get(i) instanceof Organization) {
							System.out.println("DDD");
							System.out.println(key);
							if (((Organization) tOrgs.get(i)).name
									.toLowerCase().contains(key.toLowerCase())) {
								System.out.println("Added");
								listOfResults.add(tOrgs.get(i));
								System.out.println("added   111");
								tOrgs.remove(i);
								i--;
							} else {
								if (((Organization) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									System.out.println("added   120");
									i--;
								}
								List<Tag> x = ((Organization) tOrgs.get(i)).relatedTags;
								for (int j = 0; j < x.size(); j++) {
									if (x.get(j).name.toLowerCase().contains(
											key.toLowerCase())) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										System.out.println("added   129");
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
							if (tOrgs.get(i) instanceof Organization) {
								if (((Organization) tOrgs.get(i)).name
										.toLowerCase().contains(
												key.toLowerCase())) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									System.out.println("added   146");
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
										System.out.println("added   160");
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
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
												System.out
														.println("added   178");
												System.out
														.println(x.get(j).name);
												System.out.println("key is : "
														+ key);
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
								}
								List<Tag> x = ((Organization) tOrgs.get(i)).relatedTags;
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
							if (!((Organization) tOrgs.get(i)).name
									.toLowerCase().contains(key.toLowerCase())) {
								tOrgs.remove(i);
								i--;
							} else {
								if (!((Organization) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
								boolean b = false;
								List<Tag> x = ((Organization) tOrgs.get(i)).relatedTags;
								for (int j = 0; j < x.size(); j++) {
									if (x.get(j).name.toLowerCase().contains(
											key.toLowerCase())) {
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
	 * @param listOfResults
	 * @param tOrgs
	 * @param key
	 * @param exKey
	 * @param in
	 * @param or
	 */
	public static void searchingMainEntity(List<Model> listOfResults,
			List<Model> tOrgs, String key, boolean exKey, int in, boolean or) {
		if (or) {
			if (exKey) {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof MainEntity) {
							if (((MainEntity) tOrgs.get(i)).name.equals(key)) {
								listOfResults.add(tOrgs.get(i));
								tOrgs.remove(i);
								i--;
							} else {
								if (((MainEntity) tOrgs.get(i)).description
										.equals(key)) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								}
								List<Tag> x = ((MainEntity) tOrgs.get(i)).tagList;
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
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof MainEntity) {
								if (((MainEntity) tOrgs.get(i)).name
										.equals(key)) {
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
											.equals(key)) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof MainEntity) {
										List<Tag> x = ((MainEntity) tOrgs
												.get(i)).tagList;
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
				System.out.println("goo AAA");
				if (in == 0) {
					System.out.println("gooes BBB");
					for (int i = 0; i < tOrgs.size(); i++) {
						System.out.println("gooes BBB" + i);
						if (tOrgs.get(i) instanceof MainEntity) {
							if (((MainEntity) tOrgs.get(i)).name.toLowerCase()
									.contains(key.toLowerCase())) {
								System.out.println("ADDDDDED here");
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
								}
								List<Tag> x = ((MainEntity) tOrgs.get(i)).tagList;
								for (int j = 0; j < x.size(); j++) {
									if (x.get(j).name.toLowerCase().contains(
											key.toLowerCase())) {
										listOfResults.add(tOrgs.get(i));
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
								}
								List<Tag> x = ((MainEntity) tOrgs.get(i)).tagList;
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
							if (!((MainEntity) tOrgs.get(i)).name.toLowerCase()
									.contains(key.toLowerCase())) {
								tOrgs.remove(i);
								i--;
							} else {
								if (!((MainEntity) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
								List<Tag> x = ((MainEntity) tOrgs.get(i)).tagList;
								boolean b = false;
								for (int j = 0; j < x.size(); j++) {
									if (x.get(j).name.toLowerCase().contains(
											key.toLowerCase())) {
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
	 * @param listOfResults
	 * @param tOrgs
	 * @param key
	 * @param exKey
	 * @param in
	 * @param or
	 */
	public static void searchingTopic(List<Model> listOfResults,
			List<Model> tOrgs, String key, boolean exKey, int in, boolean or) {
		if (or) {
			if (exKey) {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Topic) {
							if (((Topic) tOrgs.get(i)).title.equals(key)) {
								listOfResults.add(tOrgs.get(i));
								tOrgs.remove(i);
								i--;
							} else {
								if (((Topic) tOrgs.get(i)).description
										.equals(key)) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								}
								List<Tag> x = ((Topic) tOrgs.get(i)).tags;
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
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Topic) {
								if (((Topic) tOrgs.get(i)).title.equals(key)) {
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
											.equals(key)) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Topic) {
										List<Tag> x = ((Topic) tOrgs.get(i)).tags;
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
				System.out.println("goo AAA");
				if (in == 0) {
					System.out.println("gooes BBB");
					for (int i = 0; i < tOrgs.size(); i++) {
						System.out.println("gooes BBB" + i);
						if (tOrgs.get(i) instanceof Topic) {
							if (((Topic) tOrgs.get(i)).title.toLowerCase()
									.contains(key.toLowerCase())) {
								System.out.println("ADDDDDED here");
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
								}
								List<Tag> x = ((Topic) tOrgs.get(i)).tags;
								for (int j = 0; j < x.size(); j++) {
									if (x.get(j).name.toLowerCase().contains(
											key.toLowerCase())) {
										listOfResults.add(tOrgs.get(i));
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
								}
								List<Tag> x = ((Topic) tOrgs.get(i)).tags;
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
							if (!((Topic) tOrgs.get(i)).title.toLowerCase()
									.contains(key.toLowerCase())) {
								tOrgs.remove(i);
								i--;
							} else {
								if (!((Topic) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
								List<Tag> x = ((Topic) tOrgs.get(i)).tags;
								boolean b = false;
								for (int j = 0; j < x.size(); j++) {
									if (x.get(j).name.toLowerCase().contains(
											key.toLowerCase())) {
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
	 * @param listOfResults
	 * @param tOrgs
	 * @param key
	 * @param exKey
	 * @param in
	 * @param or
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
								}
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
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Idea) {
								if (((Idea) tOrgs.get(i)).title
										.equals(key)) {
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
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Idea) {
										List<Tag> x = ((Idea) tOrgs
												.get(i)).tagsList;
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
				System.out.println("goo AAA");
				if (in == 0) {
					System.out.println("gooes BBB");
					for (int i = 0; i < tOrgs.size(); i++) {
						System.out.println("gooes BBB"+i);
						if (tOrgs.get(i) instanceof Idea) {
							if (((Idea) tOrgs.get(i)).title.toLowerCase()
									.contains(key.toLowerCase())) {
								System.out.println("ADDDDDED here");
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
								}
								List<Tag> x = ((Idea) tOrgs.get(i)).tagsList;
								for (int j = 0; j < x.size(); j++) {
									if (x.get(j).name.toLowerCase().contains(
											key.toLowerCase())) {
										listOfResults.add(tOrgs.get(i));
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
								if (((Idea) tOrgs.get(i)).title
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
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Idea) {
										List<Tag> x = ((Idea) tOrgs
												.get(i)).tagsList;
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
								if (((Idea) tOrgs.get(i)).title
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
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Idea) {

										List<Tag> x = ((Idea) tOrgs
												.get(i)).tagsList;
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
								List<Tag> x = ((Idea) tOrgs.get(i)).tagsList;
								boolean b=false;
								for (int j = 0; j < x.size(); j++) {
									if (x.get(j).name.toLowerCase().contains(
											key.toLowerCase())) {
										b=true;
										break;
									}
								}
								if(b){
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
								if (!((Idea) tOrgs.get(i)).title
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
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Idea) {
										boolean b=false;
										List<Tag> x = ((Idea) tOrgs
												.get(i)).tagsList;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												b=true;
												break;
											}
										}
										if(b){
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
	 * @param listOfResults
	 * @param tOrgs
	 * @param key
	 * @param exKey
	 * @param in
	 * @param or
	 */
	public static void searchingItem(List<Model> listOfResults,
			List<Model> tOrgs, String key, boolean exKey, int in, boolean or) {
		if (or) {
			if (exKey) {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Item) {
							if (((Item) tOrgs.get(i)).summary.equals(key)) {
								listOfResults.add(tOrgs.get(i));
								tOrgs.remove(i);
								i--;
							} else {
								if (((Item) tOrgs.get(i)).description
										.equals(key)) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								}
								List<Tag> x = ((Item) tOrgs.get(i)).tags;
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
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Item) {
								if (((Item) tOrgs.get(i)).summary
										.equals(key)) {
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
											.equals(key)) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Item) {
										List<Tag> x = ((Item) tOrgs
												.get(i)).tags;
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
				System.out.println("goo AAA");
				if (in == 0) {
					System.out.println("gooes BBB");
					for (int i = 0; i < tOrgs.size(); i++) {
						System.out.println("gooes BBB"+i);
						if (tOrgs.get(i) instanceof Item) {
							if (((Item) tOrgs.get(i)).summary.toLowerCase()
									.contains(key.toLowerCase())) {
								System.out.println("ADDDDDED here");
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
								}
								List<Tag> x = ((Item) tOrgs.get(i)).tags;
								for (int j = 0; j < x.size(); j++) {
									if (x.get(j).name.toLowerCase().contains(
											key.toLowerCase())) {
										listOfResults.add(tOrgs.get(i));
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
							if (tOrgs.get(i) instanceof Item) {
								if (((Item) tOrgs.get(i)).summary
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
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Item) {
										List<Tag> x = ((Item) tOrgs
												.get(i)).tags;
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
								}
								List<Tag> x = ((Item) tOrgs.get(i)).tags;
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
							if (tOrgs.get(i) instanceof Item) {
								if (((Item) tOrgs.get(i)).summary
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
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Item) {

										List<Tag> x = ((Item) tOrgs
												.get(i)).tags;
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
							if (!((Item) tOrgs.get(i)).summary.toLowerCase()
									.contains(key.toLowerCase())) {
								tOrgs.remove(i);
								i--;
							} else {
								if (!((Item) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
								List<Tag> x = ((Item) tOrgs.get(i)).tags;
								boolean b=false;
								for (int j = 0; j < x.size(); j++) {
									if (x.get(j).name.toLowerCase().contains(
											key.toLowerCase())) {
										b=true;
										break;
									}
								}
								if(b){
									tOrgs.remove(i);
									i--;
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
							} else {
								for (int i = 0; i < tOrgs.size(); i++) {
									if (tOrgs.get(i) instanceof Item) {
										boolean b=false;
										List<Tag> x = ((Item) tOrgs
												.get(i)).tags;
										for (int j = 0; j < x.size(); j++) {
											if (x.get(j).name
													.toLowerCase()
													.contains(key.toLowerCase())) {
												b=true;
												break;
											}
										}
										if(b){
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
	 * @param listOfResults
	 * @param tOrgs
	 * @param key
	 * @param exKey
	 * @param in
	 * @param or
	 */
	public static void searchingPlan(List<Model> listOfResults,
			List<Model> tOrgs, String key, boolean exKey, int in, boolean or) {
		if (or) {
			if (exKey) {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Plan) {
							if (((Plan) tOrgs.get(i)).title.equals(key)) {
								listOfResults.add(tOrgs.get(i));
								tOrgs.remove(i);
								i--;
							} else {
								if (((Plan) tOrgs.get(i)).description
										.equals(key)) {
									listOfResults.add(tOrgs.get(i));
									tOrgs.remove(i);
									i--;
								}
//								List<Tag> x = ((Plan) tOrgs.get(i)).tagsList;
//								for (int j = 0; j < x.size(); j++) {
//									if (x.get(j).name.equals(key)) {
//										listOfResults.add(tOrgs.get(i));
//										tOrgs.remove(i);
//										i--;
//										break;
//									}
//								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Plan) {
								if (((Plan) tOrgs.get(i)).title
										.equals(key)) {
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
											.equals(key)) {
										listOfResults.add(tOrgs.get(i));
										tOrgs.remove(i);
										i--;
									}
								}
							}
						} else {
							if (in == 3) {
							} else {
//								for (int i = 0; i < tOrgs.size(); i++) {
//									if (tOrgs.get(i) instanceof Plan) {
//										List<Tag> x = ((Plan) tOrgs
//												.get(i)).tagsList;
//										for (int j = 0; j < x.size(); j++) {
//											if (x.get(j).name.equals(key)) {
//												listOfResults.add(tOrgs.get(i));
//												tOrgs.remove(i);
//												i--;
//												break;
//											}
//										}
//									}
//								}
							}
						}
					}
				}
			} else {
				System.out.println("goo AAA");
				if (in == 0) {
					System.out.println("gooes BBB");
					for (int i = 0; i < tOrgs.size(); i++) {
						System.out.println("gooes BBB"+i);
						if (tOrgs.get(i) instanceof Plan) {
							if (((Plan) tOrgs.get(i)).title.toLowerCase()
									.contains(key.toLowerCase())) {
								System.out.println("ADDDDDED here");
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
								}
//								List<Tag> x = ((Plan) tOrgs.get(i)).tagsList;
//								for (int j = 0; j < x.size(); j++) {
//									if (x.get(j).name.toLowerCase().contains(
//											key.toLowerCase())) {
//										listOfResults.add(tOrgs.get(i));
//										tOrgs.remove(i);
//										i--;
//										break;
//									}
//								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Plan) {
								if (((Plan) tOrgs.get(i)).title
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
							} else {
//								for (int i = 0; i < tOrgs.size(); i++) {
//									if (tOrgs.get(i) instanceof Plan) {
//										List<Tag> x = ((Plan) tOrgs
//												.get(i)).tagsList;
//										for (int j = 0; j < x.size(); j++) {
//											if (x.get(j).name
//													.toLowerCase()
//													.contains(key.toLowerCase())) {
//												listOfResults.add(tOrgs.get(i));
//												tOrgs.remove(i);
//												i--;
//												break;
//											}
//										}
//									}
//								}
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
								}
//								List<Tag> x = ((Plan) tOrgs.get(i)).tagsList;
//								for (int j = 0; j < x.size(); j++) {
//									if (x.get(j).name.toLowerCase().contains(
//											key.toLowerCase())) {
//										tOrgs.remove(i);
//										i--;
//										break;
//									}
//								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Plan) {
								if (((Plan) tOrgs.get(i)).title
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
							} else {
//								for (int i = 0; i < tOrgs.size(); i++) {
//									if (tOrgs.get(i) instanceof Plan) {
//
//										List<Tag> x = ((Plan) tOrgs
//												.get(i)).tagsList;
//										for (int j = 0; j < x.size(); j++) {
//											if (x.get(j).name
//													.toLowerCase()
//													.contains(key.toLowerCase())) {
//												tOrgs.remove(i);
//												i--;
//												break;
//											}
//										}
//									}
//								}
							}
						}
					}
				}
			} else {
				if (in == 0) {
					for (int i = 0; i < tOrgs.size(); i++) {
						if (tOrgs.get(i) instanceof Plan) {
							if (!((Plan) tOrgs.get(i)).title.toLowerCase()
									.contains(key.toLowerCase())) {
								tOrgs.remove(i);
								i--;
							} else {
								if (!((Plan) tOrgs.get(i)).description
										.toLowerCase().contains(
												key.toLowerCase())) {
									tOrgs.remove(i);
									i--;
								}
//								List<Tag> x = ((Plan) tOrgs.get(i)).tagsList;
//								boolean b=false;
//								for (int j = 0; j < x.size(); j++) {
//									if (x.get(j).name.toLowerCase().contains(
//											key.toLowerCase())) {
//										b=true;
//										break;
//									}
//								}
//								if(b){
//									tOrgs.remove(i);
//									i--;
//								}
							}
						}
					}
				} else {
					if (in == 1) {
						for (int i = 0; i < tOrgs.size(); i++) {
							if (tOrgs.get(i) instanceof Plan) {
								if (!((Plan) tOrgs.get(i)).title
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
							} else {
//								for (int i = 0; i < tOrgs.size(); i++) {
//									if (tOrgs.get(i) instanceof Plan) {
//										boolean b=false;
//										List<Tag> x = ((Plan) tOrgs
//												.get(i)).tagsList;
//										for (int j = 0; j < x.size(); j++) {
//											if (x.get(j).name
//													.toLowerCase()
//													.contains(key.toLowerCase())) {
//												b=true;
//												break;
//											}
//										}
//										if(b){
//											tOrgs.remove(i);
//											i--;
//										}
//									}
//								}
							}
						}
					}
				}
			}
		}
	}

}
