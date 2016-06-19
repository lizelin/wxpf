package net.linvx.java.wx.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import net.linvx.java.libs.http.HttpUrl;
import net.linvx.java.libs.tools.MyLog;
import net.linvx.java.libs.utils.MyStringUtils;
import net.linvx.java.wx.api.ApiException;
import net.linvx.java.wx.api.WeixinApiImpl;
import net.linvx.java.wx.common.AppHelper;
import net.linvx.java.wx.common.Constants;
import net.sf.json.JSONObject;

public class OAuthFilter implements Filter{
	private static Logger log = MyLog.getLogger(OAuthFilter.class);
	@Override
	public void destroy() {
		log.info("OAuthFilter is destroyed!");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		//记录用户的访问操作
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        HttpUrl httpUrl = new HttpUrl(request);
        	
        log.info("before doFilter: url is :" + httpUrl.getUrlString());
        if (this.continueFilter(request, response, httpUrl) == false)
        	return;
        chain.doFilter(req, res);  //让目标资源执行，放行	
		log.info("after doFilter method.");
	}

	private boolean continueFilter(HttpServletRequest request, HttpServletResponse response, HttpUrl httpUrl) {
		String ua = request.getHeader("User-Agent");
		if (AppHelper.isWeixin(ua) == false)
			return true;
		
		String url = httpUrl.getSchemaHostPortPath().toLowerCase();
		if (this.isUrlNeedOauth(url) == false)
			return true;
		
		String openId = (String) request.getSession().getAttribute(Constants.SESSION_KEY_OPEN_ID);
		if (MyStringUtils.isNotEmpty(openId))
			return true;
		
		String accountCode = MyStringUtils.nvlString(request.getParameter("accountCode"), net.linvx.java.wx.common.Constants.DEFAULT_ACCONT_CODE);
        String state = MyStringUtils.nvlString(request.getParameter("state"), "");
        String code = MyStringUtils.nvlString(request.getParameter("code"), "");
        WeixinApiImpl api = WeixinApiImpl.createApiToWxByAccountCode(accountCode);
        
        // 已经进行过OAuth了，去取openid
        if (state.equals("go") && MyStringUtils.isNotEmpty(code)) {
        	JSONObject json = null;
			try {
				json = api.getOpenIdByOAuth2Code(code);
			} catch (ApiException e1) {
				e1.printStackTrace();
			}
        	if (json != null && MyStringUtils.isNotEmpty(json.optString("openid"))) {
        		openId = json.optString("openid");
        		request.getSession().setAttribute(Constants.SESSION_KEY_OPEN_ID, openId);
        		log.info("openId is: " + request.getSession().getAttribute(Constants.SESSION_KEY_OPEN_ID));
        		return true;
        	} else {
        		goErrorPage(response);
        		return false;
        	}
        }
        
        // 调用oauth
        if (MyStringUtils.isEmpty(state) || MyStringUtils.isEmpty(code)) {
        	String redirectUrl = null;
        	try {
				redirectUrl = api.getOAuth2Url(request, httpUrl.getUrlString());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        	if (MyStringUtils.isEmpty(redirectUrl)) {
        		goErrorPage(response);
        		return false;
        	}
        	try {
				response.sendRedirect(redirectUrl);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
		return true;
	}
	
	private void goErrorPage(HttpServletResponse response) {
		// TODO: need to redirect showmsg.jsp ... 
		try {
			response.sendError(403);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private boolean isUrlNeedOauth(String url){
		url = url.toLowerCase();
		if ( url.endsWith(".gif") || url.endsWith(".jpg") || url.endsWith(".png")
            || url.endsWith(".bmp") || url.endsWith(".css") || url.endsWith(".js")
            || url.endsWith(".jsx")){
            return false;
        } 
		
		// 只截获 .jsp .do .html
		if (!url.endsWith(".jsp") && !url.endsWith(".do") && !url.endsWith(".html"))
			return false;
		
		return true;
    }
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		log.info("OAuthFilter is initialed!");
	}

}
