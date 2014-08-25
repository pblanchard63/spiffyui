/*******************************************************************************
 * 
 * Copyright 2011-2014 Spiffy UI Team   
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
 ******************************************************************************/
package org.spiffyui.client.login;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

import org.spiffyui.client.JSUtil;
import org.spiffyui.client.MessageUtil;
import org.spiffyui.client.rest.AuthUtil;
import org.spiffyui.client.rest.RESTAuthProvider;
import org.spiffyui.client.rest.RESTException;
import org.spiffyui.client.rest.RESTObjectCallBack;
import org.spiffyui.client.rest.RESTility;
import org.spiffyui.client.widgets.SmallLoadingIndicator;

/**
 * <p>
 * A reusable login dialog.
 * </p>
 *
 * <p>
 * The styles (css classes) that the panel uses are as follows:
 * </p>
 * 
 * <ul>
 * <li>.logintitle - For the title of the login dialog
 * <li>.login_username - For the username label
 * <li>.login_username_txt - For the username text field
 * <li>.login_password - For the password label
 * <li>.login_password_txt - For the password text box
 * <li>.login_submit - For the submit button on the login form
 * </ul>
 *
 */
public class LoginPanel extends Composite implements KeyUpHandler
{
    private RESTAuthProvider m_authUtil = RESTility.getAuthProvider();

    private static LoginPanel g_loginPanel;

    private HTML m_message;
    private TextBox m_username;
    private TextBox m_pwd;
    private Button m_submit;
    private FormPanel m_fp;

    private SimplePanel m_glassPanel;
    private SmallLoadingIndicator m_loading;

    private Object m_callbackKey;
    private String m_tokenServerUrl;

    private HTMLPanel m_panel;

    private boolean m_inRequest = false;
    private boolean m_isRepeat = false;

    private Anchor m_logout;

    private LoginStringHelper m_helper;

    /**
     * Show the login panel.  This should only be called from AuthUtil
     *
     * @param helper    the string helper for this login panel
     * @param title     the title of this login dialog
     * @param callbackKey
     *                 the callback key used to replay the REST call that caused the need for login
     * @param tokenServerUrl
     *                 the url for the token server the user is logging in to
     * @param code     the error code
     * @param isRepeat true if this login request is a repeat because of token time out, false otherwise
     * @param username the username of the currently logged in user.  This parameter is optional.
     */
    public static void showLoginPanel(LoginStringHelper helper, String title, Object callbackKey, String tokenServerUrl,
                                      String code, boolean isRepeat, String username)
    {
        if (g_loginPanel == null) {
            g_loginPanel = new LoginPanel(title, helper);
        }

        g_loginPanel.setIsRepeat(isRepeat);

        g_loginPanel.setCallbackKey(callbackKey);
        g_loginPanel.setTokenServerUrl(tokenServerUrl);

        if (username != null) {
            g_loginPanel.m_username.setText(username);
        }

        g_loginPanel.show();


        if (code != null && AuthUtil.NO_PRIVILEGE.equals(code)) {
            /*
             * This error indicates the username and password is
             * valid, but the user doesn't have permission to access
             * the application.
             */
            MessageUtil.showWarning(g_loginPanel.m_helper.getString(LoginStrings.NO_PRIVILEGE, 
                                                                    g_loginPanel.m_username.getText()), false);
        }

    }

    /**
      * Check if this is login case or renew token case (after token has timed out) and show the login dialog differently
      * @param isRepeat boolean indicating whether this is login or renew token case
     */
    public void setIsRepeat(boolean isRepeat)
    {
        m_isRepeat = isRepeat;

        if (isRepeat) {
            m_glassPanel.getElement().addClassName("loginRepeatGlass");
            m_fp.getElement().addClassName("loginRepeat");
            m_panel.getElementById("login_titlespan").setInnerText(m_helper.getString(LoginStrings.RENEW));
            if (m_username.getText() != null &&
                m_username.getText().length() > 0) {
                /*
                 * In the case of SSO or page refreshing we won't have
                 * the user name so we have to prompt for it.
                 */
                JSUtil.hide("login_username_row", "fast");
                m_panel.getElementById("loginmessage").setInnerText(m_helper.getString(LoginStrings.REPEAT_LOGIN));
            } else {
                JSUtil.show("login_username_row", "fast");
                m_panel.getElementById("loginmessage").setInnerText(m_helper.getString(LoginStrings.REPEAT_LOGIN_TWO));
            }
        } else {
            if (JSUtil.isVisible("#mainWrap")) {
                JSUtil.hide("#mainWrap", "fast");
            }
            m_glassPanel.getElement().removeClassName("loginRepeatGlass");
            m_fp.getElement().removeClassName("loginRepeat");
            m_panel.getElementById("login_titlespan").setInnerText(m_helper.getString(LoginStrings.LOGIN_TITLE));
            m_panel.getElementById("loginmessage").setInnerText("");
            JSUtil.show("login_username_row", "fast");
        }
    }

