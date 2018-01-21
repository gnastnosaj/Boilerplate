package com.github.gnastnosaj.boilerplate.cordova.crosswalk.plus;

/**
 * Created by jasontsang on 1/21/18.
 */

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class HackyWebViewEngineAspect {

    @Before("execution(* org.xwalk.core.XWalkView.load(java.lang.String, java.lang.String))")
    public void hacky(JoinPoint joinPoint) throws Throwable {
        Log.d("HackyWebViewEngine", "aspect before:" + joinPoint.getSignature());
    }
}