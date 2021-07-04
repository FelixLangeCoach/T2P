pipeline {
    environment {
        VERSION = getVersion()
        DOCKER_VERSION = getDockerVersion()
        registryCredential = 'docker-hub'
    }
    agent {
        docker {
            image 'maven:3.6.3-jdk-11'
            args '-u root'
        }
    } 

    stages {
        stage('build') {
            steps {
                sh 'mvn clean install -Dmaven.test.skip=true'
            }
        }
        stage('deploy jar') {
            steps {
                configFileProvider([configFile(fileId: 'nexus-credentials', variable: 'MAVEN_SETTINGS')]) {
                    sh 'mvn -s $MAVEN_SETTINGS deploy -Dmaven.test.skip=true'
                }
            }
        }
        stage('build docker') {
            steps {
                script {
                        docker.withRegistry('https://registry.hub.docker.com/v1/repositories/woped', registryCredential) {
                            def dockerImage = docker.build("woped/text2process:$DOCKER_VERSION")
                            def dockerImageLatest = docker.build("woped/text2process:latest")
                            dockerImage.push();
                            dockerImageLatest.push();
                        }
                }
            }
        }
    }
}

def getVersion() {
    pom = readMavenPom file: 'pom.xml'
    return pom.version
}

def getDockerVersion() {
    pom = readMavenPom file: 'pom.xml'
    version = pom.version

    if(version.toString().contains('SNAPSHOT')) {
        return version + '-' + "${currentBuild.startTimeInMillis}"
    } else {
        return version
    }
}
