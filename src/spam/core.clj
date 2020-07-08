(ns spam.core
  (:require [clj-http.client :as client])
  (:gen-class))

(def API_URL "https://api.spotify.com/v1")
(def TOKEN "TOKEN")
(def PLAYLIST_URI "3ysl0PdLZ3A6t05Hqz2REh")
(def SONG_URI "spotify:track:3cfOd4CMv2snFaKAnMdnvK")

(defn build-url
  "Builds an URL for adding tracks to a playlist"
  [playlist-uri]
  (format "%s/playlists/%s/tracks" API_URL, playlist-uri)
)

(defn build-auth-headers
  "Builds the authorization headers from a token"
  [token]
  {"Authorization" (format "Bearer %s" token)}
  )

(defn build-body
  "Builds the POST body from a song uri"
  [uri]
  (format "{\"uris\": [\"%s\"]}" uri)
  )

(defn add-song
  "Adds a given song to a playlist"
  [playlist-uri song-uri]
  (client/post
    (build-url playlist-uri)
    {:headers (build-auth-headers TOKEN) :body (build-body song-uri)}
    )
)

(defn pause-random
  "Halts the current thread for a random delay from 0 to n ms"
  [n]
  (Thread/sleep (rand-int (+ n 1)))
  )

(defn spam
  "Add a song to a playlist x times"
  [x]
  (dotimes [n x]
    (println (format "Adding song %d" n))
    (add-song PLAYLIST_URI SONG_URI)
    (pause-random 200)
  )
)

(defn -main [& args]
)
