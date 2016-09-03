# Marketing Reading Between the Lines

Please note that the code is not in a usable state at this time!!!

This project is initially a PoC but the intent is to continue to grow it into a marketer facing natural language listening service.

The [Wiki](https://github.com/chriseebee/mktgbtwlines/wiki) will be the main content repository for this project, but this README will contain deployment related documentation only.

# Installation

## Pre-Requisites

The following technical dependencies are required:

### Stanford NLP

Although the code for Stanford Core are included in the POM, we do need to download the models that this solution will use.
These need to be downloaded from here

Make sure you download the models for Stanford NLP: http://stanfordnlp.github.io/CoreNLP

Select the English models file, download it (it's big) and save it to the ${project.dir}/lib folder	


### Google Speech API

This service uses the Google Speech API v1 to translate audio to speech. Please see [this page](https://github.com/chriseebee/mktgbtwlines/wiki/The-Google-Speech-API) for more details: 

### IBM Watson Speech Service

There is code in the project to also use the Watson Speech API (only at unit test level right now). If you want to test this, you will need a username and password for a registered Speech API on Bluemix

### IBM Alchemy Language API

I tried the Stanford semantic annotator and found it to be nowhere near as accurate as the Alchemy Language API. Again, you will need this API and an associated API key setup in your Bluemix environment.

### Influx DB

Influx is used to store events in a time series manner when the service detects any event that makes sense to trap. RIGHT NOW THIS IS COMMENTED OUT AS NOT WORKING

### CMUSphinx

There is some code in this service to run CMU Sphinx. The intention is in future to try and remove reliance on external speech to text, but we'll see...

# Install

1. Clone the repository
2. Update the config.yml in the /usr/main/resources folder to incorporate your keys for the above services
3. Run mvn install

# Limitations

1. This will only work on English - and in specific places UK English - This will not be fixed in the short term.
2. I have some issues with my POM which means I have had to hardcode the netty architecture to Mac OSX 64. - [Issue](https://github.com/chriseebee/mktgbtwlines/issues/2)
3. I have not created Windows specific shell scripts as yet. So, Mac and Linux only I'm afraid - [Issue](https://github.com/chriseebee/mktgbtwlines/issues/3)

