package com.silicornio.googlyeyessqlite;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class GooglyEyesOrmLiteTests {

    @Test
    public void useAppContext() throws Exception {

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.silicornio.googlyeyessqlite", appContext.getPackageName());
    }

    //----- TESTS -----

    @Test
    public void test001AddItem(){
        assertEquals(1, 1);
    }

    @Test
    public void test001GetItem(){
        assertEquals(1, 1);
    }

    @Test
    public void test001UpdateItem(){
        assertEquals(1, 1);
    }

    @Test
    public void test001DeleteItem(){
        assertEquals(1, 1);
    }
}
