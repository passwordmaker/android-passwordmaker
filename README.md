Passwordmaker Pro for Android
===========

This is the android implementation of the Passwordmaker Pro algorithm designed by [Passwordmaker.org](http://passwordmaker.org). View the [Android public webpage](http://android.passwordmaker.org).

How it works:
You provide PasswordMaker two pieces of information: a "master password" -- that one, single password you like -- and
the URL of the website requiring a password. Through the magic of one-way hash algorithms, PasswordMaker calculates a
message digest(hash), also known as a digital fingerprint, which can be used as your password for the website.
Although one-way hash algorithms have a number of interesting characteristics, the one capitalized by PasswordMaker
is that the resulting fingerprint (password) does "not reveal anything about the input that was used to generate it."
In other words, if someone has one or more of your generated passwords, it is computationally infeasible for him
to derive your master password or to calculate your other passwords. Computationally infeasible means even computers
like this won't help!

There are the same tools that you can download for many of the popular browsers, and other mobile devices.  See the
[Passwordmaker.org](http://passwordmaker.org) website for more information.

For bug reports please see the [issue tracker](https://github.com/passwordmaker/android-passwordmaker/issues).

Feel free to create a pull request to fix a bug or add a feature yourself!

Users
======
Welcome to passwordmaker!  If you haven't already checkout [Passwordmaker.org](http://passwordmaker.org) to know what password maker is all about.

If you have an issues or questions, with the Android version of PasswordMaker Pro, go to the [Issue Tracker](https://github.com/passwordmaker/android-passwordmaker/issues)

Have a comment, question, or issue that you want to say more privately not on the issue tracker, feel free to [email](mailto:pwdmkrpro.android.84a75@tasermonkeys.com) me.

[Developer's keybase.io account](https://keybase.io/jstapleton)

Developers
==========
If you are not a developer you probably don't need to read any farther down this file.

Repository layout
==================
This project uses the [Git Flow](http://nvie.com/posts/a-successful-git-branching-model/) layout.  You can download a helper plugin for git from https://github.com/nvie/gitflow repository.

Compiling
==========
* You will ofcourse need to download the [Android SDK](http://developer.android.com/sdk/index.html#download)
* Download and install an IDE like [IntelliJ](http://www.jetbrains.com/idea/) with its plugin for Android.
* Until [passwordmaker-je-lib](https://github.com/passwordmaker/java-passwordmaker-lib) makes it into maven central, check it out and do a mvn install on it first.

I now set this up using the gradle build process.  I use the Intellij IDE which makes the process really easy.  Just
install the Intellij Android plugin and import this project after cloning this repository.  Use the import from external
model, then choose "gradle".  It should take care of the rest.  Intellij created gradle wrappers which I believe you can
just run `./gradlew <task>`

See `./gradlew tasks` for all of the tasks you wish to do.

For example to run the Android Test cases, run: `./gradlew connectedAndroidTest`

Though I heavily suggest using an IDE like Intellij or Eclipse.

Step by Step Compiling in the commandline
===========
### Step 1: get build, and install passwordmaker-je

    git clone https://github.com/tasermonkey/passwordmaker-je-lib.git
    cd passwordmaker-je-lib
    git checkout 0.9.3
    mvn install

### Step 2: get and build android passwordmaker

    # the cd .. is just to go to the same parent directory as the passwordmaker-je-lib to checkout the android code
    cd ..
    git clone https://github.com/tasermonkey/android-passwordmaker.git
    git checkout release/v2.0.0
    cd android-passwordmaker/passwordmaker
    ../gradlew assembleDebug

### Step 3: apk
This should have built an apk inside of the  android-passwordmaker/passwordmaker/build/apk directory: passwordmaker-debug.apk

To use gradle to install this, run:

    ../gradlew installDebug

To assemble the release mode, you run the task `assembleRelease` and to install: `installRelease`.  However in order to build this you need to setup the signing.

Signing
==========
In order to build this project you need to setup signing.  I can't just include the signing keys in the git repo because that would mean anyone could sign as me.
So you will need to generate your own signing keys. See [Android signing help](http://developer.android.com/tools/publishing/app-signing.html)

Then from the `android-passwordmaker/passwordmaker` you need to setup your Environment by running the script:

    source set_signing_env_vars.sh

This script is required to be 'sourced' from your shell whenever you open up a new shell, and be source because it adds 4 environment variables to your session.
<br/>NOTE: This seems like a pretty decent way to do this, as the password will never be visible.  Also never stored on disk.  However, maybe somehow invent a gpg way of storing the password and config info.


Notes
======
  * Why does PasswordMakerPro require using `spongycastle`?
    - Because Android provides part of `BouncyCastle`, but not all of it.  And some of the Hash algorithms used by passwordmaker didn't make the cut.
    - Spongycastle is basically just `BouncyCastle` moved to a different package.  Then renamed to `SC` as its provider name.
  * Why did you need to Shim out the XmlWriter interface?
    - Because once against, Android moved out some of the `javax.xml.streaming.*` classes and it doesn't work.
    - So the solution was to just shim out the interface, then have it use the xmlpull package that comes with Android.
  * Why did you want to move to use the java library `passwordmaker-je`?
    - This way any improvements to the core can be made to both the standard `java edition` and the android edition!
    - See the Java library code repository here: [passwordmaker-je-lib](http://github.com/tasermonkey/passwordmaker-je-lib)
    - The original source came from: http://code.google.com/p/passwordmaker-je/
      - Thanks to Dave Marotti for this
  * Version Code strategy:
    - `<major>.<minor>.<revision>` -> code: First digit is major, second two digits is minor, and last two digits are revision.
    - Example: for `2.13.4` the code would be `21304`  This way as the version number increases the code will also be a bigger number
    - This is different for the first 10 released versions which were just the release numbers.
  * What the blank, mvn and gradle?
    - Yeah, until I fixed up the build for the passwordmaker-je-lib more, it is using maven.
    - And this project is using gradle, cause really, I like gradle, other than its slow to startup.  But it works well with android dev.

Deploying using the android developer webpage and Android Studio
=======================
1. In Android Studio go to action: `Generate Signed Bundled`
1. Choose `Android App Bundle`
1. Locate keystore, select alias `passwordmakerproforandroid` and type in the correct password.
1. Select destination folder and use build type `releases`
1. Locate the release bundle (Android Studio should prompt for it): something named `passwordmaker.aab`
1. Log into Android developer Play console: https://play.google.com/apps/publish
1. Within the application for passwordmaker go to the `Release management` / `App Releases`
1. Select `Manage` from the `Production` track
1. Click on the button `Create Release`
1. Give it the signed bundle generated.
1. Ensure the internal version is reasonable.
1. give a short description on what changed
1. Click Save, followed by 'Review'
1. Let it get deployed.


Status of this project
=======================

## Upate 2014-07-17 by @tasermonkey

Release [Version 2.0.1](https://github.com/passwordmaker/android-passwordmaker/releases/tag/v2.0.1)

# Note on Patches/Pull Requests:

* Fork the project.
* Make your feature addition or bug fix.
* Commit, do not mess with version in build.gradle
  (if you want to have your own version, that is fine but bump version in a commit by itself I can ignore when I pull)
* Send me a pull request. Bonus points for feature branches.

Copyright
==========

Copyright (c) 2010 [James Stapleton](https://keybase.io/jstapleton) and [PasswordMaker.org](http://passwordmaker.org) . See LICENSE for details. A list of all contributors can be found at the [contributors](http://github.com/passwordmaker/android-passwordmaker/contributors) page.
