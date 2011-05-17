package models;

import javax.persistence.Entity;
import javax.persistence.Lob;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * This is a model for storing documents
 * 
 * @author Ibrahim Al-Khayat
 * 
 */
@Entity
public class Document extends CoolModel {

	/**
	 * Document name
	 */
	@Required
	public String name;

	/**
	 * Data
	 */
	@Lob
	@Required
	public String data;

	/**
	 * Id of organization or user
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
	 * 
	 * @param name
	 *            Document name
	 * @param file
	 *            Data in bytes
	 * @param id
	 *            Id of the owner user or organization
	 * @param isOrganization
	 *            True for organization false for user
	 */

	public Document(String name, String data, long id, boolean isOrganization) {
		this.name = name;
		this.data = data;
		this.userOrganizationId = id;
		this.isOrganization = isOrganization;
	}
}