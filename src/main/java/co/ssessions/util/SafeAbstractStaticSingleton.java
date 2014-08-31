package co.ssessions.util;


/**
 * Implements a common safe singleton creation pattern.
 * 
 * @author rashadmoore
 *
 * @param <T>
 */
public abstract class SafeAbstractStaticSingleton<T> {

	private static volatile Object singleton;
	
	
	public SafeAbstractStaticSingleton() {
		
		
	}
	
	
	@SuppressWarnings("unchecked")
	public T getInstance() {
		Object proxy = singleton;
		if (proxy == null) {
			synchronized (this) {
				proxy = singleton;
				if (proxy == null) {
					singleton = proxy = this.init();
				}
			}
		}
		return (T) proxy;
	}
	
	
	public void clearSingleton() {
		singleton = null;
	}
	
	
	public abstract T init();
	
}
