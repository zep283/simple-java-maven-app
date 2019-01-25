node('master') {
    maven = tool 'M3'
    stage('Checkout') {
        checkout scm 
    }
    stage('Build') {
        mvn "clean install"
    }
}

parallel verify: {
    node('slave1') {
        stage('Verification') {
            if (env.BRANCH_NAME == 'multibranch') {
                echo 'Cannot verify because reasons.'
            } else {
                authVerify()
            }
        }
    }
}, tester: {
    node('slave2') {
        maven = tool 'M3'
        stage('Test') {
            mvn "test"
            junit 'target/surefire-reports/*.xml'
        }
    }
}, sAnalysis: {
    node('slave3') {
        scanner = tool 'Scanner' 
        stage('SonarQube analysis') {
            sonar()
        }
    }
}, 
failFast: true|false

node('master') {
    maven = tool 'M3'
    stage('QA') {
        timeout(time: 10, unit: 'MINUTES') {
            waitForQualityGate abortPipeline: true
        }
    }
    stage('Deliver') {
        withEnv( ["PATH+MAVEN=${maven}/bin"] ) {
            sh './jenkins/scripts/deliver.sh'
        }
        artifactory()
    }
}

def mvn(args) {
    withEnv( ["PATH+MAVEN=${maven}/bin"] ) {
        sh "mvn ${args}" 
    }
}
def sonar() {
    withSonarQubeEnv('Sonarqube') {
        sh "${scanner}/bin/sonar-scanner"
    }
}
def authVerify() {
    withCredentials([usernameColonPassword(credentialsId: 'fruity', variable: 'USERPASS')]) {
        rootdir = pwd()
        method = load "${rootdir}/auth.groovy"
        method.auth(USERPASS)
    }
}
def artifactory() {
    withCredentials([usernamePassword(credentialsId: 'art', usernameVariable: 'USR', passwordVariable: 'PASS')]) {
        rtServer (
            id: "Artifactory-1",
            url: "http://172.17.0.3:8081/artifactory",
            username: "${USR}",
            password: "${PASS}"
        )
        rtUpload (
            serverId: "Artifactory-1",
            spec:
                """{
                "files": [
                    {
                    "pattern": "/home/Documents/simple-java-maven-app/auth.groovy",
                    "target": "Jenkins-integration/"
                    }
                ]
                }"""
        )
    }               
}