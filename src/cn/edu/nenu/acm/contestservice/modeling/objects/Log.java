package cn.edu.nenu.acm.contestservice.modeling.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cn.edu.nenu.acm.contestservice.Site;

public class Log {
	private int id=-1;
	private int user=0;
	private String ip="";
	private long time=0;
	private String action="";

	public Log(){
		super();
	}
	
	public Log(int id, int user, String ip, long time, String action) {
		super();
		this.id = id;
		this.user = user;
		this.setIp(ip);
		this.time = time;
		this.action = action;
	}

	public Log(int user, String ip, long time, String action) {
		super();
		this.user = user;
		this.setIp(ip);
		this.time = time;
		this.action = action;
	}

	public static List<Log> getAllLogs() throws SQLException{
		List<Log> logs=new ArrayList<Log>();
		Connection conn=Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,User,IP,Time,Action FROM Log",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.execute();
		ResultSet rs=pstat.getResultSet();
		while(rs.next()){
			logs.add(new Log(
					rs.getInt("id"),
					rs.getInt("User"),
					rs.getString("IP"),
					rs.getLong("time"),
					rs.getString("Action")
			));
		}
		rs.close();
		pstat.close();
		conn.close();
		return logs;
	}
	public static List<Log> getLogs(int page,int pagesize) throws SQLException{
		List<Log> logs=new ArrayList<Log>();
		Connection conn=Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,User,IP,Time,Action FROM Log order by id desc limit ?,?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, (page-1)*pagesize);
		pstat.setInt(2, pagesize);
		pstat.execute();
		ResultSet rs=pstat.getResultSet();
		while(rs.next()){
			logs.add(new Log(
					rs.getInt("id"),
					rs.getInt("User"),
					rs.getString("IP"),
					rs.getLong("time"),
					rs.getString("Action")
			));
		}
		rs.close();
		pstat.close();
		conn.close();
		return logs;
	}
	/**
	 * 增加一条日志，日志信息需要已经存在
	 * @throws SQLException 
	 */
	public void add() throws SQLException{
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"INSERT INTO Log(User,IP,Time,Action) VALUES(?,?,?,?)",
						Statement.RETURN_GENERATED_KEYS);
		pstat.setInt(1, user);
		pstat.setString(2, ip);
		pstat.setLong(3, time);
		pstat.setString(4, action);
		pstat.execute();
		ResultSet rs = pstat.getGeneratedKeys();
		if (rs.next()) {
			this.id = rs.getInt(1);
		}
		rs.close();
		pstat.close();
		conn.close();
	}
	
	/**
	 * 删除一条日志
	 * @param id
	 * @throws SQLException 
	 */
	public static void delete(int id) throws SQLException{
		Connection conn = Site.getDataBaseConnection();
		conn.createStatement().executeUpdate("DELETE FROM Log WHERE id=" + id);
		conn.close();
	}
	
	/**
	 * 加载日志信息
	 * @param id
	 * @return true 如果存在，false 不存在
	 * @throws SQLException 
	 */
	public boolean load(int id) throws SQLException{
		boolean ret=false;
		Connection conn=Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,User,IP,Time,Action FROM Log Where id=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, id);
		pstat.execute();
		ResultSet rs=pstat.getResultSet();
		if(rs.next()){
			this.id=rs.getInt("id");
			user=rs.getInt("User");
			ip=rs.getString("IP");
			time=rs.getLong("time");
			action=rs.getString("Action");
			ret=true;
		}
		rs.close();
		pstat.close();
		conn.close();
		return ret;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUser() {
		return user;
	}

	public void setUser(int user) {
		this.user = user;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = Site.maxString(ip, 255);
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
