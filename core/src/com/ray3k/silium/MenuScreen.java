package com.ray3k.silium;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class MenuScreen implements Screen {
    private Stage stage;
    private Skin skin;
    
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), new TwoColorPolygonBatch());
        Gdx.input.setInputProcessor(stage);
        
        skin = Core.instance.assetManager.get("ui/silium-ui.json");
    
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
    
        SpineDrawable.SpineDrawableTemplate template = new SpineDrawable.SpineDrawableTemplate();
        SpineDrawable spineDrawable = new SpineDrawable(Core.instance.assetManager.get("ui/logo.json", SkeletonData.class),Core.instance.skeletonRenderer, template);
        spineDrawable.getAnimationState().addAnimation(0, "animation", false, 0);
        Image image = new Image(spineDrawable);
        root.add(image);
        
        root.row();
        template = new SpineDrawable.SpineDrawableTemplate();
        spineDrawable = new SpineDrawable(Core.instance.assetManager.get("ui/loading.json", SkeletonData.class),Core.instance.skeletonRenderer, template);
        spineDrawable.getAnimationState().setAnimation(0,"show",false);
        spineDrawable.getAnimationState().addAnimation(0, "animation", false, 0);
        spineDrawable.getAnimationState().addAnimation(0, "animation", false, 0);
        spineDrawable.getAnimationState().addAnimation(0, "hide", false, 0);
        image = new Image(spineDrawable);
        root.add(image);
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            dispose();
            show();
        }
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void pause() {
    
    }
    
    @Override
    public void resume() {
    
    }
    
    @Override
    public void hide() {
    
    }
    
    @Override
    public void dispose() {
        stage.dispose();
    }
}
