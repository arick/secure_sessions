package co.ssessions.crypto;

import javax.validation.constraints.NotNull;
import javax.validation.executable.ValidateOnExecution;

import org.hibernate.validator.constraints.NotBlank;

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
	@ValidateOnExecution
	public abstract byte[] encrypt(@NotBlank String content);
	
	
	/**
	 * 
	 * @param contentBytes Content in byte array form that will be encrypted
	 * @return a byte array representing the encrypted content 
	 */
	@ValidateOnExecution
	public abstract byte[] encrypt(@NotNull byte[] contentBytes);
	
	
	/**
	 * 
	 * @param encryptedContentBytes Content in byte array form that will be decrypted
	 * @return a byte array representing the decrypted content 
	 */
	@ValidateOnExecution
	public abstract byte[] decrypt(@NotNull byte[] encryptedContentBytes);
	
	
}