# Document generation POC for PB hackathon

### Introduction
This is an end to end SAAS based solution which will gives the power to
- Extract the data from external sources like Databases, ERP systems
- Compose customized and domain specific documents
- Distribution of composed documents to end customes through In house printing, Print providers, Digital deliveries like Inlet etc.

### How it works
A brief flow of the system is as per below:
1. Spectrum server to connect to Data source.
2. Reads data and transforms into flat file format for Engage one
Designer which will compose these documents.
3. Preview the composed document and publish.
4. Output document once produced will be sent to Relay.
5. Relay can further Enhance these documents if the need be else
distributes these documents to Print Provider , In-house printing or
digital channels.

### Flow Diagram

![alt text](https://raw.githubusercontent.com/username/projectname/branch/path/to/img.png)


### Tools and products used (All of PB)
- Spectrum data intergration
- ED 
- Relay Communication Hub
- Custom Job client to submit output of Spectrum to Relay hub for digital delivery

