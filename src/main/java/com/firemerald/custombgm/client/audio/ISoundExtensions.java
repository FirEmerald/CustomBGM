package com.firemerald.custombgm.client.audio;

public interface ISoundExtensions {
	public void setLoop(int start, int end);

	public int loopStart();

	public int loopEnd();

	public boolean hasLoop();
}
