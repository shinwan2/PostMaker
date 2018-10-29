PostMaker
=========

Simple app to post content for Android. This app utilizes Firebase as backend services, i.e.
Firebase SDK Authentication and Firebase Realtime Database. Signed in users can post to a timeline,
view posts, and delete his/her own post.

### How to run
1. You need to set up your own Firebase Project in [Firebase Console](https://console.firebase.google.com).
    Spark plan is free with a certain limit.
2. Once set up, download and place your `google-services.json` in `/app` directory.
3. Build and run this project.

### Tested Environment
1. Android Device minimum API Level 19
2. Built using Android Studio 3.2.1

### Improvements
1. Add push notification after any post/batch of posts are submitted. <br />
    Currently user needs to manually refresh once his/her post are submitted from the same device.
2. Edit profile, upload photo to Firebase Storage.
3. Manage your own posts.

License
=======

    Copyright 2018 shinwan2@gmail.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License
