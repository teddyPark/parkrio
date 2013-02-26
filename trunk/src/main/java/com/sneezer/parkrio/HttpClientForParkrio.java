package com.sneezer.parkrio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;

import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class HttpClientForParkrio {

	public static String fetch(URL url, String cookieStr, String postParam) {

		HttpResponse response = null;
		HttpURLConnection httpClient = null;
		StringBuilder html = new StringBuilder();

		// set http client
		try {
			httpClient = (HttpURLConnection) url.openConnection();
			httpClient.setRequestMethod("POST");
			httpClient.setRequestProperty("Referer",
					"http://211.49.175.211/hwork/iframe_DayValue.aspx");
			httpClient
					.setRequestProperty(
							"User-Agent",
							"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1;; APCPMS=E839.353E.2E0A-win7^N20120502090046254556C65BBCE3E22DEE3F_1981^1^1^2186362890^30282584^2160947136; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; TCO_20130225130629)");
			httpClient.setRequestProperty("Cookie",cookieStr);

			OutputStream opstrm = httpClient.getOutputStream();
			opstrm.write(postParam.getBytes());
			opstrm.flush();
			opstrm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}


		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				html.append(line + '\n');
				//Log.i("readHTML", line);
			}
			br.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html.toString();
	}
}
