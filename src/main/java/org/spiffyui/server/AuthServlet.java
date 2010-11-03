/*
 * Copyright (c) 2010 Unpublished Work of Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS AN UNPUBLISHED WORK AND CONTAINS CONFIDENTIAL,
 * PROPRIETARY AND TRADE SECRET INFORMATION OF NOVELL, INC. ACCESS TO
 * THIS WORK IS RESTRICTED TO (I) NOVELL, INC. EMPLOYEES WHO HAVE A NEED
 * TO KNOW HOW TO PERFORM TASKS WITHIN THE SCOPE OF THEIR ASSIGNMENTS AND
 * (II) ENTITIES OTHER THAN NOVELL, INC. WHO HAVE ENTERED INTO
 * APPROPRIATE LICENSE AGREEMENTS. NO PART OF THIS WORK MAY BE USED,
 * PRACTICED, PERFORMED, COPIED, DISTRIBUTED, REVISED, MODIFIED,
 * TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED,
 * LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT OF NOVELL, INC. ANY USE OR EXPLOITATION OF THIS WORK WITHOUT
 * AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL
 * LIABILITY.
 *
 * ========================================================================
 */
package org.spiffyui.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.text.ParseException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import org.spiffyui.client.rest.util.RESTAuthConstants;

/**
 * This servlet is a passthrough for authentication requests.
 */
public class AuthServlet extends HttpServlet
{
    private static final String BASIC_AUTH = "BASIC";

    /**
     * This is a constant indicating the authentication server returned invalid JSON
     */
    public static final String INVALID_JSON_RESPONSE = "InvalidJSONReseponse";

    private static final long serialVersionUID = -1L;

    /**
     * This is the default content type for an HTML file.
     */
    public static final String CONTENT_TYPE = "application/json";

    static {
        setHostnameVerifier();
    }

