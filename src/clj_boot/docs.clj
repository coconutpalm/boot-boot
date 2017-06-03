(ns clj-boot.docs
  (:require [hiccup.page :refer [html5]]))



(defn renderer [{content :entry}]
  (html5 {:lang "en"}
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0, user-scalable=no"}]
     [:meta {:name "description" :itemprop "description" :content (str "Library Documentation - " (:name content) " - " (:description content))}]
     [:title (:name content)]
     [:link {:rel "shortcut icon" :href "/favicon.ico"}]
     [:link {:rel "stylesheet" :href "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
             :integrity "sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7"
             :crossorigin "anonymous"}]
     [:link {:rel "stylesheet" :href "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css"
             :integrity "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css"
             :crossorigin "anonymous"}]

     [:link {:rel "stylesheet" :href "https://fonts.googleapis.com/css?family=Source+Code+Pro|Source+Sans+Pro|Source+Serif+Pro"}]

     [:link {:rel "stylesheet" :href "https://fonts.googleapis.com/css?family=Josefin+Sans|Josefin+Slab"}]

     [:script {:src "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"
              :integrity "sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS"
              :crossorigin "anonymous"}]
     #_(include-css "/css/app.css")]

    [:body
     [:div {:class "jumbotron"}
      [:div {:class "container"}
       [:h1 (:name content)]
       [:p (:description content)]]]

     [:div {:class "container"}
      [:div {:class "row"}
       [:div {:class "col-md-12"}
        (str (:content content))]]]]))
