package cn.edu.nenu.acm.contestservice;

import java.sql.SQLException;
import java.util.Date;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import cn.edu.nenu.acm.contestservice.modeling.objects.Log;

/**
 * Application Lifecycle Listener implementation class SessionManager
 *
 */
@WebListener
public class SessionManager implements HttpSessionListener {

    /**
     * Default constructor. 
     */
    public SessionManager() {
    }

	/**
     * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent arg0) {
        arg0.getSession().setAttribute("CSSession", new CSSession());
        System.out.println("New Session");
    }

	/**
     * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent arg0) {
    	CSSession s=(CSSession)arg0.getSession().getAttribute("CSSession");
    	if(s!=null&&s.user!=null){
    		try {
				new Log(s.user.getId(),"",new Date().getTime(),"Session time out, Logout.").add();
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
    	}
    }
	
}
