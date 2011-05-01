import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.Timer;

import controllers.TimerCall;

import play.*;
import play.jobs.*;
import play.test.*;

import models.*;

@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob() {
	/*	
		User u1 = new User("h@gmail.com", "1234", "Hamed", "EL-Akhdar",
				"hamedcool", 0, new Date(), "egypt", "student");
		//u1._save();
		Organization org1 = new Organization("HamedSoft", u1, (short) 0, true);
		org1._save();
		MainEntity me1 = new MainEntity("Hamed yntlek",
				"ideas to help hamed become like billgates", org1);
		me1._save();
		Topic t1 = new Topic("What to do?", "Help me please", (short) 0, u1,
				me1);
		t1._save();
		Tag tag = new Tag("Sports");
		tag._save();
		RequestToJoin request = new RequestToJoin(u1, t1, org1, "I would like to join your organization..");
		request._save();
		*/
		
		
		//Calling Thread that will run when the application start to check
		//For Ideas every day
		ActionListener listener = new TimerCall();

	       // construct a timer that calls the listener
	       // once every 24 hours = 24*60*60 = 86400 seconds
	       // The timer thread takes milli seconds so 86400000
	    Timer t = new Timer(86400000, listener);
	    t.start();
	    System.exit(0);
	}
}

	