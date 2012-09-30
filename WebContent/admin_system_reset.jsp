<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*,
	java.sql.SQLException"%> 
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
	String password=request.getParameter("password");
	if(Site.isEmpty(password)){
		s.msgType=MSG.warning;
		s.message="警告：你正在重置系统，请先输入登录密码！";
	}else{
		password = Site.hash(password, s.user.getSalt());
		if(password.equals(s.user.getPassword())){
			if(Site.isEmpty(request.getParameter("volunteer")))
				Site.getDataBaseConnection().createStatement().execute("DELETE FROM User WHERE Id!=87 AND Permission!=0");
			else
				Site.getDataBaseConnection().createStatement().execute("DELETE FROM User WHERE Id!=87 AND Permission="+User.USER_LEADER);
			Site.getDataBaseConnection().createStatement().execute("UPDATE Room SET Booked=0");
			Site.getDataBaseConnection().createStatement().execute("DELETE FROM School WHERE Id!=1");
			s.msgType=MSG.notice;
			s.message="系统已经重置！";
			response.sendRedirect(".");
			return;
		}else{
			s.msgType=MSG.error;
			s.message="错误：密码错误！<br/>警告：你正在重置系统，请先输入正确的登录密码！";
		}
		
	}
%>
<%@ include file="include/head_html.jspf"%>
<title>系统重置</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
<form action="" method="post" class="form-horizontal">
	<div class="control-group">
	<label class="control-label" for="volunteer">只清空领队：</label>
	<div class="controls">
	<input type="checkbox" name="volunteer" id="volunteer" checked="checked" />
	</div>
	</div>
	<div class="control-group">
	<label class="control-label" for="password">请输入密码：</label>
	<div class="controls">
	<input type="password" name="password" id="password" />
	</div>
	</div>
	<div class="form-actions"><input type="submit" class="btn btn-primary" /></div>
</form>
</div>
<%@ include file="include/bottom_html_java.jspf"%>

