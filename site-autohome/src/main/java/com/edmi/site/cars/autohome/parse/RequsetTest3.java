package com.edmi.site.cars.autohome.parse;

import com.edmi.site.cars.autohome.http.AutohomeCommonHttp;

import fun.jerry.httpclient.bean.HttpRequestHeader;

/**
 * @author 
 *https://www.walmart.com/
 */
public class RequsetTest3 {

	public static void main(String[] args) {
//		String url = "https://www.walmart.com/all-departments";
//		String url = "https://www.walmart.com/browse/electronics/tvs/3944_1060825_447913";
//		String url = "https://www.walmart.com/browse/electronics/tvs/3944_1060825_447913?page=3#searchProductResult";
		String url = "https://www.walmart.com/ip/VIZIO-D-Series-55-Class-4K-2160P-Ultra-HD-HDR-Smart-LED-TV-D55-F2-2018-Model/743971586";
		String cookie = "AID=wmlspartner%3D0%3Areflectorid%3D0000000000000000000000%3Alastupd%3D1532397165401; com.wm.reflector=\"reflectorid:0000000000000000000000@lastupd:1532401538044@firstcreate:1532397140726\"; DL=94066%2C%2C%2Cip%2C94066%2C%2C; NewYork=1; vtc=QOpqnbgKfQoQVUkVeD1mLE; TS011baee6=0103fe0547594e268577d165e00ff2f64994064581903ee5b27d5ab181e38648dac923f495848cf3ff3916b3db6dc95c7c7326f757cebfc46d9a105b63ba5fef6e417af238; TS01a3099b=0103fe05472cbe6280f0f2b045a2bbfb760f0ad0c2834b5d9470e737943bed89f08261228c47d1fd449d9de9a84555077b4219cfdf2b1f5292f90b860146af3d5531280fb9; TS01e3f36f=0103fe0547d53a76963f96caabfe02ffee589e397d903ee5b27d5ab181e38648dac923f495a75d1486adef43d065c9222e95c0a96dea717a4dd37bd25587dc74e5d4f2b2e0536380dbd954663a07e0cc2e74d4f61a8fbd94e96fb75bcaa26a5d60fdfb25408a3cf23e583dc030d8dde87e6f7c394d975e8ee11d86a3e19844dd2466e541c64afb15d6dd3eea59c7ed6d241ea2fc995cf61c9801375227a765a51246b1fbb840ed093193ac5dd0c2f4816884424a75; TS018dc926=0103fe0547fd2b433dd5bb303d078d824f4f51045a903ee5b27d5ab181e38648dac923f49502b050371ac810fce9b540f685601f5fe61c9f06335eacb0f1ecfe1f8380a9636a376236fdb6cf2cf56a265236b6c3aa8d0dd0cc61f93a8b88073ba266889b291b4b3f2000262a9c6b8308348f80c841; akavpau_p0=1532402138~id=faf4f5f626a676f22a8d10a35b7b389a; TBV=a0zye; s_pers=%20s_fid%3D4845D56E2891DF40-050681BBB8E04C23%7C1595557417127%3B%20s_v%3DY%7C1532400817133%3B%20gpv_p11%3DElectronics%253A%2520TV%2520%2526%2520Video%253A%2520All%2520TVs%7C1532400817138%3B%20gpv_p44%3DShelf%7C1532400817142%3B%20s_vs%3D1%7C1532400817143%3B; s_vi=[CS]v1|2DAB432E852A50AA-40000120E000002E[CE]; __gads=ID=b17181598999206e:T=1532397152:S=ALNI_MZNvE21Gy055eEDFfI-s9RIrrH_nQ; search.perf.metric=timerPromiseAll=1417ms|timerHeaderAction=56ms|timerSearchAction=1417ms|timerFooterAction=38ms|timerPreso=1415ms; s_sess=%20ent%3Dectronics%253ATV%2526Video%253AAllTVs%3B%20cp%3DY%3B%20chan%3Dorg%3B%20v59%3DElectronics%3B%20v54%3DElectronics%253A%2520TV%2520%2526%2520Video%253A%2520All%2520TVs%3B%20cps%3D1%3B%20s_sq%3D%3B; location-data=94066%3ASan%20Bruno%3ACA%3A%3A0%3A0|21k%2C46y%2C1kf%2C1rc%2C46q%2C2nz%2C2b1%2C4bu%2C2er%2C1o1|2|1; athrvi=RVI~h2c581b02";
		HttpRequestHeader hearder = new HttpRequestHeader();
		hearder.setUrl(url);
//		hearder.setCookie(cookie);
		String content = AutohomeCommonHttp.getReputationComment(hearder);
		System.out.println(content);

	}
}
