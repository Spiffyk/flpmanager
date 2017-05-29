package cz.spiffyk.flpmanager.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lombok.NonNull;

/**
 * A singleton class for sending messages between modules while practically leaving them independent of each other
 * @author spiffyk
 */
public class Messenger {
	
	/**
	 * The set of subscribers
	 */
	private Set<Subscriber> subscribers = new HashSet<>();
	
	/**
	 * The singleton instance of messenger
	 */
	private static final Messenger singleton = new Messenger();
	
	
	
	/**
	 * Singleton constructor
	 */
	private Messenger() {}
	
	
	
	/**
	 * Gets the singleton instance of the messenger
	 * @return The instnace
	 */
	public static Messenger get() {
		return singleton;
	}
	
	/**
	 * Adds a new subscriber to the messenger
	 * @param sub The subscriber
	 * @return {@code true} if the subscriber has been really added
	 */
	public boolean addListener(@NonNull Subscriber sub) {
		return subscribers.add(sub);
	}
	
	/**
	 * Removes a subscriber from the messenger
	 * @param sub The subscriber
	 * @return {@code true} if the subscriber has been really removed
	 */
	public boolean removeListener(@NonNull Subscriber sub) {
		return subscribers.remove(sub);
	}
	
	/**
	 * Sends a message to all the subscribers
	 * @param type The type of the message
	 * @param args The arguments of the message
	 */
	public void message(@NonNull MessageType type, Object... args) {
		System.out.println("Message (" + type.name() + "): " + Arrays.toString(args));
		
		for (Subscriber sub : subscribers) {
			sub.onMessage(type, args);
		}
	}
	
	/**
	 * The enum of message types
	 * @author spiffyk
	 */
	public enum MessageType {
		ERROR, WARNING, INFO, HIDE_STAGE, SHOW_STAGE, PROJECT_OPEN, PROJECT_CLOSE
	}
	
	/**
	 * The interface of the messenger's subscriber
	 * @author spiffyk
	 */
	public interface Subscriber {
		public void onMessage(MessageType type, Object... args);
	}
}
