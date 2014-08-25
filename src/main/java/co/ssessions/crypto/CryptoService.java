package co.ssessions.crypto;

/**
 * Defines the interface that encryption schemes must implement.
 * 
 * @author rashadmoore
 *
 */
public interface CryptoService {

	/**
	 * 
	 * @param content Content to be encrypted in String form
	 * @return a byte array representing the encrypted content 
	 */
	public abstract byte[] encrypt(String content);
	
	
	/**
	 * 
	 * @param contentBytes Content in byte array form that will be encrypted
	 * @return a byte array representing the encrypted content 
	 */
	public abstract byte[] encrypt(byte[] contentBytes);
	
	
	/**
	 * 
	 * @param encryptedContentBytes Content in byte array form that will be decrypted
	 * @return a byte array representing the decrypted content 
	 */
	public abstract byte[] decrypt(byte[] encryptedContentBytes);

}