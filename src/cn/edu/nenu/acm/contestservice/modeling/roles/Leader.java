package cn.edu.nenu.acm.contestservice.modeling.roles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.nenu.acm.contestservice.Site;
import cn.edu.nenu.acm.contestservice.modeling.objects.BookedRoomInfo;
import cn.edu.nenu.acm.contestservice.modeling.objects.Coach;
import cn.edu.nenu.acm.contestservice.modeling.objects.LedPlan;
import cn.edu.nenu.acm.contestservice.modeling.objects.Person;
import cn.edu.nenu.acm.contestservice.modeling.objects.School;
import cn.edu.nenu.acm.contestservice.modeling.objects.User;

public class Leader {
	private static Object book_room_lock = new Object();
	private User user = null;
	private LedPlan plan = null;
	private List<Coach> coaches = null;
	private List<Person> allTeamMembers = null;
	private List<Person> retinue = null;
	private School school = null;

	public Leader(User user) {
		this.user = user;
	}

	public void loadMyPlans() throws SQLException {
		this.plan = LedPlan.getLeaderPlans(this.user.getId());
	}

	public void loadChaches() throws SQLException {
		this.coaches = Person.loadCoaches(user.getId());
	}

	public void loadAllTeamMembers() throws SQLException {
		this.allTeamMembers = Person.loadSpecifyPersons(user.getId(),
				Person.PERSON_TEAM_MEMBER);
	}

	public void loadRetinue() throws SQLException {
		this.retinue = Person.loadSpecifyPersons(user.getId(),
				Person.PERSON_RETINUE);
	}

	public void loadSchool() throws SQLException {
		if (user.getSchool() != 1)
			this.school = new School(user.getSchool());
		else
			this.school = new School();
	}

	public void setSchool(School school) throws SQLException {
		if(school==null)
			loadSchool();
		else
			this.school = school;
	}

	public User getUser() {
		return user;
	}

	public LedPlan getPlan() throws SQLException {
		if(plan==null)loadMyPlans();
		return plan;
	}

	public List<Coach> getCoaches() throws SQLException {
		if(coaches==null)loadChaches();
		return coaches;
	}

	public List<Person> getAllTeamMembers() throws SQLException {
		if(allTeamMembers==null)loadAllTeamMembers();
		return allTeamMembers;
	}

	public List<Person> getRetinue() throws SQLException {
		if(retinue==null)loadRetinue();
		return retinue;
	}

	public School getSchool() throws SQLException {
		if(school==null)loadSchool();
		return school;
	}

	/**
	 * 预定房间，更新订房关系表，（？）注意考虑并发问题。
	 * 
	 * @param room
	 * @param bookCount
	 * @return 订房失败返回false，反之亦然。
	 * @throws SQLException
	 */
	public boolean bookRoom(int room, int bookCount) throws SQLException {
		synchronized (book_room_lock) {
			boolean ret = false;
			Connection conn = Site.getDataBaseConnection();
			conn.setAutoCommit(false);
			PreparedStatement pstat = conn.prepareStatement(
					"SELECT id,Total,Booked FROM Room WHERE id=?",
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE,
					ResultSet.CLOSE_CURSORS_AT_COMMIT);
			pstat.setInt(1, room);
			pstat.execute();
			ResultSet rs = pstat.getResultSet();
			if (rs.next()) {
				int total = rs.getInt("Total");
				int booked = rs.getInt("Booked");
				if (total >= booked + bookCount) {
					rs.updateInt("Booked", booked + bookCount);
					rs.updateRow();
					rs.close();
					rs = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_UPDATABLE,
							ResultSet.CLOSE_CURSORS_AT_COMMIT).executeQuery(
							"SELECT User_id,Room_id,Count FROM Leader_Book_Room WHERE User_id="
									+ this.user.getId() + " AND Room_id="
									+ room);
					if (rs.next()) {
						rs.updateInt("Count", rs.getInt("Count") + bookCount);
						rs.updateRow();
						rs.close();
					} else {
						conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
								ResultSet.CONCUR_UPDATABLE,
								ResultSet.CLOSE_CURSORS_AT_COMMIT)
								.executeUpdate(
										"INSERT INTO Leader_Book_Room(User_id,Room_id,Count)"
												+ "VALUES(" + this.user.getId()
												+ "," + room + "," + bookCount
												+ ")");
					}
					conn.commit();
					ret = true;
				} else {
					conn.rollback();
				}
			} else {
				conn.rollback();
			}
			pstat.close();
			conn.close();
			return ret;
		}
	}

	/**
	 * 取消预订房间，如果返回取消的结果，成功or失败。 如果退房比订房多，则退全部的房间。
	 * 
	 * @param room
	 * @param cancleCount
	 * @throws SQLException
	 * @return true 取消成功，反之失败
	 */
	public boolean cancleBookRoom(int room, int cancleCount)
			throws SQLException {
		synchronized (book_room_lock) {
			boolean ret = false;
			Connection conn = Site.getDataBaseConnection();
			conn.setAutoCommit(false);
			PreparedStatement pstat = conn
					.prepareStatement(
							"SELECT User_id,Room_id,Count FROM Leader_Book_Room WHERE User_id=? AND Room_id=?",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_UPDATABLE,
							ResultSet.CLOSE_CURSORS_AT_COMMIT);
			pstat.setInt(1, this.user.getId());
			pstat.setInt(2, room);
			pstat.execute();
			ResultSet rs = pstat.getResultSet();
			if (rs.next()) {
				int bookedCount = rs.getInt("Count");
				if (cancleCount >= bookedCount) {
					cancleCount = bookedCount;
					rs.updateInt("Count", bookedCount - cancleCount);
					rs.updateRow();
					rs.deleteRow();//2012-09-06 因为数据库现在由一个trigger处理删除的时候对房间的更新，所以必须先update使之变0，否则就会多退房间，为了安全……。
				} else {
					rs.updateInt("Count", bookedCount - cancleCount);
					rs.updateRow();
				}
				conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE,
						ResultSet.CLOSE_CURSORS_AT_COMMIT).executeUpdate(
						"UPDATE Room SET Booked = Booked - " + cancleCount
								+ " WHERE id=" + room);
				ret = true;
				conn.commit();
			} else {
				conn.rollback();
			}
			rs.close();
			pstat.close();
			conn.close();
			return ret;
		}
	}

	public List<BookedRoomInfo> getBookedRoomInfo() throws SQLException {
		ArrayList<BookedRoomInfo> bookedRoomsInfo = new ArrayList<BookedRoomInfo>();
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT Room.id id,Hotel,TypeName,Breakfast,Internet,Volume,Total,Booked,Price,Count,Name,Room.Description Description"
								+ " FROM Room,Leader_Book_Room,Hotel WHERE Room_id=Room.id AND Room.Hotel=Hotel.id AND User_id=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, user.getId());
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		while (rs.next()) {
			bookedRoomsInfo.add(new BookedRoomInfo(rs.getInt("id"), rs
					.getInt("Hotel"), rs.getBoolean("Breakfast"), rs
					.getBoolean("Internet"), rs.getString("TypeName"), rs
					.getInt("Volume"), rs.getInt("Total"), rs.getInt("Booked"),
					rs.getInt("Price"), rs.getInt("Count"), rs
							.getString("Name"), rs.getString("Description")));
		}
		rs.close();
		pstat.close();
		conn.close();
		return bookedRoomsInfo;
	}
}
