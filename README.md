# sinew

It's a pluggable movie tracker and metadata scanner/organizer.  Sinew knows
about the concept of a 'scene base' and a 'scene type' and will allow you to
track these things.  For instance, you can then query: "all videos by
TheRussianHacker, where the type is `youve-been-doing-it-wrong`".

## Installation

You can take a look at `install.sh`.  PostgreSQL 9.4 is required to store
the database.  Last tested with Leiningen 2.8.1

## Usage

Before you can run the server, you'll have to apply the database schema.  You
can do that by running:

    lein migrate

After this, to start the server, do the following:

    lein run -m sinew.server/run

The server starts on port 8000 by default.

Or use the venerable `lein ring server-headless`, if you want to watch for
server reloads.  In this case the server will start on port 3000.

## Options

You have to configure it before it will work.  It expects to find a file at
`/usr/local/etc/sinew.edn`.  The file looks as such:

    {:file-root "/mnt/kirk/genre/action"
     :db-spec {:subprotocol "postgresql"
               :subname "//localhost/sinew"
               :user "sinew"
               :password "xyzzy"}
    :prefixes
     {:type1 "http://www.site1.com/episode/"
      :type2 "http://www.site2.org/episode/"}}

Prefixes defines where to look for video descriptions.


## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2018 David Banks

License is AGPLv3.
