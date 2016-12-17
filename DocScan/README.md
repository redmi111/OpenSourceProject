DocScan
===============

This little application provides a way on scanning handwritten notes and printed documents.

It automatically detect the edge of the paper over a contrastant surface.

It automatically detects the QR Code printed on the bottom right corner and scans the page immediately.

After the page is detected, it compensates any perspective from the image adjusting it to a 90 degree top view and saves it on a folder on the device.

It is also possible to launch the application from any other application that asks for a picture, just make sure that there is no default application associated with this action.

Requirements
------------

Because of the version of OpenCV that is used in the project it needs to run on Android 5.0 (lollipop) or newer.

In order to capture and manipulate images Doc Scan depends on having the OpenCV Manager application installed. If not installed Doc Scan will ask to download it from https://github.com/ctodobom/OpenCV-3.1.0-Android or from Google Play Store.

Instructions for building
-------------------------

### Android Studio

Import the project from GitHub using File -> New -> Project from Version Control -> GitHub, fill the URL https://github.com/thgunner/OpenSourceProject/tree/master/DocScan

It will ask for a base directory, normally AndroidStudioProjects, you can change it to your preference.

After this the Doc Scan can be built.


### Command Line

Go to your base folder and import it using ```git```:

```
$ git clone https://github.com/thgunner/OpenSourceProject/tree/master/DocScan
```

This should import the Doc Scan repository in DocScan folder

You need to point the environment variable ```ANDROID_HOME``` to your Android SDK folder and run ```gradle``` to build the project:

```
$ cd DocScan
$ export ANDROID_HOME=~/android-sdk-linux
$ ./gradlew assembleRelease
```


History
-------

I've started this app on a brazilian holyday "extended weekend" based on the fact that I was unable to find any open source application that does this job. I was mainly inspired on the RocketBook Wave closed source application.

I really do not know if I will extend more the application, but I am writing bellow some objectives to make it better.

Roadmap
-------

* enhance the image gallery of scanned documents
* register a share action in order to obtain documents already pictured through standard camera apps
* implement automatic action based on the RocketBook Wave marking of the page

Contributing
------------

If you have any idea, feel free to fork it and submit your changes back to me.

Thanks
------
### External code

This application wouldn't be possible without the great material produced by the community. I would like to give special thanks to the authors of essencial parts I've got on the internet and used in the code:
* [Claudemir Todo Bom  /open source Open note scanner] https://github.com/ctodobom/OpenNoteScanner.git
* [Android-er / GridView code sample](http://android-er.blogspot.com.br/2012/07/gridview-loading-photos-from-sd-card.html)
* [Android Hive / Full Screen Image pager](http://www.androidhive.info/2013/09/android-fullscreen-image-slider-with-swipe-and-pinch-zoom-gestures/)
* [Adrian Rosebrock from pyimagesearch.com for the excellent tutorial on how to handle the images](http://www.pyimagesearch.com/2014/09/01/build-kick-ass-mobile-document-scanner-just-5-minutes/)
* [Gabriele Mariotti / On how to implement sections in the RecyclerView](https://gist.github.com/gabrielemariotti/e81e126227f8a4bb339c)


License
-------

Copyright 2016 - GunnerPro

Software licensed under the GPL version 2 available in GPLv3.TXT and
online on http://www.gnu.org/licenses/gpl.txt.

Use parts from other developers, sometimes with small changes,
references on autorship and specific licenses are on individual
source files.
