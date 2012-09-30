package cn.edu.nenu.acm.contestservice.modeling.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import cn.edu.nenu.acm.contestservice.PageInfo;
import cn.edu.nenu.acm.contestservice.Site;

public class Person {
	
	public static final int PERSON_UNKNOW=0;
	public static final int PERSON_VOLUNTEER=1;
	public static final int PERSON_RETINUE=2;
	public static final int PERSON_COACH=4;
	public static final int PERSON_TEAM_MEMBER=8;
	public static final int PERSON_TEAM_MEMBER_RESERVE=16;
	public static final int PERSON_VOLUNTEER_PEDDING=32;
	
	public static final int EVENT_ENTER=1;
	public static final int EVENT_NOT_ENTER=0;
	public static final int EVENT_NOT_SURE=2;
	
	private int id = -1;// `id` INT NOT NULL ,
	private int team = 0;// `Team` INT NULL DEFAULT NULL ,
	private int userBelongs = 0;
	private int title = PERSON_UNKNOW;// `Title` INT NULL DEFAULT NULL ,
	private String chineseName = "";// `ChineseName` VARCHAR(45) NULL ,
	private String englishName = "";// `EnglishName` VARCHAR(45) NULL ,
	private String idNumber = "";// `IDNumber` VARCHAR(45) NULL ,
	private boolean gender = true;// `Gender` TINYINT(1) NULL DEFAULT True ,
	private String mobile = "";// `Mobile` VARCHAR(45) NULL ,
	private int welcomeParty = EVENT_NOT_ENTER;// `WelcomeParty` INT NULL DEFAULT 0 ,
	private int awardsCeremony = EVENT_NOT_ENTER;// `AwardsCeremony` INT NULL DEFAULT
	private String major = "";// `Major` VARCHAR(45) NULL ,
	private String photo = "";// `Photo` VARCHAR(255) NULL ,
	private String email = "";// `Email` VARCHAR(255) NULL ,
	private String clothes = "";// `Clothes` VARCHAR(10) NULL ,
	private String description = "";// `Description` LONGTEXT NULL,

	public Person() {

	}

	public Person(int id) throws SQLException{
		load(id);
	}
	
	public Person(int id, int team, int userBelongs, int title,
			String chineseName, String englishName, String idNumber,
			boolean gender, String mobile, int welcomeParty,
			int awardsCeremony, String major, String photo, String email,
			String clothes, String description) {
		super();
		this.id = id;
		this.team = team;
		this.userBelongs = userBelongs;
		this.title = title;
		this.setChineseName(chineseName);
		this.setEnglishName(englishName);
		this.setIdNumber(idNumber);
		this.gender = gender;
		this.setMobile(mobile);
		this.welcomeParty = welcomeParty;
		this.awardsCeremony = awardsCeremony;
		this.setMajor(major);
		this.setPhoto(photo);
		this.setEmail(email);
		this.setClothes(clothes);
		this.description = description;
	}

	public Person(int team, int userBelongs, int title, String chineseName,
			String englishName, String idNumber, boolean gender, String mobile,
			int welcomeParty, int awardsCeremony, String major, String photo,
			String email, String clothes, String description) {
		super();
		this.team = team;
		this.userBelongs = userBelongs;
		this.title = title;
		this.setChineseName(chineseName);
		this.setEnglishName(englishName);
		this.setIdNumber(idNumber);
		this.gender = gender;
		this.setMobile(mobile);
		this.welcomeParty = welcomeParty;
		this.awardsCeremony = awardsCeremony;
		this.setMajor(major);
		this.setPhoto(photo);
		this.setEmail(email);
		this.setClothes(clothes);
		this.description = description;
	}

