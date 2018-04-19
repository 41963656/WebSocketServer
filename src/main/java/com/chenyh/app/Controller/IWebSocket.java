package com.chenyh.app.Controller;

import java.io.IOException;

import javax.websocket.Session;

public interface IWebSocket {

	public void open(Session session);

	public void close();
	
	public void onMessage(String message, Session session) ;
	
	public void onError(Session session, Throwable error);
	
	public void sendMessage(String message) throws IOException;
	
	
}
