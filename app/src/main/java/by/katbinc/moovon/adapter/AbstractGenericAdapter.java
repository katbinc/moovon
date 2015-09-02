package by.katbinc.moovon.adapter;

/**
 * Created by katb on 01.09.15.
 */

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractGenericAdapter<T> extends BaseAdapter {

    protected List<T> mObjects = new ArrayList<T>();

    public int getCount() {
        return mObjects.size();
    }

    public Object getItem(int position) {
        return getObject(position);
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * calls notifyDataSetChanged()
     */
    public void invalidate() {
        super.notifyDataSetChanged();
    }

    /**
     * Sets objects for this adapter. Call to this method also calls invalidate();
     */
    public void setObjects(List<T> objects) {
        this.mObjects = new ArrayList<T>(objects);
        invalidate();
    }

    /**
     * Adds objects to this adapter. Call to this method also calls invalidate();
     */
    public void addObjects(List<T> objects) {
        this.mObjects.addAll(new ArrayList<T>(objects));
        invalidate();
    }

    /**
     * Adds single object to this adapter. Call to this method also calls invalidate();
     */
    public void add(T object) {
        this.mObjects.add(object);
        invalidate();
    }

    /**
     * Adds single object to this adapter. Call to this method also calls invalidate();
     */
    public void addFirst(T object) {
        this.mObjects.add(0, object);
        invalidate();
    }

    /**
     * Deletes single object from this adapter. Call to this method also calls invalidate();
     */
    public void remove(T object) {
        this.mObjects.remove(object);
        invalidate();
    }

    /**
     * Returns underlying object list.
     */
    public List<T> getObjects() {
        return Collections.unmodifiableList(mObjects);
    }

    /**
     * A typed version of {@link #getItem(int)}.
     */
    public T getObject(int position) {
        return mObjects.get(position);
    }

    /**
     * A version of {@link #getObject(int)} but with long argument.
     */
    public T getObject(long position) {
        return getObject((int) position);
    }
}
