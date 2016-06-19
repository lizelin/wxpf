package net.linvx.java.wx.common;

public class AppHelper {
	public static boolean isWeixin(String ua) {
		return ua.matches(".*MicroMessenger\\/(\\S+).*");
	}
	
	public static void main(String[] args) {
		String ua = "Mozilla/5.0 (Linux; Android 5.1; HUAWEI TAG-AL00 Build/HUAWEITAG-AL00) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/37.0.0.0 Mobile MQQBrowser/6.2 TBS/036523 Safari/537.36 MicroMessenger/6.3.18.800 NetType/WIFI Language/zh_CN";
		System.out.println(AppHelper.isWeixin(ua));
	}
}
