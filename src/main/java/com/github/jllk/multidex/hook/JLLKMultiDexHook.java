/*
 * Copyright (C) 2016 chentaov5@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jllk.multidex.hook;

import android.app.Instrumentation;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author chentaov5@gmail.com
 */
public class JLLKMultiDexHook {

    private static final String TAG = "JLLKMultiDexHook";

    private static Map<String, Integer> sModule2DexIdx;

    /**
     * Install dex in right time.
     *
     * @param module2DexIdx Module Activity fullName with DexIndex
     */
    public static void lazyInstall(Map<String, Integer> module2DexIdx) {
        if (module2DexIdx == null) {
            throw new IllegalArgumentException("[JLLKMultiDexHook] lazyInstall args can't be null.");
        }
        sModule2DexIdx = module2DexIdx;
        hook();
    }

    static int getModuleDexIdx(String name) {
        int ret = 0;
        if (sModule2DexIdx.containsKey(name)) {
            ret = sModule2DexIdx.get(name);
        }
        Log.d(TAG, "[JLLKMultiDexHook] getModuleDexIdx: " + ret);
        return ret;
    }

    private static void hook() {
        Log.d(TAG, "[JLLKMultiDexHook] hook..");
        try {
            Class<?> clz_ActivityThread =  Class.forName("android.app.ActivityThread");
            Method mtd_currentActivityThread = JLLKReflectHelper.getMethod(clz_ActivityThread, "currentActivityThread");
            Object obj_sCurrentActivityThread = mtd_currentActivityThread.invoke(null);

            Field fld_mInstrumentation = JLLKReflectHelper.getField(clz_ActivityThread, "mInstrumentation");
            Object obj_mInstrumentation = fld_mInstrumentation.get(obj_sCurrentActivityThread);

            JLLKInstrumentationProxy instrumentationProxy = new JLLKInstrumentationProxy((Instrumentation) obj_mInstrumentation);
            fld_mInstrumentation.set(obj_sCurrentActivityThread, instrumentationProxy);

        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
        }
        Log.d(TAG, "[JLLKMultiDexHook] hook done.");
    }
}
