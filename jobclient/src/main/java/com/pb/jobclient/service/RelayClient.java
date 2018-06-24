package com.pb.jobclient.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.h2.util.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.pb.jobclient.models.Company;
import com.pb.jobclient.models.Session;
import com.pb.jobclient.models.Upload;
import com.pb.jobclient.models.UploadChunk;
import com.pb.jobclient.utils.Constants;
import com.pb.jobclient.utils.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RelayClient implements ApplicationContextAware {

    /*
     * @Value("${relay.server.host:localhost}") private String host;
     *
     * @Value("${relay.server.port:8080}") private int port;
     *
     * @Value("${relay.server.scheme:http}") private String scheme;
     */

    @Value("${relay.chunk.size:5242880}")
    private long chunkSize;

    @Value("${relay.info.version:1.0}")
    private String clientVersion;

    @Value("${relay.info.client.id:relay-desktop-client}")
    private String clientId;

    private Session session;

    private RestTemplate rest;

    private ApplicationContext context;

    @Autowired
    public RelayClient(RestTemplate rest) {
        this.rest = rest;
    }
//
//    public Session doLogin() {
//        Session session = safeLogin();
//
//        log.info("doLogin , session : " + session.getErrorCode());
//
//        log.info("Relay Client URL: {}", getCredentials().getS3ServerUrl());
//
//        switch (session.getErrorCode()) {
//            case -1:
//            case 0:
//                log.info("Successful login");
//                break;
//            case 400:
//                log.error("Please update credentials file with your username and password.");
//                break;
//            case 401:
//                log.error("Invalid credentials, please update the credentials and try again");
//                sendInvalidPasswordNotification(getCredentials());
//                //TODO stop service here
//                break;
//            case 403:
//                log.error(
//                        "Insufficient privileges, the configured user doesn't have the necessary permissions to connect to this server.");
//                log.error("Please use a different user or update permissions");
//                break;
//            case 500:
//            case 503:
//            default:
//                log.error("There was an unknown error while trying to connect");
//                break;
//        }

//		if (session.getErrorCode() > 0)
//			System.exit(Constants.LOGIN_FAILURE_EXIT_CODE_1);

//        return session;
//    }

    public Session safeLogin(String username, String password) {
        Session s;

        try {
            s = login(username, password);
        } catch (RestClientException e) {
            log.error("Error while trying to login", e);

            s = new Session();
            s.setErrorCode(503);
        }
        if (s != null) {
            log.info("safeLogin , Returning session error code : " + s.getErrorCode());
        } else {
            log.info("login returned null");
        }
        return s;
    }


    public synchronized Session login(String username, String password) {
        if (StringUtils.isNullOrEmpty(username) || StringUtils.isNullOrEmpty(password)) {
            log.error("#############################################################################");
            log.error("Please fill UserName and Password in credentials file and restart to use Desktop Client as a Service");
            log.error("#############################################################################");
            throw new RuntimeException("Invalid Credentials ..");
        }
        Map<String, String> map = new HashMap<>();
        map.put("username", username.toLowerCase());
        map.put("password", password);
        log.info("Login with username {}", username);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity entity = new HttpEntity(map, headers);

        Session loginSession = null;
        try {
            if (EmailValidator.getInstance().isValid(username)) {
                loginSession = execute(
                        baseUri().path("/api/v1/auth/login").queryParam("desktopclient", true).build().toUri(), HttpMethod.POST,
                        entity, Session.class);
            } else {
                log.error("Invalid email id passed in user field : {}" + username);
                return null;
            }

            if (loginSession.getErrorCode() <= 0) {
                log.info("getErrorCode less than 0 : " + loginSession.getErrorCode());
                session = loginSession;
            }
            User.setRoles(loginSession.getRoles());
        } catch (HttpClientErrorException e) {
            log.info("e.getStatusCode().value() : " + e.getStatusCode().value());
            loginSession = new Session();
            loginSession.setErrorCode(e.getStatusCode().value());
        }
        log.info("Login info : {},username: {}", loginSession, username);
        return loginSession;
    }

    public Upload initiateUpload(Upload upload) {
        upload.setUserFullName(session.getFullName());
        upload.setCompanyId(session.getCompanyId());

        Upload result = execute(uri("/api/v1/uploads"), HttpMethod.POST,
                upload, Upload.class);

        //return result.getIdentifier();\
        return result;
    }

    public Company getCompany() {
        Company[] companies = execute(uri("/api/v1/companies"), HttpMethod.GET,
                null, Company[].class);

        Company company = null;
        if (companies.length > 0)
            company = companies[0];

        return company;
    }

    public boolean uploadChunk0(UploadChunk chunk) {
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<Resource> entity = new HttpEntity<>(chunk.getResource(),
                headers);

        ResponseEntity<String> response = executeForResponse(
                baseUri().path("/api/v1/uploads/chunks")
                        .queryParam("flowChunkNumber", chunk.getNumber())
                        .queryParam("flowChunkSize", chunkSize)
                        .queryParam("flowIdentifier", chunk.getIdentifier())
                        .queryParam("flowTotalSize", chunk.getTotalSize())
                        .queryParam("flowCurrentChunkSize", chunk.getSize())
                        .build().toUri(), HttpMethod.POST, entity, String.class);

        return response.getStatusCode() == HttpStatus.OK;
    }

    public boolean uploadChunk(UploadChunk chunk) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        //HttpEntity<Resource> entity = new HttpEntity<>(chunk.getResource(),
        //	headers);

        //MultipartFileRDC multipart = null;
        //multipart = new MultipartFileRDC(chunk);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

        map.add("name", chunk.getFilename());
        map.add("filename", chunk.getFilename());
        map.add("flowChunkNumber", chunk.getNumber());
        map.add("flowChunkSize", chunkSize);
        map.add("flowIdentifier", chunk.getIdentifier());
        map.add("flowTotalSize", chunk.getTotalSize());
        map.add("flowCurrentChunkSize", chunk.getSize());
        map.add("file", chunk.getResource());

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<MultiValueMap<String, Object>>(map, headers);


        ResponseEntity<String> response = executeForResponse(
                baseUri().path("/api/v1/uploads/chunks")
                        //.queryParam("flowChunkNumber", chunk.getNumber())
                        //.queryParam("flowChunkSize", chunkSize)
                        //.queryParam("flowIdentifier", chunk.getIdentifier())
                        //.queryParam("flowTotalSize", chunk.getTotalSize())
                        //.queryParam("flowCurrentChunkSize", chunk.getSize())
                        //.queryParam("file", multipart)
                        .build().toUri(), HttpMethod.POST, entity, String.class);

        return response.getStatusCode() == HttpStatus.OK;
    }

    public boolean hasChunk(UploadChunk chunk) {
        try {
            ResponseEntity<String> forEntity = executeForResponse(
                    baseUri()
                            .path("/api/v1/uploads/chunks")
                            .queryParam("flowChunkNumber", chunk.getNumber())
                            .queryParam("flowIdentifier", chunk.getIdentifier())
                            .build().encode().toUri(), HttpMethod.GET, null,
                    String.class);

            return forEntity.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                return false;

            throw e;
        }
    }

    public void cancelUpload(Upload upload) {
        if (upload == null)
            return;

        try {
            execute(baseUri().path("/api/v1/uploads/{id}").build()
                            .expand(upload.getIdentifier()).encode().toUri(),
                    HttpMethod.DELETE, null, null);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                log.warn("Could not find upload {} to delete", upload);
        } catch (RestClientException e) {
            log.error("Unkonwn error while trying to delete upload {}", upload);
        }
    }

    protected <T> T execute(String path, HttpMethod method, Object request,
                            Class<T> responseType) {
        return execute(uri(path), method, request, responseType);
    }

    protected <T> T execute(URI uri, HttpMethod method, Object request,
                            Class<T> responseType) {
        log.debug("Executing URL {} using method {}", uri, method);
        ResponseEntity<T> response = executeForResponse(uri, method, request,
                responseType);
        log.debug("Response of Executing URL {} is {}", uri, response);
        return response.getBody();
    }

    protected <T> ResponseEntity<T> executeForResponse(URI uri,
                                                       HttpMethod method, Object request, Class<T> responseType) {
        return context.getBean(RelayClient.class).executeWithRetry(uri, method,
                request, responseType);
    }

    @Retryable(backoff = @Backoff(delay = 500, maxDelay = 30000, multiplier = 2, random = true), include = RestClientException.class, exclude = HttpClientErrorException.class)
    protected <T> ResponseEntity<T> executeWithRetry(URI uri,
                                                     HttpMethod method, Object request, Class<T> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            Object requestBody = request;
            if (request instanceof HttpEntity) {
                requestBody = ((HttpEntity<?>) request).getBody();
                headers.putAll(((HttpEntity<?>) request).getHeaders());
            }

            headers.add("X-Client-Id", clientId);
            headers.add("X-Client-Version", clientVersion);

            if (session != null) {
                headers.add("X-Session-Id", session.getId());
                headers.add("X-CSRF-TOKEN", session.getCsrf());
            }

            ResponseEntity<T> response = rest.exchange(uri, method,
                    new HttpEntity<>(requestBody, headers), responseType);
            return response;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.PRECONDITION_FAILED) {
                log.error("Desktop client is out-dated, please download the latest version");
                System.exit(5);
            }
