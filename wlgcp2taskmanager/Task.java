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
public final class Task {
    private int taskID;
    
    //Constructors
    public Task(int taskID, String task, String taskDueDate, Calendar taskDueDateValue, String status) {
        this.taskID = taskID;
        setTask(task);
        setDueDate(taskDueDate);
        setDueDateCalendar(taskDueDateValue);
        setStatus(status);
    }
    
    public Task(String task, LocalDate dueDateValue, int hour, int minute, String am_pm) {
        setTask(task);
        setDueDateCalendar(dueDateValue, hour, minute, am_pm);
    }
    
    //Get the tasks db ID
    public int getTaskID() {
        return taskID;
    }
    
    //Task Content String
    private StringProperty task;
    
    public String getTask() { return taskProperty().get(); }  
    
    public void setTask(String value) { taskProperty().set(value); }

    public StringProperty taskProperty() { 
        if (task == null)  task = new SimpleStringProperty(this, "task");
        return task; 
    }
      
    //Task Due Date String
    private StringProperty taskDueDate;
    
    public String getDueDate() { return taskDueDateProperty().get(); }
    
    public void setDueDate(String value) { taskDueDateProperty().set(value); }  
    
    public StringProperty taskDueDateProperty() { 
        if (taskDueDate == null) taskDueDate = new SimpleStringProperty(this, "taskDueDate");
        return taskDueDate; 
    }
    
    //Task Due Date Calender
    private Calendar taskDueDateValue;
    
    public Calendar getDueDateCalendar() { return taskDueDateValue; }
    
    public void setDueDateCalendar(Calendar date) { taskDueDateValue = date; }  
    
    public void setDueDateCalendar(LocalDate localDate, int hour, int minute, String am_pm) { 
        Date dateBuffer = Date.valueOf(localDate);
        
        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(dateBuffer);
        
        dueDate.set(Calendar.HOUR, hour);
        dueDate.set(Calendar.MINUTE, minute);
        dueDate.set(Calendar.YEAR, localDate.getYear());
        
        if(am_pm.equals("AM")) dueDate.set(Calendar.AM_PM, Calendar.AM);
        else if(am_pm.equals("PM")) dueDate.set(Calendar.AM_PM, Calendar.PM);

        taskDueDateValue = dueDate;
    }  
      
    //Task Status String
    private StringProperty taskStatus;
    
    public String getStatus() { return taskStatusProperty().get(); }
    
    public void setStatus(String value) { taskStatusProperty().set(value); }

    public StringProperty taskStatusProperty() { 
        if (taskStatus == null) taskStatus = new SimpleStringProperty(this, "taskStatus");
        return taskStatus; 
    }
}
