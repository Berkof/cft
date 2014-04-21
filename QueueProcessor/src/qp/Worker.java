package qp;


/**
 * Рабочий поток, обрабатывающий задачи в очереди
 * @author sbelyak
 *
 */
public class Worker implements Runnable {

	private Queues queues;
	
	public Worker(Queues queues) {
		this.queues = queues;
	}
	
	@Override
	public void run() {
		Task tTask=null;
		while (true) {
			try{
				tTask = queues.getNextTask();
				processTask(tTask.getQueueId(), tTask.getItemId());
				
			} finally {
				if (tTask != null) {
					queues.setProcessed(tTask);
				}
			}
			
			
		}
	}
	
	private void processTask(long queueId, long itemId) {
		String result = "queueId=" + queueId + ", itemId=" + itemId;
		System.out.println("queueId=" + queueId + ", itemId=" + itemId);
	}

}
