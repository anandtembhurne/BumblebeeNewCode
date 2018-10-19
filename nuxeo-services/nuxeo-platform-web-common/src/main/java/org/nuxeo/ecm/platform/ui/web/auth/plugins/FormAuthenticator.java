/*
 * (C) Copyright 2006-2007 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id: JOOoConvertPluginImpl.java 18651 2007-05-13 20:28:53Z sfermigier $
 */

package org.nuxeo.ecm.platform.ui.web.auth.plugins;

import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.ERROR_CONNECTION_FAILED;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.ERROR_USERNAME_MISSING;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.FORM_SUBMITTED_MARKER;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.LOGIN_CONNECTION_FAILED;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.LOGIN_ERROR;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.LOGIN_FAILED;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.LOGIN_MISSING;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.PASSWORD_KEY;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.REQUESTED_URL;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.SESSION_TIMEOUT;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.START_PAGE_SAVE_KEY;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.USERNAME_KEY;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.ALREADY_LOGGEDIN;

import java.io.IOException;
import java.security.Key;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.misc.*;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.URIUtils;
import org.nuxeo.ecm.platform.api.login.UserIdentificationInfo;
import org.nuxeo.ecm.platform.ui.web.auth.LoginScreenHelper;
import org.nuxeo.ecm.platform.ui.web.auth.interfaces.NuxeoAuthenticationPlugin;

public class FormAuthenticator implements NuxeoAuthenticationPlugin {

	private static final Log log = LogFactory.getLog(FormAuthenticator.class);

	protected String loginPage = "login.jsp";

	protected String usernameKey = USERNAME_KEY;

	protected String passwordKey = PASSWORD_KEY;

	private static final String ALGO = "AES";

	private static final byte[] keyValue = new byte[] { 'T', 'h', 'e', 'B',
			'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };

	protected String getLoginPage() {
		return loginPage;
	}

	@Override
	public Boolean handleLoginPrompt(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, String baseURL) {
		try {
			log.debug("Forward to Login Screen");
			Map<String, String> parameters = new HashMap<String, String>();
			String redirectUrl = baseURL + getLoginPage();
			@SuppressWarnings("unchecked")
			Enumeration<String> paramNames = httpRequest.getParameterNames();
			while (paramNames.hasMoreElements()) {
				String name = paramNames.nextElement();
				String value = httpRequest.getParameter(name);
				parameters.put(name, value);
			}
			HttpSession session = httpRequest.getSession(false);
			String requestedUrl = null;
			boolean isTimeout = false;
			if (session != null) {
				requestedUrl = (String) session
						.getAttribute(START_PAGE_SAVE_KEY);
				Object obj = session.getAttribute(SESSION_TIMEOUT);
				if (obj != null) {
					isTimeout = (Boolean) obj;
				}
			}
			if (requestedUrl != null && !requestedUrl.equals("")) {
				parameters.put(REQUESTED_URL, requestedUrl);
			}
			String loginError = (String) httpRequest.getAttribute(LOGIN_ERROR);
			if (loginError != null) {
				if(ALREADY_LOGGEDIN.equals(loginError)){// code change added new 'if' for already login user msg
					parameters.put(ALREADY_LOGGEDIN, "true");
				}else if (ERROR_USERNAME_MISSING.equals(loginError)) {
					parameters.put(LOGIN_MISSING, "true");
				} else if (ERROR_CONNECTION_FAILED.equals(loginError)) {
					parameters.put(LOGIN_CONNECTION_FAILED, "true");
					parameters.put(LOGIN_FAILED, "true"); // compat
				} else {
					parameters.put(LOGIN_FAILED, "true");
				}
			}
			if (isTimeout) {
				parameters.put(SESSION_TIMEOUT, "true");
			}

			// avoid resending the password in clear !!!
			parameters.remove(passwordKey);
			redirectUrl = URIUtils.addParametersToURIQuery(redirectUrl,
					parameters);
			httpResponse.sendRedirect(redirectUrl);
		} catch (IOException e) {
			log.error(e, e);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	@Override
	public UserIdentificationInfo handleRetrieveIdentity(
			HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		// Only accept POST requests
		String method = httpRequest.getMethod();
		if (!"POST".equals(method)) {
			log.debug("Request method is " + method + ", only accepting POST");
			return null;
		}
		log.debug("Looking for user/password in the request");
		String userName = httpRequest.getParameter(usernameKey);
		String password = decrypt(httpRequest);//httpRequest.getParameter(passwordKey);
		// NXP-2650: ugly hack to check if form was submitted
		if (httpRequest.getParameter(FORM_SUBMITTED_MARKER) != null
				&& (userName == null || userName.length() == 0)) {
			httpRequest.setAttribute(LOGIN_ERROR, ERROR_USERNAME_MISSING);
		}
		if (userName == null || userName.length() == 0) {
			return null;
		}
		return new UserIdentificationInfo(userName, password);
	}

	@Override
	public Boolean needLoginPrompt(HttpServletRequest httpRequest) {
		return Boolean.TRUE;
	}

	@Override
	public void initPlugin(Map<String, String> parameters) {
		if (parameters.get("LoginPage") != null) {
			loginPage = parameters.get("LoginPage");
		}
		if (parameters.get("UsernameKey") != null) {
			usernameKey = parameters.get("UsernameKey");
		}
		if (parameters.get("PasswordKey") != null) {
			passwordKey = parameters.get("PasswordKey");
		}
	}

	@Override
	public List<String> getUnAuthenticatedURLPrefix() {
		// Login Page is unauthenticated !
		List<String> prefix = new ArrayList<String>();
		prefix.add(getLoginPage());
		return prefix;
	}
	// method added for decryption
	private String decrypt(HttpServletRequest httpRequest)
			 {
		try {
			String salt = httpRequest.getParameter("salt");
			String iv = httpRequest.getParameter("iv");
			String encPassword =httpRequest.getParameter(passwordKey);
			String key= (String) httpRequest.getSession().getAttribute("key");
			int len = salt.length();
			byte[] saltBytes = new byte[len / 2];
			for (int i = 0; i < len; i += 2) {
				saltBytes[i / 2] = (byte) ((Character.digit(salt.charAt(i), 16) << 4) + Character
						.digit(salt.charAt(i + 1), 16));
			}
			
			len = iv.length();
			byte[] ivBytes = new byte[len / 2];
			for (int i = 0; i < len; i += 2) {
				ivBytes[i / 2] = (byte) ((Character.digit(iv.charAt(i), 16) << 4) + Character
						.digit(iv.charAt(i + 1), 16));
			}

			IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
			
			KeySpec keySpec = new PBEKeySpec(key.toCharArray(),saltBytes, 100, 128);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			SecretKey secretKey = keyFactory.generateSecret(keySpec);
			SecretKeySpec sKey = new SecretKeySpec(secretKey.getEncoded(), "AES");
			
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, sKey, ivParameterSpec);
			byte[] decordedValue = new sun.misc.BASE64Decoder().decodeBuffer(encPassword);
			byte[] decValue = c.doFinal(decordedValue);
			String decryptedValue = new String(decValue);	
			return decryptedValue;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	// method added for decryption	
}
