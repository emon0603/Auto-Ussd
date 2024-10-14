package com.bangladeshisoftware.autoussd;



import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UssdService extends AccessibilityService {
    public static String TAG = "USSD";
    private List<String> numberList; // নম্বরের তালিকা
    private int currentIndex = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent");

        try{
            //Get the source
            AccessibilityNodeInfo nodeInfo = event.getSource();

            String text = event.getText().toString();
            if (event.getClassName().equals("android.app.AlertDialog")) {
                Log.d(TAG, text);
                Toast.makeText(this, text, Toast.LENGTH_LONG).show();

                AccessibilityNodeInfo nodeInput = nodeInfo.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);

                String numberToDial = numberList.get(currentIndex);

                Bundle bundle = new Bundle();
                bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, numberToDial);
                nodeInput.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);
                nodeInput.refresh();

                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("Send");
                for (AccessibilityNodeInfo node : list) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        } catch (Exception e){
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
            numberList.add("1");
            numberList.add("1");


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