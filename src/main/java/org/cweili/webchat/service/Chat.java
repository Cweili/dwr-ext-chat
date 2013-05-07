package org.cweili.webchat.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.cweili.webchat.domain.User;
import org.cweili.webchat.util.StaticVariable;
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

	public Chat() {
		StaticVariable.chat = this;
	}

	public String sendMessage(final String message) {
		final String username;
		try {
			username = (String) WebContextFactory.get().getScriptSession()
					.getAttribute(StaticVariable.USERNAME);
		} catch (Exception e) {
			return StaticVariable.ERROR;
		}

		if (null == username) {
			return StaticVariable.ERROR;
		}

		final String time = time();
		Browser.withCurrentPage(new Runnable() {
			@Override
			public void run() {
				ScriptSessions.addFunctionCall("addMessage", username, time, message);
			}
		});

		return StaticVariable.SUCCESS;
	}

	public static Set<User> getOnlineSet() {
		return StaticVariable.onlineSet;
	}

	public void updateOnlineList() {
		Browser.withCurrentPage(new Runnable() {
			@Override
			public void run() {
				ScriptSessions.addFunctionCall("updateOnlineList", StaticVariable.onlineSet);
			}
		});
	}

	public String login(final String username) {
		if (StaticVariable.onlineSet.contains(new User(username)) || "".equals(username)) {
			return StaticVariable.ERROR;
		} else {
			StaticVariable.onlineSet.add(new User(username, time()));
			updateOnlineList();
			WebContextFactory.get().getScriptSession()
					.setAttribute(StaticVariable.USERNAME, username);
			return StaticVariable.SUCCESS;
		}
	}

	public String logout(final String username) {
		if (!StaticVariable.onlineSet.contains(new User(username))) {
			return StaticVariable.ERROR;
		} else {
			StaticVariable.onlineSet.remove(new User(username));
			updateOnlineList();
			WebContextFactory.get().getScriptSession().invalidate();
			return StaticVariable.SUCCESS;
		}
	}

	private String time() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
}
