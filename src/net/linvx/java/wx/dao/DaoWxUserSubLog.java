package net.linvx.java.wx.dao;

import java.sql.Connection;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import net.linvx.java.libs.db.MyDbUtils;
import net.linvx.java.libs.tools.MyLog;

public class DaoWxUserSubLog {
	private static Logger log = MyLog.getLogger(DaoWxUserSubLog.class);
	/**
	 * 记录用户的关注取关操作日志
	 * 
	 * @param numUserGuid
	 * @param type
	 * @param sceneId
	 */
	public static void logUserSubOrUnSub(Integer numUserGuid, String type, String sceneId, Connection db) {
		String sql = "insert into wx_sub_or_unsub_log(numUserGuid,vc2SubOrUnSubFlag, vc2QRSceneId, datCreation)"
				+ "values(?,?,?,?)";

		try {
			MyDbUtils.update(db, sql,
					new Object[] { numUserGuid, type, sceneId, new Timestamp(System.currentTimeMillis()) });
		} catch (Exception e) {
			e.printStackTrace();
			log.error("", e);
		} 
	}
}
