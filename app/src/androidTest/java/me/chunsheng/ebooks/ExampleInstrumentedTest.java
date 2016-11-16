package me.chunsheng.ebooks;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        URL markdownUrl;
        try {
            markdownUrl = new URL("https://github.com/rongchang/android_app/blob/master/README.md");
            Log.e("TestTag:","markdownUrl:"+markdownUrl.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TestTag:","error:");
        }

        assertEquals("me.chunsheng.ebooks", appContext.getPackageName());
    }
}
