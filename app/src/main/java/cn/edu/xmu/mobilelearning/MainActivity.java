package cn.edu.xmu.mobilelearning;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.*;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;
import android.os.Handler;

import org.xwalk.core.*;

public class MainActivity extends AppCompatActivity {

    private final int __SPLASH_DELAY__ = 0;
    private XWalkView mXWalkView;
    private long exitTime = 0;
    private boolean loadedOnce = false;
    protected static MainActivity instance = null;

    class MyResourceClient extends XWalkResourceClient {
        MyResourceClient(XWalkView view) {
            super(view);
        }

        @Override
        public void onLoadFinished(XWalkView view, String url) {
            super.onLoadFinished(view, url);
            if(!loadedOnce) {
                final Animation anim = new AlphaAnimation(1, 0);
                anim.setDuration(500);
                anim.setFillAfter(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getSplashView().startAnimation(anim);
                    }
                }, __SPLASH_DELAY__);
                loadedOnce = true;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        mXWalkView = (XWalkView) findViewById(R.id.xWalkView);
        mXWalkView.setResourceClient(new MyResourceClient(mXWalkView));
        mXWalkView.addJavascriptInterface(new JsInterface(this), "NativeInterface");
        mXWalkView.load("file:///android_asset/index.html", null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mXWalkView != null) {
            mXWalkView.pauseTimers();
            mXWalkView.onHide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mXWalkView != null) {
            mXWalkView.resumeTimers();
            mXWalkView.onShow();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mXWalkView != null) {
            mXWalkView.onDestroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mXWalkView != null) {
            mXWalkView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mXWalkView != null) {
            mXWalkView.onNewIntent(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private View getSplashView() {
        return findViewById(R.id.splash);
    }
}
