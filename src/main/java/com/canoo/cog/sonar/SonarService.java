package com.canoo.cog.sonar;

/*
 * #%L
 * code-of-gotham
 * %%
 * Copyright (C) 2015 Canoo Engineering AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.canoo.cog.sonar.model.CityModel;
import com.canoo.cog.sonar.model.SonarProject;

public class SonarService {

    SonarResultParser sonarResultParser;

    private String baseUrl;
    private String userName;
    private String password;
    private String proxyHost;
    private Integer proxyPort;

    public SonarService() {
        sonarResultParser = new SonarResultParser();
    }

    public void setSonarSettings(String baseUrl, String userName, String password, String proxyHostAndPort) throws SonarException {
        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/";
        }
        this.baseUrl = baseUrl;
        this.userName = userName;
        this.password = password;
        if (!proxyHostAndPort.isEmpty()) {
            if (!proxyHostAndPort.matches(".+:\\d+")) {
                throw new SonarException("Error when parsing the Proxy.");
            } else {
                try {
                    String[] split = proxyHostAndPort.split(":");
                    proxyHost = split[0];
                    proxyPort = Integer.valueOf(split[1]);
                } catch (Exception e) {
                    throw new SonarException("Error when parsing the Proxy.");
                }
            }
        }
    }

    public List<SonarProject> getProjects() throws SonarException {
        String projectResultString = callSonarAuth(SonarConstants.SONAR_PROJECTS_QUERY, userName, password);
        return sonarResultParser.parseProjects(projectResultString);
    }

    public CityModel getCityData(String projectKey) throws SonarException {
        String cityResultString = callSonarAuth(SonarConstants.getMetricsQueryForProject(projectKey), userName, password);
        return sonarResultParser.parseCity(cityResultString);
    }

    String callSonarAuth(String path, String userName, String password) throws SonarException {
        URL url;
        try {
            url = new URL(baseUrl + path);
        } catch (MalformedURLException e) {
            throw new SonarException("Error in Calling Sonar Service.", e);
        }

        try {
            String authPropertyKey;
            HttpURLConnection connection;
            String encoding = new String(Base64.encodeBase64((userName + ":" + password).getBytes()));

            if (isProxyUsed()) {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                authPropertyKey = "Proxy-Authorization";
                connection = (HttpURLConnection) url.openConnection(proxy);
            } else {
                authPropertyKey = "Authorization";
                connection = (HttpURLConnection) url.openConnection();
            }

            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty(authPropertyKey, "Basic " + encoding);
            InputStream content = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new SonarException("HTTP Get call to " + url.getPath() + " was not successful.");
            }

            return IOUtils.toString(in);
        } catch (ProtocolException e) {
            throw new SonarException("Error in Calling Sonar Service.", e);
        } catch (IOException e) {
            throw new SonarException("Error in Calling Sonar Service.", e);
        }
    }

    private boolean isProxyUsed() {
        return proxyHost != null && proxyPort != null;
    }
}
