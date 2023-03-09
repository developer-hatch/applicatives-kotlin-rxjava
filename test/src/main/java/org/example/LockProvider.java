package org.example;

import java.util.concurrent.locks.ReentrantLock;

public interface LockProvider {
    /**
     * Get a lock with a given ID.
     *
     * @param lockId the ID of the lock to get.
     * @return lock for the given ID.
     */
    ReentrantLock get(String lockId);
}
