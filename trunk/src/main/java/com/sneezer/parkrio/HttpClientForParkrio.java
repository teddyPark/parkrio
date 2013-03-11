package com.sneezer.parkrio;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

public class HttpClientForParkrio {

	private final static String userAgentHeader = new String("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1;; APCPMS=E839.353E.2E0A-win7^N20120502090046254556C65BBCE3E22DEE3F_1981^1^1^2186362890^30282584^2160947136; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; TCO_20130225130629)");
	private final static String refererHeader = new String("http://211.49.175.211/hwork/iframe_DayValue.aspx");
	
	public final static String TODAY_PARAM_VIEWSTATE = "/wEPDwUKMTExNDI5NDgyMmRkxn5PRekHb9BgiR+zMd0FnM0Fa5I=";
	public final static String TODAY_URI = "/hwork/iframe_DayValue.aspx";
	public final static String MONTHLY_PARAM_VIEWSTATE = "/wEPDwUKMTU5NDg5OTA1MGRkQtZJsU4tV32pG1Vj7vs7uizyBb4=";
	public final static String MONTHLY_URI = "/hwork/iframe_MonthGraph.aspx";
	public final static String YEARLY_PARAM_VIEWSTATE = "/wEPDwUKMjAyNzU3OTkxM2RkMXAfR6Im5D2lQRtlQ01g4/icE7k=";
	public final static String YEARLY_URI = "/hwork/iframe_YearGraph.aspx";
	
	private String html;
	
	public String SERVER_CHARSET = "EUC-KR";
	public String paramViewState;
	public String uri;
	
	public HttpClientForParkrio (String catId) throws Exception {
		if ( catId.equals("daily") ) {
			paramViewState = URLEncoder.encode(TODAY_PARAM_VIEWSTATE, SERVER_CHARSET);
			uri = TODAY_URI;
		} else if ( catId.equals("monthly")) {
			paramViewState=URLEncoder.encode(MONTHLY_PARAM_VIEWSTATE, SERVER_CHARSET);
			uri=MONTHLY_URI;
		} else if ( catId.equals("yearly")) {
			paramViewState=URLEncoder.encode(YEARLY_PARAM_VIEWSTATE, SERVER_CHARSET);
			uri=YEARLY_URI;
		}
	}
	
	public String fetch(URL url, String cookieStr, String postParam ) throws Exception {

		HttpURLConnection httpClient = null;
		StringBuilder sb = new StringBuilder();

		// set http client
		try {
			httpClient = (HttpURLConnection) url.openConnection();
			httpClient.setConnectTimeout(3000);
			httpClient.setReadTimeout(2000);
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
	
	public Map<String,Measurement> parseDayValuePage(String dayValue) throws Exception {
		Measurement usedMeasure = new Measurement();
		Measurement amountMeasure = new Measurement();
		Map<String,Measurement> resultMap = new HashMap<String,Measurement>();
		
		Source source = new Source(dayValue);
		source.fullSequentialParse();
		
		Element table = source.getAllElements(HTMLElementName.TABLE).get(0);
		List trList = table.getAllElements(HTMLElementName.TR);
		Iterator trIter = trList.iterator();
		
		trIter.next();
		trIter.next();
		trIter.next();
 
		for ( int i = 0 ; i < 5 ; i++ ) {
			Element tr = (Element) trIter.next();
			List dataList = tr.getAllElements(HTMLElementName.TD);
			Iterator tdIter = dataList.iterator();
			
			//current total value
			tdIter.next();
			tdIter.next();
			Element data = (Element) tdIter.next();	// third TD
			double amount = Double.parseDouble(data.getContent().getTextExtractor().toString());

			// today use value
			tdIter.next();
			data = (Element) tdIter.next();	// fifth TD
			double used = Double.parseDouble(data.getContent().getTextExtractor().toString());
			
			if (i == 0) {
				// 전기
				usedMeasure.setElec(used);
				amountMeasure.setElec(amount);
			} else if ( i == 1 ) {
				// 수도
				usedMeasure.setWater(used);
				amountMeasure.setWater(amount);
			} else if ( i == 2 ) {
				// 온수
				usedMeasure.setHotwater(used);
				amountMeasure.setHotwater(amount);
			} else if ( i == 3 ) {
				// 가스
				usedMeasure.setGas(used);
				amountMeasure.setGas(amount);
			} else if ( i == 4 ) {
				// 난방
				usedMeasure.setHeat(used);
				amountMeasure.setHeat(amount);
			}
			 
			trIter.next();				
		}
		Log.i("used",usedMeasure.toString());
		Log.i("amount",amountMeasure.toString());
		resultMap.put("used",usedMeasure);
		resultMap.put("amount",amountMeasure);
		source.clearCache();
		return resultMap;
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

}
