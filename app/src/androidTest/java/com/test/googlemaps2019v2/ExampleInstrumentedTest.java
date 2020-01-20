package com.test.googlemaps2019v2;

import android.content.Context;
<<<<<<< HEAD
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
=======
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
>>>>>>> Migrate to Android X and AutoCompleteTV

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.test.googlemaps2019", appContext.getPackageName());
    }
}
