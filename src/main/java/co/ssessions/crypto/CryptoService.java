package co.ssessions.crypto;

public interface CryptoService {

	public abstract byte[] encrypt(String content);
	
	public abstract byte[] encrypt(byte[] contentBytes);

	public abstract byte[] decrypt(byte[] encryptedContentBytes);

}