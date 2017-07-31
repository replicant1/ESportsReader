# ESportsReader

Rod Bailey
Sunday 25 September 2016

## Summary

This is my answer to the coding challenge for UBank.

## Philosophy

I have tried to use as few external libraries as possible, so as to focus on what I can do rather than what the libraries can do. Only one library is used, and that is JUnit.

In retrospect this may have been a mistake. Note the nasty little details that I had to deal with as a consequence like:

* XmlPullParser's very poor handling of nested XML
* HttpUrlConnection's unwillingness to follow 30x redirects if you're redirecting from HTTP to HTTPS

Libraries such as ROME and Volley would have shielded me from this, but also hidden an understanding of the low level details. I now understand fully how awful HTTP communication is at the low level, and what a mess the "format wars" between syndiaiton formats has created.

## User Stories

The following user stories are addressed by this implementation.

* Story 1 - As a user I want to pick the news that interests me. The UI presents several screens of cascading lists which the user can navigate to find the news they're interested in.
* Story 2 - As I user I want to read my news in an orientation that suite me. The app supports both portrait and landscape device orientations.
* Story 3 - As I user I want to minimise data usage. Documents remotely retrived are cached locally on a per-session basis. Also the ability of the server to perform conditional GETs is exploited to avoid repeated downloading of unmodified files.

## Shortcomings

* The app is not nearly as robust as it needs to be. Most of the challenges were associated with processing of various syndication formats, and handling the complexities of HTTP communication. Android itself was the least of my worries.

* I have not had the time to do anywhere near enough writing of automated unit tests - in fact, I've just started.

* Although the HTML source of each news feed item is stored in the local cahce, the images that the HTML references (and any other thing it drags in) is not stored in the cache.
