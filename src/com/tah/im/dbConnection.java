package com.tah.im;
import java.sql.*;



public class dbConnection {
    /**
     * @see HttpServlet#HttpServlet()
     */
	private String db_host;
	private String db_user;
	private String db_password;
	private Connection con;
	private Statement stmt;
	private ResultSet rs;	

    // Constructor
    public dbConnection() throws SQLException {
        
        // TODO Auto-generated constructor stub

        db_host = "jdbc:mysql://localhost:3306/talkmidb";
        db_user = "talkmidb";
        db_password = "applepie";
        con = DriverManager.getConnection(db_host, db_user, db_password);
        stmt = con.createStatement();
    }

    public dbConnection(String _sql) throws SQLException {
        
        // TODO Auto-generated constructor stub

        db_host = "jdbc:mysql://localhost:3306/talkmidb";
        db_user = "talkmidb";
        db_password = "applepie";
        con = DriverManager.getConnection(db_host, db_user, db_password);
        stmt = con.createStatement();
        setRs(_sql);
    }
	// Return connection 
	public Connection getCon(){
		return con;
	}
	// Setting resultset
	public void setRs(String _sqlStmt) throws SQLException{
		rs =  stmt.executeQuery(_sqlStmt);	
	}
	// Return resultset
	public ResultSet getRs(){
		return rs;
	}

}
