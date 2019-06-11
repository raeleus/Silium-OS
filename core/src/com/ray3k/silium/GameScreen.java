package com.ray3k.silium;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
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
    private String tty1Path;
    private Array<String> tty1Messages;
    private String tty2Path;
    private Array<String> tty2Messages;
    private enum TtyMode {
        DISABLED, NETWORK, SERVER, BRUTE_FORCE, VULNERABILITY, BLACK_WEB, NOC
    }
    private TtyMode tty1Mode;
    private TtyMode tty2Mode;
    private Table root;
    ButtonGroup<TextButton> tabButtonGroup;
    
    @Override
    public void show() {
        tty1Path = "/> ";
        tty1Messages = new Array<String>();
        tty1Messages.add("{FASTER}Type \"help\" and press enter to list available commands.");
        tty1Mode = TtyMode.NETWORK;
        
        tty2Path = "/> ";
        tty2Messages = new Array<String>();
        tty2Messages.add("{FASTER}Type \"help\" and press enter to list available commands.");
        tty2Mode = TtyMode.DISABLED;
        
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
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                DialogParameters.show(skin, stage);
            }
        });
    
        button = new Button(skin,"menu-close");
        table.add(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                changeScreen(new IntroScreen());
            }
        });
        
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
    
        Table subTable = new Table();
        subTable.setName("tty1-message-table");
        subTable.pad(3);
        final ScrollPane scrollPane = new ScrollPane(subTable, skin);
        scrollPane.setName("tty1-message-scroll");
        table.add(scrollPane).growX();
        
        for (int i = 0; i < tty1Messages.size; i++) {
            String message = tty1Messages.get(i);
        
            TypingLabel typingLabel = new TypingLabel(message, skin);
            typingLabel.setWrap(true);
            subTable.add(typingLabel).growX().colspan(2);
            typingLabel.skipToTheEnd();
            if (i != tty1Messages.size - 1) {
                subTable.row();
            } else if (tab != Tab.TTY1) {
                typingLabel.setTypingListener(new TypingAdapter() {
                    @Override
                    public void end() {
                        Table subTable = root.findActor("tty1-field-table");
                        subTable.addAction(Actions.fadeIn(.25f));
                    }
                });
            }
        }
    
        table.row();
        subTable = new Table();
        subTable.setName("tty1-field-table");
        table.add(subTable).grow();
        if (tab != Tab.TTY1) subTable.setColor(1, 1, 1, 0);
    
        subTable.defaults().padTop(5);
        Label label = new Label(tty1Path, skin);
        subTable.add(label).expandY().top();
    
        final TextField textField = new TextField("", skin);
        textField.setName("tty1-field");
        textField.setFocusTraversal(false);
        textField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                if (c == '\t') {
                    return false;
                } else {
                    return true;
                }
            }
        });
        subTable.add(textField).growX().expandY().top().minWidth(50);
        textField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    String command = textField.getText();
                    command = interpretCommand(tty1Mode, command);
                    tty1Messages.add(command);
    
                    Table subTable = root.findActor("tty1-message-table");
                    for (Actor actor : subTable.getChildren()) {
                        ((TypingLabel) actor).skipToTheEnd();
                    }
                    
                    subTable.row();
                    TypingLabel typingLabel = new TypingLabel(command, skin);
                    typingLabel.setWrap(true);
                    subTable.add(typingLabel).growX();
    
                    scrollPane.layout();
                    scrollPane.layout();
                    scrollPane.setScrollPercentY(1);
                    
                    textField.setText("");
                } else if (keycode == Input.Keys.TAB) {
                    textField.setText(autoComplete(tty1Mode, textField.getText()));
                    textField.setCursorPosition(textField.getText().length());
                } else if (keycode == Input.Keys.SPACE && Gdx.app.getType() == Application.ApplicationType.WebGL) {
                    int position = textField.getCursorPosition();
                    textField.setText(textField.getText().substring(0,position) + " " + textField.getText().substring(position));
                    textField.setCursorPosition(position + 1);
                    return true;
                }
                return super.keyDown(event, keycode);
            }
        });
    
        stage.setKeyboardFocus(root.findActor("tty1-field"));
        stage.setScrollFocus(root.findActor("tty1-message-scroll"));
        tab = Tab.TTY1;
    }
    
    private void changeScreen(final Screen screen) {
        transitioning = true;
        stage.addAction(Actions.sequence(Actions.fadeOut(1), Actions.parallel(new TemporalAction(1) {
            float currentVolume = Core.instance.playList.getCurrent().getVolume();
    
            @Override
            protected void update(float percent) {
                Core.instance.playList.setVolume((1 - percent) * currentVolume);
            }
        }, new TemporalAction(1) {
            float currentVolume = Core.instance.currentVoice.getVolume();
            
            @Override
            protected void update(float percent) {
                Core.instance.currentVoice.setVolume((1 - percent) * currentVolume);
            }
        }), new Action() {
            @Override
            public boolean act(float delta) {
                Core.instance.setScreen(screen);
                return true;
            }
        }));
    }
    
    private String interpretCommand(TtyMode ttyMode,  String text) {
        String returnValue = "";
        
        if (ttyMode == TtyMode.NETWORK) {
            if (text.equalsIgnoreCase("help")) {
                returnValue += "{FASTER}Welcome to Silium OS Command\n";
                returnValue += "The following commands can be entered while logged into the NETWORK:\n";
                returnValue += "help {COLOR=#FFFFFFAA}This command list{CLEARCOLOR}\n";
                returnValue += "clear {COLOR=#FFFFFFAA}Clears all text from the TTY{CLEARCOLOR}\n";
                returnValue += "brt <ip address> {COLOR=#FFFFFFAA}Initiate a brute force password hack on the specified IP address{CLEARCOLOR}\n";
                returnValue += "vul <ip address> {COLOR=#FFFFFFAA}Initiate a vulnerability hack on the specified IP address{CLEARCOLOR}\n";
                returnValue += "ssh <ip address> <username> <password> {COLOR=#FFFFFFAA}Connect to the system at the specified IP address{CLEARCOLOR}";
            } else if (text.equalsIgnoreCase("clear")) {
                if (tab == tab.TTY1) {
                    tty1Messages.clear();
                    createTTY1();
                }
            } else if (text.startsWith("brt")) {
                String[] split = text.split("\\s");
                if (split.length != 2 || !split[1].matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                    returnValue = "{FASTER}Incorrect paramaters for brt command. Type \"help\" and press enter to list available commands.";
                } else {
                    if (tab == Tab.TTY1) {
                        tty1Mode = TtyMode.BRUTE_FORCE;
                        animateBruteForce();
                    }
                }
            } else if (text.startsWith("vul")) {
        
            } else if (text.startsWith("ssh")) {
        
            } else {
                returnValue += "{FASTER}Unknown command: \"" + text + "\"\nType \"help\" and press enter to list available commands.";
            }
        } else if (ttyMode == TtyMode.SERVER) {
        
        } else {
        }
        
        return returnValue;
    }
    
    private String autoComplete(TtyMode ttyMode, String text) {
        String returnValue = text;
        
        if (!text.equals("")) {
            Array<String> commands = new Array<String>();
            if (ttyMode == TtyMode.NETWORK) {
                commands.add("help");
                commands.add("clear");
                commands.add("brt");
                commands.add("vul");
                commands.add("ssh");
            } else if (ttyMode == TtyMode.SERVER) {
    
            } else {
            }
    
            for (String command : commands) {
                if (command.toLowerCase().startsWith(text)) {
                    returnValue = command;
                    break;
                }
            }
        }
        
        return returnValue;
    }
    
    private void animateBruteForce() {
        SequenceAction sequenceAction = new SequenceAction();
        
        for (int i = 0; i < 200; i++) {
            if (tab == Tab.TTY1) {
                sequenceAction.addAction(new Action() {
                    @Override
                    public boolean act(float delta) {
                        String password = "{FASTER}" + Core.instance.passwords.random();
                        tty1Messages.add(password);
                        Table subTable = root.findActor("tty1-message-table");
                        subTable.row();
                        TypingLabel typingLabel = new TypingLabel(password, skin);
                        typingLabel.setWrap(true);
                        subTable.add(typingLabel).growX();
    
                        ScrollPane scrollPane = root.findActor("tty1-message-scroll");
                        scrollPane.layout();
                        scrollPane.layout();
                        scrollPane.setScrollPercentY(1);
                        return true;
                    }
                });
                sequenceAction.addAction(Actions.delay(.1f));
            }
        }
        
        final String user = Core.instance.users.random();
        final String password = Core.instance.passwords.random();
    
        if (tab == Tab.TTY1) {
            sequenceAction.addAction(new Action() {
                @Override
                public boolean act(float delta) {
                    String message = "{FASTER}Successfully Hacked.\nUsername is " + user + "\nPassword is " + password;
                    tty1Messages.add(message);
                    Table subTable = root.findActor("tty1-message-table");
                    subTable.row();
                    TypingLabel typingLabel = new TypingLabel(message, skin);
                    typingLabel.setWrap(true);
                    subTable.add(typingLabel).growX();
                
                    ScrollPane scrollPane = root.findActor("tty1-message-scroll");
                    scrollPane.layout();
                    scrollPane.layout();
                    scrollPane.setScrollPercentY(1);
                    return true;
                }
            });
        }
        
        stage.addAction(sequenceAction);
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
            stage.setScrollFocus(null);
        }
    }
    
    private void createNotes() {
        TextButton textButton = root.findActor("notes-button");
        textButton.setChecked(true);
        
        stage.setKeyboardFocus(root.findActor("notes-area"));
        stage.setScrollFocus(null);
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
                if (tab != Tab.TTY1) createTTY1();
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
        Core.instance.playList.stop();
    }
    
    @Override
    public void dispose() {
        stage.dispose();
    }
}
