package com.acuitybotting.path_finding.tile_data.api;

import com.acuitybotting.path_finding.tile_data.domain.TileCapture;
import com.acuitybotting.path_finding.tile_data.domain.TileCaptureCheck;
import com.acuitybotting.path_finding.tile_data.service.TileCaptureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TileCaptureAPI {

    private final TileCaptureService service;

    @Autowired
    public TileCaptureAPI(TileCaptureService service) {
        this.service = service;
    }

    @RequestMapping(value = "/Test", method = RequestMethod.GET)
    public String test(){
        return "Running";
    }

    @RequestMapping(value = "/TileCaptureCheck", method = RequestMethod.POST)
    public String tileCheck(@RequestBody TileCaptureCheck tileCaptureCheck) {
        return service.tileCheck(tileCaptureCheck);
    }

    @RequestMapping(value = "/TileCapture", method = RequestMethod.POST)
    public String tileUpload(@RequestBody TileCapture tileCapture) {
        return service.tileUpload(tileCapture);
    }
}
