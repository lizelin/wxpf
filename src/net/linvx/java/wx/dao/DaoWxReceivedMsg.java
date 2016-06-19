package net.linvx.java.wx.dao;

import java.sql.Connection;

import org.apache.log4j.Logger;

import net.linvx.java.libs.db.MyDbUtils;
import net.linvx.java.libs.tools.MyLog;
import net.linvx.java.wx.po.PoWxReceivedMsg;

public class DaoWxReceivedMsg {
	private static Logger log = MyLog.getLogger(DaoWxReceivedMsg.class);
	/**
	 * 保存消息，包括收到的和返回给用户的（一条记录）
	 * 
	 * @param msg
	 */
	public static void saveWxReceivedMsg(PoWxReceivedMsg msg, final Connection db) {
		StringBuffer sb = new StringBuffer();
		String sqlSelect = "select numMsgGuid from wx_received_msg where vc2MsgId = ? and numAccountGuid = ?";
		try {
			Integer msgGuid = MyDbUtils.getOne(db, sqlSelect, new Object[] { msg.getVc2MsgId(), msg.getNumAccountGuid() });
			if (msgGuid == null) {
				sb.append("INSERT INTO `wx_received_msg`");
				sb.append("	(`numAccountGuid`, `vc2ToUserName`, `vc2FromUserName`, `numWxCreateTime`,");
				sb.append("  `vc2MsgType`, `datReceive`, `vc2OriMsg`,`datReply`, ");
				sb.append("	 `vc2ReplyMsg`, `vc2MsgId`)");
				sb.append("VALUES");
				sb.append("	(?,?,?,?,");
				sb.append("	 ?,?,?,?,");
				sb.append("	 ?,?)");
				msgGuid = MyDbUtils.insertReturnKey(db, sb.toString(),
						new Object[] { msg.getNumAccountGuid(), msg.getVc2ToUserName(), msg.getVc2FromUserName(),
								msg.getNumWxCreateTime(), msg.getVc2MsgType(), msg.getDatReceive(), msg.getVc2OriMsg(),
								msg.getDatReply(), msg.getVc2ReplyMsg(), msg.getVc2MsgId() });
				msg.setNumMsgGuid(msgGuid);
			} else {
				sb.append("update `wx_received_msg`");
				sb.append("	set `numAccountGuid`=?, `vc2ToUserName`=?, `vc2FromUserName`=?, `numWxCreateTime`=?,");
				sb.append("  `vc2MsgType`=?, `datReceive`=?, `vc2OriMsg`=?,`datReply`=?, ");
				sb.append("	 `vc2ReplyMsg`=? ");
				sb.append(" where vc2MsgId = ? and numAccountGuid = ? ");
				int rowcount = MyDbUtils.update(db, sb.toString(),
						new Object[] { msg.getNumAccountGuid(), msg.getVc2ToUserName(), msg.getVc2FromUserName(),
								msg.getNumWxCreateTime(), msg.getVc2MsgType(), msg.getDatReceive(), msg.getVc2OriMsg(),
								msg.getDatReply(), msg.getVc2ReplyMsg(), msg.getVc2MsgId(), msg.getNumAccountGuid() });
				if (rowcount <= 0) {
					log.error("update wx_received_msg error: msgid is " + msg.getVc2MsgId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("", e);
		} 
	}

}
