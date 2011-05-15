package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@MappedSuperclass
public class CoolModel extends Model {

	@OneToMany
    public List<Log> logs;

    /**
     * default constructor in order to make the arraylist of logs on creation of
     * any model instance
     * 
     * @author Lama Ashraf
     */
    public CoolModel()
    {
            this.logs = new ArrayList<Log>();
    }
}
