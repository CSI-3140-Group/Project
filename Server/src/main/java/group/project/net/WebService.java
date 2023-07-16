package group.project.net;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;

@ServerEndpoint(value="/")
public class WebService {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Session opened, id: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println(message);
        Connection.of(session).handle(message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("Error occurred");
        error.printStackTrace();
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Session closed, reason: " + reason);
        Connection.of(session).close(reason);
    }

}