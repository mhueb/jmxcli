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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class StdIOCapture implements AutoCloseable {
	private ByteArrayOutputStream baos;
	private PrintStream capture;
	private PrintStream out;
	private PrintStream err;

	private String msg;

	public void open() {
		if (baos != null)
			throw new IllegalStateException("Already open");
		msg = null;
		baos = new ByteArrayOutputStream();
		capture = new PrintStream(baos, true);
		out = System.out;
		err = System.err;
		System.setOut(capture);
		System.setErr(capture);
	}

	public boolean isCapturing() {
		return baos != null;
	}

	public void close() {
		if (baos == null)
			return;
		try {
			System.setOut(out);
			System.setErr(err);
			capture.flush();
			baos.flush();
			msg = baos.toString();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			baos = null;
			out = null;
			capture = null;
		}
	}

	public String getMsg() {
		return msg;
	}

	public void print(PrintStream out) {
		if (msg != null && !msg.isEmpty())
			out.print(msg);
	}
}
