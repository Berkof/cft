package qp;

/**
 * Описатель задачи
 * @author sbelyak
 *
 */
public class Task {

	private long itemId;	// Идентификатор задачи, уникальный в пределах очереди
	private int queueId;	// Идентификатор очереди, в которой должна обрабатываться заявка
	
	public Task() {
		
	}
	
	public Task(int queueId, long itemId) {
		this.queueId = queueId;
		this.itemId = itemId;
	}
	
	public long getItemId() {
		return itemId;
	}
	public void setItemId(long itemId) {
		this.itemId = itemId;
	}
	public int getQueueId() {
		return queueId;
	}
	public void setQueueId(int queueId) {
		this.queueId = queueId;
	}
	
	@Override
	public String toString() {
		return "Task: itemId=" + itemId + ", queueId=" + queueId;
	}
	
}
