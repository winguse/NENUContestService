package cn.edu.nenu.acm.contestservice.modeling.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cn.edu.nenu.acm.contestservice.Site;

public class Room {
	protected int id = -1;// `id` INT NOT NULL AUTO_INCREMENT ,
	protected int hotel = 0;// `Hotel` INT NULL ,
	protected boolean breakfast=false;
	protected boolean internet=true;
	protected String typeName = "";// `TypeName` VARCHAR(45) NULL ,
	protected int volume = 1;// `Volume` INT NULL DEFAULT 1 ,
	protected int total = 0;// `Total` INT NULL ,
	protected int booked = 0;// `Booked` INT NULL DEFAULT 0 ,
	protected int price = 0;// `Price` INT NULL ,
	protected String description="";//Description LONGTEXT 
	
	public Room(int id, int hotel,boolean breakfast,boolean internet, String typeName, int volume, int total,
			int booked, int price,String description) {
		super();
		this.id = id;
		this.hotel = hotel;
		this.breakfast=breakfast;
		this.internet=internet;
		this.setTypeName(typeName);
		this.volume = volume;
		this.total = total;
		this.booked = booked;
		this.price = price;
		this.description=description;
	}

	public Room(int hotel,boolean breakfast,boolean internet,  String typeName, int volume, int total, int booked,
			int price,String description) {
		super();
		this.hotel = hotel;
		this.breakfast=breakfast;
		this.internet=internet;
		this.setTypeName(typeName);
		this.volume = volume;
		this.total = total;
		this.booked = booked;
		this.price = price;
		this.description=description;
	}

	public Room() {

	}

	public static List<Room> getHotelRooms(int hotel) throws SQLException{
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Hotel,Breakfast,Internet,TypeName,Volume,Total,Booked,Price,Description FROM Room WHERE Hotel=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, hotel);
		pstat.execute();
		ResultSet rs=pstat.getResultSet();
		ArrayList<Room> rooms=new ArrayList<Room>();
		while(rs.next()){
			rooms.add(new Room(
				rs.getInt("id"),
				rs.getInt("Hotel"),
				rs.getBoolean("Breakfast"),
				rs.getBoolean("Internet"),
				rs.getString("TypeName"),
				rs.getInt("Volume"),
				rs.getInt("Total"),
				rs.getInt("Booked"),
				rs.getInt("Price"),
				rs.getString("Description")
			));
		}
		rs.close();
		pstat.close();
		conn.close();
		return rooms;
	}
	
	public static List<Room> getLeaderBookedRooms(int leader) throws SQLException{
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Hotel,TypeName,Volume,Total,Booked,Price,Description FROM Room,Leader_Book_Room WHERE Room_id=id User_id=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, leader);
		pstat.execute();
		ResultSet rs=pstat.getResultSet();
		ArrayList<Room> rooms=new ArrayList<Room>();
		while(rs.next()){
			rooms.add(new Room(
				rs.getInt("id"),
				rs.getInt("Hotel"),
				rs.getBoolean("Breakfast"),
				rs.getBoolean("Internet"),
				rs.getString("TypeName"),
				rs.getInt("Volume"),
				rs.getInt("Total"),
				rs.getInt("Booked"),
				rs.getInt("Price"),
				rs.getString("Description")
			));
		}
		rs.close();
		pstat.close();
		conn.close();
		return rooms;
	}
	
	public boolean load(int id) throws SQLException {
		boolean ret = true;
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Hotel,TypeName,Volume,Total,Booked,Price,Breakfast,Internet,Description FROM Room Where id=?",
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
			this.hotel=rs.getInt("Hotel");
			this.typeName=rs.getString("TypeName");
			this.volume=rs.getInt("Volume");
			this.total=rs.getInt("Total");
			this.booked=rs.getInt("Booked");
			this.price=rs.getInt("Price");
			this.breakfast=rs.getBoolean("Breakfast");
			this.internet=rs.getBoolean("Internet");
			this.description=rs.getString("Description");
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
						"INSERT INTO Room(Hotel,TypeName,Volume,Total,Booked,Price,Breakfast,Internet,Description) VALUES(?,?,?,?,?,?,?,?,?)",
						Statement.RETURN_GENERATED_KEYS);
		pstat.setInt(1,hotel);
		pstat.setString(2,typeName);
		pstat.setInt(3,volume);
		pstat.setInt(4,total);
		pstat.setInt(5,booked);
		pstat.setInt(6,price);
		pstat.setBoolean(7, breakfast);
		pstat.setBoolean(8, internet);
		pstat.setString(9, description);
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
		conn.createStatement().executeUpdate("DELETE FROM Room WHERE id=" + id);
		conn.close();
	}

	public void update() throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"UPDATE Room SET Hotel=?,TypeName=?,Volume=?,Total=?,Booked=?,Price=?,Breakfast=?,Internet=?,Description=? WHERE id=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1,hotel);
		pstat.setString(2,typeName);
		pstat.setInt(3,volume);
		pstat.setInt(4,total);
		pstat.setInt(5,booked);
		pstat.setInt(6,price);
		pstat.setBoolean(7, breakfast);
		pstat.setBoolean(8, internet);		
		pstat.setString(9, description);		
		pstat.setInt(10, id);
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

	public int getHotel() {
		return hotel;
	}

	public boolean hasBreakfast() {
		return breakfast;
	}

	public void setBreakfast(boolean breakfast) {
		this.breakfast = breakfast;
	}

	public boolean hasInternet() {
		return internet;
	}

	public void setInternet(boolean internet) {
		this.internet = internet;
	}

	public void setHotel(int hotel) {
		this.hotel = hotel;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = Site.maxString(typeName, 45);
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getBooked() {
		return booked;
	}

	public void setBooked(int booked) {
		this.booked = booked;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
