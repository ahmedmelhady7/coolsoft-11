package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Idea;
import models.LinkDuplicatesRequest;
import models.MainEntity;
import play.mvc.Controller;

public class MarkingRequest extends Controller {

	public static void markDuplicate(Idea idea1, Idea idea2,String des) {
		Long ideaOrg1 = idea1.belongsToTopic.entity.id;
		Long ideaOrg2 = idea2.belongsToTopic.entity.id;
		if (ideaOrg1 == ideaOrg2) {
			List<Idea> duplicateIdeas = new ArrayList<Idea>();
			duplicateIdeas.add(idea1);
			duplicateIdeas.add(idea2);
			MainEntity ent = MainEntity.findById(ideaOrg1);
			new LinkDuplicatesRequest(ent.organizers.get(0), idea1, idea2 , des);
		}
		render(idea1,idea2,des);
	}
}
