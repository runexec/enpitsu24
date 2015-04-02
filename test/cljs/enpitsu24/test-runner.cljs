(ns enpitsu24.test-runner
  (:require
   [cljs.test :refer-macros [run-tests]]
   [enpitsu24.core-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'enpitsu24.core-test))
    0
    1))
