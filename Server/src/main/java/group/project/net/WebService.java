package group.project.net;

import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;

@ServerEndpoint(value="/service")
public class WebService {

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        Connection.of(session).handle(message);
    }

}