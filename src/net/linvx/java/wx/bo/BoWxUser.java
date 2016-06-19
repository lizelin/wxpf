package net.linvx.java.wx.bo;

import java.sql.Connection;
import java.sql.Timestamp;

import net.linvx.java.libs.db.MyDbUtils;
import net.linvx.java.libs.utils.MyDateUtils;
import net.linvx.java.libs.utils.MyStringUtils;
import net.linvx.java.wx.api.ApiException;
import net.linvx.java.wx.api.WeixinApiImpl;
import net.linvx.java.wx.dao.DaoWxUserInfo;
import net.linvx.java.wx.dao.DaoWxUserStatus;
import net.linvx.java.wx.dao.DaoWxUserSubLog;
import net.linvx.java.wx.po.PoWxUserInfo;
import net.linvx.java.wx.po.PoWxUserStatus;
import net.sf.json.JSONObject;

public class BoWxUser {
	private PoWxUserStatus userStatus = null;
	private PoWxUserInfo userInfo = null;
	
	private Integer mAccountGuid = null;
	private String mOpenId = null;
	private Connection mDb = null;
	private Timestamp now = MyDateUtils.now();
	
	private boolean userStatusExists = true;
	private boolean userInfoExists = true;
	
	public BoWxUser(int accountGuid, String openId, Connection db) {
		this.mAccountGuid = Integer.valueOf(accountGuid);
		this.mOpenId = openId;
		this.mDb = db;
		now = MyDateUtils.now();
		userStatus = DaoWxUserStatus.findByOpenId(Integer.valueOf(accountGuid), openId, db);
		if (userStatus == null) {
			userStatusExists = false;
			userStatus = new PoWxUserStatus();
			userStatus.setDatCreation(now)
				.setDatLastUpdate(now)
				.setNumAccountGuid(Integer.valueOf(accountGuid))
				.setVc2EnabledFlag("Y")
				.setVc2OpenId(openId);
		}
		userInfo = DaoWxUserInfo.findByOpenId(Integer.valueOf(accountGuid), openId, db);
		if (userInfo == null ) {
			userInfoExists = false;
			userInfo = new PoWxUserInfo();
			userInfo.setDatCreation(now)
				.setDatLastUpdate(now);
		}
	}
	
	
	public void sub(String sceneId) {
		String _scene = MyStringUtils.nvlString(sceneId, "");
		_scene = _scene.replace("qrscene_", "");
		
		WeixinApiImpl api = WeixinApiImpl.createApiToWxByAccountGuid(mAccountGuid);
		JSONObject jsonUserInfo = null;
		try {
			jsonUserInfo = api.getUserInfo(mOpenId);
		} catch (ApiException e) {
			e.printStackTrace();
		}
		
		userStatus.setVc2SubscribeFlag(jsonUserInfo.optString("subscribe", "0"));
		
		// 未关注状态：
		if (userStatus.getVc2SubscribeFlag().equals("0")) {
			unSub();
			return;
		}
		
		// 已关注状态
		Timestamp lastSub = MyDateUtils.toTimestatmp(jsonUserInfo.optLong("subscribe_time")*1000l);
		if (userStatusExists == false) {
			userStatus.setDatFirstSubscribeTime(lastSub)
			.setDatLastSubscribeTime(lastSub)
			.setDatLastUnSubscribeTime(null)
			.setDatLastUpdate(now)
			.setNumAccountGuid(mAccountGuid)
			.setVc2FirstQRSceneId(_scene);
			
		} else {
			userStatus.setDatLastSubscribeTime(lastSub)
			.setDatLastUpdate(now);
		}
		DaoWxUserStatus.save(userStatus, mDb);
		userStatus = DaoWxUserStatus.findByOpenId(mAccountGuid, mOpenId, mDb);
		
		if (userInfoExists == false) {
			userInfo.setNumUserGuid(userStatus.getNumUserGuid());
		}
		
		userInfo.setVc2NickName(jsonUserInfo.optString("nickname"));
		userInfo.setVc2SexCode(jsonUserInfo.optString("sex"));
		userInfo.setVc2Language(jsonUserInfo.optString("language"));
		userInfo.setVc2CityName(jsonUserInfo.optString("city"));
		userInfo.setVc2ProvinceName(jsonUserInfo.optString("province"));
		userInfo.setVc2CountryName(jsonUserInfo.optString("country"));
		userInfo.setVc2HeadImgUrl(jsonUserInfo.optString("headimgurl"));
		userInfo.setDatLastSubscribeTime(lastSub);
		userInfo.setVc2UnionId(jsonUserInfo.optString("unionid"));
		userInfo.setVc2Remark(jsonUserInfo.optString("remark"));
		userInfo.setVc2GroupId(jsonUserInfo.optString("groupid"));
		userInfo.setDatLastSyncFromWx(now);

		DaoWxUserInfo.save(userInfo, mDb);
		
		DaoWxUserSubLog.logUserSubOrUnSub(userInfo.getNumUserGuid(), "SUB", _scene, mDb);
	}
	
	public void unSub() {
		if (userStatusExists == false) {
			userStatus.setDatFirstSubscribeTime(now)
				.setDatLastSubscribeTime(now)
				.setDatLastUnSubscribeTime(now)
				.setDatLastUpdate(now)
				.setNumAccountGuid(mAccountGuid)
				.setVc2SubscribeFlag("0")
				.setVc2FirstQRSceneId("");
		} else {
			userStatus.setDatLastUnSubscribeTime(now)
				.setDatLastUpdate(now)
				.setVc2SubscribeFlag("0");
		}
		DaoWxUserStatus.save(userStatus, mDb);
		
		DaoWxUserSubLog.logUserSubOrUnSub(userInfo.getNumUserGuid(), "UNSUB", "", mDb);
	}
	
	
	public static void main(String[] args) {
	}
	
}
