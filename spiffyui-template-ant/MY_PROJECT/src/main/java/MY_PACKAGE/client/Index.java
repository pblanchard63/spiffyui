/*******************************************************************************
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
package MY_PACKAGE.client;

import org.spiffyui.client.JSONUtil;
import org.spiffyui.client.MainFooter;
import org.spiffyui.client.MainHeader;
import org.spiffyui.client.MessageUtil;
import org.spiffyui.client.rest.RESTCallback;
import org.spiffyui.client.rest.RESTException;
import org.spiffyui.client.rest.RESTility;
import org.spiffyui.client.widgets.LongMessage;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;


/**
 * This class is the main entry point for our GWT module.
 */
public class Index implements EntryPoint, ClickHandler, KeyPressHandler 
{

    private static Index g_index;
    private TextBox m_text = new TextBox();
    private LongMessage m_longMessage = new LongMessage("longMsg");

    /**
     * The Index page constructor
     */
    public Index()
    {
        g_index = this;
    }


    @Override
    public void onModuleLoad()
    {
        MainHeader header = new MainHeader();
        header.setHeaderTitle("Hello Spiffy MY_PROJECT!");
        
        MainFooter footer = new MainFooter();
        footer.setFooterString("MY_PROJECT is a <a href=\"http://www.spiffyui.org\">Spiffy UI Framework</a> application");
        
        FlowPanel panel = new FlowPanel()
        {
            @Override
            public void onLoad()
            {
                super.onLoad();
                /*
                 Let's set focus into the text field when the page first loads
                 */
                m_text.setFocus(true);
            }
        };
        
        panel.add(m_longMessage);
        final InlineLabel label = new InlineLabel("What's your name? ");
        label.setHeight("1em");
        panel.add(label);
        panel.add(m_text);
        final Button button = new Button("Submit");
        panel.add(button);
        
        final HTML html = new HTML("<br><br>"+
                                   "<p>This is a <a href=\"http://www.spiffyui.org\">Spiffy UI</a> application built " +
                                   "just for you and running on your computer.  This simple example shows you an easy form " +
                                   "which makes a <a href=\"http://www.spiffyui.org/#b=rest\">REST</a> call to your server.  " +
                                   "You can edit it, build it, run it, share it with your friends, and build a product on " +
                                   "top of it.  The application is all yours.</p>"+

                                   "<h2>Next steps</h2>"+
                                   
                                   "<p>You've played with the project a little, now make some changes.  </p>"+
                                   "<ol>"+
                                   "<li><b>Change the style</b> - You can set any style in <code>src/main/java/MY_PACKAGE/public/MY_PROJECT.css</code></li>"+
                                   "<li><b>Add a widget</b> - Spiffy UI has some great <a href=\"http://www.spiffyui.org/#b=widgets\">widgets</a></li>"+
                                   "<li><b>Add a navigation bar</b> - Easy navigation for your application is one of the great features of Spiffy UI</li>"+
                                   "</ol>");
        html.getElement().setId("spiffyIntroText");
        panel.add(html);
        
        button.addClickHandler(this);
        m_text.addKeyPressHandler(this);
        
        RootPanel.get("mainContent").add(panel);
        
        button.addClickHandler(this);
        m_text.addKeyPressHandler(this);
        
        RootPanel.get("mainContent").add(panel);
    }
    
    @Override
    public void onClick(ClickEvent event)
    {
        sendRequest();
    }

    @Override
    public void onKeyPress(KeyPressEvent event)
    {
        /*
         We want to submit the request if the user pressed enter
         */
        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
            sendRequest();
        }
    }
    
    /**
     * Send the REST request to the server and read the response back.
     */
    private void sendRequest()
    {
        String q = m_text.getValue().trim();
        if (q.equals("")) {
            MessageUtil.showWarning("Please enter your name in the text field.", false);
            return;
        }
        RESTility.callREST("simple/" + q, new RESTCallback() {
            
            @Override
            public void onSuccess(JSONValue val)
            {
                showSuccessMessage(val);
            }
            
            @Override
            public void onError(int statusCode, String errorResponse)
            {
                MessageUtil.showError("Error.  Status Code: " + statusCode + " " + errorResponse);
            }
            
            @Override
            public void onError(RESTException e)
            {
                MessageUtil.showError(e.getReason());
            }
        });
        
    }
    
    /**
     * Show the successful message result of our REST call.
     * 
     * @param val    the value containing the JSON response from the server
     */
    private void showSuccessMessage(JSONValue val)
    {
        JSONObject obj = val.isObject();
        String name = JSONUtil.getStringValue(obj, "user");
        String browser = JSONUtil.getStringValue(obj, "userAgent");
        String server = JSONUtil.getStringValue(obj, "serverInfo");
        
        String message = "Hello, " + name + "!  <br/>You are using " + browser + ".<br/>The server is " + server + ".";
        m_longMessage.setHTML(message);
    }
}
