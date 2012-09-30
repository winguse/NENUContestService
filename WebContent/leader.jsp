<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="
	cn.edu.nenu.acm.contestservice.*,
	cn.edu.nenu.acm.contestservice.modeling.roles.*,
	cn.edu.nenu.acm.contestservice.modeling.objects.*,
	java.sql.SQLException,
	java.util.List,java.util.Date,java.text.SimpleDateFormat"%>
<%@ include file="include/head_java.jspf" %>
<%
if(s==null){
	response.sendRedirect("logout.jsp");
	return;
}
if(s.user==null){
	response.sendRedirect(".");
	return;
}
if(s.user.getPermission()!=User.USER_LEADER){
	response.sendRedirect(".");
	return;
}
boolean bookable=Site.isBook_able();//关于订房，目前即使是管理员来了，订房信息还是不能改，所以，有必要好好弄一下
boolean updateable=Site.isUpdateable();//下面还得根据领队是否确认判断一次
boolean coach_edit_able=Site.isCoach_edit_able();
boolean accept_reserve_team_member=false;
if(s.otl_permission==1){
	coach_edit_able=true;
}
SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
s.lastPage=request.getRequestURI()+"?"+request.getQueryString();
final String person_edit_url="person_edit.jsp";
final String team_edit_url="team_edit.jsp";
final String school_update_url="school_update";
final String plan_update_url="plan_update";
%>
<%@ include file="include/head_html.jspf" %>
<title>领队首页 - Team Leader Main Page</title>
<%@ include file="include/body_html_java.jspf" %>
<%
try {
	Leader leader=new Leader(s.user);
	updateable=Site.isUpdateable()&&!leader.getUser().isConfirm();
%><%
	leader.loadSchool();
	School school=leader.getSchool();
	%>
	<div id="confirm"  class="alert alert-info">
	<i class="icon-info-sign  icon-white"></i>
	<%if(!Site.isUpdateable()){
		%>现在系统已经冻结所有信息，您目前仅可以查看，不可更新。如果需要更新，请联系我们，谢谢！<%
	}else if(leader.getUser().isConfirm()){ %>
		谢谢您对我们工作的配合，您的信息已经确认，现在您只可以查看您的相关信息和更新您的行程计划，如有错漏，请联系我们。
	<%}else{ %>
		<script type="text/javascript">
		var confirmable=true;
		var confirmmsg="";
		function confirmcheck(){
			if(confirmable){
				if(confirm("您确定已经完成所有信息的校验，并且无误可以最终确定了吗？")){
					window.location="confirm";
				}
			}else{
				alert(confirmmsg);
			}
		}
		</script>
		如果您已经完成所有信息的录入，并且确认无误，请<b><a style="color:#b94a48" href="#" onclick="confirmcheck();">点击这里确认</a></b>。一旦确认，除行程计划外，其他信息将不可再进行更改（包括房间预订）。
	<%} %>
	</div>
	<div id="bookroom">
		<h3>酒店预订信息<br/><small>Book Room Information</small></h3>
		<div class="well">
		<table class="table table-striped table-bordered table-condensed"><thead>
				<tr><th>酒店名<br/>(Hotel)</th><th>房间类型<br/>(Room Type Name)</th><th>互联网接入<br/>(Internet Access)</th>
				<th>提供早餐<br/>(Breakfast)</th><th>房间容量/人<br/>(Volume)</th><th>房间单价<br/>(Price Per Room)</th><th>已订数量<br/>(Booked Count)</th><%if(updateable&&bookable){ %><th>退订数量<br/>(Select to Cancle)</th><%} %></tr>
				</thead>
			<tbody>
				<%
				int totalCount=0,totalPrice=0,totalVolume=0;
				for(BookedRoomInfo b:leader.getBookedRoomInfo()){ %>
				<tr><td><%=Site.htmlEncode(b.getHotelName()) %></td><td><%=Site.htmlEncode(b.getTypeName()) %></td>
				<td><%=b.hasInternet()?"Yes":"No" %></td><td><%=b.hasBreakfast()?"Yes":"No" %></td><td><%=b.getVolume() %></td>
				<td>￥<%=b.getPrice() %></td><td><%=b.getCount() %></td>
				<%if(updateable&&bookable){ %>
				<td>
					<select onchange="if(confirm('Are you sure?'))window.location='cancle_book_room?id=<%=b.getId()%>&count='+this.value;else this.value=0;">
						<option value="0">0</option>
						<%for(int i=1;i<=b.getCount();i++){ %>
						<option value="<%=i%>"><%=i%></option>
						<%} %>
					</select>
				</td><%} %></tr>
				<%
				totalCount+=b.getCount();
				totalPrice+=b.getCount()*b.getPrice();
				totalVolume+=b.getCount()*b.getVolume();
				} %>
		</tbody></table>
		<table class="table table-striped table-bordered table-condensed"><tbody>
				<tr><td>总房间数(Total Count)：<%=totalCount %>，总入住人数(Total Person)：<%=totalVolume %><%--, Total Price:￥<%=totalPrice %> --%></td></tr>
		</tbody></table>
		<%if(updateable){ %>
			<%if(bookable){%>
		<p align="right"><a href="book_room.jsp" class="btn btn-primary"><i class=" icon-plus-sign icon-white"></i> 新预订(New Order)</a></p>
		<%}else{ %>
		<div class="alert alert-error"><strong>请注意：</strong>酒店预订服务没有开放，如果有需要，请直接联系我们。</div>
		<%} %>
		<%} %>
		<%
		List<Message> msglst=null;
		Message msg=null;
		 msglst=Message.getSpecificMessage(Message.MESSAGE_SPEC_OF_BOOKROOM, leader.getUser().getId());
				if(msglst.size()>0){
			msg=msglst.get(0);
				}else{
			msg=new Message();%>
			<script type="text/javascript">confirmable=false;confirmmsg+="您还没有留下您的订房描述备注！（即使没有订房，也请点击一下提交按钮，以保证系统校验完整，谢谢！）\n\n";</script>
			<%
				}
		%>
		<p style="text-indent:2em;">本服务系统为您参赛期间提供订房服务，<span style="color:red">请务必留下准确的起止日期</span>，
		以及必要的其他备注信息，我们好提前把房间给您预留出来。房间的分配、房费的缴纳以及发票等事宜均需到酒店前台办理。
		如果需要<b>拼房</b>，可以点击<a href="message_borad.jsp?type=<%=Message.MESSAGE_TYPE_ROOM_EXCHANGE%>">这里进入讨论页</a>。
		</p>
		<form action="message_update" method="post" class="form-horizontal">
		<div class="control-group">
			<label for="book_description_content" class="control-label">订房备注：<br/>(Booked Remark)</label>
				<input type="hidden" name="action" value="bookroommsg" />
				<div class="controls">
				<textarea id="book_description_content" name="content" cols="80" rows="4" ><%=Site.htmlEncodeNoBr(msg.getContent()) %></textarea>
				</div>
		</div>
		<div class="form-actions"><input type="submit" class="btn btn-primary" /></div>
		</form>
		</div>
	</div>
	<div id="sharingroominformation">
		<h3>拼房信息<br/><small>Sharing Room Information</small></h3>
		<div class="well">
	<%
	msglst=Message.getSpecificMessage(Message.MESSAGE_SPEC_OF_TYPE, Message.MESSAGE_TYPE_ROOM_EXCHANGE);
	for(Message message:msglst){
		List<Message> replymsglst=Message.getSpecificMessage(Message.MESSAGE_SPEC_GET_REPLY, message.getId());
	%>
<div class="message thumbnail">
	<a href="message_content.jsp?id=<%=message.getId()%>"><b><%=Site.htmlEncode(message.getTitle()) %></b></a> (回复：<%=replymsglst.size() %><%if(replymsglst.size()>0){ %>，最后时间：<%=df.format(new Date(replymsglst.get(replymsglst.size()-1).getTime())) %>，by：<%=Site.htmlEncode(replymsglst.get(replymsglst.size()-1).getUsername())%><%} %>)
	<span class="pull-right"><span class="user"><%=Site.htmlEncode(message.getUsername()) %></span> <i><%=df.format((new Date(message.getTime()))) %></i></span>
</div>
<%}%>
<%if(msglst.size()==0){ %>
<div class="message thumbnail"><b>暂无主题，您可以抢沙发了！(There is no existing post, be first to post!)</b></div>
<%}%>
<div><a class="btn btn-primary pull-right" href="message_borad.jsp?type=1">更多(More)</a></div>
</div>
</div>
		<%
		msglst=Message.getSpecificMessage(Message.MESSAGE_SPEC_OF_TICKET_NICK, leader.getUser().getId());
				if(msglst.size()>0){
			msg=msglst.get(0);
				}else{
			msg=new Message();%>
			<script type="text/javascript">confirmable=false;confirmmsg+="您还没有留下您的发票抬头信息！\n";</script>
			<%
				}
		%>
		<h3>付款信息<br/><small>Payment Information</small></h3>
	<div id="ticketnick" class="well">
		<form action="message_update" method="post" class="form-horizontal">
				<input type="hidden" name="action" value="ticketnick" />
		<div class="control-group">
			<label for="_ticketnick"  class="control-label">发票抬头：<br/>(Payment Title)</label>
				<div class="controls">
				<input id="_ticketnick" name="content" value="<%=Site.htmlEncode(msg.getContent()) %>"/>
				</div>
		</div>
		<div class="form-actions"><input type="submit"  class="btn btn-primary"/></div>
		</form>
	</div>
	<div id="school">
		<h3>学校信息<br/><small>Your School Information</small></h3>
		<div class="well">
		<form action="<%=school_update_url %>" method="post" class="form-horizontal">
			<input name="id" type="hidden" value="<%=school.getId() %>" />
			<div class="control-group"><label  class="control-label" for="englishname">英文名：<br/>(English Name)</label>
			<div class="controls"><input type="text" name="englishname" id="englishname" maxlength="255" size="60"  value="<%=Site.htmlEncode(school.getEnglishName())%>"  /></div></div>
			<div class="control-group"><label  class="control-label"  for="chinesename">中文名：<br/>(Chinese Name) </label>
			<div class="controls"><input type="text" name="chinesename" id="chinesename" maxlength="255" size="60"  value="<%=Site.htmlEncode(school.getChineseName())%>"  /></div></div>
			<div class="control-group"><label  class="control-label" for="postaddress">通信地址：<br/>(Post Address)</label>
			<div class="controls"><input type="text" name="postaddress" id="postaddress" maxlength="255" size="60"  value="<%=Site.htmlEncode(school.getPostAddress())%>"  /></div></div>
			<div class="control-group"><label  class="control-label" for="postcode">邮政编码：<br/>(Post Code) </label>
			<div class="controls"><input type="text" name="postcode" id="postcode" maxlength="6" size="60"  value="<%=Site.htmlEncode(school.getPostCode())%>"  /></div></div>
			<%if(updateable){ %>
			<input name="idtoken" type="hidden" value="<%=s.getToken(school.getId()+school_update_url) %>" />
			<%} %>
			<div class="form-actions"><input type="submit"  class="btn btn-primary"/></div>
		</form>
		</div>
	</div>
	<%if(school.getId()>0){ %>
	<h3>教练及参赛队伍<br/><small>Coaches &amp; Teams</small></h3>
	<div  class="alert alert-info"><i class="icon-info-sign  icon-white"></i> 请将鼠标放在队伍上，如果有审核反馈，可以看到相应的审核反馈信息。如果<b style="color:red">没有队伍的教练，请以随行人员身份添加</b>，以便于管理，谢谢！</div>
	<div class="well">
	<div class="coach_list">
	<%
	leader.loadChaches();
	for(Coach coach:leader.getCoaches()){
		Person p=coach;
	%>
		<div class="coach">
			<div id="coachInfo_<%=p.getId()%>" class="coach_name">
				<p>
					<a href="<%=person_edit_url %>?idtoken=<%=s.getToken(p.getId()+person_edit_url) %>&id=<%=p.getId()%>"
					 rel="popover" data-content="点击链接进行教练个人信息的编辑，包括中英文名，服装等信息。" data-original-title="教练">
					<%=Site.htmlEncode(p.getChineseName()) %>(<%=Site.htmlEncode(p.getEnglishName()) %>)</a>
				<%if(updateable&&coach_edit_able){ %>
					<a href="<%=person_edit_url %>?idtoken=<%=s.getToken(p.getId()+person_edit_url) %>&id=<%=p.getId()%>&action=delete"  title="删除(Delete)"><i class="icon-trash"></i></a>
				<%} %>
				</p>
			</div>
			<div id="team_list_<%=coach.getId()%>" class="team_list">
		<%
		coach.loadMyTeams();
		for(Team t : coach.getTeams()){
		%>
				<div id="team_<%=t.getId() %>" class="team">
					<div class="team_name">
						<p><a href="<%=team_edit_url %>?idtoken=<%=s.getToken(t.getId()+team_edit_url) %>&id=<%=t.getId()%>"
					 rel="popover" data-content="点击链接进行队伍信息的编辑，包括中英文。<%
							String feedback="";
						if(!Site.isEmpty(t.getStatusDescription())){
							String[] status=t.getStatusDescription().split("\\|");
							if(status.length>=1){
								feedback=status[0];
							}
						}
					 if(!Site.isEmpty(feedback)){ %>&lt;br/&gt;&lt;b&gt;审核反馈：&lt;/b&gt;<%=feedback %><%} %>" data-original-title="队伍">
						<%=Site.htmlEncode(t.getChineseName()) %>(<%=Site.htmlEncode(t.getEnglishName()) %>)</a><br/><%
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
			case Team.TEAM_OFFICAL_UNDER_VERIFIED:
				%>待审正式队(Offical [Under Verifying])<%
				break;
			case Team.TEAM_TOURISM_VERIFIED:
				%>待审酱油队(Tourism [Under Verifying])<%
				break;
			case Team.TEAM_OFFICAL_VERIFIED:
				%>正式队（已审）(Offical Verified)<%
				break;
			case Team.TEAM_OFFICAL_UNDER_VERIFIED_AGAIN:
				%>正式队（待再审）(Offical Need Verified Again)<%
				break;
			}%>	<%if(updateable&&coach_edit_able){ %>
			<a href="<%=team_edit_url %>?idtoken=<%=s.getToken(t.getId()+team_edit_url) %>&id=<%=t.getId()%>&action=delete"  title="删除(Delete)"><i class="icon-trash"></i></a>
			<%} %>
			</p>
					</div>
					<div id="team_member_<%=t.getId() %>" class="team_member_list">
				<%
				List<Person> teamMember=Person.getTeamMembers(t.getId());
				int reserveTeamMember=0;
				for(Person ptm:teamMember){
					
					%>
						<p>
						<a href="<%=person_edit_url %>?idtoken=<%=s.getToken(ptm.getId()+person_edit_url)%>&id=<%=ptm.getId() %>"
					 rel="popover" data-content="点击链接进行队员个人信息的编辑，包括中英文名、服装等信息" data-original-title="<%
					 if(ptm.getTitle()==Person.PERSON_TEAM_MEMBER_RESERVE){
						 reserveTeamMember++; %>替补队员" style="color:#f70;<%}else{%>队员<%}%>"><%=Site.htmlEncode(ptm.getChineseName()) %>(<%=Site.htmlEncode(ptm.getEnglishName()) %>)</a>
						<%if(updateable&&coach_edit_able){ %>
						<a href="<%=person_edit_url %>?idtoken=<%=s.getToken(ptm.getId()+person_edit_url)%>&id=<%=ptm.getId() %>&action=delete" title="删除(Delete)"><i class="icon-trash"></i></a>
						<%} %>
						</p>
				<%} 
				if(updateable&&coach_edit_able){%>
						<p><%if(teamMember.size()-reserveTeamMember<3){ %>
							<a style="color:#CC2828;" href="<%=person_edit_url %>?actiontoken=<%=s.getToken(t.getId()+person_edit_url) %>&team=<%=t.getId() %>&action=addteammember">添加队员(Add Team Member)</a>
							<%} %><%if(reserveTeamMember<1&&accept_reserve_team_member){ %></p><p>
							<a style="color:#CC2828;" href="<%=person_edit_url %>?actiontoken=<%=s.getToken(t.getId()+person_edit_url+"reserve") %>&team=<%=t.getId() %>&action=addteammember&reserve=reserve">添加替补队员(Add Reserve Team Member)</a>
							<%} %>
						</p>
		<% }%>
					</div>
					<div style="clear:both"></div>
				</div>
		<%
		}
		%>
		<%if(updateable&&coach_edit_able){ %>
				<div class="team_list_control">
					<p><a style="color:#CC2828;" href="<%=team_edit_url %>?token=<%=s.getToken("-1"+p.getId()+team_edit_url) %>&id=-1&coach=<%=p.getId()%>">添加队伍(Add a team)</a></p>
				</div>
		<%} %>
			</div>
		<div style="clear:both"></div>
		</div><%
	}
	%>
	</div>
	<div style="clear:both"></div>
	<div class="coach_list_control">
	<%if(updateable&&coach_edit_able){ %>
		<p>
		<a href="<%=person_edit_url %>?actiontoken=<%=s.getToken("addcoach"+person_edit_url) %>&action=addcoach" class="btn btn-primary"><i class=" icon-plus-sign icon-white"></i> 添加教练(Add a Coach)</a>
		</p>
	<%} %>
	</div>
	</div>
	<%}else{ %>
	<script type="text/javascript">confirmable=false;confirmmsg+="您还没有留下您的学校信息！\n";</script>
	<p>您需要先录入您的学校信息才能录入您的教练、参赛队、队员等信息。(You Need To Submit Your School Information Before Enter Coach/Team/Team Member Information.)</p>
	<% }%>
	<h3>随行人员<br/><small>Retinues Information</small></h3>
<%--	<div class="alert alert-error"><strong>请注意：</strong>由于服装需要提前一个月预订，故现在录入的随行人员并无服装。</div>--%>
	<div id="retinues" class="well">
			<% for(Person r:Person.loadSpecifyPersons(s.user.getId(),Person.PERSON_RETINUE)){ %>
				<p><a href="person_edit.jsp?idtoken=<%=s.getToken(r.getId()+person_edit_url)%>&id=<%=r.getId() %>"><%=Site.htmlEncode(r.getChineseName()) %>(<%=Site.htmlEncode(r.getEnglishName()) %>)</a>
				<%if(updateable){ %><a href="person_edit.jsp?idtoken=<%=s.getToken(r.getId()+person_edit_url)%>&id=<%=r.getId() %>&action=delete"  title="删除(Delete)"><i class="icon-trash"></i></a><%} %>
				</p>
			<%} %>
	<%if(updateable){ %>
	
	<a href="<%=person_edit_url %>?actiontoken=<%=s.getToken("addretinue"+person_edit_url) %>&action=addretinue" class="btn btn-primary"><i class=" icon-plus-sign icon-white"></i> 添加随行人员(Add a Retinue)</a>
	<%} %>
	</div>
	<%
	leader.loadMyPlans(); %>
	<h3>行程计划<br/><small>Your Plan Information</small></h3>
	<%if(leader.getPlan().getId()==-1){ %>
	<script type="text/javascript">/*confirmable=false;confirmmsg+="您还没有留下您的行程计划！\n";*/</script><%} %>
	<div id="plans" class="well">
		<form action="<%=plan_update_url %>" method="post" onsubmit="return checkplan();" class="form-horizontal">
		<%if(Site.isUpdateable()){ %>
			<input type="hidden" name="id" value="<%=leader.getPlan().getId() %>" />
			<input type="hidden" name="idtoken" value="<%=s.getToken(leader.getPlan().getId()+plan_update_url) %>" />	
		<%} %>		
			<input type="hidden" id="f_arrival" name="arrival" value="<%=leader.getPlan().getArrival() %>" />			
			<input type="hidden" id="f_leave" name="leave" value="<%=leader.getPlan().getLeave() %>" />			
			<div class="control-group"><label class="control-label" for="arrival">到达时间：<br/>(Arrival Time)</label><div class="controls">
			<input  type="datetime-local" id="arrival" value="<%=leader.getPlan().getArrival()/1000%>" />
			<span>时间格式(Time Format)：<span style="color:red">YYYY-MM-DD</span>T<span style="color:red">HH:MM</span> ,<span title="shuxiao's birthday">1991-11-26</span>T05:20</span></div></div>
			<div class="control-group"><label class="control-label" for="arrivaltraffic">达到交通方式：<br/>(Arrival Traffic)</label><div class="controls">
			<input type="text"  maxlength="255" size="20"  name="arrivaltraffic" id="arrivaltraffic" value="<%=Site.htmlEncode(leader.getPlan().getArrivalTraffic())%>"/>
			</div></div>
			<div class="control-group"><label class="control-label" for="leave">返程时间：<br/>(Leave Time)</label><div class="controls">
			<input  type="datetime-local" name="leave" id="leave"  name="leave" value="<%=leader.getPlan().getLeave()/1000%>"  />
			<span>时间格式(Time Format)：<span style="color:red">YYYY-MM-DD</span>T<span style="color:red">HH:MM</span> ,2009-10-18T18:14</span>
			</div></div>
			<div class="control-group"><label class="control-label" for="leavetraffic">返程交通方式：<br/>(Leave Traffic)</label><div class="controls">
			<input type="text"  maxlength="255" size="20"  name="leavetraffic" id="leavetraffic" value="<%=Site.htmlEncode(leader.getPlan().getLeaveTraffic())%>"/></div></div>
			<div class="control-group" style="display:none" title="if you need, you may fill in this blank."><label for="bookticket">订票信息：<br/>(Book Ticket)</label><div class="controls">
			<input type="text"  maxlength="255" size="20"  name="bookticket" id="bookticket" value="<%=Site.htmlEncode(leader.getPlan().getBookTicket())%>"/></div></div>
			<script>
				$('#arrival').will_pickdate({
					  timePicker: true,
					  format: 'Y-m-dTH:i',
					  inputOutputFormat: 'U',
					  onSelect:function(d){
						  $$("f_arrival").value=d.getTime();
					  }
				});
				$('#leave').will_pickdate({
					  timePicker: true,
					  format: 'Y-m-dTH:i',
					  inputOutputFormat: 'U',
					  onSelect:function(d){
						  $$("f_leave").value=d.getTime();
					  }
				});
				function checkplan(){
				//	deal_time("leave");
				//	deal_time("arrival");
				//	if(deal_time('arrival')&&dealtime('leave'))
					return true;
				//	alert("日期有错误！");
				//	return false;
				}
			</script>
			<div class="form-actions"><input type="submit"  class="btn btn-primary"/></div>
		</form>
	<%%>
	</div>
	<h3>志愿者信息<br/><small>Volunteer Information</small></h3>
	<div id="volunteer" class="well">
	<div class="offset1">
		<ul class="thumbnails">
			<%
			boolean vf0=true;
			for(Volunteer fv:Volunteer.getSchoolsVolunteers(s.user.getSchool())){
				vf0=false;
			%>
			<li class="span3">
				<div class="thumbnail" rel="tooltip" data-original-title="志愿者信息">
				<h5><%=Site.htmlEncode(fv.person.getChineseName()) %> <small><%=Site.htmlEncode(fv.person.getEnglishName()) %></small></h5>
				<p>
					性别：<%=fv.person.getGender()?"男":"女"%><br/>
					电话：<%=Site.htmlEncode(fv.person.getMobile()) %><br/>
					Email：<%=Site.htmlEncode(fv.person.getEmail()) %><br/>
					专业：<%=Site.htmlEncode(fv.person.getMajor())%><br/>
					描述：<%=Site.htmlEncode(fv.person.getDescription())%>
				</p>
				</div>
			</li>
			<%}
			if(vf0){
				%>暂未分配，请稍等……<%
			}
			%>
		</ul>
		</div>
	</div>
		<div id="sharingroominformation">
		<h3>综合提问<br/><small>Questions and answers</small></h3>
		<div class="well">
	<%
	msglst=Message.getSpecificMessage(Message.MESSAGE_SPEC_OF_TYPE, Message.MESSAGE_TYPE_NORMAL);
	for(Message message:msglst){
		List<Message> replymsglst=Message.getSpecificMessage(Message.MESSAGE_SPEC_GET_REPLY, message.getId());
	%>
<div class="message thumbnail">
	<a href="message_content.jsp?id=<%=message.getId()%>"><b><%=Site.htmlEncode(message.getTitle()) %></b></a> (回复：<%=replymsglst.size() %><%if(replymsglst.size()>0){ %>，最后时间：<%=df.format(new Date(replymsglst.get(replymsglst.size()-1).getTime())) %>，by：<%=Site.htmlEncode(replymsglst.get(replymsglst.size()-1).getUsername())%><%} %>)
	<span class="pull-right"><span class="user"><%=Site.htmlEncode(message.getUsername()) %></span> <i><%=df.format((new Date(message.getTime()))) %></i></span>
</div>
<%}%>
<%if(msglst.size()==0){ %>
<div class="message thumbnail"><b>暂无主题，您可以抢沙发了！(There is no existing post, be first to post!)</b></div>
<%}%>
<div><a class="btn btn-primary pull-right" href="message_borad.jsp">更多(More)</a></div>
</div>
</div>
	<script type="text/javascript">
	<%if(!updateable){ %>
		$("input").attr("disabled","disabled");
		$("select").attr("disabled","disabled");
		$("textarea").attr("disabled","disabled");
		<%if(Site.isUpdateable()){%>
		$("#plans input").removeAttr("disabled");
		<%} %>
	<%} %>
	</script>
	<%
} catch (SQLException e) {
	e.printStackTrace();
	%><%=Site.htmlEncode(e.getMessage())%><%
}
%>
<%@ include file="include/bottom_html_java.jspf" %>