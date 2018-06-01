package com.acuitybotting.path_finding.tile_capture.api;

import com.acuitybotting.path_finding.tile_capture.domain.TileCapture;
import com.acuitybotting.path_finding.tile_capture.domain.TileCaptureCheck;
import com.acuitybotting.path_finding.tile_capture.service.TileCaptureService;
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
    public long tileCheck(@RequestBody TileCaptureCheck tileCaptureCheck) {
        return service.getTileDifference(tileCaptureCheck);
    }

    @RequestMapping(value = "/TileCapture", method = RequestMethod.POST)
    public boolean tileUpload(@RequestBody TileCapture tileCapture) {
        return service.save(tileCapture);
    }
}
