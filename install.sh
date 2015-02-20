#! /bin/sh

lein uberjar
sudo mkdir -p /usr/local/lib/sinew
sudo cp target/uberjar/sinew-0.1.0-SNAPSHOT-standalone.jar /usr/local/lib/sinew/sinew.jar
sudo cp wrapper.sh /usr/local/bin/sinew-add-to-db
sudo chmod +x /usr/local/bin/sinew-add-to-db
