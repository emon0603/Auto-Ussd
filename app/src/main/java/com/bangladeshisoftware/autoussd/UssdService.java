package com.bangladeshisoftware.autoussd;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.util.List;
import java.util.ArrayList;

public class UssdService extends AccessibilityService {
    public static String TAG = "USSD";
    private List<String> numberList; // নম্বরের তালিকা
    private int currentIndex = 0;    // বর্তমান নম্বরের ইনডেক্স

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent");

        try {
            // সোর্স নোড খুঁজে বের করা
            AccessibilityNodeInfo nodeInfo = event.getSource();

            if (nodeInfo == null) return;

            String text = event.getText().toString();
            if (event.getClassName().equals("android.app.AlertDialog")) {
                Log.d(TAG, text);
                Toast.makeText(this, text, Toast.LENGTH_LONG).show();

                // যদি ইনপুটের ফোকাস পাওয়া যায়
                AccessibilityNodeInfo nodeInput = nodeInfo.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
                if (nodeInput != null && currentIndex < numberList.size()) {
                    // বর্তমান ইনডেক্স অনুযায়ী নাম্বার কল হবে
                    String numberToDial = numberList.get(currentIndex);

                    Bundle bundle = new Bundle();
                    bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, numberToDial);
                    nodeInput.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);
                    nodeInput.refresh();

                    // 'Send' বাটন খুঁজে বের করে সেটায় ক্লিক করা
                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("Send");
                    for (AccessibilityNodeInfo node : list) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }

                    // পরবর্তী নাম্বারের জন্য ইনডেক্স বাড়ানো
                    currentIndex++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected");

        try {
            // নম্বরের তালিকা নির্ধারণ করা
            numberList = new ArrayList<>();
            numberList.add("9");
            numberList.add("1");
            //numberList.add("5"); // আপনার চাহিদা অনুযায়ী আরও নম্বর যোগ করতে পারেন

            AccessibilityServiceInfo info = new AccessibilityServiceInfo();
            info.flags = AccessibilityServiceInfo.DEFAULT;
            info.packageNames = new String[]{"com.android.phone"};
            info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
            setServiceInfo(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
