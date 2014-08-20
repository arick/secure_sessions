package co.ssessions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import co.ssessions.crypto.CryptoService;
import co.ssessions.crypto.PKCS8CryptoService;


public class PKITest {


	public static PrivateKey getPrivateKeyFromFile(String filePath) {
		
		PrivateKey privateKey = null;
		
		try {
			File privateKeyFile = new File(filePath);
			
			BufferedReader privateKeyBufferedReader = new BufferedReader(new FileReader(privateKeyFile));
			StringBuffer privateKeyStringBuffer = new StringBuffer();
			String privateKeyLine = null;
			while((privateKeyLine = privateKeyBufferedReader.readLine()) != null) {
				privateKeyStringBuffer.append(privateKeyLine);
			}
			String privateKeyString = privateKeyStringBuffer.toString();
			
			
			privateKeyString = privateKeyString.replace("-----BEGIN PRIVATE KEY-----", "");
			privateKeyString = privateKeyString.replace("-----END PRIVATE KEY-----", "");
	        
	        byte [] privateKeyBase64Decoded = Base64.getDecoder().decode(privateKeyString.getBytes());
			
	        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKeyBase64Decoded);
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
	        
	        return privateKey;
	        
		} catch (FileNotFoundException fnfe) { 
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
		} catch (InvalidKeySpecException ikse) {
			ikse.printStackTrace();
		}
		
		return privateKey;
		
	} // END getPrivateKeyFromFile Method
	
	
	public static PublicKey extractPublicKeyFromPrivateKey(PrivateKey privateKey) {
		
		PublicKey publicKey = null;
		
		// Extract the PublicKey from the PrivateKey
    	try {
    		
			RSAPrivateCrtKey privateCrtKey = (RSAPrivateCrtKey) privateKey;
			RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privateCrtKey.getModulus(), privateCrtKey.getPublicExponent());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			publicKey = keyFactory.generatePublic(publicKeySpec);
			
		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
		} catch (InvalidKeySpecException ikse) {
			ikse.printStackTrace();
		}
    	
    	return publicKey;
    	
	} // END extractPublicKeyFromPrivateKey Method
	
	
	public static byte[] encryptWithPublicKey(PublicKey publicKey, String content) {
		
		byte[] encryptedContentBytes = null;
			
		try {
			
			Cipher encipher = Cipher.getInstance("RSA");
			encipher.init(Cipher.ENCRYPT_MODE, publicKey);
			encryptedContentBytes = encipher.doFinal(content.getBytes());
			
		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
		} catch (IllegalBlockSizeException ibse) {
			ibse.printStackTrace();
		} catch (BadPaddingException bpe) {
			bpe.printStackTrace();
		} catch (NoSuchPaddingException nspe) {
			nspe.printStackTrace();
		} catch (InvalidKeyException ike) {
			ike.printStackTrace();
		}
		
		return encryptedContentBytes;
	}
	
	
	public static byte[] decryptWithPrivateKey(PrivateKey privateKey, byte[] encryptedContentBytes) {
		
		byte[] decryptedContentBytes = null;
			
		try {
			
			Cipher decipher = Cipher.getInstance("RSA");
			decipher.init(Cipher.DECRYPT_MODE, privateKey);
			decryptedContentBytes = decipher.doFinal(encryptedContentBytes);
			
		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
		} catch (IllegalBlockSizeException ibse) {
			ibse.printStackTrace();
		} catch (BadPaddingException bpe) {
			bpe.printStackTrace();
		} catch (NoSuchPaddingException nspe) {
			nspe.printStackTrace();
		} catch (InvalidKeyException ike) {
			ike.printStackTrace();
		}
		
		return decryptedContentBytes;
	}
	
	
	
	public static void main(String[] args) {
		
		try {
			
			String content = "This is really cool";
			String privateKeyFilePath = "src/resources/privkey.pkcs8.pem";
			
			CryptoService cryptoService = new PKCS8CryptoService(privateKeyFilePath);
			
			byte[] encryptedContentBytes = cryptoService.encrypt(content);
			
			// Print Results so far
			System.out.println("Content: " + content);
			System.out.println("Encrypted Content: " + new String(Base64.getEncoder().encode(encryptedContentBytes)));
			
			byte[] decryptedContentBytes = cryptoService.decrypt(encryptedContentBytes);
			
			System.out.println("Decrypted Content: " + new String(decryptedContentBytes, "UTF8"));
		
		} catch(UnsupportedEncodingException uee) {
			uee.printStackTrace();
		}
		
	} // END main Method


} // END PKITest Class
