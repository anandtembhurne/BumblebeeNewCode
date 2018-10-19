package com.unotech.application;

import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
 

public class AppPostgresImpl {
      
    public boolean loginDetails() {
        Connection c = null;
        
        PrintWriter printWriter=null;
        try {
            FileReader reader=new FileReader("/tmp/Bumblebee/config.properties");  
            Properties p=new Properties();
            p.load(reader);
            Class.forName("org.postgresql.Driver");
            c = DriverManager
              .getConnection("jdbc:postgresql://"+p.getProperty("link")+":"+p.getProperty("postgres.port")+"/"+p.getProperty("postgres.database.name"),
                      p.getProperty("postgres.user.name"), p.getProperty("postgres.password"));
			printWriter = new PrintWriter(p.getProperty("file.path")+"loginDetails.csv");
			printWriter.append("Name , login date & time \n");
			Statement createStatement = c.createStatement();
			ResultSet executeQuery = createStatement.executeQuery(" select * from nxp_logs l where l.log_event_id='loginSuccess' ");
           
           while (executeQuery.next()) {
        	   String principleName = executeQuery.getString("log_principal_name");
        	   printWriter.append(principleName+",");
        	   String date = executeQuery.getString("log_event_date");
        	   printWriter.append(date+"\n");        	   
        	 }
           c.close();
           reader.close();
           printWriter.flush();
           printWriter.close();
           return true;
        } catch (Exception e) {
           e.printStackTrace();
           System.err.println(e.getClass().getName()+": "+e.getMessage());
           System.exit(0);
        }
		return false;
		
	}

	public boolean todaysloginDetails() {
        Connection c = null;
        PrintWriter printWriter=null;
        try {
            FileReader reader=new FileReader("/tmp/Bumblebee/config.properties");  
            Properties p=new Properties();
            p.load(reader);
            Class.forName("org.postgresql.Driver");
            c = DriverManager
              .getConnection("jdbc:postgresql://"+p.getProperty("link")+":"+p.getProperty("postgres.port")+"/"+p.getProperty("postgres.database.name"),
              p.getProperty("postgres.user.name"), p.getProperty("postgres.password"));
			printWriter = new PrintWriter(p.getProperty("file.path")+"todaysloginDetails.csv");
			printWriter.append("Name , login date & time \n");
			Statement createStatement = c.createStatement();
			ResultSet executeQuery = createStatement.executeQuery(" select * from nxp_logs l where l.log_event_id='loginSuccess' AND cast(l.log_event_date as date)= cast(now() as date) ");
           
          while (executeQuery.next()) {
        	   String principleName = executeQuery.getString("log_principal_name");
        	   printWriter.append(principleName+",");
        	   String date = executeQuery.getString("log_event_date");
        	   printWriter.append(date+"\n");        	   
        	 }
           c.close();
           reader.close();
           printWriter.flush();
           printWriter.close();
           return true;
        } catch (Exception e) {
           e.printStackTrace();
           System.err.println(e.getClass().getName()+": "+e.getMessage());
           System.exit(0);
        }
		return false;

	}
    
}	
