/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wlgcp2taskmanager;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author William
 */
public abstract class Dependencies implements Database {
    //Current user
    protected static User user;
    
    //Constructor
    public Dependencies (){
        propertyChangeSupport = new PropertyChangeSupport(this);
    }
      
    //Property change listeners
    private PropertyChangeSupport propertyChangeSupport;
    
    public void addPropertyChangeListener(PropertyChangeListener listener){
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener){
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue){
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    //Error notifications
    public void error() {
        firePropertyChange("Error", "", "Unexpected error occured.");
    }
    
    public void dbError() {
        firePropertyChange("Error", "", "Database error occured.");
    }
    
    public void connectionError() {
        firePropertyChange("Error", "", "Cannot reach database.");
    }
    
    public void sqlDriverError() {
        firePropertyChange("Error", "", "JDBC driver not found.");
    }
    
    
    //Database Logic
    private final String url = "ec2-52-14-189-142.us-east-2.compute.amazonaws.com";
    private final String dbUsername = "RemoteUser";
    private final String dbPassword = "wlgcp2";
    private final String dbName = "CS3330";
    
    private Connection connection;
    
    protected Connection conn;
    
    private String hashOutput = null;
    
    @Override
    public Connection connectToDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            sqlDriverError();
            return null;
        }
        
        connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + url + ":3306/" + dbName, dbUsername, dbPassword);
        } 
        catch (SQLException e) {
            System.out.println("Connection Failed!:\n" + e.getMessage());
            connectionError();
            return null;
        }

        return connection;         
    }
    
    @Override
    public boolean updateDB(Connection conn, PreparedStatement stmt) {
        boolean check = false;       

        try {           
            stmt.executeUpdate();          
            check = true;
        } 
        catch (SQLException se) {
            se.printStackTrace();
            System.out.println(se); 
            dbError();
            check = false;
        } 
        finally {
            try {
                if (stmt != null)
                    stmt.close();
            } 
            catch (Exception e) {
                e.printStackTrace();
                System.out.println(e);
            }
            
            try {
                if (conn != null)
                    conn.close();
            } 
            catch (Exception e) { 
                e.printStackTrace();
                System.out.println(e); 
            }
        }
        
        return check;
    }
    
    //Hash password function
    public String hashPassword(String password) {       
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
	    
            digest.reset();
	    digest.update(password.getBytes("utf8"));
	    
            hashOutput = String.format("%040x", new BigInteger(1, digest.digest()));
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e); 
            return null;
        }
        
        return hashOutput;
    }   
}
