/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wlgcp2taskmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author William
 */
public class TaskManagerModel extends Dependencies {
    static User user;
    
    private ObservableList<Task> tasks;
    
    public TaskManagerModel() {
    }
    
    public static void setUser(User userObject) {
        user = userObject;
    }
    
    public void loadTasks() { 
        try {
            tasks = getAllTasks(conn);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e); 
        }
            
        firePropertyChange("UpdateTasks", null, tasks);             
    }
    
    //Query db for a users tasks
    public ObservableList<Task> getAllTasks(Connection conn) { 
        int userID = user.getID();
        int taskID;
        String task;
        String addDate;
        String taskDueDate;
        boolean taskStatusBool;
        String taskStatus = null;
        
        PreparedStatement stmt = null;

        if(tasks != null) tasks.clear();
        
        tasks = FXCollections.observableArrayList();
        
        try {
            conn = connectToDB();
            
            stmt = conn.prepareStatement("SELECT tasks.id, task, tasks.addDate, date_format(taskDueDate,'%b %D') as dueDate, taskStatus FROM tasks WHERE tasks.userID = ?");
            stmt.setInt(1, userID);
            
            ResultSet rs = stmt.executeQuery();
            
            if(rs != null){
                while (rs.next()) {
                
                    taskID = rs.getInt("id");
                    task = rs.getString("task");
                    addDate = rs.getString("addDate");
                    taskDueDate = rs.getString("dueDate");
                    taskStatusBool = rs.getBoolean("taskStatus");
                    
                    if(!taskStatusBool) { taskStatus = "Incomplete"; }
                    else if(taskStatusBool) { taskStatus = "Completed"; }

                    tasks.add(new Task(taskID, task, taskDueDate, taskStatus));
                }
            }           
        } 
        catch (SQLException se) {
            se.printStackTrace();
            System.out.println(se); 
            dbError();
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            dbError();
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
        
        return tasks;
    }
    
    //Add a task to the db
    public void addTask(Task newTask) {
        boolean check = false;
        
        PreparedStatement stmt = null;
        
        try {
            conn = connectToDB();
            
            Timestamp dueDate = new java.sql.Timestamp(newTask.getDueDateValue().getTimeInMillis());
            
            stmt = conn.prepareStatement("INSERT INTO tasks (userID, task, addDate, taskDueDate, taskStatus) values (?, ?, NOW(), ?, 0);");
            stmt.setInt(1, user.getID());
            stmt.setString(2, newTask.getTask());
            stmt.setTimestamp(3, dueDate);
            
            updateDB(conn, stmt);
            
            check = true;
        }
        catch (Exception e) {
            dbError();
            check = false;
        }
        
        //Reload tasks
        if(check == true) {
            loadTasks();
        }
    }

    //Remove a task from the db
    public void deleteTask(Task task) {
        boolean check = false;
        
        PreparedStatement stmt = null;
        
        try {
            conn = connectToDB();
            
            stmt = conn.prepareStatement("DELETE FROM tasks WHERE id = ? AND userID = ?");
            stmt.setInt(1, task.getTaskID());
            stmt.setInt(2, user.getID());
            
            check = updateDB(conn, stmt);
        }
        catch (Exception e) {
            dbError();
            return;
        }
            
        if(check == true) {
            tasks.remove(task);
            firePropertyChange("UpdateTasks", null, tasks);
        }
    }
    
    //Mark a task as completed
    public void completeTask(Task task) {     
        if(updateTaskStatus(task, 1)) {
            task.setStatus("Completed");
            firePropertyChange("UpdateTasks", null, tasks);
        } 
    }
    
    //Mark a task as incomplete
    public void incompleteTask(Task task) {     
        if(updateTaskStatus(task, 0)) {
            task.setStatus("Incomplete");
            firePropertyChange("UpdateTasks", null, tasks);
        }
    }
    
    //Update task status
    private boolean updateTaskStatus(Task task, int status) {
        boolean check = false;
        
        PreparedStatement stmt = null;
        
        try {
            conn = connectToDB();
            
            stmt = conn.prepareStatement("UPDATE tasks SET taskStatus = ? WHERE id = ? AND userID = ?");
            stmt.setInt(1, status);
            stmt.setInt(2, task.getTaskID());
            stmt.setInt(3, user.getID());
            
            check = updateDB(conn, stmt);
        }
        catch (Exception e) {
            dbError();
            check = false;
        }
   
        return check;
    }
    
}
