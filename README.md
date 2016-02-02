# Docker CI Demo
A docker demo project which shows the usage of Jenkins in combination with Docker UCP, Docker DTR and a Jenkins Docker container.

## Prerequisites
This demo project assumes the following components are already setup.
* Docker UCP
* Docker DTR

## Jenkins container Setup
To build the Jenkins container download the `ucp-bundle` from the UCP admin console. Unpack the bundle at: `jenkins/config/ucp-bundle-admin`.

Change the following environment variables in `docker-compose.yml`

1. DOCKER_HOST
2. GITLAB_HOST

Change URLs in `jenkins/seedjobs/docker-ci-jobs.groovy` so that they match your environment.

## Building
1. Load the UCP environment by sourcing the `env.sh` from the ucp-bundle.
2. Run `docker-compose build` to build the jenkins containers

## Running
Run `docker-composee up -d` to start the CI/CD suite consisting of

* Jenkins
* Gitlab

## Usage
Components can be found at

| *Tool* | *Link* | *Credentials* |
| ------------- | ------------- | ------------- |
| Jenkins | http://${UCP_HOST}:18080/ | no login required |
| GitLab | http://${UCP_HOST}:10080/ | root/5iveL!fe |

# Credits
Inspired by the repository from: https://github.com/marcelbirkner/docker-ci-tool-stack
