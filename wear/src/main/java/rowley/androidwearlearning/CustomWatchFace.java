package rowley.androidwearlearning;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class CustomWatchFace {

    private static final String TIME_FORMAT_STRING = "kk:mm:ss a";
    private static final String DATE_FORMAT_STRING = "EEE, dd MMM yyyy";
    private final Paint timePaint;

    private final Paint datePaint;
    private final Paint batteryPaint;
    private final SimpleDateFormat timeFormat;
    private final SimpleDateFormat dateFormat;
    private final String BATTERY_TEXT_FORMAT;

    private String timeText;
    private String dateText;
    private String batteryText;

    public static CustomWatchFace newInstance(Context context) {
        Paint timePaint = new Paint();
        timePaint.setColor(Color.RED);
        timePaint.setTextSize(25);

        Paint datePaint = new Paint();
        datePaint.setColor(Color.GREEN);
        datePaint.setTextSize(35);

        Paint batteryPaint = new Paint();
        batteryPaint.setColor(Color.WHITE);
        batteryPaint.setTextSize(35);

        return new CustomWatchFace(timePaint, datePaint,
                batteryPaint, context.getString(R.string.battery_level_formatted));
    }

    private CustomWatchFace(Paint timePaint, Paint datePaint, Paint batteryPaint, String batteryTextFormat) {
        this.timePaint = timePaint;
        this.datePaint = datePaint;
        this.batteryPaint = batteryPaint;

        timeFormat = new SimpleDateFormat(TIME_FORMAT_STRING, Locale.getDefault());
        dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING, Locale.getDefault());

        BATTERY_TEXT_FORMAT = batteryTextFormat;

        batteryText = String.format(batteryTextFormat, 0);
    }

    public void setBatteryText(String batteryText) {
        this.batteryText = batteryText;
    }

    public void draw(Canvas canvas, Rect bounds) {
        canvas.drawColor(Color.BLACK);
        timeText = timeFormat.format(Calendar.getInstance().getTime());
        dateText = dateFormat.format(Calendar.getInstance().getTime());
        float timeXOffset = calculateXOffset(timeText, timePaint, bounds);
        float timeYOffset = calculateTimeYOffset(timeText, timePaint, bounds);
        canvas.drawText(timeText, timeXOffset, timeYOffset, timePaint);
        float dateXOffset = calculateXOffset(dateText, datePaint, bounds);
        float dateYOffset = calculateDateYOffset(dateText, datePaint);
        canvas.drawText(dateText, dateXOffset, timeYOffset + dateYOffset, datePaint);
        float batteryXOffset = calculateXOffset(batteryText, batteryPaint, bounds);
        float batteryYOffset = calculateBatteryYOffset(batteryText, batteryPaint);
        canvas.drawText(batteryText, batteryXOffset, dateYOffset + batteryYOffset, batteryPaint);
    }

    private float calculateXOffset(String text, Paint paint, Rect bounds) {
        float centerX = bounds.exactCenterX();
        float textLength = paint.measureText(text);
        return centerX - (textLength / 2.0F);
    }

    private float calculateTimeYOffset(String timeText, Paint timePaint, Rect bounds) {
        float centerY = bounds.exactCenterY();
        Rect textBounds = new Rect();
        timePaint.getTextBounds(timeText, 0, timeText.length(), textBounds);
        int textHeight = textBounds.height();
        return centerY + (textHeight / 2);
    }

    private float calculateDateYOffset(String dateText, Paint datePaint) {
        Rect textBounds = new Rect();
        datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
        return textBounds.height() + 10.0F;
    }

    private float calculateBatteryYOffset(String batteryText, Paint batteryPaint) {
        Rect textBounds = new Rect();
        batteryPaint.getTextBounds(batteryText, 0, batteryText.length(), textBounds);
        return textBounds.height() + 40.0F;
    }

    public void setAntiAlias(boolean antiAlias) {
        batteryPaint.setAntiAlias(antiAlias);
        timePaint.setAntiAlias(antiAlias);
        datePaint.setAntiAlias(antiAlias);
    }

    public void setColor(int red, int green, int white) {
        batteryPaint.setColor(red);
        timePaint.setColor(green);
        datePaint.setColor(white);
    }

    public void updateTimeZoneWith(String timeZone) {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
        timeText = timeFormat.format(Calendar.getInstance().getTime());
    }
}
