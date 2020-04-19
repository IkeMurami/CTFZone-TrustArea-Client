# How to build

Release: `./gradlew build :app:assembleRelease`

Debug: `./gradlew build :app:assembleDebug`

Application in `app/build/outputs/apk/release`

# Clean build cache directory

`./gradlew cleanBuildCache`

# Clear build

`./gradlew clean`



# How to run tests
```
Test Registration:
adb shell am startservice -a <applicationId>.action.TEST_REGISTER -e USER_NAME testuser -e FIRST_NAME firstname_testuser -e LAST_NAME lastname_testuser <applicationId>/com.zfr.ctfzoneclient.service.view.TestService
```

