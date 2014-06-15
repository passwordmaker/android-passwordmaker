Passwordmaker Pro for Android
===========

This is the android implementation of the Passwordmaker Pro algorithm designed by [Passwordmaker.org](http://passwordmaker.org).

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

For bug reports please see the https://github.com/tasermonkey/android-passwordmaker/issues tracker.

Feel free to create a pull request to fix a bug or add a feature yourself!

Compiling
==========
* You will ofcourse need to download the (Android SDK)[http://developer.android.com/sdk/index.html#download]
* Download and install an IDE like (IntelliJ)[http://www.jetbrains.com/idea/] with its plugin for Android.

I now set this up using the gradle build process.  I use the Intellij IDE which makes the process really easy.  Just
install the Intellij Android plugin and import this project after cloning this repository.  Use the import from external
model, then choose "gradle".  It should take care of the rest.  Intellij created gradle wrappers which I believe you can
just run `./gradlew <task>`

See `./gradlew tasks` for all of the tasks you wish to do.

For example to run the Android Test cases, run: `./gradlew connectedAndroidTest`

Though I heavily suggest using an IDE like Intellij or Eclipse.

TODO: Step by step instructions on how to compile/run this project from a fresh install.

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
    - See the Java library code repository here: (passwordmaker-je-lib)[http://github.com/tasermonkey/passwordmaker-je-lib]
    - The original source came from: http://code.google.com/p/passwordmaker-je/
      - Thanks to Dave Marotti for this
  * Version Code strategy:
    - `<major>.<minor>.<revision>` -> code: First digit is major, second two digits is minor, and last two digits are revision.
    - Example: for `2.13.4` the code would be `21304`  This way as the version number increases the code will also be a bigger number
    - This is different for the first 10 released versions which were just the release numbers.
