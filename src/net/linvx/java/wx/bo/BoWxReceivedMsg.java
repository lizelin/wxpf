package net.linvx.java.wx.bo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import net.linvx.java.libs.tools.MyLog;
import net.linvx.java.libs.utils.MyStringUtils;
import net.linvx.java.wx.po.PoWxOfficialAccount;
import net.linvx.java.wx.po.PoWxReceivedMsg;

public class BoWxReceivedMsg {
	private static Logger log = MyLog.getLogger(BoWxReceivedMsg.class);
		
	public static PoWxReceivedMsg createReceivedMsgPo(PoWxOfficialAccount account, String msg) {
		Element rootElt = null;
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(msg);
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}
		rootElt = doc.getRootElement();
		return BoWxReceivedMsg.createReceivedMsgPo(account, rootElt);
	}
	
	/**
	 * 根据微信传来的xml，生成消息
	 * 
	 * @param account
	 * @param rootElt
	 * @return
	 */
	public static PoWxReceivedMsg createReceivedMsgPo(PoWxOfficialAccount account, Element rootElt) {
		PoWxReceivedMsg recvMsg = new PoWxReceivedMsg();
		recvMsg.setDatReceive(new Timestamp(System.currentTimeMillis()));
		recvMsg.setDatReply(null);
		recvMsg.setNumAccountGuid(account.getNumAccountGuid());
		recvMsg.setNumWxCreateTime(BigDecimal.valueOf(Long.parseLong(rootElt.elementText("CreateTime"))));
		recvMsg.setVc2FromUserName(rootElt.elementText("FromUserName"));
		recvMsg.setVc2MsgType(rootElt.elementText("MsgType"));
		recvMsg.setVc2OriMsg(rootElt.asXML());
		recvMsg.setVc2ReplyMsg(null);
		recvMsg.setVc2ToUserName(rootElt.elementText("ToUserName"));
		String msgId = rootElt.elementText("MsgId");
		if (MyStringUtils.isEmpty(msgId)) {
			msgId = recvMsg.getVc2FromUserName() + rootElt.elementText("CreateTime");
		}
		recvMsg.setVc2MsgId(msgId);

		Map<String, String> msgAttr = recvMsg.attrs;
		// if (MyStringUtils.isNotEmpty(rootElt.elementText("Event")))
		msgAttr.put("Event", rootElt.elementText("Event"));
		// if (MyStringUtils.isNotEmpty(rootElt.elementText("EventKey")))
		msgAttr.put("EventKey", rootElt.elementText("EventKey"));
		// if (MyStringUtils.isNotEmpty(rootElt.elementText("Content")))
		msgAttr.put("Content", rootElt.elementText("Content"));
		// if (MyStringUtils.isNotEmpty(rootElt.elementText("Ticket")))
		msgAttr.put("Ticket", rootElt.elementText("Ticket"));
		// if (MyStringUtils.isNotEmpty(rootElt.elementText("Latitude")))
		msgAttr.put("Latitude", rootElt.elementText("Latitude"));
		// if (MyStringUtils.isNotEmpty(rootElt.elementText("Longitude")))
		msgAttr.put("Longitude", rootElt.elementText("Longitude"));
		// if (MyStringUtils.isNotEmpty(rootElt.elementText("Precision")))
		msgAttr.put("Precision", rootElt.elementText("Precision"));
		// if (MyStringUtils.isNotEmpty(rootElt.elementText("OrderId")))
		msgAttr.put("OrderId", rootElt.elementText("OrderId"));
		// if (MyStringUtils.isNotEmpty(rootElt.elementText("OrderStatus")))
		msgAttr.put("OrderStatus", rootElt.elementText("OrderStatus"));
		// if (MyStringUtils.isNotEmpty(rootElt.elementText("ProductId")))
		msgAttr.put("ProductId", rootElt.elementText("ProductId"));
		// if (MyStringUtils.isNotEmpty(rootElt.elementText("SkuInfo")))
		msgAttr.put("SkuInfo", rootElt.elementText("SkuInfo"));
		return recvMsg;
	}
	
}
