package edu.stanford.protege.csv.export.ui;

import javax.swing.*;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Rafael Gonçalves <br>
 * Center for Biomedical Informatics Research <br>
 * Stanford University
 */
public class SortedListModel<E> extends AbstractListModel<E> {
    private SortedSet<E> model = new TreeSet<>();

    /**
     * No-arguments constructor
     */
    public SortedListModel() { }

    @Override
    public int getSize() {
        return model.size();
    }

    @Override
    public E getElementAt(int index) {
        checkNotNull(index);
        return (E) model.toArray()[index];
    }

    public void add(E element) {
        checkNotNull(element);
        if (model.add(element)) {
            fireContentsChanged(this, 0, getSize());
        }
    }

    public void addAll(E elements[]) {
        checkNotNull(elements);
        Collection<E> c = Arrays.asList(elements);
        addAll(c);
    }

    public void addAll(Collection<E> elements) {
        checkNotNull(elements);
        model.addAll(elements);
        fireContentsChanged(this, 0, getSize());
    }

    public void clear() {
        model.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public boolean contains(E element) {
        checkNotNull(element);
        return model.contains(element);
    }

    public E firstElement() {
        return model.first();
    }

    public Iterator<E> iterator() {
        return model.iterator();
    }

    public E lastElement() {
        return model.last();
    }

    public boolean removeElement(E element) {
        checkNotNull(element);
        boolean removed = model.remove(element);
        if (removed) {
            fireContentsChanged(this, 0, getSize());
        }
        return removed;
    }
}