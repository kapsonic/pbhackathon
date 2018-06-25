# PB Document Suite

### Introduction
This is an end to end SAAS based solution which will gives the power to
- Extract the data from external sources like Databases, ERP systems
- Compose customized and domain specific documents
- Distribution of composed documents to end customes through In house printing, Print providers, Digital deliveries like Inlet etc.

> ## Complete working POC is in the VM with following details
> - VM Name: **pulkit-VM**
> - VM Location: **Watford**
> **All the required server including spectrum server, Enterprise designer are already there at this VM.**

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

![alt text](https://raw.githubusercontent.com/kapsonic/pbhackathon/master/Flow.PNG)


### Tools and products used (All of PB)
- Spectrum data intergration
- ED 
- Relay Communication Hub
- Custom Job client to submit output of Spectrum to Relay hub for digital delivery




### Contributors
- Kapil Soni (kapil.soni@pb.com)
- Sourabh Lodha (sourabh.lodha@pb.com)
- Pulkit Anchalia(pulkit.anchalia@pb.com)
- Vineet Kala (vineet.kala@pb.com)

