package net.linvx.java.wx.dao;

import java.sql.Connection;

import net.linvx.java.libs.db.MyDbUtils;
import net.linvx.java.wx.po.PoWxUserInfo;

public class DaoWxUserInfo {
	public static void main(String[] args) {
	}
	
	public static PoWxUserInfo findByOpenId(Integer accountGuid, String openId, final Connection db) {
		PoWxUserInfo userInfo = null;
		String sql = "select ui.* from wx_user_info ui, wx_user_status us "
				+ "		where us.numUserGuid = ui.numUserGuid and us.numAccountGuid = ? and us.vc2OpenId = ?";
		try {
			userInfo = MyDbUtils.getRow(db, sql, new Object[]{accountGuid, openId}, PoWxUserInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return userInfo;
	}
	
	public static PoWxUserInfo findByUserGuid(Integer userGuid, final Connection db) {
		PoWxUserInfo userInfo = null;
		String sql = "select ui.* from wx_user_info ui  "
				+ "		where ui.numUserGuid = ?";
		try {
			userInfo = MyDbUtils.getRow(db, sql, new Object[]{userGuid}, PoWxUserInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return userInfo;
	}
	
	public static boolean save(PoWxUserInfo userInfo, final Connection db) {
		if (userInfo == null || userInfo.getNumUserGuid() == null || userInfo.getNumUserGuid().intValue()<0 ) {
			return false;
		}
		
		PoWxUserInfo userInfo1 = DaoWxUserInfo.findByUserGuid(userInfo.getNumUserGuid(), db);
		int cnt = 0;
		if (userInfo1 == null) {
			cnt = DaoWxUserInfo.insert(userInfo, db);
		} else {
			cnt = DaoWxUserInfo.update(userInfo, db);
		}
		return cnt > 0;
	}
	
	private static int insert(PoWxUserInfo userInfo, final Connection db) {
		String sql = "insert into wx_user_info("
				+ "`numUserGuid`,`vc2NickName`,`vc2SexCode`,`vc2ProvinceName`,`vc2CityName`,"
				+ "`vc2CountryName`,`vc2Language`,`vc2HeadImgUrl`,`vc2UnionId`,`vc2Remark`,"
				+ "`vc2GroupId`,`datCreation`,`datLastUpdate`,`datLastSyncFromWx`,`datLastSubscribeTime`)"
				+ "values("
				+ "?,?,?,?,?,"
				+ "?,?,?,?,?,"
				+ "?,?,?,?,?)";
		int cnt = -1;
		try {
			cnt = MyDbUtils.insert(db, sql, new Object[]{
					userInfo.getNumUserGuid(), userInfo.getVc2NickName(), userInfo.getVc2SexCode(), userInfo.getVc2ProvinceName(), userInfo.getVc2CityName(),
					userInfo.getVc2CountryName(), userInfo.getVc2Language(), userInfo.getVc2HeadImgUrl(), userInfo.getVc2UnionId(), userInfo.getVc2Remark(),
					userInfo.getVc2GroupId(), userInfo.getDatCreation(), userInfo.getDatLastUpdate(), userInfo.getDatLastSyncFromWx(), userInfo.getDatLastSubscribeTime()
					});
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return cnt;
	}
	
	private static int update(PoWxUserInfo userInfo, final Connection db) {
		String sql = " UPDATE `wx_user_info` " +
				" SET " +
				" `numUserGuid` = ?, " +
				" `vc2NickName` = ?, " +
				" `vc2SexCode` = ?, " +
				" `vc2ProvinceName` = ?, " +
				" `vc2CityName` = ?, " +
				" `vc2CountryName` = ?, " +
				" `vc2Language` = ?, " +
				" `vc2HeadImgUrl` = ?, " +
				" `vc2UnionId` = ?, " +
				" `vc2Remark` = ?, " +
				" `vc2GroupId` = ?, " +
				" `datCreation` = ?, " +
				" `datLastUpdate` = ?, " +
				" `datLastSyncFromWx` = ?, " +
				" `datLastSubscribeTime` = ? " +
				" WHERE `numUserGuid` = ? ";

		int cnt = -1;
		try {
			cnt = MyDbUtils.update(db, sql, new Object[]{
					userInfo.getNumUserGuid(), userInfo.getVc2NickName(), userInfo.getVc2SexCode(), userInfo.getVc2ProvinceName(), userInfo.getVc2CityName(),
					userInfo.getVc2CountryName(), userInfo.getVc2Language(), userInfo.getVc2HeadImgUrl(), userInfo.getVc2UnionId(), userInfo.getVc2Remark(),
					userInfo.getVc2GroupId(), userInfo.getDatCreation(), userInfo.getDatLastUpdate(), userInfo.getDatLastSyncFromWx(), userInfo.getDatLastSubscribeTime(),
					userInfo.getNumUserGuid()
					});
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return cnt;
	}
	
}
