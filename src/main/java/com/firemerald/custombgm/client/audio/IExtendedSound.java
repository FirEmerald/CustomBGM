package com.firemerald.custombgm.client.audio;

public interface IExtendedSound
{
	public int getLoopStart();

	public int getLoopEnd();

	public void setLoopStart(int start);

	public void setLoopEnd(int end);
}