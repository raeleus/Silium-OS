package com.ray3k.silium;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;

public class Core extends Game {
    public Array<String> users;
    public Array<String> passwords;
    public Array<String> vulnerabilities;
    public Array<String> userFilePaths;
    public Array<String> userFileContents;
    public Array<String> cultistFilePaths;
    public Array<String> cultistFileContents;
    public Array<String> userRewardPaths;
    public Array<String> userRewardContents;
    public Array<String> cultistRewardPaths;
    public Array<String> cultistRewardContents;
    public static Core instance;
    public SkeletonRenderer skeletonRenderer;
    public AssetManager assetManager;
    public PlayList playList;
    public Music currentVoice;
    public float bgmVolume = .25f;
    public float sfxVolume = 1f;
    public CrossPlatformWorker crossPlatformWorker;
    
    public final static String VERSION = "1.0";
    @Override
    public void create () {
        instance = this;
        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(true);
        
        addAssets();
        
        setScreen(new GameScreen());
    }
    
    private void addAssets() {
        users = new Array<String>(Gdx.files.internal("data/users.txt").readString().replaceAll("\\r", "").split("\\n"));
        passwords = new Array<String>(Gdx.files.internal("data/passwords.txt").readString().replaceAll("\\r", "").split("\\n"));
        vulnerabilities = new Array<String>(Gdx.files.internal("data/vulnerabilities.txt").readString().replaceAll("\\r", "").split("\\n"));
    
        JsonReader jsonReader = new JsonReader();
        
        JsonValue json = jsonReader.parse(Gdx.files.internal("data/user-files.json"));
        userFilePaths = new Array<String>();
        userFileContents = new Array<String>();
        for (JsonValue child : json.iterator()) {
            userFilePaths.add(child.name);
            userFileContents.add(child.asString());
        }
    
        json = jsonReader.parse(Gdx.files.internal("data/user-rewards.json"));
        userRewardPaths = new Array<String>();
        userRewardContents = new Array<String>();
        for (JsonValue child : json.iterator()) {
            userRewardPaths.add(child.name);
            userRewardContents.add(child.asString());
        }
//
//        json = jsonReader.parse(Gdx.files.internal("data/cultist-files.json"));
//        cultistFilePaths = new Array<String>();
//        cultistFileContents = new Array<String>();
//        for (JsonValue child : json.iterator()) {
//            cultistFilePaths.add(child.name);
//            cultistFileContents.add(child.asString());
//        }
//
//        json = jsonReader.parse(Gdx.files.internal("data/cultist-rewards.json"));
//        cultistRewardPaths = new Array<String>();
//        cultistRewardContents = new Array<String>();
//        for (JsonValue child : json.iterator()) {
//            cultistRewardPaths.add(child.name);
//            cultistRewardContents.add(child.asString());
//        }
        
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
        assetManager.load("music/snd_theme_prophecy.mp3", Music.class);
    
        for (int i = 1; i <= 25; i++) {
            assetManager.load("voice/" + i + "-Mix.mp3", Music.class);
        }
        
        assetManager.finishLoading();
    
        Array<Music> musics = new Array<Music>();
        musics.add(assetManager.get("music/01 Evil Has Been Done.mp3", Music.class));
        musics.add(assetManager.get("music/02 Relinquish Your Hold.mp3", Music.class));
        musics.add(assetManager.get("music/03 In The Shadows They Hide.mp3", Music.class));
        musics.add(assetManager.get("music/04 He Eludes Us.mp3", Music.class));
        musics.add(assetManager.get("music/05 To Seek Find And Capture.mp3", Music.class));
        playList = new PlayList(musics);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        
        assetManager.dispose();
    }
    
    public Music playVoice(int number) {
        Music music = assetManager.get("voice/" + Integer.toString(number) + "-Mix.mp3");
        music.setVolume(Core.instance.sfxVolume);
        music.play();
        currentVoice = music;
        return music;
    }
}
