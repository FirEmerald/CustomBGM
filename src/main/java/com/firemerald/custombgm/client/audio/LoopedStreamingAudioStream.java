package com.firemerald.custombgm.client.audio;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.LoopingAudioStream.AudioStreamProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LoopedStreamingAudioStream implements AudioStream {
    private final AudioStreamProvider provider;
    private AudioStream stream;
    private final BufferedInputStream bufferedInputStream;

	private final int loopStart, loopEnd;
	private int position = 0;

    public LoopedStreamingAudioStream(AudioStreamProvider provider, InputStream inputStream, int loopStart, int loopEnd) throws IOException {
        this.provider = provider;
        this.bufferedInputStream = new BufferedInputStream(inputStream);
        this.bufferedInputStream.mark(Integer.MAX_VALUE);
        this.stream = provider.create(new NoCloseBuffer(this.bufferedInputStream));
        this.loopStart = toBytePosition(loopStart);
        this.loopEnd = toBytePosition(loopEnd);
    }

    @Override
    public AudioFormat getFormat() {
        return this.stream.getFormat();
    }

	private int toBytePosition(int samples) {
		return (getFormat().getChannels() * getFormat().getSampleSizeInBits() * samples) / 8;
	}

    /**
     * Reads audio data from the stream and returns a byte buffer containing at most the specified number of bytes.
     * The method reads audio frames from the stream and adds them to the output buffer until the buffer contains at least the specified number of bytes or the end fo the stream is reached.
     * @return a byte buffer containing at most the specified number of bytes to read
     * @throws IOException if an I/O error occurs while reading the audio data
     *
     * @param size the maximum number of bytes to read
     */
    @Override
    public ByteBuffer read(int size) throws IOException {
    	int toRead = loopEnd > 0 ? Math.min(size, loopEnd - position) : size;
		ByteBuffer bytebuffer = null;
		if ((loopEnd > 0 && position >= loopEnd) || !(bytebuffer = this.stream.read(toRead)).hasRemaining()) { //if past end OR end of stream, re-open stream
        	position = 0; //reset position
            this.stream.close();
            this.bufferedInputStream.reset();
            this.stream = this.provider.create(new NoCloseBuffer(this.bufferedInputStream));
            while (position <= loopStart) { //seek to past loop start position
            	bytebuffer = this.stream.read(size + (loopStart - position)); //read up till the point of size PAST loop start
            	if (!bytebuffer.hasRemaining()) return bytebuffer; //prevent issues
            	position += bytebuffer.limit();
            }
            if (bytebuffer != null) { //remove leading bytes
            	bytebuffer.position(loopStart - (position - bytebuffer.limit())); //skip loopStart - position we were at before this buffer was read
            } else { //should not happen
            	bytebuffer = this.stream.read(size);
            	position += bytebuffer.limit();
            }
		}
		else position += bytebuffer.limit();
		if (loopEnd > 0 && position > loopEnd) { //trim trailing bytes
			bytebuffer.limit(bytebuffer.limit() - (position - loopEnd));
			position = loopEnd;
		}
        return bytebuffer;
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
        this.bufferedInputStream.close();
    }

    /**
     * A {@linkplain FilterInputStream} that does not close the underlying {@linkplain InputStream}.
     */
    @OnlyIn(Dist.CLIENT)
    static class NoCloseBuffer extends FilterInputStream {
        NoCloseBuffer(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public void close() {
        }
    }
}
