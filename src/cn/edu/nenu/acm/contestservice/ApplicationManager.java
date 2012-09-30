package cn.edu.nenu.acm.contestservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class ApplicationManager
 *
 */
@WebListener
public class ApplicationManager implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public ApplicationManager() {
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("Servlet Context Initialized.");
		try {
			Connection conn = Site.getDataBaseConnection();
			PreparedStatement pstat=null;
			ResultSet rs=null;
			pstat = conn
					.prepareStatement(
							"SELECT Setting,Value FROM SystemSettings WHERE Setting=?",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY,
							ResultSet.CLOSE_CURSORS_AT_COMMIT);
			
			pstat.setString(1, "IndexNotice");
			pstat.execute();
			rs=pstat.getResultSet();
			if(rs.next()){
				Site.setIndexNotice(rs.getString("Value"));
			}
			rs.close();			
			
			pstat.setString(1, "Updateable");
			pstat.execute();
			rs=pstat.getResultSet();
			if(rs.next()){
				Site.setUpdateable(rs.getBoolean("Value"));
			}
			rs.close();
			
			pstat.setString(1, "OpenTime");
			pstat.execute();
			rs=pstat.getResultSet();
			if(rs.next()){
				Site.setOpenTime(rs.getLong("Value"));
			}
			rs.close();
			
			pstat.setString(1, "Coach_edit_able");
			pstat.execute();
			rs=pstat.getResultSet();
			if(rs.next()){
				Site.setCoach_edit_able(rs.getBoolean("Value"));
			}
			rs.close();
			
			pstat.setString(1, "book_able");
			pstat.execute();
			rs=pstat.getResultSet();
			if(rs.next()){
				Site.setBook_able(rs.getBoolean("Value"));
			}
			rs.close();
			
			pstat.setString(1, "volunteer_reg_able");
			pstat.execute();
			rs=pstat.getResultSet();
			if(rs.next()){
				Site.setVolunteer_reg_able(rs.getBoolean("Value"));
			}
			rs.close();
			
			pstat.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
//    	INSERT INTO `ContestService`.`SystemSettings` (`Setting`) VALUES ('IndexNotice');
//    	INSERT INTO `ContestService`.`SystemSettings` (`Setting`) VALUES ('Updateable');
//    	INSERT INTO `ContestService`.`SystemSettings` (`Setting`) VALUES ('OpenTime');
		try {
			Connection conn;
			conn = Site.getDataBaseConnection();
			PreparedStatement pstat = conn
					.prepareStatement(
							"UPDATE SystemSettings SET Value=? WHERE Setting=?",
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_UPDATABLE,
							ResultSet.CLOSE_CURSORS_AT_COMMIT);
			
			pstat.setString(1, Site.getIndexNotice());
			pstat.setString(2, "IndexNotice");
			pstat.execute();
			
			pstat.setBoolean(1, Site.isUpdateable());
			pstat.setString(2, "Updateable");
			pstat.execute();
			
			pstat.setLong(1, Site.getOpenTime());
			pstat.setString(2, "OpenTime");
			pstat.execute();
			
			pstat.setBoolean(1, Site.isCoach_edit_able());
			pstat.setString(2, "Coach_edit_able");
			pstat.execute();
		
			pstat.setBoolean(1, Site.isBook_able());
			pstat.setString(2, "book_able");
			pstat.execute();

			
			pstat.setBoolean(1, Site.isVolunteer_reg_able());
			pstat.setString(2, "volunteer_reg_able");
			pstat.execute();
			
			pstat.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
       System.out.println("Servlet Context Destoyed.");
    }
	
}
