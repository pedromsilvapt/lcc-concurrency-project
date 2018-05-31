package com.pcc.project.Prefabs.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Button;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Layout.PositionLayout;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Theme;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Window;
import com.pcc.project.ECS.Components.Graphics2D.Primitive.Rectangle;
import com.pcc.project.ECS.Components.Graphics2D.Text;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Components.LifecycleHooks;
import com.pcc.project.ECS.Components.Network.NetworkGameMaster;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;
import com.pcc.project.Prefabs.GameObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Leaderboards extends Prefab< Entity > {
    public static class LeaderboardEntry {
        public String name;
        public String value;

        public LeaderboardEntry ( String name, String value ) {
            this.name = name;
            this.value = value;
        }
    }

    private boolean destroyed = false;

    public void goBack ( Entity leaderboards ) {
        this.destroyed = true;

        leaderboards.parent.instantiate( new MainMenu() );

        leaderboards.destroy();
    }

    public float getListHeight ( float heightPerEntry, int listSize ) {
        return listSize * heightPerEntry;
    }

    public float getEntryHeight ( float listHeight, float heightPerEntry, int index ) {
        return listHeight - ( heightPerEntry * index );
    }

    public Vector2 setData ( Entity leaderboard, Entity container, List< List< LeaderboardEntry > > lists, String[] labels ) {
        if ( destroyed ) {
            return null;
        }

        float width  = 0;
        float height = 0;

        float widthPerList   = 230;
        float heightPerEntry = 35;

        float maxHeight = ( float ) lists.stream()
                .mapToDouble( list -> getListHeight( heightPerEntry, list.size() ) )
                .max().orElse( 0 );

        int i;

        float positionWidth = 40;
        float valueWidth    = 40;
        float nameWidth     = widthPerList - positionWidth - valueWidth;

        int col = 0;
        for ( List< LeaderboardEntry > list : lists ) {
            i = 0;

            container.instantiate( new GameObject( width, maxHeight + heightPerEntry ) )
                    .addComponent( Text.class )
                    .setValue( labels[ col ] )
                    .setBitmapFont( BaseStylesheet.font )
                    .setColor( BaseStylesheet.dark )
                    .setWrap( false )
                    .setTextAlign( Align.center )
                    .setSize( widthPerList, heightPerEntry )
                    .setAutoSize( false );

            for ( LeaderboardEntry entry : list ) {
                height = this.getEntryHeight( maxHeight, heightPerEntry, i );

                int finalI = i + 1;
                container.instantiate( new GameObject( width, height ), entity -> {
                    entity.addComponent( Text.class )
                            .setValue( String.valueOf( finalI ) )
                            .setBitmapFont( BaseStylesheet.font )
                            .setColor( BaseStylesheet.dark )
                            .setWrap( false )
                            .setTextAlign( Align.left )
                            .setSize( positionWidth, heightPerEntry )
                            .setAutoSize( false );
                } );

                container.instantiate( new GameObject( width + positionWidth, height ), entity -> {
                    entity.addComponent( Text.class )
                            .setBitmapFont( BaseStylesheet.font )
                            .setValue( entry.name )
                            .setColor( BaseStylesheet.dark )
                            .setTextAlign( Align.left )
                            .setWrap( false )
                            .setSize( nameWidth, heightPerEntry )
                            .setAutoSize( false );
                } );

                container.instantiate( new GameObject( width + positionWidth + nameWidth, height ), entity -> {
                    entity.addComponent( Text.class )
                            .setBitmapFont( BaseStylesheet.font )
                            .setValue( entry.value )
                            .setColor( BaseStylesheet.golden )
                            .setWrap( false )
                            .setTextAlign( Align.left )
                            .setSize( valueWidth, heightPerEntry )
                            .setAutoSize( false );
                } );

                i++;
            }

            width += widthPerList + 30;
            col++;
        }

        if ( width > 0 ) {
            width -= 30;
        }

        return new Vector2( width, maxHeight );
    }

    public void relayout ( Vector2 size, Entity leaderboards, Entity container ) {
        if ( size == null ) {
            return;
        }

        float width  = size.x + 20;
        float height = size.y + 49 + 10 + 68 + 10;

        Gdx.app.log( "Window", String.format( "%f, %f (%s)", width, height, size.toString() ) );

        leaderboards.getComponent( Window.class )
                .setSize( width, height );

        container.getComponent( Transform.class )
                .setPosition( 10, 26 );
    }

    @Override
    public Entity instantiate () {
        float width  = 500;
        float height = 200;

        // Window
        Entity leaderboards = new GameObject( "leaderboards" ).instantiate();
        leaderboards.addComponent( Window.class )
                .setTitle( "Leaderboards" )
                .setSize( width, height );
        leaderboards.addComponent( PositionLayout.class )
                .setAlign( Align.center );

        Entity container = leaderboards.instantiate( new GameObject( "lists" ) );

        float btnWidth = 100;

        Entity goBack = leaderboards.instantiate( new GameObject( "goBackBtn" ) );
        goBack.addComponent( Button.class )
                .setValue( "Back" )
                .setColor( BaseStylesheet.dark )
                .setTheme( Theme.Grey )
                .setAction( btn -> this.goBack( leaderboards ) )
                .setSize( btnWidth, 49 );

        goBack.addComponent( PositionLayout.class )
                .setVisual( goBack.getComponent( Button.class ) )
                .setFrame( leaderboards.getComponent( Rectangle.class ) )
                .setMargin( 20, 10 )
                .setAlign( Align.bottom );

        goBack.getComponent( Button.class ).setAutoFocus( true );

        leaderboards.addComponent( LifecycleHooks.class )
                .setOnAwake( entity -> {
                    NetworkGameMaster gameMaster = entity.getComponentInParent( NetworkGameMaster.class );

                    gameMaster.commandViewLeaderboards( ( scores, points ) -> {
                        List< List< LeaderboardEntry > > lists = new ArrayList<>();

                        scores.sort( ( a, b ) -> Float.compare( Float.parseFloat( a.value ), Float.parseFloat( b.value ) ) * -1 );

                        lists.add( points.stream()
                                .sorted( ( a, b ) -> Float.compare( Float.parseFloat( a.value ), Float.parseFloat( b.value ) ) * -1 )
                                .limit( 5 )
                                .collect( Collectors.toList() ) );
                        lists.add( scores.stream()
                                .sorted( ( a, b ) -> Float.compare( Float.parseFloat( a.value ), Float.parseFloat( b.value ) ) * -1 )
                                .limit( 5 )
                                .collect( Collectors.toList() ) );

                        Vector2 size = this.setData( leaderboards, container, lists, new String[] { "Levels", "Scores" } );
                        this.relayout( size, leaderboards, container );
                    } );
                } );

        return leaderboards;
    }
}
