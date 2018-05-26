package com.pcc.project.ECS;

import com.pcc.project.Utils.SafeArrayList;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/*
* The game makes use of a simplified Entity Component System.
* The GameWorld is composed of a Tree of Entities (also known as GameObjects). To form a Tree,
* each Entity can have an indeterminate number of child entities.
*
* Each entity on its own has no behavior not visual appearence. However, each entity can have
* a List of Components, that give it behavior. Components can depend on other components.
*
* For instance, an Entity can have the transform component. This Component allows the Entity
* to have a local position, scale and rotation. The Transform component depends upon the parent's own Transform component,
* to then calculate the global position, scale and rotation of the object.
*
* The Sprite Component, on the other hand, depends on the Entity's own Transform, to draw a visual 2D image to the screen.
*
* Since setting up a useful object in the Game World requires adding and configuring it's components, doing this every time
* for every object can be tedius.
* To fix that, we have prefabs that can be user to instantiate entities. When instantiating an entity, we can pass a prefab instead.
* Prefabs simply create the entities, add the objects, and in the end, return the main entity to be added.
* */

public class Entity {
    public Entity root;

    public Entity parent;

    public SafeArrayList< Entity > children;

    public SafeArrayList< Component > components;

    public HashMap< Component, ComponentState > componentStates;

    public String name;

    public boolean enabled = true;

    public Entity () {
        this( null, "root" );
    }

    public Entity ( Entity parent, String name ) {
        this.parent = parent;
        this.name = name;

        this.children = new SafeArrayList<>();
        this.componentStates = new HashMap<>();
        this.components = new SafeArrayList<>();

        if ( this.parent != null ) {
            this.root = this.parent.root;
        } else {
            this.root = this;
        }
    }

    public boolean getEnabled () {
        return this.enabled;
    }

    public Entity setEnabled ( boolean enabled ) {
        this.enabled = enabled;

        return this;
    }

    public Entity setChildAfter ( Entity child, Entity predecessor ) {
        if ( this.children.contains( predecessor ) ) {
            this.children.remove( child );

            int successorIndex = this.children.indexOf( predecessor );

            this.children.add( successorIndex + 1, child );
        }

        return this;
    }

    public Entity setChildBefore ( Entity child, Entity successor ) {
        if ( this.children.contains( successor ) ) {
            this.children.remove( child );

            int successorIndex = this.children.indexOf( successor );

            this.children.add( successorIndex, child );
        }

        return this;
    }

    public Entity setAfter ( Entity predecessor ) {
        if ( this.parent != null ) {
            this.parent.setChildAfter( this, predecessor );
        }

        return this;
    }

    public Entity setBefore ( Entity successor ) {
        if ( this.parent != null ) {
            this.parent.setChildBefore( this, successor );
        }

        return this;
    }

    public  <T extends Entity> T instantiate ( Prefab<T> prefab ) {
        return this.instantiate( prefab, null );
    }

    public  <T extends Entity> T instantiate ( Prefab<T> prefab, Consumer<T> consumer ) {
        T entity = prefab.instantiate();

        entity.parent = this;

        entity.root = this.root;

        this.children.add( entity );

        if ( consumer != null ) {
            consumer.accept( entity );
        }

        return entity;
    }

    public <T extends Entity> T addEntity ( Class<T> entityType, String name ) {
        return this.addEntity( entityType, name, null );
    }

