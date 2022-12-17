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
Stop ChatHead(see below pic 'LongPressing to Stop') :<br/>
long click and move chat-head to the round on bottom


ScreenShot
-----------
**Dragging and Bounce animation**<br/>
![](app/src/main/assets/drag.png)

<br/><br/>
**Messenger**<br/>
![](app/src/main/assets/messenger1.png)
<br/>
![](app/src/main/assets/messenger2.png)

<br/><br/>
**LongPressing to Stop**<br/>
![](app/src/main/assets/pic_delete.png)

<br/><br/>
**landscape screenOrientation**<br/>
![](app/src/main/assets/landscape.png)

Orientation
-----------
**Support landscape screenOrientation**<br/>
when screenOrientation change to portrait or landscape, ChatHead will reposition automatically.
<br/>

