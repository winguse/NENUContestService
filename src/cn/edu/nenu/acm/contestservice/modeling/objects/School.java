package cn.edu.nenu.acm.contestservice.modeling.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cn.edu.nenu.acm.contestservice.Site;

public class School {
	private int id = -1; // `id` INT NOT NULL AUTO_INCREMENT ,
	private String chineseName="";// `ChineseName` VARCHAR(255) NULL ,
	private String englishName="";// `EnglishName` VARCHAR(255) NULL ,
	private String postAddress="";// `PostAddress` VARCHAR(255) NULL ,
	private String postCode="";// `PostCode` VARCHAR(10) NULL ,
	private int teamCount=0;

	public School(int id, String chineseName, String englishName,
			String postAddress, String postCode) {
		super();
		this.id = id;
		this.chineseName = chineseName;
		this.englishName = englishName;
		this.postAddress = postAddress;
		this.postCode = postCode;
	}
	public School(int id, String chineseName, String englishName,
			String postAddress, String postCode,int teamCount) {
		super();
		this.id = id;
		this.chineseName = chineseName;
		this.englishName = englishName;
		this.postAddress = postAddress;
		this.postCode = postCode;
		this.teamCount=teamCount;
	}
	public School(String chineseName, String englishName,
			String postAddress, String postCode) {
		super();
		this.chineseName = chineseName;
		this.englishName = englishName;
		this.postAddress = postAddress;
		this.postCode = postCode;
	}
	
	public School(){
		
	}
	
	public School(int id) throws SQLException{
		this.load(id);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getChineseName() {
		return chineseName;
	}
	public void setChineseName(String chineseName) {
		this.chineseName = Site.maxString(chineseName,255);
	}
	public String getEnglishName() {
		return englishName;
	}
	public void setEnglishName(String englishName) {
		this.englishName = Site.maxString(englishName,255);
	}
	public String getPostAddress() {
		return postAddress;
	}

	public void setPostAddress(String postAddress) {
		this.postAddress = Site.maxString(postAddress, 255);
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = Site.maxString(postCode, 10);
	}
	public static List<School> getAllSchoolsWithoutTeamRelation() throws SQLException{
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT  id,ChineseName,EnglishName,PostAddress,PostCode FROM School ",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.execute();
		ResultSet rs=pstat.getResultSet();
		ArrayList<School> schools=new ArrayList<School>();
		while(rs.next()){
			schools.add(new School(
				rs.getInt("id"),
				rs.getString("ChineseName"),
				rs.getString("EnglishName"),
				rs.getString("PostAddress"),
				rs.getString("PostCode")
			));
		}
		rs.close();
		pstat.close();
		conn.close();
		return schools;
	}
	public static List<School> getAllSchools() throws SQLException{
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT School.id id,School.ChineseName ChineseName," +
						"School.EnglishName EnglishName,PostAddress,PostCode,COUNT(Team.School)" +
						" TeamCount FROM School LEFT JOIN Team ON School.id=Team.School WHERE " +
						"School.id>1 GROUP BY School.id",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.execute();
		ResultSet rs=pstat.getResultSet();
		ArrayList<School> schools=new ArrayList<School>();
		while(rs.next()){
			schools.add(new School(
				rs.getInt("id"),
				rs.getString("ChineseName"),
				rs.getString("EnglishName"),
				rs.getString("PostAddress"),
				rs.getString("PostCode"),
				rs.getInt("TeamCount")
			));
		}
		rs.close();
		pstat.close();
		conn.close();
		return schools;
	}
	
	public boolean load(int id) throws SQLException {
		boolean ret = true;
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,ChineseName,EnglishName,PostAddress,PostCode FROM School Where id=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1,  id);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		if (!rs.next()) {
			ret = false;
		} else {
			this.id = rs.getInt("id");
			chineseName=rs.getString("ChineseName");
			englishName=rs.getString("EnglishName");
			postAddress=rs.getString("PostAddress");
			postCode=rs.getString("PostCode");
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
						"INSERT INTO School(ChineseName,EnglishName,PostAddress,PostCode) VALUES(?,?,?,?)",
						Statement.RETURN_GENERATED_KEYS);
		pstat.setString(1,chineseName);
		pstat.setString(2,englishName);
		pstat.setString(3,postAddress);
		pstat.setString(4,postCode);
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
		conn.createStatement().executeUpdate("DELETE FROM School WHERE id=" + id);
		conn.close();
	}

	public void update() throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"UPDATE School SET ChineseName=?,EnglishName=?,PostAddress=?,PostCode=? WHERE id=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setString(1,chineseName);
		pstat.setString(2,englishName);
		pstat.setString(3,postAddress);
		pstat.setString(4,postCode);
		pstat.setInt(5,id);
		pstat.execute();
		pstat.close();
		conn.close();
	}
	public int getTeamCount() {
		return teamCount;
	}
	public void setTeamCount(int teamCount) {
		this.teamCount = teamCount;
	}
	
}
