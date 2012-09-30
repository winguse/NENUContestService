<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*"%>
<%@ include file="include/head_java.jspf"%>
<%
/*
这个系统设计有问题。
页面显示框架不整齐。
关于学校的问题，是一对多，多对多还是什么的，应该再斟酌。
领队增加学校信息的时候，让他自己选择或增加会比较好。
层次在后面各种补丁后，层次都已经打破了。MVC几乎混在一起了。
对于管理员角色扮演，otl_permission这个选项是挺好的功能，顺便otl可以发给对应的管理者，例如志愿者帮忙更新信息，可以不用带队的密码。不过，这个值目前功能没有hash校验。当然，现在用的只是能否增加队伍的权限
数据库设计保守，所有外键约束不进行自动的级联更新或删除，全部强制约束。但是几乎所有的删除功能都没完成级联删除的功能。//目前（4-12）已经启用了所有级联，由数据库实现。除了订房，因为trigger那里写，试过了，首先是无法级联触发删除，其次，和原有退订的情况全部时冲突。
对于队伍所在酒店这个功能，设计之初考虑的是志愿者通过手机登录，报告队伍下榻的酒店。考虑这种情况的是，我出去比赛的时候，看到了这样的情况，有些学校不知道队伍在哪，无法确定接送的车。貌似师大无此必要。
对于志愿者这个角色，设计之初考虑的是，先注册，然后审核使用。不过注册流程有审核阶段，然后才会出现正确的志愿者。不过，目前系统如果注册了志愿者，那么会直接显示，虽然不能登录。注册功能也没做。
*/
	if (s.user != null) {
			switch (s.user.getPermission()) {
			case User.USER_LEADER:
				response.sendRedirect("leader.jsp");
				break;
			case User.USER_ADMINISTRATOR:
				response.sendRedirect("admin.jsp");
				break;
			case User.USER_VOLUNTEER:
				response.sendRedirect("volunteer.jsp");
				break;
			case User.USER_VOLUNTEER_PEDDING:
				response.sendRedirect("volunteer.jsp");
				break;
			}
			return;
		}
%>
<%@ include file="include/head_html.jspf"%>
<title>NENU Contest Service</title>
<style>
</style>
<%@ include file="include/body_html_java.jspf"%>
<div><%=Site.getIndexNotice() %></div>
<div class="well">
<div id="login">
	<form action="login" method="post" id="login" class="form-horizontal">
		<div class="control-group">
			<label class="control-label" for="username">用户名：<br/>(Username)</label>
			<div class="controls"><input id="username" name="username" type="text" maxlength="40" size="20" /></div>
		</div>
		<div class="control-group">
			<label class="control-label" for="password">密码：<br/>(Password)</label>
			<div class="controls"><input id="password" name="password" type="password" maxlength="40" size="20" /></div>
		</div>
		<div class="form-actions"><input type="submit"  class="btn btn-primary"/></div>
	</form>
	</div>
</div>
<%@ include file="include/bottom_html_java.jspf"%>