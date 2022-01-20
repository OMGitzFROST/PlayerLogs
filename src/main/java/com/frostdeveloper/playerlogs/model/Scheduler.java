package com.frostdeveloper.playerlogs.model;

/**
 * An interface used to define the required classes needed in-order for a scheduler to work.
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public interface Scheduler
{
	/**
	 * Returns the taskId for the task.
	 *
	 * @return Task id number
	 * @since 1.2
	 */
	int getTaskId();
	
	/**
	 * Returns true if this task has been cancelled.
	 *
	 * @return true if the task has been cancelled
	 * @since 1.2
	 */
	boolean isCancelled();
	
	/**
	 * Will attempt to cancel this task.
	 *
	 * @since 1.2
	 */
	void cancel();
}
