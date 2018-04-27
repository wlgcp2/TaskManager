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
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author William
 */
public class TaskManagerController extends CommonUI implements Initializable, PropertyChangeListener {
    private Stage stage; 
    public Scene LoginScene; 
    public LoginController LoginController; 
    
    private TaskManagerModel taskManagerModel;
    
    @FXML private TableView<Task> taskTable;
    
    @FXML private TableColumn taskColumn;
    @FXML private TableColumn dueDateColumn;
    @FXML private TableColumn statusColumn;
    
    @FXML
    public void completeTaskButton() {
        if (taskTable.getSelectionModel().getSelectedItem() != null)
            taskManagerModel.completeTask(taskTable.getSelectionModel().getSelectedItem());
    }
    
    @FXML
    public void incompleteTaskButton() {
        if (taskTable.getSelectionModel().getSelectedItem() != null)
            taskManagerModel.incompleteTask(taskTable.getSelectionModel().getSelectedItem());
    }
    
    @FXML
    private void deleteTaskButton(ActionEvent event) {
        if (taskTable.getSelectionModel().getSelectedItem() != null)
            taskManagerModel.deleteTask(taskTable.getSelectionModel().getSelectedItem());
    }
    
    @FXML
    private void addTaskButton() {      
        Dialog<Task> newTaskDialog = new Dialog<>();
        newTaskDialog.setTitle("Add Task");
        
        DialogPane dialogPane = newTaskDialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        TextField taskInput = new TextField();
        taskInput.setPrefWidth(300);
        Label taskLabel = new Label("Task: ");
        
        DatePicker dueDate = new DatePicker(LocalDate.now());
        Label dateLabel = new Label("Due Date: ");
        
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        
        gridPane.add(taskLabel, 0, 0);
        gridPane.add(taskInput, 1, 0);
        gridPane.add(dateLabel, 0, 1);
        gridPane.add(dueDate, 1, 1);
 
        
        dialogPane.setContent(gridPane);
        
        newTaskDialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Task(taskInput.getText(), dueDate.getValue());
            }
            return null;
        });
        
        Optional <Task> optionalResult = newTaskDialog.showAndWait();
        optionalResult.ifPresent((newTask) -> { taskManagerModel.addTask(newTask); });
    }
    
    public void start(Stage stage, User user){
        this.stage = stage; 
        stage.setTitle("Task Manager for " + user.getFirstName() + " " + user.getLastName());

        TaskManagerModel.setUser(user);
        
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
            ObservableList<Task> tasks = (ObservableList<Task>)evt.getNewValue();
            taskTable.setItems(tasks);
        }
        
        if(evt.getPropertyName().equals("Error")){
            errorPopup((String) evt.getNewValue());
        }
    }
    
}
