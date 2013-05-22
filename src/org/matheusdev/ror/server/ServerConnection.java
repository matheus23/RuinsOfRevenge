/*
 * Copyright (c) 2013 matheusdev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.matheusdev.ror.server;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import org.matheusdev.ror.net.packages.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author matheusdev
 *
 */
public class ServerConnection extends Listener implements Disposable {

	private boolean disposed = false;

	private final Server server;
	private final LinkedBlockingQueue<Runnable> queue;
	private final ObjectMap<Integer, ConnectedClient> connectedClients;
	private final ServerMaster master;

	private long tickTime = 0;

	public ServerConnection(ServerMaster master, int tcpPort, int udpPort) throws IOException {
		this.master = master;
		this.server = new Server();
		this.queue = new LinkedBlockingQueue<>();
		this.connectedClients = new ObjectMap<>();

		Register.registerAll(server.getKryo());
		server.start();
		server.bind(tcpPort, udpPort);
		server.addListener(new QueuedListener(this) {
			@Override
			protected void queue(Runnable runnable) {
				queue.add(runnable);
			}
		});
	}

	public Input getInput(int connectionID) {
		return connectedClients.get(connectionID).getInput();
	}

	public Server getServer() {
		return server;
	}

	public void sendToAllTCP(NetPackage pkg) {
		pkg.connectionType = NetPackage.TCP;
		pkg.time = tickTime;
		server.sendToAllTCP(pkg);
	}

	public void sendToAllUDP(NetPackage pkg) {
		pkg.connectionType = NetPackage.UDP;
		pkg.time = tickTime;
		server.sendToAllUDP(pkg);
	}

	public void sendToTCP(Connection connection, NetPackage pkg) {
		pkg.connectionType = NetPackage.TCP;
		pkg.time = tickTime;
		server.sendToTCP(connection.getID(), pkg);
	}

	public void sendToUDP(Connection connection, NetPackage pkg) {
		pkg.connectionType = NetPackage.UDP;
		pkg.time = tickTime;
		server.sendToUDP(connection.getID(), pkg);
	}

	public void tick() {
		Runnable run = null;
		while ((run = queue.poll()) != null) {
			run.run();
		}
		tickTime++;
	}

	@Override
	public void dispose() {
		if (!disposed) {
			disposed = true;
			server.stop();
		}
	}

	@Override
	public void connected(Connection connection) {
		System.out.println("[SERVER]: Client " + connection.getRemoteAddressTCP() + " connected.");
		connectedClients.put(connection.getID(), new ConnectedClient("TestClient", connection.getID()));
	}

	@Override
	public void disconnected(Connection connection) {
		System.out.println("[SERVER]: Client " + connection.getRemoteAddressTCP() + " disconnected.");
		connectedClients.remove(connection.getID());
		master.removeEntities(connection.getID());
	}

	private static final SimpleDateFormat minuteFormat = new SimpleDateFormat("HH:mm");

	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof CreateEntity) {
			CreateEntity create = (CreateEntity) object;
			create.state.id = master.getNewEntityID();
			master.addEntity(create.type, create.state, connection.getID());

			sendToAllTCP(create);
		} else if (object instanceof EntityState) {
			sendToAllUDP((EntityState)object);
		} else if (object instanceof Input) {
			Input in = (Input) object;
			connectedClients.get(connection.getID()).getInput().set(in.time, in);

			sendToTCP(connection, in);
		} else if (object instanceof FetchEntities) {
			ServerEntity[] entities = master.getEntities();
			CreateEntity[] creates = new CreateEntity[entities.length];
			for (int i = 0; i < entities.length; i++) {
				creates[i] = new CreateEntity(master.getTime(), entities[i].getEntity().getType(), entities[i].getEntity());
			}
			sendToTCP(connection, new FetchEntities(master.getTime(), creates));
		} else if (object instanceof ChatMessage) {
			Date date = new Date();
			String msg = String.format("[%s] %s: %s",
					minuteFormat.format(date), connectedClients.get(connection.getID()).getUsername(), object);
			sendToAllTCP(new ChatMessage(msg));
			System.out.println("Chat: " + msg);
		} else {
			System.out.println("Recieved strange object: " + object);
		}
	}

}