    public <T extends Entity> T addEntity ( Class<T> entityType, String name, Consumer<T> consumer ) {
        try {
            Constructor< T > constructor = entityType.getConstructor( Entity.class, String.class );

            T entity = constructor.newInstance( this, name );

            entity.parent = this;

            entity.root = this.root;

            this.children.add( entity );

            if ( consumer != null ) {
                consumer.accept( entity );
            }

            return entity;
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return null;
    }

    public void removeEntity ( Entity entity ) {
        entity.onDestroy();

        this.children.remove( entity );
    }

    public < T extends Component > T addComponent ( Class< T > componentType ) {
        String name;

        try {
            name = (String)componentType.getField( "defaultName" ).get( null );
        } catch ( Exception e ) {
            name = null;
        }

        return this.addComponent( componentType, name );
    }

    public < T extends Component > Entity addComponent ( Class< T > componentType, Consumer<T> consumer ) {
        T entity = this.addComponent( componentType );

        if ( consumer != null ) {
            consumer.accept( entity );
        }

        return this;
    }

    public < T extends Component > T addComponent ( Class< T > componentType, String name ) {
        try {
            Constructor< T > constructor = componentType.getConstructor( Entity.class, String.class );

            T component = constructor.newInstance( this, name );

            ComponentState state = new ComponentState();

            this.componentStates.put( component, state );

            this.components.add( component );

            component.onCreate();

            return component;
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return null;
    }

    public < T extends Component > Entity addComponent ( Class< T > componentType, String name, Consumer<T> consumer ) {
        T entity = this.addComponent( componentType, name );

        if ( entity != null ) {
            consumer.accept( entity );
        }

        return this;
    }

    public void destroyComponent ( Component component ) {
        if ( this.componentStates.containsKey( component ) ) {
            ComponentState state = this.componentStates.get( component );

            state.destroyed = true;

            if ( state.awakened ) {
                component.onDestroy();
            }

            this.components.remove( component );

            this.componentStates.remove( component );
        }
    }

    public Entity getEntity ( String name ) {
        if ( name.equals( this.name ) ) {
            return this;
        }

        for ( Entity entity : this.children ) {
            if ( entity.name != null && entity.name.equals( name ) ) {
                return entity;
            }
        }

        return null;
    }

    public Entity getEntityInChildren ( String name ) {
        Entity entity = this.getEntity( name );

        if ( entity == null ) {
            for ( Entity child : this.children ) {
                entity = child.getEntityInChildren( name );

                if ( entity != null ) {
                    break;
                }
            }
        }

        return entity;
    }

    public Entity getEntityInParent ( String name ) {
        if ( this.name != null && this.name.equals( name ) ) {
            return this;
        }

        if ( this.parent != null ) {
            return this.getEntityInParent( name );
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public < T extends Component > T getComponent ( String name ) {
        for ( Component component : this.components ) {
            if ( component.name.equals( name ) ) {
                return ( T ) component;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public < T extends Component > T getComponent ( Class< T > type ) {
        for ( Component component : this.components ) {
            if ( type.isAssignableFrom( component.getClass() ) ) {
                return ( T ) component;
            }
        }

        return null;
    }

    public < T extends Component > T getComponentInChildren ( Class< T > type ) {
        T component = this.getComponent( type );

        if ( component == null ) {
            for ( Entity child : this.children ) {
                component = child.getComponentInChildren( type );

                if ( component != null ) {
                    break;
                }
            }
        }

        return component;
    }

    public < T extends Component > T getComponentInParent ( Class< T > type ) {
        T component = this.getComponent( type );

        if ( component == null && this.parent != null ) {
            component = this.parent.getComponentInParent( type );
        }

        return component;
    }

    public List< Component > getComponents () {
        return new ArrayList<>( this.components );
    }

    @SuppressWarnings("unchecked")
    public < T extends Component > List< T > getComponents ( Class< T > type ) {
        return ( List< T > ) this.components.stream().filter( component -> type.isAssignableFrom( component.getClass() ) ).collect( Collectors.toList() );
    }

    public < T extends Component > List< T > getComponentsInChildren ( Class< T > type ) {
        List< T > components = this.getComponents( type );

        for ( Entity child : this.children ) {
            components.addAll( child.getComponentsInChildren( type ) );
        }

        return components;
    }

    public < T extends Component > List< T > getComponentsInParent ( Class< T > type ) {
        List< T > components = this.getComponents( type );

        if ( this.parent != null ) {
            components.addAll( this.parent.getComponentsInParent( type ) );
        }

        return components;
    }


    public List< Component > getComponentsInChildren () {
        List< Component > components = this.getComponents();

        for ( Entity child : this.children ) {
            components.addAll( child.getComponentsInChildren() );
        }

        return components;
    }

    public List< Component > getComponentsInParent () {
        List< Component > components = this.getComponents();

        if ( this.parent != null ) {
            components.addAll( this.parent.getComponentsInParent() );
        }

        return components;
    }

    public void destroy () {
        if ( this.parent != null ) {
            this.parent.removeEntity( this );
        } else {
            this.onDestroy();
        }
    }

    /* Lifecycle Events */

    public void onAwake () {
        boolean allAwakened = false;

        while ( !allAwakened ) {
            allAwakened = true;

            for ( Component component : this.components ) {
                ComponentState state = this.componentStates.get( component );

                if ( !state.awakened ) {
                    allAwakened = false;

                    state.awakened = true;

                    component.onAwake();
                }
            }
        }

        for ( Entity child : this.children ) {
            child.onAwake();
        }
    }

    public void syncEnableState () {
        if ( !this.enabled ) {
            return;
        }

        for ( Component component : this.components ) {
            ComponentState state = this.componentStates.get( component );

            if ( state.enabled && !component.enabled ) {
                component.onDisable();

                state.enabled = false;
            } else if ( !state.enabled && component.enabled ) {
                component.onEnable();

                state.enabled = true;
            }
        }

        for ( Entity child : this.children ) {
            child.syncEnableState();
        }
    }

    public void onUpdate () {
        this.children.purgeIterators();
        this.components.purgeIterators();

        this.syncEnableState();

        if ( this.enabled ) {
            for ( Component component : this.components ) {
                ComponentState state = this.componentStates.get( component );

                if ( state != null && component.enabled && state.awakened && !state.destroyed ) {
                    component.onUpdate();
                }
            }

            for ( Entity child : this.children ) {
                child.onUpdate();
            }

            for ( Component component : this.components ) {
                ComponentState state = this.componentStates.get( component );

                if ( state != null && component.enabled && state.awakened && !state.destroyed ) {
                    component.onAfterUpdate();
                }
            }
        }
    }

    public void onDraw () {
        if ( this.enabled ) {
            for ( Component component : this.components ) {
                ComponentState state = this.componentStates.get( component );

                if ( component.enabled && state.awakened && !state.destroyed ) {
                    component.onDraw();
                }
            }

            for ( Entity child : this.children ) {
                child.onDraw();
            }

            for ( Component component : this.components ) {
                ComponentState state = this.componentStates.get( component );

                if ( component.enabled && state.awakened && !state.destroyed ) {
                    component.onAfterDraw();
                }
            }
        }
    }

    public void onDestroy () {
        for ( Component component : this.components ) {
            this.destroyComponent( component );
        }

        for ( Entity child : this.children ) {
            child.onDestroy();
        }

        this.children.clear();
    }
}
