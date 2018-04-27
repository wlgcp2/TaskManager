/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wlgcp2taskmanager;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author William
 */
public class Task {
    private int taskID;
    
    public Task(int taskID, String task, String taskDueDate, String status) {
        this.taskID = taskID;
        setTask(task);
        setDueDate(taskDueDate);
        setStatus(status);
    }
    
    public Task(String task, LocalDate dueDateValue) {
        setTask(task);
        setDueDateLocal(dueDateValue);
    }
    
    public int getTaskID() {
        return taskID;
    }
    
    //Task Content String
    private StringProperty task;
    
    public String getTask() { return taskProperty().get(); }  
    
    public void setTask(String val) { taskProperty().set(val); }

    public StringProperty taskProperty() { 
        if (task == null)  task = new SimpleStringProperty(this, "task");
        return task; 
    }
      
    //Task Due Date String
    private StringProperty taskDueDate;
    
    public String getDueDate() { return taskDueDateProperty().get(); }
    
    public void setDueDate(String val) { taskDueDateProperty().set(val); }  
    
    public StringProperty taskDueDateProperty() { 
        if (taskDueDate == null) taskDueDate = new SimpleStringProperty(this, "taskDueDate");
        return taskDueDate; 
    }
    
    //Task Due Date Calender
    private Calendar taskDueDateValue;
    
    public Calendar getDueDateValue() { return taskDueDateValue; }
    
    public void setDueDateValue(Calendar date) { taskDueDateValue = date; }  
    
    public void setDueDateLocal(LocalDate localDate) { 
        Date dateBuffer = Date.valueOf(localDate);
        
        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(dateBuffer);
        
        taskDueDateValue = dueDate;
    }  
      
    //Task Status String
    private StringProperty taskStatus;
    
    public String getStatus() { return taskStatusProperty().get(); }
    
    public void setStatus(String val) { taskStatusProperty().set(val); }

    public StringProperty taskStatusProperty() { 
        if (taskStatus == null) taskStatus = new SimpleStringProperty(this, "taskStatus");
        return taskStatus; 
    }
}
