# intersection-om-next
my attempt to simulate an intersection using clojurescript and om next

To run what I presently have (visibly not much, essentially just displaying data about what streets are defined in the initial app state) you'll need the clojure build tool Leiningen https://leiningen.org/

Once you have leiningen installed / available then you would type in the following into your terminal at the project root

lein run -m clojure.main script/figwheel.clj

Presently I'm in the middle of making the logic to create intersection nodes out of streets that intersect. The decision to make the intersections emergent from the data defining the streets is making it take a bit longer to implementing the intersection simulation, but also would enable a far more extensible simulator as it could simulate entire grids of streets with many different configurations.

