# UpdateCenter

[![pipeline status](https://gitlab.com/unitedclassifiedsapps/updatecenter.android/badges/master/pipeline.svg))](https://gitlab.com/unitedclassifiedsapps/updatecenter.android/commits/master)
[![coverage report](https://gitlab.com/unitedclassifiedsapps/updatecenter.android/badges/master/coverage.svg)](https://gitlab.com/unitedclassifiedsapps/updatecenter.android/coverage/html)

An app update checking library.

## Download

Add the jitpack.io repository:
```gradle
allprojects {
 repositories {
    jcenter()
    maven { url "https://jitpack.io" }
 }
}
```
and:
```gradle
dependencies {
    compile 'com.github.jitpack:updatecenter.android:1.0.1'
}
```

## Documentation

[Documentation](http://inzercia-mob-app.azet.gitlab/updatecenter.android/docs/javadoc/updatecenter-lib)

## How to use

### Firebase configuration
Setup your app with Firebase as shown [here][2]

Add Firebase remote config dependency:
```
implementation 'com.google.firebase:firebase-config:15.0.2'
```

Via Firebase console define following [parameters][3]:
```
key:update_center_must_update value:[true/false]
key:update_center_should_update value:[true/false]
key:update_center_latest_version value:[semantic version string ex.: 1.2.3]
```

By setting up Remote Config Conditions you can control which version gets what value.

### In app use
Show in [sample app][1]

Simply initialize UpdateCenter with provided FirebaseVersionDataSource:
```kotlin
val updateCenter = UpdateCenter(FirebaseVersionDataSource(context, FirebaseRemoteConfig.getInstance(), BuildConfig.VERSION_NAME), object : OnVersionCheckedListener {

            override fun mustUpdate(currentVersion: String, latestVersion: String) {
                //startMustUpdateActivity()
            }

            override fun shouldUpdate(currentVersion: String, latestVersion: String) {
                //startShouldUpdateActivity()
            }

            override fun notLatestVersion(currentVersion: String, latestVersion: String) {
                //showNewVersionSnack()
            }

            override fun onError(currentVersion: String, exception: Exception?) {
                //log error
            }
        })
```
and call check() method
```kotlin
updateCenter.check()
```

Based on your Firebase remote config configuration the appropriate OnVersionCheckedListener callback will be invoked.

## Compatibility
 * **Minimum Android SDK**: UpdateCenter requires a minimum API level of 16.


[1]: sample-app
[2]: https://firebase.google.com/docs/android/setup
[3]: https://firebase.google.com/docs/remote-config/parameters