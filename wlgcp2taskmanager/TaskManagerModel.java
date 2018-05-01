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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author William
 */
public class TaskManagerModel extends Dependencies {
    private ObservableList<Task> tasks;
    
    private final String about = " - This application is to help you manage your to-do list.\n"
                        + " - All information is stored in a remote database so that it can be accessed from any computer connected to the internet.\n"
                        + " - To add a new task, click on New -> Task and enter the details about your task.\n"
                        + " - To edit a task's status or to delete the task, simply right click on the task.\n\n"
                        + "   Created by William Givens";
    
    public static void setUser(User userObject) {
        user = userObject;
    }
    
    public void logout() {
        user = null;
        tasks = null;
        firePropertyChange("UpdateTasks", null, tasks);        
    }
    
    public void loadTasks() { 
        try {
            tasks = getAllTasks(user);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e); 
            return;
        }
            
        firePropertyChange("UpdateTasks", null, tasks);             
    }
    
    //Query db for a users tasks
    private ObservableList<Task> getAllTasks(User user) { 
        int taskID;
        String task;
        String addDate;
        String taskDueDate;
        String calendarTaskDueDate, calendarAMPM;
        boolean taskStatusBool;
        String taskStatus = null;
        
        SimpleDateFormat calendarFormat = new SimpleDateFormat("MMM dd HH:mm yyyy");      
        
        PreparedStatement stmt = null;

        if(tasks != null) tasks.clear();
        
        tasks = FXCollections.observableArrayList();
        
        try {
            conn = connectToDB();
            
            stmt = conn.prepareStatement("SELECT tasks.id, task, tasks.addDate, date_format(taskDueDate,'%b %D at %l:%i %p') as dueDate, date_format(taskDueDate,'%b %d %l:%i %Y') as calendarDueDate, date_format(taskDueDate,'%p') as calendarAMPM, taskStatus FROM tasks WHERE tasks.userID = ? ORDER BY taskDueDate ASC");
                stmt.setInt(1, user.getID());
            
            ResultSet rs = stmt.executeQuery();
            
            if(rs != null){
                while (rs.next()) {
                    taskID = rs.getInt("id");
                    task = rs.getString("task");
                    addDate = rs.getString("addDate");
                    taskDueDate = rs.getString("dueDate");
                    calendarTaskDueDate = rs.getString("calendarDueDate");
                    calendarAMPM = rs.getString("calendarAMPM");
                    taskStatusBool = rs.getBoolean("taskStatus");
                    
                    //Convert status bool to string
                    if(!taskStatusBool) { taskStatus = "Incomplete"; }
                    else if(taskStatusBool) { taskStatus = "Completed"; }
                    
                    //Set time as AM or PM
                    Calendar taskDueDateValue = Calendar.getInstance();
                             
                    try { 
                        taskDueDateValue.setTime(calendarFormat.parse(calendarTaskDueDate)); 
                        if(calendarAMPM.equals("AM")) taskDueDateValue.set(Calendar.AM_PM, Calendar.AM);
                        else if(calendarAMPM.equals("PM")) taskDueDateValue.set(Calendar.AM_PM, Calendar.PM);
                    }
                    catch (ParseException e) { error(); }
                    
                    //Add the task to the observable list
                    tasks.add(new Task(taskID, task, taskDueDate, taskDueDateValue, taskStatus));
                    
                    taskDueDateValue = null;
                }
            }           
        } 
        catch (SQLException se) {
            se.printStackTrace();
            System.out.println(se); 
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
            
            calendarFormat = null;
        }
        
        return tasks;
    }
    
    
    //Add a task to the db
    public void addTask(Task newTask) {
        boolean check = false;
        
        Timestamp dueDate = new java.sql.Timestamp(newTask.getDueDateCalendar().getTimeInMillis());
        
        PreparedStatement stmt = null;
        
        try {
            conn = connectToDB();
            
            stmt = conn.prepareStatement("INSERT INTO tasks (userID, task, addDate, taskDueDate, taskStatus) values (?, ?, NOW(), ?, 0);");
                stmt.setInt(1, user.getID());
                stmt.setString(2, newTask.getTask());
                stmt.setTimestamp(3, dueDate);
            
            check = updateDB(conn, stmt);;
        }
        catch (SQLException se) {
            se.printStackTrace();
            System.out.println(se); 
            dbError();
        }
        finally {
            dueDate = null;
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
        catch (SQLException se) {
            se.printStackTrace();
            System.out.println(se); 
            dbError();
            check = false;
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
        catch (SQLException se) {
            se.printStackTrace();
            System.out.println(se); 
            dbError();
            check = false;
        }
   
        return check;
    }   
    
    
    //Edit task 
    public void editTask(Task task) {
        boolean check = false;
        
        Timestamp dueDate = new java.sql.Timestamp(task.getDueDateCalendar().getTimeInMillis());
        
        PreparedStatement stmt = null;
        
        try {
            conn = connectToDB();
 
            stmt = conn.prepareStatement("UPDATE tasks SET task = ?, taskDueDate = ? WHERE tasks.id = ? AND userID = ?");
                stmt.setString(1, task.getTask());
                stmt.setTimestamp(2, dueDate);
                stmt.setInt(3, task.getTaskID());
                stmt.setInt(4, user.getID());
            
            check = updateDB(conn, stmt);
        }
        catch (SQLException se) {
            se.printStackTrace();
            System.out.println(se); 
            dbError();
            check = false;
        }
        finally {
            dueDate = null;
        }
   
        if(check == true) {
            loadTasks();
        }
    }
    
    public String getAbout() {
        return about;
    }
}
