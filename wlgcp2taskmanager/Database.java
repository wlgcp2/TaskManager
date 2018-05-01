/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wlgcp2taskmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 *
 * @author William
 */
public interface Database {
    public Connection connectToDB();
    public boolean updateDB(Connection conn, PreparedStatement stmt);
}
