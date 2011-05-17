package controllers;

import java.util.List;

import play.db.jpa.Blob;
import play.mvc.Controller;
import models.Document;
import models.Organization;
import models.Picture;
import models.User;

import org.hsqldb.store.BitMap;

/**
 * This is the controller for Picture model 
 * 
 * @author Ibrahim Al-Khayat
 * 
 */
public class Pictures extends Controller {

	/**
	 * Renders the gallery
	 * 
	 * @author Ibrahim Al-Khayat
	 * @param id
	 *            id of the organization (-1 for user)
	 */
	public static void index(long id) {
		User user = Security.getConnected();
		List<Picture> pictures;
		boolean isOrganization = false;
		boolean canDelete = false;
		if (id == -1) {
			pictures = Picture.find("byUserOrganizationId", user.id).fetch();
			for (int i = 0; i < pictures.size(); i++) {
				if (pictures.get(i).isOrganization)
					pictures.remove(i);
			}
			canDelete = true;
		} else {
			isOrganization = true;
			pictures = Picture.find("byUserOrganizationId", id).fetch();
			for (int i = 0; i < pictures.size(); i++) {
				if (!pictures.get(i).isOrganization)
					pictures.remove(i);
			}
			canDelete = (((Organization) Organization.findById(id)).creator.id == user.id)
					|| user.isAdmin;

		}
		render(pictures, id, isOrganization, canDelete);
	}

	/**
	 * Creates a new Picture
	 * 
	 * @author Ibrahim Al-Khayat
	 * @param name
	 *            image display name
	 * @param image
	 *            image data
	 * @param id
	 *            id of the owner user/organization
	 * @param isOrganization
	 *            True if the owner in an Organization false for user
	 */
	public static void uploadPicture(String name, Blob image, long id,
			boolean isOrganization) {
		if (!isOrganization)
			id = Security.getConnected().id;
		new Picture(name, image, id, isOrganization).save();
		if (!isOrganization) {
			index(-1);
		} else {
			index(id);
		}
	}

	/**
	 * Renders the image
	 * 
	 * @author Ibrahim Al-Khayat
	 * @param id
	 *            id of the Picture
	 */
	public static void getPicture(long id) {
		Picture picture = Picture.findById(id);
		response.setContentTypeIfNotSet(picture.image.type());
		renderBinary(picture.image.get());
	}

	/**
	 * Deletes a Picture
	 * 
	 * @author Ibrahim Al-Khayat
	 * @param pictureId
	 *            picture id
	 * @param id
	 *            id of the organization (-1 for user) needed for rendering the
	 *            gallery
	 */
	public static void deletePicture(long pictureId, long id) {
		Picture picture = Picture.findById(pictureId);
		picture.image.getFile().delete();
		picture.delete();
		index(id);
	}

	/**
	 * Set the Picture as profile picture
	 * 
	 * @author Ibrahim Al-Khayat
	 * @param id
	 *            the id of the picture
	 */
	public static void setProfilePicture(long id) {
		Picture picture = Picture.findById(id);
		if (picture.isOrganization) {
			Organization organization = Organization
					.findById(picture.userOrganizationId);
			organization.profilePictureId = id;
			organization.save();
		} else {
			User user = User.findById(picture.userOrganizationId);
			user.profilePictureId = id;
			user.save();
		}
	}

}