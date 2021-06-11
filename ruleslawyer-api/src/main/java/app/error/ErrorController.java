package app.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@CrossOrigin(maxAge = 3600)
public class ErrorController {

    @RequestMapping(value="/error", method = {GET, POST})
    public ResponseEntity error() {
        return new ResponseEntity(INTERNAL_SERVER_ERROR);
    }

}
