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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.github.jllk.multidex.JLLKMultiDexInstaller;

import java.lang.reflect.Method;

/**
 * @author chentaov5@gmail.com
 */
public class JLLKInstrumentationProxy extends Instrumentation {

    private static final String TAG = "JLLKInstrumentationProxy";

    private Method mMtd_execStartActivity;

    private Instrumentation mInstrumentation;

    public JLLKInstrumentationProxy(Instrumentation instrumentation) {
        mInstrumentation = instrumentation;
        mMtd_execStartActivity = JLLKReflectHelper.getMethod(mInstrumentation.getClass(), "execStartActivity");
    }

    private ActivityResult execStartActivityProxy(Context who, Intent intent, IExecStartActivityDelegate delegate) {
        String className = null;
        if (intent.getComponent().getClassName() == null) {
            ResolveInfo resolveInfo = getContext().getPackageManager()
                    .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfo != null) {
                className = resolveInfo.activityInfo.name;
            }
        } else {
            className = intent.getComponent().getClassName();
        }
        Log.d(TAG, "[execStartActivityProxy] " + className);

        // Install secondary dex by index.
        int dexIdx = JLLKMultiDexHook.getModuleDexIdx(className);
        if (dexIdx > 0) {
            JLLKMultiDexInstaller.installOne(who, dexIdx);
        }
        return delegate.execStartActivity();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public ActivityResult execStartActivity(
            Context who,
            IBinder contextThread,
            IBinder token,
            Activity target,
            Intent intent,
            int requestCode,
            Bundle bundle) {
        return execStartActivityProxy(
                who,
                intent,
                new ExecStartActivityDelegateV17(who, contextThread, token, target,
                        intent, requestCode, bundle));
    }

    class ExecStartActivityDelegateV17 implements IExecStartActivityDelegate {

        final Context who;
        final IBinder contextThread;
        final IBinder token;
        final Activity target;
        final Intent intent;
        final int requestCode;
        final Bundle bundle;

        public ExecStartActivityDelegateV17(
                Context who,
                IBinder contextThread,
                IBinder token,
                Activity target,
                Intent intent,
                int requestCode,
                Bundle bundle){
            this.who = who;
            this.bundle = bundle;
            this.token = token;
            this.target = target;
            this.intent = intent;
            this.requestCode = requestCode;
            this.contextThread = contextThread;
        }

        @Override
        public ActivityResult execStartActivity() {
            try {
                return (ActivityResult) mMtd_execStartActivity.invoke(
                        mInstrumentation, who, contextThread, token,
                        target, intent, requestCode, bundle);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage(), e);
            }
            return null;
        }
    }

    interface IExecStartActivityDelegate {
        ActivityResult execStartActivity();
    }

}
