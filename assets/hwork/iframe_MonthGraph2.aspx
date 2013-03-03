HTTP/1.1 200 OK
Date: Wed, 27 Feb 2013 05:25:51 GMT
Server: WWW Server/1.1
X-Powered-By: ASP.NET
X-AspNet-Version: 2.0.50727
Cache-Control: private
Content-Type: text/html; charset=ks_c_5601-1987
Content-Length: 16600

__VIEWSTATE=%2FwEPDwUKMTU5NDg5OTA1MGRkQtZJsU4tV32pG1Vj7vs7uizyBb4%3D&selYear=2013&selMonth=2&sKind=ELEC&x=13&y=6

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" >
<HTML>
	<HEAD>
		<title>iframe_MonthGraph</title>
		<meta name="GENERATOR" Content="Microsoft Visual Studio .NET 7.1">
		<meta name="CODE_LANGUAGE" Content="C#">
		<meta name="vs_defaultClientScript" content="JavaScript">
		<meta name="vs_targetSchema" content="http://schemas.microsoft.com/intellisense/ie5">
		<LINK REL="stylesheet" TYPE="text/css" HREF="../style.css">
		<script language="javascript" src="/Js/Global/JsGlobal.js"></script>
		<script language="javascript">
		
			function onLayer(dayValue) {
		
				var objDiv = eval(document.all.divView);
				
				objDiv.innerHTML = "<div style='border-color: blue;'>" + dayValue + "(㎾)</div>";
				
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
		    
			// 월 선택
			function makeGraduMonthOption(month)
			{
				var ret = "<option value=''>월선택</option>";
				for(i=1;i<13;i++)
				{
					if(month==i)
					{
						ret +="<option value='"+ i +"' selected>"+ i +"</opion>";
					}else
					{
						ret +="<option value='"+ i +"'>"+ i +"</opion>";
					}
				}
				document.write(ret);        
			}    
    
		
		</script>
	</HEAD>
	<body>
		<form name="MonthForm" method="post" action="iframe_MonthGraph.aspx" id="MonthForm">
<div>
<input type="hidden" name="__VIEWSTATE" id="__VIEWSTATE" value="/wEPDwUKMTU5NDg5OTA1MGRkQtZJsU4tV32pG1Vj7vs7uizyBb4=" />
</div>

			<TABLE WIDTH="580" height="25" BORDER="0" ALIGN="center" CELLPADDING="0" CELLSPACING="0">
				<TR align="left" valign="middle">
					<TD width="60" HEIGHT="25">
						<a href="iframe_MonthGraph.aspx?sKind=ELEC&sYear=2013&sMonth=02"><img src="../images/homenetwork/top_menu01_ov.gif" width="50" height="19" border="0"></a></TD>
					<TD width="60" HEIGHT="25">
						<a href="iframe_MonthGraph.aspx?sKind=WATER&sYear=2013&sMonth=02"><img src="../images/homenetwork/top_menu02.gif" width="50" height="19" border="0"></a></TD>
					
					<TD width="60" HEIGHT="25">
						<a href="iframe_MonthGraph.aspx?sKind=HOTWATER&sYear=2013&sMonth=02"><img src="../images/homenetwork/top_menu03.gif" width="50" height="19" border="0"></a></TD>
					
					<TD width="60" HEIGHT="25">
										
					<a href="iframe_MonthGraph.aspx?sKind=GAS&sYear=2013&sMonth=02"><img src="../images/homenetwork/top_menu04.gif" width="50" height="19" border="0"></a></TD>						
					
					<TD width="340" HEIGHT="25">
						<a href="iframe_MonthGraph.aspx?sKind=HEAT&sYear=2013&sMonth=02"><img src="../images/homenetwork/top_menu05.gif" width="50" height="19" border="0"></a></TD>
					
				</TR>
			</TABLE>
			<BR>
			<table width="600" height="430" border="0" cellpadding="0" cellspacing="0" ALIGN="center">
				<tr>
					<td width="40" align="right">
						
						<div style="HEIGHT:40px">15.3
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">13.8
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">12.2
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">10.7
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">9.2
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">7.7
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">6.1
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">4.6
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">3.1
							(㎾)
						</div>
						
						<div style="HEIGHT:40px">1.5
							(㎾)
						</div>
						
						<br>
					</td> <!--  background="../images/homenetwork/graph_bigbg2.gif"-->
					<td valign="bottom" style="BACKGROUND-IMAGE: url(../images/homenetwork/graph2_bigbg.gif); BACKGROUND-REPEAT: no-repeat">
						<table width="560" height="430" border="0" cellpadding="0" cellspacing="0">
							<tr valign="bottom">
								<td width="8" height="9"></td>
								<td height="9" colspan="31"></td>
								<td width="23" height="9"></td>
							</tr>
							<tr valign="bottom">
								<td width="8" height="400"></td>
								<!-- 일 그래프 시작 heigh=400 이 만땅 ------------------>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="292.810448879536" border="0" onmouseover="onLayer('9.20')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="253.594763093901" border="0" onmouseover="onLayer('7.70')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="318.954239403292" border="0" onmouseover="onLayer('10.20')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="287.581695761317" border="0" onmouseover="onLayer('10.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="316.339875310513" border="0" onmouseover="onLayer('9.10')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="313.725486285073" border="0" onmouseover="onLayer('10.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="277.124189524879" border="0" onmouseover="onLayer('7.60')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="326.797381546951" border="0" onmouseover="onLayer('8.50')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="400" border="0" onmouseover="onLayer('12.30')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="300.653591023195" border="0" onmouseover="onLayer('8.50')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="311.111097259632" border="0" onmouseover="onLayer('9.90')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="303.267980048635" border="0" onmouseover="onLayer('9.60')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="284.967306735876" border="0" onmouseover="onLayer('8.90')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="256.209152119342" border="0" onmouseover="onLayer('7.80')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="287.581695761317" border="0" onmouseover="onLayer('13.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="284.967306735876" border="0" onmouseover="onLayer('12.90')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="316.339875310513" border="0" onmouseover="onLayer('9.10')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="316.339875310513" border="0" onmouseover="onLayer('10.10')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="303.267980048635" border="0" onmouseover="onLayer('15.60')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="292.810448879536" border="0" onmouseover="onLayer('10.20')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="298.039201997754" border="0" onmouseover="onLayer('18.40')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="282.352942643098" border="0" onmouseover="onLayer('11.80')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="303.267980048635" border="0" onmouseover="onLayer('11.60')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="300.653591023195" border="0" onmouseover="onLayer('14.50')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="303.267980048635" border="0" onmouseover="onLayer('10.60')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="250.980399001123" border="0" onmouseover="onLayer('7.60')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="115.032680797793" border="0" onmouseover="onLayer('3.40')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="0" border="0" onmouseover="onLayer('10.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>

								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="0" border="0" onmouseover="onLayer('10.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>

								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="0" border="0" onmouseover="onLayer('10.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>

								<td width="14" height="40" align="center" valign="bottom">
									<table width="12" height="0" border="0" onmouseover="onLayer('10.00')" cellpadding="0" cellspacing="0">
										<tr>
											<td background="../images/homenetwork/graph_bar_ELEC.gif"></td>
										</tr>
									</table>
								</td>
								
								<!-- 일 그래프 끝 ------------------>
								<td width="23" height="400"></td>
							</tr>
							<tr valign="bottom">
								<td width="8" height="21"></td>
								<td height="21" colspan="31"></td>
								<td width="23" height="21"></td>
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
					<TD width="200" HEIGHT="25" align="left">
						<select name="selYear" style="WIDTH:60px">
							<script>makeGraduYearOption('2013');</script>
						</select>
						년
						<select name="selMonth" style="WIDTH:45px">
							<script>makeGraduMonthOption('02');</script>
						</select>
						월
					</TD>
					<td width="270" HEIGHT="25" align="left">
						<input type="image" src="../images/control/search_btn.gif" width="70" height="19" style="border:none 0 none" align="absMiddle">
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
			<input type="hidden" value="ELEC" name="sKind"> 
			<!-- 레이어 뷰 ------------------------>
			<div id="divView" style="PADDING-RIGHT: 5px; DISPLAY: none; PADDING-LEFT: 1px; FONT-SIZE: 12px; LEFT: 300px; PADDING-BOTTOM: 1px; WIDTH: 100px; COLOR: #9f9f9f; BORDER-TOP-STYLE: double; PADDING-TOP: 1px; FONT-FAMILY: 돋움; BORDER-RIGHT-STYLE: double; BORDER-LEFT-STYLE: double; POSITION: absolute; TOP: 600px; BACKGROUND-COLOR: #fffafa; TEXT-ALIGN: right; BORDER-BOTTOM-STYLE: double"></div>
		</form>
	</body>
</HTML>
