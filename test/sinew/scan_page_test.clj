(ns sinew.scan-page-test
  (:require [clojure.test :refer :all]
            [sinew.scan-page :as scan-page]
            [net.cgrand.enlive-html :as html]
            [sinew.utility :as utility]))

(def fakedata (html/html-resource (utility/string->stream "<span>foo</span")))

(deftest extract-tags-works
  (is (= ["foo"] (scan-page/extract-tags fakedata [:span]))))

(deftest extract-description-works
  (is (= "foo" (scan-page/extract-description fakedata [:span]))))
