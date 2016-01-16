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

import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author chentaov5@gmail.com
 */
public class JLLKReflectHelper {
    private static final String TAG = "JLLKReflectHelper";

    static Method getMethod(Class<?> clz, final String mtdName, Class<?>[] mtdArgs) {
        if (clz == null || TextUtils.isEmpty(mtdName) || mtdArgs == null) {
            return null;
        }
        Method mtd = null;
        try {
            mtd = clz.getDeclaredMethod(mtdName, mtdArgs);
            mtd.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return mtd;
    }

    static Method getMethod(final Class<?> clz, final String mtdName) {
        if (clz == null || TextUtils.isEmpty(mtdName)) {
            return null;
        }
        Method mtd = null;
        try {
            for (Method m : clz.getDeclaredMethods()) {
                if (m.getName().equals(mtdName)) {
                    mtd = m;
                    mtd.setAccessible(true);
                    break;
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return mtd;
    }

    static Field getField(Class<?> clz, final String fldName) {
        if (clz == null || TextUtils.isEmpty(fldName)) {
            return null;
        }
        Field fld = null;
        try {
            fld = clz.getDeclaredField(fldName);
            fld.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return fld;
    }
}
