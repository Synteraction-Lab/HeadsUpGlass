
// partial code

    Intent mainIntent = new Intent();
    mainIntent.setAction("com.hci.nip.glass.EXTERNAL_APP_INTENT");
    mainIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
    mainIntent.putExtra("external.intent.url", "/displays");
    mainIntent.putExtra("external.intent.json", "{}");
    mainIntent.setType("text/json");
    mainIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
    mainIntent.setComponent(new ComponentName("com.hci.nip.glass", "com.hci.nip.android.service.broadcast.ExternalIntentReceiver"));

    sendBroadcast(mainIntent);
