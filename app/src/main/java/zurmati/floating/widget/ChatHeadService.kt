package zurmati.floating.widget

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.exp

class ChatHeadService : Service() {
    private var windowManager: WindowManager? = null
    private var chatHeadView: RelativeLayout? = null
    private var removeView: RelativeLayout? = null
    private var txtView: LinearLayout? = null
    private var txtLinearLayout: LinearLayout? = null
    private var chatHeadImg: ImageView? = null
    private var removeImg: ImageView? = null
    private var txt1: TextView? = null
    private var xInitCord = 0
    private var yInitCord = 0
    private var xInitMargin = 0
    private var yInitMargin = 0
    private val szWindow = Point()
    private var isLeft = true
    private var sMsg: String? = ""


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val builder =
                Notification.Builder(
                    this,
                    createNotificationChannel("ChatHead Service", "ChetHead")
                )
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("SmartDetection Running")
                    .setAutoCancel(true)
            val notification = builder.build()
            startForeground(1, notification)

        } else {
            startForeground(1, NotificationCompat.Builder(this, "ChatHead").build())
        }


    }


    @SuppressLint("ClickableViewAccessibility")
    private fun handleStart() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        removeView = inflater.inflate(R.layout.remove, null) as RelativeLayout

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            WindowManager.LayoutParams.TYPE_PHONE;
        }

        val paramRemove = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            flag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        paramRemove.gravity = Gravity.TOP or Gravity.LEFT
        removeView!!.visibility = View.GONE
        removeImg = removeView!!.findViewById<View>(R.id.remove_img) as ImageView
        windowManager!!.addView(removeView, paramRemove)
        chatHeadView = inflater.inflate(R.layout.chathead, null) as RelativeLayout
        chatHeadImg = chatHeadView!!.findViewById<View>(R.id.chathead_img) as ImageView
        windowManager!!.defaultDisplay.getSize(szWindow)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            flag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = 0
        params.y = 100
        windowManager!!.addView(chatHeadView, params)

        chatHeadView!!.setOnTouchListener(object : OnTouchListener {
            var timeStart: Long = 0
            var timeEnd: Long = 0
            var isLongClick = false
            var inBounded = false
            var removeImgWidth = 0
            var removeImgHeight = 0
            var handlerLongClick = Handler()
            var runnableLongClick = Runnable {
                Log.d(Utils.LogTag, "Into runnable_longClick")
                isLongClick = true
                removeView!!.visibility = View.VISIBLE
                chatHeadLongClick()
            }

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val layoutParams = chatHeadView!!.layoutParams as WindowManager.LayoutParams
                val xCord = event.rawX.toInt()
                val yCord = event.rawY.toInt()
                val xCordDestination: Int
                var yCordDestination: Int
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        timeStart = System.currentTimeMillis()
                        handlerLongClick.postDelayed(runnableLongClick, 600)
                        removeImgWidth = removeImg!!.layoutParams.width
                        removeImgHeight = removeImg!!.layoutParams.height
                        xInitCord = xCord
                        yInitCord = yCord
                        xInitMargin = layoutParams.x
                        yInitMargin = layoutParams.y
                        if (txtView != null) {
                            txtView!!.visibility = View.GONE
                            myHandler.removeCallbacks(myRunnable)
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val xDiffMove = xCord - xInitCord
                        val yDiffMove = yCord - yInitCord
                        xCordDestination = xInitMargin + xDiffMove
                        yCordDestination = yInitMargin + yDiffMove
                        if (isLongClick) {
                            val xBoundLeft = szWindow.x / 2 - (removeImgWidth * 1.5).toInt()
                            val xBoundRight = szWindow.x / 2 + (removeImgWidth * 1.5).toInt()
                            val yBoundTop = szWindow.y - (removeImgHeight * 1.5).toInt()
                            if (xCord >= xBoundLeft && xCord <= xBoundRight && yCord >= yBoundTop) {
                                inBounded = true
                                val xCordRemove =
                                    ((szWindow.x - removeImgHeight * 1.5) / 2).toInt()
                                val yCordRemove =
                                    (szWindow.y - (removeImgWidth * 1.5 + statusBarHeight)).toInt()
                                if (removeImg!!.layoutParams.height == removeImgHeight) {
                                    removeImg!!.layoutParams.height =
                                        (removeImgHeight * 1.5).toInt()
                                    removeImg!!.layoutParams.width =
                                        (removeImgWidth * 1.5).toInt()
                                    val paramRemove =
                                        removeView!!.layoutParams as WindowManager.LayoutParams
                                    paramRemove.x = xCordRemove
                                    paramRemove.y = yCordRemove
                                    windowManager!!.updateViewLayout(removeView, paramRemove)
                                }
                                layoutParams.x =
                                    xCordRemove + abs(removeView!!.width - chatHeadView!!.width) / 2
                                layoutParams.y =
                                    yCordRemove + abs(removeView!!.height - chatHeadView!!.height) / 2
                                windowManager!!.updateViewLayout(chatHeadView, layoutParams)
                            } else {
                                inBounded = false
                                removeImg!!.layoutParams.height = removeImgHeight
                                removeImg!!.layoutParams.width = removeImgWidth
                                val paramRemove =
                                    removeView!!.layoutParams as WindowManager.LayoutParams
                                val xCordRemove = (szWindow.x - removeView!!.width) / 2
                                val yCordRemove =
                                    szWindow.y - (removeView!!.height + statusBarHeight)
                                paramRemove.x = xCordRemove
                                paramRemove.y = yCordRemove
                                windowManager!!.updateViewLayout(removeView, paramRemove)
                            }
                        }
                        layoutParams.x = xCordDestination
                        layoutParams.y = yCordDestination
                        windowManager!!.updateViewLayout(chatHeadView, layoutParams)
                    }
                    MotionEvent.ACTION_UP -> {
                        isLongClick = false
                        removeView!!.visibility = View.GONE
                        removeImg!!.layoutParams.height = removeImgHeight
                        removeImg!!.layoutParams.width = removeImgWidth
                        handlerLongClick.removeCallbacks(runnableLongClick)
                        if (inBounded) {
                            if (MyDialog.active) {
                                MyDialog.myDialog!!.finish()
                            }
                            stopService(Intent(this@ChatHeadService, ChatHeadService::class.java))
                            inBounded = false
                        }
                        val xDiff = xCord - xInitCord
                        val yDiff = yCord - yInitCord
                        if (abs(xDiff) < 5 && abs(yDiff) < 5) {
                            timeEnd = System.currentTimeMillis()
                            if (timeEnd - timeStart < 300) {
                                chatHeadClick()
                            }
                        }
                        yCordDestination = yInitMargin + yDiff
                        val barHeight = statusBarHeight
                        if (yCordDestination < 0) {
                            yCordDestination = 0
                        } else if (yCordDestination + (chatHeadView!!.height + barHeight) > szWindow.y) {
                            yCordDestination = szWindow.y - (chatHeadView!!.height + barHeight)
                        }
                        layoutParams.y = yCordDestination
                        inBounded = false
                        resetPosition(xCord)
                    }
                    else -> Log.d(
                        Utils.LogTag,
                        "chatheadView.setOnTouchListener  -> event.getAction() : default"
                    )
                }
                return true
            }
        })
        txtView = inflater.inflate(R.layout.txt, null) as LinearLayout
        txt1 = txtView!!.findViewById<View>(R.id.txt1) as TextView
        txtLinearLayout = txtView!!.findViewById<View>(R.id.txt_linearlayout) as LinearLayout
        val paramsTxt = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            flag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        paramsTxt.gravity = Gravity.TOP or Gravity.LEFT
        txtView!!.visibility = View.GONE
        windowManager!!.addView(txtView, paramsTxt)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (windowManager == null) return
        windowManager!!.defaultDisplay.getSize(szWindow)
        val layoutParams = chatHeadView!!.layoutParams as WindowManager.LayoutParams
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(Utils.LogTag, "ChatHeadService.onConfigurationChanged -> landscap")
            if (txtView != null) {
                txtView!!.visibility = View.GONE
            }
            if (layoutParams.y + (chatHeadView!!.height + statusBarHeight) > szWindow.y) {
                layoutParams.y = szWindow.y - (chatHeadView!!.height + statusBarHeight)
                windowManager!!.updateViewLayout(chatHeadView, layoutParams)
            }
            if (layoutParams.x != 0 && layoutParams.x < szWindow.x) {
                resetPosition(szWindow.x)
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(Utils.LogTag, "ChatHeadService.onConfigurationChanged -> portrait")
            if (txtView != null) {
                txtView!!.visibility = View.GONE
            }
            if (layoutParams.x > szWindow.x) {
                resetPosition(szWindow.x)
            }
        }
    }

    private fun resetPosition(x_cord_now: Int) {
        if ((x_cord_now - 50) <= szWindow.x / 2) {
            isLeft = true
            moveToLeft(x_cord_now)
        } else {
            isLeft = false
            moveToRight(x_cord_now)
        }
    }

    private fun moveToLeft(x_cord_now: Int) {
        val x = szWindow.x - x_cord_now
        object : CountDownTimer(500, 5) {
            var mParams = chatHeadView!!.layoutParams as WindowManager.LayoutParams
            override fun onTick(t: Long) {
                val step = (500 - t) / 5
                Log.i("Step", "onTick:t $t")

                Log.i("Step", "onTick:step $step")
                Log.i("Step", "onTick:scale ${x.toLong()}")

                mParams.x = 0 - bounceValue(step, x.toLong()).toInt()
                windowManager!!.updateViewLayout(chatHeadView, mParams)
            }

            override fun onFinish() {
                mParams.x = 0
                windowManager!!.updateViewLayout(chatHeadView, mParams)
            }
        }.start()
    }

    private fun moveToRight(x_cord_now: Int) {
        object : CountDownTimer(500, 5) {
            var mParams = chatHeadView!!.layoutParams as WindowManager.LayoutParams
            override fun onTick(t: Long) {
                val step = (500 - t) / 5
                mParams.x = szWindow.x + bounceValue(
                    step,
                    x_cord_now.toLong()
                ).toInt() - chatHeadView!!.width
                if (chatHeadView!!.windowToken != null)
                    windowManager!!.updateViewLayout(chatHeadView, mParams)
            }

            override fun onFinish() {
                mParams.x = szWindow.x - chatHeadView!!.width
                if (chatHeadView!!.windowToken != null)
                    windowManager!!.updateViewLayout(chatHeadView, mParams)
            }
        }.start()
    }

    private fun bounceValue(step: Long, scale: Long): Double {
        return scale * exp(-0.35 * step) * cos(0.1 * step)
    }

    private val statusBarHeight: Int
        get() = ceil(25 * applicationContext.resources.displayMetrics.density)
            .toInt()

    private fun chatHeadClick() {
        if (MyDialog.active) {
            MyDialog.myDialog!!.finish()
        } else {
            val it = Intent(
                this,
                MyDialog::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(it)
        }
    }

    private fun chatHeadLongClick() {
        Log.d(Utils.LogTag, "Into ChatHeadService.chathead_longclick() ")
        val param_remove = removeView!!.layoutParams as WindowManager.LayoutParams
        val x_cord_remove = (szWindow.x - removeView!!.width) / 2
        val y_cord_remove = szWindow.y - (removeView!!.height + statusBarHeight)
        param_remove.x = x_cord_remove
        param_remove.y = y_cord_remove

        if (removeView!!.windowToken != null)
            windowManager!!.updateViewLayout(removeView, param_remove)
    }

    private fun showMsg(sMsg: String) {
        if (txtView != null && chatHeadView != null) {
            Log.d(Utils.LogTag, "ChatHeadService.showMsg -> sMsg=$sMsg")
            txt1!!.text = sMsg
            myHandler.removeCallbacks(myRunnable)
            val param_chathead = chatHeadView!!.layoutParams as WindowManager.LayoutParams
            val param_txt = txtView!!.layoutParams as WindowManager.LayoutParams
            txtLinearLayout!!.layoutParams.height = chatHeadView!!.height
            txtLinearLayout!!.layoutParams.width = szWindow.x / 2
            if (isLeft) {
                param_txt.x = param_chathead.x + chatHeadImg!!.width
                param_txt.y = param_chathead.y
                txtLinearLayout!!.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
            } else {
                param_txt.x = param_chathead.x - szWindow.x / 2
                param_txt.y = param_chathead.y
                txtLinearLayout!!.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            }
            txtView!!.visibility = View.VISIBLE
            windowManager!!.updateViewLayout(txtView, param_txt)
            myHandler.postDelayed(myRunnable, 4000)
        }
    }

    var myHandler = Handler()
    var myRunnable = Runnable {
        if (txtView != null) {
            txtView!!.visibility = View.GONE
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(Utils.LogTag, "ChatHeadService.onStartCommand() -> startId=$startId")
        if (intent != null) {
            val bd = intent.extras
            if (bd != null) sMsg = bd.getString(Utils.EXTRA_MSG)
            if (sMsg != null && sMsg!!.isNotEmpty()) {
                if (startId == START_STICKY) {
                    Handler().postDelayed({
                        showMsg(sMsg!!)
                    }, 300)
                } else {
                    showMsg(sMsg!!)
                }
            }
        }
        return if (startId == START_STICKY) {
            handleStart()
            super.onStartCommand(intent, flags, startId)
        } else {
            START_NOT_STICKY
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (chatHeadView != null) {
            windowManager!!.removeView(chatHeadView)
        }
        if (txtView != null) {
            windowManager!!.removeView(txtView)
        }
        if (removeView != null) {
            windowManager!!.removeView(removeView)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(Utils.LogTag, "ChatHeadService.onBind()")
        return null
    }
}