package org.example.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractClientHandler implements Callable<String> {
    protected static ExecutorService clientExecutor = Executors.newCachedThreadPool();

    protected final Socket socket;
    protected final String nickname;
    protected final String roomID;
    protected final ObjectInputStream in;
    protected final ObjectOutputStream out;

    protected AbstractClientHandler(Socket socket) throws IOException, ClassNotFoundException {
        this.socket = socket;
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());

        ClientConnectionDTO dto = (ClientConnectionDTO) in.readObject();

        nickname = dto.nickname();
        roomID = dto.roomID();
    }

    public void sendToClient(Object msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    public String getNickname() {
        return nickname;
    }
}
