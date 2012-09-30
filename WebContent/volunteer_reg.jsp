<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*,
	java.sql.SQLException"%> 
<%@ include file="include/head_java.jspf"%>
<%
if(s.user!=null){
	response.sendRedirect(".");
	return;
}
if(!Site.isVolunteer_reg_able()){
	s.message="尚未接受志愿者注册";
	s.msgType=MSG.error;
	response.sendRedirect(".");
	return;
}
try{
	String username=request.getParameter("username");
	String password=request.getParameter("password");
	String password2=request.getParameter("password2");
	if(username!=null){
		if(Site.isEmpty(username)||Site.isEmpty(password)){
			s.message="空表单项目！";
			s.msgType=MSG.error;
		}else if(!password.equals(password2)){
			s.message="两次输入的密码不一致，请检查！";
			s.msgType=MSG.error;
		}else{
			User user=new User();
			user.setUsername(username);
			user.setOrginalPassword(password);
			user.setPermission(User.USER_VOLUNTEER_PEDDING);
			user.setSchool(1);
			if(user.add()==User.ADD_NEW_USER_SUCCESS){
				Person person=new Person();
				person.setTitle(Person.PERSON_VOLUNTEER_PEDDING);
				person.setUserBelongs(user.getId());
				person.add();
				System.out.println(person.getId()+" "+user.getId());
				s.user=user;
				response.sendRedirect("volunteer.jsp");
				return;
			}else{
				s.message="用户已经存在，请重试！";
				s.msgType=MSG.error;
			}
		}
	}
}catch(SQLException e){
	e.printStackTrace();
	s.message="Error Occour While Perform Your Request. Technical Detial: " +e.getMessage();
	s.msgType=MSG.error;
	response.sendRedirect(".");
}
%>
<%@ include file="include/head_html.jspf"%>
<title>志愿者注册 - 用户注册</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
	<h1 style="margin:10px 0 30px 80px;">志愿者注册</h1>
	<div class="alert alert-info">感谢您有兴趣参加本次志愿活动，请先注册一个登录用户！</div>
	<form action="" method="post" class="form-horizontal">
		<div class="control-group">
			<label class="control-label" for="username">用户名：<br/>(Username)</label>
			<div class="controls"><input id="username" name="username" type="text" maxlength="40" size="20" /></div>
		</div>
		<div class="control-group">
			<label class="control-label" for="password">密码：<br/>(Password)</label>
			<div class="controls"><input id="password" name="password" type="password" maxlength="40" size="20" /></div>
		</div>
		<div class="control-group">
			<label class="control-label" for="password2">重复密码：<br/>(Repeat Password)</label>
			<div class="controls"><input id="password2" name="password2" type="password" maxlength="40" size="20" /></div>
		</div>
		<div class="form-actions"><input type="submit"  class="btn btn-primary"/></div>
	</form>
</div>
<%@ include file="include/bottom_html_java.jspf"%>

