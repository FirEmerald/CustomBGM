package firemerald.custombgm.common;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommonState
{
	public static final Queue<Runnable> QUEUED_ACTIONS = new ConcurrentLinkedQueue<>();
}