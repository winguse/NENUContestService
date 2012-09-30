<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="
	cn.edu.nenu.acm.contestservice.*,
	cn.edu.nenu.acm.contestservice.modeling.roles.*,
	cn.edu.nenu.acm.contestservice.modeling.objects.*,
	java.sql.SQLException,
	java.util.List,java.util.Date"%>
<%@ include file="include/head_java.jspf" %>
<%
if (s==null||s.user == null){
	response.sendRedirect(".");
	return;
}
if(s.user.getPermission()!=User.USER_VOLUNTEER&&s.user.getPermission()!=User.USER_VOLUNTEER_PEDDING){
	response.sendRedirect(".");
	return;
}

boolean updateable=Site.isUpdateable();

final String person_edit_url="person_edit.jsp";
final String team_edit_url="team_edit.jsp";
final String school_update_url="school_update";
final String plan_update_url="plan_update";
final String sx="刘美君";
%>
<%@ include file="include/head_html.jspf" %>
<title>志愿者首页</title>
<%@ include file="include/body_html_java.jspf" %>
	<div id="main"  class="main_mid">
<%
try {
	s.user.load(s.user.getId());
	Volunteer v=new Volunteer(s.user);
	boolean pedding=false;
	if(v.person.getTitle()==Person.PERSON_VOLUNTEER_PEDDING){
		pedding=true;
	}
	if(!pedding){
		v.loadServeObject();
	%>
	<div class="alert alert-info">您现在是正式志愿者。欢迎您参加本次比赛的志愿服务！现在请你注意修改你的个人信息，特别是个人描述！</div>
	<%
	}else{
	%>
	<div class="alert alert-info"><strong>请注意：</strong>您现在的状态是预注册志愿者，请完善您的信息以便审核。</div>
	<%} %>
	<h3>我的个人信息<br/><small>My Personal Information</small></h3>
	<div class="well"<%if(s.user.getUsername().equals(sx)){%> rel="tooltip" data-original-title="舒啸，欢迎你！祝你开开心心每一天～"<%}%>>
	<table class="table table-striped table-bordered table-condensed">
	<tr><th width="150px">中文名<br/><small>Chinese Name</small></th><td><%=Site.htmlEncode(v.person.getChineseName())%></td></tr>
	<tr><th>英文名<br/><small>English Name</small></th><td><%=Site.htmlEncode(v.person.getEnglishName())%></td></tr>
	<tr><th>专业<br/><small>Major</small></th><td><%=Site.htmlEncode(v.person.getMajor())%></td></tr>
	<tr><th>性别<br/><small>Gender</small></th><td><%=v.person.getGender()?"男(Male)":"女(Female)"%></td></tr>
	<tr><th>手机<br/><small>Mobile</small></th><td><%=Site.htmlEncode(v.person.getMobile())%></td></tr>
	<tr><th>电子邮箱<br/><small>Email</small></th><td><%=Site.htmlEncode(v.person.getEmail())%></td></tr>
	<tr><th>服装大小<br/><small>Clothes Size</small></th><td><%=Site.htmlEncode(v.person.getClothes())%></td></tr>
	<tr><th>描述补充<br/><small>More  Description</small></th><td><%=Site.htmlEncode(v.person.getDescription())%></td></tr>
	</table>
	<p>&nbsp;
	<a class="pull-right btn btn-primary" href="person_edit.jsp?idtoken=<%=s.getToken(v.person.getId()+person_edit_url)%>&id=<%=v.person.getId() %>">编辑个人信息
	</a></p>
	</div>
	<%if(s.user.getUsername().equals(sx)){%> 
		<div id="lmj_message" class="alert alert-error" rel="tooltip" data-original-title="呃，侦测到你在别人的电脑上登录过，希望不是账户泄漏吧，嗯，你用自己的电脑就能够看到这个彩蛋了，需要HTML5支持的！" style="cursor:pointer" onclick="show_lmj()">
		<h4 class="alert-heading">彩蛋<a id="lmj_loc"></a></h4>小心，好奇害死猫哦！</div>
		<div class="thumbnail" id="lmj" style="display:none" title="我不能回避对你的喜欢，无论如何，请不要拒绝我对你好一点。4月16那天，其实我已经够开心的了，我们这样相处就很好～">
			<div rel="tooltip" data-placement="left" data-original-title="背景音乐是 Best Monent ，觉得这个名字挺对的！" style="width:680px;height:640px;margin:0 auto;border:0">
			<iframe id="lmj_if" src="" width="680px" height="630px" style="border:0" ></iframe>
			</div>
		</div>
		<script type="text/javascript">
		function show_lmj(){
			$('#lmj').show();
			$$("lmj_if").src="winguse/shuxiao.html";
		//	document.body.scrollTop=$("#lmj_message").offset().top;
			window.location.hash="lmj_loc";
		}
		</script>
	<%} %>
	<%if(!pedding){ %>
	<h3<%if(s.user.getUsername().equals(sx)){%> rel="tooltip" data-original-title="舒啸，辛苦你了哦～"<%}%>>
	我的服务对象信息<br/><small>My Served Persons' Information</small></h3>
		<%
		boolean vt0=true;
	for(School sc:v.serveSchool){
		for (User u : User.loadSpecifyUser(User.USER_LEADER, sc.getId())) {
			Leader l=new Leader(u);
			vt0=false;
			%>
		<div class="alert alert-info" rel="tooltip" data-original-title="你要服务的一个学校">
		<h4 class="alert-heading"><%=sc.getChineseName() %><br><small><%=sc.getEnglishName() %></small></h4>
		<%-- 领队账户：<%=Site.htmlEncode(l.getUser().getUsername()) %>--%>
		</div>
	<div class="thumbnail"  rel="tooltip" data-original-title="和你一起服务这个学校的志愿者<%if(s.user.getUsername().equals(sx)){%>，即使这里没有别人，我总是背后的一个，有麻烦，记得告诉我哦～<%}%>">
	<h4>我的协同志愿者<br/><small>My Fellow Volunteer</small></h4>
		<div class="offset1">
		<ul class="thumbnails">
			<%
			boolean vf0=true;
			for(Volunteer fv:Volunteer.getSchoolsVolunteers(sc.getId())){
				if(fv.user.getId()==v.user.getId())continue;
				vf0=false;
			%>
			<li class="span3">
				<div class="thumbnail" rel="tooltip" data-original-title="和你协同工作的志愿者">
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
				%>无<%
			}
			%>
		</ul>
		</div>
	</div><%l.loadMyPlans(); %>
	<div class="thumbnail">
			<h4 rel="tooltip" data-original-title="这个学校的领队行程计划">领队行程计划<br/><small>Leader's Plan</small></h4>	
			<div>
			<%if(l.getPlan().getId()>0){ %>
			<table class="table table-striped table-bordered table-condensed">
				<tr><th width="150px">抵达时间<br/><small>Arrival Time</small></th><td><%=new Date(l.getPlan().getArrival()).toLocaleString() %></td></tr>
				<tr><th>抵达交通方式<br/><small>Arrival Traffic</small> </th><td><%=l.getPlan().getArrivalTraffic() %></td></tr>
				<tr><th>离开时间<br/><small>Leaving Time</small> </th><td><%=new Date(l.getPlan().getLeave()).toLocaleString() %></td></tr>
				<tr><th>离开交通方式<br/><small>Leaving Traffic</small> </th><td><%=l.getPlan().getLeaveTraffic() %></td></tr>
			</table>
			<%}else{ %>
			暂无信息。
			<%} %>
			</div>
	</div>
		<%l.loadChaches();
		for(Coach c:l.getCoaches()){ %>
		<div class="thumbnail">
			<h4 rel="tooltip" data-original-title="这个学校下的一个教练"><%=Site.htmlEncode(c.getChineseName()) %> <small><%=Site.htmlEncode(c.getEnglishName()) %><br/>
			电话：<%=Site.htmlEncode(c.getMobile()) %><br/>
			<%--Email：<%=Site.htmlEncode(c.getEmail()) %><br/>--%>性别：<%=c.getGender()?"男":"女"%><br/>专业：<%=Site.htmlEncode(c.getMajor())%></small>
			</h4>
			<%c.loadMyTeams();
			for(Team t : c.getTeams()){ %>
			<div class="offset1">
			<h4  rel="tooltip" data-original-title="这个教练的参赛队"><%=Site.htmlEncode(t.getChineseName()) %><br/><small>
			<%=Site.htmlEncode(t.getEnglishName()) %> [<%
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
			case Team.TEAM_OFFICAL_VERIFIED:
				%>正式队（已审）(Offical Verified)<%
				break;
			case Team.TEAM_OFFICAL_UNDER_VERIFIED_AGAIN:
				%>正式队（待再审）(Offical Need Verified Again)<%
				break;
			}%>]</small></h4>
			<ul class="thumbnails">
				<%List<Person> teamMember=Person.getTeamMembers(t.getId());
				for(Person ptm:teamMember){ %>
				<li class="span3">
					<div class="thumbnail" rel="tooltip" data-original-title="参赛队员">
					<h5><%=Site.htmlEncode(ptm.getChineseName()) %> <small><%=Site.htmlEncode(ptm.getEnglishName()) %></small></h5>
					<p>
					性别：<%=ptm.getGender()?"男":"女"%><br/>
					电话：<%=Site.htmlEncode(ptm.getMobile()) %><br/>
					Email：<%=Site.htmlEncode(ptm.getEmail()) %><br/>
					专业：<%=Site.htmlEncode(ptm.getMajor())%><br/>
					描述：<%=Site.htmlEncode(ptm.getDescription())%>
					</p>
					</div>
				</li>
				<%} %>
			</ul>
			</div>
			<%} %>
		</div>
		<%} %>
		<div class="thumbnail">
		<h4 rel="tooltip" data-original-title="这个学校的随行人员">随行人员<br/><small>Retinues</small></h4>
		<div class="offset1">
		<ul class="thumbnails">
			<%
			boolean r0=true;
			for(Person r:Person.loadSpecifyPersons(l.getUser().getId(),Person.PERSON_RETINUE)){
				r0=false;
			%>
			<li class="span3">
				<div class="thumbnail" rel="tooltip" data-original-title="随行人员">
				<h5><%=Site.htmlEncode(r.getChineseName()) %> <small><%=Site.htmlEncode(r.getEnglishName()) %></small></h5>
				<p>
					性别：<%=r.getGender()?"男":"女"%><br/>
					电话：<%=Site.htmlEncode(r.getMobile()) %><br/>
					Email：<%=Site.htmlEncode(r.getEmail()) %><br/>
					专业：<%=Site.htmlEncode(r.getMajor())%><br/>
					描述：<%=Site.htmlEncode(r.getDescription())%>
				</p>
				</div>
			</li>
			<%}
			if(r0){
				%>无<%
			}
			%>
		</ul>
		</div>
		</div>
	<%}} 
	if(vt0){
	%><div class="well"<%if(s.user.getUsername().equals(sx)){%> rel="tooltip" data-original-title="舒啸，你的任务还没分配哦～我也在努力完善这个系统中……"<%}%>><p>暂无信息，请稍等……</p></div>
	<%} %>
	<%if(!updateable&&false){%>
	<script>
	$("input").attr("disabled","disabled");
	$("select").attr("disabled","disabled");
	$("textarea").attr("disabled","disabled");
	</script>
	<%} %>
	<%}//not pedding %>
	<%
} catch (SQLException e) {
	e.printStackTrace();
	%><%=Site.htmlEncode(e.getMessage())%><%
}
%>
	</div>
<%@ include file="include/bottom_html_java.jspf" %>