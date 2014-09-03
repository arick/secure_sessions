package co.ssessions.models;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import co.ssessions.json.DateJsonDeserializer;
import co.ssessions.json.DateJsonSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import junit.framework.TestCase;

public class SessionModelTest extends TestCase {


	@Test
	public void test_GSON_Generation() {
		
		Date createDate = new Date();
		Date updateDate = new Date(createDate.getTime() + 3);
		SessionModel sessionModel = new SessionModel();
		sessionModel.setCreateTime(createDate);
		sessionModel.setUpdateTime(updateDate);
		String privateKeyId = "This_Is_Private_Key_ID";
		sessionModel.setPrivateKeyId(privateKeyId);
		String dataEncoding = "BASE64_ENCODING";
		sessionModel.setDataEncoding(dataEncoding);
		String dataContent = "This is the data for the session model.";
		sessionModel.setData(dataContent);
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateJsonSerializer()).create();
		/*
		 * Method under test.
		 */
		String sessionModelJson = gson.toJson(sessionModel);
		
		
		assertNotNull(sessionModelJson);
		
		assertThat(sessionModelJson, containsString("\"privateKeyId\":\""+ privateKeyId +"\""));
		assertThat(sessionModelJson, containsString("\"dataEncoding\":\""+ dataEncoding +"\""));
		assertThat(sessionModelJson, containsString("\"data\":\""+ dataContent +"\""));
		assertThat(sessionModelJson, containsString("\"createTime\":"+ createDate.getTime()));
		assertThat(sessionModelJson, containsString("\"updateTime\":"+ updateDate.getTime()));
		
	}
	
	
	
	@Test
	public void test_SessionModel_from_GSON() {
		
		String sessionModelJson = "{\"privateKeyId\":\"This_Is_Private_Key_ID\",\"data\":\"This is the data for the session model.\",\"dataEncoding\":\"BASE64_ENCODING\",\"createTime\":1409661100243,\"updateTime\":1409661100244}";
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateJsonDeserializer()).create();
		/*
		 * Method under test.
		 */
		SessionModel sessionModel = (SessionModel) gson.fromJson(sessionModelJson, SessionModel.class); 
		
		
		assertNotNull(sessionModelJson);
		
		assertThat(sessionModel.getPrivateKeyId(), equalTo("This_Is_Private_Key_ID"));
		assertThat(sessionModel.getDataEncoding(), equalTo("BASE64_ENCODING"));
		assertThat(sessionModel.getData(), equalTo("This is the data for the session model."));
		assertThat(sessionModel.getCreateTime(), equalTo(new Date(1409661100243L)));
		assertThat(sessionModel.getUpdateTime(), equalTo(new Date(1409661100244L)));
	}
	
	
}
