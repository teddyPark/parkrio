package com.sneezer.parkrio;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.AssetManager;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

public class HttpClientForParkrio {

	private final static String userAgentHeader = new String("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1;; APCPMS=E839.353E.2E0A-win7^N20120502090046254556C65BBCE3E22DEE3F_1981^1^1^2186362890^30282584^2160947136; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; TCO_20130225130629)");
	private final static String refererHeader = new String("http://211.49.175.211/hwork/iframe_DayValue.aspx");
	
	private final static String TODAY_PARAM_VIEWSTATE = "/wEPDwUKMTExNDI5NDgyMmRkxn5PRekHb9BgiR+zMd0FnM0Fa5I=";
	private final static String TODAY_URI = "/hwork/iframe_DayValue.aspx";
	private final static String MONTHLY_PARAM_VIEWSTATE = "/wEPDwUKMTU5NDg5OTA1MGRkQtZJsU4tV32pG1Vj7vs7uizyBb4=";
	private final static String MONTHLY_URI = "/hwork/iframe_MonthGraph.aspx";
	private final static String YEARLY_PARAM_VIEWSTATE = "/wEPDwUKMjAyNzU3OTkxM2RkMXAfR6Im5D2lQRtlQ01g4/icE7k=";
	private final static String YEARLY_URI = "/hwork/iframe_YearGraph.aspx";
	
	private String html;
	
	public String SERVER_CHARSET = "EUC-KR";
	public String paramViewState;
	public String uri;
	
	public HttpClientForParkrio (String catId) {
		if ( catId.equals("dayly") ) {
			paramViewState=TODAY_PARAM_VIEWSTATE;
			uri = TODAY_URI;
		} else if ( catId.equals("monthly")) {
			paramViewState=MONTHLY_PARAM_VIEWSTATE;
			uri=MONTHLY_URI;
		} else if ( catId.equals("yearly")) {
			paramViewState=YEARLY_PARAM_VIEWSTATE;
			uri=YEARLY_URI;
		}
	}
	
	public String fetch(URL url, String cookieStr, String postParam ) throws Exception {

		HttpURLConnection httpClient = null;
		StringBuilder sb = new StringBuilder();

		// set http client
		try {
			httpClient = (HttpURLConnection) url.openConnection();
			httpClient.setRequestMethod("POST");
			httpClient.setRequestProperty("Referer",refererHeader);
			httpClient.setRequestProperty("User-Agent", userAgentHeader);
			httpClient.setRequestProperty("Cookie",cookieStr);

			OutputStream opstrm = httpClient.getOutputStream();
			opstrm.write(postParam.getBytes());
			opstrm.flush();
			opstrm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
		try {
			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				sb.append(line + '\n');
				//Log.i("readHTML", line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			br.close();
			httpClient.disconnect();
		}

		html = sb.toString();
		if ( checkLoginSession() ) {
			return html;
		} else {
			throw new LogoutException();
		}

	}

	public static List<Double> parseMonthlyDataFromHtml (String html) throws Exception {
		List<Double> valueList = new ArrayList<Double>();

		Source source = new Source(html);
		source.fullSequentialParse();

		Element outerTable = source.getAllElements(HTMLElementName.TABLE)
				.get(1).getAllElements(HTMLElementName.TABLE).get(0);

		Element outerTd = outerTable.getAllElements(HTMLElementName.TR).get(0)
				.getAllElements(HTMLElementName.TD).get(1);
		Element innerTable = outerTd.getAllElements(HTMLElementName.TABLE).get(
				0);
		List innerTd = innerTable.getAllElements(HTMLElementName.TR).get(1)
				.getAllElements(HTMLElementName.TD);

		Iterator tdIter = innerTd.iterator();

		tdIter.next(); // skip empty td

		while (tdIter.hasNext()) {

			Element td = (Element) tdIter.next();
			if (td.getChildElements().size() > 0) {
				Element dataTable = td.getAllElements(HTMLElementName.TABLE).get(0);
				String temp = dataTable.getAttributeValue("onmouseover");
				Pattern p = Pattern.compile("[0-9.]+");
				Matcher mc = p.matcher(temp);
				if (mc.find()) {
					valueList.add(Double.parseDouble(mc.group(0)));
				}
			}
		}
		source.clearCache();		
		return valueList;
	}
		
	public static List<Double> parseYearlyDataFromHtml (String html) throws Exception {
		List<Double> valueList = new ArrayList<Double>();

		Source source = new Source(html);
		source.fullSequentialParse();

		Element outerTable = source.getAllElements(HTMLElementName.TABLE)
				.get(1).getAllElements(HTMLElementName.TABLE).get(0);

		Element outerTd = outerTable.getAllElements(HTMLElementName.TR).get(0)
				.getAllElements(HTMLElementName.TD).get(1);
		Element innerTable = outerTd.getAllElements(HTMLElementName.TABLE).get(
				0);
		List innerTd = innerTable.getAllElements(HTMLElementName.TR).get(1)
				.getAllElements(HTMLElementName.TD);

		Iterator tdIter = innerTd.iterator();

		tdIter.next(); // skip empty td

		while (tdIter.hasNext()) {

			Element td = (Element) tdIter.next();
			if (td.getChildElements().size() > 0) {
				Element dataTable = td.getAllElements(HTMLElementName.TABLE)
						.get(0);
				String temp = dataTable.getAttributeValue("onmouseover");
				Pattern p = Pattern.compile("[0-9.]+");
				Matcher mc = p.matcher(temp);
				if (mc.find()) {
					valueList.add(Double.parseDouble(mc.group(0)));
				}
			}
		}
		source.clearCache();
		return valueList;
	}	
	
	public static String readParkrioAsset(Context context, String filename) {
		AssetManager am = context.getResources().getAssets();
		InputStream is = null;
		String result = null;
		try {
			is = am.open(filename);
			int size = is.available();

			if (size > 0) {
				byte[] data = new byte[size];
				is.read(data);
				result = new String(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (Exception e) {
				}
			}
		}
		am = null;
		return result;
	}
	
	public boolean checkLoginSession () {
        if ( html.length() > 400 ) {
        	return true;
        }
		return false;
	}
	
//	public void finalize() {
//
//	}
}