package com.github.gnastnosaj.boilerplate.rxbus;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private final Subject bus = PublishSubject.create().toSerialized();

    public Observable<Object> toObserverable() {
        return bus;
    }

    public void send(Object o) {
        bus.onNext(o);
    }

    private final Map<Object, List<Subject>> subjectMapper = new HashMap<>();

    public <T> Observable<T> register(@NonNull Object tag, @NonNull Class<T> c) {
        Subject subject = PublishSubject.create().toSerialized();

        synchronized (subjectMapper) {
            List<Subject> subjectList = subjectMapper.get(tag);
            if (null == subjectList) {
                subjectList = new CopyOnWriteArrayList<>();
                subjectMapper.put(tag, subjectList);
                send(new RxBusEvent(RxBusEvent.CREATE, tag));
            }

            subjectList.add(subject);
            send(new RxBusEvent(RxBusEvent.ADD, tag, subject));
        }

        return subject;
    }

    public void unregister(@NonNull Object tag, @NonNull Observable observable) {
        synchronized (subjectMapper) {
            List<Subject> subjectList = subjectMapper.get(tag);
            if (null != subjectList) {
                subjectList.remove(observable);
                send(new RxBusEvent(RxBusEvent.REMOVE, tag, observable));

                if (subjectList.isEmpty()) {
                    subjectMapper.remove(tag);
                    send(new RxBusEvent(RxBusEvent.DESTROY, tag));
                }
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

        private int type;
        private Object tag;
        private Observable observable;

        private RxBusEvent(int type, Object tag) {
            this(type, tag, null);
        }

        private RxBusEvent(int type, Object tag, Observable observable) {
            this.type = type;
            this.tag = tag;
            this.observable = observable;
        }

        public int getType() {
            return type;
        }

        public Object getTag() {
            return tag;
        }

        public Observable getObservable() {
            return observable;
        }
    }
}