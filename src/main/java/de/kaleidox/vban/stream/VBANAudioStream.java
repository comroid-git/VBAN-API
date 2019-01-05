package de.kaleidox.vban.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import de.kaleidox.vban.model.AudioRecievedHandler;

import sun.audio.AudioData;

public abstract class VBANAudioStream {
    private final Queue<AudioData> dataQueue;
    private final ExecutorService executor;
    private List<AudioRecievedHandler> handlers;

    protected VBANAudioStream(ExecutorService executor) {
        this.executor = executor;

        handlers = new ArrayList<>();
        dataQueue = new LinkedBlockingQueue<>();

        executor.execute(new DataDistributionThread());
    }

    protected synchronized AudioData feed(byte[] bytes) {
        synchronized (dataQueue) {
            AudioData audioData = new AudioData(bytes);
            dataQueue.add(audioData);
            dataQueue.notify();
            return audioData;
        }
    }

    public VBANAudioStream addAudioRecievedHandler(AudioRecievedHandler audioRecievedHandler) {
        handlers.add(audioRecievedHandler);
        return this;
    }

    private class DataDistributionThread implements Runnable {
        @SuppressWarnings("InfiniteLoopStatement")
        @Override
        public void run() {
            while (true) {
                synchronized (dataQueue) {
                    try {
                        while (dataQueue.isEmpty()) dataQueue.wait();
                        final AudioData poll = dataQueue.poll();
                        for (final AudioRecievedHandler handler : handlers) {
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    handler.onAudioRecieve(poll);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
