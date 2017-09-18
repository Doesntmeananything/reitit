(ns reitit.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as str]
            [reitit.core :as reitit]))

;;
;; routes
;;

(s/def ::path (s/with-gen (s/and string? #(or (str/blank? %) (str/starts-with? % "/")))
                          #(gen/fmap (fn [s] (str "/" s)) (s/gen string?))))

(s/def ::arg (s/and any? (complement vector?)))
(s/def ::meta (s/map-of keyword? any?))
(s/def ::result any?)

(s/def ::raw-route
  (s/cat :path ::path
         :arg (s/? ::arg)
         :childs (s/* (s/and (s/nilable ::raw-route)))))

(s/def ::raw-routes
  (s/or :route ::raw-route
        :routes (s/coll-of ::raw-route :into [])))

(s/def ::route
  (s/cat :path ::path
         :meta ::meta
         :result (s/? any?)))

(s/def ::routes
  (s/or :route ::route
        :routes (s/coll-of ::route :into [])))

;;
;; router
;;

(s/def ::router reitit/router?)
(s/def :reitit.router/path ::path)
(s/def :reitit.router/routes ::routes)
(s/def :reitit.router/meta ::meta)
(s/def :reitit.router/expand fn?)
(s/def :reitit.router/coerce fn?)
(s/def :reitit.router/compile fn?)
(s/def :reitit.router/conflicts fn?)
(s/def :reitit.router/router fn?)

(s/def ::opts
  (s/nilable
    (s/keys :opt-un [:reitit.router/path
                     :reitit.router/routes
                     :reitit.router/meta
                     :reitit.router/expand
                     :reitit.router/coerce
                     :reitit.router/compile
                     :reitit.router/conflicts
                     :reitit.router/router])))

(s/fdef reitit/router
        :args (s/or :1arity (s/cat :data (s/spec ::raw-routes))
                    :2arity (s/cat :data (s/spec ::raw-routes), :opts ::opts))
        :ret ::router)
