package cn.edu.xmu.mobilelearning;

/**
 * Created by Louie Yan on 2017/4/29.
 */

import org.xwalk.core.JavascriptInterface;

import android.widget.Toast;

public class JsInterface {
    private MainActivity activity;

    public JsInterface(MainActivity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public String sayHello() {
        return "Hello world.";
    }

    @JavascriptInterface
    public void toast(String content) {
        Toast.makeText(this.activity, content, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void toastLong(String content) {
        Toast.makeText(this.activity, content, Toast.LENGTH_LONG).show();
    }
}
