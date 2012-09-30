package cn.edu.nenu.acm.contestservice.modeling.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cn.edu.nenu.acm.contestservice.Site;

public class Hotel {
	private int id=-1;// `id` INT NOT NULL AUTO_INCREMENT ,
	private String name="";// `Name` VARCHAR(45) NULL ,
	private String address="";// `Address` VARCHAR(255) NULL ,
	private String telephone="";// `Telephone` VARCHAR(45) NULL ,
	private String mapURL="";// `MapURL` LONGTEXT NULL ,
	private String description="";// `Description` LONGTEXT NULL ,

	public Hotel(int id, String name, String address, String telephone,
			String mapURL,String description) {
		super();
		this.id = id;
		this.setName(name);
		this.setAddress(address);
		this.setTelephone(telephone);
		this.setMapURL(mapURL);
		this.setDescription(description);
	}

	public Hotel(String name, String address, String telephone, String mapURL,String description) {
		super();
		this.setName(name);
		this.setAddress(address);
		this.setTelephone(telephone);
		this.setMapURL(mapURL);
		this.setDescription(description);
	}

	public Hotel(){
		
	}
	
	/**
	 * 返回所有酒店
	 * @return
	 * @throws SQLException 
	 */
	public static List<Hotel> getAllHotels() throws SQLException{
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Name,Address,Telephone,MapURL,Description FROM Hotel ",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.execute();
		ResultSet rs=pstat.getResultSet();
		ArrayList<Hotel> hotels=new ArrayList<Hotel>();
		while(rs.next()){
			hotels.add(new Hotel(
				rs.getInt("id"),
				rs.getString("Name"),
				rs.getString("Address"),
				rs.getString("Telephone"),
				rs.getString("MapURL"),
				rs.getString("Description")				
			));
		}
		rs.close();
		pstat.close();
		conn.close();
		return hotels;
	}
	
	public boolean load(int id) throws SQLException {
		boolean ret = true;
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Name,Address,Telephone,MapURL,Description FROM Hotel Where id=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1,  id);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		if (!rs.next()) {
			ret = false;
		} else {
			this.id=rs.getInt("id");
			name=rs.getString("Name");
			address=rs.getString("Address");
			telephone=rs.getString("Telephone");
			mapURL=rs.getString("MapURL");
			description=rs.getString("Description");
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
						"INSERT INTO Hotel(Name,Address,Telephone,MapURL,Description) VALUES(?,?,?,?,?)",
						Statement.RETURN_GENERATED_KEYS);
		pstat.setString(1,name);
		pstat.setString(2,address);
		pstat.setString(3,telephone);
		pstat.setString(4,mapURL);
		pstat.setString(5,description);
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
		conn.createStatement().executeUpdate("DELETE FROM Hotel WHERE id=" + id);
		conn.close();
	}

	public void update() throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"UPDATE Hotel SET Name=?,Address=?,Telephone=?,MapURL=?,Description=? WHERE id=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setString(1,name);
		pstat.setString(2,address);
		pstat.setString(3,telephone);
		pstat.setString(4,mapURL);
		pstat.setString(5,description);
		pstat.setInt(6,id);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = Site.maxString(name, 45);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = Site.maxString(address, 255);
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = Site.maxString(telephone, 255);
	}

	public String getMapURL() {
		return mapURL;
	}

	public void setMapURL(String mapURL) {
		this.mapURL = mapURL;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
