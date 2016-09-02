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




# Limitations

1. This will only work on English - and in specific places UK English - This will not be fixed in the short term.
2. I have some issues with my POM which means I have had to hardcode the netty architecture to Mac OSX 64. - [Issue](https://github.com/chriseebee/mktgbtwlines/issues/2)
3. I have not created Windows specific shell scripts as yet. So, Mac and Linux only I'm afraid - [Issue](https://github.com/chriseebee/mktgbtwlines/issues/3)

