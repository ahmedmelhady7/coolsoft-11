package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class OrganizationRelationsNames extends CoolModel {
	
	public String name ;
	
	
	@ManyToOne
   public Organization organization;
	
	public OrganizationRelationsNames(String name, Organization organization){
		this.name = name;
		this.organization=organization;
	}
	
	

}
