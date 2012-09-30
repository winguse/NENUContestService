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

public class Message {
	public static int MESSAGE_TYPE_NORMAL=0;
	public static int MESSAGE_TYPE_ROOM_EXCHANGE=1;
	public static int MESSAGE_TYPE_BOOK_DESCRIPTION=2;
	public static int MESSAGE_TYPE_BOOK_TICKET_NICK=3;
	
	public static int MESSAGE_SPEC_GET_REPLY=0;
	public static int MESSAGE_SPEC_OF_TYPE=1;
	public static int MESSAGE_SPEC_OF_BOOKROOM=2;
	public static int MESSAGE_SPEC_OF_TICKET_NICK=3;
	
	protected int id=-1;
	protected int replyId=0;
	protected int user=0;
	protected String username="";
	protected String title="";
	protected String content="";
	protected long time=0;
	protected int type=MESSAGE_TYPE_NORMAL;

	/**
	 * 获得相应类型那个的信息。<br>
	 * 比如：<br>
	 * 		1.获得某主题下面的所有回复（replyId=?）<br>
	 * 		2.获得某个类型下面的所有主题（type=? && replyId==0）<br>
	 * 		3.获得订房间的描述信息（user=? && replyId==0 && type=MESSAGE_BOOK_DESCRIPTION）<br>
	 * @param specific
	 * @param agr
	 * @return
	 * @throws SQLException 
	 */
	public static List<Message> getSpecificMessage(int specific,int agr) throws SQLException{
		String sql=null;
		if(specific==MESSAGE_SPEC_GET_REPLY){
			sql="SELECT Message.id,ReplyId,User,Title,Content,Time,Type,Username FROM Message,User Where Message.User=User.id and ReplyId=?";
		}else if(specific==MESSAGE_SPEC_OF_TYPE){
			sql="SELECT Message.id,ReplyId,User,Title,Content,Time,Type,Username FROM Message,User Where Message.User=User.id and ReplyId is NULL and Type=? ORDER BY Time DESC";
		}else if(specific==MESSAGE_SPEC_OF_BOOKROOM){
			sql="SELECT Message.id,ReplyId,User,Title,Content,Time,Type,Username FROM Message,User Where Message.User=User.id and ReplyId is NULL and Type="+MESSAGE_TYPE_BOOK_DESCRIPTION+" and User=?";
		}else if(specific==MESSAGE_SPEC_OF_TICKET_NICK){
			sql="SELECT Message.id,ReplyId,User,Title,Content,Time,Type,Username FROM Message,User Where Message.User=User.id and ReplyId is NULL and Type="+MESSAGE_TYPE_BOOK_TICKET_NICK+" and User=?";
		}else{
			return null;
		}
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						sql,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1,  agr);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		List<Message> msgs=new ArrayList<Message>();
		while(rs.next()) {
			msgs.add(new Message(
					rs.getInt("id"),rs.getInt("ReplyId"),rs.getInt("User"),rs.getString("Title"),rs.getString("content"),rs.getLong("Time"),rs.getInt("Type"),rs.getString("Username")
			));
		}
		rs.close();
		pstat.close();
		conn.close();
		return msgs;
	}
	
	public Message(){
		
	}
	
	public Message(int id, int replyId, int user, String title, String content,
			long time, int type) {
		super();
		this.id = id;
		this.replyId = replyId;
		this.user = user;
		this.setTitle(title);
		this.content = content;
		this.time = time;
		this.type = type;
	}

	public Message(int id, int replyId, int user,
			String title, String content, long time, int type, String username) {
		super();
		this.id = id;
		this.replyId = replyId;
		this.user = user;
		this.username = username;
		this.title = title;
		this.content = content;
		this.time = time;
		this.type = type;
	}

	public boolean load(int id) throws SQLException {
		boolean ret = false;
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"SELECT Message.id id,ReplyId,User,Title,Content,Time,Type,Username FROM Message,User Where Message.User=User.id and Message.id=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		pstat.setInt(1,  id);
		pstat.execute();
		ResultSet rs = pstat.getResultSet();
		if (!rs.next()) {
			ret = false;
		} else {
			ret=true;
			this.id=rs.getInt("id");
			this.replyId=rs.getInt("ReplyId");
			this.user=rs.getInt("User");
			this.title=rs.getString("Title");
			this.content=rs.getString("Content");
			this.time=rs.getLong("Time");
			this.type=rs.getInt("Type");
			this.username=rs.getString("Username");
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
						"INSERT INTO Message(ReplyId,User,Title,Content,Time,Type) VALUES(?,?,?,?,?,?)",
						Statement.RETURN_GENERATED_KEYS);
		if(replyId!=0)
			pstat.setInt(1,replyId);
		else
			pstat.setNull(1, Types.INTEGER);
		pstat.setInt(2,user);
		pstat.setString(3, title);
		pstat.setString(4, content);
		pstat.setLong(5, time);
		pstat.setInt(6, type);
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
		conn.createStatement().executeUpdate("DELETE FROM Message WHERE id=" + id);
		conn.close();
	}

	public void update() throws SQLException {
		Connection conn = Site.getDataBaseConnection();
		PreparedStatement pstat = conn
				.prepareStatement(
						"UPDATE Message SET ReplyId=?,User=?,Title=?,Content=?,Time=?,Type=? WHERE id=?",
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE,
						ResultSet.CLOSE_CURSORS_AT_COMMIT);
		if(replyId!=0)
			pstat.setInt(1,replyId);
		else
			pstat.setNull(1, Types.INTEGER);
		pstat.setInt(2,user);
		pstat.setString(3, title);
		pstat.setString(4, content);
		pstat.setLong(5, time);
		pstat.setInt(6, type);
		pstat.setInt(7, id);
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
	public int getReplyId() {
		return replyId;
	}
	public void setReplyId(int replyId) {
		this.replyId = replyId;
	}
	public int getUser() {
		return user;
	}
	public void setUser(int user) {
		this.user = user;
	}
	public String getTitle() {
		if(Site.isEmpty(title))return "[无标题]";
		return title;
	}
	public void setTitle(String title) {
		this.title = Site.maxString(title, 255);
	}
	public String getContent() {
		if(Site.isEmpty(content))return "[无内容]";
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
