package com.tah.im.model;

public class IMMessage {
	
	public enum MessageCommand {
		START_CONVO("start"),
		START_QUESTION("question"),
		REPLY("reply"),
		EXIT("exit"),
		
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
			//TODO: bad parsing
			int startIndex = getCommandText().length();
			return body.substring(startIndex);
		}
	}
	
	private MessageCommand command;
	private String param;
	
	
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
