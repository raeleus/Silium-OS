package com.ray3k.silium;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.rafaskoberg.gdx.typinglabel.TypingAdapter;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import java.util.Iterator;

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
    private Array<Server> servers;
    private Server connectedServer;
    private int tutorialLevel;
    private int proxies;
    private int kreddits;
    private int firewalls;
    private int dataPoints;
    private int upgrades;
    private boolean vulnerabilityModule;
    
    @Override
    public void show() {
        proxies = 1;
        kreddits = 0;
        firewalls = 2;
        dataPoints = 10;
        upgrades = 0;
        
        tutorialLevel = 0;
        servers = new Array<Server>();
        refreshServers();
        
        tty1Path = "";
        tty1Messages = new Array<String>();
        tty1Messages.add("{FASTER}Type \"help\" and press enter to list available commands.");
        tty1Mode = TtyMode.NETWORK;
        
        tty2Path = "";
        tty2Messages = new Array<String>();
        tty2Messages.add("{FASTER}Type \"help\" and press enter to list available commands.");
        tty2Mode = TtyMode.DISABLED;
        
        stage = new Stage(new ScreenViewport());
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
        table.add(subTable).growX();
        
        subTable.defaults().expandX();
        Image image = new Image(skin,"icon-kreddits");
        image.setName("icon-kreddits");
        image.setColor(skin.getColor("ui"));
        subTable.add(image);
        
        Label label = new Label("$" + kreddits, skin, "white");
        label.setName("label-kreddits");
        label.setColor(skin.getColor("ui"));
        subTable.add(label);
    
        image = new Image(skin,"icon-data-points");
        image.setName("icon-data-points");
        image.setColor(skin.getColor("ui"));
        subTable.add(image).spaceLeft(10);
    
        label = new Label("X" + dataPoints, skin, "white");
        label.setName("label-data-points");
        label.setColor(skin.getColor("ui"));
        subTable.add(label);
    
        image = new Image(skin,"icon-firewall");
        image.setName("icon-firewall");
        image.setColor(skin.getColor("ui"));
        subTable.add(image).spaceLeft(10);
    
        label = new Label("X" + firewalls, skin, "white");
        label.setName("label-firewall");
        label.setColor(skin.getColor("ui"));
        subTable.add(label);
    
        image = new Image(skin,"icon-proxy");
        image.setName("icon-proxy");
        image.setColor(skin.getColor("ui"));
        subTable.add(image).spaceLeft(10);
    
        label = new Label("X" + proxies, skin, "white");
        label.setName("label-proxy");
        label.setColor(skin.getColor("ui"));
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
    
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.SPACE && Gdx.app.getType() == Application.ApplicationType.WebGL) {
                    TextArea textArea = root.findActor("notes-area");
                    int position = textArea.getCursorPosition();
                    textArea.setText(textArea.getText().substring(0,position) + " " + textArea.getText().substring(position));
                    textArea.setCursorPosition(position + 1);
                    return true;
                }
                return super.keyDown(event, keycode);
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
        Label label = new Label("/" + tty1Path + ">", skin);
        label.setName("tty1-path-label");
        subTable.add(label).expandY().top();
    
        final TextField textField = new TextField("", skin);
        textField.setName("tty1-field");
        textField.setFocusTraversal(false);
        final KeyFilter keyFilter = new KeyFilter();
        textField.setTextFieldFilter(keyFilter);
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
                } else if (keycode == Input.Keys.SPACE) {
                    int position = textField.getCursorPosition();
                    keyFilter.acceptSpace = true;
                    textField.setText(textField.getText().substring(0,position) + " " + textField.getText().substring(position));
                    keyFilter.acceptSpace = false;
                    textField.setCursorPosition(position + 1);
                    return true;
                } else if (keycode == Input.Keys.CONTROL_LEFT || keycode == Input.Keys.CONTROL_RIGHT) {
                    keyFilter.acceptSpace = true;
                }
                return super.keyDown(event, keycode);
            }
    
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.CONTROL_LEFT || keycode == Input.Keys.CONTROL_RIGHT) {
                    keyFilter.acceptSpace = false;
                }
                return super.keyUp(event, keycode);
            }
        });
    
        stage.setKeyboardFocus(root.findActor("tty1-field"));
        stage.setScrollFocus(root.findActor("tty1-message-scroll"));
        tab = Tab.TTY1;
    }
    
    private class KeyFilter implements TextField.TextFieldFilter {
        boolean acceptSpace = false;
        
        @Override
        public boolean acceptChar(TextField textField, char c) {
            if (c == '\t') {
                return false;
            } if (c == ' ') {
                return acceptSpace;
            } else {
                return true;
            }
        }
    }
    
    private void changeScreen(final Screen screen) {
        changeScreen(screen,0);
    }
    
    private void changeScreen(final Screen screen, float delay) {
        transitioning = true;
        stage.addAction(Actions.sequence(Actions.delay(delay), Actions.fadeOut(1), Actions.parallel(new TemporalAction(1) {
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
        String returnValue = text + "\n";
        
        if (ttyMode == TtyMode.NETWORK) {
            if (text.equalsIgnoreCase("help")) {
                returnValue += "{FASTER}Welcome to Silium OS Command\n";
                returnValue += "The following commands can be entered while logged into the network:\n";
                returnValue += "help {COLOR=#FFFFFFAA}This command list{CLEARCOLOR}\n";
                returnValue += "clear {COLOR=#FFFFFFAA}Clears all text from the TTY{CLEARCOLOR}\n";
                returnValue += "nmap {COLOR=#FFFFFFAA}Lists all servers connected on the network{CLEARCOLOR}\n";
                returnValue += "brt <ip address> {COLOR=#FFFFFFAA}Initiate a brute force password hack on the specified IP address{CLEARCOLOR}\n";
                if (vulnerabilityModule) returnValue += "vul <ip address> {COLOR=#FFFFFFAA}Initiate a vulnerability hack on the specified IP address{CLEARCOLOR}\n";
                returnValue += "ssh <ip address> <username> <password> {COLOR=#FFFFFFAA}Connect to the system at the specified IP address{CLEARCOLOR}\n";
                returnValue += "store {COLOR=#FFFFFFAA}Lists available upgrades for purchase with Kreddits{CLEARCOLOR}\n";
                returnValue += "upgrade {COLOR=#FFFFFFAA}Upgrade Uni Ver to version 2.0{CLEARCOLOR}\n";
            } else if (text.equalsIgnoreCase("clear")) {
                if (tab == tab.TTY1) {
                    tty1Messages.clear();
                    createTTY1();
                }
            } else if (text.equalsIgnoreCase("nmap")) {
                if (tutorialLevel < 6) {
                    Core.instance.playVoice(6);
                    tutorialLevel = 6;
                }
                
                returnValue += "{FASTER}Network Map:";
                for (Server server : servers) {
                    returnValue += "\n" + server.address;
                }
                
                returnValue += "\n192.168.1.255 (BLACK WEB)";
            } else if (text.startsWith("brt")) {
                String[] split = text.split("\\s");
                if (split.length != 2 || !split[1].matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                    returnValue += "{FASTER}Incorrect paramaters for brt command. Type \"help\" and press enter to list available commands.";
                } else {
                    Server matchedServer = null;
                    for (Server server : servers) {
                        if (server.address.equalsIgnoreCase(split[1])) {
                            matchedServer = server;
                            break;
                        }
                    }
                    
                    if (matchedServer != null) {
                        if (tab == Tab.TTY1) {
                            tty1Mode = TtyMode.BRUTE_FORCE;
                            animateBruteForce(matchedServer);
                        }
                    } else {
                        returnValue += "{FASTER}IP address " + split[1] + " does not exist. Type \"help\" and press enter to list available commands.";
                    }
                }
            } else if (vulnerabilityModule && text.startsWith("vul")) {
                String[] split = text.split("\\s");
                if (split.length != 2 || !split[1].matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                    returnValue += "{FASTER}Incorrect paramaters for vul command. Type \"help\" and press enter to list available commands.";
                } else {
                    Server matchedServer = null;
                    for (Server server : servers) {
                        if (server.address.equalsIgnoreCase(split[1])) {
                            matchedServer = server;
                            break;
                        }
                    }
    
                    if (matchedServer != null) {
                        if (tab == Tab.TTY1) {
                            tty1Mode = TtyMode.BRUTE_FORCE;
                            animateVulnerability(matchedServer);
                        }
                    } else {
                        returnValue += "{FASTER}IP address " + split[1] + " does not exist. Type \"help\" and press enter to list available commands.";
                    }
                }
            } else if (text.equalsIgnoreCase("store")) {
                returnValue += "{FASTER}Equipment available for purchase:\n";
                returnValue += "buy proxy {COLOR=#FFFFFFAA}$1,000 A proxy server that increases the time it takes for you to be detected on a system{CLEARCOLOR}\n";
                returnValue += "buy firewall {COLOR=#FFFFFFAA}$20,000 A firewall system that prevents detection upon system breach{CLEARCOLOR}\n";
                if(!vulnerabilityModule) returnValue += "buy vul {COLOR=#FFFFFFAA}$50,000 Installs a vulnerability module effective against insecure systems.{CLEARCOLOR}\n";
            } else if (text.equalsIgnoreCase("buy proxy")) {
                if (kreddits >= 1000) {
                    returnValue += "{FASTER}Purchase successful.";
                    proxies++;
                    kreddits -= 1000;
                    updateCounterUI();
                } else {
                    returnValue += "{FASTER}Not enough Kreddits. Sell account number on the Black Web to turn a profit.";
                }
            } else if (text.equalsIgnoreCase("buy firewall")) {
                if (kreddits >= 20000) {
                    returnValue += "{FASTER}Purchase successful.";
                    firewalls++;
                    kreddits -= 20000;
                    updateCounterUI();
                } else {
                    returnValue += "{FASTER}Not enough Kreddits. Sell account number on the Black Web to turn a profit.";
                }
            } else if (!vulnerabilityModule && text.equalsIgnoreCase("buy vul")) {
                if (kreddits >= 50000) {
                    returnValue += "{FASTER}Purchase successful.";
                    vulnerabilityModule = true;
                    kreddits -= 50000;
                    updateCounterUI();
                } else {
                    returnValue += "{FASTER}Not enough Kreddits. Sell account number on the Black Web to turn a profit.";
                }
            } else if (text.equalsIgnoreCase("ssh 192.168.1.255 l337h4ck3r changeme")) {
                tty1Mode = TtyMode.BLACK_WEB;
                returnValue += "{FASTER}Successfully logged into the Black Web:\nWe specialize in the untraceable purchase of Kreddit Card numbers.\nWe guarantee complete anonymity.\nType the account number below or \"exit\" to quit";
            } else if (text.startsWith("ssh")) {
                String[] split = text.split("\\s");
                if (split.length != 4 || !split[1].matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                    returnValue += "{FASTER}Incorrect paramaters for ssh command. Type \"help\" and press enter to list available commands.";
                } else {
                    Server matchedServer = null;
                    for (Server server : servers) {
                        if (server.address.equalsIgnoreCase(split[1])) {
                            matchedServer = server;
                            break;
                        }
                    }
        
                    if (matchedServer != null) {
                        if (matchedServer.user.equals(split[2]) && matchedServer.password.equals(split[3])) {
                            if (tab == Tab.TTY1) {
                                tty1Mode = TtyMode.SERVER;
                                connectedServer = matchedServer;
                                returnValue = "{FASTER}Connected to " + matchedServer.address + ". Welcome " + matchedServer.user + "!";
    
                                if (tutorialLevel < 8) {
                                    Core.instance.playVoice(8);
                                    tutorialLevel = 8;
                                }
                                
                                if (tutorialLevel >= 10) {
                                    stage.addAction(Actions.sequence(Actions.delay(10), new Action() {
                                        @Override
                                        public boolean act(float delta) {
                                            initiateTrace();
                                            return true;
                                        }
                                    }));
                                }
                            }
                        } else {
                            returnValue += "{FASTER}Username or password is incorrect. Type \"help\" and press enter to list available commands.";
                        }
                    } else {
                        returnValue += "{FASTER}IP address " + split[1] + " does not exist. Type \"help\" and press enter to list available commands.";
                    }
                }
            } else if (text.equalsIgnoreCase("swordfish")) {
                returnValue += "{FASTER}CLAUS: Ahh fooey! The connection dropped!\nSYSADMIN: WTH? Why am I still getting your messages?\nCLAUS: I'm transmitting through an intermediary right now, but the connection is {SHAKE}shakey!{ENDSHAKE}\nSYSADMIN: Alright, sign off and sign back on. It should be fixed now.\n{SLOW}{WAVE}USER HAS DISCONNECTED";
            }  else if (text.equalsIgnoreCase("upgrade")) {
                if (dataPoints > 0) {
                    dataPoints--;
                    upgrades++;
                    updateCounterUI();
                    
                    switch (upgrades) {
                        case 1:
                            Core.instance.playVoice(16);
                            break;
                        case 2:
                            Core.instance.playVoice(17);
                            break;
                        case 3:
                            Core.instance.playVoice(18);
                            break;
                        case 4:
                            Core.instance.playVoice(19);
                            break;
                        case 5:
                            Core.instance.playVoice(20);
                            break;
                        case 6:
                            Core.instance.playVoice(21);
                            break;
                        case 7:
                            Core.instance.playVoice(22);
                            break;
                        case 8:
                            Core.instance.playVoice(23);
                            changeScreen(new GameOverAScreen(), 5);
                            break;
                    }
                    returnValue += "{FASTER}Upgrade installing, please wait...\n...............\n...............\n...............";
                } else {
                    returnValue += "{FASTER}Insufficient data points. Infect Cultist servers to collect data points";
                }
            }
            
            else {
                returnValue += "{FASTER}Unknown command: \"" + text + "\"\nType \"help\" and press enter to list available commands.";
            }
        } else if (ttyMode == TtyMode.SERVER) {
            if (text.equalsIgnoreCase("help")) {
                returnValue += "{FASTER}Welcome to Silium OS Remote Terminal\n";
                returnValue += "The following commands can be entered while logged into a remote system:\n";
                returnValue += "help {COLOR=#FFFFFFAA}This command list{CLEARCOLOR}\n";
                returnValue += "clear {COLOR=#FFFFFFAA}Clears all text from the TTY{CLEARCOLOR}\n";
                returnValue += "ls {COLOR=#FFFFFFAA}list all files and directories in the current folder{CLEARCOLOR}\n";
                returnValue += "cd <path> {COLOR=#FFFFFFAA}navigate to specified folder{CLEARCOLOR}\n";
                returnValue += "cd.. {COLOR=#FFFFFFAA}return to parent folder{CLEARCOLOR}\n";
                returnValue += "read <filename> {COLOR=#FFFFFFAA}read the contents of a file{CLEARCOLOR}\n";
                returnValue += "del <filename> {COLOR=#FFFFFFAA}delete the specified file{CLEARCOLOR}\n";
                returnValue += "exit {COLOR=#FFFFFFAA}disconnect from system{CLEARCOLOR}\n";
                returnValue += "Press tab to autocomplete filenames";
            } else if (text.equalsIgnoreCase("clear")) {
                if (tab == tab.TTY1) {
                    tty1Messages.clear();
                    createTTY1();
                }
            } else if (text.equalsIgnoreCase("ls")) {
                if (tutorialLevel < 9) {
                    Core.instance.playVoice(9);
                    tutorialLevel = 9;
    
                    stage.addAction(Actions.sequence(Actions.delay(10), new Action() {
                        @Override
                        public boolean act(float delta) {
                            if (tutorialLevel < 10) {
                                Core.instance.playVoice(10);
                                tutorialLevel = 10;
                            }
                            
                            initiateTrace();
                            return true;
                        }
                    }));
                }
                Array<String> paths = new Array<String>(connectedServer.filePaths);
                Iterator<String> iter = paths.iterator();
                while(iter.hasNext()) {
                    String path = iter.next();
                    if (!path.startsWith(tty1Path)) {
                        iter.remove();
                    }
                }
                
                for (int i = 0; i < paths.size; i++) {
                    String path = paths.get(i);
                    paths.set(i, path.substring(tty1Path.length()).replaceAll("\\/.*","/"));
                }

                Array<String> noDupes = new Array<String>();
                for (String path : paths) {
                    if (!noDupes.contains(path,false)) {
                        noDupes.add(path);
                    }
                }
                paths = noDupes;
                paths.sort();
                
                for (String path : paths) {
                    returnValue += "{FASTER}" + path + "\n";
                }
            } else if (text.startsWith("cd ")) {
                String[] split = text.split("\\s");
                if (split.length == 2) {
                    split[1] = split[1].replaceAll("\\/","");
                    
                    boolean foundDirectory = false;
                    for (String possibleDirectory : connectedServer.filePaths) {
                        if (possibleDirectory.replaceAll("(?!.*\\/).*$","").startsWith(tty1Path + split[1] + "/")) {
                            tty1Path += split[1] + "/";
                            Label label = root.findActor("tty1-path-label");
                            label.setText("/" + tty1Path + ">");
                            foundDirectory = true;
                            break;
                        }
                    }
                    
                    if (!foundDirectory) {
                        returnValue += "{FASTER}Directory does not exist. Type \"help\" and press enter to list available commands.";
                    }
                } else {
                    returnValue += "{FASTER}Incorrect parameters for cd command. Type \"help\" and press enter to list available commands.";
                }
            } else if (text.equalsIgnoreCase("cd..")) {
                tty1Path = tty1Path.replaceAll("\\/$","").replaceAll("(?!.*\\/).*","");
                Label label = root.findActor("tty1-path-label");
                label.setText("/" + tty1Path + ">");
            } else if (text.startsWith("read")) {
                String[] split = text.split("\\s");
                if (split.length == 2) {
                    int index = connectedServer.filePaths.indexOf(tty1Path + split[1], false);
                    if (index != -1) {
                        returnValue += connectedServer.fileContents.get(index);
                    } else {
                        returnValue += "{FASTER}Could not find file. Type \"help\" and press enter to list available commands.";
                    }
                } else {
                    returnValue += "{FASTER}Incorrect paramaters for del command. Type \"help\" and press enter to list available commands.";
                }
            } else if (text.startsWith("del")) {
                String[] split = text.split("\\s");
                if (split.length == 2) {
                    int index = connectedServer.filePaths.indexOf(tty1Path + split[1], false);
                    if (index != -1) {
                        connectedServer.filePaths.removeIndex(index);
                        returnValue += "{FASTER}Deleted file " + split[1];
                    } else {
                        returnValue += "{FASTER}Could not find file. Type \"help\" and press enter to list available commands.";
                    }
                } else {
                    returnValue += "{FASTER}Incorrect paramaters for del command. Type \"help\" and press enter to list available commands.";
                }
            } else if (text.equalsIgnoreCase("exit")) {
                returnValue += "Disconnected from server";
                tty1Path = "";
                Label label = root.findActor("tty1-path-label");
                label.setText("/" + tty1Path + ">");
                tty1Mode = TtyMode.NETWORK;
                refreshServers();
                
                if (!connectedServer.filePaths.contains("log.txt",false) && tutorialLevel < 12) {
                    Core.instance.playVoice(12);
                    tutorialLevel = 12;
                    
                    TextArea textArea = root.findActor("notes-area");
                    textArea.setText(textArea.getText() + "\nBlack Web User: l337h4ck3r\nPassword: changeme");
                }
            }
        } else if (ttyMode == TtyMode.BLACK_WEB) {
            if (text.equalsIgnoreCase("help")) {
                returnValue += "{FASTER}Type the account number below or \"exit\" to quit\n";
            } else if (text.equalsIgnoreCase("exit")) {
                returnValue += "Disconnected from server";
                tty1Path = "";
                Label label = root.findActor("tty1-path-label");
                label.setText("/" + tty1Path + ">");
                tty1Mode = TtyMode.NETWORK;
            } else if (text.matches("\\d{16}")) {
                returnValue += "{FASTER}Kreddits have been added to your account.";
            } else {
                returnValue += "{FASTER}Incorrect value. Kreddit card numbers are 16 digits long";
            }
        } else {
            returnValue += "{FASTER}Error: Terminal not Responding";
        }
        
        return returnValue;
    }
    
    private void initiateTrace() {
        stage.addAction(Actions.delay(20, new Action() {
            @Override
            public boolean act(float delta) {
                if (tty1Mode == TtyMode.SERVER || connectedServer.filePaths.contains("log.txt",false)) {
                    
                    firewalls--;
                    updateCounterUI();
                    if (firewalls < 0) {
                        changeScreen(new GameOverBScreen());
                    } else {
                        Core.instance.playVoice(11);
                    }
                    
                    if (tty1Mode == TtyMode.SERVER) {
                        tty1Path = "";
                        Label label = root.findActor("tty1-path-label");
                        label.setText("/" + tty1Path + ">");
                        tty1Mode = TtyMode.NETWORK;
                        tty1Messages.add("Disconnected from server");
    
                        Table subTable = root.findActor("tty1-message-table");
                        for (Actor actor : subTable.getChildren()) {
                            ((TypingLabel) actor).skipToTheEnd();
                        }
    
                        subTable.row();
                        TypingLabel typingLabel = new TypingLabel(tty1Messages.get(tty1Messages.size - 1), skin);
                        typingLabel.setWrap(true);
                        subTable.add(typingLabel).growX();
                        
                        refreshServers();
    
                        ScrollPane scrollPane = root.findActor("tty1-message-scroll");
                        scrollPane.layout();
                        scrollPane.layout();
                        scrollPane.setScrollPercentY(1);
    
                        stage.addAction(Actions.delay(12, new Action() {
                            @Override
                            public boolean act(float delta) {
                                if (tutorialLevel < 12) {
                                    Core.instance.playVoice(12);
                                    tutorialLevel = 12;
    
                                    TextArea textArea = root.findActor("notes-area");
                                    textArea.setText(textArea.getText() + "\nBlack Web User: l337h4ck3r\nPassword: changeme");
                                }
                                return true;
                            }
                        }));
                    }
                }
                return true;
            }
        }));
    }
    
    public void updateCounterUI() {
        Label label = root.findActor("label-kreddits");
        String originalValue = label.getText().toString();
        label.setText("$" + kreddits);
        if (!label.getText().toString().equals(originalValue)) {
            label.addAction(Actions.sequence(Actions.color(skin.getColor("red"), .25f), Actions.delay(2), Actions.color(skin.getColor("ui"), .25f)));
            Image image = root.findActor("icon-kreddits");
            image.addAction(Actions.sequence(Actions.color(skin.getColor("red"), .25f), Actions.delay(2), Actions.color(skin.getColor("ui"), .25f)));
        }
    
        label = root.findActor("label-data-points");
        originalValue = label.getText().toString();
        label.setText("X" + dataPoints);
        if (!label.getText().toString().equals(originalValue)) {
            label.addAction(Actions.sequence(Actions.color(skin.getColor("red"), .25f), Actions.delay(2), Actions.color(skin.getColor("ui"), .25f)));
            Image image = root.findActor("icon-data-points");
            image.addAction(Actions.sequence(Actions.color(skin.getColor("red"), .25f), Actions.delay(2), Actions.color(skin.getColor("ui"), .25f)));
        }
    
        label = root.findActor("label-firewall");
        originalValue = label.getText().toString();
        label.setText("X" + firewalls);
        if (!label.getText().toString().equals(originalValue)) {
            label.addAction(Actions.sequence(Actions.color(skin.getColor("red"), .25f), Actions.delay(2), Actions.color(skin.getColor("ui"), .25f)));
            Image image = root.findActor("icon-firewall");
            image.addAction(Actions.sequence(Actions.color(skin.getColor("red"), .25f), Actions.delay(2), Actions.color(skin.getColor("ui"), .25f)));
        }
    
        label = root.findActor("label-proxy");
        originalValue = label.getText().toString();
        label.setText("X" + proxies);
        if (!label.getText().toString().equals(originalValue)) {
            label.addAction(Actions.sequence(Actions.color(skin.getColor("red"), .25f), Actions.delay(2), Actions.color(skin.getColor("ui"), .25f)));
            Image image = root.findActor("icon-proxy");
            image.addAction(Actions.sequence(Actions.color(skin.getColor("red"), .25f), Actions.delay(2), Actions.color(skin.getColor("ui"), .25f)));
        }
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
    
    private void animateBruteForce(final Server server) {
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
    
        if (tab == Tab.TTY1) {
            sequenceAction.addAction(new Action() {
                @Override
                public boolean act(float delta) {
                    String message = "{FASTER}\nSuccessfully Hacked " + server.address + "\nUsername is " + server.user + "\nPassword is " + server.password;
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
                    tty1Mode = TtyMode.NETWORK;
    
                    if (tutorialLevel < 7) {
                        Core.instance.playVoice(7);
                        tutorialLevel = 7;
                    }
                    return true;
                }
            });
        }
        
        stage.addAction(sequenceAction);
    }
    
    private void animateVulnerability(final Server server) {
        SequenceAction sequenceAction = new SequenceAction();
        
        for (int i = 0; i < 200; i++) {
            if (tab == Tab.TTY1) {
                sequenceAction.addAction(new Action() {
                    @Override
                    public boolean act(float delta) {
                        String vulnerability = "{FASTER}" + Core.instance.vulnerabilities.random();
                        tty1Messages.add(vulnerability);
                        Table subTable = root.findActor("tty1-message-table");
                        subTable.row();
                        TypingLabel typingLabel = new TypingLabel(vulnerability, skin);
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
        
        if (tab == Tab.TTY1) {
            sequenceAction.addAction(new Action() {
                @Override
                public boolean act(float delta) {
                    String message = "{FASTER}\nSuccessfully Hacked " + server.address + "\nUsername is " + server.user + "\nPassword is " + server.password;
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
                    tty1Mode = TtyMode.NETWORK;
    
                    if (tutorialLevel < 7) {
                        Core.instance.playVoice(7);
                        tutorialLevel = 7;
                    }
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
    
    public void refreshServers() {
        servers.clear();
        for(int i = 0; i < 5; i++) {
            servers.add(new Server());
        }
    }
    
    private static class Server {
        private String address;
        private String user;
        private String password;
        private Array<String> filePaths;
        private Array<String> fileContents;
        
        public Server() {
            address = MathUtils.random(255) + "." + MathUtils.random(255) + "." + MathUtils.random(255) + "." + MathUtils.random(255);
            user = Core.instance.users.random();
            password = Core.instance.passwords.random();
            filePaths = new Array<String>();
            fileContents = new Array<String>();
            populateFiles();
        }
        
        public void populateFiles() {
            Array<String> allPaths = new Array<String>(Core.instance.userFilePaths);
            Array<String> allContents = new Array<String>(Core.instance.userFileContents);
            
            for (int i = 0; i < 5; i++) {
                int index = MathUtils.random(allPaths.size - 1);
                filePaths.add(allPaths.get(index));
                fileContents.add(allContents.get(index));
                allPaths.removeIndex(index);
                allContents.removeIndex(index);
            }
    
            allPaths = new Array<String>(Core.instance.userRewardPaths);
            allContents = new Array<String>(Core.instance.userRewardContents);
    
            for (int i = 0; i < 5; i++) {
                int index = MathUtils.random(allPaths.size - 1);
                filePaths.add(allPaths.get(index));
                fileContents.add(allContents.get(index));
                allPaths.removeIndex(index);
                allContents.removeIndex(index);
            }
            
            filePaths.add("log.txt");
            fileContents.add("User \"OPERATOR\" (" + MathUtils.random(255) + "." + MathUtils.random(255) + "." + MathUtils.random(255) + "." + MathUtils.random(255) + ") connected at " + TimeUtils.millis());
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
        Core.instance.playList.stop();
    }
    
    @Override
    public void dispose() {
        stage.dispose();
    }
}
