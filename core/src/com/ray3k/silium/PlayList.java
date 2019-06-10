package com.ray3k.silium;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Array;

public class PlayList implements Music.OnCompletionListener {
    private int index;
    private Array<Music> musics;
    
    public PlayList(Array<Music> musics) {
        this.index = 0;
        this.musics = musics;
        
        for (Music music : musics) {
            music.setOnCompletionListener(this);
        }
    }
    
    @Override
    public void onCompletion(Music music) {
        index++;
        if (index >= musics.size) index = 0;
        
        musics.get(index).play();
    }
    
    public void play() {
        musics.get(index).stop();
        musics.get(index).play();
    }
    
    public void setVolume(float volume) {
        for (Music music : musics) {
            music.setVolume(volume);
        }
    }
}
