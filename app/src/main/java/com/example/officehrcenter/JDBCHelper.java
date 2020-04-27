package com.example.officehrcenter;

import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCHelper {
    private String URL = "jdbc:mysql://frodo.bentley.edu:3306/officehrdb";
    private String dbusername = "harry";
    private String dbpassword = "harry";

    private Statement stmt = null;
    private Connection con = null;

    public void connenctDB() {
        try { //load driver into VM memory
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            Log.e("JDBC", "Did not load driver");

        }
        try { //create connection and statement objects
            con = DriverManager.getConnection(
                    URL,
                    dbusername,
                    dbpassword);
            stmt = con.createStatement();
        } catch (SQLException e) {
            Log.e("JDBC", "problem connecting");
        }
    }

    public ResultSet select(String query) {
        ResultSet result = null;
        try {
            Log.e("JDBC", query);
            // execute SQL commands to create table, insert data, select contents
            result = stmt.executeQuery(query);
        } catch (SQLException e) {
            Log.e("JDBC", "problems with SQL sent to " + URL +
                    ": " + e.getMessage());
        }
        return result;
    }

    public int update(String query) {
        int count = 0;
        try {
            Log.e("JDBC", query);
            // execute SQL commands to create table, insert data, select contents
            count = stmt.executeUpdate(query);
        } catch (SQLException e) {
            Log.e("JDBC", "problems with SQL sent to " + URL +
                    ": " + e.getMessage());
        }
        Log.e("JDBC", "Count " + count);
        return count;
    }

    public void disConnect() {
        try { //close connection, may throw checked exception
            if (con != null)
                con.close();
        } catch (SQLException e) {
            Log.e("JDBC", "close connection failed");
        }
    }
}
