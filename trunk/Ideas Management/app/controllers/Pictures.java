package controllers;

import java.util.List;

import play.db.jpa.Blob;
import play.mvc.Controller;
import models.Document;
import models.Picture;
import models.User;

import org.hsqldb.store.BitMap;

public class Pictures extends Controller {

	public static void index(long id) {
		User user = Security.getConnected();
		List<Picture> pictures;
		boolean isOrganization = false;
		if (id == -1) {
			pictures = Picture.find("byUserOrganizationId", user.id).fetch();
			for (int i = 0; i < pictures.size(); i++) {
				if (pictures.get(i).isOrganization)
					pictures.remove(i);
			}
		} else {
			isOrganization = true;
			pictures = Picture.find("byUserOrganizationId", id).fetch();
			for (int i = 0; i < pictures.size(); i++) {
				if (!pictures.get(i).isOrganization)
					pictures.remove(i);
			}
		}
		render(pictures, id, isOrganization);
	}

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

	public static void getPicture(long id) {
		Picture picture = Picture.findById(id);
		response.setContentTypeIfNotSet(picture.image.type());
		renderBinary(picture.image.get());
	}

	// public static void getThumb(long id) {
	// Picture picture = Picture.findById(id);
	// response.setContentTypeIfNotSet(picture.thumbnail.type());
	// renderBinary(picture.thumbnail.get());
	// }
}