	/**
	 * 根据队伍ID获得对应的人物
	 * 
	 * @param team
	 * @return
	 * @throws SQLException
	 */
	public static List<Person> getTeamMembers(int _team) throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Team,UserBelongs,Title,ChineseName,EnglishName,IDNumber,Gender,Mobile,"
								+ "WelcomeParty,AwardsCeremony,Major,Photo,Email,Clothes,Description FROM Person Where Team="
								+ _team+" ORDER BY Title ASC", ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		ArrayList<Person> persons = new ArrayList<Person>();
		while (rs.next()) {
			persons.add(new Person(rs.getInt("id"), rs.getInt("Team"), rs
					.getInt("UserBelongs"), rs.getInt("Title"), rs
					.getString("ChineseName"), rs.getString("EnglishName"), rs
					.getString("IDNumber"), rs.getBoolean("Gender"), rs
					.getString("Mobile"), rs.getInt("WelcomeParty"), rs
					.getInt("AwardsCeremony"), rs.getString("Major"), rs
					.getString("Photo"), rs.getString("Email"), rs
					.getString("Clothes"), rs.getString("Description")));
		}
		pstat.close();
		conn.close();
		return persons;
	}

	/**
	 * 查找人，同时检索英文、中文名、身份证号、手机号、专业、邮箱和个人描述
	 * 
	 * @param name
	 * @return
	 */
	public static List<Person> searchPerson(String keyword, PageInfo pageInfo)
			throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT COUNT(*) FROM Person "
								+ "Where ChineseName LIKE ? OR EnglishName LIKE ? OR IDNumber LIKE ? OR"
								+ " Major LIKE ? OR Mobile LIKE ? OR Email LIKE ? OR Description LIKE ?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setString(1, "%" + keyword + "%");
		pstat.setString(2, "%" + keyword + "%");
		pstat.setString(3, "%" + keyword + "%");
		pstat.setString(4, "%" + keyword + "%");
		pstat.setString(5, "%" + keyword + "%");
		pstat.setString(6, "%" + keyword + "%");
		pstat.setString(7, "%" + keyword + "%");
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		pageInfo.totalCount = rs.getInt(1);
		pageInfo.pages = pageInfo.totalCount / pageInfo.pagesize + 1;
		if (pageInfo.page > pageInfo.pages) {
			pageInfo.page = pageInfo.pages;
		} else if (pageInfo.page < 1) {
			pageInfo.page = 1;
		}
		if (pageInfo.pagesize < 10) {
			pageInfo.pagesize = 10;
		}
		rs.close();
		pstat.close();

		pstat = conn
				.prepareStatement(
						"SELECT id,Team,UserBelongs,Title,ChineseName,EnglishName,IDNumber,Gender,Mobile,WelcomeParty,"
								+ "AwardsCeremony,Major,Photo,Email,Clothes,Description FROM Person "
								+ "Where ChineseName LIKE ? OR EnglishName LIKE ? OR IDNumber LIKE ? OR Major "
								+ "LIKE ? OR Mobile LIKE ? OR Email LIKE ? OR Description LIKE ? LIMIT ?,?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setString(1, "%" + keyword + "%");
		pstat.setString(2, "%" + keyword + "%");
		pstat.setString(3, "%" + keyword + "%");
		pstat.setString(4, "%" + keyword + "%");
		pstat.setString(5, "%" + keyword + "%");
		pstat.setString(6, "%" + keyword + "%");
		pstat.setString(7, "%" + keyword + "%");
		pstat.setInt(8, (pageInfo.page - 1) * pageInfo.pagesize);
		pstat.setInt(9, pageInfo.pagesize);
		pstat.execute();
		rs = pstat.getResultSet();
		ArrayList<Person> persons = new ArrayList<Person>();
		while (rs.next()) {
			persons.add(new Person(rs.getInt("id"), rs.getInt("Team"), rs
					.getInt("UserBelongs"), rs.getInt("Title"), rs
					.getString("ChineseName"), rs.getString("EnglishName"), rs
					.getString("IDNumber"), rs.getBoolean("Gender"), rs
					.getString("Mobile"), rs.getInt("WelcomeParty"), rs
					.getInt("AwardsCeremony"), rs.getString("Major"), rs
					.getString("Photo"), rs.getString("Email"), rs
					.getString("Clothes"), rs.getString("Description")));
		}
		rs.close();
		pstat.close();
		conn.close();
		return persons;
	}

	/**
	 * 获得参加欢迎宴会或者是颁奖典礼的人
	 * 
	 * @param eventName
	 *            - WelcomeParty or AwardsCeremony ,null 或者是不匹配则取前者
	 * @return
	 * @throws SQLException
	 */
	public static List<Person> getPeopleEnterEvent(String eventName,
			PageInfo pageInfo) throws SQLException {
		if (eventName == null || !eventName.equals("AwardsCeremony")) {
			eventName = "WelcomeParty";
		}
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn.prepareStatement(
				"SELECT COUNT(*) FROM Person " + "Where " + eventName
						+ "=true ", ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		pageInfo.totalCount = rs.getInt(1);
		pageInfo.pages = pageInfo.totalCount / pageInfo.pagesize + 1;
		if (pageInfo.page > pageInfo.pages) {
			pageInfo.page = pageInfo.pages;
		} else if (pageInfo.page < 1) {
			pageInfo.page = 1;
		}
		if (pageInfo.pagesize < 10) {
			pageInfo.pagesize = 10;
		}
		rs.close();
		pstat.close();

		pstat = conn
				.prepareStatement(
						"SELECT id,Team,UserBelongs,Title,ChineseName,EnglishName,IDNumber,Gender,Mobile,WelcomeParty,"
								+ "AwardsCeremony,Major,Photo,Email,Clothes,Description FROM Person "
								+ "Where " + eventName + "=true  LIMIT ?,?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, (pageInfo.page - 1) * pageInfo.pagesize);
		pstat.setInt(2, pageInfo.pagesize);
		pstat.execute();
		rs = pstat.getResultSet();
		ArrayList<Person> persons = new ArrayList<Person>();
		while (rs.next()) {
			persons.add(new Person(rs.getInt("id"), rs.getInt("Team"), rs
					.getInt("UserBelongs"), rs.getInt("Title"), rs
					.getString("ChineseName"), rs.getString("EnglishName"), rs
					.getString("IDNumber"), rs.getBoolean("Gender"), rs
					.getString("Mobile"), rs.getInt("WelcomeParty"), rs
					.getInt("AwardsCeremony"), rs.getString("Major"), rs
					.getString("Photo"), rs.getString("Email"), rs
					.getString("Clothes"), rs.getString("Description")));
		}
		rs.close();
		pstat.close();
		conn.close();
		return persons;
	}

	public boolean load(int id) throws SQLException {
		boolean ret = true;
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Team,UserBelongs,Title,ChineseName,EnglishName,IDNumber,Gender,Mobile,WelcomeParty,"
								+ "AwardsCeremony,Major,Photo,Email,Clothes,Description "
								+ " FROM Person Where id=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, id);
		if (pstat.execute() == false) {
			ret = false;
		} else {
			ResultSet rs = pstat.getResultSet();
			if (rs.next()) {
				this.id = rs.getInt("id");
				team = rs.getInt("Team");
				userBelongs = rs.getInt("UserBelongs");
				title = rs.getInt("Title");
				chineseName = rs.getString("ChineseName");
				englishName = rs.getString("EnglishName");
				idNumber = rs.getString("IDNumber");
				gender = rs.getBoolean("Gender");
				mobile = rs.getString("Mobile");
				welcomeParty = rs.getInt("WelcomeParty");
				awardsCeremony = rs.getInt("AwardsCeremony");
				major = rs.getString("Major");
				photo = rs.getString("Photo");
				email = rs.getString("Email");
				clothes = rs.getString("Clothes");
				description = rs.getString("Description");
			} else {
				this.id = -1;
				System.out.println("out found...");
			}
			rs.close();
		}
		pstat.close();
		conn.close();
		return ret;
	}

	public void add() throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"INSERT INTO Person(Team,UserBelongs,Title,ChineseName,EnglishName,IDNumber,Gender,Mobile,"
								+ "WelcomeParty,AwardsCeremony,Major,Photo,Email,Clothes,Description)"
								+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
								Statement.RETURN_GENERATED_KEYS);
		if(team==0){
			pstat.setNull(1, Types.INTEGER);
		}else{
			pstat.setInt(1, team);
		}
		pstat.setInt(2, userBelongs);
		pstat.setInt(3, title);
		pstat.setString(4, chineseName);
		pstat.setString(5, englishName);
		pstat.setString(6, idNumber);
		pstat.setBoolean(7, gender);
		pstat.setString(8, mobile);
		pstat.setInt(9, welcomeParty);
		pstat.setInt(10, awardsCeremony);
		pstat.setString(11, major);
		pstat.setString(12, photo);
		pstat.setString(13, email);
		pstat.setString(14, clothes);
		pstat.setString(15, description);
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
		conn.createStatement().executeUpdate(
				"DELETE FROM Person WHERE id=" + id);
		conn.close();
	}

	public void update() throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"UPDATE Person SET Team=?,UserBelongs=?,Title=?,ChineseName=?,EnglishName=?,IDNumber=?,Gender=?,Mobile=?,"
								+ "WelcomeParty=?,AwardsCeremony=?,Major=?,Photo=?,Email=?,Clothes=?,Description=?"
								+ " WHERE id=?", ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		if(team==0){
			pstat.setNull(1, Types.INTEGER);
		}else{
			pstat.setInt(1, team);
		}
		pstat.setInt(2, userBelongs);
		pstat.setInt(3, title);
		pstat.setString(4, chineseName);
		pstat.setString(5, englishName);
		pstat.setString(6, idNumber);
		pstat.setBoolean(7, gender);
		pstat.setString(8, mobile);
		pstat.setInt(9, welcomeParty);
		pstat.setInt(10, awardsCeremony);
		pstat.setString(11, major);
		pstat.setString(12, photo);
		pstat.setString(13, email);
		pstat.setString(14, clothes);
		pstat.setString(15, description);
		pstat.setInt(16, id);
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

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public int getTitle() {
		return title;
	}

	public void setTitle(int title) {
		this.title = title;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = Site.maxString(chineseName, 45);
		;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = Site.maxString(englishName, 45);
		;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = Site.maxString(idNumber, 45);
		;
	}

	public boolean getGender() {
		return gender;
	}

	public void setGender(boolean gender) {
		this.gender = gender;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = Site.maxString(mobile, 45);
		;
	}

	public int enterWelcomeParty() {
		return welcomeParty;
	}

	public void setWelcomeParty(int welcomeParty) {
		this.welcomeParty = welcomeParty;
	}

	public int enterAwardsCeremony() {
		return awardsCeremony;
	}

	public void setAwardsCeremony(int awardsCeremony) {
		this.awardsCeremony = awardsCeremony;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = Site.maxString(major, 45);
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = Site.maxString(photo, 255);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = Site.maxString(email, 255);
	}

	public String getClothes() {
		return clothes;
	}

	public void setClothes(String clothes) {
		this.clothes = Site.maxString(clothes, 10);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static List<Person> loadSpecifyPersons(int userId,int title) throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Team,UserBelongs,Title,ChineseName,EnglishName,IDNumber,Gender,Mobile,WelcomeParty,"
								+ "AwardsCeremony,Major,Photo,Email,Clothes,Description "
								+ " FROM Person Where UserBelongs=? AND Title=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, userId);
		pstat.setInt(2, title);
		pstat.execute();
		List<Person> persons = new ArrayList<Person>();
		ResultSet rs = pstat.getResultSet();
		while (rs.next()) {
			persons.add(new Person(rs.getInt("id"), rs.getInt("Team"), rs
					.getInt("UserBelongs"), rs.getInt("Title"), rs
					.getString("ChineseName"), rs.getString("EnglishName"), rs
					.getString("IDNumber"), rs.getBoolean("Gender"), rs
					.getString("Mobile"), rs.getInt("WelcomeParty"), rs
					.getInt("AwardsCeremony"), rs.getString("Major"), rs
					.getString("Photo"), rs.getString("Email"), rs
					.getString("Clothes"), rs.getString("Description")));
		}
		rs.close();
		pstat.close();
		conn.close();
		return persons;
	}

	public static List<Person> loadSpecifyPersons(int title) throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Team,UserBelongs,Title,ChineseName,EnglishName,IDNumber,Gender,Mobile,WelcomeParty,"
								+ "AwardsCeremony,Major,Photo,Email,Clothes,Description "
								+ " FROM Person Where Title=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, title);
		pstat.execute();
		List<Person> persons = new ArrayList<Person>();
		ResultSet rs = pstat.getResultSet();
		while (rs.next()) {
			persons.add(new Person(rs.getInt("id"), rs.getInt("Team"), rs
					.getInt("UserBelongs"), rs.getInt("Title"), rs
					.getString("ChineseName"), rs.getString("EnglishName"), rs
					.getString("IDNumber"), rs.getBoolean("Gender"), rs
					.getString("Mobile"), rs.getInt("WelcomeParty"), rs
					.getInt("AwardsCeremony"), rs.getString("Major"), rs
					.getString("Photo"), rs.getString("Email"), rs
					.getString("Clothes"), rs.getString("Description")));
		}
		rs.close();
		pstat.close();
		conn.close();
		return persons;
	}
	public int getUserBelongs() {
		return userBelongs;
	}

	public void setUserBelongs(int userBelongs) {
		this.userBelongs = userBelongs;
	}

	public static List<Coach> loadCoaches(int userId) throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Team,UserBelongs,Title,ChineseName,EnglishName,IDNumber,Gender,Mobile,WelcomeParty,"
								+ "AwardsCeremony,Major,Photo,Email,Clothes,Description "
								+ " FROM Person Where UserBelongs=? AND Title=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, userId);
		pstat.setInt(2, PERSON_COACH);
		List<Coach> coaches = new ArrayList<Coach>();
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		while (rs.next()) {
			coaches.add(new Coach(rs.getInt("id"), rs.getInt("Team"), rs
					.getInt("UserBelongs"), rs.getInt("Title"), rs
					.getString("ChineseName"), rs.getString("EnglishName"), rs
					.getString("IDNumber"), rs.getBoolean("Gender"), rs
					.getString("Mobile"), rs.getInt("WelcomeParty"), rs
					.getInt("AwardsCeremony"), rs.getString("Major"), rs
					.getString("Photo"), rs.getString("Email"), rs
					.getString("Clothes"), rs.getString("Description")));
		}
		rs.close();
		pstat.close();
		conn.close();
		return coaches;
	}

}
