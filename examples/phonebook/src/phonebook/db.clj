;; Copyright © 2015, JUXT LTD.

(ns phonebook.db
  (:require
   [schema.core :as s]))

(s/defschema PhonebookEntry {:surname String :firstname String :phone String})

(s/defschema Phonebook {s/Int PhonebookEntry})

(s/defn create-db [entries :- Phonebook]
  (assert entries)
  {:phonebook (ref entries)
   :next-entry (ref (if (not-empty entries)
                      (inc (apply max (keys entries)))
                      1))})

(defn add-entry
  "Add a new entry to the database. Returns the id of the newly added
  entry."
  [db entry]
  (dosync
   ;; Why use 2 refs when one atom would do? It comes down to being able
   ;; to return nextval from this function. While this is possible to do
   ;; with an atom, its feels less elegant.
   (let [nextval @(:next-entry db)]
     (alter (:phonebook db) conj [nextval entry])
     (alter (:next-entry db) inc)
     nextval)))

(s/defn get-entries :- Phonebook
  [db]
  @(:phonebook db))

(s/defn get-entry :- PhonebookEntry
  [db id]
  (get @(:phonebook db) id))

(s/defn count-entries :- s/Int
  [db]
  (count @(:phonebook db)))