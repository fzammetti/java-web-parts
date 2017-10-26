No, Java Web Parts does NOT use Maven as its build system.  However,
we do want to have JWP in the Maven repo at iBiblio to it can be
used as a Maven dependency (or Ant dependency using the ant-dependencies
task), and this directory contains the POM's needed to build to upload
bundles for the repo.  See the "maven-repo" task for their usage.