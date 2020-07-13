(defproject spam "0.1.0-SNAPSHOT"
  :description "Small and dumb script to add the same song to a Spotify playlist"
  :dependencies [[org.clojure/clojure "1.10.0"], [clj-http "2.3.0"], [org.clojure/tools.cli "1.0.194"]]
  :plugins [[lein-cljfmt "0.6.8"]]
  :repl-options {:init-ns spam.core}
  :main spam.core
  )
