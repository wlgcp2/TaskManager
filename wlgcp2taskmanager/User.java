/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wlgcp2taskmanager;

/**
 *
 * @author William
 */
public class User {
    private Integer id;
    private String username;
    private String firstName;
    private String lastName;
    
    public User(Integer id, String username, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public int getID() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
}
