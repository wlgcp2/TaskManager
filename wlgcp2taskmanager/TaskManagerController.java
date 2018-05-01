/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wlgcp2taskmanager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author William
 */
public class TaskManagerController extends CommonUI implements Initializable, PropertyChangeListener {
    private TaskManagerModel taskManagerModel;
    
    @FXML private TableView<Task> taskTable;
    
    @FXML private TableColumn taskColumn;
    @FXML private TableColumn dueDateColumn;
    @FXML private TableColumn statusColumn;
    
    @FXML
    private void completeTaskButton() {
        if (taskTable.getSelectionModel().getSelectedItem() != null)
            taskManagerModel.completeTask(taskTable.getSelectionModel().getSelectedItem());
    }
    
    @FXML
    private void incompleteTaskButton() {
        if (taskTable.getSelectionModel().getSelectedItem() != null)
            taskManagerModel.incompleteTask(taskTable.getSelectionModel().getSelectedItem());
    }
    
    @FXML
    private void deleteTaskButton() {
        if (taskTable.getSelectionModel().getSelectedItem() != null)
            taskManagerModel.deleteTask(taskTable.getSelectionModel().getSelectedItem());
    }
    
    @FXML
    private void editTaskButton() {
        if (taskTable.getSelectionModel().getSelectedItem() != null)
            addTask(true, taskTable.getSelectionModel().getSelectedItem());
    }
    
    @FXML
    private void addTaskButton() { 
        addTask(false, null);
    }
    
    //Add task and edit task popup
    private void addTask(boolean edit, Task task) {      
        Dialog<Task> newTaskDialog = new Dialog<>();
        
        //Page Title
        if(edit == false) newTaskDialog.setTitle("Add Task");
        else if(edit == true) newTaskDialog.setTitle("Edit Task");
        
        //Dialog Pane
        DialogPane taskDialogPane = newTaskDialog.getDialogPane();
        taskDialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        //Task text field
        TextField taskInput = new TextField();
        taskInput.setPrefWidth(300);
        
        if(edit == true) taskInput.setText(task.getTask());
        
        Label taskLabel = new Label("Task: ");
        
        //Date picker
        DatePicker dueDate = new DatePicker();
        
        if(edit == false) dueDate.setValue(LocalDate.now());
        else if(edit == true) dueDate.setValue( LocalDate.of(
                        task.getDueDateCalendar().get(Calendar.YEAR), 
                        task.getDueDateCalendar().get(Calendar.MONTH) + 1, 
                        task.getDueDateCalendar().get(Calendar.DAY_OF_MONTH))
        );
        
        Label dateLabel = new Label("Due Date: ");
        
        //Hour picker
        final ComboBox hourComboBox = new ComboBox();
        hourComboBox.getItems().addAll( "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"); 
        
        if(edit == false) hourComboBox.getSelectionModel().selectFirst();
        else if(edit == true) {
            if(task.getDueDateCalendar().get(Calendar.HOUR) == 0) hourComboBox.setValue("12");
            else hourComboBox.setValue(Integer.toString(task.getDueDateCalendar().get(Calendar.HOUR)));
        }
        
        //Minute picker
        final ComboBox minuteComboBox = new ComboBox();
        minuteComboBox.getItems().addAll( "00", "15", "30", "45"); 
        
        if(edit == false) minuteComboBox.getSelectionModel().selectFirst();
        else if(edit == true){
            if(task.getDueDateCalendar().get(Calendar.MINUTE) == 0) minuteComboBox.setValue("00");
            else minuteComboBox.setValue(Integer.toString(task.getDueDateCalendar().get(Calendar.MINUTE)));
        }
        
        //AM PM picker
        final ComboBox am_pmComboBox = new ComboBox();
        am_pmComboBox.getItems().addAll(
            "AM", "PM" 
        );
        
        if(edit == false) am_pmComboBox.getSelectionModel().selectFirst();
        else if(edit == true) {
            if(task.getDueDateCalendar().get(Calendar.AM_PM) == Calendar.AM) am_pmComboBox.setValue("AM");
            else if(task.getDueDateCalendar().get(Calendar.AM_PM) == Calendar.PM) am_pmComboBox.setValue("PM");
        }
        
        Label timeLabel = new Label("Time Due: ");
        Label timeColon = new Label(" : ");
        Label am_pmPad = new Label(" ");
        
        //HBox declaration
        HBox timeSelection = new HBox();
        timeSelection.getChildren().addAll(hourComboBox, timeColon, minuteComboBox, am_pmPad, am_pmComboBox);
        
        //Grid pane declaration
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        
        gridPane.add(taskLabel, 0, 0);
        gridPane.add(taskInput, 1, 0);
        gridPane.add(dateLabel, 0, 1);
        gridPane.add(dueDate, 1, 1);
        gridPane.add(timeLabel, 0, 2);
        gridPane.add(timeSelection, 1, 2);
      
        taskDialogPane.setContent(gridPane);
        
        //Button result
        newTaskDialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                if(edit == false){
                    return new Task(
                        taskInput.getText(), 
                        dueDate.getValue(), 
                        Integer.valueOf((String) hourComboBox.getValue()), 
                        Integer.valueOf((String) minuteComboBox.getValue()), 
                        (String) am_pmComboBox.getValue());
                }
                else if(edit == true) {
                    task.setTask(taskInput.getText()); 
                    task.setDueDateCalendar(dueDate.getValue(), Integer.valueOf((String) hourComboBox.getValue()), Integer.valueOf((String) minuteComboBox.getValue()), (String) am_pmComboBox.getValue()); 
                    return task;
                }
            }
            return null;
        });
        
        Optional <Task> optionalResult = newTaskDialog.showAndWait();
        
        //If there is a result
        if(edit == false) optionalResult.ifPresent((newTask) -> { taskManagerModel.addTask(newTask); });
        else if(edit == true) optionalResult.ifPresent((editedTask) -> { taskManagerModel.editTask(editedTask); });
    }
    
    
    @FXML //About popup
    private void aboutButton() {
        Dialog aboutDialog = new Dialog();
        aboutDialog.setTitle("About");
        
        DialogPane aboutDialogPane = aboutDialog.getDialogPane();
        aboutDialogPane.getButtonTypes().addAll(ButtonType.OK);
        
        Text aboutText = new Text(taskManagerModel.getAbout());
        
        aboutText.setLineSpacing(5);
        
        aboutDialogPane.setContent(aboutText);
        
        aboutDialog.showAndWait();     
    }
    
    @FXML
    private void logoutButton() {
        taskManagerModel.logout();
        
        stage.setScene(loginScene);
        stage.setTitle("Login");
    }
    
    @Override
    public void start(Stage stage){
        this.stage = stage; 
        stage.setTitle("Task Manager for " + TaskManagerModel.user.getFirstName() + " " + TaskManagerModel.user.getLastName());
        
        taskManagerModel.addPropertyChangeListener(this);
        
        taskManagerModel.loadTasks();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        taskManagerModel = new TaskManagerModel();
        
        taskColumn.setCellValueFactory(new PropertyValueFactory<>("task"));      
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("taskDueDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("taskStatus"));
        
        taskTable.getColumns().setAll(taskColumn, dueDateColumn, statusColumn);
    }    
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) { 
        if(evt.getPropertyName().equals("UpdateTasks")){
            taskTable.setItems((ObservableList<Task>)evt.getNewValue());
        }
        
        if(evt.getPropertyName().equals("Error")){
            errorPopup((String) evt.getNewValue());
        }
    }  
}
