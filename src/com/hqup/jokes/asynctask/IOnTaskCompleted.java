package com.hqup.jokes.asynctask;

/**
 * http://stackoverflow.com/questions/19520188/return-value-from-asynctask-
 * without-get-method
 * 
 * @author Andrew2212 </br>If you need to have Asynctask in another class,
 *         then an interface is probably your best option.</br>The idea of
 *         the interface is more or less to have the method called by the
 *         Asynctask and executed inside of the activity, hence the callback
 *         concept.
 */
public interface IOnTaskCompleted {
	/**
	 * This method is called by the Asynctask and executes inside of the
	 * ListJokesActivity
	 * 
	 */
	void onTaskCompleted();
}
