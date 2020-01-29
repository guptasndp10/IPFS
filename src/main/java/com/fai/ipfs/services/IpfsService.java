package com.fai.ipfs.services;

import com.fai.ipfs.model.Document;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.io.File;

@Component
public class IpfsService {

    @Value("${user.dir}")
    String dir;

    @Value("${user.txt}")
    String txt;

    @Value("${user.pdf}")
    String pdf;

    public String Hash(Document document, String ipfsApi) throws Exception{

        Logger logger = LoggerFactory.getLogger(IpfsService.class);
        IPFS ipfs;
        ArrayList<String> ext = new ArrayList();
        ext.add(txt);
        ext.add(pdf);
        logger.debug("GETTING THE FILE FROM MAIN CONTROLLER");
        int pos = document.getFileName().lastIndexOf(".");
        logger.debug("AFTER FILE CREATION"+pos);
        if (pos > 0 && ext.contains(document.getFileName().substring(pos).toLowerCase())) {
            logger.debug("FILE UPLOADED WITH EXTENSION " + document.getFileName().substring(pos));
        }else {
            ipfs = new IPFS(ipfsApi);
        }

        File fi = new File(dir+document.getFileName());

        FileUtils.copyInputStreamToFile(new ByteArrayInputStream(document.getFileData()), fi);

        logger.debug("CALLING THE IPFS NODE");

        if(System.getenv().containsKey("IPFS BIND API")) {
            ipfs = new IPFS(System.getenv("IPFS_BIND_IP"), 5001);
        }else {
            ipfs = new IPFS(ipfsApi);
        }
        NamedStreamable.FileWrapper file1 = new NamedStreamable.FileWrapper((fi));

        MerkleNode fileHash = ipfs.add(file1).get(0);
        logger.debug("HASH FROM THE IPFS NODE");

        fi.delete();

        return fileHash.hash.toString();
    }
}
