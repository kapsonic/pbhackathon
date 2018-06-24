set output_file=E:\DesignerHIP\Relay\Relay\data_file_%1.txt
set output_pdf=E:\DesignerHIP\Relay\Relay\%1.pdf
set ops_file=E:\DesignerHIP\Relay\Relay\docgen_%1.ops
set hip_file=E:\DesignerHIP\Relay\Relay\Test_Relay.hip
set JAVA="C:\Program Files\Java\jdk1.8.0_51\bin\java"
set DOC1GEN="E:\DesignerHIP\Doc1Host\doc1gen"
@echo "++++++++++++++++++++++++++++++++++++++++"
@echo "Starting at time %time%
@echo "++++++++++++++++++++++++++++++++++++++++"


rem echo "Starting it %1" > E:\DesignerHIP\Relay\Relay\started.txt

echo "---------------------------------------------------------------------------------"
@echo "Executing Job"

%JAVA% -jar E:\DesignerHIP\Relay\Relay\jobexecutor.jar  -w  -u admin -p admin -j %1 "Output"="file:%output_file%" 


@echo "Generating OPS file"
%JAVA% -jar E:\DesignerHIP\Relay\Relay\generate-ops1.jar -i %output_file% -o %output_pdf% -ops %ops_file% 

@echo "Calling document compose engine"
%DOC1GEN% %hip_file% OPS=%ops_file% 

@echo "Deleting temp files"
@del %output_file%
@del %ops_file%

@echo "-----------------------------------------"
@echo "Submitting Job to Relay "
%JAVA% -jar E:\DesignerHIP\Relay\Relay\jobclient-0.0.1-SNAPSHOT.jar %output_pdf% 
@echo %output_pdf%
@echo "-----------------------------------------"

@echo "-----------------------------------------"
@echo "Ending at time %time%
@echo "-----------------------------------------"
exit 0