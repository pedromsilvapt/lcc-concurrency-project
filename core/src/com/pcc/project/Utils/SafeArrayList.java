package com.pcc.project.Utils;

import java.util.*;

public class SafeArrayList<E> extends ArrayList<E> {
    public class SafeArrayListIterator<E> implements Iterator<E> {
        private SafeArrayList<E> array;

        private ArrayList<E> remaining = null;

        private int cursor = 0;

        private boolean saved = false;

        public SafeArrayListIterator ( SafeArrayList<E> array ) {
            this.array = array;
            this.remaining = array;
        }

        public void secure () {
            if ( this.saved ) {
                return;
            }

            this.remaining = new ArrayList<>();

            for ( int i = this.cursor; i < this.array.size(); i++ ) {
                this.remaining.add( this.array.get( i ) );
            }

            this.cursor = 0;
        }

        @Override
        public boolean hasNext () {
            return this.cursor < this.array.size();
        }

        @Override
        public E next () {
            if ( this.cursor + 1 >= this.remaining.size() ) {
                this.array.removeIterator( this );
            }

            return this.array.get( this.cursor++ );
        }
    }

    private Set<SafeArrayListIterator<E>> iterators = new HashSet<>();

    private void addIterator ( SafeArrayListIterator<E> iterator ) {
        this.iterators.add( iterator );
    }

    @SuppressWarnings( "unchecked" )
    private void removeIterator ( Object iterator ) {
        this.iterators.remove( iterator );
    }

    @Override
    public boolean add ( E e ) {
        this.iterators.forEach( SafeArrayListIterator::secure );
        this.iterators.clear();

        return super.add( e );
    }

    @Override
    public boolean remove ( Object o ) {
        this.iterators.forEach( SafeArrayListIterator::secure );
        this.iterators.clear();

        return super.remove( o );
    }

    @Override
    public Iterator< E > iterator () {
        SafeArrayListIterator<E> iterator = new SafeArrayListIterator<E>( this );

        this.addIterator( iterator );

        return iterator;
    }
}
