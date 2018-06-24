package com.pb.jobclient.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AESEncryptionUtil {

	public static final String AES = "AES";
	public static final String KEY = "70acb9ee31fe47eeb9011de5f0e5a1c7";

	private static String byteArrayToHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

	private static byte[] hexStringToByteArray(String s) {
		byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < b.length; i++) {
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte) v;
		}
		return b;
	}

	public static String encryptQuiet(String plain) {
		try {
			return encrypt(plain);
		} catch (Exception e) {
			return "";
		}
	}

	public static String decryptQuiet(String enc){
		try {
			return decrypt(enc);
		} catch (Exception e) {
			return "";
		}
	}

	public static String encrypt(String textToEncrypt)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		byte[] bytekey = hexStringToByteArray(KEY);
		SecretKeySpec sks = new SecretKeySpec(bytekey, AESEncryptionUtil.AES);
		Cipher cipher = Cipher.getInstance(AESEncryptionUtil.AES);
		cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
		byte[] encrypted = cipher.doFinal(textToEncrypt.getBytes());
		String encryptedpwd = byteArrayToHexString(encrypted);
		return encryptedpwd;
	}

	public static String decrypt(String encryptedPwd) throws NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		if(encryptedPwd==null || encryptedPwd.trim().length()<1)return null;
		byte[] bytekey = hexStringToByteArray(KEY);
		SecretKeySpec sks = new SecretKeySpec(bytekey, AESEncryptionUtil.AES);
		Cipher cipher = Cipher.getInstance(AESEncryptionUtil.AES);
		cipher.init(Cipher.DECRYPT_MODE, sks);
		byte[] decrypted = cipher.doFinal(hexStringToByteArray(encryptedPwd));
		return new String(decrypted);
	}

	public static void main(String[] args) {
		System.out.println(encryptQuiet("sourabhadmin303@yopmail.com"));
		System.out.println(encryptQuiet("Password1@"));
	}

}
