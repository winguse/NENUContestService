package cn.edu.nenu.acm.contestservice.control.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.nenu.acm.contestservice.CSSession;
import cn.edu.nenu.acm.contestservice.MSG;
import cn.edu.nenu.acm.contestservice.modeling.objects.Log;
import cn.edu.nenu.acm.contestservice.modeling.objects.Message;
import cn.edu.nenu.acm.contestservice.modeling.objects.User;

/**
 * Servlet implementation class MessageUpdate
 */
@WebServlet("/message_update")
public class MessageUpdate extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MessageUpdate() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		CSSession s = (CSSession) request.getSession()
				.getAttribute("CSSession");
		if (s == null || s.user == null) {
			response.sendRedirect(".");
			return;
		}
		try {
			String action = request.getParameter("action");
			String title = request.getParameter("title");
			String content = request.getParameter("content");
			int type = Message.MESSAGE_TYPE_NORMAL, id = -1, replyId = 0;
			try {
				if (request.getParameter("id") != null)
					id = Integer.parseInt(request.getParameter("id"));
				if (request.getParameter("type") != null)
					type = Integer.parseInt(request.getParameter("type"));
				if (request.getParameter("replyid") != null)
					replyId = Integer.parseInt(request.getParameter("replyid"));
			} catch (Exception e) {
			}
			if (content == null) {
				response.sendRedirect(s.lastPage);
				return;
			}
			if (action == null) {
				action = "";
			}
			if (title == null) {
				title = "";
			}
			Message msg = null;
			if (action.equals("bookroommsg")) {
				List<Message> msglst = Message.getSpecificMessage(
						Message.MESSAGE_TYPE_BOOK_DESCRIPTION, s.user.getId());
				if (msglst.size() > 0) {
					msg = msglst.get(0);
					msg.setContent(content);
					msg.setTime(new Date().getTime());
					msg.update();
				} else {
					msg = new Message();
					msg.setTime(new Date().getTime());
					msg.setUser(s.user.getId());
					msg.setType(Message.MESSAGE_TYPE_BOOK_DESCRIPTION);
					msg.setContent(content);
					msg.add();
				}
				s.message = "您的订房备注已经更新！(Your book room description has updated.)";
				s.msgType = MSG.notice;
			}else if (action.equals("ticketnick")) {
				List<Message> msglst = Message.getSpecificMessage(
						Message.MESSAGE_SPEC_OF_TICKET_NICK, s.user.getId());
				if (msglst.size() > 0) {
					msg = msglst.get(0);
					msg.setContent(content);
					msg.setTime(new Date().getTime());
					msg.update();
				} else {
					msg = new Message();
					msg.setTime(new Date().getTime());
					msg.setUser(s.user.getId());
					msg.setType(Message.MESSAGE_TYPE_BOOK_TICKET_NICK);
					msg.setContent(content);
					msg.add();
				}
				s.message = "您的发票抬头已经更新！(Your payment title has updated.)";
				s.msgType = MSG.notice;
			}  else if (action.equals("update")) {
				msg = new Message();
				msg.load(id);
				if (msg.getUser() == s.user.getId()
						|| s.user.getPermission() == User.USER_ADMINISTRATOR) {
					msg.setContent(content);
					msg.setTitle(title);
					msg.setTime(new Date().getTime());
					msg.update();
					s.message = "Your new message has updated.";
					s.msgType = MSG.notice;
				} else {
					s.message = "Error! You May NOT Update Other's Message!";
					s.msgType = MSG.error;
					return;
				}
			} else {
				msg = new Message();
				msg.setType(type);
				msg.setContent(content);
				msg.setTitle(title);
				msg.setTime(new Date().getTime());
				msg.setReplyId(replyId);
				msg.setUser(s.user.getId());
				msg.add();
				s.message = "Your new message has posted.";
				s.msgType = MSG.notice;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			s.message = "Sorry, An SQL Error Occurs. Please Send Us The Technical Information: "
					+ e.getMessage();
			s.msgType = MSG.error;
			try {
				new Log(s.user.getId(), request.getRemoteAddr() + " / "
						+ request.getHeader("x-forwarded-for"),
						new Date().getTime(), "SQL Error. " + e.getMessage())
						.add();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			s.message = "Sorry, An Unknow Error Occurs. Please Send Us The Technical Information: "
					+ e.getMessage();
			s.msgType = MSG.error;
			try {
				new Log(s.user.getId(), request.getRemoteAddr() + " / "
						+ request.getHeader("x-forwarded-for"),
						new Date().getTime(), "Unknow Error. " + e.getMessage())
						.add();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			request.getSession().setAttribute("CSSession", s);
			response.sendRedirect(s.lastPage);
		}
	}

}
