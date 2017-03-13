(ns om-tutorial.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [clojure.walk :as w]))



(def app-state
  (atom
    {:app/title                "Traffic Intersection"
     :road/general-speed-limit 2
     :road/lane-thickness      1
     :canvas/x                 100
     :canvas/y                 100
     :general/speed            {:units 1 :second 1}
     :road/roads               [
                                {:road/name      "Main St"
                                 :position/terminus
                                                 {:position/a {:position/x 50
                                                               :position/y 10}
                                                  :position/b {:position/x 50
                                                               :position/y 90}}
                                 :division/lanes [
                                                  {
                                                   :lane/name                                             "south to west"
                                                   :lane/intersection-prevelance-and-initial-signal-state [[:prevale :red]]
                                                   :lane/thickness                                        :road/lane-thickness
                                                   :lane/lane-from-port-where-a-is-fore-b-is-aft          1
                                                   :lane/auto-intake                                      :position/a
              cd                                     :lane/auto-schedule                                    [{:auto/body :body-size/coupe :auto/speed-variance 0 :init-time-spread 2}
                                                                                                           ]
                                                   }
                                                  {
                                                   :lane/name                                             "south to north"
                                                   :lane/intersection-prevelance-and-initial-signal-state [:prevale :red]
                                                   :lane/thickness                                        :road/lane-thickness
                                                   :lane/lane-from-port-where-a-is-fore-b-is-aft          2
                                                   :lane/auto-intake                                      :position/a
                                                   :lane/auto-schedule                                    [{:auto/body :body-size/coupe :auto/speed-variance 0 :init-time-spread 2}
                                                                                                           ]
                                                   }
                                                  {
                                                   :lane/name                                             "north to south"
                                                   :lane/intersection-prevelance-and-initial-signal-state [:prevale :red]
                                                   :lane/thickness                                        :road/lane-thickness
                                                   :lane/lane-from-port-where-a-is-fore-b-is-aft          3
                                                   :lane/auto-intake                                      :position/b
                                                   :lane/auto-schedule                                    [{:auto/body :body-size/coupe :auto/speed-variance 0 :init-time-spread 2}
                                                                                                           ]
                                                   }
                                                  {
                                                   :lane/name                                             "south to east"
                                                   :lane/intersection-prevelance-and-initial-signal-state [:prevale :red]
                                                   :lane/thickness                                        :road/lane-thickness
                                                   :lane/lane-from-port-where-a-is-fore-b-is-aft          4
                                                   :lane/auto-intake                                      :position/b
                                                   :lane/auto-schedule                                    [{:auto/body :body-size/coupe :auto/speed-variance 0 :init-time-spread 2}
                                                                                                           ]
                                                   }
                                                  ]
                                 }

                                {:road/name      "Small Town Ave"
                                 :position/terminus
                                                 {:position/a {:position/x 5
                                                               :position/y 50}
                                                  :position/b {:position/x 95
                                                               :position/y 50}}
                                 :division/lanes [
                                                  {
                                                   :lane/name                                             "east to north"
                                                   :lane/intersection-prevelance-and-initial-signal-state [:prevale :red]
                                                   :lane/thickness                                        :road/lane-thickness
                                                   :lane/lane-from-port-where-a-is-fore-b-is-aft          1
                                                   :lane/auto-intake                                      :position/a
                                                   :lane/auto-schedule                                    [{:auto/body :body-size/coupe :auto/speed-variance 0 :init-time-spread 2}
                                                                                                           ]
                                                   }
                                                  {
                                                   :lane/name                                             "east to west"
                                                   :lane/intersection-prevelance-and-initial-signal-state [:prevale :red]
                                                   :lane/thickness                                        :road/lane-thickness
                                                   :lane/lane-from-port-where-a-is-fore-b-is-aft          2
                                                   :lane/auto-intake                                      :position/a
                                                   :lane/auto-schedule                                    [{:auto/body :body-size/coupe :auto/speed-variance 0 :init-time-spread 2}
                                                                                                           ]
                                                   }
                                                  {
                                                   :lane/name                                             "west to east"
                                                   :lane/intersection-prevelance-and-initial-signal-state [:prevale :red]
                                                   :lane/thickness                                        :road/lane-thickness
                                                   :lane/lane-from-port-where-a-is-fore-b-is-aft          3
                                                   :lane/auto-intake                                      :position/b
                                                   :lane/auto-schedule                                    [{:auto/body :body-size/coupe :auto/speed-variance 0 :init-time-spread 2}
                                                                                                           ]
                                                   }
                                                  {
                                                   :lane/name                                             ""
                                                   :lane/intersection-prevelance-and-initial-signal-state [:prevale :red]
                                                   :lane/thickness                                        :road/lane-thickness
                                                   :lane/lane-from-port-where-a-is-fore-b-is-aft          4
                                                   :lane/auto-intake                                      :position/b
                                                   :lane/auto-schedule                                    [{:auto/body :body-size/coupe :auto/speed-variance 0 :init-time-spread 2}
                                                                                                           ]
                                                   }
                                                  ]}
                                ]
     }
    )
  )


(defmulti read (fn [env key params] key))

(defmethod read :default
  [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:value :not-found})))

(defmethod read :animals/list
  [{:keys [state] :as env} key {:keys [start end]}]
  {:value (subvec (:animals/list @state) start end)})

(defmethod read :road/roads
  [{:keys [state] :as env} key params]
  {
   :value (get @state :road/roads [{:road/name "Nowhere St"}])
   }
  )



(defui TrafficWorld
  static om/IQuery
  (query [this]
    '[:app/title :road/roads])
  Object
  (render [this]
    (let [{:keys [app/title road/roads]} (om/props this)]
      (dom/div nil
               (dom/h2 nil title)
               (apply dom/ul nil
                      (map (fn [m]
                             (let [{:keys                                         [road/name division/lanes]
                                    {{ax :position/x ay :position/y} :position/a
                                     {bx :position/x by :position/y} :position/b} :position/terminus} m]
                               (dom/div nil
                                        (dom/li nil (str "There exists a street named " name " it has its 'a' terminus at x:" ax " y:" ay " and its 'b' terminus at x:" bx))

                                        )
                               )
                             )
                           roads))
               (dom/h3 nil "place holder for intersection calculated data")))))

(defn width-of-lanes [lanes props]
  ; need to figure out how to get this reference to a global thickness element working
  ;(map (fn [x] (let [thickness (get x :lane/thickness 0)]
  ;               ;(if (not (number? thickness)) (get props :road/lane-thickness) thickness)
  ;               (if (= thickness :road/lane-thickness) (let [{keys road/lane-thickness} props] lane-thickness) thickness)
  ;               )
  ;              ) lanes)
  )

(defn road-coordinate [road props]
  (let [{:keys                                         [road/name division/lanes]
         {{ax :position/x ay :position/y} :position/a
          {bx :position/x by :position/y} :position/b} :position/terminus} road]
    [ax ay bx by (width-of-lanes lanes props)]
    ))

(defn all-road-coordinates [roads props]
  (map #(road-coordinate % props)
       roads
       )
  )

(defn intersection [roads props]
  (all-road-coordinates roads props)
  )

(def reconciler
  (om/reconciler
    {:state  app-state
     :parser (om/parser {:read read})}))

(om/add-root! reconciler
              TrafficWorld (gdom/getElement "app"))
