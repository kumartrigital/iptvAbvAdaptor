package com.obs.main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;  
public class DateTest {  
public static void main(String[] args) {  
    Date date = new Date();  
    
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");  
    String strDate = formatter.format(date);  
    System.out.println("Date Format with MM/dd/yyyy : "+strDate);  
  
    formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");  
    strDate = formatter.format(date);  
    System.out.println("Date Format with dd-M-yyyy hh:mm:ss : "+strDate);  
    
    SimpleDateFormat existFormatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
    try {
    	 String inputDate = "Thu Nov 09 00:00:00 IST 2017"; 	    

		Date expiryDate1 = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(inputDate);
		System.out.println("Date created : " + expiryDate1);
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		strDate = formatter.format(date);  
		Date expiryDate2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
		System.out.println("Date created2 : " + expiryDate2);
		 System.out.println("New Date Format with yyyy-MM-dd HH:mm:ss : "+strDate);
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  
    formatter = new SimpleDateFormat("dd MMMM yyyy");  
    strDate = formatter.format(date);  
    System.out.println("Date Format with dd MMMM yyyy : "+strDate);  
  
    formatter = new SimpleDateFormat("dd MMMM yyyy zzzz");  
    strDate = formatter.format(date);  
    System.out.println("Date Format with dd MMMM yyyy zzzz : "+strDate);  
  
    formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");  
    strDate = formatter.format(date);  
    System.out.println("Date Format with E, dd MMM yyyy HH:mm:ss z : "+strDate);  
    
    formatter = new SimpleDateFormat("E, MMM dd HH:mm:ss z yyyy");  
    strDate = formatter.format(date);  
    System.out.println("Date Format with E, MMM dd HH:mm:ss z yyyy: "+strDate);  
}  
}  