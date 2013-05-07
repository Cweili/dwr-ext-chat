package org.cweili.webchat.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.cweili.webchat.domain.User;
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

	private static Set<User> onlineSet;

	public Chat() {
		onlineSet = new LinkedHashSet<User>();
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

		final String time = time();
		Browser.withCurrentPage(new Runnable() {
			@Override
			public void run() {
				ScriptSessions.addFunctionCall("addMessage", username, time, message);
			}
		});

		return SUCCESS;
	}

	public static Set<User> getOnlineSet() {
		return onlineSet;
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
		if (onlineSet.contains(new User(username)) || "".equals(username)) {
			return ERROR;
		} else {
			onlineSet.add(new User(username, time()));
			updateOnlineList();
			WebContextFactory.get().getScriptSession().setAttribute(USERNAME, username);
			return SUCCESS;
		}
	}

	public String logout(final String username) {
		if (!onlineSet.contains(new User(username))) {
			return ERROR;
		} else {
			onlineSet.remove(new User(username));
			updateOnlineList();
			WebContextFactory.get().getScriptSession().invalidate();
			return SUCCESS;
		}
	}

	private String time() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
}
