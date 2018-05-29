package com.pcc.project.Prefabs.GUI;

import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Components.GameLogic.Game;
import com.pcc.project.ECS.Components.GameLogic.Ship;
import com.pcc.project.ECS.Components.GameLogic.User;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Layout.PositionLayout;
import com.pcc.project.ECS.Components.Graphics2D.Sprite;
import com.pcc.project.ECS.Components.Graphics2D.Text;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Components.LifecycleHooks;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;
import com.pcc.project.Prefabs.GameObject;

class PlayerDetails extends Prefab<Entity> {
    protected User player;

    protected int align;

    protected String color;

    protected int shape = 1;

    public PlayerDetails ( User player, int align, String color ) {
        this.player = player;
        this.align = align;
        this.color = color;
    }

    public PlayerDetails ( User player, int align, String color, int shape ) {
        this( player, align, color );

        this.shape = shape;
    }

    public boolean isLeft () {
        return this.align == Align.left || this.align == Align.topLeft || this.align == Align.bottomLeft;
    }

    @Override
    public Entity instantiate () {
        Entity group = new GameObject().instantiate();

        group.instantiate( new GameObject( "icon" ) )
                .addComponent( Sprite.class )
                .setTexturePath( String.format( "spaceshooter/PNG/UI/playerLife%d_%s.png", this.shape, this.color ) )
                .setSize( 33, 26 );

        group.addComponent( PositionLayout.class )
                .setSize( 33, 26 )
                .setMargin( 20, 20 )
                .setAlign( this.align );

        int nameAlign = this.isLeft() ? Align.left : Align.right;

        int margin = this.isLeft() ? 33 + 8 : -8;

        group.instantiate( new GameObject( "username", margin, 8 ) )
                .addComponent( Text.class )
                .setBitmapFont( BaseStylesheet.font )
                .setTextAlign( nameAlign )
                .setValue( String.format( "%s l.%d", this.player.getUsername(), this.player.getLevel() ) )
                .setColor( BaseStylesheet.white );

        return group;
    }
}

public class PlayerHud extends Prefab<Entity> {
    protected Ship playerShip;

    protected Game game;

    public PlayerHud ( Ship playerShip, Game game ) {
        this.playerShip = playerShip;
        this.game = game;
    }

    public float getEnergyBarWidth ( float amount ) {
        return amount * 200 / playerShip.getEnergy().getCapacity() + 18;
    }

    public float getEnergyBarWidth () {
        return this.getEnergyBarWidth( playerShip.getEnergy().getAmount() );
    }

    @Override
    public Entity instantiate () {
        Entity hud = new GameObject( "hud" ).instantiate();

        Entity energySprite = hud.instantiate( new GameObject( "energySprite" ) );

        energySprite.addComponent( Sprite.class )
                .setTexturePath( "spaceshooter/PNG/Power-ups/bolt_gold.png" );

        energySprite.addComponent( PositionLayout.class )
                .setMargin( 20, 20 )
                .setAlign( Align.bottomLeft );

        Entity energyBar = hud.instantiate( new GameObject( "energyBar" ) );

        energyBar.addComponent( Sprite.class )
                .setNinePatchConfig( 9, 9, 13, 13 )
                .setTexturePath( "uipack-space/PNG/square_shadow.png" )
                .setSize( this.getEnergyBarWidth( playerShip.getEnergy().getCapacity() ), 26 );

        energyBar.addComponent( Sprite.class, "amount" )
                .setNinePatchConfig( 9, 9, 13, 13 )
                .setTexturePath( "uipack-space/PNG/squareYellow.png" )
                .setSize( this.getEnergyBarWidth(), 26 );

        energyBar.addComponent( PositionLayout.class )
                .setSize( this.getEnergyBarWidth(), 26 )
                .setAlign( Align.bottomLeft )
                .setMargin( 50, 23 );

        energySprite.addComponent( PositionLayout.class )
                .setMargin( 20, 20 )
                .setAlign( Align.bottomLeft );

        energyBar.addComponent( LifecycleHooks.class )
            .setOnUpdate( e -> energyBar.<Sprite>getComponent( "amount" ).setSize( this.getEnergyBarWidth(), 26 ) );



        // Score
        Entity scoreSprite = hud.instantiate( new GameObject( "scoreSprite" ) );

        scoreSprite.addComponent( Sprite.class )
                .setTexturePath( "spaceshooter/PNG/Power-ups/powerupYellow_star.png" );

        scoreSprite.addComponent( PositionLayout.class )
                .setMargin( 20, 20 )
                .setAlign( Align.bottomRight );

        Entity scoreValue = hud.instantiate( new GameObject( "scoreValue" ) );

        scoreValue.addComponent( Text.class, "title" )
            .setBitmapFont( BaseStylesheet.font )
            .setValue( "00010" )
            .setAutoSize( false )
            .setTextAlign( Align.right )
            .setColor( BaseStylesheet.white )
            .setSize( 100, 30 );

        scoreValue.addComponent( PositionLayout.class )
                .setMargin( 60, 20 )
                .setAlign( Align.bottomRight );

        scoreValue.addComponent( LifecycleHooks.class )
                .setOnUpdate( e -> e.<Text>getComponent( "title" ).setValue( String.valueOf( game.getScore() ) ) );


        hud.instantiate( new PlayerDetails( game.getPlayer(), Align.topLeft, "blue" ) );

        hud.instantiate( new PlayerDetails( game.getOpponent(), Align.topRight, "red" ) );

        return hud;
    }
}
