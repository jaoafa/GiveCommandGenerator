package com.jaoafa.GiveCommandGenerator.Lib;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;

import com.jaoafa.GiveCommandGenerator.GiveCommandGenerator;

public class Pastebin {
	String code, name, expire, format, type;
	public Pastebin(String code, String name, String type, String expire, String format) {
		this.code = code;
		this.name = name;
		if(!type.equalsIgnoreCase("0") && !type.equalsIgnoreCase("1") && !type.equalsIgnoreCase("2")){
			throw new IllegalArgumentException("Type Error");
		}
		this.type = type;
		this.expire = expire;
		this.format = format;
	}
	public String Send() throws BadRequestException{
		String address = "https://pastebin.com/api/api_post.php";

		String devKey = GiveCommandGenerator.pastebin_devkey;
		if(devKey == null){
			throw new IllegalStateException("devKey Error");
		}

		Map<String, String> contents = new HashMap<>();
		contents.put("api_option", "paste");
		contents.put("api_user_key", "");
		contents.put("api_paste_private", type);
		contents.put("api_paste_name", name);
		contents.put("api_paste_expire_date", expire);
		contents.put("api_paste_format", format);
		contents.put("api_dev_key", devKey);
		contents.put("api_paste_code", code);
		String res = postHttpJson(address, null, contents);
		if(res == null){
			throw new NullPointerException("Null Error");
		}if(res.contains("Bad API request")){
			throw new BadRequestException(res);
		}
		return res;
	}
	private static String postHttpJson(String address, Map<String, String> headers, Map<String, String> contents){
		StringBuilder builder = new StringBuilder();
		try{
			URL url = new URL(address);

			HttpsURLConnection connect = (HttpsURLConnection)url.openConnection();
			connect.setRequestMethod("POST");
			if(headers != null){
				for(Map.Entry<String, String> header : headers.entrySet()){
					connect.setRequestProperty(header.getKey(), header.getValue());
				}
			}

			connect.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(connect.getOutputStream(), "UTF-8");
			List<String> list = new ArrayList<>();
			//JSONObject paramobj = new JSONObject();
			for(Map.Entry<String, String> content : contents.entrySet()){
				list.add(content.getKey() + "=" + content.getValue());
				//paramobj.put(content.getKey(), content.getValue());
			}
			String param = implode(list, "&");
			//out.write(paramobj.toJSONString());
			out.write(param);
			Bukkit.getLogger().info(param);
			//Bukkit.getLogger().info(paramobj.toJSONString());
			out.close();

			connect.connect();

			if(connect.getResponseCode() != HttpURLConnection.HTTP_OK){
				InputStream in = connect.getErrorStream();

				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				in.close();
				connect.disconnect();

				Bukkit.getLogger().warning("PastebinWARN: " + builder.toString());
				return null;
			}

			InputStream in = connect.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			in.close();
			connect.disconnect();
			return builder.toString();
		}catch(Exception e){
			e.printStackTrace();
			//BugReport.report(e);
			return null;
		}
	}
	public static <T> String implode(List<T> list, String glue) {
	    StringBuilder sb = new StringBuilder();
	    for (T e : list) {
	        sb.append(glue).append(e);
	    }
	    return sb.substring(glue.length());
	}
	public class BadRequestException extends Exception {
		public BadRequestException(String str) {
			super(str);
		}
	}
}
