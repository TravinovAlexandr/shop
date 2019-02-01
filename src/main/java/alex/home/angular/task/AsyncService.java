package alex.home.angular.task;

import alex.home.angular.task.mediator.AsyncMethod;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class AsyncService {

    private final Map<Object, ServiceEntry> map = new WeakHashMap<>();

    public void initCall(Object key, AsyncMethod mediator) {
        Thread th = Thread.currentThread();
        AsyncTask task = new AsyncCallableTask(mediator);
        FutureTask futureTask = new FutureTask(task);
        ServiceEntry entry = new ServiceEntry(futureTask);
        
        th.setName(key + UUID.randomUUID().toString());
        map.put(th.getName(), entry);
    }

    public Future execute(Object key, Executor taskExecutor) {
        ServiceEntry entry = map.get(Thread.currentThread().getName());
        
        taskExecutor.execute(entry.futureTask);
        return entry.futureTask;
    }

    private static class ServiceEntry {

        private final FutureTask futureTask;

        private ServiceEntry(FutureTask futureTask) {
            this.futureTask = futureTask;
        }
    }
}