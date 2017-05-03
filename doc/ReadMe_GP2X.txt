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
