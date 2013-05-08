package org.cweili.webchat.util;

import org.directwebremoting.event.ScriptSessionEvent;
import org.directwebremoting.event.ScriptSessionListener;
import org.directwebremoting.impl.DefaultScriptSessionManager;

/**
 * 
 * @author Cweili
 * @version 2013-5-8 上午11:07:13
 * 
 */
public class DwrScriptSessionManager extends DefaultScriptSessionManager {

	public DwrScriptSessionManager() {
		super();
		addScriptSessionListener(new ScriptSessionListener() {

			@Override
			public void sessionDestroyed(ScriptSessionEvent ev) {
				Global.chat.logout((String) ev.getSession().getAttribute(
						Global.USERNAME));
			}

			@Override
			public void sessionCreated(ScriptSessionEvent ev) {
			}
		});
	}

}
