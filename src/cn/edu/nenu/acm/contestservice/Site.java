package cn.edu.nenu.acm.contestservice;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

public class Site {	
	
	private static HashMap<String,Object> systemSettings;
	
	private static String indexNotice="";
	private static long openTime=0;
	private static boolean updateable=true;
	private static boolean coach_edit_able=true;
	private static boolean book_able=false;
	private static boolean volunteer_reg_able=false;
	private static BasicDataSource dataSource = null;
	public static final String appsalt=generateSalt();
	
	public static void dataBaseInit() {
		if (dataSource != null) {
			try {
				dataSource.close();
			} catch (Exception e) {
				//
			}
			dataSource = null;
		}
		try {
			Properties p = new Properties();
			p.setProperty("driverClassName", "com.mysql.jdbc.Driver");
//			p.setProperty("url", "jdbc:mysql://localhost:3306/ContestServiceZJNU?autoReconnect=true&autoDeserialize=true");//TODO
			p.setProperty("url", "jdbc:mysql://localhost:3306/ContestService?autoReconnect=true&autoDeserialize=true");
			p.setProperty("password", "我是不会告诉你数据库的密码的");
			p.setProperty("username", "我是不会告诉你数据库连接的用户名的");
			p.setProperty("maxActive", "50");
			p.setProperty("maxIdle", "10");
			p.setProperty("maxWait", "1000");
			p.setProperty("removeAbandoned", "true");
			p.setProperty("removeAbandonedTimeout", "120");
			p.setProperty("testOnBorrow", "true");
			p.setProperty("logAbandoned", "true");
			dataSource = (BasicDataSource) BasicDataSourceFactory
					.createDataSource(p);
		} catch (Exception e) {
			//
		}
	}

	public static synchronized Connection getDataBaseConnection() throws SQLException {
		if (dataSource == null) {
			dataBaseInit();
		}
		Connection conn = null;
		if (dataSource != null) {
			conn = dataSource.getConnection();
		}
		return conn;
	}
	
	public final static String hash(String string,String salt) {
		String method = "SHA";
		String hex="i.love_shuxIaO!~";//"1234567890abcdef"
		string =salt+string+ "敏感信息，不可示人的，因为这个salt可以决定整个系统的One Time Login的Token计算";
//		string =salt+string+ "For ZJNU";//TODO
		String s = "";
		MessageDigest mdTemp = null;
		try {
			mdTemp = MessageDigest.getInstance(method);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return string;
		}
		byte[] md = mdTemp.digest(string.getBytes());
		for (int i = 0; i < md.length; i++) {
			s += hex.charAt(md[i] >>> 4 & 0xf);
			s += hex.charAt(md[i] & 0xf);
		}
		return s;
	}
	
	public final static String htmlEncode(String str){
		if(str==null)return "";
		str=str.replace(">", "&gt;");
		str=str.replace("<", "&lt;");
		str=str.replace("\"", "&quot;");
//		str=str.replace(" ", "&nbsp;");
		str=str.replace("\n", "<br/>");
		return str;
	}
	public final static String htmlEncodeNoBr(String str){
		if(str==null)return "";
		str=str.replace(">", "&gt;");
		str=str.replace("<", "&lt;");
		str=str.replace("\"", "&quot;");
//		str=str.replace(" ", "&nbsp;");
//		str=str.replace("\n", "<br/>");
		return str;
	}
	public final static String generateSalt(){
		Random random = new Random();
		return new Integer(random.nextInt()).toString();
	}
	public final static String maxString(String string,int length){
		if(string==null)return "";
		if(string.length()<=length){
			return string;
		}
		return string.substring(0, length);
	}

	public static String getIndexNotice() {
		return indexNotice;
	}

	public static void setIndexNotice(String indexNotice) {
		Site.indexNotice = indexNotice;
	}

	public static long getOpenTime() {
		return openTime;
	}

	public static void setOpenTime(long openTime) {
		Site.openTime = openTime;
	}

	public static boolean isUpdateable() {
		return updateable;
	}

	public static void setUpdateable(boolean updateable) {
		Site.updateable = updateable;
	}

	public static boolean isCoach_edit_able() {
		return coach_edit_able;
	}

	public static void setCoach_edit_able(boolean coach_edit_able) {
		Site.coach_edit_able = coach_edit_able;
	}

	public static boolean isBook_able() {
		return book_able;
	}

	public static void setBook_able(boolean book_able) {
		Site.book_able = book_able;
	}
	
	public static boolean isVolunteer_reg_able() {
		return volunteer_reg_able;
	}

	public static void setVolunteer_reg_able(boolean volunteer_reg_able) {
		Site.volunteer_reg_able = volunteer_reg_able;
	}

	public final static boolean isEmpty(Object str){
		if(str==null)
			return true;
		if("".equals(str)){
			return true;
		}
		return false;
	}
}
