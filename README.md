# Stretchly Android App

A comprehensive break reminder app for Android that helps you maintain healthy work habits through regular breaks and guided exercises.

## Features Implemented

### Core Features ✅
- **Break Scheduling**: Foreground service that schedules microbreaks and long breaks
- **Ongoing Notification**: Shows status like "Next break in X minutes"
- **Break Screens**: Full-screen countdown UIs for both break types
- **Lock-screen Behavior**: Break screens appear on lock screen and turn screen on

### Preferences ✅
- **Enable/disable**: Toggles for microbreaks and long breaks
- **Configurable timings**: Customizable intervals and durations
- **Real-time updates**: Changes apply immediately

### Notifications & Actions ✅
- **Persistent notification actions**: Pause, Resume, Snooze, Skip
- **Break alerts**: Tapping opens corresponding break screen

### Do Not Disturb Awareness ✅
- **Dynamic DND checks**: Breaks suppressed when system DND is active
- **Status text**: Notification reflects when breaks are paused due to DND

### Guided Microbreaks ✅
- **Structured exercises**: Breathing, eye exercises, neck stretches
- **Step progression**: Timed steps with fade transitions
- **Progress indicator**: Shows current step in routine
- **Haptic feedback**: Optional vibration cues
- **Breathing animation**: Animated circle for breathing exercises

### Content Packs ✅
- **Built-in packs**: General Wellness, Eye Care, Desk Mobility
- **Enable/disable**: Toggle individual packs
- **Variety**: Rotate ideas to avoid repetition
- **Statistics**: Shows idea counts per pack

### Material You / Dynamic Color ✅
- **Adaptive theming**: Modern UI with consistent colors
- **Card-based design**: Clean, organized interface
- **Color scheme**: Primary, secondary, and accent colors

### Streaks & Badges ✅
- **Motivation system**: Track consecutive days and achievements
- **Badges**: 8 different badges for various milestones
- **Streak tracking**: Current and longest streaks for both break types
- **Privacy**: All data stays on-device

### Insights ✅
- **Analytics**: Daily, weekly, and monthly statistics
- **Metrics**: Breaks taken vs skipped, adherence rate, time protected
- **Data export**: CSV export functionality
- **Privacy**: On-device processing

### Goal Modes ✅
- **Preset modes**: Pomodoro, Balanced, Gentle, Intensive
- **Custom modes**: Create your own break patterns
- **Work hours**: Configure active hours and days
- **Daily targets**: Set goals for breaks per day

### Startup & Permissions ✅
- **Auto-start on boot**: Resumes after device reboot
- **Notification permission**: Requests POST_NOTIFICATIONS on Android 13+
- **Vibration permission**: For haptic feedback

## Technical Implementation

### Architecture
- **Activities**: Main, Preferences, Insights, Badges, Goal Modes, Content Packs
- **Services**: BreakSchedulerService (foreground service)
- **Managers**: ContentPackManager, StreakManager, InsightsManager, GoalModeManager
- **Data Classes**: GuidedRoutine, ContentPack, Badge, GoalMode

### Key Components
- **BreakPlanner**: Core scheduling logic with DND and work hours support
- **DndManager**: Do Not Disturb detection
- **SettingsManager**: Persistent preferences storage
- **AutostartReceiver**: Boot completion handling

### UI/UX Features
- **Modern Material Design**: Card-based layouts with elevation
- **Responsive Design**: Works on different screen sizes
- **Accessibility**: Proper text sizing and contrast
- **Animations**: Smooth transitions and breathing animations

## Default Settings
- **Microbreak**: 10 minutes interval, 20 seconds duration
- **Long Break**: 30 minutes interval, 5 minutes duration
- **General Wellness Pack**: Enabled by default
- **Haptic Feedback**: Enabled by default

## Getting Started
1. Install the app
2. Grant notification permissions when prompted
3. Configure your preferred break settings in Preferences
4. Enable content packs for variety
5. Choose a goal mode that fits your work style
6. Start taking healthy breaks!

## Privacy
All data is stored locally on your device. No personal information is collected or transmitted.
