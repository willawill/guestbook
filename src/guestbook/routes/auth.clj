(ns guestbook.routes.auth
  (:require [compojure.core :refer [defroutes GET POST]]
            [hiccup.form :refer :all]
            [noir.session :as session]
            [noir.response :refer [redirect]]
            [guestbook.views.layout :as layout]))


(defn control [field name text]
  (list (label name text)
        (field name)
        [:br]))

(defn registration-page []
  (layout/common
    (form-to [:post "/register"]
      (control text-field :id "Screen name")
      (control password-field :pass "Password")
      (control password-field :pass1 "Retype Password")
      (submit-button "Create Account"))))

(defn login-page []
  (layout/common
   (form-to [:post "/login"]
            (control text-field :id "Screen name")
            (control password-field :pass "Password")
            (submit-button "Log in"))))

(defroutes auth-routes
  (GET "/register" [] (registration-page))
  (POST "/register" [id pass pass1]
        (if (= pass pass1)
          (redirect "/")
          (registration-page)))

  (GET "/login" [] (login-page))
  (POST "/login" [id pass]
        (session/put! :user id)
        (redirect "/")))