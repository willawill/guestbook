(ns guestbook.routes.auth
  (:require [compojure.core :refer [defroutes GET POST]]
            [hiccup.form :refer :all]
            [guestbook.views.layout :as layout]))
(defn registration-page []
  (layout/common
   [:p "Welcome to registration page!"])
  )

(defroutes auth-routes
  (GET "/register" [] (registration-page)))