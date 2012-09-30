package cn.edu.nenu.acm.contestservice.modeling.roles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.nenu.acm.contestservice.Site;
import cn.edu.nenu.acm.contestservice.modeling.objects.Person;
import cn.edu.nenu.acm.contestservice.modeling.objects.School;
import cn.edu.nenu.acm.contestservice.modeling.objects.Team;
import cn.edu.nenu.acm.contestservice.modeling.objects.User;

//这个Volunteer的，处理得不是很好，层次不太好却地暖，因为这里设计的服务对象是学校，领队下面才是学校，领队下面有教练-》队伍-》队员
public class Volunteer {
	public User user = null;
	public Person person = null;
	public List<School> serveSchool = null;

	public Volunteer(User user) throws SQLException {
		this.user = user;
		if(user.getPermission()==User.USER_VOLUNTEER)
			this.person = Person.loadSpecifyPersons(this.user.getId(),
				Person.PERSON_VOLUNTEER).get(0);
		else
			this.person = Person.loadSpecifyPersons(this.user.getId(),
					Person.PERSON_VOLUNTEER_PEDDING).get(0);
			
	}

	public Volunteer() {

	}

	public void loadServeObject() throws SQLException {
		serveSchool = new ArrayList<School>();
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn.prepareStatement(
				"SELECT School_id FROM School_Volunteer WHERE User_id=?",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
				ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, user.getId());
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		while (rs.next()) {
			School s = new School();
			s.load(rs.getInt("School_id"));
			serveSchool.add(s);
		}
		rs.close();
		pstat.close();
		conn.close();
	}

