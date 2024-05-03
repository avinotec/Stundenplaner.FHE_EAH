# About OpenAPI + this directory 

This directory is meant to be used as some kind of "single source of truth" for the API 
we want to use. It contains different different YAML and JSON files, which are to be used 
with the `openApiGenerate` task defined in the build.gradle file. These files are specification
files which define language-agnostic interfaces for HTTP API. Together with `openApiGenerate` 
we are able to generate almost everything from models to methods and API clients. 
Using this approach guarantees type safety and compliance with the Moses API.

## Attention

Although a specification should also correspond to the actual API, 
this is unfortunately not the case at the moment. 
Both `openapi-spec.json` (provided by the Moses API vendor) and `openapi-spec--formatted.json` 
do not correspond to the actual API. `openapi-moses-eah.yaml` is our attempt to remodel the actual 
responses.

For further details have a look at those files.