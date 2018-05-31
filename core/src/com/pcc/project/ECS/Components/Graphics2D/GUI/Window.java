package com.pcc.project.ECS.Components.Graphics2D.GUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.pcc.project.ECS.Components.Graphics2D.Sprite;
import com.pcc.project.ECS.Components.Graphics2D.Text;
import com.pcc.project.ECS.Components.Graphics2D.Transform;
import com.pcc.project.ECS.Components.Graphics2D.VisualComponent;
import com.pcc.project.ECS.Entity;
import com.pcc.project.Prefabs.GameObject;


public class Window extends VisualComponent {
    public static String name = "window";

    protected Theme theme = Theme.Blue;

    protected Sprite spriteStateNormal;

    protected Entity windowLabel;

    protected Text windowLabelText;

    protected String title = "";

    public Window ( Entity entity, String name ) {
        super( entity, name );
    }

    public Window setSize ( float width, float height ) {
        super.setSize( width, height );

        this.onAlign();

        return this;
    }

    public Window setSize ( Size size ) {
        super.setSize( size );

        this.onAlign();

        return this;
    }

    public Window setAlign ( int align ) {
        if ( this.align != align ) {
            super.setAlign( align );
        }
        return this;
    }

    // Adding getters and setters for the window
    public Theme getTheme () {
        return this.theme;
    }

    public Window setTheme ( Theme theme ) {
        this.theme = theme;

        return this;
    }

    public String getTitle () {
        return this.title;
    }

    public Window setTitle ( String value ) {
        if ( this.title != value ) {
            this.title = value;

            this.onAlign();
        }

        return this;
    }


    public void onAlign () {
        // We call this method when the text or size of the window changes, to refresh
        // the position of the Title so that it is always centered and on the top

        // But if we happen to change any of those things before the window was even created
        // Then the pointer windowLabel would be null and using it would throw an exception
        // So we don't run the function in those cases
        if ( this.windowLabel == null ) {
            return;
        }

        // We change to align code
        // In OpenGL, the point (0,0) lays in the left bottom corner. So, incrementing the Y coordinate
        // moves things up. Since we want the title of the window to be on top of it, we have to set the position
        // to the height of the windows minus the height of the text
        Vector2 anchor = this.getAnchor();

        float x = anchor != null ? anchor.x : 0;
        float y = anchor != null ? anchor.y : 0;

        this.spriteStateNormal.setSize( this.size );

        this.windowLabel.getComponent( Transform.class )
                .setPosition( x + 10, y + this.getSize().height - 30 );

        this.windowLabel.getComponent( Text.class )
                .setValue( this.getTitle() )
                .setSize( new Size( this.getSize().width - 20, 30 ) );
    }

    public String getThemeString () {
        return Theme.getString( this.theme );
    }

    public String getWindowAssetName () {
        // We also changed this function to return the path for the window.
        // Again, since windows don't need state, we removed that argument
        // And added a property called theme that allows us to set the window color
        // from the available images (blue, green, yellow, red)
        return String.format( "metalPanel_%s", this.getThemeString() );
    }


    public void onAwake () {
        super.onAwake();

        // We  had to change the PatchConfig a little, the third value (top) was changed to 30
        // Since that is the height of the window bar on the top
        Sprite.PatchConfig windowPatch = new Sprite.PatchConfig( 12, 12, 30, 21 );

        // Unlike buttons or textboxes, our windows will only have one "state": normal
        // Since buttons could be pressed and hovered with the mouse, they needed more than one sprite
        // But one for windows will do just fine
        this.spriteStateNormal = this.entity.addComponent( Sprite.class, "window_normal" );
        this.spriteStateNormal
                .setNinePatchConfig( windowPatch )
                .setTexturePath( String.format( "uipack-space/PNG/%s.png", this.getWindowAssetName() ) )
                .setAlign( this.getAlign() )
                .setSize( this.size )
                .setAnchor( this.getAnchor() );
        // And since we only have one state, it is always enabled
        //.setEnabled(this.state == Window.WindowState.Normal);

        this.windowLabel = this.entity.instantiate( new GameObject( "windowLabel" ) );

        this.windowLabelText = this.windowLabel.addComponent( Text.class );
        this.windowLabelText
                .setBitmapFont( "fonts/KenVector_Future_16_white.fnt" )
                .setValue( this.getTitle() )
                .setAutoSize( false )
                .setWrap( false )
                .setTruncateText( "..." )
                .setColor( Color.WHITE )
                .setTextAlign( Align.center )
                .setSize( new Size( this.getSize().width - 20, 30 ) );

        this.onAlign();
    }
}


