package co.ssessions.crypto;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class PKCS8CryptoServiceTest extends TestCase {

	@Before
	protected void setUp() throws Exception {
		super.setUp();
	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testEncrpytDecrypt_1() {
		
		try {
			
			String content = "This is really cool";
			String privateKeyFilePath = "src/resources/privkey.pkcs8.pem";
			
			CryptoService cryptoService = new PKCS8CryptoService(privateKeyFilePath);
			
			
			/*
			 * Method under test
			 */
			byte[] encryptedContentBytes = cryptoService.encrypt(content);
			
			
			/*
			 * Method under test
			 */
			byte[] decryptedContentBytes = cryptoService.decrypt(encryptedContentBytes);
			
			String decryptedContent = new String(decryptedContentBytes, "UTF8");
			
			assertEquals(content, decryptedContent);
		
		} catch(UnsupportedEncodingException uee) {
			uee.printStackTrace();
		}
	} // END testEncrpytDecrypt_1 Metod
	
	

} // END PKCS8CryptoServiceTest Class
