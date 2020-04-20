package com.pr656d.cattlenotes.test.util

import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.domain.internal.SyncScheduler
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Rule to be used in tests that sets a synchronous task scheduler used to avoid race conditions.
 */
class SyncTaskExecutorRule : TestWatcher() {
    override fun starting(description: Description?) {
        super.starting(description)
        DefaultScheduler.setDelegate(SyncScheduler)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        SyncScheduler.clearScheduledPostDelayedTasks()
        DefaultScheduler.setDelegate(null)
    }

    /**
     * Force the (previously deferred) execution of all [Scheduler.postDelayedToMainThread] tasks.
     *
     * In tests, postDelayed is not eagerly executed, allowing test code to test self-scheduling
     * tasks.
     *
     * This will *not* run any tasks that are scheduled as a result of running the current delayed
     * tasks. If you need to test that more tasks were scheduled, call this function again.
     */
    fun runAllScheduledPostDelayedTasks() {
        SyncScheduler.runAllScheduledPostDelayedTasks()
    }
}
