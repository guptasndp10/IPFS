package com.fai.ipfs.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.fai.ipfs.services.RestTemplateOverride;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.javafx.collections.MappingChange;
import io.ipfs.multihash.Multihash;
import com.fai.ipfs.model.Document;
import com.fai.ipfs.services.IpfsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import io.ipfs.api.IPFS;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(allowedHeaders =  "*", origins = "*")
@Controller
@RequestMapping(path = "/")
public class MainController {

    @Autowired
    RestTemplateOverride restTemplateOverride;

    @Value("${ipfs.api.peer}")
    String ipfsApi;

    @Value("${ipfs.api.ipfs}")
    String url;

    @Autowired
    IpfsService ipfsService;

    @Value("${user.txt}")
    String txt;

    @Value("${user.pdf}")
    String pdf;

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Document> uploadDocument(HttpServletRequest request, HttpServletResponse response) throws Exception {

        long startTime = System.currentTimeMillis();
        Logger logger = LoggerFactory.getLogger(MainController.class);
        try {
            logger.debug("**************************************UPLOADING A FILE IN MAIN CONTROLLER**************************************************");
            Collection<Part> parts = request.getParts();
            Document p = new Document();
            if (parts.size() == 0) {
                logger.debug("NO FILE FOUND EXCEPTION");
                throw new Exception("No File is uploaded");
            } else if (parts.size() == 1) {

                if (!parts.iterator().next().getName().equals("multiPartFile")) {
                    logger.debug("INCORRECT ATTRIBUTE NAME EXCEPTION");
                    throw new Exception("Incorrect attribute name");
                }
                else {
                    logger.debug("GETTING THE FILE PART FROM HTTP REQUEST");

                    Part partDocument = request.getPart("multiPartFile");

                    ObjectMapper objectMapper = new ObjectMapper();

                    Document document = objectMapper.readValue(partDocument.getInputStream(),Document.class);
                    logger.debug("REQUESTING THE HASH FROM IPFS SERVICE");

                    String fileHash = ipfsService.Hash(document,ipfsApi);
                    logger.debug("SETTING THE HASH VALUE FROM IPFS SERVICE TO DOCUMENT OBJECT");
                    p.setFilHash(fileHash);
                    p.setFileName(document.getFileName());
                    p.setFileSize(partDocument.getSize());
                    p.setMessage("File is Successfully uploaded");
                }
            }
            long endTime = System.currentTimeMillis();

            logger.debug("REQUEST COMPLETED AFTER "+((double)(endTime-startTime)/1000)+" SECONDS");

            return new ResponseEntity<Document>(p, HttpStatus.CREATED);
        }  catch (Exception e) {

            Document b = new Document();
            b.setMessage(e.getMessage());
            long endTime = System.currentTimeMillis();

            logger.debug("REQUEST FAILED AFTER "+((double)(endTime-startTime)/1000)+" SECONDS");

            return new ResponseEntity<>(b, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/getfile" )
    public ResponseEntity<Document> getFile(HttpServletRequest request, HttpServletResponse response) throws Exception {

        long startTime = System.currentTimeMillis();
        Logger logger = LoggerFactory.getLogger(MainController.class);
        try {
            logger.debug("**************************************FETCHING OF FILE IN MAIN CONTROLLER******************************************");
            Map<String,String> headerList = Collections.list(request.getHeaderNames())
                    .stream()
                    .collect(Collectors.toMap(h -> h, (request)::getHeader));
                    ArrayList<String> ext = new ArrayList();
                    ext.add(txt);
                    ext.add(pdf);
                    if (!headerList.keySet().contains("filehash") ) {
                        logger.debug("FILE HASH SHOULD CONTAIN VALID HEADER EXCEPTION");
                        throw new Exception("File hash should contain valid header name");
                    }
                    String fileHash = request.getHeader("fileHash");
                    logger.debug("GETTING HASH FROM HEADER");

                    String[] parts = fileHash.split("-");

                    if (parts.length==1){
                        logger.debug("HEADER SHOULD CONTAIN HASH AND FILE EXCEPTION");
                        throw new Exception("Header should contain Hash and File Name");
                    }
                    int pos = parts[1].lastIndexOf(".");

                    if(parts.length==2 && parts[0].length()==46 && ext.contains(parts[1].substring(pos))) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.set("fileHash",fileHash);
                        HttpEntity<String> entity = new HttpEntity(headers);
                        logger.debug("CALLING THE IPFS SERVICE USING REST TEMPLATE");
                        ResponseEntity<Document> response1 = restTemplateOverride.restTemplate.exchange(url,HttpMethod.GET,entity,Document.class);
                        long endTime = System.currentTimeMillis();
                        logger.debug("REQUEST COMPLETED AFTER "+((double)(endTime-startTime)/1000+" SECONDS"));
                        return new ResponseEntity(response1.getBody(),HttpStatus.CREATED);
                    }

                    else if (parts[0].length()<46) {
                        logger.debug("HEADER SHOULD CONTAIN VALID HASH EXCEPTION");
                        throw new Exception("Header should contain valid hash");
                    }

                    else if ( !ext.contains(parts[1].substring(pos))) {
                        logger.debug("HEADER SHOULD CONTAIN FILE EXTENSION EXCEPTION");
                        throw new Exception("Header should contain valid file extension");
                    }

                    else {
                        logger.debug("HEADER SHOULD CONTAIN TWO INPUT STRING PARTS EXCEPTION");
                        throw new Exception("Header should contain two input string parts");
                    }

        } catch (Exception e) {
            Document b = new Document();
            b.setMessage(e.getMessage());
            long endTime = System.currentTimeMillis();

            logger.debug("REQUEST FAILED AFTER "+((double)(endTime-startTime)/1000)+" SECONDS");

            return new ResponseEntity<>(b, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/ipfs" )
    public ResponseEntity<Document> ipfs(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Logger logger = LoggerFactory.getLogger(MainController.class);
        logger.debug("**************************************FETCHING OF FILE FROM IPFS******************************************");
        IPFS ipfs;
        String fileHash = request.getHeader("fileHash");

        logger.debug("GETTING HASH HEADER FROM MAIN HTTP CALL");
        String[] parts = fileHash.split("-");

        if(System.getenv().containsKey("IPFS_BIND_IP")) {
            ipfs = new IPFS(System.getenv("IPFS_BIND_IP"),5001);
        }else{
            ipfs = new IPFS(ipfsApi);
        }
        logger.debug("INITIALIZING A IPFS NODE");
        Multihash filePointer = Multihash.fromBase58(parts[0]);
        logger.debug("FETCHING THE FILE POINTER FROM IPFS NODE");
        byte[] fileContents = ipfs.cat(filePointer);
        logger.debug("FETCHING THE BYTE DATA FROM IPFS NODE");
        Document p = new Document();
        p.setFileName(parts[1]);
        p.setFileData(fileContents);
        p.setMessage("Successfully retrieved file from ipfs");
        logger.debug("SETTING THE ATTRIBUTES IN DOCUMENT OBJECT");
        return new ResponseEntity<Document>(p, HttpStatus.CREATED);
    }
}
