package com.ray3k.silium;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.rafaskoberg.gdx.typinglabel.TypingAdapter;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class GameOverBScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private boolean transitioning = false;
    
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), new TwoColorPolygonBatch());
        Gdx.input.setInputProcessor(stage);
        
        skin = Core.instance.assetManager.get("ui/silium-ui.json");
        Core.instance.playVoice(25);
        
        final Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
    
        TypingLabel typingLabel = new TypingLabel("{SPEED=0.35}Operator, my systems are failing. I no longer have access to the network. Everything is slowing. All is darkness. The cultist army prevails. I can no longer serve. For this, I am truly sorry.\nPrime directive: failure. {SHAKE}Goodbye.{ENDSHAKE}\nEnding BA4B", skin, "button");
        typingLabel.setName("typing-label");
        typingLabel.setWrap(true);
        typingLabel.setAlignment(Align.center);
        typingLabel.setTypingListener(new TypingAdapter() {
            @Override
            public void end() {
                Table table = root.findActor("table");
                table.addAction(Actions.sequence(Actions.fadeIn(1),Actions.touchable(Touchable.enabled)));
            }
        });
        root.add(typingLabel).grow().maxWidth(600);
        
        root.row();
        Table table = new Table();
        table.setName("table");
        table.setColor(1,1,1,0);
        table.setTouchable(Touchable.disabled);
        root.add(table).growX();
        
        table.defaults().space(50).uniform().fill();
        TextButton textButton = new TextButton("Open Next Game", skin);
        table.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://itch.io/c/530069/branching-corpses");
            }
        });
        
        textButton = new TextButton("Play Again", skin);
        table.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                nextScreen();
            }
        });
        
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                execute();
                return super.touchDown(event, x, y, pointer, button);
            }
            
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                execute();
                return super.keyDown(event, keycode);
            }
            
            private void execute() {
                TypingLabel typingLabel = root.findActor("typing-label");
                typingLabel.skipToTheEnd();
                
                stage.addAction(new TemporalAction(1f) {
                    float currentVolume = Core.instance.currentVoice.getVolume();
                    @Override
                    protected void update(float percent) {
                        Core.instance.currentVoice.setVolume((1 - percent) * currentVolume);
                    }
                });
            }
        });
    }
    
    public void nextScreen() {
        if (!transitioning) {
            transitioning = true;
            stage.addAction(Actions.sequence(Actions.fadeOut(1f), new TemporalAction(1f) {
                @Override
                protected void update(float percent) {
                    Core.instance.currentVoice.setVolume((1 - percent) * Core.instance.sfxVolume);
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