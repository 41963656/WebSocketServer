package com.chenyh.app.Controller;

import java.io.IOException;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ServerEndpoint("/demowSocket")
@Component
public class DomeWebSocketController implements IWebSocket {

	private static final String WELCOME_STR = "Welcome login Message Plat!";

	private static int onlineCount = 0;
	private static Set<IWebSocket> webSocketSet = new java.util.concurrent.CopyOnWriteArraySet<>();

	private Session session;

	private synchronized void addOnlineCount() {
		this.onlineCount++;
	}

	private synchronized void subOnlineCount() {
		this.onlineCount--;
	}

	private synchronized int getOnlineCount() {
		return this.onlineCount;
	}

	@Override
	public void sendMessage(String msg) throws IOException {
		this.session.getBasicRemote().sendText(msg);
	}

	@OnOpen
	public void open(Session session) {
		this.session = session;
		this.webSocketSet.add(this);
		this.addOnlineCount();
		log.info("有新连接加入！当前在线人数为{}", this.getOnlineCount());
		try {
			this.sendMessage(String.format("%s ,You ID is %s", this.WELCOME_STR, session.getId()));
		} catch (IOException e) {
			log.error("IO Error", e);
		}

	}

	@OnClose
	public void close() {
		this.webSocketSet.remove(this);
		this.subOnlineCount();
		log.info("有一连接关闭！当前在线人数为{}", this.getOnlineCount());

	}

	@OnMessage
	public void onMessage(String message, Session session) {
		log.info("来自客户端的消息:" + message);
		for (IWebSocket ws : this.webSocketSet) {
			try {
				ws.sendMessage(String.format("%s:%s", session.getId(), message));
			} catch (IOException e) {
				log.error("IO Error", e);
			}
		}

	}

	@OnError
	public void onError(Session session, Throwable error) {
		log.error("发生错误", error);
	}

}
