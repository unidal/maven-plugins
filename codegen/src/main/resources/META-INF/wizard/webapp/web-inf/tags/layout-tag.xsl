<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" indent="yes" media-type="text/html" encoding="utf-8" xmlns:xalan="http://xml.apache.org/xslt"/>
<xsl:variable name="space" select="' '" />
<xsl:variable name="empty" select="''" />
<xsl:variable name="empty-line" select="'&#x0A;'" />

<xsl:template match="/">
   <xsl:apply-templates select="/wizard" />
</xsl:template>

<xsl:template match="wizard">
<xsl:text disable-output-escaping="yes"><![CDATA[<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
]]></xsl:text>
<xsl:value-of select="'&lt;'" disable-output-escaping="yes"/>jsp:useBean id="navBar" class="<xsl:value-of select="webapp/@package"/>.view.NavigationBar" scope="page" /<xsl:value-of select="'&gt;'" disable-output-escaping="yes"/>
<xsl:text disable-output-escaping="yes"><![CDATA[

<!DOCTYPE html>
<html lang="en">

<head>
	<title>Phoenix - ${model.page.description}</title>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="Phoenix Console">
	<link rel="shortcut icon" href="${model.webapp}/img/favicon.png">
	<link href="${model.webapp}/css/bootstrap.css" type="text/css" rel="stylesheet">
	<link href="${model.webapp}/css/bootstrap-responsive.css" type="text/css" rel="stylesheet">
	<script src="${model.webapp}/js/jquery-1.8.3.min.js" type="text/javascript"></script>
	<script type="text/javascript">var contextpath = "${model.webapp}";</script>
</head>

<body data-spy="scroll" data-target=".subnav" data-offset="50">
	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container-fluid">
				<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</a> 
				
				<div class="nav-collapse collapse">
					<ul class="nav">
						<c:forEach var="page" items="${navBar.visiblePages}">
							<c:if test="${page.standalone}">
								<li ${model.page.name == page.name ? 'class="selected"' : ''}><a
									href="${model.webapp}/${page.moduleName}/${page.path}?domain=${model.domain}&date=${model.date}&reportType=${payload.reportType}&op=${payload.action.name}">${page.title}</a></li>
							</c:if>
							<c:if
								test="${not page.standalone and model.page.name == page.name}">
								<li class="selected">${page.title}</li>
							</c:if>
						</c:forEach>
					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
		</div>
	</div>

	<div class="container-fluid" style="min-height:524px;">
		<div class="row-fluid">
			<div class="span12">
				<jsp:doBody />
			</div>
		</div>
	
		<br />
		<div class="container">
			<footer><center>@2013 Dianping Phoenix Team, Email: <a href="mailto:www@dianping.com">www@dianping.com</a></center></footer>
		</div>
	</div>
	<!--/.fluid-container-->

	<script src="${model.webapp}/js/bootstrap.js" type="text/javascript"></script>
</body>
</html>
]]></xsl:text>
</xsl:template>

</xsl:stylesheet>
