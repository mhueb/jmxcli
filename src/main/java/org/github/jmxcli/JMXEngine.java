/* 
 * Copyright 2014 jmxcli contributers
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.github.jmxcli;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JMXEngine implements AutoCloseable {

	private JMXConnector connector;
	private MBeanServerConnection mbsc;

	public JMXEngine(String server, String user, String password) throws ErrorException {
		try {
			Map<String, Object> env = null;
			if (user != null && password != null) {
				env = new HashMap<String, Object>();
				String[] creds = { user, password };
				env.put(JMXConnector.CREDENTIALS, creds);
			}
			this.connector = JMXConnectorFactory.connect(new JMXServiceURL(server), env);
		} catch (IOException e) {
			throw new ErrorException("Failed to connect Server: " + e.getMessage(), e);
		}

		try {
			mbsc = connector.getMBeanServerConnection();
		} catch (IOException e) {
			close();
			throw new ErrorException("Failed to get server connection: " + e.getMessage(), e);
		}
	}

	@Override
	public void close() {
		try {
			JMXConnector clean = connector;
			connector = null;
			mbsc = null;
			clean.close();
		} catch (IOException e) {
			System.err.println("Failed to close connection: " + e.getMessage());
		}
	}

	public String getValue(String objectName, String attributeName) throws ErrorException {
		try {
			return String.valueOf(mbsc.getAttribute(new ObjectName(objectName), attributeName));
		} catch (AttributeNotFoundException | InstanceNotFoundException | MalformedObjectNameException | MBeanException | ReflectionException | IOException e) {
			throw new ErrorException("Failed to get jmx attribute: " + e.getMessage(), e);
		}
	}

	public String listObjectNames() throws ErrorException {
		try {
			StringBuilder buff = new StringBuilder();
			Set<ObjectName> names = new TreeSet<ObjectName>(mbsc.queryNames(null, null));
			for (ObjectName name : names)
				buff.append(name).append("\n");
			if (buff.length() > 1)
				buff.setLength(buff.length() - 1);
			return buff.toString();
		} catch (IOException e) {
			throw new ErrorException("Failed to list jmx objects: " + e.getMessage(), e);
		}
	}

	public String listAttributeNames(String objectName) throws ErrorException {
		try {
			StringBuilder buff = new StringBuilder();
			MBeanInfo mBeanInfo = mbsc.getMBeanInfo(new ObjectName(objectName));
			for (MBeanAttributeInfo attr : mBeanInfo.getAttributes())
				buff.append(attr.getName()).append("\n");
			if (buff.length() > 1)
				buff.setLength(buff.length() - 1);
			return buff.toString();
		} catch (IOException | InstanceNotFoundException | IntrospectionException | MalformedObjectNameException | ReflectionException e) {
			throw new ErrorException("Failed to list jmx attributes: " + e.getMessage(), e);
		}
	}
}
