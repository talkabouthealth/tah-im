package com.tah.im.model;

public class UserMessage {
	
	public enum MessageCommand {
		START_CONVO("start"),
		START_QUESTION("question"),
		REPLY("reply"),
		EXIT("exit"),
		YES("yes"),
		
		NO_COMMAND("");
		
		private final String commandText;

		private MessageCommand(String commandText) {
			this.commandText = commandText;
		}

		public String getCommandText() {
			return commandText;
		}

		public String parseParam(String body) {
			if (this == NO_COMMAND) {
				return body;
			}
			int startIndex = getCommandText().length() + 2;
			if (startIndex >= body.length()) {
				return null;
			}
			else {
				String param = body.substring(startIndex);
				if (param.trim().length() == 0) {
					param = null;
				}
				return param;
			}
		}
	}
	
	private MessageCommand command;
	private String param;
	
	@Override
	public String toString() {
		return command+" : "+param;
	}
	public MessageCommand getCommand() {
		return command;
	}
	public void setCommand(MessageCommand command) {
		this.command = command;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public boolean hasParam() {
		// TODO Auto-generated method stub
		return false;
	}
}
