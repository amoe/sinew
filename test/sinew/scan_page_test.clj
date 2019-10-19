(ns sinew.scan-page-test
  (:require [clojure.test :refer :all]
            [sinew.jsoup-scanner :as jsoup-scanner]
            [net.cgrand.enlive-html :as html]
            [sinew.utility :as utility]))

(def fakedata (jsoup-scanner/get-html-from-string  "<span>foo</span"))

(deftest extract-tags-works
  (is (= ["foo"] (jsoup-scanner/extract-tags fakedata "span"))))

(deftest extract-description-works
  (is (= "foo" (jsoup-scanner/extract-description fakedata "span"))))
