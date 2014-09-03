package co.ssessions.crypto;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import co.ssessions.conf.EnvConfig;
import co.ssessions.couchbase.CouchbaseSecureSessionsModule;
import co.ssessions.crypto.impl.PKCS8CryptoService;

import com.google.inject.Guice;
import com.google.inject.Injector;


public class PKCS8CryptoServiceTest extends TestCase {

	Injector injector = null;
	CryptoService cryptoService = null;
	
	
	@Before
	protected void setUp() throws Exception {

		String managerConfigFilePath = EnvConfig.getSafe("tomcat_manager_conf.propertiesFilePath");
		EnvConfig.load(managerConfigFilePath);
		
		this.injector = Guice.createInjector(new CouchbaseSecureSessionsModule());
		this.cryptoService = this.injector.getInstance(CryptoService.class);
		assertNotNull(this.cryptoService);
		assertTrue(this.cryptoService instanceof PKCS8CryptoService);
	}

	@After
	protected void tearDown() throws Exception {
		this.injector = null;
		this.cryptoService = null;
		EnvConfig.clearSingleton();
	}

	@Test
	public void testEncrpytDecrypt_1() {
		
		try {
			
			String content = "This is really cool";
			
			/*
			 * Method under test
			 */
			byte[] encryptedContentBytes = this.cryptoService.encrypt(content);
			
			/*
			 * Method under test
			 */
			byte[] decryptedContentBytes = this.cryptoService.decrypt(encryptedContentBytes);
			
			String decryptedContent = new String(decryptedContentBytes, "UTF8");
			
			assertThat(content, equalTo(decryptedContent));
		
		} catch(UnsupportedEncodingException uee) {
			uee.printStackTrace();
		}
	} // END testEncrpytDecrypt_1 Method
	
	
	
	@Test
	public void testEncrpytDecrypt_EmptyString() {
		
		try {
			
			String content = "";
			
			/*
			 * Method under test
			 */
			byte[] encryptedContentBytes = this.cryptoService.encrypt(content);
			
			/*
			 * Method under test
			 */
			byte[] decryptedContentBytes = this.cryptoService.decrypt(encryptedContentBytes);
			
			String decryptedContent = new String(decryptedContentBytes, "UTF8");
			
			assertThat(content, equalTo(decryptedContent));
		
		} catch(UnsupportedEncodingException uee) {
			uee.printStackTrace();
		}
	}	
	
	
	@Test
	public void testEncrpytDecrypt_AllWhiteSpaceString() {
		
		try {
			
			String content = "    ";
			
			/*
			 * Method under test
			 */
			byte[] encryptedContentBytes = this.cryptoService.encrypt(content);
			
			/*
			 * Method under test
			 */
			byte[] decryptedContentBytes = this.cryptoService.decrypt(encryptedContentBytes);
			
			String decryptedContent = new String(decryptedContentBytes, "UTF8");
			
			assertThat(content, equalTo(decryptedContent));
		
		} catch(UnsupportedEncodingException uee) {
			uee.printStackTrace();
		}
	}	
	
	
	public void testEncrypt_String_Null_Parameter() {
		
		String content = null;
		boolean caught = false;
		/*
		 * Method under test
		 */
		try {
			this.cryptoService.encrypt(content);
		} catch (IllegalArgumentException iae) {
			caught = true;
		}
		
		assertTrue(caught);
	}

	

	
	public void testEncrypt_bytes_Null_Parameter() {
		
		byte[] contentBytes = null;
		boolean caught = false;
		/*
		 * Method under test
		 */
		try {
			this.cryptoService.encrypt(contentBytes);
		} catch (IllegalArgumentException iae) {
			caught = true;
		}
		
		assertTrue(caught);
	}
	
	
	public void testDecrypt_bytes_Null_Parameter() {
		
		byte[] contentBytes = null;
		boolean caught = false;
		/*
		 * Method under test
		 */
		try {
			this.cryptoService.decrypt(contentBytes);
		} catch (IllegalArgumentException iae) {
			caught = true;
		}
		
		assertTrue(caught);
	}

} // END PKCS8CryptoServiceTest Class
