package com.ray3k.silium;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;

public class NetworkMapWidget extends Table {
    private Skin skin;
    private Stack playerStack;
    private Array<GameScreen.Server> servers;
    private Array<Stack> serverStacks;
    private ObjectMap<Stack, Image> serverLines;
    
    public NetworkMapWidget(Array<GameScreen.Server> servers, Skin skin) {
        this.skin = skin;
        this.servers = servers;
        serverStacks = new Array<Stack>();
        serverLines = new ObjectMap<Stack, Image>();
        
        populate();
    }
    
    private void populate() {
        clearChildren();
        serverStacks.clear();
        serverLines.clear();
    
        Array<Image> lines = new Array<Image>();
        
        for (int i = 0; i < servers.size + 1; i++) {
            Image image = new Image(skin, "line-tinted");
            image.setScaling(Scaling.stretch);
            addActor(image);
            lines.add(image);
            
            image.pack();
        }
        
        //player
        playerStack = new Stack();
        addActor(playerStack);
    
        Image image = new Image(skin, "icon-player-tinted");
        image.setScaling(Scaling.none);
        playerStack.add(image);
    
        playerStack.pack();
        
        //black web
        Stack stack = new Stack();
        addActor(stack);
        serverStacks.add(stack);
    
        image = new Image(skin, "icon-system-tinted");
        image.setScaling(Scaling.none);
        stack.add(image);
    
        Table table = new Table();
        stack.add(table);
        Label label = new Label("192.168.1.255\n(BLACK WEB)", skin, "ip");
        label.setAlignment(Align.center);
        table.add(label).bottom().expand();
    
        stack.pack();
    
        serverLines.put(stack, lines.get(servers.size));
        
        //servers
        for (int i = 0; i < servers.size; i++) {
            GameScreen.Server server = servers.get(i);
            
            stack = new Stack();
            addActor(stack);
            serverStacks.add(stack);
            
            image = new Image(skin, "icon-system-tinted");
            image.setScaling(Scaling.none);
            stack.add(image);
            
            table = new Table();
            stack.add(table);
            label = new Label(server.address + (server.cultist ? "\nCULTIST" : ""), skin, "ip");
            label.setAlignment(Align.center);
            table.add(label).bottom().expand();
            
            stack.pack();
            
            serverLines.put(stack, lines.get(i));
        }
    }
    
    @Override
    public void layout() {
        super.layout();
        
        playerStack.setPosition(.5f * getWidth(), .5f * getHeight(), Align.center);
        
        for (int i = 0; i < serverStacks.size; i++) {
            Stack stack = serverStacks.get(i);
            Vector2 point = new Vector2();
            point.x = 125;
            point.rotate(360 / serverStacks.size * i);
            
            stack.setPosition(playerStack.getX(Align.center) + point.x, playerStack.getY(Align.center) + point.y, Align.center);
    
            Image line = serverLines.get(stack);
            if (line != null) {
                line.setWidth(point.len());
                line.setPosition(stack.getX(Align.center), stack.getY(Align.center));
                line.setRotation(point.angle() + 180);
                line.layout();
            }
        }
    }
}
