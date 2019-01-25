def auth(token) {
    def (usr, pass) = token.split(':')
    assert usr == 'banana'
    assert pass == 'apple'
    println "Verification successful."
}

def newJob() {
    println "start"
    def date = Calendar.getInstance()
    def time = date.get(Calendar.HOUR_OF_DAY)
    def jobName = "RemoteJob" + time
    println "name set"
    def config = "curl " +
        "http://zac:11ccec7b2c0a7ab6a8b1328c6dbb20a9fa@172.17.0.2:8080/job/MvnPipe/config.xml" +
        " > config.xml"
    config.execute()
    Thread.start { System.err << config.err }
    config.waitFor()
    println "config generated."
    def job = "curl -X POST -H " + 
            "Content-Type:application/xml " + 
            "-d @config.xml " + 
            "http://zac:11ccec7b2c0a7ab6a8b1328c6dbb20a9fa@172.17.0.2:8080/createItem?name=${jobName}"
    job.execute()
    Thread.start { System.err << job.err }
    job.waitFor()
    println "Job created."
}

return [
    auth: this.&auth,
    newJob: this.&newJob
]