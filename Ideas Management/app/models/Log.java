package models;

import java.util.Arrays;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import java.lang.reflect.*;

import javax.persistence.Entity;

import models.User;
//import models.Project;

import controllers.Security;

import play.db.jpa.*;

/**
 *  Model Invitation by Lama Ashraf
 */

@Entity
public class Log extends CoolModel{
	/**
	 *  description of the action saved in the log
	 */
	public String actionDescription;
	/**
	 *  timestamp of the action 
	 */
	public String time;
	
	/**
	 * Adds a log with the models attached
	 * 
	 * @author Lama Ashraf
	 * 
	 * @story C1S8
	 * 
	 * @param  message 
	 * 				String log message
	 *@param models 
	 *				CoolModel all models attached to this log        
	 */
	
	public static void addLog(String message, CoolModel... models) {
        Log log = new Log();
        log.actionDescription = message;
        log.time = new Date().toString();
        System.out.println(log.time);
        log.save();

        List<Class> clazzes = new ArrayList<Class>();
        for (CoolModel model : models) {
                model.logs.add(log);
                if (clazzes.contains(model.getClass())) {
                        model.refresh();
                } else {
                        clazzes.add(model.getClass());
                }
                model.save();
        }
}
	
	/**
	 * Adds a log with the connected user attached to the models
	 * 
	 * @author Lama Ashraf
	 * 
	 * @story C1S8
	 * 
	 * @param  message 
	 * 				String log message
	 *@param models 
	 *				CoolModel all models attached to this log        
	 */
	
    public static void addUserLog(String message, CoolModel... models) {
            CoolModel[] newModels = Arrays.copyOf(models, models.length + 1);
            newModels[models.length] = Security.getConnected();
            addLog(message, newModels);
    }
    
   
	
    
}
