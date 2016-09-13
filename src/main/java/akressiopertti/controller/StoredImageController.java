package akressiopertti.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("kuvat")
public class StoredImageController {
    
    @Autowired
    private ServletContext servletContext;
    
    @RequestMapping(value = "/logo", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getLogo() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        InputStream in = servletContext.getResourceAsStream("static/images/logo.png");
        return IOUtils.toByteArray(in);
    }
            
}
