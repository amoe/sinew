# sinew

> While Michael Angeloʼs Sistine roof  
> His ‘Morning’ and his ‘Night’ disclose  
> How sinew that has been  pulled tight,  
> Or it may be loosened in repose,  
> Can rule by supernatural right  
> Yet be but sinew.

-- Yeats, _Michael Robartes and the Dancer_

It's a pluggable media tracker and metadata scanner/organizer.  Sinew knows
about the concept of a 'file root' and a 'scene type' and will allow you to
track these things.  For instance, you can then query: "all videos by
TheRussianHacker, where the type is `youve-been-doing-it-wrong`".

It also tracks the watched-status of each video -- manually toggled, not like
things like Plex, and allows you to store star-ratings.

## Installation

You can take a look at `install.sh`.  PostgreSQL 9.4 is required to store
the database.  Last tested with Leiningen 2.8.1

## Usage -- Web server

Before you can run the server, you'll have to apply the database schema.  You
can do that by running:

    lein migrate

After this, to start the server, do the following:

    lein run -m sinew.server/run

The server starts on port 8000 by default.

Or use the venerable `lein ring server-headless`, if you want to watch for
server reloads.  In this case the server will start on port 3000.  *NOTE: This
is currently not working.*

## Usage -- Command line

You can invoke the various CLI interfaces also using lein.  For instance,
one of them is `add-to-db`.

    lein run -m sinew.add-to-db

## Options

You have to configure it before it will work.  It expects to find a file at
`/usr/local/etc/sinew.edn`.  The file looks as such:

    {:file-root "/mnt/kirk/genre/action"
     :db-spec {:subprotocol "postgresql"
               :subname "//localhost/sinew"
               :user "sinew"
               :password "xyzzy"}
    :prefixes
     {:type1 {:url "http://www.site1.com/episode/"
              :selectors {:description [:div.desc]
                          :tags [:div.tags a]}}}}

Prefixes defines where to look for video descriptions.

One example is given of the site here.  At the moment the code assumes that each
video has a 'plaintext name', which is basically the machine-readable id for
that video, and that this id can simply be appended to the `:url` to get the
metadata page for the video.  That plaintext name is given by the user when
calling `add-to-db`.  Then the description is looked up with the Enlive selector
(which is `[:div.desc]` in this example).  The text-content of that element
forms the description that's stored in Sinew's database.  The same goes for the
tags, except that the selector for this case may return multiple elements.

Not every video may have tags, and that's pretty fine.

## License

Copyright © 2018 David Banks

License is AGPLv3.
