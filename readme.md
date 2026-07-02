# TimeTube

TimeTube is a lightweight, open-source Android app that helps limit time spent on YouTube.

When YouTube is opened, TimeTube starts a countdown. If YouTube remains open until the timer expires, the app automatically exits YouTube by returning to the Home screen (or another configurable action in future versions).

## Features

* Automatically detects when YouTube is opened
* Starts a configurable timer (down to the second)
* Cancels the timer when YouTube is closed
* Warning notification before the timer expires
* Accessibility service status indicator
* Material 3 interface
* No root required
* Open source

## How it works

1. Enable the TimeTube Accessibility Service.
2. Set a timer duration.
3. (Optional) Set how many seconds before the timer ends you want to receive a warning.
4. Open YouTube.
5. When the timer expires, TimeTube automatically exits YouTube.

## Planned Features

* Multiple supported apps (Instagram, Reddit, Chrome, etc.)
* Different timers for different apps
* Countdown display
* Daily usage statistics
* Home / Back action selection
* Import and export settings
* F-Droid release

## Requirements

* Android 7.0 (API 24) or later
* Accessibility Service enabled

## Privacy

TimeTube does **not** collect, store, or transmit personal data.

The Accessibility Service is used only to:

* Detect when YouTube is in the foreground.
* Perform the configured action when the timer expires.

No browsing history, typed text, passwords, or personal information are collected.

## License

This project will be released under the MIT License.
