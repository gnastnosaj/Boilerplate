package com.github.gnastnosaj.boilerplate.conceal.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.gnastnosaj.boilerplate.conceal.service.ConcealService;
import com.github.gnastnosaj.boilerplate.conceal.service.GuardService;

/**
 * Created by jasontsang on 9/20/17.
 */

public class ConcealRuntimeProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        getContext().startService(new Intent(getContext(), ConcealService.class));
        getContext().startService(new Intent(getContext(), GuardService.class));
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
