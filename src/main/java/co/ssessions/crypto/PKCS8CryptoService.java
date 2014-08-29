package co.ssessions.crypto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import javax.validation.constraints.NotNull;
import javax.validation.executable.ValidateOnExecution;

import org.hibernate.validator.constraints.NotBlank;

import co.ssessions.util.DoValidation;

import com.google.inject.Inject;



public class PKCS8CryptoService implements CryptoService {

	private CryptoServiceConfiguration cryptoServiceConfiguration;
	private String privateKeyFilePath;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	
	@Inject
	public PKCS8CryptoService(CryptoServiceConfiguration cryptoServiceConfiguration) {
		this.cryptoServiceConfiguration = cryptoServiceConfiguration;
		this.privateKeyFilePath = this.cryptoServiceConfiguration.get("crypto.privateKeyFilePath");
		this.init();
	}
	
	
	public void init() {
		this.privateKey = this.getPrivateKeyFromFile(this.privateKeyFilePath);
		this.publicKey = this.extractPublicKeyFromPrivateKey(this.privateKey);
	}
	
	
	private PrivateKey getPrivateKeyFromFile(@NotNull String filePath) {
		
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
			privateKeyBufferedReader.close();
			
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
	
	
	private PublicKey extractPublicKeyFromPrivateKey(PrivateKey privateKey) {
		
		PublicKey publicKey = null;
		
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
	
	
	private byte[] encryptWithPublicKey(PublicKey publicKey, byte[] contentBytes) {
		
		byte[] encryptedContentBytes = null;
			
		try {
			
			Cipher encipher = Cipher.getInstance("RSA");
			encipher.init(Cipher.ENCRYPT_MODE, publicKey);
			encryptedContentBytes = encipher.doFinal(contentBytes);
			
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
	
	
	private byte[] decryptWithPrivateKey(PrivateKey privateKey, byte[] encryptedContentBytes) {
		
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
	
	
	/* (non-Javadoc)
	 * @see co.ssessions.crypto.CryptoService#encrypt(java.lang.String)
	 */
	@ValidateOnExecution
	@Override
	public byte[] encrypt (@NotBlank String content) {
		
		byte[] encryptedContentBytes = this.encrypt(content.getBytes());
		return encryptedContentBytes;

	}
	
	
	/* (non-Javadoc)
	 * @see co.ssessions.crypto.CryptoService#decrypt(byte[])
	 */
	@ValidateOnExecution
	@Override
	public byte[] decrypt (byte[] encryptedContentBytes) {
		
		byte[] decryptedContentBytes = this.decryptWithPrivateKey(this.privateKey, encryptedContentBytes);
		return decryptedContentBytes;
	}


	/* (non-Javadoc)
	 * @see co.ssessions.crypto.CryptoService#encrypt(byte[])
	 */
	@ValidateOnExecution
	@Override
	public byte[] encrypt(@NotNull byte[] contentBytes) {
		
		byte[] encryptedContentBytes = this.encryptWithPublicKey(this.publicKey, contentBytes);
		return encryptedContentBytes;
	}
	
//	
//	private void validateParameters(String methodName, Class<?>[] parameterTypes, Object[] parameters) {
//
//		Method method = null;
//		try {
//			method = this.getClass().getMethod(methodName, parameterTypes);
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		}
//		
//		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//		ExecutableValidator executableValidator = factory.getValidator().forExecutables();
//		Set violations = executableValidator.validateParameters(this, method, parameters);
//
//		if ((violations != null) && (violations.size() > 0)) {
//			throw new IllegalArgumentException(violations.toString());
//		}
//	}

}
