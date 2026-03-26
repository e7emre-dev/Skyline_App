package com.manifix.incx;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Context context;
    private Thread.UncaughtExceptionHandler defaultHandler;

    public CrashHandler(Context context) {
        this.context = context;
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            String stackTrace = sw.toString();

            String crashInfo = "=== CRASH REPORT ===\n";
            crashInfo += "Time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n";
            crashInfo += "Device: " + Build.MANUFACTURER + " " + Build.MODEL + "\n";
            crashInfo += "Android Version: " + Build.VERSION.RELEASE + "\n";
            crashInfo += "App Version: 0.3.3 Beta\n";
            crashInfo += "Thread: " + thread.getName() + "\n\n";
            crashInfo += "Exception: " + throwable.getClass().getSimpleName() + "\n";
            crashInfo += "Message: " + throwable.getMessage() + "\n\n";
            crashInfo += "Stack Trace:\n" + stackTrace;

            Intent intent = new Intent(context, CrashActivity.class);
            intent.putExtra("crash_info", crashInfo);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);

            System.exit(1);
        } catch (Exception e) {
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable);
            }
        }
    }
}