	/**
	 * 返回学校对应的志愿者列表查询两个表<br>
	 * 高效些
	 * 
	 * @param schoolId
	 *            学校的ID
	 * @return ArrayList<Volunteer>
	 * @throws SQLException
	 */
	public static List<Volunteer> getSchoolsVolunteers(int schoolId)
			throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT User.id,Username,Password,Salt,Permission,School,Person.id,Team,UserBelongs,Title,ChineseName,"
								+ "EnglishName,IDNumber,Gender,Mobile,WelcomeParty,AwardsCeremony,Major,Photo,"
								+ "Email,Clothes,Description FROM School_Volunteer,User,Person Where School_id=?"
								+ " AND User_id=User.id AND User.id=Person.UserBelongs",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, schoolId);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		ArrayList<Volunteer> volunteers = new ArrayList<Volunteer>();
		while (rs.next()) {
			Volunteer v = new Volunteer();
			v.user = new User(rs.getInt("User.id"), rs.getString("Username"),
					rs.getString("Password"), rs.getString("Salt"),
					rs.getInt("Permission"), rs.getInt("School"));
			v.person = new Person(rs.getInt("Person.id"), rs.getInt("Team"),
					rs.getInt("UserBelongs"),
					rs.getInt("Title"), rs.getString("ChineseName"),
					rs.getString("EnglishName"), rs.getString("IDNumber"),
					rs.getBoolean("Gender"), rs.getString("Mobile"),
					rs.getInt("WelcomeParty"), rs.getInt("AwardsCeremony"),
					rs.getString("Major"), rs.getString("Photo"),
					rs.getString("Email"), rs.getString("Clothes"),
					rs.getString("Description"));
			volunteers.add(v);
		}
		rs.close();
		pstat.close();
		conn.close();
		return volunteers;
	}

	/**
	 * 更新志愿者和学校之间的关系，如果volunteerId<0，则表示删除该志愿者的关系。 不存在的话，则自动增加。 不存在修改的情况。
	 * 
	 * @param volunteerId
	 * @param schoolId
	 * @return
	 * @throws SQLException
	 */
	public static boolean volunteerSchool(int volunteerId, int schoolId)
			throws SQLException {
		boolean ret=false;
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = null;
		if (volunteerId < 0) {
			volunteerId = -volunteerId;
			pstat = conn
					.prepareStatement(
							"DELETE FROM School_Volunteer WHERE User_id=? AND School_id=?",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_UPDATABLE,
							ResultSet.CLOSE_CURSORS_AT_COMMIT);
			pstat.setInt(1, volunteerId);
			pstat.setInt(2, schoolId);
			pstat.execute();
			pstat.close();
			ret=true;
		}else{
			pstat = conn
					.prepareStatement(
							"SELECT User_id FROM School_Volunteer WHERE User_id=? AND School_id=?",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_UPDATABLE,
							ResultSet.CLOSE_CURSORS_AT_COMMIT);
			pstat.setInt(1, volunteerId);
			pstat.setInt(2, schoolId);
			pstat.execute();
			if(pstat.getResultSet().next()){
				//do nothing,已经存在了
			}else{
				ret=true;
				conn.createStatement().executeUpdate("INSERT INTO School_Volunteer(User_id,School_id) VALUES("+volunteerId+","+schoolId+")");
			}
			pstat.close();
		}
		conn.close();
		return ret;
	}

	/**
	 * 返回全部志愿者的信息，不过查询效率高
	 * 
	 * @return
	 * @throws SQLException
	 *             public static List<Volunteer> __getAllVolunteer() throws
	 *             SQLException { Connection conn =
	 *             Site.getDataBaseConnection(); PreparedStatement pstat = conn
	 *             .prepareStatement(
	 *             "SELECT User.id,Person,Username,Password,Salt,Permission," +
	 *             "Person.id,Team,Title,ChineseName,EnglishName,IDNumber,Gender,Mobile,WelcomeParty,"
	 *             + "AwardsCeremony,Major,Photo,Email,Clothes,Description " +
	 *             " FROM User,Person Where Permission=2 AND  User.Person=Person.id"
	 *             , ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
	 *             ResultSet.CLOSE_CURSORS_AT_COMMIT); pstat.execute();
	 *             ResultSet rs = pstat.getResultSet(); ArrayList<Volunteer>
	 *             volunteers = new ArrayList<Volunteer>(); while (rs.next()) {
	 *             Volunteer v = new Volunteer(); // v.user = new
	 *             User(rs.getInt("User.id"), // rs.getString("Username"),
	 *             rs.getString("Password"), // rs.getString("Salt"),
	 *             rs.getInt("Permission")); v.person = new
	 *             Person(rs.getInt("Person.id"), rs.getInt("Team"),
	 *             rs.getInt("Title"), rs.getString("ChineseName"),
	 *             rs.getString("EnglishName"), rs.getString("IDNumber"),
	 *             rs.getBoolean("Gender"), rs.getString("Mobile"),
	 *             rs.getInt("WelcomeParty"), rs.getInt("AwardsCeremony"),
	 *             rs.getString("Major"), rs.getString("Photo"),
	 *             rs.getString("Email"), rs.getString("Clothes"),
	 *             rs.getString("Description")); volunteers.add(v); }
	 *             rs.close(); pstat.close(); conn.close(); return volunteers; }
	 */
	/**
	 * 返回全部志愿者的信息，不过查询效率最低下
	 * 
	 * @return
	 * @throws SQLException
	 *             public static List<Volunteer> getAllVolunteer() throws
	 *             SQLException { Connection conn =
	 *             Site.getDataBaseConnection(); PreparedStatement pstat =
	 *             conn.prepareStatement(
	 *             "SELECT id FROM User Where Permission=2 ",
	 *             ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
	 *             ResultSet.CLOSE_CURSORS_AT_COMMIT); pstat.execute();
	 *             ResultSet rs = pstat.getResultSet(); ArrayList<Volunteer>
	 *             volunteers = new ArrayList<Volunteer>(); while (rs.next()) {
	 *             volunteers.add(new Volunteer(new User(rs.getInt("id")))); }
	 *             rs.close(); pstat.close(); conn.close(); return volunteers; }
	 */
	/**
	 * 返回队伍对应的志愿者列表查询两个表<br>
	 * 高效些
	 * 
	 * @param team
	 *            队伍的ID
	 * @return ArrayList<Volunteer>
	 * @throws SQLException
	 *             public static List<Volunteer> __getTeamsVolunteers(int team)
	 *             throws SQLException { Connection conn =
	 *             Site.getDataBaseConnection(); PreparedStatement pstat = conn
	 *             .prepareStatement(
	 *             "SELECT User.id,Person,Username,Password,Salt,Permission," +
	 *             "Person.id,Team,Title,ChineseName,EnglishName,IDNumber,Gender,Mobile,WelcomeParty,"
	 *             + "AwardsCeremony,Major,Photo,Email,Clothes,Description " +
	 *             " FROM Volunteer_Team,User,Person Where Team_id=? AND User_id=User.id AND User.Person=Person.id"
	 *             , ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
	 *             ResultSet.CLOSE_CURSORS_AT_COMMIT); pstat.setInt(1, team);
	 *             pstat.execute(); ResultSet rs = pstat.getResultSet();
	 *             ArrayList<Volunteer> volunteers = new ArrayList<Volunteer>();
	 *             while (rs.next()) { Volunteer v = new Volunteer(); // v.user
	 *             = new User(rs.getInt("User.id"), // rs.getString("Username"),
	 *             rs.getString("Password"), // rs.getString("Salt"),
	 *             rs.getInt("Permission"));//TODO v.person = new
	 *             Person(rs.getInt("Person.id"), rs.getInt("Team"),
	 *             rs.getInt("Title"), rs.getString("ChineseName"),
	 *             rs.getString("EnglishName"), rs.getString("IDNumber"),
	 *             rs.getBoolean("Gender"), rs.getString("Mobile"),
	 *             rs.getInt("WelcomeParty"), rs.getInt("AwardsCeremony"),
	 *             rs.getString("Major"), rs.getString("Photo"),
	 *             rs.getString("Email"), rs.getString("Clothes"),
	 *             rs.getString("Description")); volunteers.add(v); }
	 *             rs.close(); pstat.close(); conn.close(); return volunteers; }
	 */

	/**
	 * 返回队伍对应的志愿者列表查询两个表<br>
	 * PS:这个方法的效率不是最好的。最好的，应该是一次性查询User,Volunteer_Team,Person三个表，把所有的字段都写好。
	 * 现在却是完全分开做，一次查询Volunteer_Team，User，Person，查询次数为
	 * 志愿者数x2+1，总体复杂度应该是一样的，但是额外开销大了，应该扔给后端MySQL做。不过现在这样写，充分利用了现有成果。
	 * 
	 * @param team
	 *            队伍的ID
	 * @return ArrayList<Volunteer>
	 * @throws SQLException
	 *             public static List<Volunteer> getTeamsVolunteers(int team)
	 *             throws SQLException { Connection conn =
	 *             Site.getDataBaseConnection(); PreparedStatement pstat =
	 *             conn.prepareStatement(
	 *             "SELECT User_id FROM Volunteer_Team Where Team_id=? ",
	 *             ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
	 *             ResultSet.CLOSE_CURSORS_AT_COMMIT); pstat.setInt(1, team);
	 *             pstat.execute(); ResultSet rs = pstat.getResultSet();
	 *             ArrayList<Volunteer> volunteers = new ArrayList<Volunteer>();
	 *             while (rs.next()) { volunteers.add(new Volunteer(new
	 *             User(rs.getInt("User_id")))); } rs.close(); pstat.close();
	 *             conn.close(); return volunteers; }
	 */
	/*
	 * public void loadMyTeams() throws SQLException { this.teams =
	 * Team.getVolunteerTeams(this.user.getId()); }
	 */
}
