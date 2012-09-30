<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.roles.*,cn.edu.nenu.acm.contestservice.modeling.objects.*"%>
<%@ include file="include/head_java.jspf"%>
<%
		if (s.user == null) {
			response.sendRedirect(".");
			return;
		}
		if (s.user.getPermission() != User.USER_LEADER&&s.user.getPermission() != User.USER_ADMINISTRATOR) {
			response.sendRedirect(".");
			return;
		}
		s.lastPage=request.getRequestURI()+"?"+request.getQueryString();
		String leaderURL="";
		if(s.user.getPermission() == User.USER_ADMINISTRATOR){
			leaderURL="&leaderid="+request.getParameter("leaderid");
		}
		boolean bookable=Site.isBook_able();
		boolean updateable=Site.isUpdateable()||s.user.getPermission()==User.USER_ADMINISTRATOR;
		updateable&=bookable;
		if(s.user.isConfirm()){
			updateable=false;
		}
%>
<%@ include file="include/head_html.jspf"%>
<title>房间预订 - Booked Room</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
	<%if(!bookable){%>
		<div class="alert alert-error"><strong>请注意：</strong>酒店预订服务没有开放，如果有需要，请直接联系我们。</div>
	<%} %>
	<h1 style="text-align:center;">房间预订<br><small>Book Room</small></h1>
	<%
		for (Hotel h : Hotel.getAllHotels()) {
	%>
	<h3><%=Site.htmlEncode(h.getName())%></h3>
	<div class="hotel_description">
		<p>
			<b>地址(Address)：</b><%=Site.htmlEncode(h.getAddress())%>
			[<a  target="top"  href="<%=Site.htmlEncode(h.getMapURL())%>">map</a>]
		</p>
		<p>
			<b>电话(Telephone)：</b><%=Site.htmlEncode(h.getTelephone())%></p>
		<p><b>描述(Description)：</b><br/><%=Site.htmlEncode(h.getDescription())%></p>
	</div>
	<table class="table table-striped table-bordered table-condensed"><thead>
		
			<tr>
				<th>房间类型名<br/>(Room Type Name)</th>
				<th>互联网<br/>(Internet Access)</th>
				<th>早餐<br/>(Breakfast)</th>
				<th>单间容量<br/>(Volume)</th>
				<th>单价<br/>(Price Per Order)</th>
				<th>剩余数量<br/>(Room Available)</th>
				<th>描述信息<br/>(Description)</th>
		<%if(updateable){ %>
				<th>选择以预订<br/>(Select to Book)</th>
				<%} %>
			</tr>
			</thead>
			<tbody>
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
				<td><%=available%></td>
				<td><%=Site.htmlEncode(r.getDescription())%></td>
		<%if(updateable){ %>
				<td><select
					onchange="if(confirm('Are you sure?'))window.location='book_room?id=<%=r.getId()%>&count='+this.value+'<%=leaderURL %>';else this.value=0;">
						<option value="0">Select To Book</option>
						<%
							for (int i = 1; i <= available&&i<=10; i++) {
						%>
						<option value="<%=i%>">
							Book
							<%=i%>
							Room
						</option>
						<%
							}
						%>
				</select></td>
				<%} %>
			</tr>
			<%
				}
			%>
	</tbody></table>
	<%
		}
	%>
</div>
<%@ include file="include/bottom_html_java.jspf"%>