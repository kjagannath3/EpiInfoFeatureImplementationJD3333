# Epi Info Android Companion
Updated Android version for the Epi Info application developed by the Centers for Disease Control and Prevention. Epi Info is an application used by epidimiologists worldwide to help record, track and analyze information about outbreaks. Epidimiolgists create forms, ask patients in the field to fill out their symptoms, and then use the data they collect to find trends in symptoms/disease spread. The mobile application is primarily used to analyze data, fill out forms and do different statistical calculations. 

## Release Notes
### Version 0.3.0

#### New Features
* The application now has access to audio, video and photo from the device it is being run on so that users can upload specific interview material to forms. Users can enable this access.
* Users have the ability to export files to the cloud at any given time. There were issues with this functionality specifcially in regards to Box.

#### Bug Fixes
* Fixed: Updated the Gradle and SDK to match current standards for both in hopes that it can be packaged into an APK for the Google Play Store.
* Fixed: Fixed depreciation issues in DialogFragment, Dialog, SharedPreferences, and Date within mainActivity as well as within methods in EpiDbHelper.

#### Known Issues
* Expansive ste/plethora of depreciated files: As we have started working on fixing depreciation and implementing new features for our clients, we have realized that due to interdependency of certain depreciated files on each other, one depreciation fix or update in software usually has an effect on multiple other files in the codebase. We are planning on shifting away from trying to fix every small depreciation issue and rather doing what is necessary to make it compatible enough to be packaged into an APK.

### Version 0.2.0

#### New Features
* The frequency gadget of the Analyze Data tab now displays ContactID, Frequency in terms of count, and Frequency in terms of percentage. This came directly as a high priority wishlist item of the client.
* The User Interface of StatCalc has been improved to where users can select which calculation they want to do by a dropdown menu. This declutters the original menu option page.

#### Bug Fixes
* Fixed: Identified and mapped all the compatibility issues with outdated versions.
* Fixed: Fixed depreciation issues for Interviewer, InterviewLayoutManager, FormLayoutManager, LineListFragment, createRecord() in EpiDbHelper.

#### Known Issues
* Large, unknown existing codebase: We have been able to identify the majority of the depreciated/incompatible code, but the problem is know being able to understand the functionalities of the plethora of files in order to properly update the software and implement new and improved features. We have been working with our clients to gather as much information on how different components interact in order to combat this.

### Version 0.1.0

#### New Features
* Source code and functionality from the outdated CDC repository was implemented to replace the previous "blank slate" development approach.  
* After these changes, the scope of future project goals will be adjusted accordingly to suit development objectives.

#### Bug Fixes
* Fixed: Updated file permissions to access videos, images, and audio on local device. 
* Fixed: Incorrect Grade plugin version was resolved in classpath. 
* Fixed: Enabled ability to open Collect Data and View Records screens. 

#### Known Issues
* Compatibility: Features from the old source code have not been rigorously tested, with a likely presence of compatibility errors. 
** Given that this update has provided features encompassed by later sprints, a rigorous testing process has not yet been conducted for related functionalities.
---
