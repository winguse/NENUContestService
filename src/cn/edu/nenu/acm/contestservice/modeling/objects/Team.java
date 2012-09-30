package cn.edu.nenu.acm.contestservice.modeling.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import cn.edu.nenu.acm.contestservice.Site;

public class Team {

	public final static int TEAM_NOT_VERIFIED = 0;
	public final static int TEAM_OFFICAL_UNDER_VERIFIED = -1;
	public final static int TEAM_TOURISM_VERIFIED =-2;
	public final static int TEAM_OFFICAL = 1;
	public final static int TEAM_OFFICAL_VERIFIED = 4;
	public final static int TEAM_OFFICAL_UNDER_VERIFIED_AGAIN = -4;
	public final static int TEAM_TOURISM = 2;

	private int id = -1;// `id` INT NOT NULL AUTO_INCREMENT ,
	private int coach = 0;// `Coach` INT NULL ,
	private int hotel = 0;// `Hotel` INT NULL DEFAULT NULL ,
	private int type = TEAM_OFFICAL;// `Type` INT NULL ,
	private String chineseName = "";// `ChineseName` VARCHAR(255) NULL ,
	private String englishName = "";// `EnglishName` VARCHAR(255) NULL ,
	private String seat = "";// `Seat` VARCHAR(45) NULL ,
	private boolean arrivaled = false;// `Arrivaled` TINYINT(1) NULL DEFAULT
										// False ,
	private boolean leaved = false;// `Leaved` TINYINT(1) NULL DEFAULT False ,
	private String statusDescription = "|没有ICPC数据。";// `StatusDescription` LONGTEXT NULL
	private int school=0;
	
	
	public Team() {

	}

	public Team(int id, int coach, int hotel, int type, String seat,
			String chineseName, String englishName, boolean arrivaled,
			boolean leaved, String statusDescription,int school) {
		super();
		this.id = id;
		this.coach = coach;
		this.hotel = hotel;
		this.type = type;
		this.setSeat(seat);
		this.setChineseName(chineseName);
		this.setEnglishName(englishName);
		this.arrivaled = arrivaled;
		this.leaved = leaved;
		this.statusDescription = statusDescription;
		this.school=school;
	}

	public Team(int coach, int hotel, int type, String seat,
			String chineseName, String englishName, boolean arrivaled,
			boolean leaved, String statusDescription,int school) {
		super();
		this.coach = coach;
		this.hotel = hotel;
		this.type = type;
		this.setSeat(seat);
		this.setChineseName(chineseName);
		this.setEnglishName(englishName);
		this.arrivaled = arrivaled;
		this.leaved = leaved;
		this.statusDescription = statusDescription;
		this.school=school;
	}

