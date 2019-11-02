(ns sinew.scan-page-test
  (:require [clojure.test :refer :all]
            [sinew.jsoup-scanner :as jsoup-scanner :refer [extract-description]]
            [net.cgrand.enlive-html :as html]
            [sinew.utility :as utility]))

(def nthchild-data (jsoup-scanner/get-html-from-string "<div><p>Foo</p><p>Bar</p><p>Baz</p></div>"))

(def fakedata (jsoup-scanner/get-html-from-string  "<span>foo</span"))

(deftest extract-tags-works
  (is (= ["foo"] (jsoup-scanner/extract-tags fakedata "span"))))

(deftest extract-description-works
  (is (= "foo" (jsoup-scanner/extract-description fakedata "span"))))

  (def html "<div class=\"tags\"><a href=\"#foo\">foo</a><a href=\"#bar\">bar</a></div>")

  (def selector "div.tags a")
