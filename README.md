# How to build

## If Android SDK is already installed (ANDROID_HOME is exported on PATH)

Release: `./gradlew build :app:assembleRelease`

Application in `app/build/outputs/apk/release`

## Build via docker

```
docker-compose -f build.yaml build
```

Application in `/home/app/build/outputs/apk/release`

**NB**: Building the application for the first time can take about 10 minutes.
Rebuild (provided that you are connecting to an already deployed container) - less than a minute.

config.json contains information about the address of your backend and the identifier of your application.
It is strongly recommended that these values not be changed.