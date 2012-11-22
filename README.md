crt - Clojure Refactoring Tool
================================

Disclaimer
-----------

This is a work in progress.

Idea
------

I'm trying to create a simple tool for the most basic Clojure refactorings, such as rename, extract function and move, as as portable Clojure code. Also there will be various useful visualizations for your Clojure project structure.

Usage
-------

Add dependency to your project (maybe dev)

    [crt "0.1.0"]

Require the code

    (:require [crt.core :as crt])

Run the tool while you have your other code loaded as well
    
    (crt/open-crt)

A window will pop up with your code dependencies shown. There is not much you can do at the moment.

License
---------

Copyright (C) 2011-2012 Markku Rontu / markku.rontu@iki.fi / @zorcam

Distributed under the Eclipse Public License, the same as Clojure.
