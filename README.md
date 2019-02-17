## P2P Chat

What is it? P2P chat is a very simple example of leveraging the libp2p library from clojurescript.

Why? As a quick demonstration of how we can build P2P applications in Reagent / Re-Frame with a data-binding backed by a pub/sub model provided by the libp2p framework.

How does it work?
- Static files for the initial page, javascript and css are provided by the server.
- Next, each browser window connects to a bootstrapped peer list using libp2p and then subscribes to a "topic"
- Under the covers, libp2p uses web-sockets to connect the peers to each other without connecting directly to one centralized server
- Messages that are sent are published and each node "subscribes" to these updates.
- When the node gets an update, it populates its own local "app db" in re-frame with the relevant message
- The messages view then gets the update and shows it to each of the users.

**NOTE** libp2p is very experimental and subject to change. This project was meant as a toy exploration and not meant as a production solution.

![Alt text](/screenshots/screenshot.png?raw=true "Screenshot")

## Installation

1. Clone this repo
1. Run `./scripts/install.sh` to obtain the libp2p library (warning: experimental)
1. Run `lein cljsbuild once` to build the javascript
1. Run `lein garden once` to build the css
1. Run `lein repl" and "(-main)` to start the server (only serves static files)

