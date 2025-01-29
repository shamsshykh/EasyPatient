package com.app.easy_patient.util;

public class Constants {
	public static final int NOTIFICATION_ID_FOREGROUND_SERVICE = 8466503;
	public static Boolean UpdateCount = false;

	public static class ACTION {
		public static final String MAIN_ACTION = "test.action.main";
		public static final String START_ACTION = "test.action.start";
		public static final String STOP_ACTION = "test.action.stop";
	}

	public static class STATE_SERVICE {
		public static final int CONNECTED = 10;
		public static final int NOT_CONNECTED = 0;
	}

	public static class INTENT_KEYS {
		public static final String MEDICINE_REMINDER = "medicine_reminder";
		public static final String STOP_REMINDER_SOUND = "stop_reminder_sound";
	}

}
