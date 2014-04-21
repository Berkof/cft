package qp;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Queues {

	private AtomicInteger lastProcessedQId;			// Идентификатор последней обработанной очереди 
	private AtomicLong totalProcessed;				// Общее количество обработанных элементов
	private static final int PAUSE = 10;			// Время парковки потока, если нет задач
	
	private LinkedBlockingQueue<Task> queues[];		// Массив очередей обработки
	private AtomicInteger qThreads[];				// Массив идентификаторов потоков, занятых соответвтвующей очередью 
	
	
	/**
	 * Конструктор, задающий общее количество очередей
	 * @param queueCount общее количество очередей
	 */
	public Queues(int queueCount, int workerCount) {
		lastProcessedQId = new AtomicInteger(0);
		totalProcessed = new AtomicLong(0L);
		queues = new LinkedBlockingQueue[queueCount];
		qThreads = new AtomicInteger[queueCount];
		for (int i=0;i<queueCount;i++) {
			queues[i] = new LinkedBlockingQueue<Task>();
			qThreads[i] = new AtomicInteger(-1);
		}
		
		for (int i=0;i<workerCount;i++) {
			Worker tWorker = new Worker(this);
			Thread tThread = new Thread(tWorker);
			tThread.start();
		}
		
	}
	
	public void enqueueTask(Task task) {
		int queueId = task.getQueueId();
		boolean isOk = false;
		while(!isOk) {
			try {
				queues[queueId].put(task);
				qThreads[queueId].compareAndSet(-1, 0);
				isOk =true;
			} catch (InterruptedException e) {
				
			}
		}
	}
	
		
	public Task getNextTask() {
		Task result = null;
		int tQid,tNewQid;
		int counter=0;
		while (null == result) {
			tQid = lastProcessedQId.incrementAndGet();
			if (tQid >= queues.length) {
				tNewQid = tQid % queues.length;
				lastProcessedQId.compareAndSet(tQid, tNewQid);
				tQid = tNewQid;
			}
			if (qThreads[tQid].compareAndSet(0, 1)) {
				if (!queues[tQid].isEmpty()) {
					result = queues[tQid].peek();
				} else {
					qThreads[tQid].set(-1);
				}
			} else if (qThreads[tQid].compareAndSet(-1, 1)) {
				// TODO: организовать проверку на ложно-пустые 
				// очереди в следующем круге
				if (!queues[tQid].isEmpty()) {
					result = queues[tQid].peek();
				} else {
					qThreads[tQid].set(-1);
				}	
			}
			
			if (++counter > queues.length) {
				counter = 0;
				try {
					Thread.sleep(PAUSE);
				} catch (InterruptedException e) {
					// Ничего не предпринимаем
				}
			}
		}
		return result;
	}
	
	public void setProcessed(Task task) {
		int queieId = task.getQueueId();
		queues[queieId].poll();
		if (queues[queieId].isEmpty()) {
			qThreads[queieId].set(-1);
		} else {
			qThreads[queieId].set(0);
		}
		totalProcessed.incrementAndGet();
	}
	
	public long getTotalProcessed() {
		return totalProcessed.get();
	}
}
