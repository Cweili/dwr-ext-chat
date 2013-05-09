package org.cweili.webchat.util;

import org.directwebremoting.event.ScriptSessionEvent;
import org.directwebremoting.event.ScriptSessionListener;
import org.directwebremoting.impl.DefaultScriptSessionManager;

/**
 * 自己实现 ScriptSessionManager 监听 ScriptSession
 * 
 * @author Cweili
 * @version 2013-5-8 上午11:07:13
 * 
 */
public class ScriptSessionManager extends DefaultScriptSessionManager {

	public ScriptSessionManager() {
		super();
		addScriptSessionListener(new ScriptSessionListener() {

			@Override
			public void sessionDestroyed(ScriptSessionEvent ev) {
				Global.chat.logout((String) ev.getSession().getAttribute(Global.USERNAME));
			}

			@Override
			public void sessionCreated(ScriptSessionEvent ev) {
			}
		});
	}

}
