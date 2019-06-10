package com.ray3k.silium;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;

public class Core extends Game {
    public static Core instance;
    public SkeletonRenderer skeletonRenderer;
    public AssetManager assetManager;
    public PlayList playList;
    
    public final static String VERSION = "1.0";
    @Override
    public void create () {
        instance = this;
        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(true);
        
        addAssets();
        
        setScreen(new MenuScreen());
    }
    
    private void addAssets() {
        assetManager = new AssetManager(new InternalFileHandleResolver());
        assetManager.load("ui/silium-ui.json", Skin.class);
        assetManager.setLoader(SkeletonData.class, new SkeletonDataLoader(assetManager.getFileHandleResolver()));
        
        SkeletonDataLoader.SkeletonDataLoaderParameter parameter = new SkeletonDataLoader.SkeletonDataLoaderParameter("ui/silium-spine.atlas");
        assetManager.load("ui/logo.json", SkeletonData.class, parameter);
        assetManager.load("ui/loading.json", SkeletonData.class, parameter);
        
        assetManager.load("music/01 Evil Has Been Done.mp3", Music.class);
        assetManager.load("music/02 Relinquish Your Hold.mp3", Music.class);
        assetManager.load("music/03 In The Shadows They Hide.mp3", Music.class);
        assetManager.load("music/04 He Eludes Us.mp3", Music.class);
        assetManager.load("music/05 To Seek Find And Capture.mp3", Music.class);
        
        assetManager.finishLoading();
    
        Array<Music> musics = new Array<Music>();
        musics.add(assetManager.get("music/01 Evil Has Been Done.mp3", Music.class));
        musics.add(assetManager.get("music/02 Relinquish Your Hold.mp3", Music.class));
        musics.add(assetManager.get("music/03 In The Shadows They Hide.mp3", Music.class));
        musics.add(assetManager.get("music/04 He Eludes Us.mp3", Music.class));
        musics.add(assetManager.get("music/05 To Seek Find And Capture.mp3", Music.class));
        playList = new PlayList(musics);
        playList.setVolume(.1f);
        playList.play();
    }
    
    @Override
    public void dispose() {
        super.dispose();
        
        assetManager.dispose();
    }
}