    /**
     * Create a new LoginPanel.
     * @param title  title of the login panel
     * @param helper string helper for getting strings used in login panel
     */
    protected LoginPanel(String title, LoginStringHelper helper)
    {
        m_helper = helper;

        m_glassPanel = new SimplePanel();

        if (RootPanel.get("loginPanel") != null) {
            RootPanel.get("loginPanel").add(m_glassPanel);
        } else {
            throw new IllegalStateException("Unable to locate the loginPanel element.  You must import spiffyui.min.js before using the LoginPanel.");
        }
        
        
        m_glassPanel.setVisible(false);
        m_glassPanel.getElement().setId("login_glass_pane");

        SimplePanel dialog = new SimplePanel();
        dialog.getElement().setId("login_form_panel");

        m_fp = new FormPanel();
        m_fp.getElement().setId("login_form");
        dialog.setWidget(m_fp);

        String html =
            "<div id=\"loginHeaderContainer\">" +
                "<div id=\"loginHeaderleft\">" +
                    "<div id=\"loginHeaderlogo\"> </div>" +
                    "<span class=\"headertitle\">" + m_helper.getString(LoginStrings.PRODUCT_NAME) + "</span>" +
                "</div>" +
            "</div>" +
            "<div class=\"login_content\">" +
                "<div style=\"display: block;\" id=\"contentDetail\">" +
                "<div id=\"login_titlediv\" class=\"logintitle\"><span id=\"login_titlespan\">" + title + "</span></div>" +
                    "<div id=\"loginDetailsId\" class=\"loginDetail\">" +
                        "<div id=\"loginmessage\"></div>" +
                        "<div id=\"loginFieldsContainer\" style=\"display: block;\">" +
                                "<div id=\"login_username_row\">" +
                                    "<label>" + m_helper.getString(LoginStrings.USERNAME) + "</label>" +
                                    "<div id=\"login_username\"></div>" +
                                "</div>" +
                                "<div id=\"login_password_row\">" +
                                    "<label>" + m_helper.getString(LoginStrings.PASSWORD) + "</label>" +
                                    "<div id=\"login_password\"></div>" +
                                "</div>" +
                                "<div id=\"gwtsubmit\"></div>" +
                        "</div>" +
                        "<div id=\"loginconfig\"></div>" +
                    "</div>" +
                "</div>" +
            "</div>";

        // Create a panel to hold all of the form widgets.
        m_panel = new HTMLPanel(html);

        m_fp.setWidget(m_panel);

        m_message = new HTML();
        m_panel.add(m_message, "loginmessage");

        //username
        m_username = new TextBox();
        m_username.setName("login_panel_user");
        m_username.addKeyUpHandler(this);
        m_username.getElement().setId("login_username_txt");
        m_panel.add(m_username, "login_username");

        // password
        m_pwd = new PasswordTextBox();
        m_pwd.setName("login_panel_pwd");
        m_pwd.addKeyUpHandler(this);
        m_pwd.getElement().setId("login_password_txt");
        m_panel.add(m_pwd, "login_password");

        // login button
        m_submit = new Button(m_helper.getString(LoginStrings.LOGIN), new ClickHandler() {
                public void onClick(ClickEvent event)
                {
                    event.preventDefault();
                    doRequest();
                }
        });

        m_submit.getElement().setId("login_submit_button");
        m_panel.add(m_submit, "gwtsubmit");

        m_logout = new Anchor(m_helper.getString(LoginStrings.LOGOUT));
        m_logout.getElement().setId("loging_logout_link");
        m_logout.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event)
                {
                    event.preventDefault();
                    /*
                     * At this point we are already logged out, so we
                     * just need to reload the window.
                     */
                    JSUtil.reload();
                }
            });
        m_logout.setVisible(false);
        m_panel.add(m_logout, "gwtsubmit");

        m_loading = new SmallLoadingIndicator();
        m_loading.setVisible(false);
        m_panel.add(m_loading, "gwtsubmit");

        RootPanel.get("loginPanel").add(m_fp);
        initWidget(dialog);

        enableButton();
    }

    public void setCallbackKey(Object key)
    {
        m_callbackKey = key;
    }

    public void setTokenServerUrl(String url)
    {
        m_tokenServerUrl = url;
    }

    private void enableButton()
    {
        m_submit.setEnabled(m_username.getText().length() > 0 &&
                            m_pwd.getText().length() > 0);
    }

    /**
     * perform login request
     */
    public void doRequest()
    {
        m_loading.setVisible(true);
        m_inRequest = true;

        /*
         * We want to reset the password field when they log in
         * in case it is wrong and to clear it from the page.
         */
        String pwd = m_pwd.getText().trim();

        m_authUtil.login(m_username.getText().trim(), pwd, m_tokenServerUrl,
                       new RESTObjectCallBack<String>() {
            @Override
            public void success(String userToken)
            {
                m_inRequest = false;
                m_loading.setVisible(false);
                m_pwd.setText("");

                hide();
                m_authUtil.finishRESTCall(m_callbackKey);
            }

            @Override
            public void error(String message)
            {
                m_pwd.setText("");
                m_inRequest = false;
                m_loading.setVisible(false);
            }

            @Override
            public void error(RESTException e)
            {
                handleAuthError(e);
            }
        });
    }
    
    private void handleAuthError(RESTException e)
    {
        m_pwd.setText("");
        m_submit.setEnabled(false);
        m_inRequest = false;

        if (AuthUtil.INVALID_TS_URL.equals(e.getCode())) {
            MessageUtil.showError(m_helper.getString(LoginStrings.INVALID_TS_URL, e.getReason()));
        } else if (AuthUtil.NOTFOUND_TS_URL.equals(e.getCode())) {
            MessageUtil.showError(m_helper.getString(LoginStrings.NOT_FOUND_TS_URL, e.getUrl()));
        } else if (AuthUtil.MULTIPLE_ACCOUNTS.equals(e.getCode())) {
            MessageUtil.showWarning(m_helper.getString(LoginStrings.MULTIPLE_ACCOUNTS), false);
        } else if (AuthUtil.INVALID_INPUT.equals(e.getSubcode())) {
            /*
             * This is a very common error.  It means the username
             * and password were incorrect.
             */
            MessageUtil.showWarning(m_helper.getString(LoginStrings.INVALID_USERNAME_PASSWORD), false);
        } else {
            MessageUtil.showError(e.getReason());
        }
        m_loading.setVisible(false);
    }

    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        m_glassPanel.setVisible(visible);
        m_fp.setVisible(visible);
    }

    /**
     * show the login panel
     */
    public void show()
    {
        JSUtil.hide("mainWrap", "fast");
        m_glassPanel.setVisible(true);
        m_fp.setVisible(true);

        m_logout.setVisible(m_isRepeat);

        if (m_isRepeat &&
            m_username.getText() != null &&
            m_username.getText().length() > 0) {
            m_pwd.setFocus(true);
        } else {
            m_username.setFocus(true);
        }

        setMessage("");
    }

    private void hide()
    {
        m_glassPanel.setVisible(false);
        m_fp.setVisible(false);
        JSUtil.show("mainWrap");

        /*
         * JQuery sets the overflow property to hidden after running the show animation.
         * I think it does this to make clean up any overflowing elements based on the 
         * animation effect.  Firefox and Chrome have no problem with this, but IE doesn't
         * resize the element properly and it causes the main element to get cut off just
         * below the header.  We are seting the overflow style back so everything is
         * displayed properly.
         */
        DOM.getElementById("mainWrap").getStyle().setOverflow(Style.Overflow.VISIBLE);
    }

    @Override
    public void onKeyUp(KeyUpEvent event)
    {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            /*
             * Special handling to make sure the user can press enter instead
             * of having to click the login button.
             */
            m_submit.click();
        } else {
            enableButton();
        }
    }

    private void setMessage(String message)
    {
        m_message.setHTML(message);
    }

    public TextBox getUsername()
    {
        return m_username;
    }

    public TextBox getPwd()
    {
        return m_pwd;
    }

    public boolean isInRequest()
    {
        return m_inRequest;
    }

    public void setInRequest(boolean inRequest)
    {
        this.m_inRequest = inRequest;
    }

    public boolean isRepeat()
    {
        return m_isRepeat;
    }

    public LoginStringHelper getHelper()
    {
        return m_helper;
    }

    public static LoginPanel getLoginPanel()
    {
        return g_loginPanel;
    }

    public static void setLoginPanel(LoginPanel loginPanel)
    {
        g_loginPanel = loginPanel;
    }


}
