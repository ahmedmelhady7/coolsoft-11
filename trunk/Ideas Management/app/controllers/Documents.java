package controllers;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import models.Document;
import models.Idea;
import models.Organization;
import models.User;

import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import controllers.CRUD.ObjectType;

public class Documents extends CRUD {

	public static void newDocument() {
		boolean isOrganization = false;
		Organization organization = null;
		User user = Security.getConnected();
		long id = user.id;
		// if (isOrganization) {
		// organization = Organization.findById(id);
		// owner = organization.creator;
		// } else {
		// owner = User.findById(id);
		// }
		render(id, user, organization, isOrganization);
	}

	public static void createDocument(long id, boolean isOrganization,
			String name, String data) {
		System.out.println("Entered Create");
		new Document(name, data, id, isOrganization).save();
	}

	public static void viewDocument(long id) {
		System.out.println("ENTERED viewDOC");
		Document document = Document.findById(id);
		render(document);
	}

	public static void listDocument() {
		User user = Security.getConnected();
		List<Document> documents = Document.find("byUserOrganizationId",
				user.id).fetch();
		for (int i = 0; i < documents.size(); i++) {
			if (documents.get(i).isOrganization)
				documents.remove(i);
		}
		render(user, documents);
	}

}
