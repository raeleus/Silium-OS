package com.ray3k.silium;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class GameScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private final static String LONG_TEXT = "This is a very long text that I'm trying to use to develop my abilities in the naked arts. It is known throughout the land as an evil assassin supersoldier.";
    
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), new TwoColorPolygonBatch());
        Gdx.input.setInputProcessor(stage);
        
        skin = Core.instance.assetManager.get("ui/silium-ui.json");
    
        Table root = new Table();
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
        
        Image image = new Image(skin,"icon-kreddits-tinted");
        subTable.add(image);
        
        Label label = new Label("$300",skin);
        subTable.add(label);
    
        image = new Image(skin,"icon-data-points-tinted");
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
    
        ButtonGroup<TextButton> buttonGroup = new ButtonGroup<TextButton>();
        
        TextButton textButton = new TextButton("TTY1", skin, "tab");
        table.add(textButton);
        buttonGroup.add(textButton);
    
        textButton = new TextButton("TTY2", skin, "tab");
        table.add(textButton);
        buttonGroup.add(textButton);
        
        table.add().expandX();
        
        table.row();
        subTable = new Table();
        table.add(subTable).grow().padTop(8).colspan(3);
        
        typingLabel = new TypingLabel(LONG_TEXT, skin);
        typingLabel.setWrap(true);
        subTable.add(typingLabel).growX().colspan(2);
        
        subTable.defaults().padTop(5);
        subTable.row();
        label = new Label("root/documents>", skin);
        subTable.add(label).expandY().top();
        
        TextField textField = new TextField("", skin);
        subTable.add(textField).growX().expandY().top().minWidth(50);
        
        table = new Table();
        table.setBackground(skin.getDrawable("table-with-tab-tinted"));
        bottom.add(table).fillX().growY().maxWidth(200);
        
        textButton = new TextButton("Notes", skin, "tab");
        table.add(textButton).left();
        buttonGroup.add(textButton);
    
        table.row();
        TextArea textArea = new TextArea("",skin);
        table.add(textArea).grow().padTop(8).minWidth(50);
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
