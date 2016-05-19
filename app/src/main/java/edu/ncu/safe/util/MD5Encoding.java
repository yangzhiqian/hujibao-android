package edu.ncu.safe.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encoding {

	public static String encoding(String pwd){
		
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] res = digest.digest(pwd.getBytes());
			
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < res.length; i++) {
				String s = Integer.toHexString(0xff&res[i]);
				if(s.length()==1){
					sb.append("0"+s);
				}else{
					sb.append(s);
				}
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
	
}
