package org.cweili.webchat.service;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Cweili
 * @version 2013-5-3 上午10:24:55
 * 
 */
@RemoteProxy(creator = SpringCreator.class)
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "prototype")
public class Test {

	private int staticInt = 0;

	@RemoteMethod
	public String getUserName(int id) {
		staticInt += id;
		System.out.println("user id is " + staticInt);
		return "UserName: " + staticInt;
	}
}
