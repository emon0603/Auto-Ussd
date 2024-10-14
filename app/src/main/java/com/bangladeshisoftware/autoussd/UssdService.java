package com.bangladeshisoftware.autoussd;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.util.List;

public class UssdService extends AccessibilityService {
    public static String TAG = "USSD";
    private boolean hasDialed = false; // Flag to track if the number has been dialed

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent");

        try {
            // Get the source
            AccessibilityNodeInfo nodeInfo = event.getSource();

            if (nodeInfo == null) return;

            String text = event.getText().toString();
            if (event.getClassName().equals("android.app.AlertDialog")) {
                Log.d(TAG, text);
                Toast.makeText(this, text, Toast.LENGTH_LONG).show();

                // Check if the number has already been dialed
                if (!hasDialed) {
                    AccessibilityNodeInfo nodeInput = nodeInfo.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
                    if (nodeInput != null) {
                        Bundle bundle = new Bundle();
                        bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "9");
                        nodeInput.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);
                        nodeInput.refresh();

                        // Find and click the 'Send' button
                        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("Send");
                        for (AccessibilityNodeInfo node : list) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }

                        // Set the flag to true after dialing
                        hasDialed = true;
                    }
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
