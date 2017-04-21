package com.github.gnastnosaj.boilerplate.rxbus;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by jason on 10/14/2015.
 */
public class RxBus {
    private static RxBus rxBus;

    public static RxBus getInstance() {
        if (rxBus == null) {
            rxBus = new RxBus();
        }
        return rxBus;
    }

    private final Subject bus = PublishSubject.create();

    public Observable<Object> toObserverable() {
        return bus;
    }

    public void send(Object o) {
        bus.onNext(o);
    }

    private final ConcurrentHashMap<Object, List<Subject>> subjectMapper = new ConcurrentHashMap<>();

    public <T> Observable<T> register(@NonNull Object tag, @NonNull Class<T> c) {
        List<Subject> subjectList = subjectMapper.get(tag);
        if (null == subjectList) {
            subjectList = new ArrayList<>();
            subjectMapper.put(tag, subjectList);
            send(new RxBusEvent(tag, RxBusEvent.CREATE));
        }
        Subject subject = PublishSubject.create();
        subjectList.add(subject);
        send(new RxBusEvent(tag, RxBusEvent.ADD, subject));
        return subject;
    }

    public void unregister(@NonNull Object tag, @NonNull Observable observable) {
        List<Subject> subjectList = subjectMapper.get(tag);
        if (null != subjectList) {
            subjectList.remove((Subject) observable);
            send(new RxBusEvent(tag, RxBusEvent.REMOVE, observable));
            if (subjectList.isEmpty()) {
                subjectMapper.remove(tag);
                send(new RxBusEvent(tag, RxBusEvent.DESTROY));
            }
        }
    }

    public <T> void post(@NonNull Object tag, @NonNull T content) {
        List<Subject> subjectList = subjectMapper.get(tag);
        if (null != subjectList) {
            for (Subject subject : subjectList) {
                subject.onNext(content);
            }
        }
    }

    public static class RxBusEvent {
        public final static int CREATE = 0;
        public final static int ADD = 1;
        public final static int REMOVE = 2;
        public final static int DESTROY = 3;

        private Object tag;
        private int type;
        private Observable observable;

        private RxBusEvent(Object tag, int type) {
            this(tag, type, null);
        }

        private RxBusEvent(Object tag, int type, Observable observable) {
            this.tag = tag;
            this.type = type;
            this.observable = observable;
        }

        public Object getTag() {
            return tag;
        }

        public int getType() {
            return type;
        }

        public Observable getObservable() {
            return observable;
        }
    }
}