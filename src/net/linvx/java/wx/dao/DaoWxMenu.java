package net.linvx.java.wx.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import net.linvx.java.libs.db.MyDbUtils;
import net.linvx.java.libs.tools.MyLog;
import net.linvx.java.wx.model.Menu;

public class DaoWxMenu {
	private static Logger log = MyLog.getLogger(DaoWxMenu.class);
	/**
	 * 保存menus到数据库(注意，只支持两层！！！！)
	 * 
	 * @param menus
	 */
	public static void saveMenus(Menu[] menus, Integer numAccountGuid) {
		if (menus == null || menus.length == 0)
			return;
		Connection db = null;
		try {
			db = DbHelper.getWxDb();
			db.setAutoCommit(false);
			MyDbUtils.update(db, "delete from wx_menu where numAccountGuid = ?", new Object[] { numAccountGuid });
			String insertSql = "insert into wx_menu(`numAccountGuid`, `vc2MenuName`, `vc2MenuType`, "
					+ "`vc2MenuKey`, `vc2MenuUrl`, `numParentMenuGuid`, "
					+ "`datCreation`, `vc2CreatedBy`, `datLastUpdate`, "
					+ "`vc2LastUpdatedBy`, `vc2EnabledFlag`) values (" + "?,?,?," + "?,?,?," + "now(),'0',now(),"
					+ "'0','Y')";
			for (int i = 0; i < menus.length; i++) {
				Menu menu = menus[i];
				System.out.println(menu.getName());
				int menuGuid = MyDbUtils.insertReturnKey(db, insertSql, new Object[] { numAccountGuid, menu.getName(),
						menu.getType(), menu.getKey(), menu.getUrl(), 0 });
				if (menu.getSub_button() != null && menu.getSub_button().size() > 0) {
					for (int k = 0; k < menu.getSub_button().size(); k++) {
						Menu menusub = menu.getSub_button().get(k);
						MyDbUtils.insertReturnKey(db, insertSql, new Object[] { numAccountGuid, menusub.getName(),
								menusub.getType(), menusub.getKey(), menusub.getUrl(), menuGuid });
					}
				}
			}
			db.commit();
		} catch (InstantiationException e) {
			e.printStackTrace();
			log.error("", e);
		} catch (IllegalAccessException e) {
			log.error("", e);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("", e);
			e.printStackTrace();
		} catch (SQLException e) {
			log.error("", e);
			e.printStackTrace();
		} catch (NamingException e) {
			log.error("", e);
			e.printStackTrace();
		} catch (Exception e) {
			log.error("", e);
			e.printStackTrace();
		} finally {
			MyDbUtils.closeQuietly(db);
		}

	}

	/**
	 * 获取菜单
	 * 
	 * @param numAccountGuid
	 * @return
	 */
	public static Menu[] loadMenus(Integer numAccountGuid) {
		List<Menu> menus = new ArrayList<Menu>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT `numMenuGuid`,");
		sb.append("    `numAccountGuid`,");
		sb.append("    `vc2MenuName`,");
		sb.append("    `vc2MenuType`,");
		sb.append("    `vc2MenuKey`,");
		sb.append("    `vc2MenuUrl`,");
		sb.append("    `numParentMenuGuid` ");
		sb.append("	FROM `wx_menu`");
		sb.append("	where vc2EnabledFlag = 'Y' and numAccountGuid = ? ");
		sb.append("	and numParentMenuGuid = ? order by numOrder, numMenuGuid");
		Connection db = null;
		try {
			db = DbHelper.getWxDb();
			PreparedStatement ps = db.prepareStatement(sb.toString());
			ps.setInt(1, numAccountGuid.intValue());
			ps.setInt(2, 0);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Menu m = new Menu();
				m.setKey(rs.getString("vc2MenuKey"));
				m.setName(rs.getString("vc2MenuName"));
				m.setType(rs.getString("vc2MenuType"));
				m.setUrl(rs.getString("vc2MenuUrl"));
				m.setSub_button(new ArrayList<Menu>());
				PreparedStatement pssub = db.prepareStatement(sb.toString());
				pssub.setInt(1, numAccountGuid.intValue());
				pssub.setInt(2, rs.getInt("numMenuGuid"));
				ResultSet rssub = pssub.executeQuery();
				while (rssub.next()) {
					Menu msub = new Menu();
					msub.setKey(rssub.getString("vc2MenuKey"));
					msub.setName(rssub.getString("vc2MenuName"));
					msub.setType(rssub.getString("vc2MenuType"));
					msub.setUrl(rssub.getString("vc2MenuUrl"));
					m.getSub_button().add(msub);
				}
				rssub.close();
				pssub.close();
				menus.add(m);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			log.error("", e);
			e.printStackTrace();
		} finally {
			MyDbUtils.closeQuietly(db);
		}

		return menus.toArray(new Menu[] {});
	}
}
