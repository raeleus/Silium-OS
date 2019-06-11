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
        slider.setValue(Core.instance.sfxVolume);
        root.add(slider);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Core.instance.sfxVolume = ((Slider) actor).getValue();
                Core.instance.currentVoice.setVolume(Core.instance.sfxVolume);
                if (!Core.instance.currentVoice.isPlaying()) Core.instance.currentVoice.play();
            }
        });
        
        root.row();
        label = new Label("BGM",skin);
        root.add(label).padLeft(8);
        
        slider = new Slider(0, 1, .01f, false, skin);
        slider.setValue(Core.instance.bgmVolume);
        root.add(slider);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Core.instance.bgmVolume = ((Slider) actor).getValue();
                Core.instance.playList.setVolume(Core.instance.bgmVolume);
            }
        });
        
        key(Input.Keys.ESCAPE, null).key(Input.Keys.ENTER,null);
    }
    
    public static DialogParameters show(Skin skin, Stage stage) {
        DialogParameters dialog = new DialogParameters(skin);
        dialog.show(stage);
        return dialog;
    }
}
