package co.ssessions;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class TestEncrypt {


	public static void main(String[] args) {
		
		
		try {
			String content = "This is really cool";
			
			SecretKey secretKey = KeyGenerator.getInstance("DES").generateKey();
			Cipher encipher = Cipher.getInstance("DES");
			Cipher decipher = Cipher.getInstance("DES");
			
			encipher.init(Cipher.ENCRYPT_MODE, secretKey);
			decipher.init(Cipher.DECRYPT_MODE, secretKey);
			
			
			byte[] contentBytes = content.getBytes();
			byte[] encodedBytes = encipher.doFinal(contentBytes);
			byte[] Base64encodedBytes = Base64.getEncoder().encode(encodedBytes);
			String encodedBytesString = new String(Base64encodedBytes);
			
			System.out.println("Content: " + content);
			System.out.println("\n\n\nEncrypted: " + encodedBytesString);
			
			byte[] decodedBytes = Base64.getDecoder().decode(encodedBytesString.getBytes());
			byte[] decodedContentBytes = decipher.doFinal(decodedBytes);
			
			String decodedContentString = new String(decodedContentBytes, "UTF8");
			
			System.out.println("Original Content: " + decodedContentString);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	} // END main Method

} // END TestEncrypt Method
