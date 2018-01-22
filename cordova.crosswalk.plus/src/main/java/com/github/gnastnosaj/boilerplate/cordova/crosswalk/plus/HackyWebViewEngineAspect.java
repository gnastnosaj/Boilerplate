package com.github.gnastnosaj.boilerplate.cordova.crosswalk.plus;

/**
 * Created by jasontsang on 1/21/18.
 */

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.crosswalk.engine.XWalkCordovaView;

@Aspect
public class HackyWebViewEngineAspect {

    @Before("execution(* org.xwalk.core.XWalkView.load(java.lang.String, java.lang.String))")
    public void hacky(JoinPoint joinPoint) throws Throwable {
        for (HackyWebViewEngineWorkaround workaround : HackyWebViewEngineWorkaround.WORKAROUND) {
            workaround.hack((XWalkCordovaView) joinPoint.getThis(), (String) joinPoint.getArgs()[0]);
        }
    }
}