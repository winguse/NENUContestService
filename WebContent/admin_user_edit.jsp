<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*"%>
<%@ include file="../include/head_java.jspf"%>
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
		User user = new User();
		if (request.getParameter("id") != null) {
			int id = Integer.parseInt(request.getParameter("id"));
			if(request.getParameter("action")!=null&&request.getParameter("action").equals("delete")){
				try{
					User.deleteUser(id);
				}catch(Exception e){
					s.msgType=MSG.error;
					s.message="删除用户时出现错误，请检查数据库完整性约束。如果删除的是领队，检查订房信息是否已经退订！技术信息："+e.getMessage();
				}
				response.sendRedirect(s.lastPage);
				return;
			}
			if (id > 0){
				user.load(id);
			}
			if(!Site.isEmpty(request.getParameter("password")))
				user.setOrginalPassword(request.getParameter("password"));
			user.setUsername(request.getParameter("username"));
			int permission=Integer.parseInt(request.getParameter("permission"));
			if(
					(permission==User.USER_VOLUNTEER||permission==User.USER_VOLUNTEER_PEDDING)//又是要改变为志愿者
			&&	user.getPermission()!=permission//改变了角色
			&&	id>0//从数据库读取的，也就是要修改啦		
			){
				s.message="不允许在这里修改用户的角色为志愿者或者待审志愿者";
				s.msgType=MSG.error;
				response.sendRedirect(s.lastPage);
				return;
				//不允许这样修改
			}
			user.setPermission(permission);
			if(request.getParameter("confirm")!=null){
				user.setConfirm(request.getParameter("confirm").equals("1"));
			}
			if (id > 0){
				user.updateUser();
			}else{
				user.setSchool(1);
				user.add();
				if(user.getPermission()==User.USER_VOLUNTEER){
					Person p=new Person();
					p.setTitle(Person.PERSON_VOLUNTEER);
					p.setUserBelongs(user.getId());
					p.add();
				}
			}
			if(id!=-2){
				response.sendRedirect(s.lastPage);
			}else{
				%>{"code":0}<%
				return;
			}
		} else {
			if (request.getParameter("action") != null) {
				if (request.getParameter("action").equals("addleader")) {
					user.setPermission(User.USER_LEADER);
				} else if (request.getParameter("action").equals(
						"addvolunteer")) {
					user.setPermission(User.USER_VOLUNTEER);
				}
			} else if (request.getParameter("username") != null) {
				user.load(request.getParameter("username"));
			}
%>
<%@ include file="../include/head_html.jspf"%>
<title>登录用户信息管理</title>
<%@ include file="../include/body_html_java.jspf"%>
	<h1 style="text-align:center">登录用户信息管理</h1>
	<div><%=user.getId() < 1 ? "增加用户" : "编辑 ID 为 "
							+ user.getId() + " 的用户"%></div>
	<form action="admin_user_edit.jsp" method="post">
		<input type="hidden" name="id" value="<%=user.getId()%>" />
		<p>
			<label for="username">Username:</label> <input id="username"
				name="username" type="text" maxlength="40" size="20"
				value="<%=Site.htmlEncode(user.getUsername()) == null ? "" : user
							.getUsername()%>" />
		</p>
		<p>
			<label for="password">密码：</label> <input id="password"
				name="password" type="password" maxlength="40" size="20" />
		</p>
		<p>
			<label for="permission">权限：</label> 
			<select id="permission" name="permission" >
				<option value="-1">未审核</option>
				<option value="0">管理员</option>
				<option value="1">领队</option>
				<option value="2">志愿者</option>
				<option value="4">待审核志愿者</option>
			</select>
			<script>
				document.getElementById("permission").value=<%=user.getPermission()%>;
			</script>
		</p>
		<p>
			<label for="confirm">信息已确认：</label> 
			<select id="confirm" name="confirm" >
				<option value="0">No</option>
				<option value="1">Yes</option>
			</select>
			<script>
				document.getElementById("confirm").value=<%=user.isConfirm()?"1":"0"%>;
			</script>
		</p>
		<p>
			<input type="submit" value="Submit" />
		</p>
	</form>
<%
	}
%>
<%@ include file="../include/bottom_html_java.jspf"%>

