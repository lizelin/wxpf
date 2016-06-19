<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	// 该jsp为oauth的过渡页面，因为在跳转到微信的oauth2 url时，必须时指定的域才行；故通过该过渡页面接收code，并且传回给实际的url
	String oriurl = request.getParameter("redirect_uri_proxy");
	if (net.linvx.java.libs.utils.MyStringUtils.isEmpty(oriurl)) {
		response.sendError(404);
		return;
	}
	String code = request.getParameter("code");
	if (net.linvx.java.libs.utils.MyStringUtils.isEmpty(code)) {
		response.sendError(404);
		return;
	}
	
	oriurl = net.linvx.java.libs.http.HttpUrl.addParam(oriurl, "code", code);
	oriurl = net.linvx.java.libs.http.HttpUrl.addParam(oriurl, "authdone", "y");
	
	String state = request.getParameter("state");
	if (net.linvx.java.libs.utils.MyStringUtils.isNotEmpty(state))
		oriurl = net.linvx.java.libs.http.HttpUrl.addParam(oriurl, "state", state);
	
	response.sendRedirect(oriurl);
%>