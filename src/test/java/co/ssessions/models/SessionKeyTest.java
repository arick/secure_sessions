package co.ssessions.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;

import junit.framework.TestCase;

public class SessionKeyTest extends TestCase {

	@Before
	protected void setUp() throws Exception {
		super.setUp();
	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Ensure that the empty constructor produces the correct default values
	 */
	@Test
	public void test_EmptyConstructor() {
		
		/*
		 * Method under test
		 */
		SessionKey sessionKey = new SessionKey();
		
		
		assertThat(sessionKey.getApplicationId(), equalTo("UNKNOWN"));
		assertThat(sessionKey.getSessionId(), equalTo(null));
	}
	
	
	@Test
	public void test_SessionId_Constructor() {
		
		/*
		 * Fixtures
		 */
		String sessionId = "13A_#$$%2";
		
		
		/*
		 * Method under test
		 */
		SessionKey sessionKey = new SessionKey(sessionId);
		
		
		assertThat(sessionKey.getApplicationId(), equalTo("UNKNOWN"));
		assertThat(sessionKey.getSessionId(), equalTo(sessionId));
	}
	
	
	@Test
	public void test_AppId_SessionId_Constructor() {
		
		/*
		 * Fixtures
		 */
		String applicationId = "Test_Application_Id";
		String sessionId = "13A_#$$%2";
		
		
		/*
		 * Method under test
		 */
		SessionKey sessionKey = new SessionKey(applicationId, sessionId);
		
		
		assertThat(sessionKey.getApplicationId(), equalTo(applicationId));
		assertThat(sessionKey.getSessionId(), equalTo(sessionId));
	}
	
	
	@Test
	public void test_toString_Valid() {
		
		/*
		 * Fixtures
		 */
		String applicationId = "Test_Application_Id";
		String sessionId = "13A_#$$%2";
		SessionKey sessionKey = new SessionKey(applicationId, sessionId);
		
		
		/*
		 * Method under test
		 */
		String result = sessionKey.toString();
		
		
		assertThat(result, equalTo("Test_Application_Id~~13A_#$$%2"));
	}
	
	
	@Test
	public void test_fromString_Valid() {
		
		/*
		 * Fixtures
		 */
		SessionKey sessionKey = new SessionKey();
		String sessionKeyString = "Test_Application_Id~~13A_#$$%2";
		
		
		/*
		 * Method under test
		 */
		SessionKey result = sessionKey.fromString(sessionKeyString);
		
		
		assertThat(result.getApplicationId(), equalTo("Test_Application_Id"));
		assertThat(result.getSessionId(), equalTo("13A_#$$%2"));
	}
	

	
	
}
