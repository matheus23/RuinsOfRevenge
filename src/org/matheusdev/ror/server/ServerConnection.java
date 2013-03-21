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
	private final ObjectMap<Integer, Input> inputs;
	private final ServerMaster master;

	public ServerConnection(ServerMaster master, int port) throws IOException {
		this.master = master;
		this.server = new Server();
		this.queue = new LinkedBlockingQueue<>();
		this.inputs = new ObjectMap<>();

		Register.registerAll(server.getKryo());
		server.start();
		server.bind(port);
		server.addListener(new QueuedListener(this) {
			@Override
			protected void queue(Runnable runnable) {
				queue.add(runnable);
			}
		});
	}

	public Input getInput(int connectionID) {
		return inputs.get(connectionID);
	}

	public Server getServer() {
		return server;
	}

	public void send(Object obj) {
		server.sendToAllTCP(obj);
	}

	public void tick() {
		Runnable run = null;
		while ((run = queue.poll()) != null) {
			run.run();
		}
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
		inputs.put(connection.getID(), new Input());
	}

	@Override
	public void disconnected(Connection connection) {
		System.out.println("[SERVER]: Client " + connection.getRemoteAddressTCP() + " disconnected.");
		inputs.remove(connection.getID());
		master.removeEntities(connection.getID());
	}

	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof CreateEntity) {
			CreateEntity create = (CreateEntity) object;
			create.state.id = master.getNewEntityID();
			master.addEntity(create.type, create.state, connection.getID());

			server.sendToAllTCP(object);
		} else if (object instanceof EntityState) {
			server.sendToAllTCP(object);
		} else if (object instanceof Input) {
			Input in = (Input) object;
			inputs.get(connection.getID()).set(in.time, in);

			server.sendToTCP(connection.getID(), object);
		} else if (object instanceof FetchEntities) {
			ServerEntity[] entities = master.getEntities();
			CreateEntity[] creates = new CreateEntity[entities.length];
			for (int i = 0; i < entities.length; i++) {
				creates[i] = new CreateEntity(master.getTime(), entities[i].getEntity().getType(), entities[i].getEntity());
			}
			server.sendToTCP(connection.getID(), new FetchEntities(master.getTime(), creates));
		} else if (object instanceof String) {
            Date date = new Date();
            server.sendToAllTCP("[" + date + "]" + " " + ((String) object));
        }
	}

}
