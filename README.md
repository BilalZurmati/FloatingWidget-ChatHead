Floating Widget (Android-ChatHead)
===========

Kotlin Version of [Android-ChatHead](https://github.com/henrychuangtw/Android-ChatHead)


ChatHead for Android app, like FB messenger.

<br/><br/>

How to Use
-----------
Start ChatHead :
```Kotlin
ContextCompat.startForegroundService(this@Main, ChatHeadService::class.java)
```
<br/>
Click on Show Message to start the floating widget service like Facebook messenger

<br/>
When chat head is long pressed then an exit view is visible to drag the widget there to stop the ChatHead :<br/>

Orientation
-----------
**Support landscape screenOrientation**<br/>
when screenOrientation change to portrait or landscape, ChatHead will reposition automatically.
<br/>

