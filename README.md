# PB Document Suite

### Introduction
This is an end to end SAAS based solution which will gives the power to
- Extract the data from external sources like Databases, ERP systems
- Compose customized and domain specific documents
- Distribution of composed documents to end customes through In house printing, Print providers, Digital deliveries like Inlet etc.

> ## Complete working POC is in the VM with following details
> - VM Name: **pulkit-VM**
> - VM Location: **Watford**
>
> **All the required server including spectrum server, Enterprise designer are already there at this VM.**

### How it works
A brief flow of the system is as per below:
1. Data sources are configured in Spectrum server.
2. Spectrum server connects to Data source.
3. Workflows defined in Enterprise Designer, reads data and transform to the required format (Flat files which are readable by Engage One).
4. Templates are defined in Engage one designer which holds the placeholders for the composistion.
5. Preview the composed document and publish.
6. Flat files generated from Spectrum workflows and templates from Engage one together processed in Enagage one to make a complete document.
7. Final composed documents will be sent off to Relay with the help of custom tool built just for the purpose.
8. Relay can further Enhance these documents if the need be with the help of workflows defined in it and then 
distributes these documents to Print Provider , In-house printing or
digital channels like Inlet etc or available for direct download.

### Flow Diagram

![alt text](https://raw.githubusercontent.com/kapsonic/pbhackathon/master/Flow.PNG)


### Tools and products used (All of PB)
- Spectrum data intergration
- Enterprise Designer
- Engage one
- Relay Communication Hub
- Custom Job client to submit output of Spectrum to Relay hub for digital delivery




### Contributors
- Kapil Soni (kapil.soni@pb.com)
- Sourabh Lodha (sourabh.lodha@pb.com)
- Pulkit Anchalia(pulkit.anchalia@pb.com)
- Vineet Kala (vineet.kala@pb.com)