//
//            // if session is invalid, expired or missing recreate it as long as
//            // we're not doing a login
//            if (!uri.toString().contains("/auth/login")
//                    && e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
//                Session s = doLogin();
//
//                // wrap exception to continue with retries
//                if (s.getErrorCode() <= 0
//                        || !HttpStatus.valueOf(s.getErrorCode())
//                        .is4xxClientError())
//                    throw new RestClientException("Expired session");
//            }

            // break out retry
            throw e;
        }
    }

    /**
     * Writes the inputstream directly to disk.
     *
     * @param uri
     * @param method
     * @param request
     * @param responseType
     * @return
     */
    public <T> ResponseEntity<T> executeForFileDownload(URI uri,
                                                        HttpMethod method, Object request, Class<T> responseType) {
        try {

            // TODO: Move headers to RequestCallback implementation
            HttpHeaders headers = new HttpHeaders();
            Object requestBody = request;
            if (request instanceof HttpEntity) {
                requestBody = ((HttpEntity<?>) request).getBody();
                headers.putAll(((HttpEntity<?>) request).getHeaders());
            }

            headers.add("X-Client-Id", clientId);
            headers.add("X-Client-Version", clientVersion);

            if (session != null)
                headers.add("X-Session-Id", session.getId());


            ResponseEntity<T> response = rest.exchange(uri, method,
                    new HttpEntity<>(requestBody, headers), responseType);
            return response;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.PRECONDITION_FAILED) {
                log.error("Desktop client is out-dated, please download the latest version");
                System.exit(5);
            }

            // if session is invalid, expired or missing recreate it as long as
            // we're not doing a login
//            if (!uri.toString().contains("/auth/login")
//                    && e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
//                Session s = doLogin();
//
//                // wrap exception to continue with retries
//                if (s.getErrorCode() <= 0
//                        || !HttpStatus.valueOf(s.getErrorCode())
//                        .is4xxClientError())
//                    throw new RestClientException("Expired session");
//            }

            // break out retry
            throw e;
        }
    }

    public File downloadAndWriteFile(URI uri) throws IOException {

        try {

            File tempDir = new File("tmp-download");

            // Make directory in case it doesn't exist or if someone deleted it.
            FileUtils.forceMkdir(tempDir);

            // Clean the directory in case it already existed.
            //TODO: This should be done just before the loop that downloads files
            //      so we're not doing this for each download.
            try {
                FileUtils.cleanDirectory(tempDir);
            } catch (Exception e) {
                log.error("Tried to cleanup the {} directory but there was an error.", tempDir.getPath(), e);
            }

            File file = new File(tempDir.getPath() + "/download-file_" + System.currentTimeMillis() + ".tmp");

            rest.execute(uri, HttpMethod.GET, new RequestCallback() {

                @Override
                public void doWithRequest(ClientHttpRequest request) throws IOException {
                    request.getHeaders().add("X-Client-Id", clientId);
                    request.getHeaders().add("X-Client-Version", clientVersion);
                    if (session != null)
                        request.getHeaders().add("X-Session-Id", session.getId());
                }

            }, new ResponseExtractor<String>() {

                @Override
                public String extractData(ClientHttpResponse response) throws IOException {

                    log.info("Writing inputstream from server to {}", file.getPath());

                    InputStream inputStream = response.getBody();
                    write(inputStream, file);

                    return null;
                }
            });

            return file;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.PRECONDITION_FAILED) {
                //log.error("Desktop client is out-dated, please download the latest version");
                System.exit(5);
            }

            // if session is invalid, expired or missing recreate it as long as
            // we're not doing a login
//            if (!uri.toString().contains("/auth/login")
//                    && e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
//                Session s = doLogin();
//
//                // wrap exception to continue with retries
//                // NOTE: This method is called inside a loop that performs retries.
//                if (s.getErrorCode() <= 0
//                        || !HttpStatus.valueOf(s.getErrorCode())
//                        .is4xxClientError())
//                    throw new RestClientException("Expired session");
//            }

            throw e;
        } catch (IOException e) {
            throw e;
        }
    }

    private void write(InputStream in, File file) throws IOException {
        OutputStream outStream = null;
        int totalBytes = 0;
        try {
            outStream = new FileOutputStream(file);

            byte[] buffer = new byte[8 * 1024];
            int bytesRead = 0;
            while ((bytesRead = in.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
                totalBytes = totalBytes + bytesRead;
            }

        } catch (IOException e) {
            log.error("While writing inputstream from server to {}:", file.toPath(), e);
            throw e;
        } finally {
            IOUtils.closeQuietly(outStream);
        }
        log.info("Downloaded file {} contains {} bytes", file.getPath(), totalBytes);
    }

    public UriComponentsBuilder baseUri() {
        return UriComponentsBuilder.newInstance().scheme(scheme()).host(host());
    }


    public URI uri(String path) {
        return baseUri().path(path).build().toUri();
    }

    public long getChunkSize() {
        return chunkSize;
    }

    @Override
    public void setApplicationContext(ApplicationContext context)
            throws BeansException {
        this.context = context;
    }

    public String scheme() {
        return "https";
    }

    public String host() {
        return "client-qa.ca.relayhub.pitneycloud.com";
    }

    public int port() {
        return 80;
    }


}
