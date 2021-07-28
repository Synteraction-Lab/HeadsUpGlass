package com.hci.nip.android.actuators.model;

import com.hci.nip.base.model.BaseData;

public class NotificationData implements BaseData {
    public static final int TYPE_TOAST = 0;
    public static final int TYPE_HEADS_UP = 1;
    public static final int TYPE_CUSTOM = 2;

    private final String title;         // FORMAT: <color?> <text>
    private final String message;       // FORMAT: <color?> <icon>
    private final int type;

    private long when = 0;              // when to display the notification in milliseconds
    private long duration = 0;          // notification display duration in milliseconds
    private int priority = 2;           // NotificationCompat.PRIORITY_MAX;
    private String appName = null;
    private String smallIcon = null;    // FORMAT: <color?> <icon>
    private String largeIcon = null;
    private boolean bigTextEnable = false;
    private boolean soundEnable = false;
    private boolean vibrationEnable = false;

    private boolean lightsEnable = false;
    private String config = null;

    /**
     * @param message The message to be displayed
     */
    public NotificationData(String title, String message, int type) {
        this.title = title;
        this.message = message;
        this.type = type;
    }

    /**
     * @param message The message to be displayed
     * @param millis  The duration of the message in milliseconds
     */
    public static NotificationData getToastNotification(String message, long millis) {
        NotificationData data = new NotificationData(null, message, TYPE_TOAST);
        data.setWhen(millis);
        return data;
    }

    /**
     * @param message The message to be displayed
     * @param title   The title of the message
     */
    public static NotificationData getHeadsUpNotification(String title, String message) {
        return new NotificationData(title, message, TYPE_HEADS_UP);
    }

    public static NotificationData getCustomNotification(String title, String message) {
        return new NotificationData(title, message, TYPE_CUSTOM);
    }

    public NotificationData(NotificationData data) {
        this(data.title, data.message, data.type);

        this.when = data.when;
        this.duration = data.duration;
        this.priority = data.priority;
        this.appName = data.appName;
        this.smallIcon = data.smallIcon;
        this.largeIcon = data.largeIcon;
        this.bigTextEnable = data.bigTextEnable;
        this.soundEnable = data.soundEnable;
        this.vibrationEnable = data.vibrationEnable;
        this.lightsEnable = data.lightsEnable;
        this.config = data.config;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }

    public long getWhen() {
        return when;
    }

    public void setWhen(long when) {
        this.when = when;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getSmallIcon() {
        return smallIcon;
    }

    public void setSmallIcon(String smallIcon) {
        this.smallIcon = smallIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getLargeIcon() {
        return largeIcon;
    }

    public void setLargeIcon(String largeIcon) {
        this.largeIcon = largeIcon;
    }

    public boolean isBigTextEnable() {
        return bigTextEnable;
    }

    public void setBigTextEnable(boolean bigTextEnable) {
        this.bigTextEnable = bigTextEnable;
    }

    public boolean isSoundEnable() {
        return soundEnable;
    }

    public void setSoundEnable(boolean soundEnable) {
        this.soundEnable = soundEnable;
    }

    public boolean isVibrationEnable() {
        return vibrationEnable;
    }

    public void setVibrationEnable(boolean vibrationEnable) {
        this.vibrationEnable = vibrationEnable;
    }

    public boolean isLightsEnable() {
        return lightsEnable;
    }

    public void setLightsEnable(boolean lightsEnable) {
        this.lightsEnable = lightsEnable;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    @Override
    public String toString() {
        return "NotificationData{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", when=" + when +
                ", duration=" + duration +
                ", priority=" + priority +
                ", smallIcon='" + smallIcon + '\'' +
                ", appName='" + appName + '\'' +
                ", largeIcon='" + largeIcon + '\'' +
                ", bigTextEnable=" + bigTextEnable +
                ", soundEnable=" + soundEnable +
                ", vibrationEnable=" + vibrationEnable +
                ", lightsEnable=" + lightsEnable +
                ", config='" + config + '\'' +
                '}';
    }
}
