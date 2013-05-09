package org.cweili.webchat.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.cweili.webchat.domain.User;
import org.cweili.webchat.util.Global;
import org.directwebremoting.Browser;
import org.directwebremoting.ScriptSessions;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.stereotype.Service;

/**
 * Chat 服务
 * 
 * @author Cweili
 * @version 2013-5-6 下午12:40:38
 * 
 */
@RemoteProxy(creator = SpringCreator.class)
@Service
public class Chat {

	/**
	 * 注册全局唯一的 Chat
	 */
	public Chat() {
		Global.chat = this;
	}

	/**
	 * 发送消息
	 * 
	 * @param message
	 *            消息
	 * @return 是否发送成功
	 */
	public String sendMessage(final String message) {
		final String username;
		try {
			username = (String) WebContextFactory.get().getScriptSession()
					.getAttribute(Global.USERNAME);
		} catch (Exception e) {
			return Global.ERROR;
		}

		if (null == username) {
			return Global.ERROR;
		}

		final String time = time();
		Browser.withCurrentPage(new Runnable() {
			@Override
			public void run() {
				ScriptSessions.addFunctionCall("addMessage", username, time, message);
			}
		});

		return Global.SUCCESS;
	}

	/**
	 * 获取在线列表
	 * 
	 * @return 在线列表
	 */
	public static Set<User> getOnlineSet() {
		return Global.onlineSet;
	}

	/**
	 * 向所有用户推送在线列表更新
	 */
	public void updateOnlineList() {
		Browser.withCurrentPage(new Runnable() {
			@Override
			public void run() {
				ScriptSessions.addFunctionCall("updateOnlineList", Global.onlineSet);
			}
		});
	}

	/**
	 * 用户登录
	 * 
	 * @param username
	 *            用户名
	 * @return 是否登录成功
	 */
	public String login(final String username) {
		if (Global.onlineSet.contains(new User(username)) || "".equals(username)) {
			return Global.ERROR;
		} else {
			Global.onlineSet.add(new User(username, time()));
			updateOnlineList();
			WebContextFactory.get().getScriptSession().setAttribute(Global.USERNAME, username);
			return Global.SUCCESS;
		}
	}

	/**
	 * 用户登出
	 * 
	 * @param username
	 *            用户名
	 * @return 是否登出成功
	 */
	public String logout(final String username) {
		if (!Global.onlineSet.contains(new User(username))) {
			return Global.ERROR;
		} else {
			Global.onlineSet.remove(new User(username));
			updateOnlineList();
			WebContextFactory.get().getScriptSession().invalidate();
			return Global.SUCCESS;
		}
	}

	/**
	 * 获取时间字符串
	 * 
	 * @return 时间字符串
	 */
	private String time() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

}
