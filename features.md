### Stretchly (Android) — Features

#### Core
- **Break scheduling**: Foreground service that schedules two break types
  - **Microbreaks**: short rests with ideas/prompts
  - **Long breaks**: longer rests with title + description ideas
- **Ongoing notification**: Shows status like “Next break in X minutes”
- **Break screens**: Full-screen countdown UIs for both break types

#### Preferences
- **Enable/disable**: Toggles for microbreaks and long breaks
- **Configurable timings**:
  - Microbreak interval (minutes) and duration (seconds)
  - Long break interval (minutes) and duration (seconds)
  - Changes apply immediately (service reschedules)

#### Notifications & Actions
- **Persistent notification actions**:
  - Pause breaks
  - Resume breaks
  - Snooze 10 minutes (suppress all breaks)
  - Skip next microbreak
  - Skip next long break
- **Break alerts**: Tapping opens the corresponding break screen

#### Do Not Disturb awareness
- **Dynamic DND checks**: Breaks are suppressed when system DND is active
- **Status text**: Notification reflects when breaks are paused due to DND

#### Lock-screen behavior
- **Show over lock screen**: Break screens appear on the lock screen and turn the screen on

#### Content
- **Ideas library**: Bundled ideas for microbreaks and long breaks (titles + texts)

#### Startup & permissions
- **Auto-start on boot**: Schedules resume after device reboot
- **Notification permission**: Requests POST_NOTIFICATIONS on Android 13+

#### Defaults (can be changed in Preferences)
- **Microbreak**: interval 10 minutes, duration 20 seconds
- **Long break**: interval 30 minutes, duration 5 minutes


