package controllers;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import models.Idea;

public class TimerCall implements ActionListener
{
	 public void actionPerformed(ActionEvent event)
	 {
	       Idea.getAllIdeas();
	 }
	 
}
