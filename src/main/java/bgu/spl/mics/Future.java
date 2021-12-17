package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	private T result;
	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		result = null ;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
	 * @post returns valid result
     */
	public T get() {
		while(!isDone()) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		return result;
	}
	
	/**
     * Resolves the result of this Future object.
	 * @param result - a completed proccess from a microservice
	 * @pre result != null && this.result == null
	 * @post this.result = T result
     */
	public void resolve (T result) {
		if(result == null || this.result != null )
			throw new IllegalArgumentException("argument is null or this.result is already resolved");
		this.result = result;
		synchronized (this){
			notifyAll();
		}
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isDone() {
		if(this.result == null)
			return false;
		return true;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timeout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
	 * @pre unit is legal TimeUnit && timeout != null && timeout > 0
	 * @post return legal result if available
     */
	public T get(long timeout, TimeUnit unit) {
		if (result == null) {
			try {
				synchronized (this) {
					unit.sleep(timeout);
				}
			} catch (InterruptedException e) {
			}
		}
			return result;
	}
}
