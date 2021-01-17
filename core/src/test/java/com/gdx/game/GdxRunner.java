package com.gdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import org.junit.jupiter.api.extension.Extension;
import org.junit.platform.launcher.TestExecutionListener;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Allow to run test on Gdx app logic.
 */
public class GdxRunner implements ApplicationListener, Extension {

    private final Map<Method, TestExecutionListener> invokeInRender = new HashMap<>();

    public GdxRunner() {
        HeadlessApplicationConfiguration conf = new HeadlessApplicationConfiguration();

        new HeadlessApplication(this, conf);
    }

    @Override
    public void create() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void render() {
        synchronized (invokeInRender) {
            for (Map.Entry<Method, TestExecutionListener> each : invokeInRender.entrySet()) {
                runChild(each.getKey(), each.getValue());
            }
            invokeInRender.clear();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void dispose() {
    }

    protected void runChild(Method method, TestExecutionListener testExecutionListener) {
        synchronized (invokeInRender) {
            //add for invoking in render phase, where gl context is available
            invokeInRender.put(method, testExecutionListener);
        }
        //wait until that test was invoked
        waitUntilInvokedInRenderMethod();
    }

    private void waitUntilInvokedInRenderMethod() {
        try {
            while (true) {
                Thread.sleep(10);
                synchronized (invokeInRender) {
                    if (invokeInRender.isEmpty()) break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
