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

	/**
	 * Renders the page for creating a new document
	 * 
	 * @author Ibrahim Al-Khayat
	 * @param id
	 *            id of the owner organization (-1 for user)
	 */
	public static void newDocument(long id) {
		boolean isOrganization = true;
		User user = Security.getConnected();
		if (id == -1) {
			isOrganization = false;
			id = user.id;
		}
		render(id, user, isOrganization);
	}

	/**
	 * Creates a new document and store it in the database
	 * 
	 * @author Ibrahim Al-Kahayat
	 * @param id
	 *            id of the owner user/organization
	 * @param isOrganization
	 *            flag to indicate weather the owner is an organization or not
	 * @param title
	 *            document title
	 * @param data
	 *            document body
	 */
	public static void createDocument(long id, boolean isOrganization,
			String title, String data) {
		new Document(title, data, id, isOrganization).save();
	}

	/**
	 * Renders the viewsDocument HTML
	 * 
	 * @author Ibrahim Al-Kahayat
	 * @param id
	 *            id of the document
	 */
	public static void viewDocument(long id) {
		Document document = Document.findById(id);
		boolean canEdit = false;
		long userId = Security.getConnected().id;
		if (document.isOrganization) {
			if (((Organization) Organization
					.findById(document.userOrganizationId)).creator.id == userId)
				canEdit = true;
		} else {
			if (userId == document.userOrganizationId)
				canEdit = true;
		}
		render(document, canEdit);
	}

	/**
	 * Renders the listDocument HTML
	 * 
	 * @author Ibrahim Al-Kahayat
	 * @param id
	 *            id of the Organization (-1 for rendering the list for a user)
	 */
	public static void listDocument(long id) {
		User user = Security.getConnected();
		List<Document> documents;
		if (id == -1) {
			documents = Document.find("byUserOrganizationId", user.id).fetch();
			for (int i = 0; i < documents.size(); i++) {
				if (documents.get(i).isOrganization)
					documents.remove(i);
			}
		} else {
			documents = Document.find("byUserOrganizationId", id).fetch();
			for (int i = 0; i < documents.size(); i++) {
				if (!documents.get(i).isOrganization)
					documents.remove(i);
			}
		}
		render(user, documents);
	}

	/**
	 * Renders the editDocument HTML
	 * 
	 * @author Ibrahim Al-Kahayat
	 * @param id
	 *            id of the document
	 */
	public static void editDocument(long id) {
		User user = Security.getConnected();
		Document document = Document.findById(id);
		render(user, document);
	}

	/**
	 * Edits the title and the body of the document
	 * 
	 * @author Ibrahim Al-Kahayat
	 * @param id
	 *            id of the document
	 * @param title
	 *            the updated title
	 * @param data
	 *            the updated body
	 */
	public static void updateDocument(long id, String title, String data) {
		Document document = Document.findById(id);
		document.data = data;
		document.name = title;
		document.save();
	}

	/**
	 * Deletes a document
	 * 
	 * @author Ibrahim Al-Kahayat
	 * @param id
	 *            id of the document
	 */
	public static void deleteDocument(long id) {
		Document document = Document.findById(id);
		document.delete();
	}

}
