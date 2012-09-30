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
	s.lastPage=request.getRequestURI()+"?"+request.getQueryString();
%>
<%@ include file="include/head_html.jspf"%>
<title>酒店信息管理</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
	<h1 style="text-align:center">酒店信息管理</h1>
		<%
		for (Hotel h : Hotel.getAllHotels()) {
	%>
	<h3><%=Site.htmlEncode(h.getName())%></h3>
	<div class="hotel_description">
		<p>
			<b>地址：</b><%=Site.htmlEncode(h.getAddress())%>
			[<a  target="top"  href="<%=Site.htmlEncode(h.getMapURL())%>">map</a>]
		</p>
		<p><b>电话：</b><%=Site.htmlEncode(h.getTelephone())%></p>
		<p><b>描述信息：</b><br/><%=Site.htmlEncode(h.getDescription())%></p>
		<p>
			<a href="admin_hotel_edit.jsp?id=<%=h.getId() %>">编辑</a>
			<a href="admin_hotel_edit.jsp?id=<%=h.getId() %>&action=delete">删除</a>
			<a href="admin_room_edit.jsp?id=-1&hotel=<%=h.getId() %>">增加房间类型</a>
		</p>
	</div>
	<table class="table table-striped table-bordered table-condensed"><thead>
			<tr>
				<th>房间类型名</th>
				<th>互联网接入</th>
				<th>提供早餐</th>
				<th>房间容量/人</th>
				<th>房间单价</th>
				<th>剩余+已订=总数量</th>
				<th>描述信息</th>
				<th>管理选项</th>
			</tr></thead><tbody>
			<%
				int totalCount = 0, totalPrice = 0;
						for (Room r : Room.getHotelRooms(h.getId())) {
							int available = r.getTotal() - r.getBooked();
			%>
			<tr>
				<td><%=Site.htmlEncode(r.getTypeName())%></td>
				<td><%=r.hasInternet() ? "Yes" : "No"%></td>
				<td><%=r.hasBreakfast() ? "Yes" : "No"%></td>
				<td><%=r.getVolume()%></td>
				<td>￥<%=r.getPrice()%></td>
				<td><%=available%> + <%=r.getBooked()%> = <%=r.getTotal()%></td>
				<td><%=Site.htmlEncode(r.getDescription())%></td>
				<td><a href="admin_room_edit.jsp?id=<%=r.getId() %>">编辑</a> <a href="admin_room_edit.jsp?id=<%=r.getId() %>&action=delete">删除</a></td>
			</tr>
			<%
				}
			%>
	</tbody></table>
	<%
		}
	%>
	<p><a href="admin_hotel_edit.jsp?id=-1">添加酒店</a></p>
</div>
<%
	
%>
<%@ include file="include/bottom_html_java.jspf"%>

