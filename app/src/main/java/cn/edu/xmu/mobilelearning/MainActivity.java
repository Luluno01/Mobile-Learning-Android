package cn.edu.xmu.mobilelearning;

//android
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.*;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;
import android.os.Handler;

//crosswalk
import org.xwalk.core.*;

public class MainActivity extends AppCompatActivity {

    private final static int __SPLASH_DELAY__ = 0;
    private final static int __SPLASH_FADE_DURATION__ = 500;
    private XWalkView mXWalkView;
    private long exitTime = 0;
    private boolean loadedOnce = false;
    public static MainActivity instance = null;

    private class MyResourceClient extends XWalkResourceClient {
        MyResourceClient(XWalkView view) {
            super(view);
        }

        @Override
        public void onLoadFinished(XWalkView view, String url) {
            super.onLoadFinished(view, url);
            if(!loadedOnce) {
                final Animation anim = new AlphaAnimation(1, 0);
                anim.setDuration(__SPLASH_FADE_DURATION__);
                anim.setFillAfter(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getSplashView().startAnimation(anim);
                    }
                }, __SPLASH_DELAY__);
                loadedOnce = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeSplashView();
                    }
                }, __SPLASH_FADE_DURATION__);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true); // Enable remote debugging

        mXWalkView = (XWalkView) findViewById(R.id.xWalkView); // Get XWalkView

        // Disable zoom
        XWalkSettings xWalkViewSettings = mXWalkView.getSettings();
        xWalkViewSettings.setBuiltInZoomControls(false);
        xWalkViewSettings.setSupportZoom(false);

        mXWalkView.setResourceClient(new MyResourceClient(mXWalkView)); // Support for splash
        mXWalkView.addJavascriptInterface(new JsInterface(this), "NativeInterface"); // Add js interface

        mXWalkView.load("file:///android_asset/index.html", null); // Load resource
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
        instance = null;
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
                Toast.makeText(this, this.getString(R.string.exit_confirm), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        if(keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN) {
            mXWalkView.loadUrl("javascript:toggleSideNav();");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private View getSplashView() {
        return findViewById(R.id.splash);
    }

    private void removeSplashView() {
        View splash = getSplashView();
        if(null != splash) {
            ((ConstraintLayout) splash.getParent()).removeView(splash);
        }
    }
}
