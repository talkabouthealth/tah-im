package com.tah.im;

import java.sql.*;
import java.util.Date;

public class SQL_CON {

	//login info
	private String db_host=null;
	private String db_name=null;
	private String db_user=null;
	private String db_password=null;
	//DB Statement
	private String _sql=null;
	private Connection con=null;
	private Statement sqlStatement=null;
	private PreparedStatement pst = null;
	private ResultSet rs=null;

	//default constructor
	public SQL_CON() throws Exception{
		this.db_host = "localhost";
		this.db_name = "talkmidb";
		this.db_user = "root";
		this.db_password = "dvl";
		con = setCon(db_host, db_name, db_user, db_password);
	}
	
	//Constructor with command
	public SQL_CON(String host, String database, String user, String password, String sql) throws Exception{

		db_host = host;
		db_name = database;
		db_user = user;
		db_password = password;
		_sql = sql;
		con = setCon(db_host, db_name, db_user, db_password);
		sqlStatement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		rs = sqlStatement.executeQuery(_sql);
		//this.Close();
	}
	public Connection getCon(){
		return con;
	}
	public Statement getStatement(){
		return sqlStatement;
	}
	public String getSql(){
		return _sql;
	}

	private Connection setCon (String host, String database, String user, String password)
    	throws Exception {
    
          String url = "";
          try {
        	  url = "jdbc:mysql://" + host + ":3306/" + database;
        	  	Connection con = DriverManager.getConnection(url, user, password);
                System.out.println("Connection established to " + url + "...");
                return con;
          } catch (java.sql.SQLException e) {
         System.out.println("Connection couldn't be established to " + url);
          throw (e);
          }
    }
    public boolean driverTest () throws Exception {
        try {
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("MySQL Driver Found");
                return true;
        } catch (java.lang.ClassNotFoundException e) {
                System.out.println("MySQL JDBC Driver not found ... ");
                throw (e);
        }
     }
    public ResultSet getRS(){
     return rs;
    }
    
    public void InsertToNoti(int uid, int topic_id, int sc) throws SQLException{
    	final String tableName = "noti_history";
    	String insertdbSQL = "insert into " + tableName +
        " select ifNULL(max(noti_hist_id),0)+1,?,?,?,? FROM "+ tableName;
    	
    	//delete data of the same uid
    	String deletedbSQL = "delete from " + tableName + " WHERE uid = " + uid;
    	sqlStatement = con.createStatement();
    	sqlStatement.executeUpdate(deletedbSQL);
    	
    	Timestamp t = new Timestamp(new Date().getTime()); // get current time
    	
    	pst = con.prepareStatement(insertdbSQL);	
        pst.setTimestamp(1, t);
        pst.setInt(2, uid);
        pst.setInt(3, topic_id);
        pst.setInt(4, sc);
        pst.executeUpdate();
    	
    	Close();
    }
        
    private void Close()
    {
      try
      {
        if(rs!=null)
        {
          rs.close();
          rs = null;
        }
        if(sqlStatement!=null)
        {
        	sqlStatement.close();
        	sqlStatement = null;
        }
        if(pst!=null)
        {
          pst.close();
          pst = null;
        }
      }
      catch(SQLException e)
      {
        System.out.println("Close Exception :" + e.toString());
      }
    }
    
    public void CloseLink(){
    	if(con!=null){
    		try {
				con.close();
				con = null;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    }
    
}