Sudokumaster
============

This Java ME application demonstrates the use case of downscaling and upscaling the UI to 
different types of devices and using touch and keyboard events in the same 
application. UX design and graphics have been ported from the Sudokumaster Flash
Lite application. Additionally, the example application introduces a reusable custom 
view structure.

This example demonstrates:
* Controlling layouts dynamically for multiple screen resolutions
* Supporting several input methods such as key, touch, and 
  key & touch

The application is hosted in GitHub:
* https://github.com/nokia-developer/sudokumaster-jme

For more information on the implementation, visit Java Developer's Library:
* http://developer.nokia.com/Resources/Library/Java/#!code-examples/game-api-sudokumaster.html

1. Prerequisites
-------------------------------------------------------------------------------
Java ME basics

2. Important files and classes
-------------------------------------------------------------------------------
Files:
* src\..\SudokuCanvas.java
* src\..\View.java
* src\..\Layout.java
* src\..\SudokuView.java

Classes: 
* GameCanvas
* Vector 
* Graphics
* Image 
* TiledLayer

3. Design considerations
-------------------------------------------------------------------------------
The game separates UI rendering to a second thread to make sure events are handled
smoothly. Rendering pauses when the screensaver starts, there is a incoming call,
or so on, to prevent excess power consumption.

The state of the game is stored when the user closes the application or the
application is paused for any reason. The state is restored when the user
returns to play.

4. Known issues
-------------------------------------------------------------------------------
No known issues.

5. Build and installation instructions
-------------------------------------------------------------------------------
The example has been created with NetBeans 7.3 and Nokia Asha SDK 1.0.
The project can be easily opened in NetBeans by selecting 'Open Project' 
from the File menu and selecting the application. 

Before opening the project, make sure the Series 40 6th Edition, FP1 SDK or newer is 
installed and added to NetBeans. Building is done by selecting 'Build main 
project'.

Installing the application on a phone can be done by transfering the JAR file 
via Nokia Suite or via Bluetooth.

The example can also be opened and run with Eclipse.

6. Running the example
-------------------------------------------------------------------------------
When the application is started for the first time, a random sudoku puzzle is
displayed. Next time the application will return to the same state it was left
in when closed.

From the Options menu, the user can restart the current game, start a new game,
and exit the game. The new game selection start a new random sudoku puzzle.

The sudoku can be filled by clicking a cell and selecting a digit or by navigating 
with the keyboard to a cell and pressing a number key. Pressing the 0  key clears any 
previous value from the cell.

7. Compatibility
-------------------------------------------------------------------------------
This MIDlet is compatible with Nokia Asha software platform and Series 40 2nd
Edition and S60 2nd Edition FP2 devices and newer.

Tested on:
* Nokia 2710 Navigation Edition (Series 40 6th Edition)
* Nokia 3110 Classic (Series 40 3rd Edition FP2)
* Nokia 6630 (S60 2nd Edition FP2)
* Nokia 7390 (Series 40 3rd Edition FP2
* Nokia Asha 200 (Java Runtime 1.0.0 for Series 40)
* Nokia Asha 305 (Java Runtime 2.0.0 for Series 40)
* Nokia Asha 311 (Java Runtime 2.0.0 for Series 40)
* Nokia C7-00 (Symbian Anna with Java Runtime 2.2 for Symbian)
* Nokia E71 (S60 3rd Edition FP1)
* Nokia X3-02 (Series 40 6th Edition FP1)
* Nokia X6-00 (S60 5th Edition )
* Nokia 308 (Java Runtime 3.0.0 for Series 40)
* Nokia Asha 501 (Java Runtime 3.0.0 for Series 40)

Developed with:
* Netbeans 7.3
* Nokia Asha SDK 1.0

8. Version history
-------------------------------------------------------------------------------
* 1.2.0 Ported to Nokia Asha devices.
* 1.1.0 Code cleanup and new icons. Tested with Series 40 full touch devices.
* 1.0.0 First release