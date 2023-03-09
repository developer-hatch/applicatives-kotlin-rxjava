package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import static org.example.LockProviderImpl.getLockProvider;

public class SomeProcessor {

    public void process(final UserEvent userEvent) {
        String userId = userEvent.getId();
        System.out.println("Created user id: " + userId);
        LockProvider locks = getLockProvider();
        ReentrantLock lock = locks.get(userId);
        System.out.println("User id before long execution: " + userId);
        lock.lock();
        try {
            Thread.sleep(0);
            System.out.println("User id do stuff. ID: " + userId);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public static UserEvent createUserEvent() {
        final UserEvent userEvent = new UserEvent(UUID.randomUUID().toString());
        System.out.println("User event created successfully with id: " + userEvent.getId());
        getLockProvider().createLock(userEvent.getId());
        return userEvent;
    }

    public List<UserEvent> createUserEvents() {
        final List<UserEvent> userEventList = new ArrayList<>();
        for (int i = 0; i < 1000000L; i++) {
            final UserEvent userEvent = createUserEvent();
            userEventList.add(userEvent);
        }
        return userEventList;
    }

    public static void main(String[] args) {
        final SomeProcessor someProcessor = new SomeProcessor();
        final List<UserEvent> userEvents1 = someProcessor.createUserEvents();
        final List<UserEvent> userEvents2 = someProcessor.createUserEvents();
        final List<UserEvent> userEvents3 = someProcessor.createUserEvents();
        new Thread(() -> {
            userEvents1.forEach((someProcessor::process));
            System.out.println("Thread 1");
        }).start();

        new Thread(() -> {
            userEvents2.forEach((someProcessor::process));
            System.out.println("Thread 2");
        }).start();

        new Thread(() -> {
            userEvents3.forEach((someProcessor::process));
            System.out.println("Thread 3");
        }).start();

    }

}
