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

public class CLI {
	public static void main(String[] args) {
		try {
			execute(Arguments.build(args));
		} catch (HelpException e) {
			System.out.println(e.getMessage());
		} catch (ErrorException e) {
			System.err.println(e.getMessage());
			System.exit(2);
		} catch (Exception e) {
			System.err.println(e.toString());
			System.exit(2);
		}
	}

	private static void execute(Arguments arguments) throws ErrorException {
		StdIOCapture ioc = new StdIOCapture();
		ioc.open();
		try (JMXEngine jmxEngine = new JMXEngine(arguments.getServer(),arguments.getUsername(), arguments.getPassword())) {
			ioc.close();
			for (Command cmd : arguments)
				execute(jmxEngine, cmd);
		} catch (Exception e) {
			ioc.close();
			ioc.print(System.out);
			throw e;
		}
	}

	private static void execute(JMXEngine jmxEngine, Command cmd) throws ErrorException {
		switch (cmd.getType()) {
		case GET:
			System.out.println(jmxEngine.getValue(cmd.getObjectName(), cmd.getAttributeName()));
			break;
		case LO:
			System.out.println(jmxEngine.listObjectNames());
			break;
		case LA:
			System.out.println(jmxEngine.listAttributeNames(cmd.getObjectName()));
			break;
		default:
			break;
		}
	}
}
