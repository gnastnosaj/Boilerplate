package com.github.gnastnosaj.boilerplate.cordova.crosswalk.plus;

import org.crosswalk.engine.XWalkCordovaView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jasontsang on 1/22/18.
 */

public interface HackyWebViewEngineWorkaround {
    List<HackyWebViewEngineWorkaround> WORKAROUND = new ArrayList<>();

    void hack(XWalkCordovaView webView, String url);
}