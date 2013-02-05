package ca.etsmtl.applets.radio.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

public class CalendarCell extends Observable implements Collection<ActivityCalendar> {

    List<ActivityCalendar> events;

    Date date;

    public CalendarCell() {
	events = new ArrayList<ActivityCalendar>();
    }

    public CalendarCell(final Date date) {
	events = new ArrayList<ActivityCalendar>();
	this.date = date;
    }

    @Override
    public boolean add(final ActivityCalendar object) {
	return events.add(object);
    }

    @Override
    public boolean addAll(final Collection<? extends ActivityCalendar> arg0) {
	return events.addAll(arg0);
    }

    @Override
    public void clear() {
	events.clear();
    }

    @Override
    public boolean contains(final Object object) {
	return events.contains(object);
    }

    @Override
    public boolean containsAll(final Collection<?> arg0) {
	return events.containsAll(arg0);
    }

    public Date getDate() {
	return date;
    }

    public List<ActivityCalendar> getEvents() {
	return events;
    }

    @Override
    public boolean isEmpty() {
	return events.isEmpty();
    }

    @Override
    public Iterator<ActivityCalendar> iterator() {
	return events.iterator();
    }

    @Override
    public void notifyObservers() {
	this.notifyObservers(events);
    }

    @Override
    public boolean remove(final Object object) {
	return events.remove(object);
    }

    @Override
    public boolean removeAll(final Collection<?> arg0) {
	return events.removeAll(arg0);
    }

    @Override
    public boolean retainAll(final Collection<?> arg0) {
	return events.retainAll(arg0);
    }

    @Override
    public void setChanged() {
	super.setChanged();
    }

    public void setDate(final Date date) {
	this.date = date;
    }

    public void setEvents(final List<ActivityCalendar> events) {
	this.events = events;
    }

    @Override
    public int size() {
	return events.size();
    }

    @Override
    public Object[] toArray() {
	return events.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] array) {
	return events.toArray(array);
    }

}
