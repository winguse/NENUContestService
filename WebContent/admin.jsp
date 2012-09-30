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
	s.lastPage=request.getRequestURI()+"?"+request.getQueryString();
%>
<%@ include file="include/head_html.jspf"%>
<title>后台首页</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
	<h1 style="text-align:center">后台首页</h1>
<div class="thumbnail">
<h3>使用建议</h3>
	<ol>
		<li>使用支持HTML5的浏览器，建议用Chrome或FireFox，因为表格(点击表头)是可以排序的，这两个的JS性能比IE9好很多。</li>
		<li><b>角色扮演</b>那个选项是会登出当前用户的，建议用Chrome，对那个链接点击右键，用隐身模式就不会退出了。</li>
		<li>角色扮演的链接本质上是个登录链接，生成后5分钟内有效。设计之初是给一些人临时的权限修改对应用户的信息的。例如志愿者修改教练的信息。也可以发给教练临时登录用。</li>
		<li>没了</li>
	</ol>
</div>
<div class="thumbnail">
<h3>更新日志</h3>
	<ul>
		<li>
			<h6>9-8</h6>
			<ol>
				<li>志愿者注册功能，包括注册、审核、注册开发控制。</li>
			</ol>
		</li>
		<li>
			<h6>9-6</h6>
			<ol>
				<li>数据导入的功能，但是要求严格按照截图方法导出。注意，当前处理的是为PEDDING状态的队伍、人物，实际时一定要记得改回ACCEPT。</li>
				<li>系统重置功能，不可以恢复，清空整个数据库。</li>
				<li>队员可以有替补身份。</li>
				<li>修改样式。</li>
			</ol>
		</li>
		<li>
			<h6>4-12</h6>
			<ol>
				<li>系统设置里面可以控制带队能否更新订房信息、和增加教练的信息</li>
				<li>编辑用户那个地方，可以控制这个用户是否确认</li>
				<li>领队角色扮演链接增加了一个otl_permission的选项，token的Hash链接不校验该信息，值已经弄成1了，表示能够增加教练/队伍信息。由于暂时不校验这个权限，所以发送给别人时注意。</li>
				<li>看见删除的地方，基本都能删除了，除了订房约束外。比如删除带队的话，要把房间都退掉。<span style="color:red">请注意：不要点错删了。原来我考虑不提供删除用户的原因是系统日志表的信息想保存下来，考虑的是信息安全的分析，比如账户泄漏，被入侵什么的。当然，可能多虑。</span></li>
				<li>用JQuery选择器对所有包含删除字样的链接做了处理判断，js性能不好的浏览器可能载入时卡。换Chrome吧。</li>
			</ol>
		</li>
	</ul>
</div>
<h3>缺陷说明<br/>
<small style="color:red">有需求和错误，请及时告诉我。</small>
</h3>
<pre>
导入数据的时候，要特别注意，格式不能错，一旦错误，导入的程序会崩溃，并且不能确定在哪个地方错了。
由于使用一个连接池，每个对象访问的连接不一定是一样的，所以没办法做数据库事务，所以无法回滚。失败只能重置系统重来。
后台会输出所有中间过程，可以通过它来调试。
</pre>
<%
//try{

%>

<%
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

