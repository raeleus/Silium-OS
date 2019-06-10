package com.ray3k.silium;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class MenuScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private SpineDrawable loadingDrawable;
    private Table buttonTable;
    
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), new TwoColorPolygonBatch());
        Gdx.input.setInputProcessor(stage);
        
        skin = Core.instance.assetManager.get("ui/silium-ui.json");
        
        if (!Core.instance.playList.isPlaying()) {
            Core.instance.playList.play();
        }
        
        Core.instance.playVoice(1).setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                loadingDrawable.getAnimationState().setAnimation(1, "hide", false);
            }
        });
    
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
    
        SpineDrawable.SpineDrawableTemplate template = new SpineDrawable.SpineDrawableTemplate();
        SpineDrawable spineDrawable = new SpineDrawable(Core.instance.assetManager.get("ui/logo.json", SkeletonData.class),Core.instance.skeletonRenderer, template);
        spineDrawable.getAnimationState().setAnimation(0, "animation", false);
        Image image = new Image(spineDrawable);
        root.add(image);
        spineDrawable.getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                loadingDrawable.getAnimationState().setAnimation(0, "show", false);
                loadingDrawable.getAnimationState().addAnimation(0, "animation", true, 0);
            }
        });
        
        root.row();
        Stack stack = new Stack();
        root.add(stack);
        
        template = new SpineDrawable.SpineDrawableTemplate();
        loadingDrawable = new SpineDrawable(Core.instance.assetManager.get("ui/loading.json", SkeletonData.class),Core.instance.skeletonRenderer, template);
        loadingDrawable.getAnimationState().setAnimation(0,"invisible",false);
        image = new Image(loadingDrawable);
        image.setScaling(Scaling.none);
        stack.add(image);
        loadingDrawable.getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if (entry.getAnimation().getName().equals("hide")) {
                    buttonTable.addAction(Actions.sequence(Actions.fadeIn(1.0f), Actions.touchable(Touchable.enabled)));
                }
            }
        });
        
        buttonTable = new Table();
        buttonTable.setColor(1, 1, 1, 0);
        buttonTable.setTouchable(Touchable.disabled);
        stack.add(buttonTable);
    
        buttonTable.defaults().uniform().fill();
        TextButton textButton = new TextButton("Mission Details", skin);
        buttonTable.add(textButton);
    
        textButton = new TextButton("Engage", skin);
        buttonTable.add(textButton);
    
        textButton = new TextButton("Parameters", skin);
        buttonTable.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Core.instance.playVoice(2);
                DialogParameters.show(skin,stage);
            }
        });
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
