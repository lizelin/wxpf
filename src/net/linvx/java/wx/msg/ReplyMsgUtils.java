package net.linvx.java.wx.msg;

import java.sql.Timestamp;

import net.linvx.java.libs.utils.MyStringUtils;
import net.linvx.java.wx.po.PoWxOfficialAccount;
import net.linvx.java.wx.po.PoWxReceivedMsg;

public class ReplyMsgUtils {

	public static ReplyMsgBase createTextMsg(final PoWxOfficialAccount account, final PoWxReceivedMsg recvMsg,
			final String content) {
		ReplyMsgBase replyMsg = new ReplyTextMsg(account.getNumAccountGuid(), recvMsg.getVc2ToUserName(),
				recvMsg.getVc2FromUserName()).setContent(content);
		recvMsg.setVc2ReplyMsg(replyMsg.getCompleteMsgData());
		recvMsg.setDatReply(new Timestamp(System.currentTimeMillis()));
		return replyMsg;
	}

	public static ReplyMsgBase createWelcomeTextMsg(final PoWxOfficialAccount account, final PoWxReceivedMsg recvMsg) {
		// ReplyMsgBase replyMsg = new ReplyTextMsg(account.numAccountGuid,
		// recvMsg.vc2ToUserName, recvMsg.vc2FromUserName)
		// .setContent("Welcome to wx!");
		// return replyMsg;
		return ReplyMsgUtils.createTextMsg(account, recvMsg, "Welcome to wx!");
	}

	/**
	 * 是否推送消息（微信的）
	 * 
	 * @param msg
	 * @return
	 */
	public static boolean isEventMsg(PoWxReceivedMsg msg) {
		return MsgEnums.MsgType.event.name().equalsIgnoreCase(msg.getVc2MsgType());
	}

	public static boolean isSubscribeMsg(PoWxReceivedMsg msg) {
		return isEventMsg(msg) && MsgEnums.EventType.subscribe.name().equalsIgnoreCase(msg.attrs.get("Event"));
	}

	public static boolean isUnSubscribeMsg(PoWxReceivedMsg msg) {
		return isEventMsg(msg) && MsgEnums.EventType.unsubscribe.name().equalsIgnoreCase(msg.attrs.get("Event"));
	}

	public static boolean isSubscribeScanMsg(PoWxReceivedMsg msg) {
		return isSubscribeMsg(msg) && MyStringUtils.startWithIgnoreCase(msg.attrs.get("EventKey"), "qrscene_");
	}

	public static boolean isScanMsg(PoWxReceivedMsg msg) {
		return isEventMsg(msg) && MsgEnums.EventType.SCAN.name().equalsIgnoreCase(msg.attrs.get("Event"));
	}

	public static boolean isNeedReply(PoWxReceivedMsg msg) {
		return !ReplyMsgUtils.isEventMsg(msg) || (ReplyMsgUtils.isEventMsg(msg)
				&& (MsgEnums.EventType.CLICK.name().equalsIgnoreCase(msg.attrs.get("Event"))
						|| MsgEnums.EventType.SCAN.name().equalsIgnoreCase(msg.attrs.get("Event"))
						|| MsgEnums.EventType.subscribe.name().equalsIgnoreCase(msg.attrs.get("Event"))));
	}
}
