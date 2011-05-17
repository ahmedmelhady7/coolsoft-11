package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.persistence.Entity;
import javax.persistence.Lob;

import play.data.validation.Required;
import play.db.jpa.Blob;
import play.libs.Images;

/**
 * 
 * @author Ibrahim Al-Khayat
 * 
 */

@Entity
public class Picture extends CoolModel {

	/**
	 * Picture name
	 */
	@Required
	public String name;

	/**
	 * image
	 */

	@Required
	public Blob image;

	/**
	 * thumbnail
	 */

	@Required
	public Blob thumbnail;

	/**
	 * Id of owner organization/user
	 */
	@Required
	public long userOrganizationId;

	/**
	 * true for organization false for user
	 */
	@Required
	public boolean isOrganization;

	/**
	 * Constructor
	 * 
	 * @author Ibrahim Al-Khayat
	 * @param name
	 *            image name
	 * @param data
	 *            data in bytes
	 * @param type
	 *            image file extension
	 * @param id
	 *            id of owner user/organization
	 * @param isOrganization
	 *            True for organization false for user
	 * @throws Exception
	 */
	public Picture(String name, Blob image, long id, boolean isOrganization) {
		this.name = name;
		this.image = image;
		this.userOrganizationId = id;
		this.isOrganization = isOrganization;
		// File thumbnailFile = new File("C:\\Users\\I.A\\Desktop\\thumb0.png");
		// Images.resize(this.image.getFile(), thumbnailFile, -1, 200);
		// InputStream is = new
		// FileInputStream("C:\\Users\\I.A\\Desktop\\thumb0.png");
		// thumbnail.set(is, "png");
	}
}
