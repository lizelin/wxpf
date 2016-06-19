package net.linvx.java.wx.dao;

import java.sql.Connection;

import net.linvx.java.libs.db.MyDbUtils;
import net.linvx.java.libs.utils.MyStringUtils;
import net.linvx.java.wx.po.PoWxUserInfo;
import net.linvx.java.wx.po.PoWxUserStatus;

public class DaoWxUserStatus {
	public static void main(String[] args) {
	}
	
	public static PoWxUserStatus findByOpenId(Integer accountGuid, String openId,final  Connection db) {
		PoWxUserStatus userInfo = null;
		String sql = "select us.* from wx_user_status us "
				+ "		where us.numAccountGuid = ? and us.vc2OpenId = ?";
		try {
			userInfo = MyDbUtils.getRow(db, sql, new Object[]{accountGuid, openId}, PoWxUserStatus.class);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return userInfo;
	}
	
	public static PoWxUserStatus findByUserGuid(Integer userGuid,final  Connection db) {
		PoWxUserStatus userInfo = null;
		String sql = "select us.* from wx_user_status us  "
				+ "		where us.numUserGuid = ?";
		try {
			userInfo = MyDbUtils.getRow(db, sql, new Object[]{userGuid}, PoWxUserStatus.class);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return userInfo;
	}
	
	public static boolean save(PoWxUserStatus userInfo, final Connection db) {
		if (userInfo == null || MyStringUtils.isEmpty(userInfo.getVc2OpenId()) 
				|| userInfo.getNumAccountGuid() == null || userInfo.getNumAccountGuid().intValue() < 0 ) {
			return false;
		}
		
		PoWxUserStatus userInfo1 = DaoWxUserStatus.findByOpenId(userInfo.getNumAccountGuid(), userInfo.getVc2OpenId(), db);
		int cnt = 0;
		if (userInfo1 == null) {
			cnt = DaoWxUserStatus.insert(userInfo, db);
		} else {
			cnt = DaoWxUserStatus.update(userInfo, db);
		}
		return cnt > 0;
	}
	
	private static int insert(PoWxUserStatus userInfo, final Connection db) {
		String sql = " INSERT INTO `wx_user_status` " +
				" (`numAccountGuid`, `vc2OpenId`, `vc2SubscribeFlag`, `vc2FirstQRSceneId`, " +
				" `datFirstSubscribeTime`, `datLastSubscribeTime`, `datLastUnSubscribeTime`, `datCreation`, " +
				" `datLastUpdate`, `vc2EnabledFlag`) " +
				" VALUES " +
				" (?, ?, ?, ?, " +
				" ?, ?, ?, ?, " +
				" ?, ?)";

		int cnt = -1;
		try {
			cnt = MyDbUtils.insert(db, sql, new Object[]{
					userInfo.getNumAccountGuid(), userInfo.getVc2OpenId(), userInfo.getVc2SubscribeFlag(), userInfo.getVc2FirstQRSceneId(),
					userInfo.getDatFirstSubscribeTime(), userInfo.getDatLastSubscribeTime(), userInfo.getDatLastUnSubscribeTime(), userInfo.getDatCreation(),
					userInfo.getDatLastUpdate(), userInfo.getVc2EnabledFlag()
					});
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return cnt;
	}
	
	private static int update(PoWxUserStatus userInfo, final Connection db) {
		String sql = " UPDATE `wx_user_status` " +
				" SET " +
				" `numAccountGuid` = ?, " +
				" `vc2OpenId` = ?, " +
				" `vc2SubscribeFlag` = ?, " +
				" `vc2FirstQRSceneId` = ?, " +
				" `datFirstSubscribeTime` = ?, " +
				" `datLastSubscribeTime` = ?, " +
				" `datLastUnSubscribeTime` = ?, " +
				" `datCreation` = ?, " +
				" `datLastUpdate` = ?, " +
				" `vc2EnabledFlag` = ? " +
				" WHERE `numUserGuid` = ? ";

		int cnt = -1;
		try {
			cnt = MyDbUtils.update(db, sql, new Object[]{
					userInfo.getNumAccountGuid(), userInfo.getVc2OpenId(), userInfo.getVc2SubscribeFlag(), userInfo.getVc2FirstQRSceneId(),
					userInfo.getDatFirstSubscribeTime(), userInfo.getDatLastSubscribeTime(), userInfo.getDatLastUnSubscribeTime(), userInfo.getDatCreation(),
					userInfo.getDatLastUpdate(), userInfo.getVc2EnabledFlag(), userInfo.getNumUserGuid()
					});
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return cnt;
	}
	

}
