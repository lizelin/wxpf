package net.linvx.java.wx.bo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import net.linvx.java.libs.db.MyDbUtils;
import net.linvx.java.libs.tools.MyLog;
import net.linvx.java.wx.api.ApiException;
import net.linvx.java.wx.api.WeixinApiImpl;
import net.linvx.java.wx.dao.DaoWxMenu;
import net.linvx.java.wx.dao.DaoWxOfficialAccount;
import net.linvx.java.wx.dao.DbHelper;
import net.linvx.java.wx.model.Menu;
import net.linvx.java.wx.po.PoWxOfficialAccount;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BoWxMenu {
	private static Logger log = MyLog.getLogger(BoWxMenu.class);
	/**
	 * 将菜单json数组转为 menu类数组
	 * 
	 * @param json
	 * @return
	 */
	public static Menu[] parseMenuJson(JSONObject json) {
		JSONArray arr = json.optJSONObject("menu").optJSONArray("button");
		Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
		classMap.put("sub_button", Menu.class);
		return (Menu[]) JSONArray.toArray(arr, Menu.class, classMap);
	}

	/**
	 * 转成微信要求的格式
	 * 
	 * @param menus
	 * @return
	 */
	public static JSONObject toMenuJson(Menu[] menus) {
		JSONObject jsonData = new JSONObject();
		jsonData.put("button", JSONArray.fromObject(menus));
		return jsonData;
	}

	public static void createMenuFromDb(String code) throws ApiException{
		PoWxOfficialAccount account = DaoWxOfficialAccount.getWxOfficialAccountByAccountCode(code);
		Menu[] menus = DaoWxMenu.loadMenus(account.getNumAccountGuid());
		JSONObject json = BoWxMenu.toMenuJson(menus);
		WeixinApiImpl api = WeixinApiImpl.createApiToWxByAccountCode(code);
		api.createMenus(json.toString());
	}
}
