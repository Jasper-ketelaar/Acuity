package com.acuitybotting.path_finding.tile_capture.api;

import com.acuitybotting.path_finding.tile_capture.domain.TileCapture;
import com.acuitybotting.path_finding.tile_capture.domain.TileCaptureCheck;
import com.acuitybotting.path_finding.tile_capture.service.TileCaptureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/path-finding/tile-capture/")
public class TileCaptureAPI {

    private final TileCaptureService service;

    @Autowired
    public TileCaptureAPI(TileCaptureService service) {
        this.service = service;
    }

    @PreAuthorize("authentication.principal.username == 'Zach'")
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(){
        return "Running: ";
    }

    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public long tileCheck(@RequestBody TileCaptureCheck tileCaptureCheck) {
        return service.getTileDifference(tileCaptureCheck);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public boolean tileUpload(@RequestBody TileCapture tileCapture) {
        return service.save(tileCapture);
    }
}
