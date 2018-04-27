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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author William
 */
public class NewUserController extends CommonUI implements Initializable, PropertyChangeListener {
    private Stage stage; 
    public Scene LoginScene; 
    public LoginController LoginController; 
    
    private NewUserModel newUserModel;
    
    @FXML private TextField newUsername;  
    @FXML private PasswordField newPassword1;   
    @FXML private PasswordField newPassword2;  
    @FXML private TextField newFirstName;  
    @FXML private TextField newLastName;
    
    @FXML private Text error;
    
 
    @FXML
    private void createAccountButton(ActionEvent event) {
        newUserModel.createUser(newUsername.getText(), newPassword1.getText(), newPassword2.getText(), newFirstName.getText(), newLastName.getText());
    }
    
    @FXML
    private void exitButton(ActionEvent event) {  
        clear();
        stage.setScene(LoginScene);
        stage.setTitle("Login");
    }
    
    //Clear form
    private void clear() {
        newUsername.setText("");
        newPassword1.setText("");
        newPassword2.setText("");
        newFirstName.setText("");
        newLastName.setText("");
        error.setText("");
    }
    
    
    public void start(Stage stage){
        this.stage = stage; 
        stage.setTitle("Create Account");
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        newUserModel = new NewUserModel();
        newUserModel.addPropertyChangeListener(this);
    }   
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("FormStatus")){
            error.setText((String) evt.getNewValue());
        }
        
        if(evt.getPropertyName().equals("Error")){
            errorPopup((String) evt.getNewValue());
        }
    }   
}
