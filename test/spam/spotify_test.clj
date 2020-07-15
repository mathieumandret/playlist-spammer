(ns spam.spotify-test
  (:require [clojure.test :refer :all]
            [spam.spotify :refer :all]))


(deftest url-build-test
  (testing "build-url builds proper URLs" 
    (is (= "https://api.spotify.com/v1/playlists/playlist/tracks" (build-url "playlist")))))

(deftest auth-build-test
  (testing "Authorization header is built propely"
    (is (= {"Authorization" "Bearer TOKEN"} (build-auth-headers "TOKEN")))))

(deftest body-build-test
  (testing "Build body adds uris to body"
    (is (= "{\"uris\": [\"URI\"]}" (build-body "URI")))))
