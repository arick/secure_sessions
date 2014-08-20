package co.ssessions;

import java.util.Date;

public class SessionModel {
	
	public static String BASE64 = "BASE64";
	
	private String privateKeyId;
	private String data;
	private String dataEncoding;
	private Date createTime;
	private Date updateTime;
	
	
	public String getPrivateKeyId() {
		return privateKeyId;
	}
	
	
	public void setPrivateKeyId(String privateKeyId) {
		this.privateKeyId = privateKeyId;
	}
	
	
	public String getData() {
		return data;
	}
	
	
	public void setData(String data) {
		this.data = data;
	}
	
	
	public String getDataEncoding() {
		return dataEncoding;
	}
	
	
	public void setDataEncoding(String dataEncoding) {
		this.dataEncoding = dataEncoding;
	}
	
	
	public Date getCreateTime() {
		return createTime;
	}
	
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
	public Date getUpdateTime() {
		return updateTime;
	}
	
	
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	

} // END SessionModel Class