    private static void setHostnameVerifier()
    {
        /*
         * Normally the Java URL connection is very restrictive about what
         * certificates it will accept for an SSL connection and it requires
         * the host name and the certificate name to match exactly.  We don't
         * want to be that strict so we had a custom host name verifier.
         */
        SSLSocketFactory.getSocketFactory().setHostnameVerifier(new X509HostnameVerifier()
            {
                @Override
                public void verify(String host, SSLSocket ssl) throws IOException
                {
                    // no-op
                }

                @Override
                public void verify(String host, X509Certificate cert) throws SSLException
                {
                    // no-op
                }

                @Override
                public boolean verify(String host, SSLSession session)
                {
                    return true;
                }

                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException
                {
                    //no-op
                }
            });
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        response.setContentType(CONTENT_TYPE);

        String auth = null;

        /*
         * We are manually reading the request as bytes and
         * then converting it to characters.  This is extra
         * work and we should be able to just call getReader
         * insted of getInputStream.  However, the JBoss
         * implementation of the reader here has a bug that
         * sometiems shows up where the index of the reader
         * is wrong so we can work around it using the input
         * stream directly.
         *
         * https://jira.jboss.org/browse/JBAS-7817
         */

        InputStream in = request.getInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int read;
        byte[] buf = new byte[1024];

        try {
            while ((read = in.read(buf)) > 0) {
                out.write(buf, 0, read);
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        if (request.getCharacterEncoding() != null) {
            auth = out.toString(request.getCharacterEncoding());
        } else {
            /*
             * There are some cases where the requested character encoding is null.
             * This happens with some application servers and with IE7.  We need to
             * use some encoding in that case so we just use UTF-8.
             */
            auth = out.toString("UTF-8");
        }

        try {
            JSONObject authObj = new JSONObject(auth);
            if (request.getMethod().equals("POST")) {
                doLogin(request, response,
                        authObj.getString(RESTAuthConstants.USERNAME_TOKEN),
                        authObj.getString(RESTAuthConstants.PASSWORD_TOKEN),
                        authObj.getString(RESTAuthConstants.AUTH_URL_TOKEN),
                        authObj.getString(RESTAuthConstants.AUTH_LOGOUT_URL_TOKEN));
                return;
            } else if (request.getMethod().equals("DELETE")) {
                doLogout(request, response,
                         authObj.getString(RESTAuthConstants.USER_TOKEN),
                         authObj.getString(RESTAuthConstants.AUTH_URL_TOKEN));
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            returnError(response, e.getMessage(), RESTAuthConstants.INVALID_JSON);
        }
    }

    private boolean validateURI(HttpServletRequest request, String uri)
        throws MalformedURLException
    {
        /*
         The server trusts the client to pass the authentication URL.
         If the client is compromised (like with an XSS attack) then
         it could pass the URL to a bogus authentication server and
         get this servlet to forward the user's credentials there.

         This is especially dangerous since this serverlet is not
         governed by the same origin policy.  To protect against
         this we are using a white list of approved authentication
         servers.  Right now that list is just the server containing
         the client WAR.

         In the future we may support hosting the authentication server
         on a different server and then we will need a more fully featured
         white list.
         */
        URI.create(uri);

        URL tsUrl = new URL(uri);
        URL serverUrl = new URL(request.getRequestURL().toString());

        return tsUrl.getHost().equals(serverUrl.getHost()) &&
            tsUrl.getPort() == serverUrl.getPort() &&
            tsUrl.getProtocol().equals(serverUrl.getProtocol());
    }

    private void doLogin(HttpServletRequest request, HttpServletResponse response,
                         String user, String pwd, String tsUrl, String logoutUrl)
        throws ServletException, IOException
    {
        ServletOutputStream out = response.getOutputStream();
        if (user == null || pwd == null || tsUrl == null) {
            returnError(response, "Login requires a username, password, and token server URL",
                        RESTAuthConstants.INVALID_LOGIN_REQUEST);
            return;
        }

        try {
            if (!validateURI(request, tsUrl)) {
                returnError(response, tsUrl, RESTAuthConstants.INVALID_TS_URL);
                return;
            }
        } catch (IllegalArgumentException iae) {
            returnError(response, iae.getMessage(), RESTAuthConstants.INVALID_TS_URL);
            return;
        } catch (MalformedURLException iae) {
            returnError(response, iae.getMessage(), RESTAuthConstants.INVALID_TS_URL);
            return;
        }

        HttpClient httpclient = new DefaultHttpClient();

        HttpPost httppost = new HttpPost(tsUrl);

        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Accept-Charset", "UTF-8");

        httppost.setHeader("Authorization", BASIC_AUTH + " " +
                           new String(Base64.encodeBase64(new String(user + ":" + pwd).getBytes("UTF-8"))));

        httppost.setHeader("NovellRptSignOffUri", logoutUrl);

        // Execute the request
        HttpResponse authResponse = httpclient.execute(httppost);

        int status = authResponse.getStatusLine().getStatusCode();

        if (status == 404) {
            returnError(response, "The token server URL was not found",
                        RESTAuthConstants.NOTFOUND_TS_URL);
            return;
        }

        // Get hold of the response entity
        HttpEntity entity = authResponse.getEntity();

        StringBuffer authResponseData = new StringBuffer();

        // If the response does not enclose an entity, there is no need
        // to worry about connection release
        if (entity != null) {
            InputStream instream = entity.getContent();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(instream));

                // do something useful with the response
                authResponseData.append(reader.readLine());
            } catch (RuntimeException ex) {
                // In case of an unexpected exception you may want to abort
                // the HTTP request in order to shut down the underlying
                // connection and release it back to the connection manager.
                httppost.abort();
                throw ex;
            } finally {
                // Closing the input stream will trigger connection release
                if (reader != null) {
                    reader.close();
                }
            }

            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }


        //Now we write the response back to our client.

        if (authResponse.containsHeader("WWW-Authenticate")) {
             //
             // If the authentication server responded with a 401 it means
             // that the specified username and password weren't valid.  We
             // should be returning a 401 to the client, but doing that causes
             // Firefox to prompt for login using the default login dialog
             // and we don't want that.
             //
            response.setHeader("WWW-Authenticate", authResponse.getFirstHeader("WWW-Authenticate").getValue());
            status = 400;
        }
        response.setStatus(status);
        out.print(authResponseData.toString());

        out.flush();
        out.close();
    }

    private void doLogout(HttpServletRequest request,
                          HttpServletResponse response,
                         String token, String tsUrl)
        throws ServletException, IOException
    {
        if (token == null || tsUrl == null) {
            returnError(response, "Logout requires a token and a token server URL",
                        RESTAuthConstants.INVALID_LOGOUT_REQUEST);
            return;
        }

        try {
            validateURI(request, tsUrl);
        } catch (IllegalArgumentException iae) {
            returnError(response, iae.getMessage(), RESTAuthConstants.INVALID_TS_URL);
            return;
        }

        HttpClient httpclient = new DefaultHttpClient();

        HttpDelete httpdel = new HttpDelete(tsUrl + "/" + token);

        httpdel.setHeader("Accept", "application/json");
        httpdel.setHeader("Accept-Charset", "UTF-8");
        httpdel.setHeader("Authorization", request.getHeader("Authorization"));
        httpdel.setHeader("TS-URL", request.getHeader("TS-URL"));


        // Execute the request
        HttpResponse authResponse = httpclient.execute(httpdel);

        int status = authResponse.getStatusLine().getStatusCode();
        if (status == 404) {
            returnError(response, "The token server URL was not found",
                        RESTAuthConstants.NOTFOUND_TS_URL);
            return;
        }

        // Get hold of the response entity
        HttpEntity entity = authResponse.getEntity();

        StringBuffer authResponseData = new StringBuffer();

        // If the response does not enclose an entity, there is no need
        // to worry about connection release
        if (entity != null) {
            InputStream instream = entity.getContent();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(instream));

                // do something useful with the response
                authResponseData.append(reader.readLine());
            } catch (RuntimeException ex) {
                // In case of an unexpected exception you may want to abort
                // the HTTP request in order to shut down the underlying
                // connection and release it back to the connection manager.
                httpdel.abort();
                throw ex;
            } finally {
                // Closing the input stream will trigger connection release
                if (reader != null) {
                    reader.close();
                }
            }

            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }

        //Now we write the response back to our client.
        response.setStatus(status);
        ServletOutputStream out = response.getOutputStream();
        out.print(authResponseData.toString());

        out.flush();
        out.close();
    }

    private void returnError(HttpServletResponse response, String message, String sCode)
        throws ServletException, IOException
    {
        JSONObject base = new JSONObject();

        JSONObject fault = new JSONObject();

        JSONObject code = new JSONObject();
        code.put("Value", sCode);
        JSONObject subcode = new JSONObject();
        subcode.put("Value", "");
        code.put("Subcode", subcode);
        fault.put("Code", code);

        JSONObject reason = new JSONObject();
        reason.put("Text", message);
        fault.put("Reason", reason);

        base.put("Fault", fault);
        response.getOutputStream().println(base.toString());
    }
}