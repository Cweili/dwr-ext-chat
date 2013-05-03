package org.cweili.webchat.service;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;

/**
 * 
 * @author Cweili
 * @version 2013-5-3 上午10:24:55
 * 
 */
@RemoteProxy
public class Test {

	@RemoteMethod
	public String getUserName(int id) {
		System.out.println("user id is " + id);
		return "UserName: " + id;
	}
}
