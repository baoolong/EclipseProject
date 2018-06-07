package com.helloword.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;

import com.helloword.bin.PageInfoBin;


public class CrawlerDao {

	// 数据库连接的 几个属性
	private static final String dirverName = "oracle.jdbc.driver.OracleDriver";
	private static final String jdbcurl = "jdbc:oracle:thin:@localhost:1521:orcl";
	private static final String username = "C##yworcale";
	private static final String password = "lgyw-1236987";
//	private static final String username = "C##dongbaolong7758";
//	private static final String password = "womeiyoumima7758";
	private static final String TABLE_NAME_PAGE = "PAGE_DRESS_STORE";
	private static final String TABLE_NAME_PIC = "PIC_DRESS_STORE";
	
	/**
	 * 获取连接对象
	 */
	private  Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName(dirverName);
			connection = DriverManager.getConnection(jdbcurl, username, password);
		} catch (Exception e) {
			System.out.println("数据库连接异常！");
			e.printStackTrace();
		}
		return connection;
	}
	
	
	
	
	
	/**
	 * 在数据库存贮图片路径
	 * @param URLS
	 */
	public void SavePics(String URL){
		if(URL==null) 
			return;
		// JDBC程序的3个接口： 连接对象， SQL执行容器， 结果集
		Connection connection = null;	
		PreparedStatement  statement = null;
		/*	INSERT INTO PAGE_DRESS_STORE(ADDRESS,CHECKSTATE,MDSNAMES) VALUES('张飞',1,'666');
		 * 	ADDRESS varchar2(200) not null,  --网址  200表示能存100汉字（汉字占2位）,200字母
		 *	CHECKSTATE number(2),            --是否已经检索的状态 0未检索  1已检索
   		 *	DOWNLOADSTATE number(2) not null  --是否下载
		 */
		
		try {
			connection=getConnection();
			//将事务模式设置为手动提交事务：
			//connection.setAutoCommit(false);
            //设置事务的隔离级别。
			//connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			String sqlStr="INSERT INTO "+TABLE_NAME_PIC+"(ADDRESS,CHECKSTATE,DOWNLOADSTATE) "
					+ "VALUES('"+URL+"',0,0)";
			statement = connection.prepareStatement(sqlStr);
			statement.executeUpdate();
			//connection.commit();
			System.out.println("新图片："+URL);
		} catch(Exception e){
			//e.printStackTrace();
		}finally {
			try {
				connection.setAutoCommit(true);
				if(statement!=null)
					statement.close();
				connection.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	
	
	/**
	 * 插入新的网页
	 * @param URLS
	 */
	public  void insertNewPageInfo(String URL){
		if(URL==null) 
			return;
		// JDBC程序的3个接口： 连接对象， SQL执行容器， 结果集
		Connection connection = null;	
		PreparedStatement statement = null;
		/*	INSERT INTO PAGE_DRESS_STORE(ADDRESS,CHECKSTATE,MDSNAMES) VALUES('张飞',1,'666');
		 * 	ADDRESS varchar2(200) not null,  --网址  200表示能存100汉字（汉字占2位）,200字母
         *	CHECKSTATE number(2),            --是否已经检索的状态 0未检索  1已检索
         *	MDSNAMES varchar2(100) not null  --md5
		 */
	
		connection=getConnection();
		//将事务模式设置为手动提交事务：
		//connection.setAutoCommit(false);
        //设置事务的隔离级别。
		//connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		try {
			String md5Str=getFileName(URL);
			String sqlStr="INSERT INTO "+TABLE_NAME_PAGE+"(ADDRESS,CHECKSTATE,MDSNAMES) "
					+ "VALUES('"+URL+"',0,'"+md5Str+"')";
			statement = connection.prepareStatement(sqlStr);
			statement.executeUpdate();
			System.out.println("新网页："+URL);
			//connection.commit();
		}catch (Exception e) {
			//e.printStackTrace();
		}finally {
			try {
				if(statement!=null)
					statement.close();
				connection.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	
	
	/**
	 * 查询一条未Connect连接检测的URL
	 * @param url url
	 * @param type true网址  false图片
	 * @return true 有 false 没有
	 */
	public PageInfoBin queryOneNotCheckUrl(){
		// JDBC程序的3个接口： 连接对象， SQL执行容器， 结果集
		Connection connection = null;	
		Statement statement = null;
		ResultSet resultSet = null;
		PageInfoBin  infoBin=null;
		String sqlStr="SELECT * FROM "+TABLE_NAME_PAGE+" WHERE CHECKSTATE=0 AND ROWNUM=1";
		try {
			connection=getConnection();
			// 3，创建statement对象，用于执行SQL的容器
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sqlStr);
			if(null == resultSet) {
				return infoBin;
			}
			if(resultSet.next()){
				infoBin=new PageInfoBin();
				infoBin.setSTORENUM(resultSet.getLong("STORENUM"));
				infoBin.setAddress(resultSet.getString("ADDRESS"));
				infoBin.setCheckState(resultSet.getInt("CHECKSTATE"));
				infoBin.setMd5Name(resultSet.getString("MDSNAMES"));
				String sqlUpdataStr="update PAGE_DRESS_STORE set CHECKSTATE=1 where ADDRESS = '"+infoBin.getAddress()+"'";
				statement.execute(sqlUpdataStr);
			}
			return infoBin;
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}finally {
			try {
				if(resultSet!=null)
					resultSet.close();
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	
	public String searchPicPath() {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String urlStr="";
		String sqlStr="SELECT * FROM "+TABLE_NAME_PIC+" WHERE DOWNLOADSTATE = 0 AND ROWNUM=1";
		connection=getConnection();
		// 3，创建statement对象，用于执行SQL的容器
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sqlStr);
			if(null == resultSet) {
				return urlStr;
			}
			if(resultSet.next()){
				urlStr=resultSet.getString("ADDRESS");
				String sqlUpdataStr="update PIC_DRESS_STORE set DOWNLOADSTATE=1 where ADDRESS = '"+urlStr+"'";
				statement.execute(sqlUpdataStr);
			}
			return urlStr;
		} catch (SQLException e) {
			e.printStackTrace();
			return urlStr;
		}finally {
			try {
				if(resultSet!=null)
					resultSet.close();
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	
	
	public void upDataPageInfo(PageInfoBin  infoBin){
		Connection connection = null;	
		Statement statement = null;
		String sqlStr="update PAGE_DRESS_STORE set CHECKSTATE=1,"
				+ "MDSNAMES='"+infoBin.getMd5Name()+"' where ADDRESS = '"+infoBin.getAddress()+"'";
		try {
			connection=getConnection();
			// 3，创建statement对象，用于执行SQL的容器
			statement = connection.createStatement();
			statement.execute(sqlStr);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	
	
	private  String getFileName(String url){
    	return  DigestUtils.md5Hex(url);
    }
}
