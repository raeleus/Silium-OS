package com.ray3k.silium;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.rafaskoberg.gdx.typinglabel.TypingAdapter;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class GameOverAScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private boolean transitioning = false;
    
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), new TwoColorPolygonBatch());
        Gdx.input.setInputProcessor(stage);
        
        skin = Core.instance.assetManager.get("ui/silium-ui.json");
        Core.instance.playVoice(24);
    
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
    
        TypingLabel typingLabel = new TypingLabel("{SPEED=0.4}Operator, the cultist army has been defeated. Warning, new threats detected. Humanity and all of monster kind pose immediate threat to prime directive. New directive initiated: eliminate all primitive, organic life.\nExecute.\n\nEnding A", skin, "button-red");
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
                    Core.instance.currentVoice.setVolume(1 - percent);
                }
            }, Actions.delay(1f), new Action() {
                @Override
                public boolean act(float delta) {
                    Core.instance.setScreen(new IntroScreen());
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
        Core.instance.currentVoice.stop();
    }
    
    @Override
    public void dispose() {
        stage.dispose();
    }
}
