<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*,
	java.sql.SQLException,java.util.ArrayList"%> 
<%@ include file="include/head_java.jspf"%>
<%
if (s==null||s.user == null){
	response.sendRedirect(".");
	return;
}
if (s.user.getPermission() != User.USER_ADMINISTRATOR){
	s.message="Permisson Deny!";
	s.msgType=MSG.warning;
	response.sendRedirect(".");
	return;
}
	 ArrayList<String> loginInfo=(ArrayList<String>) request.getSession().getAttribute("loginInfo");
	 if(loginInfo==null){
		 loginInfo=new ArrayList<String>();
	 }
	 if("reset".equals(request.getParameter("action"))){
		 loginInfo.clear();
		 s.message="导入用户信息缓存已经清空。";
		 s.msgType=MSG.notice;
	 }else if("delete".equals(request.getParameter("action"))){
		 User u=new User();
		 for(String l:loginInfo){
				String[] t=l.split("\\t");
				u.load(t[0]);
				if(u.getSchool()>1)
					School.delete(u.getSchool());
				User.deleteUser(u.getId());
		 }
		 s.message="导入用户信息缓存里面的用户已经全部删除。";
		 s.msgType=MSG.notice;
		 loginInfo.clear();
	}
	int cnt=0;
	request.getSession().setAttribute("loginInfo", loginInfo);
%>
<%@ include file="include/head_html.jspf"%>
<title>已导入用户信息</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
<h1 style="text-align:center">已导入用户信息</h1>
<div class="alert">这里显示的是刚刚导入都信息。如果你觉得导入的信息有问题，或者是导入出现了错误，
可以点击最下面的删除这些用户撤销导入（值得提醒的是，具体是不是完全撤销不能保证，因为我没仔细测试，
但是基本是全部删除掉了的，当然，确定不会多删的。）情况缓存可以清空这些记录，因为每次导入都会增加在缓存中，
为了方便下一次导入，请先清空缓存。登录名是在某个学校下面的某个教练挑出一个邮箱的，从Hash表里面找，所以没有规律可循。
密码是该教练的邮箱。如果想要更好一点的用户名，例如用学校命名，那么导入完后，将所有学校都中文名补充完整，用“通知领队”模块搞。
点击发邮件连接，是引导你打开一个邮件客户端发邮件，相关的内容就是用户名密码之类的，试试就知道了。</div>
<table id="loginInfo" class="table table-striped table-bordered table-condensed tablesorter"><thead>
	<tr>
		<th>用户</th>
		<th>密码</th>
		<th>操作</th>
	</tr></thead><tbody>
	<%for(String l:loginInfo){
		String[] t=l.split("\\t");
		cnt++;
		%>
	<tr><td><%=t[0] %></td><td><%=t[1] %></td><td></td></tr>
	<%} %></tbody>
</table>
<p>共计：<%=cnt %></p>
<p><a href="?action=reset">清空缓存</a> <a style="color:red;" href="?action=delete">删除这些用户</a></p>
<script type="text/javascript">
	$("#loginInfo>tbody>tr").each(
		function(idx){
			var $ds=$(this);
			var $username=$ds.find("td:nth-child(1)");
			var $password=$ds.find("td:nth-child(2)");
			var $action=$ds.find("td:nth-child(3)");
			$action.html(
				"<a href='mailto:"+$username.text()+"?subject=ACM/ICPC亚洲区长春站赛事管理系统通知&body=你好，"+$username.text()+"！%0A%0A　　欢迎贵校参加亚洲区长春站的比赛，现在中文赛事管理系统（http://acm.nenu.edu.cn/ContestService/）已经准备就绪，请您登录更新中文信息。%0A　　您的登录名："+$username.text()+"，密码："+$password.text()+"。登录名区分大小写，密码请注意修改。%0A　　谢谢！%0A%0A赛事管理系统 管理员%0A"+new Date().toLocaleString()+"'>发电邮</a>"
			);
		}
	);
</script>
<%
//try{
%><%
/*
}catch(SQLException e){
	e.printStackTrace();
	s.message="Error Occour While Perform Your Request. Technical Detial: " +e.getMessage();
	s.msgType=MSG.error;
	response.sendRedirect("admin_hotel_room.jsp");
}*/
%>
</div>
<%@ include file="include/bottom_html_java.jspf"%>

