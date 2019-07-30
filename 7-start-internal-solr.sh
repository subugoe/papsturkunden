#!/bin/bash

chmod a+w solr-internal/pu solr-internal/pu/core.properties
docker-compose up -d solr_internal
