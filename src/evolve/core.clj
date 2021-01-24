(ns evolve.core)

; Let's do a bit of evolution.
;
; We have very simple bacteria, living in a world where
; there is plenty of food, but radiation kills.
;
; On every cycle bacteria reproduce, and when they
; have enough radiation, they die.
;
;
; (run-game  (flatten
;             [(mk-bacts 100 1)
;              (mk-bacts 50 4)
;              (mk-bacts 30 95)]
;             )
;           300)

(defn bact [curr max]
  {:current curr
   :max max})

(defn make-turn-single
  "Return 0 or more bacteria"
  [{:keys [current max]}]
  (if (> current max)
    ; morto
    []

    ; vivo con figlio
    [(bact (+ 10 current) max)
     (bact 0 max)]))

(defn mk-bacts [max num]
  (map (fn [_] (bact 0 max)) (range num)))

(defn make-turn [bacterria]

  (->>
   (map make-turn-single bacterria)
   flatten
   shuffle
   (take 10000)))

(defn stats [bacterria]
  (map
   (fn [[k v]] [k (count v)])
   (group-by :max bacterria)))

(defn run-game [bacteeria turns]
  (loop [b bacteeria
         t 0]
    (prn "t" t "=" (stats b))
    (if (> t turns)
      (prn "Stats at: " turns (stats b))

      (recur (make-turn b) (inc t)))))




