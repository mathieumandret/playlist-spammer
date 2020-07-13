(ns spam.core
  (:require [clj-http.client :as client])
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(def API_URL "https://api.spotify.com/v1")
(def PLAYLIST_URI "3ysl0PdLZ3A6t05Hqz2REh")
(def SONG_URI "spotify:track:3cfOd4CMv2snFaKAnMdnvK")
(def DELAY 200)

(defn build-url
  "Builds an URL for adding tracks to a playlist"
  [playlist-uri]
  (format "%s/playlists/%s/tracks" API_URL, playlist-uri))

(defn build-auth-headers
  "Builds the authorization headers from a token"
  [token]
  {"Authorization" (format "Bearer %s" token)})

(defn build-body
  "Builds the POST body from a song uri"
  [uri]
  (format "{\"uris\": [\"%s\"]}" uri))

(defn add-song
  "Adds a given song to a playlist"
  [playlist-uri song-uri token]
  (client/post
   (build-url playlist-uri)
   {:headers (build-auth-headers token) :body (build-body song-uri)}))

(defn pause-random
  "Halts the current thread for a random delay from 0 to n ms"
  [n]
  (Thread/sleep (rand-int (+ n 1))))

(defn spam
  "Add a song to a playlist a given number of times"
  [playlist song token times]
  (dotimes [n times]
    (println (format "Adding song %d" n))
    (add-song playlist song token)
    (pause-random DELAY)))

(def cli-options
  [["-s" "--song SONG_URI" "Song uri"]
   ["-p" "--playlist PLAYLIST_URI" "Playlist URI"]
   ["-t" "--token JWT_Token" "JWT Token"]
   ["-n" "--times TIMES" "How many times to add the song"]])

(defn missing-arguments
  "Finds any missing argument, returns them as a list"
  [provided-args mandatory-args]
  (->>
   (map #(hash-map :cont (contains? provided-args %), :arg %) mandatory-args)
   (filter #(not (get % :cont)))
   (map #(get % :arg))))

(defn format-missing-args
  "Takes a list of missing params and produces an error message"
  [missing-params]
  (->>
   (map name missing-params)
   (reduce #(str %1 ", " %2))
   (str "Following arguments are missing: ")))

(defn validate-options
  "Checks that all options are valid, returns a map with :status ok/ko and an :exit message
  if ko"
  [opts]
  (let [{:keys [options arguments errors summary]} (parse-opts opts cli-options)]
    (let [missing (missing-arguments options #{:song :playlist :token :times})]
      (cond
        errors
        {:status :ko :exit-message errors}
        (seq missing)
        {:status :ko :exit-message (format-missing-args missing)}
        :else
        {:status :ok :options options}))))

(defn error
  [msg]
  (println msg))

(defn run
  [options]
  (let [{:keys [song playlist token times]} options]
    (try
      (spam song playlist token times)
      (catch Exception e (str "Error " (.getMessage e))))))

(defn -main [& args]
  (let [{:keys [status exit-message options]} (validate-options args)]
    (if (= status :ko) (error exit-message) (run options))))

