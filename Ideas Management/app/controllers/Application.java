package controllers;


import play.*;
import play.mvc.*;

import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import models.*;

public class Application extends CoolCRUD {

	public static void index() {
		render();		
	}
	/**
	 * returns the hash value of the provided String
	 * 
	 * @author Mostafa Ali
	 * 
	 * @param string
	 * 			String , the String that would be hashed
	 *
	 * @return hash String
	 */
	public static String hash( String string )
	{
		String hashedString = "";
		try
		{
			MessageDigest algorithm = MessageDigest.getInstance( "MD5" );
			algorithm.reset();
			algorithm.update( string.getBytes() );
			byte[] md5 = algorithm.digest();
			String tmp = "";
			for( int i = 0; i < md5.length; i++ )
			{
				tmp = (Integer.toHexString( 0xFF & md5[i] ));
				if( tmp.length() == 1 )
				{
					hashedString += "0" + tmp;
				}
				else
				{
					hashedString += tmp;
				}
			}
		}
		catch( NoSuchAlgorithmException ex )
		{
		}
		return hashedString;
	}


	/**
	 * Generates a random hash String with a specified length
	 * 
	 * @author Mostafa Ali
	 * 
	 * 
	 * @param length
	 *            , The length of the hash string
	 * @return hash String
	 */
	public static String randomHash( int length )
	{
		return hash( System.currentTimeMillis() * Math.random() + "" ).substring( 0, length );
	}
}