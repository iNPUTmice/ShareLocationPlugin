# Share Location Plugin for Conversations

This plugin adds location sharing capabilities to the open source Jabber/XMPP
client Conversations.

### Why a plugin?
One of the design principles of Conversations is to require as few permissions
as possible. Not every user might be comfortable with Conversations accessing
the device location at any given time.
On top of that this plugin makes heavy use of Google Play Services and the
Google Maps API to improve the user experince. (Better location detection using
cell towers and display of an actual map to verify the users position.)
Both of which are not really suitable for mainline Conversations.

### How to use
This plugin requires Conversations >= 1.2.0. If installed Conversations will
show a *Send location* in the attach menu. (Next to *Choose picture*, *Take
picture* and *Choose file*)

### How does it work
The Share Location Plugin offers an intent called
`eu.siacs.conversations.location.request` which returns (after user confirmation)
a latitude and longitude as well as accuracy and altitude information (which
currently are not being used).
Corresponding to that the plugin also offers an intent
`eu.siacs.conversations.location.show` to simple display a marker on a map. This
is being used to display a contacts location in a more lightweight enviroment than
the actual Google Maps application.

### How to build / install
The primary distrubution channel for this plugin is the Google Play Store because
it just won’t run on AOSP.
If you do want to run your own version you will need a Google Maps API Key and
place this in a string resource called `google_maps_api_key`.
