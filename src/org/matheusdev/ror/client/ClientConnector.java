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
package org.matheusdev.ror.client;

import com.badlogic.gdx.utils.Disposable;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.matheusdev.ror.net.packages.*;
import org.matheusdev.ror.server.ServerMaster;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author matheusdev
 *
 */
public class ClientConnector extends Listener implements Disposable {

	private boolean disposed;

	private final Client client;
	private final LinkedBlockingQueue<Runnable> queue;
	private final Input newestInput;
	private final ClientMaster master;

	public ClientConnector(ClientMaster master, String host) throws IOException {
		this.master = master;
		this.client = new Client();
		this.queue = new LinkedBlockingQueue<>();
		this.newestInput = new Input();

		Register.registerAll(client.getKryo());
		client.start();
		client.connect(5000, InetAddress.getByName(host), ServerMaster.PORT);
		client.addListener(new QueuedListener(this) {
			@Override
			protected void queue(Runnable runnable) {
				queue.add(runnable);
			}
		});
	}

	public Client getClient() {
		return client;
	}

	public void send(Object obj) {
		client.sendTCP(obj);
	}

	public void tick(long time) {
		Runnable run;
		while ((run = queue.poll()) != null) {
			run.run();
		}
	}

	@Override
	public void connected(Connection connection) {
		System.out.println("[CLIENT]: connected to " + connection.getRemoteAddressTCP());
	}

	@Override
	public void disconnected(Connection connection) {
		System.out.println("[CLIENT]: client " + connection.getRemoteAddressTCP() + " disconnected.");
	}

	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof Input) {
            if (master.hasInitialized()) {
                Input in = (Input) object;
                newestInput.set(in.time, in);
            }
		} else if (object instanceof EntityState) {
            if (master.hasInitialized()) {
                EntityState state = (EntityState) object;
                master.updateEntity(state);
            }
		} else if (object instanceof CreateEntity) {
			CreateEntity create = (CreateEntity) object;
			master.addEntity(create);
		} else if (object instanceof FetchEntities) {
			FetchEntities fetch = (FetchEntities) object;
			for (CreateEntity create : fetch.creates) {
				master.addEntity(create);
			}
		} else if (object instanceof DeleteEntity) {
			DeleteEntity delete = (DeleteEntity) object;
			master.removeEntity(delete.id);
		}
	}

	@Override
	public void dispose() {
		if (!disposed) {
			disposed = true;
			client.stop();
		}
	}

	public Input getNewestInput() {
		return newestInput;
	}

}
