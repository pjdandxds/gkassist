package com.tangenta.gkassist.school.representation;

import com.tangenta.gkassist.school.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class SchoolRepresentationService {

    public static SchoolRepresentation toRepresentation(SchoolInfo info, List<CampusScenery> scenery, List<AdmissionGuide> guides, Link schoolBadge) {
        List<SceneryRepresentation> sceneryReps = scenery.stream()
                .map(s -> new SceneryRepresentation(Link.of(s.getUrl()), s.getDescription()))
                .collect(Collectors.toList());
        List<AdmissionGuideRepresentation> guideReps = guides.stream()
                .map(s -> new AdmissionGuideRepresentation(Link.of(s.getLocation()), s.getDescription()))
                .collect(Collectors.toList());

        return new SchoolRepresentation(SchoolId.of(info.getSchoolId()),
                info.getName(), sceneryReps, guideReps, schoolBadge);
    }
}
