# Acuity

## What is Acuity?
Acuity is a project that handles the backend infastructure of Runescape botting clients. This includes security, databases, client communication, dashboards, and services.

# This Repo - What are all these modules?
## common-utils
Basic utility classes used throughout the modules.

## data-flow-messaging
Services related to communication. Currently uses RabbitMQ connections.

## db-arango
Spring configuration for connecting to our ArangoDB instances.

## db-influx
Spring configuration for connecting to our InfluxDB instances.

## security-acuity
Services for decoding JWTs to confirm identities.

## security-influxdb
A frontend web-server the confirms identities before forwarding requests to Influxdb.

## security-web-api
Spring configuration to confirm request jwt headers and apply the permissions to class/method annotations.

## service-acuity-identities
Service for interacting with Acuity identities stored in ArangoDB.

## service-bot-control
Service for interacting with BotInstances and other bot control domains in ArangoDB.

## service-path-finding
Service for creating decoding RS maps and then finding paths in them.

## service-script-repository
Service for handeling script uploads, compiles, obfuscations, and all database updates.

## website-acuity-dashboard
Vaadin frontend that interacts with services.
