(ns leiningen.immutant.run
  (:require [leiningen.immutant.common  :as common]
            [leiningen.immutant.shim    :as shim]
            [immutant.deploy-tools.util :as util]))

(let [jboss-home (common/get-jboss-home)]
  (defn standalone-sh []
    (let [suffix (if (= (.. System (getProperties) (get "os.name") (toLowerCase) (indexOf "win")) 0)
      (str "bat")
      (str "sh"))]
      (str (.getAbsolutePath jboss-home) "/bin/standalone." suffix)))

(defn run
  "Starts up the Immutant specified by ~/.lein/immutant/current or $IMMUTANT_HOME, displaying its console output"
  ([]
    (run nil))
  ([project & opts]
    (util/with-jboss-home jboss-home
      (and project (not (util/application-is-deployed? project nil))
        (common/err "WARNING: The current app is not deployed - deploy with 'lein immutant deploy'"))
      (let [script (standalone-sh)
            params (replace {"--clustered" "--server-config=standalone-ha.xml"} opts)]
        (apply println "Starting Immutant:" script params)
        (apply shim/lein-sh-fn script params))))))
