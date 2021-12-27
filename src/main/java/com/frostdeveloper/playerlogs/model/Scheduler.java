package com.frostdeveloper.playerlogs.model;

public interface Scheduler
{
	/**
	 * Called to initialize the runnable
	 *
	 * @since 1.2
	 */
	void start();
	
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
