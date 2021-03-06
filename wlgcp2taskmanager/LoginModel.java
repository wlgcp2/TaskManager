/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wlgcp2taskmanager;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author William
 */
public class LoginModel extends Dependencies {
    private int userID;
    private String username;
    private String password, hashedPassword;
    private String firstName, lastName;
    
    public void login(String username, String password) {
        this.username = username;
        this.password = password;
        
        //Check if form is complete
        if(!checkInput()) {
            firePropertyChange("FormStatus", "", "Please complete form!");
            return;
        }
        
        //Hash password
        if(hashPassword(password) != null){
            hashedPassword = hashPassword(password);
        }
        else {
            error();
            return;
        }

        //Check credentials in db
        if(tryLogin(this.username, hashedPassword)){
            firePropertyChange("FormStatus", null, "");
            user = new User(userID, username, firstName, lastName);
            firePropertyChange("LoginSuccessful", null, true);
        }
        else { firePropertyChange("FormStatus", "", "Incorrect username or password"); }          
    }
    
    //Query db for user
    private boolean tryLogin(String username, String password) {
        boolean login = false;
        
        PreparedStatement stmt = null;
        
        try {
            conn = connectToDB();
            
            stmt = conn.prepareStatement("SELECT id, firstName, lastName FROM users WHERE username = ? AND password = ?");
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
            
            ResultSet rs = stmt.executeQuery();
            
            if (!rs.next()) {
                login = false;
            }
            else {
                login = true;
                userID = rs.getInt("id");
                firstName = rs.getString("firstName");
                lastName = rs.getString("lastName");
            }
        } 
        catch (SQLException se) {
            se.printStackTrace();
            System.out.println(se);
            dbError();
            login = false;
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
        
        return login;
    }
    
    //Check if form is complete
    private boolean checkInput() {
        if(username == null || username.equals("")) return false;
        if(password == null || password.equals("")) return false;

        return true;
    }
}
