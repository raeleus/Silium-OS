package com.ray3k.silium;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class DialogParameters extends Dialog {
    public DialogParameters(Skin skin) {
        super("Parameters", skin);
        
        getTitleTable().getCell(getTitleLabel()).padTop(10).padLeft(10);
    
        Button button = new Button(skin,"window-close");
        getTitleTable().add(button).bottom().expandY();
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
            }
        });
        
        Table root = getContentTable();
        getCell(getContentTable()).minSize(300, 100);
        
        Label label = new Label("SFX", skin);
        root.add(label).padLeft(8);
        
        Slider slider = new Slider(0, 1, .01f, false, skin);
        root.add(slider);
        
        root.row();
        label = new Label("BGM",skin);
        root.add(label).padLeft(8);
        
        slider = new Slider(0, 1, .01f, false, skin);
        root.add(slider);
        
        key(Input.Keys.ESCAPE, null).key(Input.Keys.ENTER,null);
    }
    
    public static DialogParameters show(Skin skin, Stage stage) {
        DialogParameters dialog = new DialogParameters(skin);
        dialog.show(stage);
        return dialog;
    }
}
