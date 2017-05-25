package cz.spiffyk.flpmanager.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lombok.NonNull;

public class Messenger {
	
	private Set<Subscriber> subscribers = new HashSet<>();
	
	private static final Messenger singleton = new Messenger();
	
	private Messenger() {}
	
	public static Messenger get() {
		return singleton;
	}
	
	public boolean addListener(@NonNull Subscriber sub) {
		return subscribers.add(sub);
	}
	
	public boolean removeListener(@NonNull Subscriber sub) {
		return subscribers.remove(sub);
	}
	
	public void message(@NonNull MessageType type, Object... args) {
		System.out.println("Message (" + type.name() + "): " + Arrays.toString(args));
		
		for (Subscriber sub : subscribers) {
			sub.onMessage(type, args);
		}
	}
	
	public enum MessageType {
		ERROR, WARNING, INFO, HIDE_STAGE, SHOW_STAGE
	}
	
	public interface Subscriber {
		public void onMessage(MessageType type, Object... args);
	}
}
