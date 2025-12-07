package org.apollo.game.session;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;

import org.apollo.jagcached.RequestWorkerPool;
import org.apollo.net.codec.jaggrab.JagGrabRequest;
import org.apollo.net.codec.update.OnDemandRequest;
import org.apollo.net.update.UpdateDispatcher;

/**
 * An update session.
 *
 * @author Graham
 */
public final class UpdateSession extends Session {

	/**
	 * Creates an update session for the specified channel.
	 *
	 * @param channel The channel.
	 * @param context The server context.
	 */
	public UpdateSession(Channel channel) {
		super(channel);
	}

	@Override
	public void destroy() {
		channel.close();
	}

	@Override
	public void messageReceived(Object message) {
		UpdateDispatcher dispatcher = RequestWorkerPool.getDispatcher();

		if (message instanceof OnDemandRequest request) {
			dispatcher.dispatch(getChannel(), request);
		} else if (message instanceof JagGrabRequest request) {
			dispatcher.dispatch(getChannel(), request);
		} else if (message instanceof HttpRequest request) {
			dispatcher.dispatch(getChannel(), request);
		} else {
			throw new IllegalArgumentException("Unknown message type.");
		}
	}

}