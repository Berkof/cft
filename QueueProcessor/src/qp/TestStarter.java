package qp;

import java.util.ArrayList;
import java.util.List;


public class TestStarter implements Runnable {

	private long taskCount;
	private Queues queues;
	private List<Integer> queueIds;
	
	public static void main(String[] argv) {
		if (argv.length < 5) {
			System.err.println("Start with params <taskCount> <queuesCount> <workerCount> <loaderCount> [<loaderQueues>]. "
					+ "For example: 10 2 2 1,2,3 4,5,6,7,8,9,0 to start 2 worker on 10 queues, 2 loader "
					+ "(first for queues 1,2,3 and second for queues 4,5,6,7,8,9,0)");
			System.exit(1);
		}
		Long taskCount = Long.valueOf(argv[0]);
		Integer queuesCount = Integer.valueOf(argv[1]);
		Integer workerCount = Integer.valueOf(argv[2]);
		Integer loaderCount = Integer.valueOf(argv[3]);
		if (argv.length != 4 + loaderCount) {
			System.err.println("Wrong number of argumnets");
			System.exit(1);
		}
		List<Integer> queueDistribution[] = new List[loaderCount];
		for (int i =0;i< loaderCount;i++) {
			String[] idxStr = argv[4+i].split(",");
			queueDistribution[i] = new ArrayList<Integer>(idxStr.length);
			for (String tIdx : idxStr) {
				queueDistribution[i].add(Integer.valueOf(tIdx));
			}
		}
		
		Queues testQueues = new Queues(queuesCount, workerCount);
		
		Thread loaderThread[] = new Thread[loaderCount];
		for (int i=0;i<loaderCount;i++) {
			TestStarter tLoaderObject = new TestStarter(taskCount, testQueues, queueDistribution[i]);
			loaderThread[i] = new Thread(tLoaderObject);
		}
		System.out.print("Starting...");
		for (int i=0;i<loaderCount;i++) {
			loaderThread[i].start();
		}
		System.out.println("OK");
	}
	
	public TestStarter(long taskCount, Queues queues, List<Integer> queueIds) {
		this.taskCount = taskCount;
		this.queues = queues;
		this.queueIds = queueIds;
		System.out.println("Create thread for queues " + queueIds + " with " + taskCount + " task each.");
	}

	@Override
	public void run() {
		for (long i=0; i<taskCount; i++) {
			for (int tQid : queueIds) {
				Task tTask = new Task(tQid, i);
				queues.enqueueTask(tTask);
			}
		}
		
	}
	
	
}