	public static List<Team> getAllTeams() throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn.prepareStatement(
				"SELECT id,Coach,School,Hotel,Type,Seat,ChineseName,EnglishName,"
						+ "Arrivaled,Leaved,StatusDescription,School FROM Team ORDER BY School,Coach",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
				ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		ArrayList<Team> teams = new ArrayList<Team>();
		while (rs.next()) {
			teams.add(new Team(rs.getInt("id"), rs.getInt("Coach"), rs
					.getInt("Hotel"), rs.getInt("Type"), rs.getString("Seat"),
					rs.getString("ChineseName"), rs.getString("EnglishName"),
					rs.getBoolean("Arrivaled"), rs.getBoolean("Leaved"), rs
							.getString("StatusDescription"),rs.getInt("School")));
		}
		rs.close();
		pstat.close();
		conn.close();
		return teams;
	}
	public static List<Team> getAllTeamsThatUnderVerify() throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn.prepareStatement(
				"SELECT id,Coach,School,Hotel,Type,Seat,ChineseName,EnglishName,"
						+ "Arrivaled,Leaved,StatusDescription,School FROM Team WHERE Type<=0",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
				ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		ArrayList<Team> teams = new ArrayList<Team>();
		while (rs.next()) {
			teams.add(new Team(rs.getInt("id"), rs.getInt("Coach"), rs
					.getInt("Hotel"), rs.getInt("Type"), rs.getString("Seat"),
					rs.getString("ChineseName"), rs.getString("EnglishName"),
					rs.getBoolean("Arrivaled"), rs.getBoolean("Leaved"), rs
							.getString("StatusDescription"),rs.getInt("School")));
		}
		rs.close();
		pstat.close();
		conn.close();
		return teams;
	}
	public static List<Team> getSchoolTeams(int school) throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Coach,Hotel,Type,Seat,ChineseName,EnglishName,"
								+ "Arrivaled,Leaved,StatusDescription,School FROM Team WHERE School=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, school);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		ArrayList<Team> teams = new ArrayList<Team>();
		while (rs.next()) {
			teams.add(new Team(rs.getInt("id"), rs.getInt("Coach"), rs
					.getInt("Hotel"), rs.getInt("Type"), rs.getString("Seat"),
					rs.getString("ChineseName"), rs.getString("EnglishName"),
					rs.getBoolean("Arrivaled"), rs.getBoolean("Leaved"), rs
							.getString("StatusDescription"),rs.getInt("School")));
		}
		rs.close();
		pstat.close();
		conn.close();
		return teams;
	}

	public static List<Team> getCoachTeams(int coach) throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn.prepareStatement(
				"SELECT id,Coach,Hotel,Type,Seat,ChineseName,EnglishName,"
						+ "Arrivaled,Leaved,StatusDescription,School "
						+ "FROM Team WHERE Coach=?",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
				ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, coach);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		ArrayList<Team> teams = new ArrayList<Team>();
		while (rs.next()) {
			teams.add(new Team(rs.getInt("id"), rs.getInt("Coach"), rs
					.getInt("Hotel"), rs.getInt("Type"), rs.getString("Seat"),
					rs.getString("ChineseName"), rs.getString("EnglishName"),
					rs.getBoolean("Arrivaled"), rs.getBoolean("Leaved"), rs
							.getString("StatusDescription"),rs.getInt("School")));
		}
		rs.close();
		pstat.close();
		conn.close();
		return teams;
	}

	/**
	 * 返回对应User ID的志愿者服务的所有队伍，设计时，考虑队伍和志愿者是多对多的关系，所以这里查询两个表
	 * 
	 * @return
	 * @throws SQLException
	public static List<Team> getVolunteerTeams(int volunteer)
			throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Coach,School,Hotel,Type,Seat,ChineseName,EnglishName,"
								+ "Arrivaled,Leaved,StatusDescription,School"
								+ " FROM Team,Volunteer_Team WHERE Team_id=id AND User_id=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, volunteer);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		ArrayList<Team> teams = new ArrayList<Team>();
		while (rs.next()) {
			teams.add(new Team(rs.getInt("id"), rs.getInt("Coach"), rs
					.getInt("Hotel"), rs.getInt("Type"), rs.getString("Seat"),
					rs.getString("ChineseName"), rs.getString("EnglishName"),
					rs.getBoolean("Arrivaled"), rs.getBoolean("Leaved"), rs
							.getString("StatusDescription"),rs.getInt("School")));
		}
		rs.close();
		pstat.close();
		conn.close();
		return teams;
	}
	 */

	public boolean load(int id) throws SQLException {
		boolean ret = false;
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn.prepareStatement(
				"SELECT id,Coach,Hotel,Type,Seat,ChineseName,EnglishName,"
						+ "Arrivaled,Leaved,StatusDescription,School"
						+ " FROM Team Where id=?", ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, id);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		if (rs.next()) {
			this.id = rs.getInt("id");
			coach = rs.getInt("Coach");
			hotel = rs.getInt("Hotel");
			type = rs.getInt("Type");
			seat = rs.getString("Seat");
			chineseName = rs.getString("ChineseName");
			englishName = rs.getString("EnglishName");
			arrivaled = rs.getBoolean("Arrivaled");
			leaved = rs.getBoolean("Leaved");
			statusDescription = rs.getString("StatusDescription");
			school=rs.getInt("School");
			ret = true;
		}
		rs.close();
		pstat.close();
		conn.close();
		return ret;
	}
	public boolean load(String englishName) throws SQLException {
		boolean ret = false;
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn.prepareStatement(
				"SELECT id,Coach,Hotel,Type,Seat,ChineseName,EnglishName,"
						+ "Arrivaled,Leaved,StatusDescription,School"
						+ " FROM Team Where EnglishName=?", ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setString(1, englishName);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		if (rs.next()) {
			id = rs.getInt("id");
			coach = rs.getInt("Coach");
			hotel = rs.getInt("Hotel");
			type = rs.getInt("Type");
			seat = rs.getString("Seat");
			chineseName = rs.getString("ChineseName");
			this.englishName = rs.getString("EnglishName");
			arrivaled = rs.getBoolean("Arrivaled");
			leaved = rs.getBoolean("Leaved");
			statusDescription = rs.getString("StatusDescription");
			school=rs.getInt("School");
			ret = true;
		}
		rs.close();
		pstat.close();
		conn.close();
		return ret;
	}
	public void add() throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn.prepareStatement(
				"INSERT INTO Team(Coach,Hotel,Type,Seat,ChineseName,EnglishName,"
						+ "Arrivaled,Leaved,StatusDescription,School)"
						+ "VALUES(?,?,?,?,?,?,?,?,?,?)",
						Statement.RETURN_GENERATED_KEYS);
		pstat.setInt(1, coach);
		if (hotel == 0) {
			pstat.setNull(2, Types.INTEGER);
		} else {
			pstat.setInt(2, hotel);
		}
		pstat.setInt(3, type);
		if(seat.equals("")){
			pstat.setNull(4, Types.VARCHAR);
		}else{
			pstat.setString(4, seat);
		}
		pstat.setString(5, chineseName);
		pstat.setString(6, englishName);
		pstat.setBoolean(7, arrivaled);
		pstat.setBoolean(8, leaved);
		pstat.setString(9, statusDescription);
		if(school==0){
			pstat.setNull(10, Types.INTEGER);
		}else{
			pstat.setInt(10,school);
		}
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
		conn.createStatement().executeUpdate("DELETE FROM Team WHERE id=" + id);
		conn.close();
	}

	public void update() throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"UPDATE Team SET  Coach=?,Hotel=?,Type=?,Seat=?,ChineseName=?,EnglishName=?,"
								+ "Arrivaled=?,Leaved=?,StatusDescription=?,School=? WHERE id=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, coach);
		if (hotel == 0) {
			pstat.setNull(2, Types.INTEGER);
		} else {
			pstat.setInt(2, hotel);
		}
		pstat.setInt(3, type);
		if(Site.isEmpty(seat)){
			pstat.setNull(4, Types.VARCHAR);
		}else{
			pstat.setString(4, seat);
		}
		pstat.setString(5, chineseName);
		pstat.setString(6, englishName);
		pstat.setBoolean(7, arrivaled);
		pstat.setBoolean(8, leaved);
		pstat.setString(9, statusDescription);
		if(school==0){
			pstat.setNull(10, Types.INTEGER);
		}else{
			pstat.setInt(10,school);
		}
		pstat.setInt(11, id);
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

	public int getCoach() {
		return coach;
	}

	public void setCoach(int coach) {
		this.coach = coach;
	}

	public int getHotel() {
		return hotel;
	}

	public void setHotel(int hotel) {
		this.hotel = hotel;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSeat() {
		return seat;
	}

	public void setSeat(String seat) {
		this.seat = Site.maxString(seat, 45);
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = Site.maxString(chineseName, 255);
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = Site.maxString(englishName, 255);
	}

	public boolean isArrivaled() {
		return arrivaled;
	}

	public void setArrivaled(boolean arrivaled) {
		this.arrivaled = arrivaled;
	}

	public boolean isLeaved() {
		return leaved;
	}

	public void setLeaved(boolean leaved) {
		this.leaved = leaved;
	}

	public String getStatusDescription() {
		if(Site.isEmpty(statusDescription))statusDescription="";
		String[] st=statusDescription.split("\\|");
		if(st.length<2){
			statusDescription=st[0]+"|没有ICPC数据。";
		}
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		if(Site.isEmpty(statusDescription))statusDescription="";
		String[] st=statusDescription.split("\\|");
		if(st.length<2){
			statusDescription=st[0]+"|没有ICPC数据。";
		}
		this.statusDescription = statusDescription;
	}

	public int getSchool() {
		return school;
	}

	public void setSchool(int school) {
		this.school = school;
	}

}
