#!/bin/bash

chmod a+w solr/pu solr/pu/core.properties
docker-compose up -d solr
