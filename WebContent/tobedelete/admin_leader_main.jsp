<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*,cn.edu.nenu.acm.contestservice.modeling.roles.*,
	java.sql.SQLException,java.sql.Connection,java.sql.PreparedStatement,java.sql.ResultSet,java.util.List"%>
<%@ include file="include/head_java.jspf"%>
<%
	if (s.user == null)
			response.sendRedirect(".");
		if (s.user.getPermission() != User.USER_ADMINISTRATOR)
			response.sendRedirect(".");

final String person_edit_url="person_edit.jsp";
final String team_edit_url="team_edit.jsp";
final String school_update_url="school_update";
final String plan_update_url="plan_update";
%>
<%@ include file="include/head_html.jspf" %>
<title>Team Leader Main Page</title>
<%@ include file="include/body_html_java.jspf" %>
	<div id="main"  class="main_mid">
<%
try {
	int id=-1;
	if(request.getParameter("id")!=null){
		id=Integer.parseInt(request.getParameter("id"));
	}
	Leader leader=new Leader(new User(id));
	s.lastPage=request.getRequestURI()+"?"+request.getQueryString();
	%>
	<%
	leader.loadSchool();
	School school=leader.getSchool();
	%>
	<div id="school">
		<h3>Your School Information</h3>
		<form action="<%=school_update_url %>" method="post">
			<input name="id" type="hidden" value="<%=school.getId() %>" />
			<p><label for="englishname">英文名(English Name)：</label><input type="text" name="englishname" id="englishname" maxlength="255" size="60"  value="<%=Site.htmlEncode(school.getEnglishName())%>"  /></p>
			<p><label for="chinesename">中文名(Chinese Name)： </label><input type="text" name="chinesename" id="chinesename" maxlength="255" size="60"  value="<%=Site.htmlEncode(school.getChineseName())%>"  /></p>
			<p><label for="postaddress">通信地址(Post Address)： </label><input type="text" name="postaddress" id="postaddress" maxlength="255" size="60"  value="<%=Site.htmlEncode(school.getPostAddress())%>"  /></p>
			<p><label for="postcode">邮政编码(Post Code)： </label><input type="text" name="postcode" id="postcode" maxlength="6" size="60"  value="<%=Site.htmlEncode(school.getPostCode())%>"  /></p>
			<input type="hidden" name="leaderid" id="leaderid" maxlength="255" size="20"  value="<%=id%>"  />
			<p><input type="submit" value="Submit" /></p>
		</form>
	</div>
	<%if(school.getId()>0){ %>
	<h3>教练以及参赛队伍(Coaches & Teams)</h3>
	<div class="coach_list">
	<%
	leader.loadChaches();
	for(Coach coach:leader.getCoaches()){
		Person p=coach;
	%>
		<div class="coach">
			<div id="coachInfo_<%=p.getId()%>" class="coach_name">
				<p>
					<a href="<%=person_edit_url %>?idtoken=<%=s.getToken(p.getId()+person_edit_url) %>&id=<%=p.getId()%>">
					<%=Site.htmlEncode(p.getChineseName()) %>(<%=Site.htmlEncode(p.getEnglishName()) %>)</a>
					<a href="<%=person_edit_url %>?idtoken=<%=s.getToken(p.getId()+person_edit_url) %>&id=<%=p.getId()%>&action=delete">删除(Delete)</a>
				</p>
			</div>
			<div id="team_list_<%=coach.getId()%>" class="team_list">
		<%
		coach.loadMyTeams();
		for(Team t : coach.getTeams()){
		%>
				<div id="team_<%=t.getId() %>" class="team">
					<div class="team_name">
						<p><a href="<%=team_edit_url %>?idtoken=<%=s.getToken(t.getId()+team_edit_url) %>&id=<%=t.getId()%>">
						<%=Site.htmlEncode(t.getEnglishName()) %>(<%=Site.htmlEncode(t.getChineseName()) %>)</a>[<%
			switch(t.getType()){
			case Team.TEAM_NOT_VERIFIED:
				%>未审核(Not Verified)<%
				break;
			case Team.TEAM_OFFICAL:
				%>正式队(Offical)<%
				break;
			case Team.TEAM_TOURISM:
				%>酱油队(Tourism)<%
				break;
			}%>]	<a href="<%=team_edit_url %>?idtoken=<%=s.getToken(t.getId()+team_edit_url) %>&id=<%=t.getId()%>&action=delete">删除(Delete)</a>	</p>
					</div>
					<div id="team_member_<%=t.getId() %>" class="team_member_list">
				<%
				List<Person> teamMember=Person.getTeamMembers(t.getId());
				for(Person ptm:teamMember){ %>
						<p>
						<a href="<%=person_edit_url %>?idtoken=<%=s.getToken(ptm.getId()+person_edit_url)%>&id=<%=ptm.getId() %>"><%=Site.htmlEncode(ptm.getEnglishName()) %></a>
						<a href="<%=person_edit_url %>?idtoken=<%=s.getToken(ptm.getId()+person_edit_url)%>&id=<%=ptm.getId() %>&action=delete">删除(Delete)</a>
						</p>
				<%} 
				if(teamMember.size()<3){%>
						<p>
							<a href="<%=person_edit_url %>?actiontoken=<%=s.getToken(t.getId()+person_edit_url) %>&team=<%=t.getId() %>&action=addteammember&leaderid=<%=id %>">添加队员(Add Team Member)</a>
						</p>
		<% }%>
					</div>
					<div style="clear:both"></div>
				</div>
		<%
		}
		%>
				<div class="team_list_control">
					<p><a href="<%=team_edit_url %>?token=<%=s.getToken("-1"+p.getId()+team_edit_url) %>&id=-1&coach=<%=p.getId()%>&school=<%=leader.getSchool().getId()%>">添加队伍(Add a team)</a></p>
				</div>
			</div>
		</div>
		<div style="clear:both"></div><%
	}
	%>
	</div>
	<div style="clear:both"></div>
	<div class="coach_list_control"><a href="<%=person_edit_url %>?actiontoken=<%=s.getToken("addcoach"+person_edit_url) %>&action=addcoach&leaderid=<%=id %>">添加教练(Add Coach Information)</a></div>
	<%}else{ %>
	<p>您需要先录入您的学校信息才能录入您的教练、参赛队、队员等信息。(You Need To Submit Your School Information Before Enter Coach/Team/Team Member Information.)</p>
	<% }%>
	<h3>随行人员信息(Retinues Information)</h3>
	<div id="retinues">
			<% for(Person r:Person.loadSpecifyPersons(id,Person.PERSON_RETINUE)){ %>
				<a href="person_edit.jsp?idtoken=<%=s.getToken(r.getId()+person_edit_url)%>&id=<%=r.getId() %>"><%=Site.htmlEncode(r.getEnglishName()) %></a>
				<a href="person_edit.jsp?idtoken=<%=s.getToken(r.getId()+person_edit_url)%>&id=<%=r.getId() %>&action=delete">Delete</a><br/>
			<%} %>
	</div>
	<a href="<%=person_edit_url %>?actiontoken=<%=s.getToken("addretinue"+person_edit_url) %>&action=addretinue&leaderid=<%=id %>">Add Retinue Information</a>
	<%
	leader.loadMyPlans(); %>
	<h3>您的行程计划(Your Plan Information)</h3>
	<div id="plans">
		<form action="<%=plan_update_url %>" method="post">
			<input type="hidden" name="id" value="<%=leader.getPlan().getId() %>" />
			<input type="hidden" id="f_arrival" name="arrival" value="<%=leader.getPlan().getArrival() %>" />			
			<input type="hidden" id="f_leave" name="leave" value="<%=leader.getPlan().getLeave() %>" />			
			<p><label for="arrival">到达时间(Arrival Time)：</label><input  type="datetime-local" id="arrival" onchange="document.getElementById('f_arrival').value=getDateFromStr(this.value)" /></p>
			<p><label for="arrivaltraffic">达到交通方式(Arrival Traffic)：</label><input type="text"  maxlength="255" size="20"  name="arrivaltraffic" id="arrivaltraffic" value="<%=Site.htmlEncode(leader.getPlan().getArrivalTraffic())%>"/>
				<span>时间格式(Time Format)：<span style="color:red">YYYY-MM-DD</span>T<span style="color:red">HH:MM</span> ,2012-01-01T12:01</span>
			</p>
			<p><label for="leave">返程时间(Leave Time)：</label><input  type="datetime-local" name="leave" id="leave"  name="leave" onchange="document.getElementById('f_leave').value=getDateFromStr(this.value)"  />
				<span>时间格式(Time Format)：<span style="color:red">YYYY-MM-DD</span>T<span style="color:red">HH:MM</span> ,2012-01-01T12:01</span>
			</p>
			<p><label for="leavetraffic">返程交通方式(Leave Traffic)：</label><input type="text"  maxlength="255" size="20"  name="leavetraffic" id="leavetraffic" value="<%=Site.htmlEncode(leader.getPlan().getLeaveTraffic())%>"/></p>
			<p style="display:none" title="if you need, you may fill in this blank."><label for="bookticket">订票信息(Book Ticket)：</label><input type="text"  maxlength="255" size="20"  name="bookticket" id="bookticket" value="<%=Site.htmlEncode(leader.getPlan().getBookTicket())%>"/></p>
				<input type="hidden" name="leaderid" id="leaderid" maxlength="255" size="20"  value="<%=id%>"  />
			<script>
				var arrival=new Date(<%=leader.getPlan().getArrival()%>);
				var leave=new Date(<%=leader.getPlan().getLeave()%>);
				if(<%=leader.getPlan().getArrival()%>){
					document.getElementById("arrival").value=setDateStr(arrival);
					document.getElementById("leave").value=setDateStr(leave);
				}else{
					document.getElementById("arrival").value="";
					document.getElementById("leave").value="";
				}
				function setDateStr(myDate){
				        var year,month,date,hour,minute,second;
				        year=myDate.getFullYear();       
				        month=myDate.getMonth()+1;
				        if(parseInt(month,10)<10)
				            month="0"+month;
				        date=myDate.getDate();
				        if(parseInt(date,10)<10)
				            date="0"+date; 
				        hour=myDate.getHours();
				        if(parseInt(hour,10)<10)
				            hour="0"+hour;
				        minute=myDate.getMinutes();
				        if(parseInt(minute,10)<10)
				            minute="0"+minute;
				        second=myDate.getSeconds();
				        if(parseInt(second,10)<10)
				            second="0"+second;
				        DateStr=year+"-"+month+"-"+date+"T"+hour+":"+minute+":"+second;
				        return DateStr;
				}
				function getDateFromStr(str){
					if(!/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/.test(str)){
						alert("Your haved enter a wrong date. Check your format, please.");
						return -1;
					}
			        var year,month,date,hour,minute,second;
			        year=parseInt(str.slice(0,4));
			        month=parseInt(str.slice(5,7))-1;
			        date=parseInt(str.slice(8,10));
			        hour=parseInt(str.slice(11,13));
			        minute=parseInt(str.slice(14,16));
			        second=parseInt(str.slice(17,19));
			        //alert(year+" "+month+" "+date+" "+hour+" "+minute+" "+second);
			        d=new Date();
			        d.setFullYear(year,month,date);
			        d.setHours(hour,minute,second,0);
			        return d.getTime();
				}
			</script>
			<p><input type="submit" value="Submit" /></p>
		</form>
	<%%>
	</div>
	<div id="bookroom">
		<h3>酒店预订信息(Book Room Information)</h3>
		<table>
			<tbody>
				<tr><th>酒店名(Hotel)</th><th>房间类型(Room Type Name)</th><th>互联网接入(Internet Access)</th>
				<th>提供早餐(Breakfast)</th><th>房间容量/人(Volume)</th><th>房间单价(Price Per Room)</th><th>已订数量(Booked Count)</th><%if(Site.isUpdateable()){ %><th>退订数量(Select to Cancle)</th><%} %></tr>
				<%
				int totalCount=0,totalPrice=0,totalVolume=0;
				for(BookedRoomInfo b:leader.getBookedRoomInfo()){ %>
				<tr><td><%=Site.htmlEncode(b.getHotelName()) %></td><td><%=Site.htmlEncode(b.getTypeName()) %></td>
				<td><%=b.hasInternet()?"Yes":"No" %></td><td><%=b.hasBreakfast()?"Yes":"No" %></td><td><%=b.getVolume() %></td>
				<td>￥<%=b.getPrice() %></td><td><%=b.getCount() %></td>
				<td>
					<select onchange="if(confirm('Are you sure?'))window.location='cancle_book_room?leaderid=<%=id%>&id=<%=b.getId()%>&count='+this.value;else this.value=0;">
						<option value="0">0</option>
						<%for(int i=1;i<=b.getCount();i++){ %>
						<option value="<%=i%>"><%=i%></option>
						<%} %>
					</select>
				</td></tr>
				<%
				totalCount+=b.getCount();
				totalPrice+=b.getCount()*b.getPrice();
				totalVolume+=b.getCount()*b.getVolume();
				} %>
				<tr><td colspan="<%if(Site.isUpdateable()){ %>8<%}else{ %>7<%}%>">总房间数(Total Count)：<%=totalCount %>，总入住人数(Total Person)：<%=totalVolume %><%--, Total Price:￥<%=totalPrice %> --%></td></tr>
			</tbody>
		</tbody></table>
		<a href="book_room.jsp?leaderid=<%=id%>">新预订(New Order)</a>
		<%
			List<Message> msglst=Message.getSpecificMessage(Message.MESSAGE_SPEC_OF_BOOKROOM, id);
				Message msg=null;
				if(msglst.size()>0){
			msg=msglst.get(0);
				}else{
			msg=new Message();
				}
		%>
		<p style="text-indent:2em;">本服务系统为您参赛期间提供订房服务，<span style="color:red">请务必留下准确的起止日期</span>，
		以及必要的其他备注信息，我们好提前把房间给您预留出来。房间的分配、房费的缴纳以及发票等事宜均需到酒店前台办理。
		如果需要<b>拼房</b>，可以点击<a href="message_borad.jsp?type=<%=Message.MESSAGE_TYPE_ROOM_EXCHANGE%>">这里进入讨论页</a>。
		</p>
		<form action="message_update" method="post">
		<p>
			<label for="book_description_content">订房备注(Booked Remark)：</label>
				<input type="hidden" name="action" value="bookroommsg" />
				<textarea id="book_description_content" name="content" disabled="disabled" ><%=Site.htmlEncode(msg.getContent()) %></textarea>
				<input type="submit" disabled="disabled" />
		</p>
		</form>
	</div>
	</div>
	<%

} catch (SQLException e) {
	e.printStackTrace();
	%><%=Site.htmlEncode(e.getMessage())%><%
}
%>
	</div>
<%@ include file="include/bottom_html_java.jspf" %>