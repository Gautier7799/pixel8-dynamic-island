private int lastBatteryLevel = -1;
    private boolean isChargingActive = false;

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean charging = (status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                status == BatteryManager.BATTERY_STATUS_FULL);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

            // منع التقطيع والتكرار
            if (charging != isChargingActive || (charging && level != lastBatteryLevel && lastBatteryLevel != -1)) {
                isChargingActive = charging;
                lastBatteryLevel = level;
                if (charging) {
                    showIsland("⚡ جاري الشحن السريع", level + "%", "🔋", Color.parseColor("#00E676"));
                } else {
                    showIsland("تم فصل الشاحن", "", "🔋", Color.LTGRAY);
                }
            }
        }
    };
