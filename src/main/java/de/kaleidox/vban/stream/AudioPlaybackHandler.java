package de.kaleidox.vban.stream;

import de.kaleidox.vban.model.AudioRecievedHandler;

import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.ContinuousAudioDataStream;

public class AudioPlaybackHandler implements AudioRecievedHandler {
    private final AudioPlayer player;

    public AudioPlaybackHandler(AudioPlayer player) {
        this.player = player;
    }

    @Override
    public void onAudioRecieve(AudioData audioData) {
        ContinuousAudioDataStream stream = new ContinuousAudioDataStream(audioData);

        player.start(stream);
    }
}
