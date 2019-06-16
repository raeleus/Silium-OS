package com.ray3k.silium;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;

public class TraceWidget extends Stack {
    private Skin skin;
    public ProgressBar progressBar;
    private Array<Image> proxies;
    
    public TraceWidget(final int numProxies, final Skin skin) {
        this.skin = skin;
        proxies = new Array<Image>();
    
        Table table = new Table();
        add(table);
        
        progressBar = new ProgressBar(0, 1, .01f, true, skin);
        progressBar.setAnimateDuration(.25f);
        table.add(progressBar).growY();
    
        table = new Table();
        add(table);
        
        table.add().expand();
        table.row();
        
        for (int i = 0; i < numProxies; i++) {
            Image image = new Image(skin, "icon-proxy");
            image.setScaling(Scaling.none);
            image.setColor(skin.getColor("ui"));
            table.add(image).expand();
            proxies.add(image);
            table.row();
        }
        
        table.add().expand();
        table.row();
        
        table.add().height(70);
        
        progressBar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                for (int i = 0; i < numProxies; i++) {
                    if (1 - progressBar.getValue() > ((float) i) / numProxies) {
                        proxies.get(i).setColor(skin.getColor("red"));
                    } else {
                        proxies.get(i).setColor(skin.getColor("ui"));
                    }
                }
            }
        });
    }
}
