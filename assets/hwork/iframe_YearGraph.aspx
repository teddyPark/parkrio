HTTP/1.1 200 OK
Date: Thu, 07 Mar 2013 07:42:25 GMT
Server: WWW Server/1.1
X-Powered-By: ASP.NET
X-AspNet-Version: 2.0.50727
Cache-Control: private
Content-Type: text/html; charset=ks_c_5601-1987
Content-Length: 10272


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" >
<HTML>
	<HEAD>
		<title>iframe_YearGraph</title>
		<meta name="GENERATOR" Content="Microsoft Visual Studio .NET 7.1">
		<meta name="CODE_LANGUAGE" Content="C#">
		<meta name="vs_defaultClientScript" content="JavaScript">
		<meta name="vs_targetSchema" content="http://schemas.microsoft.com/intellisense/ie5">
		<LINK REL="stylesheet" TYPE="text/css" HREF="../style.css">
		<script language="javascript" src="/Js/Global/JsGlobal.js"></script>
		<script language="javascript">
		
			function onLayer(monthValue) {
		
				var objDiv = eval(document.all.divView);
				
				objDiv.innerHTML = "<div style='border-color: blue;'>" + monthValue + "(㎾)</div>";
				
				if(objDiv.style.display == "block") {													
					document.all.divView.style.display = "none";
				}
				objDiv.style.left = event.x + document.body.scrollLeft + 5 + 'px';
				objDiv.style.top = event.y + document.body.scrollTop + -10 + 'px';
				objDiv.style.display = "block";
			}
			
			// 년도 선택
			function makeGraduYearOption(year)
			{
				cdate = new Date();
				years = cdate.getFullYear();
			
			    var vYear = "";
				var ret = "<option value=''>년도선택</option>";
				for(i=0;i< 4;i++)
				{
					vYear = years - 3 + i;
					if(year==vYear)
					{
						ret +="<option value='"+ vYear +"' selected>"+ vYear +"</opion>";
					}else
					{
						ret +="<option value='"+ vYear +"'>"+ vYear +"</opion>";
					}
				}
				document.write(ret);        
			}
		
		</script>
	</HEAD>
	<body>
		<form name="YearForm" method="post" action="iframe_YearGraph.aspx" id="YearForm">
