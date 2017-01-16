(ns test.server
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.logger :refer [wrap-with-logger]]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.stacktrace :as trace]
            [ring.util.response :as response]
            [clojure.stacktrace :refer [print-stack-trace]]
            [compojure.core :refer [ANY GET PUT POST DELETE defroutes]]
            [compojure.route :refer [resources not-found]]
            [environ.core :refer [env]]
            [aleph [netty] [http]]
            [compojure.route :as route])
  (:import (java.lang Integer)
           (java.net InetSocketAddress))
  (:gen-class))

(defroutes app
  (GET "/" _ (response/content-type
              (response/resource-response "index.html" {:root "public"})
              "text/html"))
  (resources "/" {:root "/public"})
  ;; Sente
  (not-found "Woooot? Not found!"))


;;
;; Server setup
;;

(defonce server (atom nil))

(defn start! [& [port ip]]
  (reset! server
          (aleph.http/start-server
           (-> app
               (wrap-defaults (assoc-in (if (env :production) secure-site-defaults site-defaults)
                                        [:params :keywordize] true)))
           {:port (Integer. (or port (env :port) 5000))
            :socket-address (if ip (new InetSocketAddress ip port))})))

(defn stop! []
  (when @server
    (.close @server)
    (reset! server nil)
    'stopped))

(defn -main [& [port ip]]
  (start! port ip)
  (aleph.netty/wait-for-close @server))

