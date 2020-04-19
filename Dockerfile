FROM thyrlian/android-sdk-vnc

WORKDIR /home
COPY . .
RUN ./gradlew build :app:assembleRelease



