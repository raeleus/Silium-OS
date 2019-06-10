package com.ray3k.silium;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;

public class Core extends Game {
    public static Core instance;
    public SkeletonRenderer skeletonRenderer;
    public AssetManager assetManager;
    
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
        assetManager = new AssetManager(new InternalFileHandleResolver());
        assetManager.load("ui/silium-ui.json", Skin.class);
        assetManager.setLoader(SkeletonData.class, new SkeletonDataLoader(assetManager.getFileHandleResolver()));
        
        SkeletonDataLoader.SkeletonDataLoaderParameter parameter = new SkeletonDataLoader.SkeletonDataLoaderParameter("ui/silium-spine.atlas");
        assetManager.load("ui/logo.json", SkeletonData.class, parameter);
        assetManager.load("ui/loading.json", SkeletonData.class, parameter);
        assetManager.finishLoading();
    }
    
    @Override
    public void dispose() {
        super.dispose();
        
        assetManager.dispose();
    }
}
