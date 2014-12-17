Simple Java Sync App
====================

This app was written to scratch an itch - The itch of synchronising a directory
tree to/from Rackspace CloudFiles.

It's written in Java and uses Apache jClouds (http://jclouds.apache.org). It
currently only supports containers hosted in London, but if you want to add
support for other storage locations you can easily do so by modifying the
build.gradle to include the jClouds support for the other region and adding
in the alias to src/main/java/com/funkyandroid/sync/rackspace/cloudfiles/CloudFilesUtils.java.

The application is designed to be run from the command line, operates in a
single thread, and is not optimised for speed. It's designed to be run as
a nightly job and left to run.

License
-------

Copyright (c) 2014 Funky Android Ltd.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions
of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


Building
--------

This repository contains the gradle build file and wrapper needed to build
the application. To build it do;

./gradlew clean releaseJar

Running
-------

The application takes two or three arguments;

java -jar build/libs/cf-sync-release-1.0.0.jar [-d] source destination

where;

-d indicates the files should be deleted from the source after synchronisation

and source and destination take the following form;

For a CloudFiles container: cloudfiles://[[username:apikey@]region]/bucket_name
(e.g. cloudfiles://me:myapikey@london/continaer)

For a local directory: path
(e.g. /tmp)

Output
------

The application will print a line for each file it checks telling you whether
or not the file was synchronised or skipped because the destination has an
up to date copy of it.

Synchronising method
--------------------

Rackspace CloudFiles uses the MD5 hash of the file as its eTag, so this app
uses the MD5 of the local file and the eTag from CloudFiles to check for
differences between the files. If a difference is found the file, or the file
does not exist in the destination, it is copied.
