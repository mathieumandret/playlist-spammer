(ns spam.cli
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [spam.spotify :as spotify])
  (:gen-class))

(def cli-options
  [["-s" "--song SONG_URI" "Song uri"]
   ["-p" "--playlist PLAYLIST_URI" "Playlist URI"]
   ["-t" "--token JWT_Token" "JWT Token"]
   ["-n" "--times TIMES" "How many times to add the song"]
   ["-h", "--help"]])

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



(defn validate-options
  "Checks that all options are valid, returns a map with :status ok/ko and an :exit message
  if ko"
  [opts]
  (let [{:keys [options arguments errors summary]} (parse-opts opts cli-options)]
    (let [missing (missing-arguments options #{:song :playlist :token :times})]
      (cond
        (:help options)
        {:status :ko :exit-message summary}
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
      (spotify/spam song playlist token times)
      (catch Exception e (str "Error " (.getMessage e))))))

(defn -main [& args]
  (let [{:keys [status exit-message options]} (validate-options args)]
    (if (= status :ko) (error exit-message) (run options))))

