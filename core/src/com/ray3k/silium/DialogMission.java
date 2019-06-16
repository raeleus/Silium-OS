package com.ray3k.silium;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class DialogMission extends Dialog {
    public DialogMission(Skin skin) {
        super("Parameters", skin);
        
        getTitleTable().getCell(getTitleLabel()).padTop(10).padLeft(10);
    
        Button button = new Button(skin,"window-close");
        getTitleTable().add(button).bottom().expandY();
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Core.instance.playSound("button");
                hide();
            }
        });
        
        Table root = getContentTable();
        TypingLabel typingLabel = new TypingLabel("{FAST}From: Network Operations Command - Sysadmin\nSubject: Mission " +
                "Objective\n\nOperator, I'm giving you command of our prototype Hack AI. I know you're a lone wolf, " +
                "but you're going to need an extra hand with this one. Your mission parameters: seek and destroy all " +
                "cultist army assets at whatever the cost. This is off the books, so you'll also have to avoid " +
                "detection from enforcement agencies.\n\nMay his Reign Burn Eternal.", skin);
        typingLabel.setWrap(true);
        root.add(typingLabel).grow().pad(10).minWidth(400);
        
        key(Input.Keys.ESCAPE, null).key(Input.Keys.ENTER,null);
    }
    
    public static DialogMission show(Skin skin, Stage stage) {
        DialogMission dialog = new DialogMission(skin);
        dialog.show(stage);
        return dialog;
    }
}
