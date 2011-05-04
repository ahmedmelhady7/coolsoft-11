package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Idea;
import models.LinkDuplicatesRequest;
import models.MainEntity;
import play.mvc.Controller;

/**
 * 
 * @author Loaay Alkherbawy
 * 
 * @story C4S10 : Marking two Ideas as a duplicate
 * 
 */

public class MarkingRequest extends Controller {

	/**
	 * @author Loaay Alkherbawy
	 * 
	 * @Story C4S10: marking to ideas as duplicate
	 * 
	 * @param idea1
	 *            : Idea one
	 * @param idea2
	 *            : Idea relevant to Idea one
	 * @param des
	 *            : description of where the duplication is
	 */

	public static void markDuplicate(long idea1ID, long idea2ID, String des) {
		Idea i1 = Idea.findById(idea1ID);
		Idea i2 = Idea.findById(idea2ID);
		Long ideaOrg1 = i1.belongsToTopic.entity.id;
		Long ideaOrg2 = i2.belongsToTopic.entity.id;
		if (ideaOrg1 == ideaOrg2) {
			List<Idea> duplicateIdeas = new ArrayList<Idea>();
			duplicateIdeas.add(i1);
			duplicateIdeas.add(i2);
			MainEntity ent = MainEntity.findById(ideaOrg1);
			new LinkDuplicatesRequest(ent.organizers.get(0), i1, i2, des);
		}
		//render(i1, i2, des);
	}
}
