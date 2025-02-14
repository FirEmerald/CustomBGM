package com.firemerald.custombgm.client.audio;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

import org.lwjgl.BufferUtils;

import net.minecraft.client.sounds.AudioStream;

public class LoopedPreloadedAudioStream implements AudioStream {
	private final ByteBuffer preloadedAudioData;
	private final AudioFormat audioFormat;

	private final int loopStart, loopEnd;
	private int position = 0;

    public LoopedPreloadedAudioStream(ByteBuffer preloadedAudioData, AudioFormat audioFormat, int loopStart, int loopEnd) {
        this.preloadedAudioData = preloadedAudioData;
        this.audioFormat = audioFormat;
        int loopStart2 = toBytePosition(loopStart);
        int loopEnd2 = toBytePosition(loopEnd);
        if (loopStart2 < 0) loopStart2 = 0;
        if (loopEnd2 > preloadedAudioData.limit()) loopEnd2 = preloadedAudioData.limit();
        this.loopStart = loopStart2;
        this.loopEnd = loopEnd2;
    }

    @Override
    public AudioFormat getFormat() {
        return audioFormat;
    }

	private int toBytePosition(int samples) {
		return (getFormat().getChannels() * getFormat().getSampleSizeInBits() * samples) / 8;
	}

    @Override
    public ByteBuffer read(int size) throws IOException {
    	ByteBuffer ret = BufferUtils.createByteBuffer(size);
    	int read = 0;
    	int needs = size;
    	while (needs > 0) {
    		int toRead = Math.min(needs, loopEnd - position);
    		ret.put(read, preloadedAudioData, position, toRead);
    		needs -= toRead;
    		read += toRead;
    		position += toRead;
    		if (position >= loopEnd) position = loopStart;
    	}
    	return ret;
    }

    @Override
    public void close() throws IOException {}
}
