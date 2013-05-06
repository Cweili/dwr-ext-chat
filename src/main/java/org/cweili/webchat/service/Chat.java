package org.cweili.webchat.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.directwebremoting.Browser;
import org.directwebremoting.ScriptSessions;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Cweili
 * @version 2013-5-6 下午12:40:38
 * 
 */
@RemoteProxy(creator = SpringCreator.class)
@Service
public class Chat {

	static final String SUCCESS = "success";
	static final String ERROR = "error";
	static final String USERNAME = "username";

	private static Set<String> onlineSet;

	public Chat() {
		onlineSet = new LinkedHashSet<String>();
	}

	public String sendMessage(final String message) {
		final String username;
		try {
			username = (String) WebContextFactory.get().getScriptSession().getAttribute(USERNAME);
		} catch (Exception e) {
			return ERROR;
		}

		if (null == username) {
			return ERROR;
		}

		final String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Browser.withCurrentPage(new Runnable() {
			@Override
			public void run() {
				ScriptSessions.addFunctionCall("addMessage", username, time, message);
			}
		});

		return SUCCESS;
	}

	public void updateOnlineList() {
		Browser.withCurrentPage(new Runnable() {
			@Override
			public void run() {
				ScriptSessions.addFunctionCall("updateOnlineList", onlineSet);
			}
		});
	}

	public String login(final String username) {
		if (onlineSet.contains(username) || "".equals(username)) {
			return ERROR;
		} else {
			onlineSet.add(username);
			updateOnlineList();
			WebContextFactory.get().getScriptSession().setAttribute(USERNAME, username);
			return SUCCESS;
		}
	}

	public String logout(final String username) {
		if (!onlineSet.contains(username)) {
			return ERROR;
		} else {
			onlineSet.remove(username);
			updateOnlineList();
			WebContextFactory.get().getScriptSession().invalidate();
			return SUCCESS;
		}
	}
}
