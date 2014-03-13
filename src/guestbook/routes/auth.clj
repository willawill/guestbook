(ns guestbook.routes.auth
  (:require [compojure.core :refer [defroutes GET POST]]
            [hiccup.form :refer :all]
            [noir.session :as session]
            [noir.response :refer [redirect]]
            [noir.util.crypt :as crypt]
            [noir.validation :refer [rule errors? has-value? on-error]]
            [guestbook.models.db :as db]
            [guestbook.views.layout :as layout]))


(defn format-error [[error]]
  [:p.error error])

(defn control [field name text]
  (list (on-error name format-error)
        (label name text)
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

(defn handle-login [id pass]
  (let [user (db/get-user id)]
  (rule (has-value? id)
        [:id "Screen name is required!"])
  (rule (has-value? pass)
        [:pass "Password is required!"])
  (rule (and user (crypt/compare pass (:pass user)))
        [:pass "wrong password"])
  (if (errors? :id :pass)
    (login-page)
    (do
      (session/put! :user id)
      (redirect "/")))))

(defn handle-registration [id pass pass1]
  (rule (= pass pass1)
        [:pass "Password and password confirmation must match"])
  (if (errors? :pass)
    (registration-page)
    (do
      (db/add-user-record {:id id :pass (crypt/encrypt pass)})
        (redirect "/login"))))



(defroutes auth-routes
  (GET "/register" [] (registration-page))
  (POST "/register" [id pass pass1]
        (if (= pass pass1)
          (redirect "/")
          (registration-page)))

  (GET "/login" [] (login-page))
  (POST "/login" [id pass]
    (handle-login id pass))

  (GET "/logout" []
       (layout/common
        (form-to [:post "/logout"]
        (submit-button "Log Out"))))
  (POST "/logout" []
        (session/clear!)
        (redirect "/")))