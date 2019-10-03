# UniversalCardGameRandomizer

This project was originally intended to be a one-stop randomizer for any TCG game. The goal was to find new TCG games, reverse engineer them for a bit, then add them as separate packages to this tool. This didn't work well. For one thing, only Pokemon TCG really ever got love. For another, I became more comfortable with languages that aren't Java, and don't enjoy working with the current source. 

I will be adding a few features here and there to the Pokemon TCG part of the tool to make it as good as can be. Other than that, support will be dying off, as I (hopefully) write a better, more powerful Pokemon TCG-only randomizer with better support for hacks and mods using [pret's poketcg](https://github.com/pret/poketcg) source. 

## Rom Hack Support
Theoretically, any romhack that doesn't shift data around will work with this randomizer. Most bugs will be on the end of the hack, rather than this tool. If I notice a bug in my own hacks that causes it to break with this tool, I will try to fix them there.

The following romhacks are currently known to be (or trying to be) compatible:
* [Pokemon TCG - Card Loss Challenge](https://github.com/anmart/poketcg-hacks/tree/patch_CardLossChallenge) by Aroymart (me)

If you know of any other romhacks, please let me know so I can check if they're compatible.

## Building
Clone this project then open in either eclipse or netbeans. Netbeans is the tool currently being used for work, as the form builder is invaluable.

Does not require any special libraries or anything. 
