/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wlgcp2taskmanager;


import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author William
 */
public class NewUserModel extends Dependencies {
    
    private String hashedPassword;
    
    public void createUser(String username, String password1, String password2, String firstName, String lastName) {
        
        //Check if form is complete
        if(!checkInput(username, password1, password2, firstName, lastName)) {
            firePropertyChange("FormStatus", "", "Please complete form!");
            return;
        }
        
        //Check if passwords match
        if(!password1.equals(password2)) {
            firePropertyChange("FormStatus", "", "Passwords must match!");
            return;
        }
        
        //Hash password
        if(hashPassword(password1) != null){
            hashedPassword = hashPassword(password1);
        }
        else {
            error();
            return;
        }
        
        //Insert user into db
        if(insertUser(username, hashedPassword, firstName, lastName)){
            firePropertyChange("FormStatus", "", "User created!");
        }
        else { error(); }
 
    }
    
    //Try to insert user
    private boolean insertUser(String username, String hashedPassword, String firstName, String lastName) {
        boolean check = false;
        
        PreparedStatement stmt = null;
        
        try {
            conn = connectToDB();
            
            stmt = conn.prepareStatement("INSERT INTO users (username, password, firstName, lastName, addDate) values (?, ?, ?, ?, NOW())");
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, firstName);
                stmt.setString(4, lastName);

            check = updateDB(conn, stmt);
        } 
        catch (SQLException se) {
            se.printStackTrace();
            System.out.println(se); 
            dbError();
        }
        
        return check;
    }
    
    //Check if form is complete 
    private boolean checkInput(String username, String password1, String password2, String firstName, String lastName) {
        if(username == null || username.equals("")) return false; 
        if(password1 == null || password1.equals("")) return false;
        if(password2 == null || password2.equals("")) return false;
        if(firstName == null || firstName.equals("")) return false;
        if(lastName == null || lastName.equals("")) return false;             
        return true;
    }   
}
