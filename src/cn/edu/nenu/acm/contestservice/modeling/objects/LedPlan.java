package cn.edu.nenu.acm.contestservice.modeling.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.edu.nenu.acm.contestservice.Site;

public class LedPlan {
	private int id = -1;// `id` INT NOT NULL AUTO_INCREMENT ,
	private int leader = 0;// `Leader` INT NULL ,
	private long arrival = new Date().getTime();// `Arrival` INT NULL ,
	private String arrivalTraffic = "";// `ArrivalTraffic` VARCHAR(255) NULL ,
	private long leave = new Date().getTime();// `Leave` INT NULL ,
	private String leaveTraffic = "";// `LeaveTraffic` VARCHAR(255) NULL ,
	private String bookTicket = "";// `BookTicket` LONGTEXT NULL ,

	public LedPlan(int id, int leader, long arrival, String arrivalTraffic,
			long leave, String leaveTraffic, String bookTicket) {
		super();
		this.id = id;
		this.leader = leader;
		this.arrival = arrival;
		this.setArrivalTraffic(arrivalTraffic);
		this.leave = leave;
		this.setLeaveTraffic(leaveTraffic);
		this.bookTicket = bookTicket;
	}

	public LedPlan(int leader, long arrival, String arrivalTraffic, long leave,
			String leaveTraffic, String bookTicket) {
		super();
		this.leader = leader;
		this.arrival = arrival;
		this.setArrivalTraffic(arrivalTraffic);
		this.leave = leave;
		this.setLeaveTraffic(leaveTraffic);
		this.bookTicket = bookTicket;
	}

	public LedPlan() {

	}

	public static LedPlan getLeaderPlans(int leader) throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Leader,Arrival,ArrivalTraffic,`Leave`,LeaveTraffic,BookTicket FROM LedPlan Where Leader=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, leader);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		LedPlan plan = null;
		if (rs.next()) {
			plan = new LedPlan(rs.getInt("id"), rs.getInt("Leader"),
					rs.getLong("Arrival"), rs.getString("ArrivalTraffic"),
					rs.getLong("Leave"), rs.getString("LeaveTraffic"),
					rs.getString("BookTicket"));
		}else{
			plan=new LedPlan();
		}
		rs.close();
		pstat.close();
		conn.close();
		return plan;
	}

	public static List<LedPlan> getArrivalPlans(long startTime, long endTime)
			throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Leader,Arrival,ArrivalTraffic,`Leave`,LeaveTraffic,BookTicket FROM LedPlan Where Arrival>=? AND Arrival<=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setLong(1, startTime);
		pstat.setLong(2, endTime);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		ArrayList<LedPlan> plans = new ArrayList<LedPlan>();
		while (rs.next()) {
			plans.add(new LedPlan(rs.getInt("id"), rs.getInt("Leader"), rs
					.getLong("Arrival"), rs.getString("ArrivalTraffic"), rs
					.getLong("Leave"), rs.getString("LeaveTraffic"), rs
					.getString(" BookTicket")));
		}
		rs.close();
		pstat.close();
		conn.close();
		return plans;
	}

	public static List<LedPlan> getLeavePlans(long startTime, long endTime)
			throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Leader,Arrival,ArrivalTraffic,`Leave`,LeaveTraffic,BookTicket FROM LedPlan Where Leave>=? AND Leave<=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setLong(1, startTime);
		pstat.setLong(2, endTime);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		ArrayList<LedPlan> plans = new ArrayList<LedPlan>();
		while (rs.next()) {
			plans.add(new LedPlan(rs.getInt("id"), rs.getInt("Leader"), rs
					.getLong("Arrival"), rs.getString("ArrivalTraffic"), rs
					.getLong("Leave"), rs.getString("LeaveTraffic"), rs
					.getString(" BookTicket")));
		}
		rs.close();
		pstat.close();
		conn.close();
		return plans;
	}

	public boolean load(int id) throws SQLException {
		boolean ret = false;
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Leader,Arrival,ArrivalTraffic,`Leave`,LeaveTraffic,BookTicket FROM LedPlan Where id=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, id);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		if (rs.next()) {
			ret = true;
			leader = rs.getInt("Leader");
			arrival = rs.getLong("Arrival");
			arrivalTraffic = rs.getString("ArrivalTraffic");
			leave = rs.getLong("Leave");
			leaveTraffic = rs.getString("LeaveTraffic");
			bookTicket = rs.getString(" BookTicket");
		}
		rs.close();
		pstat.close();
		conn.close();
		return ret;
	}

	public void add() throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"INSERT INTO LedPlan(Leader,Arrival,ArrivalTraffic,`Leave`,LeaveTraffic,BookTicket) VALUES(?,?,?,?,?,?)",
						Statement.RETURN_GENERATED_KEYS);
		pstat.setInt(1, leader);
		pstat.setLong(2, arrival);
		pstat.setString(3, arrivalTraffic);
		pstat.setLong(4, leave);
		pstat.setString(5, leaveTraffic);
		pstat.setString(6, bookTicket);
		pstat.execute();
		ResultSet rs = pstat.getGeneratedKeys();
		if (rs.next()) {
			this.id = rs.getInt(1);
		}
		rs.close();
		pstat.close();
		conn.close();
	}

	public static void delete(int id) throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		conn.createStatement().executeUpdate("DELETE FROＭ LedPlan WHERE id=" + id);
		conn.close();
	}

	public void update() throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"UPDATE LedPlan SET Leader=?,Arrival=?,ArrivalTraffic=?,`Leave`=?,LeaveTraffic=?,BookTicket=? WHERE id="
								+ id, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, leader);
		pstat.setLong(2, arrival);
		pstat.setString(3, arrivalTraffic);
		pstat.setLong(4, leave);
		pstat.setString(5, leaveTraffic);
		pstat.setString(6, bookTicket);
		pstat.execute();
		pstat.close();
		conn.close();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLeader() {
		return leader;
	}

	public void setLeader(int leader) {
		this.leader = leader;
	}

	public long getArrival() {
		return arrival;
	}

	public void setArrival(long arrival) {
		this.arrival = arrival;
	}

	public String getArrivalTraffic() {
		return arrivalTraffic;
	}

	public void setArrivalTraffic(String arrivalTraffic) {
		this.arrivalTraffic = Site.maxString(arrivalTraffic, 255);
	}

	public long getLeave() {
		return leave;
	}

	public void setLeave(long leave) {
		this.leave = leave;
	}

	public String getLeaveTraffic() {
		return leaveTraffic;
	}

	public void setLeaveTraffic(String leaveTraffic) {
		this.leaveTraffic = Site.maxString(leaveTraffic, 255);
	}

	public String getBookTicket() {
		return bookTicket;
	}

	public void setBookTicket(String bookTicket) {
		this.bookTicket = bookTicket;
	}
}
