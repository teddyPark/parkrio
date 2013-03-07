package com.sneezer.parkrio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClientForParkrio {

	private static String userAgentHeader = new String("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1;; APCPMS=E839.353E.2E0A-win7^N20120502090046254556C65BBCE3E22DEE3F_1981^1^1^2186362890^30282584^2160947136; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; TCO_20130225130629)");
	private static String refererHeader = new String("http://211.49.175.211/hwork/iframe_DayValue.aspx");
	private String html;
	
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