<div>
<input type="hidden" name="__VIEWSTATE" id="__VIEWSTATE" value="/wEPDwUKMjAyNzU3OTkxM2RkMXAfR6Im5D2lQRtlQ01g4/icE7k=" />
</div>

			<TABLE WIDTH="580" height="25" BORDER="0" ALIGN="center" CELLPADDING="0" CELLSPACING="0">
				<TR align="left" valign="middle">
					<TD width="60" HEIGHT="25">
						<a href="iframe_YearGraph.aspx?sKind=ELEC&sYear=2013&sMonth=03"><img src="../images/homenetwork/top_menu01_ov.gif" width="50" height="19" border="0"></a></TD>
					<TD width="60" HEIGHT="25">
						<a href="iframe_YearGraph.aspx?sKind=WATER&sYear=2013&sMonth=03"><img src="../images/homenetwork/top_menu02.gif" width="50" height="19" border="0"></a></TD>
					
					<TD width="60" HEIGHT="25">
						<a href="iframe_YearGraph.aspx?sKind=HOTWATER&sYear=2013&sMonth=03"><img src="../images/homenetwork/top_menu03.gif" width="50" height="19" border="0"></a></TD>
					
					<TD width="60" HEIGHT="25">
					
                    <a href="iframe_YearGraph.aspx?sKind=GAS&sYear=2013&sMonth=03"><img src="../images/homenetwork/top_menu04.gif" width="50" height="19" border="0"></a></TD>                    
                    					
					<TD width="340" HEIGHT="25">
						<a href="iframe_YearGraph.aspx?sKind=HEAT&sYear=2013&sMonth=03"><img src="../images/homenetwork/top_menu05.gif" width="50" height="19" border="0"></a></TD>
					
				</TR>
			</TABLE>
			<BR>
			<table width="600" height="430" border="0" cellpadding="0" cellspacing="0" ALIGN="center">
				<tr>
					<td width="40" align="right">
						
						<div style="HEIGHT:40px">349
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">314
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">279
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">244
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">209
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">174
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">140
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">105
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">70
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">35
							(㎾)
						</div>
						
						<br>
					</td>
					<td valign="bottom" style="BACKGROUND-IMAGE: url(../images/homenetwork/graph2_bigbg01.gif); BACKGROUND-REPEAT: no-repeat">
						<table width="560" height="430" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td width="10" height="9"></td>
								<td height="9" colspan="12"></td>
								<td width="30" height="9"></td>
							</tr>
							<tr>
								<td width="10" height="400"></td>
								<!-- 월 그래프 시작 heigh=400 이 만땅 ------------------>
								
								<td width="28" height="400" align="center" valign="bottom">
									<table width="23" height="400" border="0" onmouseover="onLayer('349.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC01.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="28" height="400" align="center" valign="bottom">
									<table width="23" height="366.76217765043" border="0" onmouseover="onLayer('320.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC01.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="28" height="400" align="center" valign="bottom">
									<table width="23" height="85.1575966209259" border="0" onmouseover="onLayer('74.30')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC01.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="28" height="400" align="center" valign="bottom">
									<table width="23" height="0" border="0" onmouseover="onLayer('0.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC01.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="28" height="400" align="center" valign="bottom">
									<table width="23" height="0" border="0" onmouseover="onLayer('0.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC01.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="28" height="400" align="center" valign="bottom">
									<table width="23" height="0" border="0" onmouseover="onLayer('0.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC01.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="28" height="400" align="center" valign="bottom">
									<table width="23" height="0" border="0" onmouseover="onLayer('0.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC01.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="28" height="400" align="center" valign="bottom">
									<table width="23" height="0" border="0" onmouseover="onLayer('0.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC01.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="28" height="400" align="center" valign="bottom">
									<table width="23" height="0" border="0" onmouseover="onLayer('0.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC01.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="28" height="400" align="center" valign="bottom">
									<table width="23" height="0" border="0" onmouseover="onLayer('0.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC01.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="28" height="400" align="center" valign="bottom">
									<table width="23" height="0" border="0" onmouseover="onLayer('0.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC01.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="28" height="400" align="center" valign="bottom">
									<table width="23" height="0" border="0" onmouseover="onLayer('0.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC01.gif"></td>
										</tr>
									</table>
								</td>
								
								<!-- 월 그래프 끝 ------------------>
								<td width="30" height="400"></td>
							</tr>
							<tr>
								<td width="10" height="21"></td>
								<td height="21" colspan="12"></td>
								<td width="30" height="21"></td>
							</tr>
						</table>
					</td>
					<td width="10"></td>
				</tr>
			</table>
			
			<div style="FONT-SIZE: 12px; MARGIN: -250px 0px 250px 50px; COLOR: red; FONT-FAMILY: Gulim, Arial,Helvetica, geneva, sans-serif" align="center">
				<span id="lblFalse"></span>
			</div>
			
			<BR>
			<TABLE WIDTH="580" height="25" BORDER="0" ALIGN="center" CELLPADDING="0" CELLSPACING="0">
				<TR align="center" valign="middle">
					<TD width="100" HEIGHT="25"></TD>
					<TD width="90" HEIGHT="25">날짜선택:</TD>
					<TD width="110" HEIGHT="25" align="left">
						<select name="selYear" style="WIDTH:60px">
							<script>makeGraduYearOption('2013');</script>
						</select>
						년
					</TD>
					<td width="270" HEIGHT="25" align="left">
						<input type="image" src="../images/control/search_btn.gif" width="70" height="19" style="border:none 0 none;" align="absMiddle">
					</td>
				</TR>
			</TABLE>
			<BR>
			<BR>
			<BR>
			<BR>
			<BR>
			<BR>
			<BR>
			<BR>
			<BR>
			<BR>
			<BR>
			<!-- 레이어 뷰 ------------------------>
			<div id="divView" style="PADDING-RIGHT: 5px; DISPLAY: none; PADDING-LEFT: 1px; FONT-SIZE: 12px; LEFT: 300px; PADDING-BOTTOM: 1px; WIDTH: 100px; COLOR: #9f9f9f; BORDER-TOP-STYLE: double; PADDING-TOP: 1px; FONT-FAMILY: 돋움; BORDER-RIGHT-STYLE: double; BORDER-LEFT-STYLE: double; POSITION: absolute; TOP: 600px; BACKGROUND-COLOR: #fffafa; TEXT-ALIGN: right; BORDER-BOTTOM-STYLE: double"></div>
		</form>
	</body>
</HTML>
