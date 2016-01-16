# multidex-hook

A android library for multidex to lazy install in the right time.


# Installation

`build.gradle`


```groovy
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile "com.github.jllk:multidex-hook:0.0.1-beta@aar"
}
```

`Application`:

```scala
class SampleApp extends Application {

  override def attachBaseContext(base: Context): Unit = {
    super.attachBaseContext(base)
    // SampleModuleMgr.getConfig returns Map[module, dexIdx]
    JLLKMultiDexHook.lazyInstall(SampleModuleMgr.getConfig)
  }
}
```

# See also

multidex-maker: [https://github.com/JLLK/multidex-maker](https://github.com/JLLK/multidex-maker)

multidex-installer: [https://github.com/JLLK/multidex-installer](https://github.com/JLLK/multidex-installer)

multidex-sample: [https://github.com/JLLK/multidex-sample](https://github.com/JLLK/multidex-sample)

# License

This lib is licensed under Apache License 2.0. See LICENSE for details.