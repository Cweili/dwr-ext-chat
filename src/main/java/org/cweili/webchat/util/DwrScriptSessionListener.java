package org.cweili.webchat.util;

import org.directwebremoting.event.ScriptSessionEvent;
import org.directwebremoting.event.ScriptSessionListener;

/**
 * 
 * @author Cweili
 * @version 2013-5-7 下午5:15:38
 * 
 */
public class DwrScriptSessionListener implements ScriptSessionListener {

	@Override
	public void sessionDestroyed(ScriptSessionEvent event) {
		StaticVariable.chat.logout((String) event.getSession()
				.getAttribute(StaticVariable.USERNAME));
	}

	@Override
	public void sessionCreated(ScriptSessionEvent event) {

	}

}
