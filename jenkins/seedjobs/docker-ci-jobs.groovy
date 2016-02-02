//def giturl = 'https://github.com/danielpalstra/webapp.git'
def giturl = 'http://gitlab.gntry.io:10080/bastiaan/awesome-webapp.git'


// Build a new docker container
job('build-docker-webapp') {
  deliveryPipelineConfiguration('Build', 'Docker build')
  scm {
    git {
      remote {
        url(giturl)
      }
      createTag(false)
      clean()
    }
  }
  steps {
      shell('docker login --email=admin@gntry.io --username=bastiaan --password=bastiaan dtr.gntry.io')
      shell('docker build -t dtr.gntry.io/gntry/docker-webapp:$GIT_COMMIT .')
      // shell('docker run -i --rm docker-webapp:$GIT_COMMIT ./script/test')
      shell('docker push dtr.gntry.io/gntry/docker-webapp:$GIT_COMMIT')
  }
  publishers {
    chucknorris()
    // archiveJunit('**/target/surefire-reports/*.xml')
    // publishCloneWorkspace('**', '', 'Any', 'TAR', true, null)
    downstreamParameterized {
      trigger('test-docker-webapp') {
        condition('UNSTABLE_OR_BETTER')
        predefinedProp("IMAGE_TAG","\${GIT_COMMIT}")
      }
    }
  }
}

job('test-docker-webapp') {
  deliveryPipelineConfiguration('QA', 'Testing')
  parameters {
    stringParam("IMAGE_TAG", "EMPTY", "Docker Image tag")
  }
  steps {
    shell('docker pull dtr.gntry.io/gntry/docker-webapp:$IMAGE_TAG')
    shell("echo 'Doing some testing.....'")
    shell("echo 'Tests passed wooohooo'")
    shell('docker tag -f dtr.gntry.io/gntry/docker-webapp:$IMAGE_TAG dtr.gntry.io/gntry/docker-webapp:latest')
    shell('docker push dtr.gntry.io/gntry/docker-webapp:latest')
  }
  publishers {
    chucknorris()
    downstreamParameterized {
      trigger('start-docker-webapp') {
        condition('UNSTABLE_OR_BETTER')
        predefinedProp("IMAGE_TAG","\${IMAGE_TAG}")
      }
    }
  }
}

job('start-docker-webapp') {
  deliveryPipelineConfiguration('Deploy', 'RunIt')
  parameters {
    stringParam("IMAGE_TAG", "EMPTY", "Docker Image tag")
  }
  scm {
    git {
      remote {
        url(giturl)
      }
      createTag(false)
      clean()
    }
  }
  steps {
    shell('docker-compose -p awesome-webapp stop; docker-compose -p awesome-webapp rm -f; docker-compose -p awesome-webapp up -d')
    // shell('docker run -P -d -e  dtr.gntry.io/gntry/docker-webapp:$IMAGE_TAG')
    // shell('docker push dtr.gntry.io/gntry/docker-webapp:latest')
  }
}

deliveryPipelineView('Awesome Docker Webapp') {
    pipelineInstances(5)
    showAggregatedPipeline()
    columns(2)
    sorting(Sorting.TITLE)
    updateInterval(15)
    enableManualTriggers()
    showAvatars()
    showChangeLog()
    pipelines {
        component('Docker awesome webapp', 'build-docker-webapp')
    }
}
