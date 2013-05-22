package org.matheusdev.ror.net.packages;/*
 * Created with IntelliJ IDEA.
 * Author: matheusdev
 * Date: 5/11/13
 * Time: 6:08 PM
 */

public class ChatMessage extends NetPackage {
	public String content;

	public ChatMessage(String message) {
		this.content = message;
	}

	public String toString() {
		return content;
	}
}
