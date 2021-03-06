/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wlgcp2taskmanager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author William
 */
public class LoginController extends CommonUI implements Initializable, PropertyChangeListener {
    private Scene newUserScene; 
    private NewUserController newUserController; 
    
    private Scene taskManagerScene; 
    private TaskManagerController taskManagerController; 
    
    private LoginModel loginModel;   
    
    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;
    
    @FXML private Text error;
    
    @FXML //Try login from form
    private void loginButton() {
        loginModel.login(loginUsername.getText(), loginPassword.getText());
    }
    
    @FXML //Switch to new user form
    private void newUserButton() {       
        try {
            if(newUserScene == null){               
                FXMLLoader loader = new FXMLLoader(getClass().getResource("NewUser.fxml"));
                Parent newUserRoot = loader.load(); 
                newUserController = loader.getController(); 
                newUserController.loginScene = loginScene; 
                newUserScene = new Scene(newUserRoot); 
            }
        } catch (Exception ex) {
            System.out.println(ex); 
        }
        
        stage.setScene(newUserScene); 
        newUserController.start(stage);
    }
    
    //Switch to main task manager
    private void startTaskManager() {
        try {
            if(taskManagerScene == null){               
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskManager.fxml"));
                Parent newUserRoot = loader.load(); 
                taskManagerController = loader.getController(); 
                taskManagerController.loginScene = loginScene; 
                taskManagerScene = new Scene(newUserRoot); 
            }
        } catch (Exception ex) {
            System.out.println(ex); 
        }
        
        stage.setScene(taskManagerScene);  
        
        taskManagerController.start(stage);
    }
    
    @Override
    public void start(Stage stage) {
        this.stage = stage; 
        loginScene = stage.getScene(); 
        stage.setTitle("Login");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loginModel = new LoginModel();
        loginModel.addPropertyChangeListener(this);
    } 
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("FormStatus")){
            error.setText((String) evt.getNewValue());
        }
        
        if(evt.getPropertyName().equals("LoginSuccessful")){
            startTaskManager();
        }
        
        if(evt.getPropertyName().equals("Error")){
            errorPopup((String) evt.getNewValue());
        }
    }   
}
