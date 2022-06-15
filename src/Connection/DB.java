/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;


/**
 *
 * @author Bule
 */
public class DB {
    static Connection Koneksi;
    public static Connection getConnection(){
        try{
              Class.forName("com.mysql.jdbc.Driver");
              Koneksi = DriverManager.getConnection("jdbc:mysql://localhost/inventory","root","");
              
        } catch (Exception e){
             JOptionPane.showMessageDialog(null, "Koneksi Database Gagal");
        }
        return Koneksi;
    }
    
}
