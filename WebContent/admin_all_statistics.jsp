<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="cn.edu.nenu.acm.contestservice.*,cn.edu.nenu.acm.contestservice.modeling.objects.*,cn.edu.nenu.acm.contestservice.modeling.roles.*,
	java.sql.SQLException,
	java.util.HashMap,java.util.List,java.util.Date"%> 
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
	long otl=new Date().getTime();
%>
<%@ include file="include/head_html.jspf"%>
<title>统计汇总</title>
<%@ include file="include/body_html_java.jspf"%>
<div id="main">
	<h1 style="text-align:center">统计汇总</h1>
<%
String boardSchoolSQL="";
try{
	//奇葩，数据库设计的奇葩。。
	HashMap<Integer,Pair<School,Leader>> schools=new HashMap<Integer,Pair<School,Leader>>();
	for(School sch:School.getAllSchools()){
		schools.put(new Integer(sch.getId()), new Pair<School,Leader>(sch,null));
	}
	List<Message> msglst=null;
	Message msg=null;
	String[] a=new String[1];
	String[] enterStatus={"No","Yes","Not Sure"};
	for(User user:User.loadSpecifyUser(User.USER_LEADER, -1)){
		int cL=0,cxl=0,cxxl=0,cxxxl=0,cother=0,cS=0,cm=0,ctotal=0,cwp_yes=0,cac_yes=0,cwp_no=0,cac_no=0,cwp_not_sure=0,cac_not_sure=0;
		int tL=0,txl=0,txxl=0,txxxl=0,tother=0,tS=0,tm=0,ttotal=0,twp_yes=0,tac_yes=0,twp_no=0,tac_no=0,twp_not_sure=0,tac_not_sure=0;
		int rL=0,rxl=0,rxxl=0,rxxxl=0,rother=0,rS=0,rm=0,rtotal=0,rwp_yes=0,rac_yes=0,rwp_no=0,rac_no=0,rwp_not_sure=0,rac_not_sure=0;
		Leader l=new Leader(user);
		l.loadRetinue();
		Pair<School,Leader> p=schools.get(user.getSchool());
		if(p==null){
			System.out.println("NULL: "+user.getUsername());
			continue;
		}
		School school=p.a;
		l.setSchool(p.a);
		p.b=l;
		String teamNamesList="#";
				//学校%>
		<h3><%=Site.htmlEncode(school.getChineseName()) %><br><small><%=Site.htmlEncode(school.getEnglishName()) %><a class="pull-right" href="login?action=otl&username=<%=Site.htmlEncode(user.getUsername())%>&password=<%=Site.hash(otl+user.getUsername(), "otl")%>&otl=<%=otl%>">[角色扮演]</a></small></h3>
		<div class="basic">
		<p><b>地址：</b><%=Site.htmlEncodeNoBr(school.getPostAddress()) %><br/><b>邮编：</b><%=Site.htmlEncode(school.getPostCode()) %><br/><b>发票抬头：</b><%
		msglst=Message.getSpecificMessage(Message.MESSAGE_SPEC_OF_TICKET_NICK, l.getUser().getId());
				if(msglst.size()>0){
			msg=msglst.get(0);
				}else{
			msg=new Message();}%><%=Site.htmlEncode(msg.getContent()) %><br/><b>信息已经确认：</b><%=l.getUser().isConfirm()?"YES":"NO" %></p>
		</div>
		<%
		%>
		<div class="thumbnail bookroom">
		<h4>订房信息</h4>
		<table class="table table-striped table-bordered table-condensed">
		<thead><tr><th>酒店名</th><th>房间类型</th><th>已订数量</th></tr></thead><tbody><%
		int totalCount=0,totalPrice=0,totalVolume=0;
		for(BookedRoomInfo b:l.getBookedRoomInfo()){ %>
		<tr><td><%=Site.htmlEncode(b.getHotelName()) %></td><td><%=Site.htmlEncode(b.getTypeName()) %></td><td><%=b.getCount() %></td></tr>
		<%
		totalCount+=b.getCount();
		totalPrice+=b.getCount()*b.getPrice();
		totalVolume+=b.getCount()*b.getVolume();
		} %></tbody></table>
		<p><b>订房备注：</b><%
		msglst=Message.getSpecificMessage(Message.MESSAGE_SPEC_OF_BOOKROOM, l.getUser().getId());
				if(msglst.size()>0){
			msg=msglst.get(0);
				}else{
			msg=new Message();}%><%=Site.htmlEncode(msg.getContent()) %></p></div>
<%l.loadMyPlans(); %>
<div class="thumbnail leaderplan">
			<h4 rel="tooltip" data-original-title="这个学校的领队行程计划">领队行程计划</h4>	
			<div>
			<%if(l.getPlan().getId()>0){ %>
			<table class="table table-striped table-bordered table-condensed">
				<tr><th width="150px">抵达时间</th><td><%=new Date(l.getPlan().getArrival()).toLocaleString() %></td></tr>
				<tr><th>抵达交通方式 </th><td><%=l.getPlan().getArrivalTraffic() %></td></tr>
				<tr><th>离开时间</th><td><%=new Date(l.getPlan().getLeave()).toLocaleString() %></td></tr>
				<tr><th>离开交通方式</th><td><%=l.getPlan().getLeaveTraffic() %></td></tr>
			</table>
			<%}else{ %>
			<p>暂无信息。</p>
			<%} %></div>
	</div>
		<%l.loadChaches();
		for(Coach c:l.getCoaches()){
			ctotal++;
			if(c.getClothes().equals("L")){
				cL++;
			}else if(c.getClothes().equals("XL")){
				cxl++;
			}else if(c.getClothes().equals("XXL")){
				cxxl++;
			}else if(c.getClothes().equals("XXXL")){
				cxxxl++;
			}else if(c.getClothes().equals("S")){
				cS++;
			}else if(c.getClothes().equals("M")){
				cm++;
			}else{
				cother++;
			}
			if(c.enterWelcomeParty()==Person.EVENT_ENTER){
				cwp_yes++;
			}else if(c.enterWelcomeParty()==Person.EVENT_NOT_ENTER){
				cwp_no++;
			}else if(c.enterWelcomeParty()==Person.EVENT_NOT_SURE){
				cwp_not_sure++;
			}
			if(c.enterAwardsCeremony()==Person.EVENT_ENTER){
				cac_yes++;
			}else if(c.enterAwardsCeremony()==Person.EVENT_NOT_ENTER){
				cac_no++;
			}else if(c.enterAwardsCeremony()==Person.EVENT_NOT_SURE){
				cac_not_sure++;
			} %>
		<div class="thumbnail coach">
			<h4 rel="tooltip" data-original-title="这个学校下的一个教练"><%=Site.htmlEncode(c.getChineseName()) %> (教练) <small><%=Site.htmlEncode(c.getEnglishName()) %><a href="person_edit.jsp?id=<%=c.getId() %>" title="编辑">#</a><br/>
			电话：<%=Site.htmlEncode(c.getMobile()) %><br/>
			Email：<%=Site.htmlEncode(c.getEmail()) %><br/>性别：<%=c.getGender()?"男":"女"%><br/>专业：<%=Site.htmlEncode(c.getMajor())%><br/>服装：<%=Site.htmlEncode(c.getClothes())%><br/>描述：<%=Site.htmlEncode(c.getDescription())%></small>
			</h4>
			<%c.loadMyTeams();
			for(Team t : c.getTeams()){
				teamNamesList+=t.getEnglishName()+"#";
				%>
			<div class="offset1 team">
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
			}%>]<a href="team_edit.jsp?id=<%=t.getId()%>">#</a></small></h4>
			<ul class="thumbnails teammember">
				<%List<Person> teamMember=Person.getTeamMembers(t.getId());
				for(Person ptm:teamMember){ 
			ttotal++;
			if(ptm.getClothes().equals("L")){
				tL++;
			}else if(ptm.getClothes().equals("XL")){
				txl++;
			}else if(ptm.getClothes().equals("XXL")){
				txxl++;
			}else if(ptm.getClothes().equals("XXXL")){
				txxxl++;
			}else if(ptm.getClothes().equals("S")){
				tS++;
			}else if(ptm.getClothes().equals("M")){
				tm++;
			}else{
				tother++;
			}
			if(ptm.enterWelcomeParty()==Person.EVENT_ENTER){
				twp_yes++;
			}else if(ptm.enterWelcomeParty()==Person.EVENT_NOT_ENTER){
				twp_no++;
			}else if(ptm.enterWelcomeParty()==Person.EVENT_NOT_SURE){
				twp_not_sure++;
			}
			if(ptm.enterAwardsCeremony()==Person.EVENT_ENTER){
				tac_yes++;
			}else if(ptm.enterAwardsCeremony()==Person.EVENT_NOT_ENTER){
				tac_no++;
			}else if(ptm.enterAwardsCeremony()==Person.EVENT_NOT_SURE){
				tac_not_sure++;
			} %>
				<li class="span3">
					<div class="thumbnail" rel="tooltip" data-original-title="参赛队员">
					<h5><%=Site.htmlEncode(ptm.getChineseName()) %> <small><%=Site.htmlEncode(ptm.getEnglishName()) %><a href="person_edit.jsp?id=<%=ptm.getId() %>" title="编辑">#</a></small></h5>
					<p>
					性别：<%=ptm.getGender()?"男":"女"%><br/>
					电话：<%=Site.htmlEncode(ptm.getMobile()) %><br/>
					Email：<%=Site.htmlEncode(ptm.getEmail()) %><br/>
					专业：<%=Site.htmlEncode(ptm.getMajor())%><br/>
					服装：<%=Site.htmlEncode(ptm.getClothes())%><br/>
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
		<div class="thumbnail routine">
		<h4 rel="tooltip" data-original-title="这个学校的随行人员">随行人员<br/></h4>
		<div class="offset1">
		<ul class="thumbnails">
			<%
			boolean r0=true;
			for(Person r:Person.loadSpecifyPersons(l.getUser().getId(),Person.PERSON_RETINUE)){
				r0=false;
				rtotal++;
				if(r.getClothes().equals("L")){
					rL++;
				}else if(r.getClothes().equals("XL")){
					rxl++;
				}else if(r.getClothes().equals("XXL")){
					rxxl++;
				}else if(r.getClothes().equals("XXXL")){
					rxxxl++;
				}else if(r.getClothes().equals("S")){
					rS++;
				}else if(r.getClothes().equals("M")){
					rm++;
				}else{
					rother++;
				}
				if(r.enterWelcomeParty()==Person.EVENT_ENTER){
					rwp_yes++;
				}else if(r.enterWelcomeParty()==Person.EVENT_NOT_ENTER){
					rwp_no++;
				}else if(r.enterWelcomeParty()==Person.EVENT_NOT_SURE){
					rwp_not_sure++;
				}
				if(r.enterAwardsCeremony()==Person.EVENT_ENTER){
					rac_yes++;
				}else if(r.enterAwardsCeremony()==Person.EVENT_NOT_ENTER){
					rac_no++;
				}else if(r.enterAwardsCeremony()==Person.EVENT_NOT_SURE){
					rac_not_sure++;
				} 
			%>
			<li class="span3">
				<div class="thumbnail" rel="tooltip" data-original-title="随行人员">
				<h5><%=Site.htmlEncode(r.getChineseName()) %> <small><%=Site.htmlEncode(r.getEnglishName()) %><a href="person_edit.jsp?id=<%=r.getId() %>" title="编辑">#</a></small></h5>
				<p>
					性别：<%=r.getGender()?"男":"女"%><br/>
					电话：<%=Site.htmlEncode(r.getMobile()) %><br/>
					Email：<%=Site.htmlEncode(r.getEmail()) %><br/>
					专业：<%=Site.htmlEncode(r.getMajor())%><br/>
					服装：<%=Site.htmlEncode(r.getClothes())%><br/>
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
	<div class="volunteer thumbnail">
	<h4>志愿者信息</h4>
	<div class="offset1">
		<ul class="thumbnails">
			<%
			boolean vf0=true;
			for(Volunteer fv:Volunteer.getSchoolsVolunteers(l.getUser().getSchool())){
				vf0=false;
			%>
			<li class="span3">
				<div class="thumbnail" rel="tooltip" data-original-title="志愿者信息">
				<h5><%=Site.htmlEncode(fv.person.getChineseName()) %> <small><%=Site.htmlEncode(fv.person.getEnglishName()) %><a href="person_edit.jsp?id=<%=fv.person.getId() %>" title="编辑">#</a></small></h5>
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
	<div class="alert alert-info summary"><b>小计：</b><br/>
	教练：<%=ctotal %>人。服装：<%=cL>0?"L:"+cL+"，":"" %><%=cxl>0?"XL:"+cxl+"，":"" %><%=cxxl>0?"XXL:"+cxxl+"，":"" %><%=cxxxl>0?"XXL:"+cxxxl+"，":"" %><%=cS>0?"S:"+cS+"，":"" %><%=cm>0?"M:"+cm+"，":"" %><%=cother>0?"Other:"+cother+"，":"" %><br/>
	队员：<%=ttotal %>人。服装：<%=tL>0?"L:"+tL+"，":"" %><%=txl>0?"XL:"+txl+"，":"" %><%=txxl>0?"XXL:"+txxl+"，":"" %><%=txxxl>0?"XXL:"+txxxl+"，":"" %><%=tS>0?"S:"+tS+"，":"" %><%=tm>0?"M:"+tm+"，":"" %><%=tother>0?"Other:"+tother+"，":"" %><br/>
	随行：<%=rtotal %>人。服装：<%=rL>0?"L:"+rL+"，":"" %><%=rxl>0?"XL:"+rxl+"，":"" %><%=rxxl>0?"XXL:"+rxxl+"，":"" %><%=rxxxl>0?"XXL:"+rxxxl+"，":"" %><%=rS>0?"S:"+rS+"，":"" %><%=rm>0?"M:"+rm+"，":"" %><%=rother>0?"Other:"+rother+"，":"" %><br/>
	欢迎宴会(教练)：<%=cwp_yes%>人，不缺定：<%=cwp_not_sure %>人。<br/>
	欢迎宴会(随行)：<%=rwp_yes%>人，不缺定：<%=rwp_not_sure %>人。<br/>
	欢迎宴会(队员)：<%=twp_yes%>人，不缺定：<%=twp_not_sure %>人。<br/>
	颁奖礼(教练)：<%=cac_yes%>人，不缺定：<%=cac_not_sure %>人。<br/>
	颁奖礼(随行)：<%=rac_yes%>人，不缺定：<%=rac_not_sure %>人。<br/>
	颁奖礼(队员)：<%=tac_yes%>人，不缺定：<%=tac_not_sure %>人。<br/>
	</div><%
	boardSchoolSQL+="INSERT INTO School(sDisplayName,sChineseName,sEnglishName) VALUES ('"+teamNamesList.replace("'", "\\'")+"' , '"+school.getChineseName().replace("'", "\\'")+"' , '"+school.getEnglishName().replace("'", "\\'")+"');\n";
	} 
}catch(SQLException e){
	e.printStackTrace();
	s.message="Error Occour While Perform Your Request. Technical Detial: " +e.getMessage();
	s.msgType=MSG.error;
}
%>
<pre><%=boardSchoolSQL %></pre>
<div class="thumbnail" id="school_nav" style="background-color:#fff;position:fixed;bottom: 0px; right: 0px;">
	<div>
	<!--<label class="control-label" for="schools_seleter">跳转到学校：</label>-->
	<div class="controls">
		<select rel="tooltip" data-original-title="跳转到学校" id="schools_seleter" onchange="gotoH(this.value)"></select>
	</div>
	</div>
	<div id="selector">
	<label for="basic">基本：</label><input type="checkbox" checked="checked" id="basic"/>
	<label for="bookroom">订房：</label><input type="checkbox" checked="checked" id="bookroom"/>
	<label for="leaderplan">计划：</label><input type="checkbox" checked="checked" id="leaderplan"/>
	<label for="coach">教练：</label><input type="checkbox" checked="checked" id="coach"/><br/>
	<label for="team">队伍：</label><input type="checkbox" checked="checked" id="team"/>
	<label for="routine">随行：</label><input type="checkbox" checked="checked" id="routine"/>
	<label for="volunteer">志愿者：</label><input type="checkbox" checked="checked" id="volunteer"/>
	<label for="summary">小计：</label><input type="checkbox" checked="checked" id="summary"/>
	</div>
</div>
<script type="text/javascript">
$("#selector>input").css("display","inline");
$("#selector>label").css("display","inline");
$(".team").css("border","none");
$(".thumbnail").css("box-shadow","none");
$("#selector>input").click(function(){
	$this=$(this);
	if($this.attr("checked")=="checked"){
		$("."+$this.attr("id")).show();
	}else{
		$("."+$this.attr("id")).hide();
	}
});
geth3=false;
var $schools_seleter=$("#schools_seleter");
var hi=0;
$(".container h3").each(function(index){
  	var $h=$(this);
  	var substr=$h.find("small").text();
  	$schools_seleter.append('<option value="'+hi+'">'+$h.text().replace(substr,"")+'</option>');
  	$h.before('<a id="h_'+hi+'"></a>');
  	hi++;
  });
</script>
</div>
<%@ include file="include/bottom_html_java.jspf"%>

