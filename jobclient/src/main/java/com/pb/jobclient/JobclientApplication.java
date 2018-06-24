package com.pb.jobclient;

import com.pb.jobclient.models.Company;
import com.pb.jobclient.models.Session;
import com.pb.jobclient.models.Upload;
import com.pb.jobclient.service.RelayClient;
import com.pb.jobclient.service.StreamingService;
import com.pb.jobclient.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;

@Slf4j
@SpringBootApplication
public class JobclientApplication implements CommandLineRunner {

	@Autowired
	RelayClient relayClient;

	@Autowired
	UploadService uploadService;

	@Autowired
	StreamingService streamingService;

	@Autowired
	ConfigurableApplicationContext configurableApplicationContext;

	public static void main(String[] args) {
		SpringApplication.run(JobclientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (args.length < 1) {
			return;
		}


		String fileArg = args[0];
		File file = new File(fileArg);

		String fileName = file.getName();

		String[] fileDetails = fileName.split("\\$");

		String companyId = fileDetails[0];
		String workflowName = fileDetails[1];
		String username = fileDetails[2];

		String password = "Password1@";

		log.info("Job submit details filename [{}], username [{}], workflow [{}], companyid [{}]",fileName, username, workflowName, companyId);
		Session session = relayClient.safeLogin(username, password);

		if (session.getErrorCode() > 0) {
			log.error("Login failed when tried with username [{}]", username);
			return;
		}

		log.debug("Creating workflow object");
		Company.Workflow workflow = new Company.Workflow();
		workflow.setName(workflowName);
		workflow.setS3Folder("upload/" + workflowName);

		log.info("Initiating upload create request");
		Upload upload = uploadService.startUpload(file, workflow);
		log.info("Upload created [{}]", upload);

		log.info("Going to start streaming");
		streamingService.streamUpload(upload, file, workflow);
		log.info("Streaming done");

		configurableApplicationContext.close();
	}
}
