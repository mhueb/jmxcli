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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Arguments implements Iterable<Command> {
	private String server;

	private List<Command> commands = new ArrayList<Command>();

	private String username;

	private String password;

	private static final String HELP_MESSAGE = "" + //
			"usage: jmxcli {options} {commands}\n" + //
			"\n" + //
			"commands:\n" + //
			"get <object name> <attribute name>    Get attribute value\n" + //
			"lo                                    List available objects\n" + //
			"la <object name>                      List available attributes\n" + //
			"\n" + //
			"options:\n" + //
			"-s <server>                           JMX server\n" + //
			"-user <username>                      Username for authentication\n" + //
			"-password <password>                  Password for authentication\n" + //
			"";

	private static Map<String, Command.Type> COMMANDS = new HashMap<String, Command.Type>();
	static {
		COMMANDS.put("help", Command.Type.HELP);
		COMMANDS.put("get", Command.Type.GET);
		COMMANDS.put("lo", Command.Type.LO);
		COMMANDS.put("la", Command.Type.LA);
	}

	public static Arguments build(String[] args) throws ErrorException {
		Arguments arguments = new Arguments();
		Iterator<String> it = Arrays.asList(args).iterator();
		while (it.hasNext()) {
			String arg = it.next();
			if (arg.startsWith("-")) {
				if (arg.equals("-s")) {
					arguments.server = getNext(it, arg);
				} else if (arg.equals("-password")) {
					arguments.password = getNext(it, arg);
				} else if (arg.equals("-user")) {
					arguments.username = getNext(it, arg);
				} else
					throw new ErrorException("Unknown option: " + arg);
			} else {
				Command cmd = new Command();
				arguments.commands.add(cmd);
				cmd.type = COMMANDS.get(arg);
				if (cmd.type == null)
					throw new ErrorException("Unknown command: " + arg);
				switch (cmd.type) {
				case GET:
					cmd.objectName = getNext(it, arg);
					cmd.attributeName = getNext(it, arg);
					break;
				case LO:
					break;
				case LA:
					cmd.objectName = getNext(it, arg);
					break;
				case HELP:
					throw new HelpException(HELP_MESSAGE);
				}
			}
		}

		if (arguments.commands.isEmpty())
			throw new ErrorException("Missing command(s)");
		if (arguments.server == null)
			throw new ErrorException("Missing server option");
		if ((arguments.username == null) != (arguments.password == null))
			throw new ErrorException("User and password required");

		return arguments;
	}

	private static String getNext(Iterator<String> it, String arg) throws ErrorException {
		if (!it.hasNext())
			throw new ErrorException("Missing argument for " + arg + " without value");
		return it.next();
	}

	public String getServer() {
		return server;
	}

	@Override
	public Iterator<Command> iterator() {
		return Collections.unmodifiableCollection(commands).iterator();
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
