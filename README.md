# sinew

It's a pluggable movie tracker and metadata scanner/organizer.  Sinew knows
about the concept of a 'scene base' and a 'scene type' and will allow you to
track these things.  For instance, you can then query: "all videos by
TheRussianHacker, where the type is `youve-been-doing-it-wrong`".

## Installation

You can take a look at `install.sh`.

## Usage

    lein run -m sinew.server/run

The server starts on port 8000 by default.

## Options

You have to configure it before it will work.  It expects to find a file at
`/usr/local/etc/sinew.edn`.  The file looks as such:

    {:file-root "/mnt/kirk/genre/action"}


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
