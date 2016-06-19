package net.linvx.java.wx.api;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import net.linvx.java.libs.db.MyDbUtils;
import net.linvx.java.libs.tools.MyLog;
import net.linvx.java.libs.utils.MyStringUtils;
import net.linvx.java.wx.bo.BoWxReceivedMsg;
import net.linvx.java.wx.bo.BoWxUser;

import net.linvx.java.wx.dao.DaoWxReceivedMsg;
import net.linvx.java.wx.dao.DbHelper;
import net.linvx.java.wx.msg.ReplyMsgUtils;
import net.linvx.java.wx.po.PoWxOfficialAccount;
import net.linvx.java.wx.po.PoWxReceivedMsg;

/**
 * 处理接收到的消息，包括微信服务器推送的，还有用户上行的
 * 这里也只是一个总入口，仅仅处理和关注用户相关的消息，其他消息转到真正的业务处理url去处理（http post过去）
 * 这个业务处理url在wx_official_account表中定义：vc2RouterUrl
 * 
 * @author lizelin
 *
 */
public class ProcessReceivedMsg {
	private static final Logger log = MyLog.getLogger(ProcessReceivedMsg.class);
	protected PoWxOfficialAccount account = null;
	protected Element rootElt = null;
	protected PoWxReceivedMsg recvMsg = null;
	Connection mDb = null;

	public ProcessReceivedMsg(PoWxOfficialAccount _account, Element _rootElt) {
		this.rootElt = _rootElt;
		this.account = _account;
		try {
			this.mDb = DbHelper.getWxDb();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		recvMsg = BoWxReceivedMsg.createReceivedMsgPo(account, rootElt);
	}

	public ProcessReceivedMsg process() throws ApiException {
		// 先保存一次原始数据，以防出错后错过入库时机
		saveReceivedMsg();

		// 调用业务处理url（account.vc2RouterUrl）实际处理消息
		// 并且特殊处理关注和取消关注消息
		if (ReplyMsgUtils.isSubscribeMsg(recvMsg) || ReplyMsgUtils.isUnSubscribeMsg(recvMsg)) {
			BoWxUser wxUser = new BoWxUser(account.getNumAccountGuid(), recvMsg.getVc2FromUserName(), mDb);
			if (ReplyMsgUtils.isSubscribeMsg(recvMsg) ) {
				wxUser.sub(recvMsg.attrs.get("EventKey"));
			} else {
				wxUser.unSub();
			}
			wxUser = null;
		}
		
		String reply = "";
		if (MyStringUtils.isNotEmpty(account.getVc2RouterUrl())) {
			try {
				reply = ApiUtils.post(account.getVc2RouterUrl(), recvMsg.getVc2OriMsg());
			} catch (ApiException e) {
				e.printStackTrace();
				log.error("post routerUrl return error: ", e);
			}
		}

		if (MyStringUtils.isEmpty(reply) && ReplyMsgUtils.isNeedReply(recvMsg))
			reply = ReplyMsgUtils.createWelcomeTextMsg(account, recvMsg).getCompleteMsgData();

		/*
		 * 如果处理后，reply不为空，则设置msg
		 */
		if (MyStringUtils.isNotEmpty(reply)) {
			recvMsg.setDatReply(new Timestamp(System.currentTimeMillis()));
			recvMsg.setVc2ReplyMsg(reply);
		}

		// 将自动回复的消息入库
		saveReceivedMsg();
		return this;
	}


	public PoWxReceivedMsg getReceivedMsg() {
		return recvMsg;
	}

	private void saveReceivedMsg() {
		DaoWxReceivedMsg.saveWxReceivedMsg(recvMsg, mDb);
	}

	public void releaseResource(){
		MyDbUtils.closeQuietly(mDb);
	}
	
	/**
	 * 回复给用户的消息字符串
	 * @return
	 */
	public String getReplyData() {
		if (recvMsg == null || MyStringUtils.isEmpty(recvMsg.getVc2ReplyMsg()))
			return null;
		return recvMsg.getVc2ReplyMsg();
	}

	public static void main(String[] args) {
	}
}
