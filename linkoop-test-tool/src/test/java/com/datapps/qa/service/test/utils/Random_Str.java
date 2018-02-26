package com.datapps.qa.service.test.utils;

import java.util.Random;



public class Random_Str {
	public Random_Str(){
		
	}
	
	public String getRandomString(int length){
		 String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	     Random random = new Random();
	     StringBuffer strBu = new StringBuffer();
	     for(int i=0; i<length ;i++)
	     {
	    	 int number = random.nextInt(str.length()-1);
	    	 strBu.append(str.charAt(number));
	    	 
	     }
		
		return strBu.toString();
	}
	
	
}
