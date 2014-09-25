package com.andreldm.rcontrol;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class Util {
	private static byte[] serverAddress = null;
	
	public static String checkServer() {
		try {
			if(serverAddress != null && InetAddress.getByAddress(serverAddress).isReachable(100)) {
				return getAddressAsString(serverAddress);
			}

		    byte[] ip = {(byte)192, (byte)168, 1, 0}; // for 192.168.1.x addresses  
		    for (int i = 1; i <= 254; i++)  
		    {
		        ip[3] = (byte) i;  
		        InetAddress address = InetAddress.getByAddress(ip);
		        if(address.isReachable(100)) {
		        	HttpClient httpclient = new DefaultHttpClient();
		            HttpResponse response;
		            String responseString = null;
		            try {
		                response = httpclient.execute(new HttpGet(getAddressAsString(ip) + ":3000/check"));
		                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
		                    ByteArrayOutputStream out = new ByteArrayOutputStream();
		                    response.getEntity().writeTo(out);
		                    out.close();
		                    responseString = out.toString();
		                    if("ok".equals(responseString)) {
		    					Log.i(RequestTask.class.getName(), "Found server: " + address.getHostAddress());
		                    	serverAddress = ip;
		                    	break;
		                    }
		                } else{
		                    //Closes the connection.
		                    response.getEntity().getContent().close();
		                }
		            } catch (ClientProtocolException e) {
		                //TODO Handle problems..
		            } catch (IOException e) {
		                //TODO Handle problems..
		            }
		        }
		    }
		} catch (Exception e) {
			Log.e(RequestTask.class.getName(), "Erro: " + e.toString());
		}
		
		return getAddressAsString(serverAddress);
	}
	
	private static String getAddressAsString(byte[] address) {
		StringBuffer s = new StringBuffer();
		for (byte b : address) {
			s.append(b);
			s.append(".");
		}
		s.deleteCharAt(s.length() - 1);
		return s.toString();
	}

	public static String convertStreamToString(InputStream is)
			throws IOException {
		/*
		 * To convert the InputStream to String we use the Reader.read(char[]
		 * buffer) method. We iterate until the Reader return -1 which means
		 * there's no more data to read. We use the StringWriter class to
		 * produce the string.
		 */
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	public static boolean isOnline(Activity a) {
		ConnectivityManager cm = (ConnectivityManager) a
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
}
