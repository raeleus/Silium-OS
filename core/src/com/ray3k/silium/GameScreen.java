package com.ray3k.silium;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.rafaskoberg.gdx.typinglabel.TypingAdapter;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class GameScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private enum Mode {
        BEGIN, TARGETS_OF_INTEREST
    }
    private Mode mode;
    private boolean transitioning;
    private enum Tab {
        TTY1, TTY2, NOTES
    }
    private Tab tab;
    private Table root;
    ButtonGroup<TextButton> tabButtonGroup;
    
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), new TwoColorPolygonBatch());
        Gdx.input.setInputProcessor(stage);
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.F1) {
                    createTTY1();
                    return true;
                } else if (keycode == Input.Keys.F2) {
                    createTTY2();
                    return true;
                } else if (keycode == Input.Keys.F3) {
                    createNotes();
                    return true;
                }
                return super.keyDown(event, keycode);
            }
        });
        
        skin = Core.instance.assetManager.get("ui/silium-ui.json");
    
        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        Table table = new Table();
        table.setBackground(skin.getDrawable("menu-bar-tinted"));
        root.add(table).growX();
    
        TypingLabel typingLabel = new TypingLabel("Silium OS ver. " + Core.VERSION,skin,"caption");
        table.add(typingLabel).expandX().left().padLeft(6);
    
        Button button = new Button(skin,"menu-settings");
        table.add(button);
    
        button = new Button(skin,"menu-close");
        table.add(button);
        
        root.row();
        Table bottom = new Table();
        root.add(bottom).grow();
        
        table = new Table();
        bottom.add(table).growY().width(300);
        
        Table subTable = new Table();
        table.add(subTable);
        
        Image image = new Image(skin,"icon-kreddits");
        image.setName("icon-kreddits");
        image.setColor(skin.getColor("ui"));
        subTable.add(image);
        
        Label label = new Label("$300",skin);
        subTable.add(label);
    
        image = new Image(skin,"icon-data-points");
        image.setName("icon-data-points");
        image.setColor(skin.getColor("ui"));
        subTable.add(image).spaceLeft(10);
    
        label = new Label("X5",skin);
        subTable.add(label);
    
        image = new Image(skin,"icon-firewall-tinted");
        subTable.add(image).spaceLeft(10);
    
        label = new Label("X5",skin);
        subTable.add(label);
    
        image = new Image(skin,"icon-proxy-tinted");
        subTable.add(image).spaceLeft(10);
    
        label = new Label("X5",skin);
        subTable.add(label);
        
        table.row();
        table.add().growY();
        
        table = new Table();
        table.setBackground(skin.getDrawable("table-with-tab-tinted"));
        bottom.add(table).grow();
    
        tabButtonGroup = new ButtonGroup<TextButton>();
        
        TextButton textButton = new TextButton("TTY1", skin, "tab");
        textButton.setName("tty1-button");
        textButton.setProgrammaticChangeEvents(false);
        table.add(textButton);
        tabButtonGroup.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                createTTY1();
            }
        });
    
        textButton = new TextButton("TTY2", skin, "tab");
        textButton.setName("tty2-button");
        textButton.setProgrammaticChangeEvents(false);
        table.add(textButton);
        tabButtonGroup.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                createTTY2();
            }
        });
        
        table.add().expandX();
        
        table.row();
        subTable = new Table();
        subTable.setName("tty-container");
        table.add(subTable).grow().padTop(8).colspan(3);
        
        table = new Table();
        table.setBackground(skin.getDrawable("table-with-tab-tinted"));
        bottom.add(table).fillX().growY().maxWidth(200);
        
        textButton = new TextButton("Notes", skin, "tab");
        textButton.setName("notes-button");
        textButton.setProgrammaticChangeEvents(false);
        table.add(textButton).left();
        tabButtonGroup.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                createNotes();
            }
        });
    
        table.row();
        TextArea textArea = new TextArea("",skin);
        textArea.setName("notes-area");
        table.add(textArea).grow().padTop(8).minWidth(50);
        textArea.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                TextButton textButton = root.findActor("notes-button");
                textButton.setChecked(true);
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        
        animationBegin();
    }
    
    private void createTTY1() {
        TextButton textButton = root.findActor("tty1-button");
        textButton.setChecked(true);
        
        if (tab != Tab.TTY1) {
            tab = Tab.TTY1;
            
            Table table = root.findActor("tty-container");
            table.clear();
            table.setTouchable(Touchable.enabled);
            table.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    TextButton textButton = root.findActor("tty1-button");
                    textButton.setChecked(true);
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
            
            TypingLabel typingLabel = new TypingLabel("{FASTER}Type \"help\" and press enter to list available commands.", skin);
            typingLabel.setWrap(true);
            table.add(typingLabel).growX().colspan(2);
    
            table.row();
            final Table subTable = new Table();
            table.add(subTable).grow();
            subTable.setColor(1, 1, 1, 0);
            typingLabel.setTypingListener(new TypingAdapter() {
                @Override
                public void end() {
                    subTable.addAction(Actions.fadeIn(.25f));
                }
            });
            
            subTable.defaults().padTop(5);
            Label label = new Label("root/documents> ", skin);
            subTable.add(label).expandY().top();
    
            TextField textField = new TextField("", skin);
            textField.setName("tty1-field");
            subTable.add(textField).growX().expandY().top().minWidth(50);
            
            stage.setKeyboardFocus(root.findActor("tty1-field"));
        }
    }
    
    private void createTTY2() {
        TextButton textButton = root.findActor("tty2-button");
        textButton.setChecked(true);
        
        if (tab != Tab.TTY2) {
            tab =  Tab.TTY2;
    
            Table table = root.findActor("tty-container");
            table.clear();
            table.setTouchable(Touchable.enabled);
            table.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    TextButton textButton = root.findActor("tty2-button");
                    textButton.setChecked(true);
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
    
            TypingLabel typingLabel = new TypingLabel("{FASTER}TTY2 is not available. Upgrade your systems to add this capability.", skin);
            typingLabel.setWrap(true);
            table.add(typingLabel).growX().colspan(2).expandY().top();
            
            stage.setKeyboardFocus(null);
        }
    }
    
    private void createNotes() {
        TextButton textButton = root.findActor("notes-button");
        textButton.setChecked(true);
        
        stage.setKeyboardFocus(root.findActor("notes-area"));
    }
    
    private void animationBegin() {
        mode = Mode.BEGIN;
        transitioning = true;
        stage.getRoot().setColor(1,1,1,0);
        stage.addAction(Actions.sequence(Actions.fadeIn(.5f), new Action() {
            @Override
            public boolean act(float delta) {
                transitioning = false;
                mode = Mode.TARGETS_OF_INTEREST;
                Core.instance.playVoice(3);
                return true;
            }
        }, Actions.delay(6), new Action() {
            @Override
            public boolean act(float delta) {
                Core.instance.playVoice(4);
                Image image = root.findActor("icon-data-points");
                image.addAction(Actions.color(skin.getColor("red"), .25f));
                return true;
            }
        }, Actions.delay(3), new Action() {
            @Override
            public boolean act(float delta) {
                Image image = root.findActor("icon-data-points");
                image.addAction(Actions.color(skin.getColor("ui"), .25f));
                return true;
            }
        }, new Action() {
            @Override
            public boolean act(float delta) {
                Image image = root.findActor("icon-kreddits");
                image.addAction(Actions.color(skin.getColor("red"), .5f));
                return true;
            }
        }, Actions.delay(4), new Action() {
            @Override
            public boolean act(float delta) {
                Image image = root.findActor("icon-kreddits");
                image.addAction(Actions.color(skin.getColor("ui"), .5f));
                return true;
            }
        }, Actions.delay(2), new Action() {
            @Override
            public boolean act(float delta) {
                createTTY1();
                return true;
            }
        }));
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
