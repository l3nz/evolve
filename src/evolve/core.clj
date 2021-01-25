(ns evolve.core)

;; ## Let's do a bit of evolution.
;
; We have very simple bacteria, living in a world where
; there is plenty of food, but radiation kills.
;
; On every cycle bacteria reproduce and take more radiation;
; when they have enough radiation, they die.
;
; Whenever a bacterium reproduces, it transmits along its
; own `:max` resistence to radiation; while newborns
; have zero total radiation.
;
; The world is limited to `WORLD_SIZE` items, represented
; internally as a vector of all living bacteria; on each turn,
; bacteria are shuffled in a random sequence and only
; the number specified in `WORLD_SIZE` survive.
;
; ## How to play
;
; Fire up your repl and type:
;
; First you create a gene pool, with only 1 bacterium with a
; radiation resistance of 100, and 99 with a resistance of 30:
;
; `(def POOL (flatten [(mk-bacts 100 1) (mk-bacts 30 99)]))`
;
; then you run a number of runs of the game. Each run is the time
; needed for reproduction; as a reference, in *E. Coli* under
; ideal conditions, it takes about 20 minutes.
;
; `(run-game POOL 300)`
;
; Again, for *E. Coli* this is about 2 days. You'll be impressed
; at how quickly the system

(def WORLD_SIZE 10000)

(defn bact
  "Build one **bacterium**.

  So far it only has two attributes:

  - `:current` is how much radiation it got so far
  - `:max` its maximum resistance.

  "
  [curr max]
  {:current curr
   :max max})

(defn mk-bacts
  "Makes a number of bacteria, all with the same genes.

  We'll use this to bootstrap the game with sets of different bacteria.
  "
  [max num]
  (map (fn [_]
         (bact 0 max))
       (range num)))

(defn make-turn-single
  "Runs one **turn** for one bacterium.

  Returns a vector of 0 or more bacteria, as on each turn:

  - if it's got too much radiation, it dies -> returns no elements
  - if not, the original bacterium is  returned with more radiation,
    and a newborn one is too.

  "
  [{:keys [current max]}]
  (if (> current max)
    ; it's dead
    []

    [; original
     (bact (+ 10 current) max)
      ; newborn
     (bact 0 max)]))

(defn make-turn
  "Runs a turn of the game, by going through
  all living bacteria, applying `make-turn-simple` to them,
  and then making sure that all the ones that
  exceed `WORLD_SIZE` are randomly  discarded.
  "
  [all-bacteria]

  (->>
   (map make-turn-single all-bacteria)
   flatten
   shuffle
   (take WORLD_SIZE)))

(defn stats
  "Prints very basic statistics on a set of bacteria.

  So far, it prints a vector of the `:max` gene, and
  how many items have this gene.

  E.g. `{[50 100] [30 400]}` means that there are
  100 bacteeria with `:max` =  50 and 400 with
  `:max` =  30 (and theey won't last long - hee hee hee).


  This could definitely be improved.

  "

  [all-bacteria]
  (map
   (fn [[k v]] [k (count v)])
   (group-by :max all-bacteria)))

(defn run-game
  "## Runs a game.

  You start it with a vector of bacteria, that you can
  create in bulk with the `mk-bacts` function, and
  specify a number of turns for this to run.

  On each turn, statistics are printed so you know the
  relative prevalence of `:max` genes on the gene pool.

  "
  [all-bacteria turns]
  (loop [b all-bacteria
         t 0]
    (prn "t" t "=" (stats b))
    (if (> t turns)
      (prn "Stats at: " turns (stats b))

      (recur (make-turn b) (inc t)))))




