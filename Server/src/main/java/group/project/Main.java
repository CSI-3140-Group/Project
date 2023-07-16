package group.project;

import group.project.init.Caches;
import group.project.init.Packets;
import group.project.net.WebService;
import org.glassfish.tyrus.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException {
        Packets.initialize();
        Caches.initialize();

        Server server = new Server("localhost", 6969, "/service", null, WebService.class);

        try {
            server.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            for(String command; (command = reader.readLine()) != null; ) {
                if("stop".equals(command)) {
                    System.out.println("Stopping the server...");
                    break;
                }
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
            server.stop();
        }
    }

}
