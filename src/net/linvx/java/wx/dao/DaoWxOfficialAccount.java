package net.linvx.java.wx.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import net.linvx.java.libs.db.MyDbUtils;
import net.linvx.java.libs.tools.MyLog;
import net.linvx.java.wx.po.PoWxOfficialAccount;

public class DaoWxOfficialAccount {
	private static Logger log = MyLog.getLogger(DaoWxOfficialAccount.class);
	
	public static PoWxOfficialAccount getWxOfficialAccountByApiUrl(String apiUrl) {
		PoWxOfficialAccount account = null;
		Connection db = null;
		try {
			db = DbHelper.getWxDb();
			account = DaoWxOfficialAccount.getWxOfficialAccountByApiUrl(apiUrl, db);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			MyDbUtils.closeQuietly(db);
		}
		return account;
		
	}
	/**
	 * 根据微信的api地址（即接收微信服务器推送消息的api url），获取服务号信息 将来可做cache
	 * 
	 * @param apiUrl
	 * @return
	 */
	public static PoWxOfficialAccount getWxOfficialAccountByApiUrl(String apiUrl, final Connection db) {
		PoWxOfficialAccount account = null;
		try {
			account = MyDbUtils.getRow(db, "select * from wx_official_account where vc2ApiUrl = ?",
					new String[] { apiUrl }, PoWxOfficialAccount.class);
		} catch (Exception e) {
			log.error("", e);
			e.printStackTrace();
		} 
		return account;
	}
	
	public static PoWxOfficialAccount getWxOfficialAccountByAccountCode(String accountCode) {
		PoWxOfficialAccount account = null;
		Connection db = null;
		try {
			db = DbHelper.getWxDb();
			account = DaoWxOfficialAccount.getWxOfficialAccountByAccountCode(accountCode, db);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			MyDbUtils.closeQuietly(db);
		}
		return account;
		
	}

	/**
	 * 根据微信帐号的vc2AccountCode（即每个公众号分配一个更直观的code），获取服务号信息 将来可做cache
	 * 
	 * @param accountCode
	 * @return
	 */
	public static PoWxOfficialAccount getWxOfficialAccountByAccountCode(String accountCode,final Connection db) {
		PoWxOfficialAccount account = null;
		try {
			account = MyDbUtils.getRow(db, "select * from wx_official_account where vc2AccountCode = ?",
					new String[] { accountCode }, PoWxOfficialAccount.class);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("", e);
		}
		return account;
	}

	public static PoWxOfficialAccount getWxOfficialAccountByAccountGuid(Integer accountGuid) {
		PoWxOfficialAccount account = null;
		Connection db = null;
		try {
			db = DbHelper.getWxDb();
			account = DaoWxOfficialAccount.getWxOfficialAccountByAccountGuid(accountGuid, db);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			MyDbUtils.closeQuietly(db);
		}
		return account;
		
	}
	
	
	public static PoWxOfficialAccount getWxOfficialAccountByAccountGuid(Integer accountGuid,final Connection db) {
		PoWxOfficialAccount account = null;
		try {
			account = MyDbUtils.getRow(db, "select * from wx_official_account where numAccountGuid = ?",
					new Object[] { accountGuid }, PoWxOfficialAccount.class);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("", e);
		}
		return account;
	}


}
