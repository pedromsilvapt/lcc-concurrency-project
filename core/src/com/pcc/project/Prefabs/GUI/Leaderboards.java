package com.pcc.project.Prefabs.GUI;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Button;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Layout.PositionLayout;
import com.pcc.project.ECS.Components.Graphics2D.GUI.Theme;
import com.pcc.project.ECS.Components.Graphics2D.Primitive.Rectangle;
import com.pcc.project.ECS.Components.Graphics2D.Text;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Entity;
import com.pcc.project.ECS.Prefab;
import com.pcc.project.Prefabs.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Leaderboards extends Prefab< Entity > {
    public class LeaderboardEntry {
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

    public Vector2 setData ( Entity leaderboard, Entity container, List< List< LeaderboardEntry > > lists ) {
        if ( destroyed ) {
            return null;
        }

        float width      = 0;
        float height     = 0;

        float widthPerList   = 230;
        float heightPerEntry = 35;

        float maxHeight  = (float)lists.stream()
                .mapToDouble( list -> getListHeight( heightPerEntry, list.size() ) )
                .max().orElse( 0 );

        int i;

        float positionWidth = 40;
        float valueWidth = 40;
        float nameWidth = widthPerList - positionWidth - valueWidth;

        for ( List< LeaderboardEntry > list : lists ) {
            i = 0;

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
        }

        if ( width > 0 ) {
            width -= 30;
        }

        return new Vector2( width, height );
    }

    public void relayout ( Vector2 size, Entity leaderboards, Entity leaderboardsText, Entity container ) {
        if ( size == null ) {
            return;
        }

        float width = size.x + 20;
        float height = size.y + 49 + 10 + 68 + 10;

        leaderboards.getComponent( Rectangle.class )
                .setSize( width, height );

        leaderboardsText.getComponent( Transform.class )
                .setPosition( 0, height - 40 );

        leaderboardsText.getComponent( Text.class )
                .setSize( width, 40 );

        container.getComponent( Transform.class )
                .setPosition( 10, 26 );
    }

    @Override
    public Entity instantiate () {
        float width  = 500;
        float height = 200;

        // Window
        Entity leaderboards = new GameObject( "leaderboards" ).instantiate();
        leaderboards.addComponent( Rectangle.class )
                .setColor( BaseStylesheet.grey )
                .setSize( width, height );
        leaderboards.addComponent( PositionLayout.class )
                .setAlign( Align.center );

        Entity leaderboardsText = leaderboards.instantiate( new GameObject( "header", 0, height - 40 ) );
        leaderboardsText.addComponent( Text.class )
                .setBitmapFont( BaseStylesheet.font )
                .setValue( "Leaderboards" )
                .setWrap( true )
                .setAutoSize( false )
                .setTextAlign( Align.center )
                .setColor( BaseStylesheet.white )
                .setSize( width, 40 );

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


        List< LeaderboardEntry > points = new ArrayList<>();
        points.add( new LeaderboardEntry( "Pedro", "10" ) );
        points.add( new LeaderboardEntry( "Ola", "10" ) );
        points.add( new LeaderboardEntry( "Ezequiel", "5" ) );
        points.add( new LeaderboardEntry( "Ezequiel", "5" ) );

        List< LeaderboardEntry > scores = new ArrayList<>();
        scores.add( new LeaderboardEntry( "Pedro", "10" ) );
        scores.add( new LeaderboardEntry( "Ezequiel", "5" ) );

        List< List< LeaderboardEntry > > lists = new ArrayList<>();
        lists.add( points );
        lists.add( scores );

        Vector2 size = this.setData( leaderboards, container, lists );
        this.relayout( size, leaderboards, leaderboardsText, container );

        return leaderboards;
    }
}
