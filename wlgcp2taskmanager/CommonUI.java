/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wlgcp2taskmanager;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 *
 * @author William
 */
public abstract class CommonUI {
    protected Stage stage; 
    
    protected Scene loginScene;
    
    //Display error popup
    public void errorPopup(String error) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(error);

        alert.showAndWait();
    }
    
    public abstract void start(Stage stage);
}
