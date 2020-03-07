package thread.complete.listener;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NotifyingThread extends Thread {
    private int init_thread_cnt = 0;

    private final Set<ThreadCompleteListener> listeners = new CopyOnWriteArraySet<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyingThread.class);
    protected boolean isSuccess = false;
    public final void addListener(final ThreadCompleteListener listener) {
        listeners.add(listener);
    }

    public final void removeListener(final ThreadCompleteListener listener) {
        listeners.remove(listener);
    }

    private final void notifyListeners() {
        for (ThreadCompleteListener listener : listeners) {
            listener.notifyOfThreadComplete(this);
        }
    }

    @Override
    public final void run() {
        try {
            doRun();
        } catch (Exception e){
            LOGGER.error("NotifyingThread", e);
            throw e;
        }
        finally {
            notifyListeners();
        }
    }

    public abstract void doRun();

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
