package org.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockProviderImpl implements LockProvider {

    private static final LockProviderImpl instance = new LockProviderImpl();
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    private LockProviderImpl() {
    }

    public static LockProviderImpl getLockProvider() {
        return instance;
    }

    @Override
    public ReentrantLock get(final String lockId) {
        return locks.get(lockId);
    }

    public void createLock(final String lockId) {
        locks.put(lockId, new ReentrantLock());
    }
}
