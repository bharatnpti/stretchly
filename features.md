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



#### Guided microbreaks
- **What it is**: Short, structured exercises during microbreaks to reduce eye strain and tension.
- **Included guides**:
  - Breathing (box/4-7-8)
  - Eye exercises (20-20-20, near/far focus)
  - Neck and shoulder stretches (desk-friendly)
- **Cues**: Optional subtle haptics and audio; simple on-screen animations (breathing circle).
- **Step progression**: Eye and stretch routines advance through timed steps with subtle fade transitions between cues.
- **Progress indicator**: Small dot indicator shows the current step in the routine.
- **Controls**:
  - Start/skip directly from the break screen or notification actions
  - Preferences → Guided microbreaks: enable/disable, choose routines, audio/haptic level
  - Remembers the last used routine and offers it by default next time

#### Content packs
- **What it is**: Curated sets of ideas and guided routines you can enable/disable.
- **Built-in packs**: General wellness, Eye care, Desk mobility.
- **Downloadable packs**: Optional themed packs (RSI relief, yoga-at-desk, mindfulness).
- **Customization**:
  - Enable multiple packs; rotate ideas to avoid repetition
  - Create your own custom ideas (title + description)
  - Import/export packs as JSON for backup or sharing

#### Material You / Dynamic color
- **Adaptive theming**: Colors match your system accent (Android 12+).
- **Modes**: Light, Dark, and AMOLED black.
- **Accessibility**: Larger text and high-contrast color option.
- **Controls**: Preferences → Appearance: theme, dynamic color toggle, contrast.

#### Streaks & badges
- **Motivation**: Gentle recognition for consistent, healthy break habits.
- **Streaks**: Consecutive days meeting your goals.
- **Badges**: Milestones for sustained consistency and variety (e.g., “Eye care week”).
- **Privacy**: All data stays on-device; can be reset or turned off.

#### Insights
- **Overview**: Daily/weekly/monthly snapshots of your break habits.
- **Metrics**:
  - Breaks taken vs skipped; adherence rate
  - Average time at screen between breaks
  - Snoozes/pauses and their reasons (DND, meetings, manual)
  - “Time protected” by breaks (estimated)
- **Views**: Simple charts and trend lines; export CSV (optional).
- **Privacy**: On-device processing by default.

#### Goal modes
- **Preset modes**:
  - Pomodoro: 25/5 with optional long break after N cycles
  - Balanced: regular microbreaks and periodic long breaks
  - Gentle: fewer prompts, longer intervals
- **Custom goals**:
  - Daily targets (e.g., “8 microbreaks and 2 long breaks”)
  - Long break frequency after N microbreaks
  - Work hours and weekdays/weekends profiles
- **Controls**: Preferences → Goal modes: select preset or build your own.

