package com.ctvit.framework.core.entity;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name="SOA_USER", comment="用户信息")
public class SoaUser {

	@Column(name="USER_NAME", type = "Varchar")
	public String userName;
	
	@Column(name="USER_NAME", type = "Varchar")
	public String password;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
