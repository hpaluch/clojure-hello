(ns example.core
  ;; defroutes
  (:use [compojure.core                 :only [defroutes GET POST]]
        [ring.middleware.params         :only [wrap-params]])
  (:require
    [ring.adapter.jetty :as jetty] ;; jetty via ring
    [clostache.parser :as clostache] ;; mustache for clojure
    [compojure.route :as route]
   ))

;; reads templates/template-name.mustache to string
(defn read-template [template-name]
  (slurp (clojure.java.io/resource (str "templates/" template-name ".mustache"))
         :encoding "UTF-8"))

(defn render-template [template-file params]
  (clostache/render (read-template template-file) params))


; View functions
(defn index []
  (render-template "index" {:greeting "Bonjour"}))

(defn post-index [name]
  (render-template "index" {:greeting (str "Bonjour " name)}))


; Routing
(defroutes main-routes
  (GET "/" [] (index))
  (POST "/greeting" [name] (post-index name))
  (route/resources "/")
  (route/not-found "404 Not Found"))

;; required for parameters to work, see http://stackoverflow.com/questions/6036733/missing-form-parameters-in-compojure-post-request
(def app (wrap-params main-routes))


(defn -main []
  (jetty/run-jetty app {:port 5000}))
