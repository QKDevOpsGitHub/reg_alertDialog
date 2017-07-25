package com.qk.reg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class Headless {
	static String sapklink;

	public static void apkDownload() throws Exception {
		sapklink = GmailEmailReceiver.retrieveApkLink();

		if (sapklink.length() > 0) {
			// System.out.println(GmailEmailReceiver.retrieveApkLink());
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet(sapklink);
			HttpResponse response = httpclient.execute(request);
			// System.out.println(response.toString());
			HttpEntity responsEntity = response.getEntity();

			if (responsEntity != null) {
				long len = responsEntity.getContentLength();
				InputStream is = responsEntity.getContent();

				try {

					File file = new File(System.getProperty("user.dir") + "\\app_release.apk");

					if (file.delete()) {
						System.out.println(file.getName() + " is deleted!");

					} else {
						System.out.println("Delete operation is failed.");

					}

				} catch (Exception e) {
					e.printStackTrace();

				}

				//String filePath = System.getProperty("user.dir") + "\\app_release.apk";
				String filePath = "D://apks/app-release.apk";
				System.out.println("Download directoty" + filePath);
				
				FileOutputStream fos = new FileOutputStream(new File(filePath), true);
				int inByte;

				while ((inByte = is.read()) != -1)
					fos.write(inByte);

				is.close();
				// System.out.println("Tetst");
				fos.close();

			}

		} else {
			System.out.println("APK release has not come");

		}

	}

}