package com.ray3k.silium;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.rafaskoberg.gdx.typinglabel.TypingAdapter;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class IntroScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private Music music;
    private boolean transitioning = false;
    
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), new TwoColorPolygonBatch());
        Gdx.input.setInputProcessor(stage);
        
        skin = Core.instance.assetManager.get("ui/silium-ui.json");
        
        music = Core.instance.assetManager.get("music/snd_theme_prophecy.mp3");
        music.setVolume(1f);
        music.setLooping(true);
        music.play();
    
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
    
        TypingLabel typingLabel = new TypingLabel("{EASE}{FAST}This game is part of Branching Corpses.\n\nA community-made game " +
                "series where anyone can extend on the multiple endings of previous games.\n\nWe strongly recommend you " +
                "click the link in the description to begin the story from the start.", skin, "button");
        typingLabel.setWrap(true);
        typingLabel.setAlignment(Align.center);
        typingLabel.setTypingListener(new TypingAdapter() {
            @Override
            public void end() {
                stage.addAction(Actions.delay(7, new Action() {
                    @Override
                    public boolean act(float delta) {
                        nextScreen();
                        return true;
                    }
                }));
            }
        });
        root.add(typingLabel).grow().maxWidth(600);
        
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                nextScreen();
                return super.keyDown(event, keycode);
            }
    
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                nextScreen();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }
    
    public void nextScreen() {
        if (!transitioning) {
            transitioning = true;
            stage.addAction(Actions.sequence(Actions.fadeOut(1f), new TemporalAction(1f) {
                @Override
                protected void update(float percent) {
                    music.setVolume(1 - percent);
                }
            }, Actions.delay(1f), new Action() {
                @Override
                public boolean act(float delta) {
                    Core.instance.setScreen(new MenuScreen());
                    return true;
                }
            }));
        }
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
        stage.dispose();
        music.stop();
    }
    
    @Override
    public void dispose() {
        stage.dispose();
    }
}
