package cn.edu.nenu.acm.contestservice.modeling.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.sun.xml.internal.ws.org.objectweb.asm.Type;

import cn.edu.nenu.acm.contestservice.Site;

public class User {
	public static final int LOGIN_SUCCESS = 0;
	public static final int USER_NOT_EXIST = 1;
	public static final int WRONG_PASSWORD = 2;

	public static final int ADD_NEW_USER_SUCCESS = 3;
	public static final int USERNAME_ALREADY_EXIST = 4;

	public final static int USER_NOT_EXAMED = -1;
	public final static int USER_ADMINISTRATOR = 0;
	public final static int USER_LEADER = 1;
	public final static int USER_VOLUNTEER = 2;
	public final static int USER_VOLUNTEER_PEDDING = 4;

	private int id = -1;
	private String username = null;
	private String password = null;
	private String salt = null;
	private int permission = USER_NOT_EXAMED;
	private int school = 0;
	private boolean confirm = false;

	public User() {
		this.salt = Site.generateSalt();
	}

	public User(int id) throws SQLException {
		this.load(id);
	}

	public User(int id, String username, String password, String salt,
			int permission, int school, boolean confirm) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.salt = salt;
		this.permission = permission;
		this.school = school;
		this.confirm = confirm;
	}
	public User(int id, String username, String password, String salt,
			int permission,int school) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.salt = salt;
		this.permission = permission;
		this.school = school;
	}
	public User(String username, String orginalPassword, int permission) {
		super();
		this.salt = Site.generateSalt();
		this.setUsername(username);
		this.setOrginalPassword(orginalPassword);
		this.permission = permission;
	}

	/**
	 * 根据用户ID装载用户信息
	 * 
	 * @param id
	 * @return True - 加载成功；False - 反之
	 * @throws SQLException
	 */
	public boolean load(int id) throws SQLException {
		boolean ret = true;
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Username,Password,Salt,Permission,School,Confirm FROM User Where id=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, id);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		if (rs.next()) {
			this.id = rs.getInt("id");
			this.username = rs.getString("Username");
			this.password = rs.getString("Password");
			this.salt = rs.getString("Salt");
			this.permission = rs.getInt("Permission");
			this.school = rs.getInt("School");
			this.confirm = rs.getBoolean("Confirm");
		}
		rs.close();
		pstat.close();
		conn.close();
		return ret;
	}

	/**
	 * 加载属于特定学校的人物信息，例如某个学校下面的所有带队或者服务某个学校的所有志愿者
	 * 
	 * @param permisson
	 *            权限，查看定义
	 * @param school
	 *            学校ID，如果为0，则选择NULL的外键约束的，<0则忽略该键值
	 * @return List<User>
	 * @throws SQLException
	 */
	public static List<User> loadSpecifyUser(int permission, int school)
			throws SQLException {
		List<User> ret = new ArrayList<User>();
		String sql = "SELECT id,Username,Password,Salt,Permission,School,Confirm FROM User Where Permission=? ";
		if (school >= 0) {
			sql += " and School=?";
		}
		sql+=" ORDER BY Username";
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn.prepareStatement(sql,
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
				ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1, permission);
		if (school == 0) {
			pstat.setNull(2, Types.INTEGER);
		} else if (school > 0) {
			pstat.setInt(2, school);
		}
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		while (rs.next()) {
			ret.add(new User(rs.getInt("id"), rs.getString("Username"),
					rs.getString("Password"), rs.getString("Salt"),
					rs.getInt("Permission"), rs.getInt("School"),
					rs.getBoolean("Confirm")));
		}
		rs.close();
		pstat.close();
		conn.close();
		return ret;
	}

	/**
	 * 加载用户信息，根据用户名
	 * 
	 * @param username
	 * @return True - 如果用户存在 False - 反之
	 * @throws SQLException
	 */
	public boolean load(String username) throws SQLException {
		boolean ret = true;
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT id,Username,Password,Salt,Permission,School,Confirm FROM User Where Username=?",
						Statement.RETURN_GENERATED_KEYS);
		pstat.setString(1, username);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		if (!rs.next()) {
			ret = false;
		} else {
			this.id = rs.getInt("id");
			this.username = rs.getString("Username");
			this.password = rs.getString("Password");
			this.salt = rs.getString("Salt");
			this.permission = rs.getInt("Permission");
			this.school = rs.getInt("School");
			this.confirm = rs.getBoolean("Confirm");
		}
		rs.close();
		pstat.close();
		conn.close();
		return ret;
	}

	/**
	 * 用户登录方法，如果登录成功，User对象将加载所有用户数据
	 * 
	 * @param username
	 * @param originalPassword
	 * @return USER_NOT_EXIST,LOGIN_SUCCESS,WRONG_PASSWORD
	 * @throws SQLException
	 */
	public int login(String username, String originalPassword)
			throws SQLException {
		// System.out.println(username);
		if (!load(username))
			return USER_NOT_EXIST;
		System.out.println("database: [" + password + "] - submit: ["
				+ Site.hash(originalPassword, salt) + "]");
		if (!password.equals(Site.hash(originalPassword, salt)))
			return WRONG_PASSWORD;
		return LOGIN_SUCCESS;
	}

	/**
	 * 增加用户，往数据库里面写入
	 * 
	 * @return USERNAME_ALREADY_EXIST, ADD_NEW_USER_SUCCESS
	 * @throws SQLException
	 */
	public int add() throws SQLException {
		if (new User().load(username))
			return USERNAME_ALREADY_EXIST;
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"INSERT INTO User(Username,Password,Salt,Permission,School,Confirm) VALUES(?,?,?,?,?,?)",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setString(1, username);
		pstat.setString(2, password);
		pstat.setString(3, salt);
		pstat.setInt(4, permission);
		if (school == 0) {
			pstat.setNull(5, Types.INTEGER);
		} else {
			pstat.setInt(5, school);
		}
		pstat.setBoolean(6, confirm);
		pstat.execute();
		pstat.close();
		conn.close();
		load(username);// 加载新ID回到本对象
		return ADD_NEW_USER_SUCCESS;
	}

	/**
	 * 根据ID删除用户
	 * 
	 * @param id
	 * @throws SQLException
	 */
	public static void deleteUser(int id) throws SQLException {
		//这里有好多要做呢，级联的问题
		Connection conn = Site.getDataBaseConnection();
		conn.createStatement().executeUpdate("DELETE FROM User WHERE id=" + id);
		conn.close();
	}

	/**
	 * 将各个属性写入数据库，根据ID判断写入地址，ID必须已知，ID<0则直接终止
	 * 
	 * @throws SQLException
	 */
	public void updateUser() throws SQLException {
		if (id < 0)
			return;
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"UPDATE User SET Username=?,Password=?,Salt=?,Permission=?,School=?,Confirm=? WHERE id="
								+ id, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setString(1, username);
		pstat.setString(2, password);
		pstat.setString(3, salt);
		pstat.setInt(4, permission);
		if (school == 0) {
			pstat.setNull(5, Types.INTEGER);
		} else {
			pstat.setInt(5, school);
		}
		pstat.setBoolean(6, confirm);
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = Site.maxString(username, 255);
	}

	public String getPassword() {
		return password;
	}

	/**
	 * 次数password为hash之后的值，这里不作长度判断，但是超长的话……既然没Hash过，所以我管不了
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public void setOrginalPassword(String orginalPassword) {
		if(Site.isEmpty(orginalPassword))return;
		if (salt == null)
			salt = Site.generateSalt();
		password = Site.hash(orginalPassword, salt);
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		if (salt == null)
			this.salt = Site.generateSalt();
		else
			this.salt = salt;
	}

	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public int getSchool() {
		return school;
	}

	public void setSchool(int school) {
		this.school = school;
	}

	public boolean isConfirm() {
		return confirm;
	}

	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}

}
