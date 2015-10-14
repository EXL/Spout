SPOUT for Pandora
==============
Pandora Port
------------
  It required only minor keymap changes and tweaks to the screen scaling code to make this game work nicely on Pandora, here are the keys:

	LEFT/RIGHT - steer ship
	A/B/X/Y - SHOOT!
	ESC - Quit


Orginal readme
--------------
abstract pixel shooter

On the web:
http://www.din.or.jp/~ku_/index.htm - original author's homepage (Japanese only)
http://code.mizzenblog.com/index.php?cat=2 - unix port (English only)

"Spout" is a small, abstract shooting game from Japanese developer kuni.  It plays somewhat like Finnish cavefliers, except you have to erode your surroundings with your ship's exhuast.  It's great fun :-)

This is a Pandora port of Spout.  If you want to play this game on Windows, get it from the Japanese page above





Original GP2X Notes:
--------------------

I spent a little time diffing the GP2X port source against the original code.
After a bit of cleanup, the game seems to play at full speed -- the same as the windows version.
I've included project files for CodeBlocks for the GP2X and Win32 builds.  Note that the Win32 
version will compile and run, but it will be full screen and rotated.  This is due to it using the
same video options as the GP2X, and it could be fixed but I didn't bother since there is already a 
working Win32 port of the game.  :-)

Enjoy!

Michael
adcockm@usa.net


---------------------

The GP2X port didn't require much work,
just setting a static screen size,
setting the right SDL Parameters (see http://wiki.gp2x.org/wiki/SDL_FAQ),
and adding joystick input for the GP2X

The timing, however, seems to be broken a bit as spout feels slower on the GP2X than on the PC.

bye,
no_skill
http://gp2x.72dpiarmy.com
